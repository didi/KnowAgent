
// import { IAgentParam } from '../api';
export interface IKeyValue {
  key: number;
  value: string;
}

export interface IOperationTaskQueryFormColumns {
  logAgentVersionTaskTypes: number[];
  serviceId: number[];
  logAgentVersionTaskHealthLevels: number[];
  logAgentVersionTaskIdOrName: string;
  locAgentVersionTaskCreateTime: moment.Moment[],
}

// export interface IOperationTaskParams extends IAgentParam {
//   limitFrom?: number;
//   limitSize?: number;
//   agentOperationTaskId?: any;
//   agentOperationTaskName?: string;
//   agentOperationTaskTypeList?: any;
//   agentOperationTaskStatusList?: any;
//   agentOperationTaskCreateTimeStart?: any;
//   agentOperationTaskCreateTimeEnd?: any;
// }

export interface IOperationTaskVo {
  pageNo: number; // 当前第几页
  pageSize: number; // 每页记录行数
  resultSet: IOperationTask[];
  total: number;
}

export interface IOperationTask {
  agentVersion: '',
  agentPackageName: '',
  agentVersionCreateTimeStart: '',
}

export interface IReceiverVO {
  createTime: number; // 接收端创建时间
  id: number; // 接收端对象id
  kafkaClusterBrokerConfiguration: string; // kafka集群broker配置
  kafkaClusterName: string; // kafka集群名
  kafkaClusterProducerInitConfiguration: string; // kafka集群对应生产端初始化配置
}

export interface ILogOperationTask {
  agentOperationTaskId?: any; // 运维任务id
  hostName?: string; // 主机名称
}

export interface ISwitchOperationTask {
  logAgentVersionTaskId: number;
  status: number;
}

