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

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsRecordBuilder;
import com.didichuxing.datachannel.agent.engine.metrics.system.util.SampleStat;
import org.apache.commons.lang.StringUtils;

/**
 * A mutable metric with stats
 *
 * Useful for keep throughput/latency stats.
 * e.g., new MetricMutableStat("rpcName", "rpcName stats", "ops", "time");
 */
public class MetricMutableStat extends MetricMutable {

    private final String            numSamplesName, numSamplesDesc;
    private final String            avgValueName, avgValueDesc;
    private final String            stdevValueName, stdevValueDesc;
    private final String            iMinValueName, iMinValueDesc;
    private final String            iMaxValueName, iMaxValueDesc;
    private final String            minValueName, minValueDesc;
    private final String            maxValueName, maxValueDesc;

    private final SampleStat        intervalStat = new SampleStat();
    //modified by jinbinbin 20170809
    //    private final SampleStat        prevStat     = new SampleStat();
    private final SampleStat.MinMax minMax       = new SampleStat.MinMax();
    private long                    numSamples   = 0;
    private boolean                 extended     = false;

    /**
     * Construct a sample statistics metric
     * @param name        of the metric
     * @param description of the metric
     * @param sampleName  of the metric (e.g. "ops")
     * @param valueName   of the metric (e.g. "time", "latency")
     * @param extended    create extended stats (stdev, min/max etc.) by default.
     */
    public MetricMutableStat(String name, String description, String sampleName, String valueName,
                             boolean extended) {
        super(name, description);
        String desc = StringUtils.uncapitalize(description);
        numSamplesName = name + "_num_" + sampleName;
        numSamplesDesc = "Number of " + sampleName + " for " + desc;
        avgValueName = name + "_avg_" + valueName;
        avgValueDesc = "Average " + valueName + " for " + desc;
        stdevValueName = name + "_stdev_" + valueName;
        stdevValueDesc = "Standard deviation of " + valueName + " for " + desc;
        iMinValueName = name + "_imin_" + valueName;
        iMinValueDesc = "Interval min " + valueName + " for " + desc;
        iMaxValueName = name + "_imax_" + valueName;
        iMaxValueDesc = "Interval max " + valueName + " for " + desc;
        minValueName = name + "_min_" + valueName;
        minValueDesc = "Min " + valueName + " for " + desc;
        maxValueName = name + "_max_" + valueName;
        maxValueDesc = "Max " + valueName + " for " + desc;
        this.extended = extended;
    }

    /**
     * Construct a snapshot stat metric with extended stat off by default
     * @param name        of the metric
     * @param description of the metric
     * @param sampleName  of the metric (e.g. "ops")
     * @param valueName   of the metric (e.g. "time", "latency")
     */
    public MetricMutableStat(String name, String description, String sampleName, String valueName) {
        this(name, description, sampleName, valueName, false);
    }

    /**
     * Add a number of samples and their sum to the running stat
     * @param numSamples  number of samples
     * @param sum of the samples
     */
    public synchronized void add(long numSamples, long sum) {
        intervalStat.add(numSamples, sum);
        setChanged();
    }

    /**
     * Add a snapshot to the metric
     * @param value of the metric
     */
    public synchronized void add(long value) {
        intervalStat.add(value);
        minMax.add(value);
        setChanged();
    }

    public synchronized void snapshot(MetricsRecordBuilder builder, boolean all) {
        if (all || changed()) {
            numSamples += intervalStat.numSamples();
            builder.addCounter(numSamplesName, numSamplesDesc, numSamples);
            builder.addGauge(avgValueName, avgValueDesc, intervalStat().mean());
            if (extended) {
                builder.addGauge(stdevValueName, stdevValueDesc, intervalStat().stddev());
                builder.addGauge(iMinValueName, iMinValueDesc, intervalStat().min());
                builder.addGauge(iMaxValueName, iMaxValueDesc, intervalStat().max());
                builder.addGauge(minValueName, minValueDesc, minMax.min());
                builder.addGauge(maxValueName, maxValueDesc, minMax.max());
            }
            if (changed()) {
                //modified by jinbinbin 20170809
                //                intervalStat.copyTo(prevStat);
                intervalStat.reset();
                clearChanged();
            }
        }
    }

    //modified by jinbinbin 20170809
    private SampleStat intervalStat() {
        return intervalStat;
    }

    //    private SampleStat lastStat() {
    //        return changed() ? intervalStat : prevStat;
    //    }

    /**
     * Reset the all time min max of the metric
     */
    public void resetMinMax() {
        minMax.reset();
    }

}
