import actionTypes from './actionTypes';

export const showContent = (dom: JSX.Element) => ({
  type: actionTypes.SET_CONTENT,
  payload: {
    content: dom,
  }
});

export const closeContent = () => ({
  type: actionTypes.SET_CONTENT,
  payload: {
    content: null,
  }
});