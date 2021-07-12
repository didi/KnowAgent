import actionTypes from "../actions/actionTypes"
import { ModalState } from "src/store/type";

export const initialState: ModalState = {
  loading: false,
  modalId: '',
  drawerId: '',
  params: null,
};

export default (state = initialState, action: any) => {

  switch (action.type) {
    case actionTypes.SET_MODAL_ID: {
      const { modalId, params } = action.payload;
      return { ...state, modalId, params };
      break;
    }
    case actionTypes.SET_DRAWER_ID: {
      const { drawerId, params } = action.payload;
      return { ...state, drawerId, params };
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
