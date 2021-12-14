package com.didichuxing.datachannel.system.metrcis.bean;

/**
 * @param <T> 数值类型
 * 统计周内的统计量
 * @author william.
 */
public class PeriodStatistics<T extends Number> {

    /**
     * 统计周期内最后一次采样值
     */
    private T last;

    /**
     * 统计周期内样本最大值
     */
    private T max;

    /**
     * 统计周期内样本最小值
     */
    private T min;

    /**
     * 统计周期内样本均值
     */
    private Double avg;

    /**
     * 统计周期内样本标准差
     */
    private Double stdDev;

    /**
     * 统计周期内样本55分位数
     */
    private T quantile55;

    /**
     * 统计周期内样本75分位数
     */
    private T quantile75;

    /**
     * 统计周期内样本95分位数
     */
    private T quantile95;

    /**
     * 统计周期内样本99分位数
     */
    private T quantile99;

    public T getLast() {
        return last;
    }

    public void setLast(T last) {
        this.last = last;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getStdDev() {
        return stdDev;
    }

    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }

    public T getQuantile55() {
        return quantile55;
    }

    public void setQuantile55(T quantile55) {
        this.quantile55 = quantile55;
    }

    public T getQuantile75() {
        return quantile75;
    }

    public void setQuantile75(T quantile75) {
        this.quantile75 = quantile75;
    }

    public T getQuantile95() {
        return quantile95;
    }

    public void setQuantile95(T quantile95) {
        this.quantile95 = quantile95;
    }

    public T getQuantile99() {
        return quantile99;
    }

    public void setQuantile99(T quantile99) {
        this.quantile99 = quantile99;
    }

}
