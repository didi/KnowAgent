import fetch from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { collectApiMap } from "./collectApi";
import { ICollectTaskParams, ILogCollectTask, ISwitchCollectTask, ICollectPieParams, ICollectLineParams } from '../interface/collect';

export const getCollectTask = (params: ICollectTaskParams) => {
  return fetch(apiMap.getCollectTaskList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const addCollectTask = (params: ILogCollectTask) => {
  return fetch(apiMap.addCollectTasks, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const editCollectTask = (params: ILogCollectTask) => {
  return fetch(apiMap.editCollectTasks, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
}


export const switchCollectTask = (params: ISwitchCollectTask) => {
  return fetch(apiMap.switchCollectTask + `?logCollectTaskId=${params.logCollectTaskId}&status=${params.status}`);
}

export const deleteCollectTask = (logCollectTaskId: number) => {
  return fetch(apiMap.deleteCollectTask + `/${logCollectTaskId}`, {
    method: csrfTokenMethod[2],
  });
}

export const getCollectDetails = (logCollectTaskId: number) => {
  return fetch(apiMap.getCollectDetail + `/${logCollectTaskId}`);
}

export const getHostCollectTaskList = (logCollectTaskId: number) => {
  return fetch(apiMap.getHostCollectTask + `/${logCollectTaskId}`);
}

export const getHostCollectTaskDetails = (hostId: number) => {
  return fetch(apiMap.getHostCollectTaskDetail + `/${hostId}`);
}

export const getCollectPathList = (params: any) => {
  return fetch(apiMap.getCollectPathList + `?path=${params?.path}&suffixMatchRegular=${params?.suffixMatchRegular}&hostName=${params?.hostName}`);
}



/**
 * rdb
 */
export const getUserOpPermissionPoints = (params:any) => {
  const {user,nid, rdbPoints} = params
  return fetch(`/api/rdb/can-do-node-ops?username=${user}&nid=${nid}&ops=${rdbPoints.join(',')}`)
};

export const getCollectTaskHostNameList = (logcollectTaskId: number) => {
  return fetch(collectApiMap.getCollectTaskHostNameList + `/${logcollectTaskId}`);
}

//  饼图通用请求方法
export const getCollectPieData = (type: string, params: ICollectPieParams) => {
  return fetch(collectApiMap[type], {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

// 折线图通用请求方法
export const getCollectLineData = (type: string, params: ICollectLineParams) => {
  return fetch(collectApiMap[type], {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}