import actionTypes from "../actions/actionTypes"
import { echartsState } from "src/store/type";

export const initialState: echartsState = {
  loading: false,
  chartMetrics: [],
};

export default (state = initialState, action: any) => {
  switch (action.type) {
    case actionTypes.SET_REFRESH_LOADING: {
      const { loading } = action.payload;

      return { ...state, loading };
      break;
    }
  }

  switch (action.type) {
    case actionTypes.SET_CHART_METRICS: {
      const { chartMetrics } = action.payload;

      return { ...state, chartMetrics };
      break;
    }
  }

  return state;
};
