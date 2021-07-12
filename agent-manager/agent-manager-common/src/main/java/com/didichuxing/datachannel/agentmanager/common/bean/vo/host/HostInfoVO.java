package com.didichuxing.datachannel.agentmanager.common.bean.vo.host;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机信息
 */
public class HostInfoVO {

    /**
     * 主机名
     */
    private String hostName;
    /**
     * 主机类型：
     *  0：主机
     *  1：容器
     */
    private Integer hostType;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HostInfoVO that = (HostInfoVO) o;

        return new EqualsBuilder()
                .append(hostName, that.hostName)
                .append(hostType, that.hostType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostName)
                .append(hostType)
                .toHashCode();
    }

    public HostInfoVO(String hostName, Integer hostType) {
        this.hostName = hostName;
        this.hostType = hostType;
    }

    public HostInfoVO() {

    }

}
