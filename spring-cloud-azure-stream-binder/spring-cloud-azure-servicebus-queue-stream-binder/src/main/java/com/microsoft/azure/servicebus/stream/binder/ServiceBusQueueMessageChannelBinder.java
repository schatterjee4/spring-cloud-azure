/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.servicebus.stream.binder;

import com.microsoft.azure.servicebus.stream.binder.properties.ServiceBusConsumerProperties;
import com.microsoft.azure.servicebus.stream.binder.provisioning.ServiceBusChannelProvisioner;
import com.microsoft.azure.spring.integration.core.api.CheckpointConfig;
import com.microsoft.azure.spring.integration.core.api.SendOperation;
import com.microsoft.azure.spring.integration.servicebus.inbound.ServiceBusQueueInboundChannelAdapter;
import com.microsoft.azure.spring.integration.servicebus.queue.ServiceBusQueueOperation;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.lang.NonNull;

/**
 * @author Warren Zhu
 */
public class ServiceBusQueueMessageChannelBinder extends ServiceBusMessageChannelBinder {

    private final ServiceBusQueueOperation serviceBusQueueOperation;

    public ServiceBusQueueMessageChannelBinder(String[] headersToEmbed,
            @NonNull ServiceBusChannelProvisioner provisioningProvider,
            @NonNull ServiceBusQueueOperation serviceBusQueueOperation) {
        super(headersToEmbed, provisioningProvider);
        this.serviceBusQueueOperation = serviceBusQueueOperation;
    }

    @Override
    protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
            ExtendedConsumerProperties<ServiceBusConsumerProperties> properties) {
        CheckpointConfig checkpointConfig =
                CheckpointConfig.builder().checkpointMode(properties.getExtension().getCheckpointMode()).build();
        this.serviceBusQueueOperation.setCheckpointConfig(checkpointConfig);
        ServiceBusQueueInboundChannelAdapter inboundAdapter =
                new ServiceBusQueueInboundChannelAdapter(destination.getName(), this.serviceBusQueueOperation);
        inboundAdapter.setBeanFactory(getBeanFactory());
        return inboundAdapter;
    }

    @Override
    SendOperation getSendOperation() {
        return this.serviceBusQueueOperation;
    }
}