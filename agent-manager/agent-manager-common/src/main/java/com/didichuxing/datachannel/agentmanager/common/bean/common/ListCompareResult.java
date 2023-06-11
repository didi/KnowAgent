package com.didichuxing.datachannel.agentmanager.common.bean.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * 集合对比结果类
 */
public class ListCompareResult<T> {

    /**
     * 存储待添加T对象集
     */
    private List<T> createList = new ArrayList<>();
    /**
     * 存储待修改T对象集
     */
    private List<T> modifyList = new ArrayList<>();
    /**
     * 存储待删除T对象对应id属性值集
     */
    private List<T> removeList = new ArrayList<>();

    public List<T> getCreateList() {
        return createList;
    }

    public List<T> getModifyList() {
        return modifyList;
    }

    public void setCreateList(List<T> createList) {
        this.createList = createList;
    }

    public void setModifyList(List<T> modifyList) {
        this.modifyList = modifyList;
    }

    public void setRemoveList(List<T> removeList) {
        this.removeList = removeList;
    }

    public List<T> getRemoveList() {
        return removeList;
    }

}
