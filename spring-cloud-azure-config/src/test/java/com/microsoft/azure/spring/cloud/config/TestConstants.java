/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloud.config;

/**
 * Test constants which can be shared across different test classes
 */
public class TestConstants {
    private TestConstants() {
    }

    // Store specific configuration
    public static final String TEST_STORE_NAME = "store1";
    public static final String CONN_STRING_PROP = "spring.cloud.azure.config.stores[0].connection-string";
    public static final String STORE_NAME_PROP = "spring.cloud.azure.config.stores[0].name";
    public static final String PREFIX_PROP = "spring.cloud.azure.config.stores[0].prefix";

    public static final String DEFAULT_CONTEXT_PROP = "spring.cloud.azure.config.default-context";
    public static final String CONFIG_ENABLED_PROP = "spring.cloud.azure.config.enabled";
    public static final String WATCH_ENABLED_PROP = "spring.cloud.azure.config.watch.enabled";
    public static final String SEPARATOR_PROP = "spring.cloud.azure.config.profile-separator";
    public static final String SUBSCRIPTION_ID_PROP = "spring.cloud.azure.config.arm.subscription-id";
    public static final String RESOURCE_GROUP_PROP = "spring.cloud.azure.config.arm.resource-group-name";
    public static final String CONFIG_STORE_PROP = "spring.cloud.azure.config.arm.config-store-name";
    public static final String MSI_ENABLED_PROP = "spring.cloud.azure.config.msi-enabled";

    public static final String TEST_CONN_STRING =
            "Endpoint=https://fake.test.config.io;Id=fake-conn-id;Secret=ZmFrZS1jb25uLXNlY3JldA==";

    public static final String TEST_ENDPOINT = "https://fake.test.config.io";
    public static final String TEST_KV_API = TEST_ENDPOINT + "/kv?key=fake-key*&label=fake-label";
    public static final String TEST_ID = "fake-conn-id";
    public static final String TEST_SECRET = "ZmFrZS1jb25uLXNlY3JldA=="; // Base64 encoded from fake-conn-secret

    public static final String TEST_SUBSCRIPTION_ID = "fake-subscription-id";
    public static final String TEST_RESOURCE_GROUP = "fake-resource-group";
    public static final String TEST_CONFIG_STORE = "fake-config-store";

    public static final String MSI_TOKEN = "fake_token";

    public static final String TEST_CONTEXT = "/foo/";
    public static final String TEST_KEY_1 = "test_key_1";
    public static final String TEST_VALUE_1 = "test_value_1";
    public static final String TEST_KEY_2 = "test_key_2";
    public static final String TEST_VALUE_2 = "test_value_2";
    public static final String TEST_KEY_3 = "test_key_3";
    public static final String TEST_VALUE_3 = "test_value_3";

    public static final String TEST_SLASH_KEY = "test/slash/key";
    public static final String TEST_SLASH_VALUE = "prop value for slashed key name";

    public static final String TEST_SUBSCRIPTION_1 = "fake-subscription-id-1";
    public static final String TEST_SUBSCRIPTION_2 = "fake-subscription-id-2";
    public static final String TEST_SUBSCRIPTION_3 = "fake-subscription-id-3";

    public static final String TEST_RESOURCE_GROUP_1 = "fake-resource-group-1";
    public static final String TEST_RESOURCE_GROUP_2 = "fake-resource-group-2";
    public static final String TEST_RESOURCE_GROUP_3 = "fake-resource-group-3";

    public static final String TEST_CONFIG_TYPE = "Microsoft.Azconfig/configurationStores";
    public static final String TEST_NON_CONFIG_TYPE = "Incorrect.Resource.Type.1";

    public static final String TEST_STORE_NAME_1 = "fake-config-store-1";
    public static final String TEST_STORE_NAME_2 = "fake-config-store-2";
}