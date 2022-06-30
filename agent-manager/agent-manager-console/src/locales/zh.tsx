import { systemKey } from '../constants/menu';
/**
 * 用于左侧菜单与顶部路由导航中文展示，key值与各页面路径对应，比如dashboard页，路由：/cluster/dashbord，key值：menu.cluster.dashborad
 */
export default {
  [`menu.${systemKey}.home`]: 'Dashboard',
  [`menu.${systemKey}.404`]: '404',

  [`menu`]: 'Agent',
  [`menu.${systemKey}`]: '我的工作台',
  [`menu.${systemKey}.home`]: '我的工作台',
  [`menu.${systemKey}.home.devops`]: '运维视角',
  [`menu.${systemKey}.home.operation`]: '运营视角',
  [`menu.${systemKey}.demo`]: 'Agent中心',
  [`menu.${systemKey}.demo.physics`]: 'Agent版本',
  [`menu.${systemKey}.demo.logic`]: 'Agent管理',

  /** agent中心 */
  [`menu.${systemKey}.main`]: 'Agent中心',
  [`menu.${systemKey}.main.list`]: 'Agent管理',
  [`menu.${systemKey}.main.agentVersion`]: 'Agent版本',
  [`menu.${systemKey}.main.agent-kanban`]: '指标看板',

  [`menu.${systemKey}.dataSource`]: '应用管理',

  [`menu.${systemKey}.receivingTerminal`]: '接收端管理',

  [`menu.${systemKey}.collect`]: '采集任务',
  [`menu.${systemKey}.collect.metric`]: '指标看板',
  [`menu.${systemKey}.collect.list`]: '采集任务管理',
  [`menu.${systemKey}.operationRecord`]: '操作记录',
  [`menu.${systemKey}.tool`]: '工具箱',
  [`menu.${systemKey}.tool.indicator-probe`]: '指标探查',
  // [`menu.${systemKey}.kafka`]: "Kafka管理",
  // [`menu.${systemKey}.kafka.physics`]: "物理集群",
};
