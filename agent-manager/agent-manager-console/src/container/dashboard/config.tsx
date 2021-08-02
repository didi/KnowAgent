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
    icon: fileTextFill
  },
  {
    title: '采集路径总数',
    text: '',
    api: 'logCollectPathNum',
    icon: collectPath
  },
  {
    title: '应用总数',
    text: '',
    api: 'serviceNum',
    icon: app
  },
  {
    title: '主机总数',
    text: '',
    api: 'hostNum',
    icon: severFill
  },
  {
    title: '容器总数',
    text: '',
    api: 'containerNum',
    icon: experimentFill
  },
  {
    title: 'Agent总数',
    text: '空转Agent数',
    textApi: 'nonRelateAnyLogCollectTaskAgentNum',
    api: 'agentNum',
    icon: agent
  },
  {
    title: '当日采集量',
    text: '',
    api: 'collectBytesDay',
    icon: daycollect,
    unit: 'M',
    format: byteToMB,
  },
  {
    title: '当前采集量',
    text: '',
    api: 'currentCollectBytes',
    icon: nowCollect,
    unit: 'M',
    format: byteToMB,
  },
  {
    title: '当日采集条数',
    text: '',
    api:  'collectLogEventsDay',
    icon: dayCollectCount,
    unit: '条',
    format: countChange,
  },
  {
    title: '当前采集条数',
    text: '',
    api: 'currentCollectLogEvents',
    icon: nowCollectCount,
    unit: '条',
    format: countChange,
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
  },
  {
    title: '关联Agent数 (个)',
    api: 'logCollectTaskListRelateAgentsTop5',
  },
  {
    title: '出口流量 (MB)',
    api: 'logCollectTaskListCollectBytesTop5',
  },
  {
    title: '出口条数 (条)',
    api: 'logCollectTaskListCollectCountTop5',
  },
]

const agentList = [
  {
    title: 'CPU使用率 (%)',
    api: 'agentListCpuUsageTop5',
  },
  {
    title: '内存使用量 (MB)',
    api: 'agentListMemoryUsageTop5',
  },
  {
    title: '出口流量 (MB)',
    api: 'agentListCollectBytesTop5',
  },
  {
    title: '出口条数 (条)',
    api: 'agentListCollectCountTop5',
  },
  {
    title: 'Full gc (次)',
    api: 'agentListFullGcCountTop5',
  },
  {
    title: 'FD (个)',
    api: 'agentListFdUsedTop5',
  },
  {
    title: '关联日志采集任务数 (个）',
    api: 'agentListRelateLogCollectTasksTop5',
  },
]

export { cardList, collectList, agentList };
