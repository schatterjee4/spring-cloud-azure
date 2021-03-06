/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.integration.eventhub.checkpoint;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.spring.integration.core.api.CheckpointConfig;
import com.microsoft.azure.spring.integration.core.api.CheckpointMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Do checkpoint after each batch. Effective when {@link CheckpointMode#BATCH}
 *
 * @author Warren Zhu
 */
class BatchCheckpointManager extends CheckpointManager {
    private static final Logger log = LoggerFactory.getLogger(BatchCheckpointManager.class);
    private final ConcurrentHashMap<String, EventData> lastEventByPartition = new ConcurrentHashMap<>();

    BatchCheckpointManager(CheckpointConfig checkpointConfig) {
        super(checkpointConfig);
        Assert.isTrue(this.checkpointConfig.getCheckpointMode() == CheckpointMode.BATCH,
                () -> "BatchCheckpointManager should have checkpointMode batch");
    }

    @Override
    public void onMessage(PartitionContext context, EventData eventData) {
        this.lastEventByPartition.put(context.getPartitionId(), eventData);
    }

    @Override
    public void completeBatch(PartitionContext context) {
        EventData eventData = this.lastEventByPartition.get(context.getPartitionId());
        context.checkpoint().whenComplete((v, t) -> {
            if (t != null) {
                logCheckpointFail(context, eventData, t);
            } else {
                logCheckpointSuccess(context, eventData);
            }
        });
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
