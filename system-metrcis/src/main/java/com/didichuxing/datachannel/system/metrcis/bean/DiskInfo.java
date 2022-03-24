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
    private Double bytesUsedPercent;

    /**
     * 磁盘inode总数量
     */
    private Integer inodesTotal;

    /**
     * 磁盘空闲inode数量
     */
    private Integer inodesFree;

    /**
     * 磁盘已用inode数量
     */
    private Integer inodesUsed;

    /**
     * 磁盘已用inode占比（单位：%）
     */
    private Double inodesUsedPercent;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Double getBytesUsedPercent() {
        return bytesUsedPercent;
    }

    public void setBytesUsedPercent(Double bytesUsedPercent) {
        this.bytesUsedPercent = bytesUsedPercent;
    }

    public Integer getInodesTotal() {
        return inodesTotal;
    }

    public void setInodesTotal(Integer inodesTotal) {
        this.inodesTotal = inodesTotal;
    }

    public Integer getInodesFree() {
        return inodesFree;
    }

    public void setInodesFree(Integer inodesFree) {
        this.inodesFree = inodesFree;
    }

    public Integer getInodesUsed() {
        return inodesUsed;
    }

    public void setInodesUsed(Integer inodesUsed) {
        this.inodesUsed = inodesUsed;
    }

    public Double getInodesUsedPercent() {
        return inodesUsedPercent;
    }

    public void setInodesUsedPercent(Double inodesUsedPercent) {
        this.inodesUsedPercent = inodesUsedPercent;
    }

}
