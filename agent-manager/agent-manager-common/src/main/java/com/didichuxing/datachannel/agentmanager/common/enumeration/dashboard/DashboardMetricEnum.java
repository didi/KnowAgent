package com.didichuxing.datachannel.agentmanager.common.enumeration.dashboard;

/**
 * dashboard 指标 枚举 定义
 */
public enum DashboardMetricEnum {

    LOG_COLLECT_TASK_NUM(0, "系统现有日志采集任务总数"),
    NON_RELATE_ANY_HOST_LOG_COLLECT_TASK_NUM(1, "系统现有未关联任何主机的日志采集任务数"),
    LOG_COLLECT_PATH_NUM(2, "系统现有日志采集路径总数"),
    SERVICE_NUM(3, "系统现有应用总数"),
    HOST_NUM(4, "系统现有主机总数"),
    CONTAINER_NUM(5, "系统现有容器总数"),
    AGENT_NUM(6, "系统现有agent总数"),
    NON_RELATE_ANY_LOG_COLLECT_TASK_AGENT_NUM(7, "系统现有未关联任何日志采集任务的 agent 数"),
    CURRENT_COLLECT_BYTES(8, "系统当前采集流量总量/s"),
    CURRENT_COLLECT_LOG_EVENTS(9, "系统当前采集总条数/s"),
    COLLECT_BYTES_DAY(10, "当日采集流量"),
    COLLECT_LOG_EVENTS_DAY(11, "当日采集条数"),
    RED_LOG_COLLECT_TASK_NAME_ID_PAIR_LIST(12, "系统当前处于red状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id"),
    YELLOW_LOG_COLLECT_TASK_NAME_ID_PAIR_LIST(13, "系统当前处于yellow状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id"),
    RED_AGENT_HOST_NAME_ID_PAIR_LIST(14, "系统当前处于red状态agent列表集 key：hostName value：agent id"),
    YELLOW_AGENT_HOST_NAME_ID_PAIR_LIST(15, "系统当前处于yellow状态agent列表集 key：hostName value：agent id"),
    LOG_COLLECT_TASK_LIST_COLLECT_BYTES_TOP5(16, "近1分钟日志采集量最大top5日志采集任务"),
    LOG_COLLECT_TASK_LIST_COLLECT_COUNT_TOP5(17, "近1分钟日志采集条数最大top5日志采集任务"),
    LOG_COLLECT_TASK_LIST_RELATE_HOSTS_TOP5(18, "关联主机数最多top5日志采集任务集"),
    LOG_COLLECT_TASK_LIST_RELATE_AGENTS_TOP5(19, "关联agent数最多top5日志采集任务集"),
    AGENT_LIST_COLLECT_BYTES_TOP5(20, "近1分钟日志采集量最大top5 agent"),
    AGENT_LIST_COLLECT_COUNT_TOP5(21, "近1分钟日志采集条数最大top5 agent"),
    AGENT_LIST_CPU_USAGE_TOP5(22, "cpu占用核数最多top5agent集"),
    AGENT_LIST_MEMORY_USAGE_TOP5(23, "内存使用量最多top5agent集"),
    AGENT_LIST_FD_USED_TOP5(24, "fd 使用量最多top5agent集"),
    AGENT_LIST_FULL_GC_COUNT_TOP5(25, "full gc 最多top5agent集"),
    AGENT_LIST_RELATE_LOG_COLLECT_TASKS_TOP5(26, "关联日志采集任务数最多top5 agent")
    ;

    DashboardMetricEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static DashboardMetricEnum valueOf(Integer code) {
        for(DashboardMetricEnum dashboardMetricEnum : values()) {
            if(code.equals(dashboardMetricEnum.getCode())) {
                return dashboardMetricEnum;
            }
        }
        return null;
    }

}
