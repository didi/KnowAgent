package com.didichuxing.datachannel.agentmanager.remote.host.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.remote.host.RemoteHostManageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RemoteHostManageServiceImpl implements RemoteHostManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHostManageServiceImpl.class);

    /**
     * 根据服务节点id远程请求该服务节点下所有主机、pod信息对应请求 url
     */
    @Value("${metadata.sync.request.host.url}")
    private String requestUrl;

    /**
     * 根据pod远程请求该pod关联所有容器信息对应请求 url
     */
    @Value("${metadata.sync.request.containers.url}")
    private String containerListRequestUrl;

    @Override
    public List<HostDO> getHostsByServiceIdFromRemote(Long serviceId) {
        if(StringUtils.isBlank(requestUrl)) {//未配置requestUrl，表示未开启服务节点同步功能
            throw new ServiceException(
                    "未配置请求夜莺侧主机信息requestUrl，请在配置文件配置metadata.sync.request.host.url",
                    ErrorCodeEnum.HOST_REMOTE_REQUEST_URL_NOT_CONFIG.getCode()
            );
        }
        String url = requestUrl + "/" + serviceId + "/resources";
        Map<String, String> header = new HashMap<>();
        header.put("X-Srv-Token", "rdb-builtin-token");
        String response = HttpUtils.get(url, null, header);
        N9eHostResponseResult n9eHostResponseResult = JSON.parseObject(response, N9eHostResponseResult.class);
        if (null != n9eHostResponseResult && StringUtils.isBlank(n9eHostResponseResult.getErr())) {
            N9eHostResponseDat n9eHostResponseDat = n9eHostResponseResult.getDat();
            if (CollectionUtils.isNotEmpty(n9eHostResponseDat.getList())) {
                List<HostDO> hostDOList = new ArrayList();
                for (N9eHostResponseHostInfo n9eHostResponseHostInfo : n9eHostResponseDat.getList()) {
                    //set container & parentHostName
                    String cate = n9eHostResponseHostInfo.getCate();
                    //set parentHostName & extendField if container
                    if(cate.equals("container")) {//容器
                        try {
                            Map<String, String> lablesMap = toMap(n9eHostResponseHostInfo.getLabels());
                            String region = lablesMap.get("region");
                            String podName = n9eHostResponseHostInfo.getIdent();//podName
                            String resType = lablesMap.get("res_type");
                            String resName = lablesMap.get("res_name");
                            //根据 podName、region、resName、resType 获取 pod 下容器集
                            List<HostDO> containerList = getContainerList(podName, region, resName, resType);
                            hostDOList.addAll(containerList);
                        } catch (ServiceException ex) {//lables解析出错
                            LOGGER.warn(
                                    String.format("解析远程主机={%s}对应lables字段信息出错，原因为：%s，将不同步该主机，跳过", JSON.toJSONString(n9eHostResponseHostInfo), ex.getMessage()),
                                    ex.getMessage()
                            );
                            continue;
                        }
                    } else if(cate.equals("virtual") || cate.equals("physical")) {//主机
                        HostDO hostDO = new HostDO();
                        hostDO.setParentHostName(StringUtils.EMPTY);
                        hostDO.setExtendField(StringUtils.EMPTY);
                        hostDO.setHostName(n9eHostResponseHostInfo.getName());
                        hostDO.setIp(n9eHostResponseHostInfo.getIdent());
                        hostDO.setContainer(HostTypeEnum.HOST.getCode());
                        hostDO.setMachineZone(StringUtils.EMPTY);
                        hostDO.setDepartment(StringUtils.EMPTY);
                        hostDOList.add(hostDO);
                    } else {//未知类型
                        if(LOGGER.isDebugEnabled()) {
                            LOGGER.debug(String.format("同步到非主机类型 & 容器类型的主机={%s}，将不同步该主机，跳过", JSON.toJSONString(n9eHostResponseHostInfo)));
                        }
                        continue;
                    }
                }
                return hostDOList;
            } else {
                return new ArrayList<>();
            }
        } else {//夜莺侧请求失败，抛异常
            throw new ServiceException(
                    String.format("请求夜莺侧主机信息失败, requestUrl={%s}, err={%s}", requestUrl, null != n9eHostResponseResult ? n9eHostResponseResult.getErr() : ""),
                    ErrorCodeEnum.SERVICE_NODE_REMOTE_REQUEST_FAILED.getCode()
            );
        }
    }

    /**
     * 根据给定 podName、region、resName、resType 获取该容器组下所有容器集
     * @param podName 容器组名
     * @param region region名
     * @param resName 资源名
     * @param resType 资源类型
     * @return 返回根据给定 podName、region、resName、resType 获取到的该容器组下所有容器集
     */
    private List<HostDO> getContainerList(String podName, String region, String resName, String resType) {
        Map<String, String> params = new HashMap<>();
        params.put("podName", podName);
        params.put("region", region);
        Map<String, String> headers = new HashMap<>();
        headers.put("x-srv-token", "ccp-builtin-token");
        String response = HttpUtils.get(containerListRequestUrl, params, headers);
        N9eContainersResult n9eContainersResult = JSON.parseObject(response, N9eContainersResult.class);
        if(null != n9eContainersResult && StringUtils.isBlank(n9eContainersResult.getErr())) {
            List<HostDO> containerList = new ArrayList<>();
            N9eContainers n9eContainers = n9eContainersResult.getDat();
            for (String containerName : n9eContainers.getContainerNames()) {
                HostDO container = new HostDO();
                String parentHostName = n9eContainers.getNodeName();//容器宿主机名
                String containerIp = n9eContainers.getPodIP();//容器ip
                //setter host properties
                String containerId = parentHostName + CommonConstant.COLON + podName + CommonConstant.COLON + containerName;//容器唯一标示 parentHostName + podName + containerName
                container.setHostName(containerId);
                Map<String, String> extendFieldMap = new HashMap<>();
                extendFieldMap.put("podName", podName);
                extendFieldMap.put("region", region);
                extendFieldMap.put("containerName", containerName);
                extendFieldMap.put("resType", resType);
                extendFieldMap.put("resName", resName);
                extendFieldMap.put("parentHostName", parentHostName);
                container.setExtendField(JSON.toJSONString(extendFieldMap));
                container.setParentHostName(parentHostName);
                container.setContainer(HostTypeEnum.CONTAINER.getCode());
                container.setIp(containerIp);
                container.setMachineZone(StringUtils.EMPTY);
                container.setDepartment(StringUtils.EMPTY);
                containerList.add(container);
            }
            return containerList;
        } else {
            throw new ServiceException(
                    String.format("请求夜莺侧主机信息失败, requestUrl={%s}, err={%s}", containerListRequestUrl, null != n9eContainersResult ? n9eContainersResult.getErr() : ""),
                    ErrorCodeEnum.HOST_REMOTE_RESPONSE_PARSE_FAILED.getCode()
            );
        }
    }

    /**
     * 根据给定入参构建对应map形式json串
     * @param resType 资源类型
     * @param resName 资源名
     * @param ident 唯一标示信息
     * @return 返回根据给定入参构建的对应map形式json串
     */
    private String buildExtendFieldValue(String resType, String resName, String ident) {
        Map<String, String> map = new HashMap<>();
        map.put("resType", resType);
        map.put("resName", resName);
        map.put("ident", ident);
        return JSON.toJSONString(map);
    }

    /**
     * 将给定labels解析为Map
     * @param labels labels格式须为逗号分隔的k-v对，k-v对采用=分隔
     * @return 返回将给定labels解析为的 map 对象
     */
    private Map<String, String> toMap(String labels) {
        Map<String, String> labelsMap = new HashMap<>();
        String[] kvStringArray = labels.split(",");
        if (ArrayUtils.isNotEmpty(kvStringArray)) {
            for (String kvString : kvStringArray) {
                String[] kvPair = kvString.split("=");
                if (ArrayUtils.isNotEmpty(kvPair) && kvPair.length == 2) {
                    labelsMap.put(kvPair[0], kvPair[1]);
                } else {
                    throw new ServiceException(
                            String.format("解析labels={%s}出错，labels格式须为逗号分隔的k-v对，k-v对采用=分隔", labels),
                            ErrorCodeEnum.HOST_REMOTE_RESPONSE_PARSE_FAILED.getCode()
                    );
                }
            }
        }
        return labelsMap;
    }

}

class N9eHostResponseResult {
    private N9eHostResponseDat dat;
    private String err;

    public N9eHostResponseDat getDat() {
        return dat;
    }

    public void setDat(N9eHostResponseDat dat) {
        this.dat = dat;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}

class N9eHostResponseDat {
    private List<N9eHostResponseHostInfo> list;
    private Long total;

    public List<N9eHostResponseHostInfo> getList() {
        return list;
    }

    public void setList(List<N9eHostResponseHostInfo> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}

class N9eHostResponseHostInfo {
    private Long id;
    private String uuid;
    private String ident;
    private String name;
    private String labels;
    private String note;
    private String extend;
    private String cate;
    private String tenant;
    private String last_updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}

class N9eContainersResult {
    private String err;
    private N9eContainers dat;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public N9eContainers getDat() {
        return dat;
    }

    public void setDat(N9eContainers dat) {
        this.dat = dat;
    }
}

class N9eContainers {
    private List<String> containerNames;
    private String nodeName;
    private String podIP;

    public List<String> getContainerNames() {
        return containerNames;
    }

    public void setContainerNames(List<String> containerNames) {
        this.containerNames = containerNames;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPodIP() {
        return podIP;
    }

    public void setPodIP(String podIP) {
        this.podIP = podIP;
    }
}