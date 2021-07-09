import actionTypes from './actionTypes';


export const setResPermissions = (params:any)=>{
  return ({
    type: actionTypes.SET_RES_PERMISSIONS,
    payload:{
      params,
    }
  })
};