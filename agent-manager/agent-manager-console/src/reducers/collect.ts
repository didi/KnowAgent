import actionTypes from "../actions/actionTypes"
import { CollectState } from "src/store/type";

export const initialState: CollectState = {
  collectType: '' as unknown as number,
  logType: 'file' as string,
};

export default (state = initialState, action: any) => {
  switch (action.type) {
    case actionTypes.SET_COLLECT_TYPE: {
      const { collectType } = action.payload;

      return { ...state, collectType };
      break;
    }
  }

  switch (action.type) {
    case actionTypes.SET_LOG_TYPE: {
      const { logType } = action.payload;

      return { ...state, logType };
      break;
    }
  }

  return state;
};
