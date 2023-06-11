package com.didichuxing.datachannel.agentmanager.common.bean.domain.service;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 服务信息
 */
@Data
public class ServiceDO extends BaseDO {

    /**
     * 服务信息唯一标识
     */
    private Long id;
    /**
     * 服务名
     */
    private String servicename;
    /**
     * 服务关联的主机id集
     */
    private List<Long> hostIdList;
    /**
     * 服务关联的项目id集
     */
    private List<Long> projectIdList;
    /**
     * 外部系统服务节点 id 如：夜莺系统服务节点 id
     */
    private Long extenalServiceId;

    /*********************** 用于夜莺项目id同步使用 ***********************/

    /**
     * 服务节点对应父节点id
     * 保留字段
     */
    private Long pid;
    /**
     * 服务节点类型：
     *  租户：tenant
     *  组织：organization
     *  项目：project *
     *  模块：module *
     *  集群：cluster *
     *  资源：resource * -- 带 * 表示具备项目节点的节点
     * 保留字段
     */
    private String cate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public List<Long> getHostIdList() {
        return hostIdList;
    }

    public void setHostIdList(List<Long> hostIdList) {
        this.hostIdList = hostIdList;
    }

    public Long getExtenalServiceId() {
        return extenalServiceId;
    }

    public void setExtenalServiceId(Long extenalServiceId) {
        this.extenalServiceId = extenalServiceId;
    }

    public List<Long> getProjectIdList() {
        return projectIdList;
    }

    public void setProjectIdList(List<Long> projectIdList) {
        this.projectIdList = projectIdList;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }
}
