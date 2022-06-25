// import { IAgentParam } from '../api';

export interface IKeyValue {
  key: number;
  value: string;
}

export interface IOperationRecordQueryFormColumns {
  logAgentVersionTaskTypes: number[];
  serviceId: number[];
  logAgentVersionTaskHealthLevels: number[];
  logAgentVersionTaskIdOrName: string;
  locAgentVersionTaskCreateTime: moment.Moment[],
}

export interface IOperationRecordParams {
  // export interface IOperationRecordParams extends IAgentParam {
  // limitFrom?: number;
  // limitSize?: number;
  beginTime?: any;
  bizId?: any;
  content?: any;
  endTime?: any;
  id?: any;
  moduleId?: any;
  operateId?: any;
  operateTime?: any;
  operator?: any;
}

export interface IOperationRecordVo {
  pageNo: number; // 当前第几页
  pageSize: number; // 每页记录行数
  resultSet: IOperationRecord[];
  total: number;
  id?: number;
}

export interface IOperationRecord {
  servicename: string;
  serviceCreateTimeStart: number;
}

export interface ILogOperationRecord {
  id: number; // 主机名称
}

