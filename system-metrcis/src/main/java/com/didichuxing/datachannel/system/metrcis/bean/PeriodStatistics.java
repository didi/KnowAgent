package com.didichuxing.datachannel.system.metrcis.bean;

/**
 * @param <T> 数值类型
 * 统计周内的统计量
 * @author william.
 */

public class PeriodStatistics {

    /**
     * 统计周期内最后一次采样值
     */
    private Double last;

    /**
     * 统计周期内样本最大值
     */
    private Double max;

    /**
     * 统计周期内样本最小值
     */
    private Double min;

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
    private Double quantile55;

    /**
     * 统计周期内样本75分位数
     */
    private Double quantile75;

    /**
     * 统计周期内样本95分位数
     */
    private Double quantile95;

    /**
     * 统计周期内样本99分位数
     */
    private Double quantile99;

    public Double getLast() {
        return last;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
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

    public Double getQuantile55() {
        return quantile55;
    }

    public void setQuantile55(Double quantile55) {
        this.quantile55 = quantile55;
    }

    public Double getQuantile75() {
        return quantile75;
    }

    public void setQuantile75(Double quantile75) {
        this.quantile75 = quantile75;
    }

    public Double getQuantile95() {
        return quantile95;
    }

    public void setQuantile95(Double quantile95) {
        this.quantile95 = quantile95;
    }

    public Double getQuantile99() {
        return quantile99;
    }

    public void setQuantile99(Double quantile99) {
        this.quantile99 = quantile99;
    }
}
