import fetch from '../lib/fetch';
import { apiMap, csrfTokenMethod } from './api';
export const getCollectTask = (params: any) => {
  return fetch(apiMap.getCollectTaskList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const addCollectTask = (params: any) => {
  return fetch(apiMap.addCollectTasks, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const editCollectTask = (params: any) => {
  return fetch(apiMap.editCollectTasks, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
};

export const switchCollectTask = (params: any) => {
  return fetch(apiMap.switchCollectTask + `?logCollectTaskId=${params.logCollectTaskId}&status=${params.status}`);
};

export const deleteCollectTask = (logCollectTaskId: number) => {
  return fetch(apiMap.deleteCollectTask + `/${logCollectTaskId}`, {
    method: csrfTokenMethod[2],
  });
};

export const getCollectDetails = (logCollectTaskId: number) => {
  return fetch(apiMap.getCollectDetail + `/${logCollectTaskId}`);
};

export const getHostCollectTaskList = (logCollectTaskId: number) => {
  return fetch(apiMap.getHostCollectTask + `/${logCollectTaskId}`);
};

export const getHostCollectTaskDetails = (hostId: number) => {
  return fetch(apiMap.getHostCollectTaskDetail + `/${hostId}`);
};

export const getCollectPathList = (params: any) => {
  // return fetch(
  //   apiMap.getCollectPathList + `?path=${params?.path}&suffixMatchRegular=${params?.suffixMatchRegular}&hostName=${params?.hostName}`
  // );
  return fetch(apiMap.getCollectPathList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const getSlicePreview = (params: any) => {
  return fetch('/api/v1/normal/collect-task/result-slice', {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const getCollectTaskFiles = (params: any) => {
  return fetch(apiMap.getCollectTaskFiles, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
};

export const getDataFormat = () => {
  return fetch(apiMap.getDataFormat);
};

export const getFileContent = (params: any) => {
  return fetch(apiMap.getFileContent + `?hostName=${params.hostName}&path=${params.path}`);
};

export const getSliceRule = (params: any) => {
  return fetch(apiMap.getSliceRule, {
    method: csrfTokenMethod[0],
    body: params,
  });
};

export const getRuleTips = () => {
  return fetch(apiMap.getRuleTips);
};
