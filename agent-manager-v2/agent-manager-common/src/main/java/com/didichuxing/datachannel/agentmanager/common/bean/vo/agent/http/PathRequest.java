package com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.http;

/**
 * 向agent发起path请求结构体
 */
public class PathRequest {

    /**
     * 路径：可为目录型路径或文件型路径
     */
    private String path;
    /**
     * 文件名后缀匹配正则
     */
    private String suffixRegular;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSuffixRegular() {
        return suffixRegular;
    }

    public void setSuffixRegular(String suffixRegular) {
        this.suffixRegular = suffixRegular;
    }

    public PathRequest() {
    }

    public PathRequest(String path, String suffixRegular) {
        this.path = path;
        this.suffixRegular = suffixRegular;
    }

}
