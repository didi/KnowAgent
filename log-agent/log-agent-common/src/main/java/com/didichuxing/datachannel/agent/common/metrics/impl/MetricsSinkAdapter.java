/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.agent.common.metrics.impl;

import com.didichuxing.datachannel.agent.common.loggather.LogGather;
import com.didichuxing.datachannel.agent.common.metrics.MetricsFilter;
import com.didichuxing.datachannel.agent.common.metrics.MetricsRecordBuilder;
import com.didichuxing.datachannel.agent.common.metrics.MetricsSink;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricMutableCounterInt;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricMutableGaugeInt;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricMutableStat;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricsRegistry;
import com.didichuxing.datachannel.agent.common.metrics.util.Contracts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * An adapter class for metrics sink and associated filters
 */
public class MetricsSinkAdapter {

    private final Logger                   LOGGER   = LoggerFactory
                                                        .getLogger(MetricsSinkAdapter.class);
    private final String                   name, description, context;
    private final MetricsSink              sink;
    private final MetricsFilter            sourceFilter, recordFilter, metricFilter;
    private final SinkQueue<List<MetricsEntry>> queue;
    private final Thread                   sinkThread;
    private volatile boolean               stopping = false;
    private volatile boolean               inError  = false;
    private final int                      period, firstRetryDelay, retryCount;
    private final float                    retryBackoff;
    private final MetricsRegistry          registry = new MetricsRegistry("sinkadapter");
    private final MetricMutableStat        latency;
    private final MetricMutableCounterInt  dropped;
    private final MetricMutableGaugeInt    qsize;

    private final Consumer<List<MetricsEntry>>  consumer = this::publishMetrics;

    MetricsSinkAdapter(String name, String description, MetricsSink sink, String context,
                       MetricsFilter sourceFilter, MetricsFilter recordFilter,
                       MetricsFilter metricFilter, int period, int queueCapacity, int retryDelay,
                       float retryBackoff, int retryCount) {
        if (period <= 0) {
            LogGather.recordErrorLog("MetricsSinkAdapter error", "period <= 0");
            throw new IllegalArgumentException("period must be greater than 0");
        }
        if (retryDelay <= 0) {
            LogGather.recordErrorLog("MetricsSinkAdapter error", "retryDelay <= 0");
            throw new IllegalArgumentException("retryDelay must be greater than 0");
        }
        if (retryBackoff <= 1) {
            LogGather.recordErrorLog("MetricsSinkAdapter error", "retryBackoff <= 1");
            throw new IllegalArgumentException("retryBackoff must be greater than 1");
        }
        if (queueCapacity <= 0) {
            LogGather.recordErrorLog("MetricsSinkAdapter error", "queueCapacity <= 0");
            throw new IllegalArgumentException("queueCapacity must be greater than 0");
        }

        this.name = Contracts.checkNotNull(name, "name");
        this.description = description;
        this.sink = Contracts.checkNotNull(sink, "sink object");
        this.context = context;
        this.sourceFilter = sourceFilter;
        this.recordFilter = recordFilter;
        this.metricFilter = metricFilter;
        this.period = period;
        firstRetryDelay = retryDelay;
        this.retryBackoff = retryBackoff;
        this.retryCount = retryCount;
        this.queue = new SinkQueue<>(queueCapacity);
        latency = registry.newStat("sink." + name + ".latency", "Sink end to end latency", "ops",
            "time");
        dropped = registry.newCounter("sink." + name + ".dropped", "Dropped updates per sink", 0);
        qsize = registry.newGauge("sink." + name + ".qsize", "Queue size", 0);

        sinkThread = new Thread() {
            @Override
            public void run() {
                try {
                    publishMetricsFromQueue();
                } catch (Throwable e) {
                    LOGGER.warn("fail to publishMetricFromQueue", e);
                }
            }
        };
        sinkThread.setName(name);
        sinkThread.setDaemon(true);
    }

    boolean putMetrics(List<MetricsEntry> entries, long logicalTime) {
        if (logicalTime % period == 0) {
            LOGGER.debug("enqueue, logicalTime=" + logicalTime);
            if (queue.enqueue(entries))
                return true;
            dropped.incr();
            return false;
        }
        return true; // OK
    }

    void publishMetricsFromQueue() {
        int retryDelay = firstRetryDelay;
        int n = retryCount;
        int minDelay = Math.min(500, retryDelay * 1000); // millis
        Random rng = new Random(System.nanoTime());
        while (!stopping) {
            try {
                queue.consumeAll(consumer);
                retryDelay = firstRetryDelay;
                n = retryCount;
                inError = false;
            } catch (InterruptedException e) {
                LOGGER.info(name + " thread interrupted.");
            } catch (Exception e) {
                if (n > 0) {
                    int awhile = rng.nextInt(retryDelay * 1000 - minDelay) + minDelay;
                    if (!inError) {
                        LOGGER.error("Got sink exception, retry in " + awhile + "ms", e);
                    }
                    retryDelay *= retryBackoff;
                    try {
                        Thread.sleep(awhile);
                    } catch (InterruptedException e2) {
                        LOGGER.info(name + " thread interrupted while waiting for retry", e2);
                    }
                    --n;
                } else {
                    if (!inError) {
                        LOGGER.error("Got sink exception and over retry limit, "
                                     + "suppressing further error messages", e);
                    }
                    queue.clear();
                    inError = true; // Don't keep complaining ad infinitum
                }
            }
        }
    }

    void publishMetrics(List<MetricsEntry> entries) {
        long ts = 0;
        for (MetricsEntry entry : entries) {
            LOGGER.debug("sourceFilter=" + sourceFilter);
            if (sourceFilter == null || sourceFilter.accepts(entry.getSourceName())) {
                for (MetricsRecordImpl record : entry.getRecords()) {
                    if ((context == null || context.equals(record.context()))
                        && (recordFilter == null || recordFilter.accepts(record))) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Pushing record " + entry.getSourceName() + "." + record.context()
                                         + "." + record.name() + " to " + name);
                        }
                        sink.putMetrics(metricFilter == null ? record : new MetricsRecordFiltered(
                            record, metricFilter));
                        if (ts == 0)
                            ts = record.timestamp();
                    }
                }
            }
        }
        if (ts > 0) {
            sink.flush();
            latency.add(System.currentTimeMillis() - ts);
        }
        LOGGER.debug("Done");
    }

    void start() {
        sinkThread.start();
        LOGGER.info("Sink " + name + " started");
    }

    void stop() {
        stopping = true;
        sinkThread.interrupt();
        try {
            sinkThread.join();
        } catch (InterruptedException e) {
            LOGGER.warn("Stop interrupted", e);
        }
    }

    String name() {
        return name;
    }

    String description() {
        return description;
    }

    void snapshot(MetricsRecordBuilder rb, boolean all) {
        registry.snapshot(rb, all);
    }

    MetricsSink sink() {
        return sink;
    }

}
