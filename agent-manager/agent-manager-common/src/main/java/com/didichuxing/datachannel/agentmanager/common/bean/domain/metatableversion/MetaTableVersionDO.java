package com.didichuxing.datachannel.agentmanager.common.bean.domain.metatableversion;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 表版本号信息
 */
@Data
public class MetaTableVersionDO extends BaseDO {

    /**
     * 表版本号信息唯一标识
     */
    private Long id;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表对应版本号，该变每一次变更，其版本号+1
     */
    private Long tableVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getTableVersion() {
        return tableVersion;
    }

    public void setTableVersion(Long tableVersion) {
        this.tableVersion = tableVersion;
    }

}
