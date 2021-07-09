import { IPermPoint } from 'src/store/type';
import actionTypes from './actionTypes';

export const setPermissionPoints = (data: IPermPoint) => ({
  type: actionTypes.SET_PERMISSION_POINTS,
  payload: { data }
});