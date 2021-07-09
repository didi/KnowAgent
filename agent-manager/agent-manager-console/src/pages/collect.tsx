import * as React from 'react';
import { CollectTaskList, CollectTaskDetail, AddAcquisitionTask } from '../container/collect-task';
import { IPageRouteItem } from '../interface/common';
import CommonRoutePage from './common';
import store from '../store';

export const Collect = (props: any) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/collect',
    exact: true,
    component: () => <CollectTaskList {...props} />,
    isNoPermission: !perPoints.points?.Agent_collect_page,
  }, {
    path: '/collect/detail',
    exact: true,
    component: () => <CollectTaskDetail {...props} />,
    isNoPermission: !perPoints.points?.Agent_collect_page,
  }, {
    path: '/collect/add-task',
    exact: true,
    component: () => <AddAcquisitionTask {...props} />,
    isNoPermission: !perPoints.points?.Agent_collect_page,
  }, {
    path: '/collect/edit-task',
    exact: true,
    component: () => <AddAcquisitionTask {...props} />,
    isNoPermission: !perPoints.points?.Agent_collect_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}