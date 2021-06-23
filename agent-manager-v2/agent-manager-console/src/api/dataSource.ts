import fetch from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { IDataSourceParams, ILogDataSource, IDataSource } from '../interface/DataSource';

// 服务名列表
export const getServices = () => {
  return fetch(apiMap.getServicesList);
}

// 数据源-应用列表
export const getDataSources = (params: IDataSourceParams) => {
  return fetch(apiMap.getDataSourceList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const getServiceDetail = (id: ILogDataSource) => {
  return fetch(apiMap.getServiceDetail + `/${id}`);
}

export const addService = (params: IDataSource) => {
  return fetch(apiMap.actionServices, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const modifyService = (params: IDataSource) => {
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

export const getServicesAgentId = (AgentId: number) => {
  return fetch(apiMap.getServicesAgentId + `/${AgentId}`);
}
export const getServicesLogCollectTaskId = (LogCollectTaskId: number) => {
  return fetch(apiMap.getServicesLogCollectTaskId + `/${LogCollectTaskId}`);
}

