import actionTypes from './actionTypes';

export const setCollectType = (collectType: number) => ({
  type: actionTypes.SET_COLLECT_TYPE,
  payload: {
    collectType,
  }
});

export const setLogType = (logType: string) => ({
  type: actionTypes.SET_LOG_TYPE,
  payload: {
    logType,
  }
});

export const setCollectState = (params: any) => ({
  type: actionTypes.SET_COLLECTSTATE,
  payload: params
})

export const setCollectTaskDetail = (detail: any) => ({
  type: actionTypes.SET_COLLECTTASK_DETAIL,
  payload: {
    detail
  }
})