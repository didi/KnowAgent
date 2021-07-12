import fetch from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { IOperationRecordParams, ILogOperationRecord, IOperationRecord } from '../interface/OperationRecord';

export const getOperationRecordExtraParams = (params: IOperationRecordParams) => {
  return `&bizId=${params.bizId}&content=${params.content}&beginTime=${params.beginTime}&endTime=${params.endTime}
  &id=${params.id}&moduleId=${params.moduleId}&operateId=${params.operateId}&operateTime=${params.operateTime}
  &operator=${params.operator}`;
}

// 模块列表
export const getRecordModules = () => {
  return fetch(apiMap.getRecordModules);
}

// 操作记录列表
export const getRecordList = (params: IOperationRecordParams) => {
  return fetch(apiMap.getRecordList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const getOperationRecords = (id: ILogOperationRecord) => {
  return fetch(apiMap.getServiceDetail + `/${id}`);
}

export const addService = (params: IOperationRecord) => {
  return fetch(apiMap.actionServices, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const modifyService = (params: IOperationRecord) => {
  return fetch(apiMap.actionServices, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
}

export const deleteService = (agentVersionId: number) => {
  return fetch(apiMap.actionServices + `/${agentVersionId}`, {
    method: csrfTokenMethod[2],
  });
}



