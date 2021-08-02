import zh from '@pkgs/locales/zh';
import { systemKey } from '../constants/menu';

export default {
  ...zh,
  [`menu.${systemKey}.dashboard`]: '我的工作台',
  [`menu.${systemKey}.main`]: 'Agent中心',
  [`menu.${systemKey}.main.list`]: 'Agent管理',
  [`menu.${systemKey}.main.agentVersion`]: 'Agent版本',
  [`menu.${systemKey}.main.operationTasks`]: '运维任务',
  // [`menu.${systemKey}.main.hola`]: 'hola',
  [`menu.${systemKey}.dataSource`]: '数据源管理',
  [`menu.${systemKey}.dataSource.appList`]: '应用管理',
  [`menu.${systemKey}.receivingTerminal`]: '接收端管理',
  [`menu.${systemKey}.receivingTerminal.clusterList`]: 'Kafka集群',
  [`menu.${systemKey}.collect`]: '采集任务',
  [`menu.${systemKey}.operationRecord`]: '操作记录',
};
