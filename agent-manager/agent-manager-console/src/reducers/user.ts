import actionTypes from "../actions/actionTypes";

export const initialState = {
  loading: true,
  user: null,
};

export default (state = initialState, action:any) => {
  switch (action.type) {
    case actionTypes.SET_USER: {
      const { user } = action.payload;
      return { ...state, user, loading: false };
    };
  }

  return state;
};