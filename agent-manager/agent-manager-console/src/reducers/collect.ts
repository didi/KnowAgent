import actionTypes from "../actions/actionTypes"
import { CollectState } from "src/store/type";

export const initialState: CollectState = {
  collectType: '' as unknown as number,
  logType: 'file' as string,
  logCollectPathId: '' as unknown as number,
  hostName: '',
  detail: {},
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
    case actionTypes.SET_COLLECTSTATE: {

      return {...state, ...action.payload};
      break;
    }
    case actionTypes.SET_COLLECTTASK_DETAIL: {
      const { detail } = action.payload;

      return {...state, detail};
      break;
    }

  }

  return state;
};
