import actionTypes from './actionTypes';
import { IRdAgentMetrics } from '../interface/agent';

export const setRefresh = (loading: boolean) => ({
  type: actionTypes.SET_REFRESH_LOADING,
  payload: {
    loading,
  }
});

export const setChartMetrics = (chartMetrics: IRdAgentMetrics[]) => ({
  type: actionTypes.SET_CHART_METRICS,
  payload: {
    chartMetrics,
  }
});
