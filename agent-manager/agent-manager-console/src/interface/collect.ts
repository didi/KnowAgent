// import { IAgentParam } from '../api';
import { IService } from './agent';

export interface IKeyValue {
  key: number;
  value: string;
}

export interface ICollectQueryFormColumns {
  logCollectTaskTypeList: number[];
  serviceIdList: number[];
  logCollectTaskHealthLevelList: number[];
  logCollectTaskId: number;
  logCollectTaskName: string;
  locCollectTaskCreateTime: moment.Moment[];
}

// export interface ICollectTaskParams extends IAgentParam {
//   limitFrom?: number;
//   limitSize?: number;
//   pageNo?: number;
//   pageSize?: number;
//   serviceIdList: number[]; // 服务id
//   logCollectTaskTypeList: number[]; // 采集任务类型 0：常规流式采集 1：按指定时间范围采集
//   logCollectTaskHealthLevelList: number[]; // 日志采集任务健康度  0:红 1：黄 2：绿色
//   logCollectTaskId: number; // 日志采集任务id
//   logCollectTaskName: string; // 日志采集任务名
//   locCollectTaskCreateTimeEnd: number; // 日志采集任务创建时间结束检索时间
//   locCollectTaskCreateTimeStart: number; // 日志采集任务创建时间起始检索时间
// }

export interface ICollectTaskVo {
  pageNo: number; // 当前第几页
  pageSize: number; // 每页记录行数
  resultSet: ICollectTask[];
  total: number;
}

export interface ICollectTask {
  key?: number;
  logCollectTaskCreateTime: number; // 日志采集任务创建时间 格式：unix 13 位时间戳
  logCollectTaskFinishTime: number; // 日志采集任务结束时间 格式：unix 13 位时间戳 注：仅针对logCollectTaskType为1的日志采集任务
  logCollectTaskHealthLevel: number; // 日志采集任务健康度  0:红 1：黄 2：绿色
  logCollectTaskId: number; // 日志采集任务id
  logCollectTaskName: string; // 日志采集任务名
  logCollectTaskStatus: number; // 日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 “按指定时间范围采集” 类型）
  logCollectTaskType: number; // 采集任务类型 0：常规流式采集 1：按指定时间范围采集
  receiverTopic: string; // 接收端topic
  receiverVO: IReceiverVO;
  serviceList: IService[]; // 采集服务集
}

export interface IReceiverVO {
  createTime: number; // 接收端创建时间
  id: number; // 接收端对象id
  kafkaClusterBrokerConfiguration: string; // kafka集群broker配置
  kafkaClusterName: string; // kafka集群名
  kafkaClusterProducerInitConfiguration: string; // kafka集群对应生产端初始化配置
}

export interface ILogCollectTaskBase {
  logCollectTaskName: string; // 日志采集任务名
  logCollectTaskType: number; // 采集模式 ————  0：常规流式采集 1：按指定时间范围采集
  oldDataFilterType: number; // 历史数据过滤 ———— 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值
  collectStartBusinessTime: number; // 日志采集任务对应采集开始业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 & oldDataFilterTyp = 2 时，该值必填
  collectEndBusinessTime: number; // 日志采集任务对应采集结束业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填
  logCollectTaskRemark: string; // 日志采集任务备注
  directoryLogCollectPathList: IDirectoryLogCollectPath[]; // 目录类型采集路径集
  fileLogCollectPathList: IFileLogCollectPath[]; // 文件类型路径采集配置
  logCollectTaskExecuteTimeoutMs: number; // 采集完成时间限制 ———— 日志采集任务执行超时时间，注意：该字段仅在日志采集任务类型为类型"按指定时间范围采集"时才存在值
  limitPriority: number; // 采集任务限流保障优先级 0：高 1：中 2：低
  sendTopic: string; // Topic ———— 采集任务采集的日志需要发往的topic名
  advancedConfigurationJsonString: string; // 高级配置信息 ———— 采集任务高级配置项集，为json形式字符串
  id: number; // 日志采集任务id 添加时不填，更新时必填
  opencollectDelay?: boolean; // 是否开启采集延时监控
  collectDelayThresholdMs?: number; //采集延迟监控 ———— 该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~当前时间间隔
}

export interface ILogCollectTask extends ILogCollectTaskBase {
  serviceIdList: number[]; // 采集应用 ———— 采集服务集 step1
  hostFilterRuleDTO: IHostFilterRule; // 主机范围 ———— 主机过滤规则 step1
  logContentFilterLogicDTO: ILogContentFilterRule; // 日志过滤内容规则 step2
  kafkaClusterId: number[]; // kafka集群 ———— 采集任务采集的日志需要发往的对应Kafka集群信息id step4
}

export interface ILogCollectTaskDetail extends ILogCollectTaskBase {
  logSliceRule: any;
  logContentSliceRule: any;
  fileNameSuffixMatchRule: any;
  fileNameSuffixMatchRuleVO: any;
  kafkaProducerConfiguration: string; // kafka生产端属性
  logSliceRuleVO: any;
  collectDelayThresholdMs: number;
  maxBytesPerLogEvent: any;
  services: IService[]; // 采集应用 ———— 采集服务集 step1
  hostFilterRuleVO: IHostFilterRule; // 主机范围 ———— 主机过滤规则
  logContentFilterRuleVO: ILogContentFilterRule; // 日志过滤内容规则 step2
  receiver: IReceiverVO; // kafka集群 ———— 采集任务采集的日志需要发往的对应Kafka集群信息id step4
  logCollectTaskCreator: string; // 日志采集任务创建人
  logCollectTaskFinishTime: number; // 日志采集任务执行完成时间 注：仅日志采集任务为时间范围采集类型时
  logCollectTaskHealthLevel: number; // 日志采集任务健康度
  logCollectTaskStatus: number; // 日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 “按指定时间范围采集” 类型）
}

export interface ILogCollectPath {
  charset: string; // 编码格式 ———— 待采集文件字符集
  path: string; // 路径 ———— 待采集路径
  maxBytesPerLogEvent: number; // 单条日志大小上限 单个日志切片最大大小 单位：字节 注：单个日志切片大小超过该值后，采集端将以该值进行截断采集
  logSliceRuleDTO: ILogSliceRule; // 日志切片规则 新增编辑添加时用
  logSliceRuleVO: ILogSliceRule; // 日志切片规则 获取详情时候用
  fdOffsetExpirationTimeMs: number; // 客户端offset（采集位点记录）清理  ———— 待采集文件 offset 有效期 单位：ms 注：待采集文件自最后一次写入时间 ~ 当前时间间隔 > fdOffset时，采集端将删除其维护的该文件对应 offset 信息，如此时，该文件仍存在于待采集目录下，将被重新采集
  id: number; // 采集路径id 添加时不填，更新时必填
  logCollectTaskId: number; // 采集路径关联的日志采集任务id
}

export interface IDirectoryLogCollectPath extends ILogCollectPath {
  directoryCollectDepth: number; // 采集深度 ———— 目录采集深度
  filterRuleChain: IKeyValue[]; // 采集文件黑/白名单 ———— 存储有序的文件筛选规则集。pair.key：表示黑/白名单类型，0：白名单，1：黑名单；pair.value：表示过滤规则表达式
}

export interface IFileLogCollectPath extends ILogCollectPath {
  collectDelayThresholdMs: number; // 采集延迟监控 ———— 该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~ 当前时间间隔
  fileNameSuffixMatchRuleDTO: IFileNameSuffixMatchRule; // 采集文件名后缀匹配规则 新增编辑添加时用
  fileNameSuffixMatchRuleVO: IFileNameSuffixMatchRule; // 采集文件名后缀匹配规则 获取详情时候用
}

export interface IHostFilterRule {
  needHostFilterRule: number; // 0否-全部 1是-部分 ———— 是否需要主机过滤规则 0：否 1：是
  filterSQL: string; // sql ———— 主机筛选命中sql，白名单
  hostNames: string[]; // 主机名 ———— 主机名命中列表，白名单
}

export interface ILogContentFilterRule {
  logContentFilterExpression: string; // 日志内容过滤表达式，needLogContentFilter为1时必填
  logContentFilterType: number; // 日志内容过滤类型 0：包含 1：不包含，needLogContentFilter为1时必填
  needLogContentFilter: number; // 是否需要对日志内容进行过滤 0：否 1：是
}

export interface ILogSliceRule {
  sliceType: number; // 日志内容切片类型 0：时间戳切片 1：正则匹配切片
  sliceTimestampPrefixStringIndex: number; // 切片时间戳前缀字符串左起第几个，index计数从1开始
  sliceTimestampPrefixString: string; //切片时间戳前缀字符串
  sliceTimestampFormat: string; //切片 时间戳格式
  sliceRegular: string; // 正则匹配 ———— 切片正则
}

export interface IFileNameSuffixMatchRule {
  suffixSeparationCharacter: string; // 文件名后缀分隔字符
  suffixMatchType: number; // 文件名后缀匹配类型 0：长度 1：正则
  suffixLength: number; // 文件名后缀长度 suffixMatchType为0时必填
  suffixMatchRegular: string; //文件名后缀长度 suffixMatchType为1时必填
}

export interface ISwitchCollectTask {
  logCollectTaskId: number;
  status: number;
}

export interface IHostDetail {
  container: number; // 主机类型 0：主机 1：容器
  department: string; // 主机所属部门
  id: number; // 主机id
  hostName: string; // 主机名
  ip: string; // 主机ip
  machineZone: string; // 主机所属机器单元
  parentHostName: string; // 针对容器场景，表示容器对应宿主机名
}

export interface ICollectPieParams {
  taskId: string | number;
  logCollectPathId: string | number;
}

export interface ICollectLineParams {
  taskId: string | number;
  logCollectPathId: string | number;
  hostName: string;
  startTime: number;
  endTime: number;
  eachHost: boolean;
}
