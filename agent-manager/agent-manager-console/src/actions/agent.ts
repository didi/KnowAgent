import actionTypes from './actionTypes';

export const setTimeRange = (timeRange: moment.Moment[]) => ({
  type: actionTypes.SET_TIME_RANGE,
  payload: {
    timeRange,
  }
});