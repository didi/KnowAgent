
// import { IAgentParam } from '../api';
export interface IKeyValue {
  key: number;
  value: string;
}

export interface IAgentVersionQueryFormColumns {
  logAgentVersionTaskTypes: number[];
  serviceId: number[];
  logAgentVersionTaskHealthLevels: number[];
  logAgentVersionTaskIdOrName: string;
  locAgentVersionTaskCreateTime: moment.Moment[],
}


// export interface IAgentVersionParams extends IAgentParam {
//   limitFrom?: number;
//   limitSize?: number;
//   agentVersionList?: any,
//   agentPackageName?: string,
//   agentVersionCreateTimeStart?: any,
// }

export interface IAgentVersionVo {
  pageNo: number; // 当前第几页
  pageSize: number; // 每页记录行数
  resultSet: IAgentVersion[];
  total: number;
}

export interface IVersion {
  agentVersionId: number;
  agentVersion: string;
}

export interface IAgentVersion {
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

export interface ILogAgentVersion {
  agentPackageName?: any;
  fileMd5?: any;
  uploadFile?: any;
  agentVersion: string;
  agentVersionDescription: string;
}

export interface ILogAgentVersionM {
  agentVersionDescription: string;
  id: number;
}

export interface ISwitchAgentVersion {
  logAgentVersionTaskId: number;
  status: number;
}

