import { ClusterList } from '../container/receiving-terminal';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const ReceivingTerminal = (props) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/',
    exact: true,
    component: () => <ClusterList {...props} />,
    isNoPermission: !perPoints.points?.Agent_receivingterminal_cluster_page,
  }, {
    path: '/receivingTerminal/clusterList',
    exact: true,
    component: () => <ClusterList {...props} />,
    isNoPermission: !perPoints.points?.Agent_receivingterminal_cluster_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}