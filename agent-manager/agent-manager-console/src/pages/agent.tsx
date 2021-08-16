import { AgentDetail, AgentList } from '../container/agent-management';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const Agent = (props: any) => {
  const perPoints = store?.getState().permPoints
  // const [perPoints, setPerPoints] = React.useState(null);
  const pageRoute: IPageRouteItem[] = [
    {
    path: '/list',
    exact: true,
    component: () => <AgentList {...props} />,
    isNoPermission: !perPoints.points?.Agent_agent_management_page,
    },
    {
    path: '/detail',
    exact: true,
    component: () => <AgentDetail {...props} />,
    isNoPermission: !perPoints.points?.Agent_agent_management_page,
  }];

  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}