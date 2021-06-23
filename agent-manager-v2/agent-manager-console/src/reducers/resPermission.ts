import actionTypes from "../actions/actionTypes"

export const initialState = { 
  rdbPoints: null,
 };

export default (state = initialState, action:any) => {
  switch (action.type) {
    case actionTypes.SET_RES_PERMISSIONS: {
      const { params } = action.payload;
      return { ...state, rdbPoints: params };
    }
  }
  return state;
};
