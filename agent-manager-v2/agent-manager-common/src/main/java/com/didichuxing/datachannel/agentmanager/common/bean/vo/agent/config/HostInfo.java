package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostInfo {

    @ApiModelProperty(value = "主机名")
    private String hostName;

    @ApiModelProperty(value = "主机类型：0：主机 1：容器")
    private Integer hostType;

    @ApiModelProperty(value = "主机扩展字段值")
    private String hostExtendField;

    public String getHostName() {
        return hostName;
    }

    public Integer getHostType() {
        return hostType;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setHostType(Integer hostType) {
        this.hostType = hostType;
    }

    public String getHostExtendField() {
        return hostExtendField;
    }

    public void setHostExtendField(String hostExtendField) {
        this.hostExtendField = hostExtendField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof HostInfo)) return false;

        HostInfo hostInfo = (HostInfo) o;

        return new EqualsBuilder()
                .append(hostName, hostInfo.hostName)
                .append(hostType, hostInfo.hostType)
                .append(hostExtendField, hostInfo.hostExtendField)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostName)
                .append(hostType)
                .append(hostExtendField)
                .toHashCode();
    }

    public HostInfo(String hostName, Integer hostType, String hostExtendField) {
        this.hostName = hostName;
        this.hostType = hostType;
        this.hostExtendField = hostExtendField;
    }

    public HostInfo() {

    }

}
