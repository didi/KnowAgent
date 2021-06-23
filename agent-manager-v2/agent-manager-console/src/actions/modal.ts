import actionTypes from './actionTypes';

export const setModalId = (modalId: string, params?: any) => ({
  type: actionTypes.SET_MODAL_ID,
  payload: {
    modalId,
    params,
  }
});

export const setDrawerId = (drawerId: string, params?: any) => ({
  type: actionTypes.SET_DRAWER_ID,
  payload: {
    drawerId,
    params,
  }
});