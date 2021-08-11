import fileTextFill from './../../assets/file-text-fill.png';
import collectPath from './../../assets/collectPath.png';
import app from './../../assets/app.png';
import severFill from './../../assets/file-text-fill.png';
import experimentFill from './../../assets/experiment-fill.png';
import agent from './../../assets/agent.png';
import daycollect from './../../assets/daycllect.png';
import nowCollect from './../../assets/nowCollect.png';
import dayCollectCount from './../../assets/dayCollectCount.png';
import nowCollectCount from './../../assets/nowCollectCount.png';
import { byteToMB, countChange } from './../../lib/utils';

const cardList = [
  {
    title: '采集任务总数',
    text: '僵尸采集任务',
    textApi: 'nonRelateAnyHostLogCollectTaskNum',
    api: 'logCollectTaskNum',
    icon: fileTextFill,
    code: 0,
    textCode: 7,
  },
  {
    title: '采集路径总数',
    text: '',
    api: 'logCollectPathNum',
    icon: collectPath,
    code: 2,
  },
  {
    title: '应用总数',
    text: '',
    api: 'serviceNum',
    icon: app,
    code: 3,
  },
  {
    title: '主机总数',
    text: '',
    api: 'hostNum',
    icon: severFill,
    code: 4,
  },
  {
    title: '容器总数',
    text: '',
    api: 'containerNum',
    icon: experimentFill,
    code: 5,
  },
  {
    title: 'Agent总数',
    text: '空转Agent数',
    textApi: 'nonRelateAnyLogCollectTaskAgentNum',
    api: 'agentNum',
    icon: agent,
    code: 6,
    textCode: 1,
  },
  {
    title: '当日采集量',
    text: '',
    api: 'collectBytesDay',
    icon: daycollect,
    unit: 'M',
    tip: 'byte',
    format: byteToMB,
    code: 10,
  },
  {
    title: '当前采集量',
    text: '',
    api: 'currentCollectBytes',
    icon: nowCollect,
    unit: 'M',
    tip: 'byte',
    format: byteToMB,
    code: 8,
  },
  {
    title: '当日采集条数',
    text: '',
    api:  'collectLogEventsDay',
    icon: dayCollectCount,
    unit: '条',
    tip: '条',
    format: countChange,
    code: 11,
  },
  {
    title: '当前采集条数',
    text: '',
    api: 'currentCollectLogEvents',
    icon: nowCollectCount,
    unit: '条',
    tip: '条',
    format: countChange,
    code: 9,
  },
]

const collectList = [
  // {
  //   title: 'CPU占用核数 (个）',
  //   api: 'logCollectTaskListRelateAgentsTop5',
  // },
  // {
  //   title: '内存占用 (MB)',
  //   api: 'logCollectTaskListMemoryUsageTop5',
  // },
  {
    title: '关联主机数 (个)',
    api: 'logCollectTaskListRelateHostsTop5',
    unit: '个',
    code: 18,
  },
  {
    title: '关联Agent数 (个)',
    api: 'logCollectTaskListRelateAgentsTop5',
    unit: '个',
    code: 19,
  },
  {
    title: '出口流量 (MB)',
    api: 'logCollectTaskListCollectBytesTop5',
    code: 16
  },
  {
    title: '出口条数 (条)',
    api: 'logCollectTaskListCollectCountTop5',
    unit: '条',
    code: 17
  },
]

const agentList = [
  {
    title: 'CPU使用率 (%)',
    api: 'agentListCpuUsageTop5',
    code: 22,
  },
  {
    title: '内存使用量 (MB)',
    api: 'agentListMemoryUsageTop5',
    code: 23,
  },
  {
    title: '出口流量 (MB)',
    api: 'agentListCollectBytesTop5',
    code: 20,
  },
  {
    title: '出口条数 (条)',
    api: 'agentListCollectCountTop5',
    unit: '条',
    code: 21,
  },
  {
    title: 'Full gc (次)',
    api: 'agentListFullGcCountTop5',
    unit: '次',
    code: 25,
  },
  {
    title: 'FD (个)',
    api: 'agentListFdUsedTop5',
    unit: '个',
    code: 24,
  },
  {
    title: '关联日志采集任务数 (个）',
    api: 'agentListRelateLogCollectTasksTop5',
    unit: '个',
    code: 26,
  },
]

export { cardList, collectList, agentList };
