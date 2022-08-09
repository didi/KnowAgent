import fetch from '../lib/fetch';
import { apiMap, csrfTokenMethod } from './api';
import { agentApiMap } from './agentApi';
export const getAgent = (params: any) => {
  return fetch(apiMap.getAgentHostList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const getAgentVersion = () => {
  return fetch(apiMap.getAgentVersionList);
};

export const getServices = () => {
  return fetch(apiMap.getServicesList);
};

export const createOperationTasks = (params: any) => {
  return fetch(apiMap.createOpTasks, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const getHostMachineZone = () => {
  return fetch(apiMap.getHostMachineZones);
};

export const editOpHosts = (params: any) => {
  return fetch(apiMap.editOpHosts, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
};

export const getAgentDetails = (agentId: number) => {
  return fetch(apiMap.getAgentDetail + `/${agentId}`);
};

export const editOpAgent = (params: any) => {
  return fetch(apiMap.editOpAgent, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
};

export const getReceivers = () => {
  return fetch(apiMap.getReceiversList);
};

export const getReceiversTopic = (receiverId: number) => {
  return fetch(apiMap.getReceiversTopics + `/${receiverId}/topics`);
};

export const getTopics = () => {
  return fetch(apiMap.getTopicsBrokerServers);
};

export const addOpHosts = (params: any) => {
  return fetch(apiMap.addOpHost, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
    needDuration: true,
  });
};

export const testHost = (hostname: string) => {
  return fetch(apiMap.TestEdHost + `/${hostname}`);
};

export const getHosts = () => {
  return fetch(apiMap.getHostList);
};

export const deleteHost = (hostId: number, ignoreUncompleteCollect: number) => {
  return fetch(apiMap.deleteHosts + `/${hostId}?ignoreUncompleteCollect=${ignoreUncompleteCollect}`, {
    method: csrfTokenMethod[2],
    needCode: true,
  });
};

export const getHostDetails = (hostId: number) => {
  return fetch(apiMap.getHostDetail + `/${hostId}`);
};

export const getAgentMetrics = (agentId: number, startTime: number, endTime: number) => {
  return fetch(apiMap.getAgentMetrics + `/${agentId}/metrics/${startTime}/${endTime}`);
};

export const getCollectMetrics = (logCollectTaskId: number, startTime: number, endTime: number) => {
  return fetch(apiMap.getCollectMetrics + `/${logCollectTaskId}/metrics/${startTime}/${endTime}`);
};

export const getTaskExists = (agentIdListJsonString: string) => {
  return fetch(apiMap.getCollectTaskExists + `?agentIdListJsonString=${agentIdListJsonString}`);
};

export const getHostListbyServiceId = (appId: number) => {
  return fetch(`${apiMap.getServiceDetail}/${appId}`);
};

export const getCollectPathList = (params: any) => {
  return fetch(
    apiMap.getCollectPathList + `?path=${params?.path}&suffixMatchRegular=${params?.suffixMatchRegular}&hostName=${params?.hostName}`
  );
};

// Agent管理采集日志列表
export const getAgentCollectList = (hostname: string) => {
  return fetch(`${apiMap.getAgentCollectList}` + `?hostname=${hostname}`);
};

// getMetricsErrorlogs  工作台 指标流和错误日志流集群校验接口
export const getMetricsErrorlogs = () => {
  return fetch(`${apiMap.getMetricsErrorlogs}`);
};

//  饼图通用请求方法
export const getAgentPieData = (type: string, params: any) => {
  return fetch(agentApiMap[type], {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

// 折线图通用请求方法
export const getAgentLineData = (type: string, params: any) => {
  return fetch(agentApiMap[type], {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

// 大盘请求暂时放置在agent

export const getDashboard = (startTime: number, endTime: number, dashboardMetricsCodes: number[]) => {
  // return fetch(`${apiMap.getDashboard}/${startTime}/${endTime}`);
  return fetch(apiMap.getDashboard, {
    method: csrfTokenMethod[0],
    body: JSON.stringify({
      startTime,
      endTime,
      dashboardMetricsCodes,
    }),
  });
};

export const getAgentHostId = (hostName: string) => {
  return fetch(`${apiMap.getAgentHostId}?hostname=${hostName}`);
};
