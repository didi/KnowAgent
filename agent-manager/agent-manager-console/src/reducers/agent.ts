import actionTypes from "../actions/actionTypes"
import moment from 'moment';
import { AgentState } from "src/store/type";

export const initialState: AgentState = {
  timeRange: [moment().subtract(60, 'minute'), moment()] as moment.Moment[],
};

export default (state = initialState, action: any) => {
  switch (action.type) {
    case actionTypes.SET_TIME_RANGE: {
      const { timeRange } = action.payload;
      return { ...state, timeRange };
      break;
    }
  }

  return state;
};
