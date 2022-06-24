import { IStringMap } from '../interface/common';

const prefixMap = {
  v1: '/api/v1/normal/agent',
} as IStringMap;

function getApi(path: string, prefix: string = 'v1') {
  return `${prefixMap[prefix] || ''}${path}`;
}

interface IAgentApi {
  [key: string]: string;
}

export const agentApiMap: IAgentApi = {
  // CPU使用率 (%)(折线图)
  cpuUsage: getApi('/metrics/cpu-usage'),
  // 内存使用量 (MB)(折线图)
  memoryUsage: getApi('/metrics/memory-usage'),
  //  fd使用量 (条)(折线图)
  fdUsage: getApi('/metrics/fd-usage'),
  //  full gc次数（次/min）(折线图)
  fullGcTimes: getApi('/metrics/full-gc-times'),
  // 出口发送流量 （MB/min）(折线图)
  exitSendTraffic: getApi('/metrics/exit-send-traffic'),
  //  出口发送条数 （MB/min）(折线图)
  exitSendBar: getApi('/metrics/exit-send-bar'),
  //  入口采集流量 （MB/min）(折线图)
  inletCollectTraffic: getApi('/metrics/inlet-collect-traffic'),
  //  入口采集条数（条/min）(折线图)
  inletCoLLectBar: getApi('/metrics/inlet-collect-bar'),
  //  错误日志输出条数（条/min）(折线图)
  logFaultOutPutBar: getApi('/metrics/logfault-output-bar'),
  //  日志采集任务数占比 (饼图)
  logCollectTask: getApi('/metrics/log-collect-task'),
  //  日志采集路径数占比 (饼图)
  logCollectPath: getApi('/metrics/log-collect-path'),
};
