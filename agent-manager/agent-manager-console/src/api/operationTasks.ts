import fetch from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { IOperationTaskParams, ISwitchOperationTask } from '../interface/operationTask';

export const getOperationTasks = (params: IOperationTaskParams) => {
  return fetch(apiMap.getOperationTasksList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const getTaskDetail = (agentOperationTaskId: any) => {
  return fetch(apiMap.actionTasks + `/${agentOperationTaskId}`);
}

export const getTaskHost = (agentOperationTaskId: any, hostName: any) => {
  return fetch(apiMap.actionTasks + `/${agentOperationTaskId}` + `/${hostName}`);
}

export const switchOperationTask = (params: ISwitchOperationTask) => {
  return fetch(apiMap.actionTasks, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}



