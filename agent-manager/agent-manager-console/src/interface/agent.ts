
// import { IAgentParam } from '../api';
// import { EChartOption } from 'echarts';

// export interface IAgentHostParams extends IAgentParam {
//   agentHealthLevelList: number[]; // 健康度 0:红 1：黄 2：绿色
//   agentVersionIdList: any[]; // Agent版本id
//   containerList: any[]; // 主机类型 0：主机 1：容器
//   hostCreateTimeEnd: number; // 主机创建时间结束检索时间
//   hostCreateTimeStart: number; // 主机创建时间起始检索时间
//   serviceIdList: number[]; // 服务Id
//   hostName: string; // 主机名
//   ip: string; // ip
// }

export interface IAgentQueryFormColumns {
  agentHealthLevelList: number[];
  agentVersionIdList: number[];
  containerList: number[];
  hostCreateTime: moment.Moment[],
  hostNameOrIp: string;
  serviceIdList: number[];
}

export interface IAgentHostVo {
  pageNo: number; // 当前第几页
  pageSize: number; // 每页记录行数
  resultSet: IAgentHostSet[];
  total: number; // 满足条件总记录数
}

export interface IAgentHostSet {
  key?: number;
  agentHealthLevel: number; // 健康度 0:红 1：黄 2：绿色
  agentVersion: string; // Agent版本
  container: number; // 主机类型 0：主机 1：容器
  hostName: string; // 主机名
  ip: string; // ip
  agentId: number; // Agent对象id
  department: string; // 主机所属部门
  hostCreateTime: number; // 主机创建时间 格式：unix 13 位时间戳
  hostId: number; //主机对象id
  machineZone: string; // 主机所属机器单元 所属机房
  parentHostName: string // 针对容器场景，表示容器对应宿主机名
  serviceList: IService[];
}

export interface IService {
  id: number; // 服务对象id
  servicename: string; // 服务名
}

export interface IAgentVersion {
  key?: number;
  agentVersion: string; // Agent版本号
  agentVersionId: number; // Agent版本 id
}

export interface IOperationTasksParams {
  agentIds: number[]; // 待升级/卸载的Agent对象id集 注：Agent卸载/升级case必填
  agentVersionId: number; // 待安装/升级的Agent对应的Agent版本对象id 注：Agent安装/升级case必填
  checkAgentCompleteCollect: number; // 卸载Agent时是否需要检查Agent待采集日志是否全部采集完，如设置为检查，当卸载Agent时，Agent仍未采集完待采集日志，将会导致Agent卸载失败 0：不检查 1：检查 注：Agent卸载case必填
  hostIds: number[];	// 待安装Agent的主机id集 注：Agent安装case必填
  taskType: number; // 任务类型 0：安装 1：卸载 2：升级 必填
}

export interface IEditOpHostsParams {
  department: string; // 主机所属部门 必填
  id: number; // Host对象id 注：新增主机接口不可填，仅在更新主机接口必填
  machineZone: string; // 主机所属机器单元 必填
}

export interface IOpAgent {
  metricsProducerConfiguration: string; // 指标流接收Topic生产端属性
  errorLogsProducerConfiguration: string; // 错误日志接收Topic生产端属性
  advancedConfigurationJsonString: string; // 采集端高级配置项集，为json形式字符串
  byteLimitThreshold: number; // Agent限流流量阈值 单位：字节
  collectType: number; // Agent采集方式 0：采集宿主机日志 1：采集宿主机挂载的所有容器日志 2：采集宿主机日志 & 宿主机挂载的所有容器日志
  cpuLimitThreshold: number; // Agent限流cpu阈值 单位：核
  errorLogsSendReceiverId: number; // Agent错误日志信息发往的接收端id
  errorLogsSendTopic: string; // Agent错误日志信息发往的topic名
  healthLevel: number; // Agent健康度 0：红 1：黄 2：绿
  hostName: string; // Agent宿主机名
  id: number; // AgentPO id
  ip: string; // Agent宿主机ip
  metricsSendReceiverId: number; // Agent指标信息发往的接收端id
  metricsSendTopic: string; // Agent指标信息发往的topic名
  version: string; // Agent版本号
}

export interface IReceivers {
  createTime: number; // 接收端创建时间
  id: number; // 接收端对象id
  kafkaClusterBrokerConfiguration: string; // kafka集群broker配置
  kafkaClusterName: string; // kafka集群名
  kafkaClusterProducerInitConfiguration: string; // kafka集群对应生产端初始化配置
}

export interface IAddOpHostsParams {
  container: number; // 主机类型 0：主机 1：容器 必填
  department: string; // 主机所属部门 必填
  hostName: string; // 主机名 注：新增主机接口必填，更新主机接口不可填
  id: number; //  Host对象id 注：新增主机接口不可填，仅在更新主机接口必填
  ip: string; // 主机ip 必填
  machineZone: string; // 主机所属机器单元 必填
  parentHostName: string; // 容器场景，表示容器对应宿主机名 当主机类型为容器时，必填
}

export interface IHosts {
  container: number; //  主机类型 0：主机 1：容器
  department: string; // 主机所属部门
  hostName: string; // 主机名
  id: number; // 主机id
  ip: string; // 主机ip
  machineZone: string; // 主机所属机器单元
  parentHostName: string; // 主机类型为容器时，表示容器对应宿主机名
}

export interface IRdAgentMetrics {
  metricPanelGroupName: string; // 指标面板大组名 图表总类型
  metricPanelList: IMetricPanels[];
  groupHide: boolean;
}

export interface IMetricPanels {
  metricList: any;
  panelName: string; // 指标面板名 每个里面包含多个图表
  agentMetricList: IAgentMetrics[];
  selfHide: boolean;
  api: string;
  eachHost: boolean;
  isPie: boolean;
  title: string;
  unit: string;
}

export interface IAgentMetrics {
  metricName: string; // 指标名 图表里面包含多条折线
  metricPointList: IMetricPoints[];
}

export interface IMetricPoints { // 每条折线的数据
  timestamp: number; // Agent指标的某个数据点对应时间戳
  value: number; // Agent指标的某个数据点对应值
}

// export interface IMetricOptions {
//   title: string;
//   selfHide: boolean;
//   metricOptions: EChartOption;
//   metricPanelGroupName: string;
// }

export interface IAgentPieParams {
  agentId: string | number,
}

export interface IAgentLineParams {
  agentId: string | number,
  startTime: number;
  endTime: number;
}