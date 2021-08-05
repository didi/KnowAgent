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
import com.didichuxing.datachannel.agent.common.metrics.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.metrics.MetricsException;
import com.didichuxing.datachannel.agent.common.metrics.MetricsFilter;
import com.didichuxing.datachannel.agent.common.metrics.MetricsRecordBuilder;
import com.didichuxing.datachannel.agent.common.metrics.MetricsSink;
import com.didichuxing.datachannel.agent.common.metrics.MetricsSource;
import com.didichuxing.datachannel.agent.common.metrics.MetricsSystem;
import com.didichuxing.datachannel.agent.common.metrics.MetricsTag;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricMutableCounterLong;
import com.didichuxing.datachannel.agent.common.metrics.lib.MetricMutableStat;
import com.didichuxing.datachannel.agent.common.metrics.util.MBeans;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.math.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A base class for metrics system singletons
 */
public class MetricsSystemImpl implements MetricsSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsSystemImpl.class);

    private static final String MS_CONTEXT         = "metricssystem";

    private static final String NUM_SOURCES_KEY    = "num_sources";

    private static final String NUM_SOURCES_DESC   = "Number of metrics sources";

    private static final String NUM_SINKS_KEY      = "num_sinks";

    private static final String NUM_SINKS_DESC     = "Number of metrics sinks";

    private static final String MS_NAME            = "MetricsSystem";

    private static final String MS_STATS_NAME      = MS_NAME + ",sub=Stats";

    private static final String MS_STATS_DESC      = "Metrics system metrics";

    private static final String MS_CONTROL_NAME    = MS_NAME + ",sub=Control";

    private final Map<String, MetricsSourceAdapter> sources = new LinkedHashMap<>();

    private final Map<String, MetricsSinkAdapter> sinks = new LinkedHashMap<>();

    private final List<Callback> callbacks = new ArrayList<>();

    private final MetricsBuilderImpl metricsBuilder = new MetricsBuilderImpl();

    private final MetricMutableStat                 snapshotStat       = new MetricMutableStat(
                                                                           "snapshot",
                                                                           "snapshot stats", "ops",
                                                                           "time", true);

    private final MetricMutableStat                 publishStat        = new MetricMutableStat(
                                                                           "publish",
                                                                           "publishing stats",
                                                                           "ops", "time", true);

    private final MetricMutableCounterLong          dropStat           = new MetricMutableCounterLong(
                                                                           "dropped_pub_all",
                                                                           "number of dropped updates by all sinks",
                                                                           0L);

    private final List<MetricsTag> injectedTags = new ArrayList<>();

    // Things that are changed by init()/start()/stop()
    private String                                  prefix;

    private MetricsFilter                           sourceFilter;

    private MetricsConfig                           config;

    private Map<String, MetricsConfig> sourceConfigs = new HashMap<>();
    private Map<String, MetricsConfig> sinkConfigs = new HashMap<>();

    private boolean monitoring = false;

    private Timer timer;

    private int period; // seconds

    private long logicalTime; // number of timer invocations * period

    private ObjectName                              mbeanName;

    private boolean                                 publishSelfMetrics = false;

    private MetricsSourceAdapter                    sysSource;

    /**
     * Construct the system but not initializing (read config etc.) it.
     */
    public MetricsSystemImpl() {
        this(null);
    }

    /**
     * Construct the metrics system
     * @param prefix  for the system
     */
    public MetricsSystemImpl(String prefix) {
        if (prefix != null) {
            // prefix could be null for default ctor, which requires init later
            initSystemMBean();
        }
        this.prefix = prefix;
    }

    /**
     * Initialized the metrics system with a prefix.
     * @param prefix  the system will look for configs with the prefix
     */
    public synchronized void init(String prefix) {
        if (prefix == null) {
            LogGather.recordErrorLog("MetricsSystem error", "prefix is null");
            throw new NullPointerException("prefix is null");
        }
        if (monitoring) {
            LOGGER.warn(this.prefix + " metrics system already initialized!");
            return;
        }
        this.prefix = prefix;
        try {
            start();
        } catch (MetricsConfigException e) {
            // Usually because hadoop-metrics2.properties is missing
            // We can always start the metrics system later via JMX.
            LOGGER.warn("Metrics system not started: " + e.getMessage());
            LOGGER.debug("Stacktrace: ", e);
        }
        initSystemMBean();
    }

    @Override
    public synchronized void start() {
        if (prefix == null) {
            LogGather.recordErrorLog("MetricsSystem error", "prefix is null");
            throw new NullPointerException("prefix is null");
        }
        if (monitoring) {
            LOGGER.warn(prefix + " metrics system already started!", new MetricsException(
                "Illegal start"));
            return;
        }
        for (Callback cb : callbacks)
            cb.preStart();
        configure(prefix);
        startTimer();
        monitoring = true;
        LOGGER.info(prefix + " metrics system started");
        for (Callback cb : callbacks)
            cb.postStart();
    }

    @Override
    public synchronized void stop() {
        if (!monitoring) {
            LOGGER.warn(prefix + " metrics system not yet started!", new MetricsException("Illegal stop"));
            return;
        }
        for (Callback cb : callbacks)
            cb.preStop();
        LOGGER.info("Stopping " + prefix + " metrics system...");
        stopTimer();
        stopSources();
        stopSinks();
        clearConfigs();
        monitoring = false;
        LOGGER.info(prefix + " metrics system stopped.");
        for (Callback cb : callbacks)
            cb.postStop();
    }

    @Override
    public synchronized <T extends MetricsSource> T register(final String name, final String desc,
                                                             final T source) {
        if (monitoring) {
            registerSource(name, desc, source);
        }
        // We want to re-register the source to pick up new config when the
        // metrics system restarts.
        register(new AbstractCallback() {

            @Override
            public void postStart() {
                registerSource(name, desc, source);
            }

        });
        return source;
    }

    @Override
    public synchronized MetricsSource unRegister(final String name) {
        MetricsSource metriceSource = null;
        if (monitoring) {
            metriceSource = unRegisterSource(name);
        }

        if (null == metriceSource) {
            return null;
        }

        register(new AbstractCallback() {
            @Override
            public void postStart() {
                unRegisterSource(name);
            }
        });

        return metriceSource;
    }

    synchronized void registerSource(String name, String desc, MetricsSource source) {
        if (config == null) {
            LogGather.recordErrorLog("MetricsSystem error", "config is null");
            throw new NullPointerException("config is null");
        }
        MetricsSourceAdapter sa = sources.get(name);
        if (sa != null) {
            LOGGER.warn("Source name " + name + " already exists!");
            return;
        }
        MetricsConfig conf = sourceConfigs.get(name);
        sa = conf != null ? new MetricsSourceAdapter(prefix, name, desc, source, injectedTags,
            period, conf) : new MetricsSourceAdapter(prefix, name, desc, source, injectedTags,
            period, config.subset(MetricsConfig.SOURCE_KEY));
        sources.put(name, sa);
        sa.start();
        LOGGER.debug("Registered source " + name);
    }

    synchronized MetricsSource unRegisterSource(String name) {
        if (config == null) {
            LogGather.recordErrorLog("MetricsSystem error", "config is null");
            throw new NullPointerException("config is null");
        }
        MetricsSourceAdapter sa = sources.get(name);
        if (sa == null) {
            LOGGER.warn("Source name " + name + " not exists!");
            return null;
        }

        sources.remove(name);
        sa.stop();
        LOGGER.debug("Unregistered source " + name);
        return sa.source();
    }

    @Override
    public synchronized <T extends MetricsSink> T register(final String name,
                                                           final String description, final T sink) {
        if (config != null) {
            registerSink(name, description, sink);
        }
        // We want to re-register the sink to pick up new config
        // when the metrics system restarts.
        register(new AbstractCallback() {

            @Override
            public void postStart() {
                registerSink(name, description, sink);
            }

        });
        return sink;
    }

    synchronized void registerSink(String name, String desc, MetricsSink sink) {
        if (config == null) {
            LogGather.recordErrorLog("MetricsSystem error", "config is null");
            throw new NullPointerException("config is null");
        }
        MetricsSinkAdapter sa = sinks.get(name);
        if (sa != null) {
            LOGGER.warn("Sink name " + name + " already exists!");
            return;
        }
        MetricsConfig conf = sinkConfigs.get(name);
        sa = conf != null ? newSink(name, desc, sink, conf) : newSink(name, desc, sink,
            config.subset(MetricsConfig.SINK_KEY));
        sinks.put(name, sa);
        sa.start();
        LOGGER.debug("Registered sink " + name);
    }

    @Override
    public synchronized void register(final Callback callback) {
        callbacks.add((Callback) Proxy.newProxyInstance(callback.getClass().getClassLoader(),
            new Class<?>[] { Callback.class }, new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    try {
                        return method.invoke(callback, args);
                    } catch (Exception e) {
                        LOGGER.warn("Caught exception in callback " + method.getName(), e);
                    }
                    return null;
                }
            }));
    }

    @Override
    public synchronized void refreshMBeans() {
        for (MetricsSourceAdapter sa : sources.values()) {
            sa.refreshMBean();
        }
    }

    @Override
    public synchronized String currentConfig() {
        PropertiesConfiguration saver = new PropertiesConfiguration();
        StringWriter writer = new StringWriter();
        saver.copy(config);
        try {
            saver.save(writer);
        } catch (Exception e) {
            throw new MetricsConfigException("Error stringify config", e);
        }
        return writer.toString();
    }

    private synchronized void startTimer() {
        if (timer != null) {
            LOGGER.warn(prefix + " metrics system timer already started!");
            return;
        }
        logicalTime = 0;
        long millis = period * 1000;
        timer = new Timer("Timer for '" + prefix + "' metrics system", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    onTimerEvent();
                } catch (Exception e) {
                    LOGGER.warn("", e);
                }
            }
        }, millis, millis);
        LOGGER.info("Scheduled snapshot period at " + period + " second(s).");
    }

    private synchronized void onTimerEvent() {
        logicalTime += period;
        if (sinks.size() > 0) {
            publishMetrics(snapshotMetrics());
        }
    }

    /**
     * snapshot all the sources for a snapshot of metrics/tags
     * @return the metrics buffer containing the snapshot
     */
    private synchronized List<MetricsEntry> snapshotMetrics() {
        metricsBuilder.clear();
        List<MetricsEntry> buffers = new ArrayList<>();

        for (Map.Entry<String, MetricsSourceAdapter> entry : sources.entrySet()) {
            if (sourceFilter == null || sourceFilter.accepts(entry.getKey())) {
                snapshotMetrics(entry.getValue(), buffers);
            }
        }
        if (publishSelfMetrics) {
            snapshotMetrics(sysSource, buffers);
        }
        return buffers;
    }

    private void snapshotMetrics(MetricsSourceAdapter sa, List<MetricsEntry> entries) {
        long startTime = System.currentTimeMillis();
        entries.add(new MetricsEntry(sa.name(), sa.getMetrics(metricsBuilder, false)));
        metricsBuilder.clear();
        snapshotStat.add(System.currentTimeMillis() - startTime);
        LOGGER.debug("Snapshotted source " + sa.name());
    }

    /**
     * Publish a metrics snapshot to all the sinks
     * @param entries  the metrics snapshot to publish
     */
    private synchronized void publishMetrics(List<MetricsEntry> entries) {
        int dropped = 0;
        for (MetricsSinkAdapter sa : sinks.values()) {
            long startTime = System.currentTimeMillis();
            dropped += sa.putMetrics(entries, logicalTime) ? 0 : 1;
            publishStat.add(System.currentTimeMillis() - startTime);
        }
        dropStat.incr(dropped);
    }

    private synchronized void stopTimer() {
        if (timer == null) {
            LOGGER.warn(prefix + " metrics system timer already stopped!");
            return;
        }
        timer.cancel();
        timer = null;
    }

    private synchronized void stopSources() {
        for (Map.Entry<String, MetricsSourceAdapter> entry : sources.entrySet()) {
            MetricsSourceAdapter sa = entry.getValue();
            LOGGER.info("Stopping metrics source " + entry.getKey() + "("
                        + sa.source().getClass().getName() + ")");
            sa.stop();
        }
        sysSource.stop();
        sources.clear();
    }

    private synchronized void stopSinks() {
        for (Map.Entry<String, MetricsSinkAdapter> entry : sinks.entrySet()) {
            MetricsSinkAdapter sa = entry.getValue();
            LOGGER.info("Stopping metrics sink " + entry.getKey() + "("
                        + sa.sink().getClass().getName() + ")");
            sa.stop();
        }
        sinks.clear();
    }

    private synchronized void configure(String prefix) {
        config = MetricsConfig.create(prefix);
        configureSinks();
        configureSources();
        configureSystem();
    }

    private synchronized void configureSystem() {
        injectedTags.add(new MetricsTag("hostName", "Local hostname", getHostname()));
    }

    private synchronized void configureSinks() {
        sinkConfigs = config.getInstanceConfigs(MetricsConfig.SINK_KEY);
        int confPeriod = 0;
        for (Map.Entry<String, MetricsConfig> entry : sinkConfigs.entrySet()) {
            MetricsConfig conf = entry.getValue();
            int sinkPeriod = conf.getInt(MetricsConfig.PERIOD_KEY, MetricsConfig.PERIOD_DEFAULT);
            confPeriod = confPeriod == 0 ? sinkPeriod : MathUtils.gcd(confPeriod, sinkPeriod);
            String sinkName = entry.getKey();
            LOGGER.debug("sink " + sinkName + " config:\n" + conf);
            try {
                MetricsSinkAdapter sa = newSink(sinkName,
                    conf.getString(MetricsConfig.DESC_KEY, sinkName), conf);
                // we allow config of later registered sinks
                if (sa != null) {
                    sa.start();
                    sinks.put(sinkName, sa);
                }
            } catch (Exception e) {
                LOGGER.warn("Error creating " + sinkName, e);
            }
        }
        period = confPeriod > 0 ? confPeriod : config.getInt(MetricsConfig.PERIOD_KEY,
            MetricsConfig.PERIOD_DEFAULT);
    }

    private static MetricsSinkAdapter newSink(String name, String desc, MetricsSink sink, MetricsConfig conf) {
        return new MetricsSinkAdapter(name, desc, sink, conf.getString(MetricsConfig.CONTEXT_KEY),
            conf.getFilter(MetricsConfig.SOURCE_FILTER_KEY),
            conf.getFilter(MetricsConfig.RECORD_FILTER_KEY),
            conf.getFilter(MetricsConfig.METRIC_FILTER_KEY), conf.getInt(MetricsConfig.PERIOD_KEY,
                MetricsConfig.PERIOD_DEFAULT), conf.getInt(MetricsConfig.QUEUE_CAPACITY_KEY,
                MetricsConfig.QUEUE_CAPACITY_DEFAULT), conf.getInt(MetricsConfig.RETRY_DELAY_KEY,
                MetricsConfig.RETRY_DELAY_DEFAULT), conf.getFloat(MetricsConfig.RETRY_BACKOFF_KEY,
                MetricsConfig.RETRY_BACKOFF_DEFAULT), conf.getInt(MetricsConfig.RETRY_COUNT_KEY,
                MetricsConfig.RETRY_COUNT_DEFAULT));
    }

    private static MetricsSinkAdapter newSink(String name, String desc, MetricsConfig conf) {
        MetricsSink sink = conf.getPlugin("");
        if (sink == null)
            return null;
        return newSink(name, desc, sink, conf);
    }

    private void configureSources() {
        sourceFilter = config.getFilter(MetricsConfig.PREFIX_DEFAULT
                                        + MetricsConfig.SOURCE_FILTER_KEY);
        Map<String, MetricsConfig> confs = config.getInstanceConfigs(MetricsConfig.SOURCE_KEY);
        for (Map.Entry<String, MetricsConfig> entry : confs.entrySet()) {
            sourceConfigs.put(entry.getKey(), entry.getValue());
        }
        registerSystemSource();
    }

    private void clearConfigs() {
        sinkConfigs.clear();
        sourceConfigs.clear();
        injectedTags.clear();
        config = null;
    }

    static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            LOGGER.error("Error getting localhost name. Using 'localhost'...", e);
        }
        return "localhost";
    }

    private void registerSystemSource() {
        sysSource = new MetricsSourceAdapter(prefix, MS_STATS_NAME, MS_STATS_DESC,
            new MetricsSource() {
                @Override
                public void getMetrics(MetricsBuilder builder, boolean all) {
                    int numSources, numSinks;
                    synchronized (MetricsSystemImpl.this) {
                        numSources = sources.size();
                        numSinks = sinks.size();
                    }
                    MetricsRecordBuilder rb = builder.addRecord(MS_NAME).setContext(MS_CONTEXT)
                        .addGauge(NUM_SOURCES_KEY, NUM_SOURCES_DESC, numSources)
                        .addGauge(NUM_SINKS_KEY, NUM_SINKS_DESC, numSinks);
                    synchronized (MetricsSystemImpl.this) {
                        for (MetricsSinkAdapter sa : sinks.values()) {
                            sa.snapshot(rb, all);
                        }
                    }
                    snapshotStat.snapshot(rb, all);
                    publishStat.snapshot(rb, all);
                    dropStat.snapshot(rb, all);
                }
            }, injectedTags, null, null, period);
        sysSource.start();
    }

    private void initSystemMBean() {
        if (prefix == null) {
            LogGather.recordErrorLog("MetricsSystem error", "prefix is null");
            throw new NullPointerException("prefix is null");
        }
        mbeanName = MBeans.register(prefix, MS_CONTROL_NAME, this);
    }

    @Override
    public synchronized void shutdown() {
        if (monitoring) {
            try {
                stop();
            } catch (Exception e) {
                LOGGER.warn("Error stopping the metrics system", e);
            }
        }
        MBeans.unregister(mbeanName);
    }

    public boolean isPublishSelfMetrics() {
        return publishSelfMetrics;
    }

    public void setPublishSelfMetrics(boolean publishSelfMetrics) {
        this.publishSelfMetrics = publishSelfMetrics;
    }
}
