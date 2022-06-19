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

package com.didichuxing.datachannel.agent.engine.metrics.stat;

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsRecordBuilder;
import com.didichuxing.datachannel.agent.engine.metrics.system.lib.MetricMutable;

/* 9分位统计时间
    "min": 23,
    "max": 991,
    "mean": 496,
    "stddev": 290,
    "median": 496,
    "75%": 2000,
    "95%": 210,
    "98%": 980,
    "99%": 990,
    "99.9": 991
 */

public class MetricMutableTimeStat extends MetricMutable {
    private volatile long sampleCount = 0, costCount = 0, max = 0, min = Long.MAX_VALUE;
    private Object        lock        = new Object();

    public MetricMutableTimeStat(String name, String description) {
        super(name, description);
    }

    public void add(long cost) {
        synchronized (lock) {
            if (cost > max) {
                max = cost;
            }
            if (cost < min) {
                min = cost;
            }
            sampleCount++;
            costCount += cost;
        }

        setChanged();
    }

    public void add(long cost, int num) {
        long ave = cost / num;
        synchronized (lock) {
            if (ave > max) {
                max = ave;
            }
            if (ave < min) {
                min = ave;
            }
            sampleCount += num;
            costCount += cost;
        }

        setChanged();
    }

    @Override
    public synchronized void snapshot(MetricsRecordBuilder builder, boolean all) {
        if (!all && !changed()) {
            return;
        }

        if (sampleCount == 0) {
            build(builder, 0, 0, 0);
            return;
        }

        long scount, ccount, tmpMax, tmpMin;
        synchronized (lock) {
            scount = sampleCount;
            ccount = costCount;
            tmpMax = max;
            tmpMin = min;

            sampleCount = 0;
            costCount = 0;
            max = 0;
            min = Long.MAX_VALUE;
        }

        long mean = ccount / scount;

        build(builder, tmpMin, tmpMax, mean);
    }

    private void build(MetricsRecordBuilder builder, long min, long max, long mean) {

        builder.addCounter(name + "Min", null, min);
        builder.addCounter(name + "Max", null, max);
        builder.addCounter(name + "Mean", null, mean);
    }
}
