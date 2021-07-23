import { IStringMap } from "../interface/common";

const prefixMap = {
  v1: '/api/v1',
  v2: '/api/v1/normal/collect-task/metrics',
} as IStringMap;

interface IAgentApi {
  [key: string]: string;
}

export const csrfTokenMethod = ['POST', 'PUT', 'DELETE'];

function getApi(path: string, prefix: string = 'v1') {
  return `${prefixMap[prefix] || ''}${path}`;
}

export const collectApiMap: IAgentApi = {
  //  参数{logcollectTaskId}
  getCollectTaskHostNameList: getApi('/normal/host/collect-task'), //查询采集任务的hostNameList下拉列表
  //  存在心跳主机数占比图表(饼图)
  Health: getApi('/health/heart-host-count', 'v2'),
  // 数据最大延迟
  HealthMaxDelay: getApi('/health/max-delay', 'v2'),
  // 最小采集业务时间
  HealthMinCollectBusineTime: getApi('/health/min-collectbusiness-time', 'v2'),
  //  限流时长（ms）(折线图)
  HealthLimitTime: getApi('/health/limit-time', 'v2'),
  //  异常截断条数（条）(折线图)
  HealthAbnormTrunca: getApi('/health/abnormal-truncation', 'v2'),
  //  采集路径是否存在 (折线图)
  isCollectPath: getApi('/health/iscollectpath', 'v2'),
  // 采集路径是否存在乱序 (折线图)
  isExistCollectPathChaos: getApi('/health/isexist-collectpath-chaos', 'v2'),
  //  是否存在日志切片错误 (折线图)
  islogChopFault: getApi('/health/islog-chop-fault', 'v2'),
  //  日志读取字节数
  logReadBytes: getApi('/performance/log-read-bytes', 'v2'),
  //  日志读取条数
  logReadBar: getApi('/performance/log-read-bar', 'v2'),
  // 日志读取耗时
  logReadConsuming: getApi('/performance/log-read-consuming', 'v2'),
  //  单logevent读取最大耗时 （ns）(折线图)
  logEventMaxConsuming: getApi('/performance/logevent-max-consuming', 'v2'),
  // 单logevent读取平均耗时 （ns）(折线图)
  logEventMeanConsuming: getApi('/performance/logevent-mean-consuming', 'v2'),
  //  日志发送字节数（MB）(折线图)
  logSendBytes: getApi('/performance/log-send-bytes', 'v2'),
  //  日志发送条数 （条）(折线图)
  logSnedBar: getApi('/performance/log-send-bar', 'v2'),
  //  日志发送耗时 （ms）(折线图)
  logSendConsuming: getApi('/performance/log-send-consuming', 'v2'),
  //  日志flush次数（次）(折线图)
  logFlushTime: getApi('/performance/logflush-times', 'v2'),
  //  日志flush最大耗时 （ms）(折线图)
  logFlushMaxConsuming: getApi('/performance/logflush-max-consuming', 'v2'),
  //  日志flush平均耗时 （ms）(折线图)
  logFlushMeanConsuming: getApi('/performance/logflush-mean-consuming', 'v2'),
  //  日志flush失败次数（次）(折线图)
  logFlushFailTimes: getApi('/performance/logflush-fail-times', 'v2'),
  //  数据过滤条数 （条）(折线图)
  DataFilterTimes: getApi('/performance/data-filter-times', 'v2'),
}
