import { combineReducers } from 'redux';
import hola from './hola';
import modal from './modal';
import agent from './agent';
import collect from './collect';
import fullScreen from './full-screen';
import echarts from './echarts';
import permPoints from './permPoints';
import tenantProject from './tenantProject';
import userInfo from './user';
import resPermission from './resPermission'
export default combineReducers({
  hola,
  modal,
  agent,
  collect,
  fullScreen,
  echarts,
  permPoints,
  tenantProject,
  resPermission,
  userInfo
});