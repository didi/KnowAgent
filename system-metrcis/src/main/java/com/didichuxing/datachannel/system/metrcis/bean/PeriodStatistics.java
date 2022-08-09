package com.didichuxing.datachannel.system.metrcis.bean;

import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxSystemMetricsServiceImpl;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计周内的统计量
 * @author william.
 */

public class PeriodStatistics {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodStatistics.class);

    /**
     * 存放样本值
     */
    private volatile List<Double> samples = new ArrayList<>();

    /**
     * 统计周期内最后一次采样值
     */
    private volatile Double last;

    /**
     * 统计周期内样本最大值
     */
    private volatile Double max = Double.MIN_VALUE;

    /**
     * 统计周期内样本最小值
     */
    private volatile Double min = Double.MAX_VALUE;

    /**
     * 统计周期内样本均值
     */
    private volatile Double avg;

    /**
     * 统计周期内样本标准差
     */
    private volatile Double stdDev;

    /**
     * 统计周期内样本55分位数
     */
    private volatile Double quantile55;

    /**
     * 统计周期内样本75分位数
     */
    private volatile Double quantile75;

    /**
     * 统计周期内样本95分位数
     */
    private volatile Double quantile95;

    /**
     * 统计周期内样本99分位数
     */
    private volatile Double quantile99;

    /**
     * @return 采用0作为各维度默认值PeriodStatistics对象
     */
    public static PeriodStatistics defaultValue() {
        double value = 0d;
        return new PeriodStatistics(
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value
        );
    }

    public PeriodStatistics(Double last, Double max, Double min, Double avg, Double stdDev, Double quantile55, Double quantile75, Double quantile95, Double quantile99) {
        this.last = last;
        this.max = max;
        this.min = min;
        this.avg = avg;
        this.stdDev = stdDev;
        this.quantile55 = quantile55;
        this.quantile75 = quantile75;
        this.quantile95 = quantile95;
        this.quantile99 = quantile99;
    }

    public PeriodStatistics() {

    }

    public Double getLast() {
        return last;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public Double getAvg() {
        return avg;
    }

    public Double getStdDev() {
        return stdDev;
    }

    public Double getQuantile55() {
        return quantile55;
    }

    public Double getQuantile75() {
        return quantile75;
    }

    public Double getQuantile95() {
        return quantile95;
    }

    public Double getQuantile99() {
        return quantile99;
    }

    /**
     * 添加给定样本
     * @param sample 待添加样本
     */
    public synchronized void add(Double sample) {
        this.samples.add(sample);
    }

    /**
     * @return 返回当前快照对象
     */
    public synchronized PeriodStatistics snapshot() {
        this.last = this.samples.size() == 0 ? 0d : this.samples.get(this.samples.size() - 1);
        this.min = MathUtil.getMin(this.samples);
        this.max = MathUtil.getMax(this.samples);
        this.avg = MathUtil.getMean(this.samples);
        this.stdDev = MathUtil.getStdDev(this.samples);
        this.quantile55 = MathUtil.getQuantile55(this.samples);
        this.quantile75 = MathUtil.getQuantile75(this.samples);
        this.quantile95 = MathUtil.getQuantile95(this.samples);
        this.quantile99 = MathUtil.getQuantile99(this.samples);
        this.samples.clear();
        return this;
    }

    public boolean isEmpty() {
        return this.samples.isEmpty();
    }

}
