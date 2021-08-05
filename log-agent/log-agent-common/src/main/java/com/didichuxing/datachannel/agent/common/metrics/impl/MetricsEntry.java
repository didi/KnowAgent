package com.didichuxing.datachannel.agent.common.metrics.impl;

public class MetricsEntry {
    private String                      sourceName;
    private Iterable<MetricsRecordImpl> records;

    public MetricsEntry(String sourceName, Iterable<MetricsRecordImpl> records) {
        this.sourceName = sourceName;
        this.records = records;
    }

    public MetricsEntry() {
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Iterable<MetricsRecordImpl> getRecords() {
        return records;
    }

    public void setRecords(Iterable<MetricsRecordImpl> records) {
        this.records = records;
    }
}
