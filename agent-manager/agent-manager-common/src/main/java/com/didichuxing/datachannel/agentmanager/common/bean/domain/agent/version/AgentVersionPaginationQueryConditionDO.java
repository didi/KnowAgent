package com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version;

import java.util.Date;
import java.util.List;

public class AgentVersionPaginationQueryConditionDO {

    /**
     * Agent安装包版本号
     */
    private List<String> agentVersionList;
    /**
     * Agent安装包名
     */
    private String agentPackageName;
    /**
     * Agent版本创建时间开始检索时间
     */
    private Date agentVersionCreateTimeStart;
    /**
     * Agent版本创建时间结束检索时间
     */
    private Date agentVersionCreateTimeEnd;
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
     * 是否升序
     */
    private Boolean asc;

    /**
     * 检索关键字
     */
    private String queryTerm;

    /**
     * 版本描述信息
     */
    private String agentVersionDescription;

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }

    public AgentVersionPaginationQueryConditionDO() {
    }

    public List<String> getAgentVersionList() {
        return agentVersionList;
    }

    public void setAgentVersionList(List<String> agentVersionList) {
        this.agentVersionList = agentVersionList;
    }

    public String getAgentPackageName() {
        return agentPackageName;
    }

    public void setAgentPackageName(String agentPackageName) {
        this.agentPackageName = agentPackageName;
    }

    public Date getAgentVersionCreateTimeStart() {
        return agentVersionCreateTimeStart;
    }

    public void setAgentVersionCreateTimeStart(Date agentVersionCreateTimeStart) {
        this.agentVersionCreateTimeStart = agentVersionCreateTimeStart;
    }

    public Date getAgentVersionCreateTimeEnd() {
        return agentVersionCreateTimeEnd;
    }

    public void setAgentVersionCreateTimeEnd(Date agentVersionCreateTimeEnd) {
        this.agentVersionCreateTimeEnd = agentVersionCreateTimeEnd;
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

    public String getAgentVersionDescription() {
        return agentVersionDescription;
    }

    public void setAgentVersionDescription(String agentVersionDescription) {
        this.agentVersionDescription = agentVersionDescription;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
