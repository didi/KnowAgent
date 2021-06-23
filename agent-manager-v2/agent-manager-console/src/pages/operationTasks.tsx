import { TaskDetail, TaskList } from '../container/operation-tasks';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const OperationTasks = (props: any) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/',
    exact: true,
    component: () => <TaskList {...props} />,
    isNoPermission: !perPoints.points?.Agent_operational_tasks_page,
  }, {
    path: '/operationTasks',
    exact: true,
    component: () => <TaskList {...props} />,
    isNoPermission: !perPoints.points?.Agent_operational_tasks_page,
  }, {
    path: '/operationTasks/taskDetail',
    exact: true,
    component: TaskDetail,
    isNoPermission: !perPoints.points?.Agent_operational_tasks_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}