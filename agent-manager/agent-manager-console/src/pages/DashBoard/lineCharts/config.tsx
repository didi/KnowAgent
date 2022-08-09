export const agentList = [
  {
    title: '主机时间误差时长 top5 Agents（单位：秒）',
    api: 'ntpGapTop5Agents',
    code: '',
  },
  {
    title: '进程 cpu 使用率 top5 Agents（单位：%）',
    api: 'cpuUsageTop5Agents',
    code: '',
  },
  {
    title: '进程内存使用量 top5 Agents（单位：MB）',
    api: 'memoryUsedTop5Agents',
    code: '',
  },
  {
    title: '系统带宽使用量 top5 Agents（单位：MB）',
    api: 'bandWidthUsedTop5Agents',
    code: '',
  },
  {
    title: '系统带宽使用率 top5 Agents（单位：%）',
    api: 'bandWidthUsageTop5Agents',
    code: '',
  },
  {
    title: '进程最近一分钟内 full gc 次数 top5 Agents（单位：次）',
    api: 'fullGcTimesDayTop5Agents',
    unit: '次',
    code: 25,
  },
  {
    title: '进程 fd 使用量 top5 Agents（单位：个）',
    api: 'fdUsedTop5Agents',
    unit: '个',
    code: 24,
  },
  {
    title: '进程上行流量 top5 Agents（单位：MB）',
    api: 'uplinkBytesTop5Agents',
    code: '',
  },
  {
    title: '进程近1分钟发送日志量 top5 Agents（单位：MB）',
    api: 'sendBytesLast1MinuteTop5Agents',
    code: '',
  },
  {
    title: '进程近1分钟发送日志条数 top5 Agents（单位：条）',
    api: 'sendLogEventsLast1MinuteTop5Agents',
    code: '',
  },
  {
    title: '进程当日发送日志量 top5 Agents（单位：MB）',
    api: 'sendBytesDayTop5Agents',
    code: 20,
  },
  {
    title: '进程当日发送日志条数 top5 Agents（单位：条）',
    api: 'sendLogEventsDayTop5Agents',
    unit: '条',
    code: 21,
  },
  {
    title: '具有运行状态的日志采集任务数 top5 Agents（单位：个）',
    api: 'runningLogCollectTasksTop5Agents',
    unit: '个',
    code: 26,
  },
  {
    title: '具有运行状态的日志采集路径数 top5 Agents（单位：个）',
    api: 'runningLogCollectTasksTop5Agents',
    unit: '个',
    code: 26,
  },
];

export const collectList = [
  {
    title: '采集的日志业务时间延时最大 top5 采集任务（单位：秒）',
    api: 'logTimeDelayTop5LogCollectTasks',
    unit: '',
    code: '',
  },
  {
    title: '限流时长 top5 采集任务（单位：秒）',
    api: 'limitTimeTop5LogCollectTasks',
    unit: '',
    code: '',
  },
  {
    title: '近1分钟发送日志量 top5 采集任务（单位：MB）',
    api: 'sendBytesLast1MinuteTop5LogCollectTasks',
    unit: '',
    code: '',
  },
  {
    title: '近1分钟发送日志条数 top5 采集任务（单位：条）',
    api: 'sendLogEventsLast1MinuteTop5LogCollectTasks',
    unit: '',
    code: '',
  },
  {
    title: '当日发送日志量 top5 采集任务（单位：MB）',
    api: 'sendBytesDayTop5LogCollectTasks',
    unit: '',
    code: '',
  },
  {
    title: '当日发送日志条数 top5 采集任务（单位：条）',
    api: 'sendLogEventsDayTop5LogCollectTasks',
    unit: '',
    code: '',
  },
];

export const businessList = [
  {
    title: '近1分钟发送日志量 top5 应用（单位：MB）',
    api: 'sendBytesLast1MinuteTop5Services',
    unit: '',
    code: '',
  },
  {
    title: '近1分钟发送日志条数 top5 应用（单位：条）',
    api: 'sendLogEventsLast1MinuteTop5Services',
    unit: '',
    code: '',
  },
  {
    title: '当日发送日志量 top5 应用（单位：MB）',
    api: 'sendBytesDayTop5Services',
    unit: '',
    code: '',
  },
  {
    title: '当日发送日志条数 top5 应用（单位：条）',
    api: 'sendLogEventsDayTop5Services',
    unit: '',
    code: '',
  },
];

export const chartListData = [
  {
    key: 'agent',
    title: 'agent视角',
    data: agentList,
  },
  {
    key: 'collection',
    title: '采集任务视角',
    data: collectList,
  },
  {
    key: 'business',
    title: '应用视角',
    data: businessList,
  },
];
