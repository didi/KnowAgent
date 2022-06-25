
// import { IAgentParam } from '../api';

export interface IKeyValue {
  key: number;
  value: string;
}

export interface IReceivingTerminalQueryFormColumns {
  logAgentVersionTaskTypes: number[];
  serviceId: number[];
  logAgentVersionTaskHealthLevels: number[];
  logAgentVersionTaskIdOrName: string;
  locAgentVersionTaskCreateTime: moment.Moment[],
}

// export interface IReceivingTerminalParams extends IAgentParam {
//   limitFrom?: number;
//   limitSize?: number;
//   kafkaClusterName?: string;
//   receiverCreateTimeStart?: any;
//   receiverCreateTimeEnd?: any;
//   kafkaClusterBrokerConfiguration?: any;
//   kafkaClusterProducerInitConfiguration?: any;
// }

export interface IReceivingTerminalVo {
  pageNo: number; // 当前第几页
  pageSize: number; // 每页记录行数
  resultSet: IReceivingTerminal[];
  total: number;
  id?: number;
  agentErrorLogsTopic?: any;
  agentMetricsTopic?: any;
}

export interface IReceivingTerminal {
  id?: number;
  kafkaClusterName: string;
  kafkaClusterBrokerConfiguration: string;
  kafkaClusterProducerInitConfiguration: string;
}

export interface ILogReceivingTerminal {
  id: number; // 主机名称
}


