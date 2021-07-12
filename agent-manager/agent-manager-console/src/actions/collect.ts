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