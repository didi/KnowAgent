import fetch from "../lib/fetch";
import actionTypes from './actionTypes';
import { Dispatch } from 'redux';
import { apiMap } from "src/api/api";
import { IUser } from "src/store/type";

export const setName = ({ name: string }) => ({
  type: actionTypes.SET_NAME,
  payload: { name }
});

export const setUser = (user: IUser) => {
  return ({
  type: actionTypes.SET_USER,
  payload: { user }
})
};

export const setLoading = (loading: boolean) => ({
  type: actionTypes.SET_LOADING,
  payload: {
    loading: true
  }
});

export const getUserAsync = () => (dispatch: Dispatch) => {
  dispatch(setLoading(true));
  // fetch(apiMap.getAgentHostList).then(res => dispatch(setUser(res)));
  return new Promise(res => res({
    name: 'didi',
    id: 1,
  })).then((res) => dispatch(setUser(res as IUser)));
}