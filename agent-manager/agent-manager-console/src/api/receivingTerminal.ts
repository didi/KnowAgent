import fetch from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { IReceivingTerminalParams, ILogReceivingTerminal, IReceivingTerminal } from '../interface/receivingTerminal';

// 查询系统全量接收端信息
export const getReceiversList = () => {
  return fetch(apiMap.getReceiversList);
}

export const getReceivingList = (params: IReceivingTerminalParams) => {
  return fetch(apiMap.getReceivingList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const getReceivingTopic = (receiverId: ILogReceivingTerminal) => {
  return fetch(apiMap.getTopics + `/${receiverId}/topics`);
}

export const addReceive = (params: IReceivingTerminal) => {
  return fetch(apiMap.actionReceiver, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}

export const modifyReceive = (params: IReceivingTerminal) => {
  return fetch(apiMap.actionReceiver, {
    method: csrfTokenMethod[1],
    body: JSON.stringify(params),
  });
}

export const deleteReceive = (id: number) => {
  return fetch(apiMap.actionReceiver + `/${id}`, {
    method: csrfTokenMethod[2],
  });
}

export const getAgentId = (AgentId: number) => {
  return fetch(apiMap.getAgentId + `/${AgentId}`);
}
export const getLogCollectTaskId = (LogCollectTaskId: number) => {
  return fetch(apiMap.getLogCollectTaskId + `/${LogCollectTaskId}`);
}


