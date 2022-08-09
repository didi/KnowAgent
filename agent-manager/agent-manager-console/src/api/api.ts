import { IStringMap } from '../interface/common';

const prefixMap = {
  v1: '/api/v1',
} as IStringMap;

export const csrfTokenMethod = ['POST', 'PUT', 'DELETE'];

function getApi(path: string, prefix = 'v1') {
  return `${prefixMap[prefix] || ''}${path}`;
}

export const getPageQuery = (pageNo: number, pageSize = 20) => {
  return `?pageNo=${pageNo}&pageSize=${pageSize}`;
};

export const apiMap = {
  demo: getApi('/auth/login'),
  // repeat
  getServicesList: getApi('/normal/services/list'), // 查询"服务名-服务id"列表
  getHostMachineZones: getApi('/normal/host/machine-zones'), // 获取系统中已存在的全量machineZone
  getReceiversList: getApi('/normal/receivers/list'), // 查询系统全量接收端信息
  getReceiversTopics: getApi('/normal/receivers'), // 根据接收端id获取该接收端对应kafka集群的所有topic列表
  getTopicsBrokerServers: getApi('/normal/receivers/topics?brokerServers=10.255.0.49:9092'), // 获取接收端对应kafka集群的所有topic列表
  getHostList: getApi('/normal/host/list'), // 查询全量主机列表
  // agent
  getAgentHostList: getApi('/rd/host/paging'), // 查询主机&Agent列表
  getAgentVersionList: getApi('/rd/version/list'), // 系统全量Agent版本号列表
  getCollectTaskExists: getApi('/op/agent/collect-task-exists'), // 校验给定agentIdList对应各agent是否存在日志采集任务，true：存在 false：不存在
  createOpTasks: getApi('/op/operation-tasks'), // 创建Agent操作任务
  editOpHosts: getApi('/op/hosts'), // 修改主机
  getAgentDetail: getApi('/rd/agent'), // 根据id获取Agent对象信息
  editOpAgent: getApi('/op/agent'), // 修改Agent信息
  addOpHost: getApi('/op/hosts'), // 新增主机
  TestEdHost: getApi('/rd/host/connectivity'), // 测试主机名连通性
  deleteHosts: getApi('/op/hosts'), // 删除主机 0：删除成功 10000：参数错误 23000：待删除主机在系统不存在 23004：主机存在关联的容器导致主机删除失败 22001：Agent存在未采集完的日志
  getHostDetail: getApi('/rd/host'), // 根据host对象id获取Host&Agent对象信息
  getAgentMetrics: getApi('/rd/agent'), // 根据id获取Agent对象信息
  getAgentCollectList: getApi('/normal/agent/metrics/collect-tasks'), //获取Agent详情采集任务列表
  // collect
  getCollectMetrics: getApi('/normal/collect-task'), // 获取运行指标图表 根据给定LogCollectTask对象id，获取给定时间范围（startTime ~ endTime）内的LogCollectTask运行指标集
  getCollectTaskList: getApi('/normal/collect-task/paging'), // 查询日志采集任务列表
  addCollectTasks: getApi('/normal/collect-task'), // 新增日志采集任务
  editCollectTasks: getApi('/normal/collect-task'), // 修改日志采集任务
  switchCollectTask: getApi('/normal/collect-task/switch'), // 启动/停止日志采集任务
  deleteCollectTask: getApi('/normal/collect-task'), // 删除日志采集任务
  getCollectDetail: getApi('/normal/collect-task'), // 查看日志采集任务详情
  getHostCollectTask: getApi('/normal/host/collect-task'), // 查询给定日志采集任务关联的主机信息
  getHostCollectTaskDetail: getApi('/normal/host'), // 根据id获取Host对象信息
  getCollectPathList: getApi('/rd/agent/path'), // 获取日志路径下的所有文件列表
  getCollectTaskFiles: getApi('/normal/collect-task/files'), // 根据给定主文件路径与文件后缀匹配正则获取满足匹配对应规则的文件集
  getDataFormat: getApi('/normal/system-config/datetime-format'), // 获取系统配置的所有日期/时间格式
  getFileContent: getApi('/normal/collect-task/file-content'), // 读取文件内容 注：最多读取 100 行
  getSliceRule: getApi('/normal/collect-task/slice_rule'), // 根据给定日志样本与切片时间戳串获取对应切片规则配置
  getRuleTips: getApi(`/normal/collect-task/file-name-suffix-regular-expression-examples-tips`),
  // version
  getVersionList: getApi('/op/version/paging'),
  actionVersion: getApi('/op/version'),
  // Agent Operation Task
  getOperationTasksList: getApi('/op/operation-tasks/paging'),
  actionTasks: getApi('/op/operation-tasks'),
  // Service
  getDataSourceList: getApi('/normal/services/paging'),
  getServiceDetail: getApi('/normal/services'),
  actionServices: getApi('/op/services'),
  getServicesAgentId: getApi('/normal/services/rela-host-exists'),
  getServicesLogCollectTaskId: getApi('/normal/services/rela-logcollecttask-exists'),
  // Receiver
  getReceivingList: getApi('/normal/receivers/paging'),
  getTopics: getApi('/normal/receivers'),
  actionReceiver: getApi('/op/receivers'),
  getAgentId: getApi('/normal/receivers/rela-agent-exists'),
  getLogCollectTaskId: getApi('/normal/receivers/rela-logcollecttask-exists'),
  // Operate Record
  getRecordList: getApi('/op/record/list'),
  getRecordModules: getApi('/op/record/listModules'),
  //获取大盘数据接口
  getDashboard: getApi('/normal/dashboard'),
  getAgentHostId: getApi('/normal/host'),

  // 工作台 指标流和错误日志流集群校验接口
  getMetricsErrorlogs: getApi('/normal/receivers/global-agent-errorlogs-metrics-receiver-exists'),
  // k8s容器详情接口
  getSyncReault: getApi('/op/metadata/sync-result'),
};
