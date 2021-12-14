package com.didichuxing.datachannel.system.metrcis.bean;

/**
 * 磁盘信息
 * @author william.
 */
public class DiskInfo {

    /**
     * 磁盘路径
     */
    private String path;

    /**
     * 磁盘设备名
     */
    private String device;

    /**
     * 磁盘文件系统类型
     */
    private String fsType;

    /**
     * 磁盘总量（单位：byte）
     */
    private Long bytesTotal;

    /**
     * 磁盘余量大小（单位：byte）
     */
    private Long bytesFree;

    /**
     * 磁盘用量大小（单位：byte）
     */
    private Long bytesUsed;

    /**
     * 磁盘用量占比（单位：%）
     */
    private Double usedPercent;

    /**
     * 磁盘inode总数量
     */
    private Long inodesTotal;

    /**
     * 磁盘空闲inode数量
     */
    private Long inodesFree;

    /**
     * 磁盘已用inode数量
     */
    private Long inodesUsed;

    /**
     * 磁盘已用inode占比（单位：%）
     */
    private Long inodesUsedPercent;

    /**
     * 磁盘io信息
     */
    private DiskIOInfo diskIOInfo;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getFsType() {
        return fsType;
    }

    public void setFsType(String fsType) {
        this.fsType = fsType;
    }

    public Long getBytesTotal() {
        return bytesTotal;
    }

    public void setBytesTotal(Long bytesTotal) {
        this.bytesTotal = bytesTotal;
    }

    public Long getBytesFree() {
        return bytesFree;
    }

    public void setBytesFree(Long bytesFree) {
        this.bytesFree = bytesFree;
    }

    public Long getBytesUsed() {
        return bytesUsed;
    }

    public void setBytesUsed(Long bytesUsed) {
        this.bytesUsed = bytesUsed;
    }

    public Double getUsedPercent() {
        return usedPercent;
    }

    public void setUsedPercent(Double usedPercent) {
        this.usedPercent = usedPercent;
    }

    public Long getInodesTotal() {
        return inodesTotal;
    }

    public void setInodesTotal(Long inodesTotal) {
        this.inodesTotal = inodesTotal;
    }

    public Long getInodesFree() {
        return inodesFree;
    }

    public void setInodesFree(Long inodesFree) {
        this.inodesFree = inodesFree;
    }

    public Long getInodesUsed() {
        return inodesUsed;
    }

    public void setInodesUsed(Long inodesUsed) {
        this.inodesUsed = inodesUsed;
    }

    public Long getInodesUsedPercent() {
        return inodesUsedPercent;
    }

    public void setInodesUsedPercent(Long inodesUsedPercent) {
        this.inodesUsedPercent = inodesUsedPercent;
    }

    public DiskIOInfo getDiskIOInfo() {
        return diskIOInfo;
    }

    public void setDiskIOInfo(DiskIOInfo diskIOInfo) {
        this.diskIOInfo = diskIOInfo;
    }
}
