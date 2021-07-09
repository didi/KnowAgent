package com.didichuxing.datachannel.agentmanager.common.bean.domain.globalconfig;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 全局配置信息
 */
@Data
public class GlobalConfigDO extends BaseDO {

    /**
     * 全局配置信息唯一标识
     */
    private Long id;
    /**
     * 键
     */
    private String key;
    /**
     * 值
     */
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}