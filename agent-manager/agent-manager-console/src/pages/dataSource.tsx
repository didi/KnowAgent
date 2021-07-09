import { AppList } from '../container/data-source';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const DataSource = (props) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/',
    exact: true,
    component: () => <AppList {...props} />,
    isNoPermission: !perPoints.points?.Agent_dataSource_app_page,
  }, {
    path: '/dataSource/appList',
    exact: true,
    component: () => <AppList {...props} />,
    isNoPermission: !perPoints.points?.Agent_dataSource_app_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}