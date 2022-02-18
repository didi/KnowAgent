package com.didichuxing.datachannel.agent.engine.metrics.stat;

public class MetricMutablePeriodGaugeLong {

    private volatile long    value;
    private volatile boolean changed = true;

    /**
     * Construct a mutable long gauge metric
     */
    public MetricMutablePeriodGaugeLong() {
        this.value = 0;
    }

    public synchronized void incr() {
        ++value;
        setChanged();
    }

    /**
     * Increment by delta
     * @param delta of the increment
     */
    public synchronized void incr(long delta) {
        value += delta;
        setChanged();
    }

    public synchronized void decr() {
        --value;
        setChanged();
    }

    /**
     * decrement by delta
     * @param delta of the decrement
     */
    public synchronized void decr(long delta) {
        value -= delta;
        setChanged();
    }

    /**
     * Set the value of the metric
     * @param value to set
     */
    public void set(long value) {
        this.value = value;
        setChanged();
    }

    /**
     * set the value of the metric with 0
     */
    public void reset() {
        this.value = 0;
    }

    public synchronized long snapshot() {
        long reuslt = this.value;
        if (changed()) {
            clearChanged();
            reset();
        }
        return reuslt;
    }

    /**
     * Clear the changed flag in the snapshot operations
     */
    protected void clearChanged() {
        changed = false;
    }

    /**
     * Set the changed flag in mutable operations
     */
    protected void setChanged() {
        changed = true;
    }

    /**
     * @return  true if metric is changed since last snapshot/snapshot
     */
    public boolean changed() {
        return changed;
    }

}
