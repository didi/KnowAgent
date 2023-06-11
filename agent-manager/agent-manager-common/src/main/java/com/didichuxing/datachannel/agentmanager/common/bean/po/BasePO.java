package com.didichuxing.datachannel.agentmanager.common.bean.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author d06679
 * @date 2019/3/13
 */
@Data
public class BasePO implements Serializable {

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifyTime;
    /**
     * 操作人
     */
    private String operator;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
