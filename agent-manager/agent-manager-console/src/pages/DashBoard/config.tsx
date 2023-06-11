import { byteChange, numTrans } from '../../lib/utils';

export const appBarList = [
  {
    key: 'sendBytesLast1MinuteTop5Applications',
    title: '近1分钟发送日志量 top5 应用（单位：MB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendLogEventsLast1MinuteTop5Applications',
    title: '近1分钟发送日志条数 top5 应用（单位：条）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'sendBytesDayTop5Applications',
    title: '当日发送日志量 top5 应用（单位：GB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendLogEventsDayTop5Applications',
    title: '当日发送日志条数 top5 应用（单位：条）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'relateHostsTop5Applications',
    title: '当前关联主机数 top5 应用（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'relateAgentsTop5Applications',
    title: '当前关联 Agent数 top5 应用（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'relateLogCollectTaskTop5Applications',
    title: '当前关联采集任务数 top5 应用（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
];

export const agentBarList = [
  {
    key: 'ntpGapTop5Agents',
    title: '当前主机时间误差时长 top5 Agents（单位：秒）',
  },
  {
    key: 'cpuUsageTop5Agents',
    title: '当前进程 cpu 使用率 top5 Agents（单位：%）',
  },
  {
    key: 'memoryUsedTop5Agents',
    title: '当前进程内存使用量 top5 Agents（单位：MB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'bandWidthUsedTop5Agents',
    title: '当前系统带宽使用量 top5 Agents（单位：MB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'bandWidthUsageTop5Agents',
    title: '当前系统带宽使用率 top5 Agents（单位：%）',
  },
  {
    key: 'fullGcTimesDayTop5Agents',
    title: '当日 Agent进程full gc次数 top5 Agents（单位：次）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'fdUsedTop5Agents',
    title: '当前 Agent 进程 fd 使用量 top5 Agents（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'uplinkBytesTop5Agents',
    title: '当前 Agent 进程上行流量 top5 Agents（单位：MB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendBytesLast1MinuteTop5Agents',
    title: '近1分钟 Agent 进程发送日志量 top5 Agents（单位：MB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendLogEventsLast1MinuteTop5Agents',
    title: '近1分钟 Agent 进程发送日志条数 top5 Agents（单位：条）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'sendBytesDayTop5Agents',
    title: '当日 Agent 进程发送日志量 top5 Agents（单位：GB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendLogEventsDayTop5Agents',
    title: '当日 Agent 进程发送日志条数 top5 Agents（单位：条）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'runningLogCollectTasksTop5Agents',
    title: '当前具有运行状态的日志采集任务数 top5 Agents（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'runningLogCollectPathsTop5Agents',
    title: '当前具有运行状态的日志采集路径数 top5 Agents（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
];

export const taskBarList = [
  {
    key: 'logTimeDelayTop5LogCollectTasks',
    title: '当前采集的日志业务时间最大延时 top5 采集任务（单位：秒）',
  },
  {
    key: 'limitTimeTop5LogCollectTasks',
    title: '当日限流时长 top5 采集任务（单位：秒）',
  },
  {
    key: 'sendBytesLast1MinuteTop5LogCollectTasks',
    title: '近1分钟发送日志量 top5 采集任务（单位：MB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendLogEventsLast1MinuteTop5LogCollectTasks',
    title: '近1分钟发送日志条数 top5 采集任务（单位：条）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'sendBytesDayTop5LogCollectTasks',
    title: '当日发送日志量 top5 采集任务（单位：GB）',
    formatter: (value: number) => byteChange(value),
  },
  {
    key: 'sendLogEventsDayTop5LogCollectTasks',
    title: '当日发送日志条数 top5 采集任务（单位：条）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'relateHostsTop5LogCollectTasks',
    title: '当前关联主机数 top5 采集任务（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
  {
    key: 'relateAgentsTop5LogCollectTasks',
    title: '当前关联 Agent 数 top5 采集任务（单位：个）',
    formatter: (value: number) => numTrans(value),
  },
];
