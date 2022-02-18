package com.didichuxing.datachannel.system.metrcis.bean;

/**
 * 磁盘io信息
 * @author william.
 */
public class DiskIOInfo {

    /**
     * 磁盘设备名
     */
    private String device;

    /**
     * 返回当前磁盘对应平均队列长度
     */
    private PeriodStatistics iOAvgQuSz;

    /**
     * 返回当前磁盘平均请求大小（单位：扇区）
     */
    private PeriodStatistics iOAvgRqSz;

    /**
     * 返回当前磁盘每次IO请求平均处理时间（单位：ms）
     */
    private PeriodStatistics iOAwait;

    /**
     * 返回当前磁盘读请求平均耗时(单位：ms)
     */
    private PeriodStatistics iORAwait;

    /**
     * 返回当前磁盘每秒读请求数量
     */
    private PeriodStatistics iOReadRequest;

    /**
     * 返回当前磁盘每秒读取字节数
     */
    private PeriodStatistics iOReadBytes;

    /**
     * 返回当前磁盘每秒合并到设备队列的读请求数
     */
    private PeriodStatistics iORRQMS;

    /**
     * 返回当前磁盘每次IO平均服务时间（单位：ms）
     * 注：仅 做参考
     */
    private PeriodStatistics iOSVCTM;

    /**
     * 返回当前磁盘I/O请求的时间百分比
     */
    private PeriodStatistics iOUtil;

    /**
     * 返回当前磁盘写请求平均耗时(单位：ms)
     */
    private PeriodStatistics iOWAwait;

    /**
     * 返回当前磁盘每秒写请求数量
     */
    private PeriodStatistics iOWriteRequest;

    /**
     * 返回当前磁盘每秒写字节数
     */
    private PeriodStatistics iOWriteBytes;

    /**
     * 返回当前磁盘每秒读、写字节数
     */
    private PeriodStatistics iOReadWriteBytes;

    /**
     * 返回当前磁盘每秒合并到设备队列的写请求数
     */
    private PeriodStatistics iOWRQMS;

    /**
     * 磁盘读操作耗时(单位：ms)
     */
    private PeriodStatistics readTime;

    /**
     * 磁盘读取磁盘时间百分比（单位：%）
     */
    private PeriodStatistics readTimePercent;

    /**
     * 磁盘写操作耗时（单位：ms）
     */
    private PeriodStatistics writeTime;

    /**
     * 磁盘写入磁盘时间百分比（单位：%）
     */
    private PeriodStatistics writeTimePercent;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public PeriodStatistics getiOAvgQuSz() {
        return iOAvgQuSz;
    }

    public void setiOAvgQuSz(PeriodStatistics iOAvgQuSz) {
        this.iOAvgQuSz = iOAvgQuSz;
    }

    public PeriodStatistics getiOAvgRqSz() {
        return iOAvgRqSz;
    }

    public void setiOAvgRqSz(PeriodStatistics iOAvgRqSz) {
        this.iOAvgRqSz = iOAvgRqSz;
    }

    public PeriodStatistics getiOAwait() {
        return iOAwait;
    }

    public void setiOAwait(PeriodStatistics iOAwait) {
        this.iOAwait = iOAwait;
    }

    public PeriodStatistics getiORAwait() {
        return iORAwait;
    }

    public void setiORAwait(PeriodStatistics iORAwait) {
        this.iORAwait = iORAwait;
    }

    public PeriodStatistics getiOReadRequest() {
        return iOReadRequest;
    }

    public void setiOReadRequest(PeriodStatistics iOReadRequest) {
        this.iOReadRequest = iOReadRequest;
    }

    public PeriodStatistics getiOReadBytes() {
        return iOReadBytes;
    }

    public void setiOReadBytes(PeriodStatistics iOReadBytes) {
        this.iOReadBytes = iOReadBytes;
    }

    public PeriodStatistics getiORRQMS() {
        return iORRQMS;
    }

    public void setiORRQMS(PeriodStatistics iORRQMS) {
        this.iORRQMS = iORRQMS;
    }

    public PeriodStatistics getiOSVCTM() {
        return iOSVCTM;
    }

    public void setiOSVCTM(PeriodStatistics iOSVCTM) {
        this.iOSVCTM = iOSVCTM;
    }

    public PeriodStatistics getiOUtil() {
        return iOUtil;
    }

    public void setiOUtil(PeriodStatistics iOUtil) {
        this.iOUtil = iOUtil;
    }

    public PeriodStatistics getiOWAwait() {
        return iOWAwait;
    }

    public void setiOWAwait(PeriodStatistics iOWAwait) {
        this.iOWAwait = iOWAwait;
    }

    public PeriodStatistics getiOWriteRequest() {
        return iOWriteRequest;
    }

    public void setiOWriteRequest(PeriodStatistics iOWriteRequest) {
        this.iOWriteRequest = iOWriteRequest;
    }

    public PeriodStatistics getiOWriteBytes() {
        return iOWriteBytes;
    }

    public void setiOWriteBytes(PeriodStatistics iOWriteBytes) {
        this.iOWriteBytes = iOWriteBytes;
    }

    public PeriodStatistics getiOReadWriteBytes() {
        return iOReadWriteBytes;
    }

    public void setiOReadWriteBytes(PeriodStatistics iOReadWriteBytes) {
        this.iOReadWriteBytes = iOReadWriteBytes;
    }

    public PeriodStatistics getiOWRQMS() {
        return iOWRQMS;
    }

    public void setiOWRQMS(PeriodStatistics iOWRQMS) {
        this.iOWRQMS = iOWRQMS;
    }

    public PeriodStatistics getReadTime() {
        return readTime;
    }

    public void setReadTime(PeriodStatistics readTime) {
        this.readTime = readTime;
    }

    public PeriodStatistics getReadTimePercent() {
        return readTimePercent;
    }

    public void setReadTimePercent(PeriodStatistics readTimePercent) {
        this.readTimePercent = readTimePercent;
    }

    public PeriodStatistics getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(PeriodStatistics writeTime) {
        this.writeTime = writeTime;
    }

    public PeriodStatistics getWriteTimePercent() {
        return writeTimePercent;
    }

    public void setWriteTimePercent(PeriodStatistics writeTimePercent) {
        this.writeTimePercent = writeTimePercent;
    }
}
