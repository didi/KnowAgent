import HomePage from './DashBoard';
import Devops from './DashBoard/Devops';
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
import IndexPage from './IndexPage';
import MetaVersion from './MetaVersion';

export const pageRoutes = [
  {
    path: '/',
    exact: true,
    component: IndexPage,
  },
  {
    path: '/version/operation',
    exact: true,
    component: HomePage,
  },
  {
    path: '/version/devops',
    exact: true,
    component: Devops,
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
    path: '/meta/dataSource',
    exact: true,
    component: ApplicationList,
  },
  {
    path: '/meta/receivingTerminal',
    exact: true,
    component: ReceivingTerminal,
  },
  {
    path: '/meta/metaVersion',
    exact: true,
    component: MetaVersion,
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
    path: '/monitor/agent-kanban',
    exact: true,
    component: AgentKanban,
  },
  {
    path: '/tool/indicator-probe',
    exact: true,
    component: IndicatorProbe,
  },
  {
    path: '/monitor/metric',
    exact: 'true',
    component: CollectTaskKanban,
  },
  {
    path: '/collect/add-task',
    exact: 'true',
    component: AddAcquisitionTask,
  },
];
