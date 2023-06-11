/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.agent.engine.metrics.system.lib;

import com.didichuxing.datachannel.agent.engine.metrics.system.Metric;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsException;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsRecordBuilder;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An optional metrics registry class for creating and maintaining a
 * collection of MetricsMutables, making writing metrics source easier.
 *
 * @see com.didichuxing.datachannel.agent.engine.metrics.system.impl.MetricsSystemImpl
 */
public class MetricsRegistry {
    private static final Logger              LOGGER          = LoggerFactory
                                                                 .getLogger(MetricsRegistry.class);

    /** key for the context tag */
    public static final String               CONTEXT_KEY     = "context";

    /** description for the context tag */
    public static final String               CONTEXT_DESC    = "Metrics context";

    /**
     * key for the model name
     */
    public static final String               MODEL_NAME      = "modelName";

    public static final String               MODEL_NAME_DESC = "Model Name";

    /**
     * key for the node id of the mode name,specify a uniq node 
     */
    public static final String               NODE_ID         = "nodeId";

    public static final String               NODE_ID_DESC    = "Node ID";

    private final Map<String, MetricMutable> metricsMap      = new LinkedHashMap<String, MetricMutable>();

    private final HashMap<String, MetricsTag> tagsMap = new HashMap<>();

    private final String                     name;

    private final MetricMutableFactory       mf;

    /**
     * Construct the registry with a record name
     * @param name  of the record of the metrics
     */
    public MetricsRegistry(String name) {
        this.name = name;
        this.mf = new MetricMutableFactory();
    }

    /**
     * Construct the registry with a name and a metric factory
     * @param name  of the record of the metrics
     * @param factory for creating new mutable metrics
     */
    public MetricsRegistry(String name, MetricMutableFactory factory) {
        this.name = name;
        this.mf = factory;
    }

    /**
     * @return  the name of the metrics registry
     */
    public String name() {
        return name;
    }

    /**
     * Get a metric by name
     * @param name  of the metric
     * @return  the metric object
     */
    public MetricMutable get(String name) {
        return metricsMap.get(name);
    }

    /**
     * Create a mutable integer counter
     * @param name  of the metric
     * @param description of the metric
     * @param initValue of the metric
     * @return  a new counter object
     */
    public MetricMutableCounterInt newCounter(String name, String description, int initValue) {
        checkMetricName(name);
        MetricMutableCounterInt ret = mf.newCounter(name, description, initValue);
        metricsMap.put(name, ret);
        return ret;
    }

    /**
     * Create a mutable long integer counter
     * @param name  of the metric
     * @param description of the metric
     * @param initValue of the metric
     * @return  a new counter object
     */
    public MetricMutableCounterLong newCounter(String name, String description, long initValue) {
        checkMetricName(name);
        MetricMutableCounterLong ret = mf.newCounter(name, description, initValue);
        metricsMap.put(name, ret);
        return ret;
    }

    /**
     * Create a mutable integer gauge
     * @param name  of the metric
     * @param description of the metric
     * @param initValue of the metric
     * @return  a new gauge object
     */
    public MetricMutableGaugeInt newGauge(String name, String description, int initValue) {
        checkMetricName(name);
        MetricMutableGaugeInt ret = mf.newGauge(name, description, initValue);
        metricsMap.put(name, ret);
        return ret;
    }

    /**
     * Create a mutable integer gauge
     * @param name  of the metric
     * @param description of the metric
     * @param initValue of the metric
     * @return  a new gauge object
     */
    public MetricMutablePeriodGaugeInt newPeriodGauge(String name, String description, int initValue) {
        checkMetricName(name);
        MetricMutablePeriodGaugeInt ret = mf.newPeriodGauge(name, description, initValue);
        metricsMap.put(name, ret);
        return ret;
    }

    /**
     * Create a mutable long integer gauge
     * @param name  of the metric
     * @param description of the metric
     * @param initValue of the metric
     * @return  a new gauge object
     */
    public MetricMutablePeriodGaugeLong newPeriodGauge(String name, String description,
                                                       long initValue) {
        checkMetricName(name);
        MetricMutablePeriodGaugeLong ret = mf.newPeriodGauge(name, description, initValue);
        metricsMap.put(name, ret);
        return ret;
    }

    /**
     * Create a mutable reference
     * @param name  of the metric
     * @param description of the metric
     * @param initValue of the metric
     * @return  a new gauge object
     */
    public <T> MetricMutableReference<T> newReference(String name, String description, T initValue) {
        checkMetricName(name);
        MetricMutableReference<T> ret = mf.newReference(name, description, initValue);
        metricsMap.put(name, ret);
        return ret;

    }

    /**
     * Create a mutable metric with stats
     * @param name  of the metric
     * @param description of the metric
     * @param sampleName  of the metric (e.g., "ops")
     * @param valueName   of the metric (e.g., "time" or "latency")
     * @param extended    produce extended stat (stdev, min/max etc.) if true.
     * @return  a new metric object
     */
    public MetricMutableStat newStat(String name, String description, String sampleName,
                                     String valueName, boolean extended) {
        checkMetricName(name);
        MetricMutableStat ret = mf.newStat(name, description, sampleName, valueName, extended);
        metricsMap.put(name, ret);
        return ret;
    }

    /**
     * Create a mutable metric with stats
     * @param name  of the metric
     * @param description of the metric
     * @param sampleName  of the metric (e.g., "ops")
     * @param valueName   of the metric (e.g., "time" or "latency")
     * @return  a new metric object
     */
    public MetricMutableStat newStat(String name, String description, String sampleName,
                                     String valueName) {
        return newStat(name, description, sampleName, valueName, false);
    }

    /**
     * Create a mutable metric with stats using the name only
     * @param name  of the metric
     * @return a new metric object
     */
    public MetricMutableStat newStat(String name) {
        return newStat(name, "", "ops", "time", false);
    }

    /**
     * Increment a metric by name.
     * @param name  of the metric
     */
    public void incr(String name) {
        incr(name, mf);
    }

    /**
     * Increment a metric by name.
     * @param name  of the metric
     * @param factory to lazily create the metric if not null
     */
    public void incr(String name, MetricMutableFactory factory) {
        MetricMutable m = metricsMap.get(name);

        if (m != null) {
            if (m instanceof MetricMutableGauge<?>) {
                ((MetricMutableGauge<?>) m).incr();
            } else if (m instanceof MetricMutableCounter<?>) {
                ((MetricMutableCounter<?>) m).incr();
            } else if (m instanceof MetricMutablePeriodGauge<?>) {
                ((MetricMutablePeriodGauge<?>) m).incr();
            } else {
                throw new MetricsException("Unsupported incr() for metric " + name);
            }
        } else if (factory != null) {
            synchronized (this) {
                if (!metricsMap.containsKey(name)) {
                    metricsMap.put(name, factory.newMetric(name));
                }
            }

            incr(name, null);
        } else {
            throw new MetricsException("Metric " + name + " doesn't exist");
        }
    }

    /**
     * Decrement a metric by name.
     * @param name  of the metric
     */
    public void decr(String name) {
        decr(name, mf);
    }

    /**
     * Decrement a metric by name.
     * @param name  of the metric
     * @param factory to lazily create the metric if not null
     */
    public void decr(String name, MetricMutableFactory factory) {
        MetricMutable m = metricsMap.get(name);

        if (m != null) {
            if (m instanceof MetricMutableGauge<?>) {
                ((MetricMutableGauge<?>) m).decr();
            } else if (m instanceof MetricMutablePeriodGauge<?>) {
                ((MetricMutablePeriodGauge<?>) m).incr();
            } else {
                throw new MetricsException("Unsupported decr() for metric " + name);
            }
        } else if (factory != null) {
            synchronized (this) {
                if (!metricsMap.containsKey(name)) {
                    metricsMap.put(name, factory.newMetric(name));
                }
            }
            decr(name, null);
        } else {
            throw new MetricsException("Metric " + name + " doesn't exist");
        }
    }

    /**
     * Add a value to a metric by name.
     * @param name  of the metric
     * @param value of the snapshot to add
     */
    public void add(String name, long value) {
        add(name, value, mf);
    }

    /**
     * Decrement a metric by name.
     * @param name  of the metric
     * @param value of the snapshot to add
     * @param factory to lazily create the metric if not null
     */
    public void add(String name, long value, MetricMutableFactory factory) {
        MetricMutable m = metricsMap.get(name);

        if (m != null) {
            if (m instanceof MetricMutableStat) {
                ((MetricMutableStat) m).add(value);
            } else {
                throw new MetricsException("Unsupported add(value) for metric " + name);
            }
        } else if (factory != null) {
            synchronized (this) {
                if (!metricsMap.containsKey(name)) {
                    metricsMap.put(name, factory.newStat(name));
                }
            }
            add(name, value, null);
        } else {
            throw new MetricsException("Metric " + name + " doesn't exist");
        }
    }

    /**
     * Set a value to a metric by name.
     * @param name  of the metric
     * @param value of the snapshot to add
     */
    public <T> void set(String name, T value) {
        set(name, value, mf);
    }

    /**
     * Set a metric by name with new value.
     * @param name  of the metric
     * @param value of the snapshot to add
     * @param factory to lazily create the metric if not null
     */
    public <T> void set(String name, T value, MetricMutableFactory factory) {
        MetricMutable m = metricsMap.get(name);

        if (m != null) {
            if (m instanceof MetricMutableReference<?>) {
                ((MetricMutableReference) m).set(value);
            } else {
                throw new MetricsException("Unsupported add(value) for metric " + name);
            }
        } else if (factory != null) {
            synchronized (this) {
                if (!metricsMap.containsKey(name)) {
                    metricsMap.put(name, factory.newReference(name, Metric.NO_DESCRIPTION, value));
                }
            }
            set(name, value);
        } else {
            throw new MetricsException("Metric " + name + " doesn't exist");
        }
    }

    /**
     * Set the metrics context tag
     * @param name of the context
     * @return the registry itself as a convenience
     */
    public MetricsRegistry setContext(String name) {
        return tag(CONTEXT_KEY, CONTEXT_DESC, name);
    }

    /**
     * Add a tag to the metrics
     * @param name  of the tag
     * @param description of the tag
     * @param value of the tag
     * @return  the registry (for keep adding tags)
     */
    public MetricsRegistry tag(String name, String description, String value) {
        return tag(name, description, value, false);
    }

    /**
     * Add a tag to the metrics
     * @param name  of the tag
     * @param description of the tag
     * @param value of the tag
     * @param override  existing tag if true
     * @return  the registry (for keep adding tags)
     */
    public MetricsRegistry tag(String name, String description, String value, boolean override) {
        if (name == null) {
            throw new MetricsException("metric name is null");
        }
        if (!override) {
            checkTagName(name);
        }
        tagsMap.put(name, new MetricsTag(name, description, value));
        return this;
    }

    /**
     * Get the metrics
     * @return  the metrics set
     */
    public Set<Entry<String, MetricMutable>> metrics() {
        return metricsMap.entrySet();
    }

    private void checkMetricName(String name) {
        if (metricsMap.containsKey(name)) {
            throw new MetricsException("Metric name " + name + " already exists!");
        }
    }

    private void checkTagName(String name) {
        if (tagsMap.containsKey(name)) {
            throw new MetricsException("Tag " + name + " already exists!");
        }
    }

    /**
     * Sample all the mutable metrics and put the snapshot in the builder
     * @param builder to contain the metrics snapshot
     * @param all get all the metrics even if the values are not changed.
     */
    public void snapshot(MetricsRecordBuilder builder, boolean all) {
        for (Entry<String, MetricsTag> entry : tagsMap.entrySet()) {
            builder.add(entry.getValue());
        }
        for (Entry<String, MetricMutable> entry : metrics()) {
            entry.getValue().snapshot(builder, all);
        }
    }

}
