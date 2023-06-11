package com.didichuxing.datachannel.agentmanager.common.bean.dto.receiver;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverPaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "kafka集群名")
    private String kafkaClusterName;

    @ApiModelProperty(value = "接收端创建时间起始检索时间", notes="")
    private Long receiverCreateTimeStart;

    @ApiModelProperty(value = "接收端创建时间结束检索时间", notes="")
    private Long receiverCreateTimeEnd;

    @ApiModelProperty(value = "排序依照的字段，可选kafka_cluster_name create_time", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序", notes="")
    private Boolean asc;

    @ApiModelProperty(value = "kafka集群broker配置")
    private String kafkaClusterBrokerConfiguration;

    public void setKafkaClusterName(String kafkaClusterName) {
        this.kafkaClusterName = kafkaClusterName;
    }

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public void setReceiverCreateTimeStart(Long receiverCreateTimeStart) {
        this.receiverCreateTimeStart = receiverCreateTimeStart;
    }

    public void setReceiverCreateTimeEnd(Long receiverCreateTimeEnd) {
        this.receiverCreateTimeEnd = receiverCreateTimeEnd;
    }

    public Long getReceiverCreateTimeEnd() {
        return receiverCreateTimeEnd;
    }

    public Long getReceiverCreateTimeStart() {
        return receiverCreateTimeStart;
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
