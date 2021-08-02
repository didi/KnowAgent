import { Dashboard } from '../container/dashboard';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const Dashboards = (props: any) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/dashboard',
    exact: true,
    component: () => <Dashboard {...props} />,
    isNoPermission: !perPoints.points?.Agent_collect_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}