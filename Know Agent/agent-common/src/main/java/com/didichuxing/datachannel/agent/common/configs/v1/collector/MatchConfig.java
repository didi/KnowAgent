package com.didichuxing.datachannel.agent.common.configs.v1.collector;

import com.didichuxing.datachannel.agent.common.api.FileMatchType;
import com.didichuxing.datachannel.agent.common.api.FileType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.api.StandardLogType;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 文件匹配规则
 * @author: huangjw
 * @Date: 18/7/18 14:08
 */
public class MatchConfig implements Cloneable {
    /**
     * 匹配类型，0表示按照文件后缀，1表示正则
     */
    private Integer      matchType      = FileMatchType.Length.getStatus();

    /**
     * 路径类型，0表示文件，1表示目录
     */
    private Integer      fileType       = FileType.File.getStatus();

    /**
     * 文件后缀，若matchType=0则表示长度，1表示正则
     */
    private String       fileSuffix;

    /**
     * 文件过滤黑白名单
     */
    private Integer      fileFilterType = LogConfigConstants.FILE_FILTER_TYPE_BLACK;

    /**
     * 黑白名单内容，匹配内容均为正则
     */
    private List<String> fileFilterRules;

    /**
     * 业务类型
     */
    private Integer      businessType   = StandardLogType.Normal.getType();

    /**
     * 滚动类型
     */
    private List<String> rollingSamples;

    public List<String> getRollingSamples() {
        return rollingSamples;
    }

    public void setRollingSamples(List<String> rollingSamples) {
        this.rollingSamples = rollingSamples;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public Integer getFileFilterType() {
        return fileFilterType;
    }

    public void setFileFilterType(Integer fileFilterType) {
        this.fileFilterType = fileFilterType;
    }

    public List<String> getFileFilterRules() {
        return fileFilterRules;
    }

    public void setFileFilterRules(List<String> fileFilterRules) {
        this.fileFilterRules = fileFilterRules;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    @Override
    public MatchConfig clone() {
        MatchConfig matchConfig = null;
        try {
            matchConfig = (MatchConfig) super.clone();
            if (this.fileFilterRules != null) {
                List<String> newFileFilterRules = new ArrayList<>();
                matchConfig.setFileFilterRules(newFileFilterRules);
                for (String item : this.fileFilterRules){
                    newFileFilterRules.add(item);
                }
            }

            if (this.rollingSamples != null) {
                List<String> rollingSamples = new ArrayList<>();
                matchConfig.setRollingSamples(rollingSamples);
                for (String item : this.rollingSamples){
                    rollingSamples.add(item);
                }
            }

        } catch (CloneNotSupportedException e) {

        }
        return matchConfig;
    }

    @Override
    public String toString() {
        return "MatchConfig{" + "matchType=" + matchType + ", fileType=" + fileType
               + ", fileSuffix='" + fileSuffix + '\'' + ", fileFilterType=" + fileFilterType
               + ", fileFilterRules=" + fileFilterRules + ", businessType=" + businessType
               + ", rollingSamples=" + rollingSamples + '}';
    }
}
