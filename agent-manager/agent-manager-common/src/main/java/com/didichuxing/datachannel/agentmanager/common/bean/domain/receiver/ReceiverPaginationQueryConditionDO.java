package com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ReceiverPaginationQueryConditionDO {

    /**
     * kafka 集群名
     */
    private String kafkaClusterName;
    /**
     * kafka创建时间开始查询时间
     */
    private Date createTimeStart;
    /**
     * kafka创建时间结束查询时间
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
     * 是否升序
     */
    private Boolean asc;

    /**
     * 查询关键字
     */
    private String queryTerm;

    /**
     * kafka集群broker配置
     */
    private String kafkaClusterBrokerConfiguration;

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }

    public ReceiverPaginationQueryConditionDO() {
    }

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public void setKafkaClusterName(String kafkaClusterName) {
        this.kafkaClusterName = kafkaClusterName;
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

    public String getKafkaClusterBrokerConfiguration() {
        return kafkaClusterBrokerConfiguration;
    }

    public void setKafkaClusterBrokerConfiguration(String kafkaClusterBrokerConfiguration) {
        this.kafkaClusterBrokerConfiguration = kafkaClusterBrokerConfiguration;
    }
}
