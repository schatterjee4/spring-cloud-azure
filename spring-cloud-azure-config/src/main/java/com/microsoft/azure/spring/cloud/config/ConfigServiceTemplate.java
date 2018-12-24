/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.spring.cloud.config.domain.KeyValueItem;
import com.microsoft.azure.spring.cloud.config.domain.KeyValueResponse;
import com.microsoft.azure.spring.cloud.config.resource.ConnectionString;
import com.microsoft.azure.spring.cloud.config.resource.ConnectionStringPool;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigServiceTemplate implements ConfigServiceOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceTemplate.class);
    private static final String LINK_HEADER = "link";
    private static final String NEXT_PAGE_LINK = "<(.*?)>; rel=\"next\".*";
    private static final Pattern PAGE_LINK_PATTERN = Pattern.compile(NEXT_PAGE_LINK);

    public static final String LOAD_FAILURE_MSG = "Failed to load keys from Azure Config Service.";
    public static final String LOAD_FAILURE_VERBOSE_MSG = LOAD_FAILURE_MSG + " With status code: %s, response: %s";

    private static final ObjectMapper mapper = new ObjectMapper();

    private final ConfigHttpClient configClient;
    private final ConnectionStringPool connectionStringPool;

    public ConfigServiceTemplate(ConfigHttpClient httpClient, ConnectionStringPool connectionStringPool) {
        this.configClient = httpClient;
        this.connectionStringPool = connectionStringPool;
    }

    @Override
    public List<KeyValueItem> getKeys(@Nullable String prefix, @Nullable String label, @NonNull ConfigStore store) {
        ConnectionString connString = connectionStringPool.get(store.getName());
        String storeEndpoint = connString.getEndpoint();
        String requestUri = new RestAPIBuilder().withEndpoint(storeEndpoint).buildKVApi(prefix, label);
        List<KeyValueItem> result = new ArrayList<>();

        CloseableHttpResponse response = null;
        try {
            response = getRawResponse(requestUri, connString);
            while (response != null) {
                try {
                    KeyValueResponse kvResponse = mapper.readValue(response.getEntity().getContent(),
                            KeyValueResponse.class);
                    result.addAll(kvResponse.getItems());
                } catch (IOException e) {
                    throw new IllegalStateException(LOAD_FAILURE_MSG, e);
                }

                String nextLink = getNextLink(response);
                if (!StringUtils.hasText(nextLink)) {
                    break;
                }

                String nextRequestUri = new RestAPIBuilder().withEndpoint(storeEndpoint).withPath(nextLink)
                        .buildKVApi();
                response = getRawResponse(nextRequestUri, connString);
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.warn("Response was not closed successfully.", e);
                }
            }
        }

        return result;
    }

    private CloseableHttpResponse getRawResponse(String requestUri, @NonNull ConnectionString connString) {
        HttpGet httpGet = new HttpGet(requestUri);
        Date date = new Date();

        LOGGER.debug("Loading key-value items from Azure Config service at [{}].", requestUri);
        try {
            CloseableHttpResponse response = configClient.execute(httpGet, date, connString.getId(),
                    connString.getSecret());
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                return response;
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                LOGGER.warn("No configuration data found in Azure Config Service for request uri {}.", requestUri);
                return null;
            } else {
                throw new IllegalStateException(String.format(LOAD_FAILURE_VERBOSE_MSG, statusCode, response));
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error(LOAD_FAILURE_MSG, e);
            // TODO (wp) wrap exception as config service specific exception in order to provide fail-fast etc.
            // features?
            throw new IllegalStateException(LOAD_FAILURE_MSG, e);
        }
    }

    /**
     * Extract next link path and query from {@code response} header
     * @param response which response to extract link header from
     * @return matched next link header, return empty string if not found
     */
    private static String getNextLink(@NonNull CloseableHttpResponse response) {
        Header linkHeader = response.getFirstHeader(LINK_HEADER);
        if (linkHeader == null || !StringUtils.hasText(linkHeader.getValue())) {
            return "";
        }

        Matcher linkMatcher = PAGE_LINK_PATTERN.matcher(linkHeader.getValue());
        return linkMatcher.matches() ? linkMatcher.group(1) : "";
    }
}