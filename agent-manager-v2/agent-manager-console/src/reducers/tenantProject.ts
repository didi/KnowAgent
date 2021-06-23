import { TenantProjectState } from "src/store/type";
import actionTypes from "../actions/actionTypes"

const getLocalStorageItem = (type: string) => {
  try {
    return JSON.parse(localStorage.getItem(`icee-global-${type}`) || '');
  } catch (err) {
    return null;
  }
}

export const initialState: TenantProjectState = {
  loading: true,
  project: getLocalStorageItem('project'),
  tenant: getLocalStorageItem('tenant'),
};

export default (state = initialState, action: {
  type: string;
  payload: any;
}) => {
  switch (action.type) {
    case actionTypes.SET_TENANT_PROJECT: {
      const { project, tenant } = action.payload;
      return { ...state, project, tenant };
      break;
    };
  }

  return state;
};
