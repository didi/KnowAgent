package com.didichuxing.datachannel.agentmanager.remote.service.impl.n9e;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.remote.service.RemoteServiceManageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class N9eRemoteServiceManageServiceImpl implements RemoteServiceManageService {

    /**
     * 远程请求服务节点信息对应请求 url
     */
    @Value("${metadata.sync.request.service.url}")
    private String requestUrl;

    @Override
    public List<ServiceDO> getServicesFromRemote() {
        if(StringUtils.isBlank(requestUrl)) {//未配置requestUrl，表示未开启服务节点同步功能
            throw new ServiceException(
                    "未配置请求夜莺侧服务节点信息requestUrl，请在配置文件配置metadata.sync.request.service.url",
                    ErrorCodeEnum.SERVICE_NODE_REMOTE_REQUEST_URL_NOT_CONFIG.getCode()
            );
        }
        Map<String, String> header = new HashMap<>();
        header.put("X-Srv-Token", "rdb-builtin-token");
        String response = HttpUtils.get(requestUrl, null, header);
        N9eServiceResponseResult n9eServiceResponseResult = JSON.parseObject(response, N9eServiceResponseResult.class);
        if (StringUtils.isBlank(n9eServiceResponseResult.getErr())) {
            List<N9eServiceNodeInfo> n9eServiceNodeInfos = n9eServiceResponseResult.getDat();
            if (CollectionUtils.isNotEmpty(n9eServiceNodeInfos)) {
                List<ServiceDO> serviceDOList = new ArrayList<>(n9eServiceNodeInfos.size());
                for (N9eServiceNodeInfo n9eServiceNodeInfo : n9eServiceNodeInfos) {
                    ServiceDO serviceDO = new ServiceDO();
                    serviceDO.setServicename(n9eServiceNodeInfo.getPath());
                    serviceDO.setExtenalServiceId(n9eServiceNodeInfo.getId());
                    serviceDO.setPid(n9eServiceNodeInfo.getPid());
                    serviceDO.setCate(n9eServiceNodeInfo.getCate());
                    serviceDOList.add(serviceDO);
                }
                return serviceDOList;
            } else {
                return new ArrayList<>();
            }
        } else {//夜莺侧请求失败，抛异常
            throw new ServiceException(
                    String.format("请求夜莺侧服务节点信息失败, requestUrl={%s}, err={%s}", requestUrl, n9eServiceResponseResult.getErr()),
                    ErrorCodeEnum.SERVICE_NODE_REMOTE_REQUEST_FAILED.getCode()
            );
        }
    }

}

class N9eServiceResponseResult {
    private String err;
    private List<N9eServiceNodeInfo> dat;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public List<N9eServiceNodeInfo> getDat() {
        return dat;
    }

    public void setDat(List<N9eServiceNodeInfo> dat) {
        this.dat = dat;
    }
}

class N9eServiceNodeInfo {
    private Long id;
    private Long pid;
    private String ident;
    private String name;
    private String note;
    private String path;
    private Long leaf;
    private String cate;
    private String icon_color;
    private String icon_char;
    private Long proxy;
    private String creator;
    private String last_updated;
    private String admins;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getLeaf() {
        return leaf;
    }

    public void setLeaf(Long leaf) {
        this.leaf = leaf;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getIcon_color() {
        return icon_color;
    }

    public void setIcon_color(String icon_color) {
        this.icon_color = icon_color;
    }

    public String getIcon_char() {
        return icon_char;
    }

    public void setIcon_char(String icon_char) {
        this.icon_char = icon_char;
    }

    public Long getProxy() {
        return proxy;
    }

    public void setProxy(Long proxy) {
        this.proxy = proxy;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getAdmins() {
        return admins;
    }

    public void setAdmins(String admins) {
        this.admins = admins;
    }
}