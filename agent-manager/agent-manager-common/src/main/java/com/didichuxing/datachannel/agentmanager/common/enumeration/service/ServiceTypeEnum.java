package com.didichuxing.datachannel.agentmanager.common.enumeration.service;

import org.apache.commons.lang3.StringUtils;

public enum ServiceTypeEnum {
    租户(0, "tenant"),
    组织(1, "organization"),
    项目(2, "project"),
    模块(3, "module"),
    集群(4, "cluster"),
    资源(5, "resource");

    private Integer code;
    private String description;

    ServiceTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 校验给定节点名 nodeName 是否为项目节点对应子节点
     * @param nodeName 节点名
     * @return true：项目节点子节点 false：非项目节点子节点
     */
    public static boolean subOfProject(String nodeName) {
        if(
                StringUtils.isNotBlank(nodeName) &&
                        (
                                        nodeName.equals(模块.getDescription()) ||
                                        nodeName.equals(集群.getDescription()) ||
                                        nodeName.equals(资源.getDescription())
                                )
        ) {
            return true;
        } else {
            return false;
        }
    }

    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}
