package com.didichuxing.datachannel.agent.common.configs.v1.collector;

import java.util.Map;

/**
 * @description: 业务逻辑配置
 * @author: huangjw
 * @Date: 18/6/29 15:09
 */
public class BusinessConfig {

    /**
     * 所属应用
     */
    private String              belongToCluster = "default";

    /**
     * 对应主机的部门信息
     */
    private String              queryFrom       = "didi";

    /**
     * 传输标签
     */
    private String              transName;

    /**
     * 叶子节点
     */
    private String              odinLeaf;

    /**
     * 原始的appName
     */
    private String              originalAppName;

    /**
     * 是否是服务化,0表示非Service,1表示是Service,
     */
    private Integer             isService;

    /**
     * 地区信息
     */
    private String              location;

    private Map<String, Object> others;

    public Map<String, Object> getOthers() {
        return others;
    }

    public void setOthers(Map<String, Object> others) {
        this.others = others;
    }

    public String getBelongToCluster() {
        return belongToCluster;
    }

    public void setBelongToCluster(String belongToCluster) {
        this.belongToCluster = belongToCluster;
    }

    public String getQueryFrom() {
        return queryFrom;
    }

    public void setQueryFrom(String queryFrom) {
        this.queryFrom = queryFrom;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getOdinLeaf() {
        return odinLeaf;
    }

    public void setOdinLeaf(String odinLeaf) {
        this.odinLeaf = odinLeaf;
    }

    public String getOriginalAppName() {
        return originalAppName;
    }

    public void setOriginalAppName(String originalAppName) {
        this.originalAppName = originalAppName;
    }

    public Integer getIsService() {
        return isService;
    }

    public void setIsService(Integer isService) {
        this.isService = isService;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BusinessConfig{" + "belongToCluster='" + belongToCluster + '\'' + ", queryFrom='"
               + queryFrom + '\'' + ", transName='" + transName + '\'' + ", odinLeaf='" + odinLeaf
               + '\'' + ", originalAppName='" + originalAppName + '\'' + ", isService=" + isService
               + ", location='" + location + '\'' + ", others=" + others + '}';
    }
}
