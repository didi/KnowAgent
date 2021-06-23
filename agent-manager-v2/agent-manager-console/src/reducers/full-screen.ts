import actionTypes from "../actions/actionTypes"
import { fullScreenState } from "src/store/type";

export const initialState: fullScreenState = {
  content: null as any,
};

export default (state = initialState, action: any) => {
  switch (action.type) {
    case actionTypes.SET_CONTENT: {
      const { content } = action.payload;

      return { ...state, content };
      break;
    }
  }

  return state;
};
