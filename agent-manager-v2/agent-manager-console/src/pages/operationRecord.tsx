import { OperationRecordList } from '../container/operation-record';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const OperationRecord = (props) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/operationRecord',
    exact: true,
    component: () => <OperationRecordList {...props} />,
    isNoPermission: !perPoints.points?.Agent_operationRecord_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}