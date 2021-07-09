import { VerSionList } from '../container/agent-version';
import { IPageRouteItem } from '../interface/common';
import * as React from 'react';
import CommonRoutePage from './common';
import store from '../store';

export const AgentVersion = (props: any) => {
  const perPoints = store?.getState().permPoints
  const pageRoute: IPageRouteItem[] = [{
    path: '/agentVersion',
    exact: true,
    component: () => <VerSionList {...props} />,
    isNoPermission: !perPoints.points?.Agent_agent_version_page,
  }];
  return (
    <CommonRoutePage pageRoute={pageRoute} />
  );
}