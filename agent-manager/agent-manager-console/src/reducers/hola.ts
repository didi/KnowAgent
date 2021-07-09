import actionTypes from "../actions/actionTypes"
import { UserState } from "src/store/type";

export const initialState: UserState = {
  loading: false,
  user: {
    name: "World",
    id: 0,
  }
};

export default (state = initialState, action) => {
  switch (action.type) {
    case actionTypes.SET_USER: {
      const { user } = action.payload;

      return { ...state, user, loading: false };
      break;
    }
    case actionTypes.SET_LOADING: {
      const { loading } = action.payload;
      return { ...state, loading };
      break;
    }
  }

  return state;
};
