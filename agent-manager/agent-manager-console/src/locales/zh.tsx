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
  [`menu.${systemKey}.home.devops`]: '运维大盘',
  [`menu.${systemKey}.home.operation`]: '运营大盘',
  [`menu.${systemKey}.demo`]: 'Agent中心',
  [`menu.${systemKey}.demo.physics`]: 'Agent版本',
  [`menu.${systemKey}.demo.logic`]: 'Agent管理',

  /** agent中心 */
  [`menu.${systemKey}.main`]: 'Agent中心',
  [`menu.${systemKey}.main.list`]: 'Agent管理',
  [`menu.${systemKey}.main.agentVersion`]: 'Agent版本管理',
  [`menu.${systemKey}.main.agent-kanban`]: '指标看板',

  [`menu.${systemKey}.meta`]: '元数据中心',
  [`menu.${systemKey}.meta.dataSource`]: '应用管理',
  [`menu.${systemKey}.meta.receivingTerminal`]: '接收端管理',
  [`menu.${systemKey}.meta.metaVersion`]: '元数据管理',

  [`menu.${systemKey}.collect`]: '采集任务管理',
  [`menu.${systemKey}.monitor`]: '监控中心',
  [`menu.${systemKey}.monitor.metric`]: '采集任务指标看板',
  [`menu.${systemKey}.monitor.agent-kanban`]: 'Agent指标看板',
  [`menu.${systemKey}.collect.list`]: '采集任务管理',
  [`menu.${systemKey}.operationRecord`]: '操作记录',
  [`menu.${systemKey}.tool`]: '运维中心',
  [`menu.${systemKey}.tool.indicator-probe`]: '指标探查',
  // [`menu.${systemKey}.kafka`]: "Kafka管理",
  // [`menu.${systemKey}.kafka.physics`]: "物理集群",
};
