export const oneDayMillims = 24 * 60 * 60 * 1000;
import { ILabelValue, INumberMap } from '../interface/common';

export const empty = '' as any;

export const agentHead = {
  label: '用于展示平台活跃Agent及待安装Agent的主机列表，可对Agent进行健康诊断，帮助用户了解Agent运行情况；并方便运维人员进行安装、升级、卸载等运维操作。',
  value: 'Agent管理',
} as ILabelValue;

export const versionHead = {
  label: '用于维护平台的Agent版本包，为Agent安装、升级等运维操作提供支撑。',
  value: 'Agent版本',
} as ILabelValue;

export const taskHead = {
  label: '用于展示与Agent相关的运维任务，如安装、升级、卸载，并可以查看任务详情及具体执行进度。',
  value: '运维任务',
} as ILabelValue;

export const dataSourceHead = {
  label: '展示需采集的应用，并提供应用的新增、查询，以及主机关联管理的能力。',
  value: '应用管理',
} as ILabelValue;

export const clusterHead = {
  label: '展示可用于作为日志数据接收端的集群列表，支持对接收端的增删改查操作。',
  value: '集群',
} as ILabelValue;

export const recordHead = {
  label: '记录平台增删改操作，方便问题排查、操作追踪，并为平台正常运行保驾护航。',
  value: '操作记录',
} as ILabelValue;

export const collectHead = {
  label: '对日志采集任务进行统一管理，提供新增、编辑、删除等功能；并直观展示采集任务的健康情况，方便用户排查。',
  value: '采集任务',
} as ILabelValue;

export const flowUnitList = [{
  label: 'KB',
  value: 1024,
}, {
  label: 'MB',
  value: 1048576,
}] as ILabelValue[];

export const healthTypes = [{
  value: 0,
  label: 'red',
}, {
  value: 1,
  label: 'yellow',
}, {
  value: 2,
  label: 'green',
}] as ILabelValue[];

export const queryHealthTypes = [{
  value: 'all',
  label: '请选择',
}, ...healthTypes] as ILabelValue[];

export const hostTypes = [{
  value: 0,
  label: '物理机',
}, {
  value: 1,
  label: '容器',
},
// {
//   value: 2,
//   label: 'VM虚拟机',
// }
] as ILabelValue[];

export const queryHostTypes = [{
  value: 'all',
  label: '请选择',
}, ...hostTypes] as ILabelValue[];

export const hostTypeMap = {
  0: '物理机',
  1: '容器',
  // 2:'VM虚拟机',
} as INumberMap;

export const healthMap = {
  0: 'red',
  1: 'yellow',
  2: 'green',
} as INumberMap;

export const taskHealthMap = {
  0: 'green',
  1: 'yellow',
  2: 'red',
} as INumberMap;


export const collectModes = [{
  value: 0,
  label: '流式采集',
}, {
  value: 1,
  label: '时间段补采',
}] as ILabelValue[];

export const queryCollectModes = [{
  value: '',
  label: '请选择',
}, ...collectModes] as ILabelValue[];

export const collectModeMap = {
  0: '流式采集',
  1: '时间段补采',
} as INumberMap;

export const oldDataFilterMap = {
  0: '不过滤',
  1: '从当前时间开始采集',
  2: '从自定义时间开始采集',
} as INumberMap;

export const hostRangeMap = {
  0: '全部',
  1: '部分',
} as INumberMap;

export const logSliceRuleMap = {
  0: '时间戳',
  1: '正则匹配',
} as INumberMap;

export const suffixMatchMap = {
  0: '固定格式匹配',
  1: '正则匹配',
} as INumberMap;

export const logFilter = {
  0: '否',
  1: '是',
} as INumberMap;

export const logFilterType = {
  0: '包含',
  1: '不包含',
} as INumberMap;

export const limitType = {
  0: '高',
  1: '中',
  2: '低',
} as INumberMap;

export const agentTaskType = {
  0: '安装',
  1: '卸载',
  2: '升级',
} as INumberMap;


export const rdbPoints = [
  'agent_machine_install',
  'agent_machine_upgrade',
  'agent_machine_uninstall',
  'agent_version_add',
  'agent_version_edit',
  'agent_version_delete',
  'agent_app_edit',
  'agent_kafka_cluster_edit',
  'agent_task_add',
  'agent_task_edit',
  'agent_task_delete',
  'agent_task_start_pause',
];