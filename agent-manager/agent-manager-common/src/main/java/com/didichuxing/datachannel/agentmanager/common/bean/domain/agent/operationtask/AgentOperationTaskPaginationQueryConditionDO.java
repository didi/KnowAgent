package com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.operationtask;

import java.util.Date;
import java.util.List;

public class AgentOperationTaskPaginationQueryConditionDO {

    /**
     * 采集端操作任务唯一标识
     */
    private Long id;
    /**
     * agent 操作任务名
     */
    private String taskName;
    /**
     * 任务类型 0：部署 1：升级
     */
    private List<Integer> taskTypeList;
    /**
     * 任务状态 -1：未知 0：新建 20：就绪 21：等待 30：运行中 31：杀死中 40：暂停 99：未完成 100：完成 101：成功 102：失败 103：取消
     *             104：忽略 105：超时 106：杀死失败 TODO：状态待 收敛
     */
    private List<Integer> taskStatusList;
    /**
     * 任务开始时间对应查询开始时间
     */
    private Date taskStartTimeStart;
    /**
     * 任务开始时间对应结束查询时间
     */
    private Date taskStartTimeEnd;
    /**
     * 从第几行开始
     */
    private Integer limitFrom;
    /**
     * 获取满足条件的 top limitSize 结果集行数
     */
    private Integer limitSize;

    /**
     * 排序依照的字段
     */
    private String sortColumn;

    /**
     * 是否升序
     */
    private Boolean asc;

    public AgentOperationTaskPaginationQueryConditionDO() {
    }

    public AgentOperationTaskPaginationQueryConditionDO(Long id, String taskName, List<Integer> taskTypeList, List<Integer> taskStatusList, Date taskStartTimeStart, Date taskStartTimeEnd, Integer limitFrom, Integer limitSize) {
        this.id = id;
        this.taskName = taskName;
        this.taskTypeList = taskTypeList;
        this.taskStatusList = taskStatusList;
        this.taskStartTimeStart = taskStartTimeStart;
        this.taskStartTimeEnd = taskStartTimeEnd;
        this.limitFrom = limitFrom;
        this.limitSize = limitSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<Integer> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<Integer> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Integer> getTaskStatusList() {
        return taskStatusList;
    }

    public void setTaskStatusList(List<Integer> taskStatusList) {
        this.taskStatusList = taskStatusList;
    }

    public Date getTaskStartTimeStart() {
        return taskStartTimeStart;
    }

    public void setTaskStartTimeStart(Date taskStartTimeStart) {
        this.taskStartTimeStart = taskStartTimeStart;
    }

    public Date getTaskStartTimeEnd() {
        return taskStartTimeEnd;
    }

    public void setTaskStartTimeEnd(Date taskStartTimeEnd) {
        this.taskStartTimeEnd = taskStartTimeEnd;
    }

    public Integer getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom(Integer limitFrom) {
        this.limitFrom = limitFrom;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
