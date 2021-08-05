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

package com.didichuxing.datachannel.agent.common.metrics;

/**
 * Immutable tag for metrics (for grouping on host/queue/username etc.)
 */
public class MetricsTag {

    private final String name;
    private final String description;
    private final String value;

    public MetricsTag(String name, String description, String value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetricsTag other = (MetricsTag) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.description == null) {
            return other.description == null;
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.value == null) {
            return other.value == null;
        } else {
            return this.value.equals(other.value);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return "MetricsTag{" + "name='" + name + "' description='" + description + "' value='"
               + value + "'}";
    }

}
