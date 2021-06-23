
import actionTypes from "../actions/actionTypes"

export const initialState: any = { 
  points: null,
 };

export default (state = initialState, action:any) => {
  switch (action.type) {
    case actionTypes.SET_PERMISSION_POINTS: {
      const { data } = action.payload;
      return { ...state, points: data };
    }
  }

  return state;
};
