import fetch, { formFetch } from "../lib/fetch";
import { apiMap, csrfTokenMethod } from "./api";
import { IAgentVersionParams, ILogAgentVersion, ILogAgentVersionM } from '../interface/agentVersion';

export const getAgentVersionExtraParams = (params: IAgentVersionParams) => {
  return `&agentPackageName=${params.agentPackageName}&agentVersion=${params.agentVersion}&agentVersionCreateTimeStart=${params.agentVersionCreateTimeStart}`;
}

// export const getAgentVersion = (params: IAgentVersionParams) => {
//   const { pageNo, pageSize } = params;
//   return fetch(apiMap.getVersionList + getPageQuery(pageNo, pageSize) + getAgentVersionExtraParams(params));
// }
export const getAgentVersion = (params: IAgentVersionParams) => {
  return fetch(apiMap.getVersionList, {
    method: csrfTokenMethod[0],
    body: JSON.stringify(params),
  });
}
export const getVersion = () => {
  return fetch(apiMap.getAgentVersionList);
}

export const addAgentVersion = (params: ILogAgentVersion) => {
  const formData = new FormData();
  formData.append('uploadFile', params.uploadFile.fileList[0].originFileObj);
  formData.append('agentPackageName', params.agentPackageName);
  formData.append('agentVersion', params.agentVersion);
  formData.append('agentVersionDescription', params.agentVersionDescription);
  formData.append('fileMd5', params.fileMd5);

  return formFetch(apiMap.actionVersion, {
    method: csrfTokenMethod[0],
    body: formData,
  });
}

export const modifyAgentVersion = (params: ILogAgentVersionM) => {
  return fetch(apiMap.actionVersion + `?agentVersionDescription=${params.agentVersionDescription}&id=${params.id}`, {
    method: csrfTokenMethod[1],
    // body: JSON.stringify(params),
  });
}

export const download = (agentVersionId: number) => {
  return fetch(apiMap.actionVersion + `/${agentVersionId}`);
}

export const deleteAgentVersion = (agentVersionId: number) => {
  return fetch(apiMap.actionVersion + `/${agentVersionId}`, {
    method: csrfTokenMethod[2],
  });
}


