package com.didichuxing.datachannel.agentmanager.common.bean.domain.host;

import java.util.Date;
import java.util.List;

public class HostPaginationQueryConditionDO {

    /**
     * 主机名
     */
    private String hostName;

    /**
     * 主机 ip
     */
    private String ip;

    /**
     * Agent版本id
     */
    private List<Long> agentVersionIdList;

    /**
     * Agent 健康度 0:红 1：黄 2：绿色
     */
    private List<Integer> agentHealthLevelList;

    /**
     * 主机类型 0：主机 1：容器
     */
    private List<Integer> containerList;

    /**
     * 服务 id
     */
    private List<Long> serviceIdList;

    /**
     * 创建时间开始查询时间
     */
    private Date createTimeStart;

    /**
     * 创建时间结束查询时间
     */
    private Date createTimeEnd;

    /**
     * 从第几行开始
     */
    private Integer limitFrom;

    /**
     * 获取满足条件的 top limitSize 结果集行数
     */
    private Integer limitSize;

    /**
     * 排序依照的字段
     */
    private String sortColumn;

    /**
     * 是否升序排序
     */
    private Boolean asc;
    /**
     * 项目id
     */
    private Long projectId;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Long> getAgentVersionIdList() {
        return agentVersionIdList;
    }

    public void setAgentVersionIdList(List<Long> agentVersionIdList) {
        this.agentVersionIdList = agentVersionIdList;
    }

    public List<Integer> getAgentHealthLevelList() {
        return agentHealthLevelList;
    }

    public void setAgentHealthLevelList(List<Integer> agentHealthLevelList) {
        this.agentHealthLevelList = agentHealthLevelList;
    }

    public List<Integer> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<Integer> containerList) {
        this.containerList = containerList;
    }

    public List<Long> getServiceIdList() {
        return serviceIdList;
    }

    public void setServiceIdList(List<Long> serviceIdList) {
        this.serviceIdList = serviceIdList;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Integer getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom(Integer limitFrom) {
        this.limitFrom = limitFrom;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
