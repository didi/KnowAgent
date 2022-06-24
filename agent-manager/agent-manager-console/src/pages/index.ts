import HomePage from './DashBoard';
import { DemoPage } from './DemoPage';

import AgentList from './AgentList';
import AgentVersion from './AgentVersion';
import AgentDetail from './AgentDetail';
import ApplicationList from './ApplicationList';
import ReceivingTerminal from './ReceivingTerminal';
import Collect from './CollectTasks';
import CollectDetail from './CollectDetail';
import OperationRecord from './OperationRecord';
import IndicatorProbe from './IndicatorProbe';
import AgentKanban from './AgentKanban';
import CollectTaskKanban from './CollectTaskKanban';
import CollectTaskPage from './CollectTaskPage';
import AddAcquisitionTask from './add-collect-task';
export const pageRoutes = [
  {
    path: '/',
    exact: true,
    component: HomePage,
  },
  {
    path: '/home',
    exact: true,
    component: HomePage,
  },
  {
    path: '/demo/physics',
    exact: true,
    component: DemoPage,
  },
  {
    path: '/demo/test',
    exact: true,
    component: AgentDetail,
  },
  {
    path: '/main',
    // agent管理
    exact: true,
    component: AgentList,
  },
  {
    path: '/main/detail',
    // agent管理
    exact: true,
    component: AgentDetail,
  },
  {
    path: '/main/agentVersion',
    // agent版本
    exact: true,
    component: AgentVersion,
  },
  {
    path: '/dataSource',
    exact: true,
    component: ApplicationList,
  },
  {
    path: '/receivingTerminal',
    exact: true,
    component: ReceivingTerminal,
  },
  {
    path: '/collect',
    exact: true,
    component: Collect,
  },
  {
    path: '/collect/detail',
    exact: true,
    component: CollectDetail,
  },
  {
    path: '/operationRecord',
    exact: true,
    component: OperationRecord,
  },
  {
    path: '/main/agent-kanban',
    exact: true,
    component: AgentKanban,
  },
  {
    path: '/tool/indicator-probe',
    exact: true,
    component: IndicatorProbe,
  },
  {
    path: '/collect/metric',
    exact: 'true',
    component: CollectTaskKanban,
  },
  {
    path: '/collect/add-task',
    exact: 'true',
    component: AddAcquisitionTask,
  },
];
