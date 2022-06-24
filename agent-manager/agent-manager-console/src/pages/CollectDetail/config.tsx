import React from 'react';
import moment from 'moment';
import { Tag, Tooltip } from '@didi/dcloud-design';

export const hostTypeMap = {
  0: '物理机',
  1: '容器',
  // 2:'VM虚拟机',
};

export const healthMap = {
  0: 'red',
  1: 'yellow',
  2: 'green',
};

export const collectModeMap = {
  0: '流式采集',
  1: '时间段补采',
};

export const timeFormat = 'YYYY-MM-DD HH:mm:ss';

export const renderTooltip = (text: string, num?: number) => {
  const figure = num ? num : 16;
  return (
    <>
      {text ? (
        <Tooltip title={text} placement="bottomLeft">
          {text?.length > figure ? text?.substring(0, figure) + '...' : text}
        </Tooltip>
      ) : (
          <Tag />
        )}
    </>
  );
};

export const collectTaskDetailBaseInfo = (info: any) => {
  const baseInfo: any[] = [
    {
      label: '采集任务ID',
      key: 'id',
    },
    // {
    //   label: '所属项目',
    //   key: 'project',
    // },
    {
      label: '关联Agent数',
      key: 'relateAgentNum',
      // invisible: !detail.collectStartBusinessTime,
    },
    // {
    //   label: '采集应用名',
    //   key: 'logCollectTaskName',
    // },
    // {
    //   label: '采集模式',
    //   key: 'logCollectTaskType',
    //   renderCustom: (t: any) => collectModeMap[t],
    // },
    // {
    //   label: '接收端集群',
    //   key: 'logCollectTaskType', // kafkaClusterName
    //   renderCustom: (t, record: any) => renderTooltip(record?.kafkaClusterName),
    // },
    // {
    //   label: '接收端Topic',
    //   key: 'sendTopic',
    // },
    {
      label: '创建时间',
      key: 'collectStartBusinessTime',
      // invisible: !detail.collectStartBusinessTime,
      renderCustom: (t: number) => moment(t).format(timeFormat),
    },
    // {
    //   label: '结束时间',
    //   key: 'collectEndBusinessTime',
    //   // invisible: !detail.collectEndBusinessTime,
    //   render: (t: number) => moment(t).format(timeFormat),
    // },
    {
      label: '创建人',
      key: 'logCollectTaskCreator',
    },
  ];
  return baseInfo;
};

export const getAssociateHostColumns = (getHostDetail: any) => {
  const associateHostColumns = [
    {
      title: '主机名',
      dataIndex: 'hostName',
      render: (text: string, record: any) => <a onClick={() => getHostDetail(record)}>{text}</a>,
    },
    {
      title: '主机IP',
      dataIndex: 'ip',
    },
    {
      title: '主机类型',
      dataIndex: 'container',
      render: (t: number) => hostTypeMap[t],
    },
    {
      title: 'Agent健康度',
      dataIndex: 'agentHealthLevel',
      commonSorter: true,
      sorter: (a: any, b: any) => a.agentHealthLevel - b.agentHealthLevel,
      render: (t: number) => {
        return t ? <Tag color={healthMap[t]}>{healthMap[t]}</Tag> : '-';
      },
    },
    {
      title: 'Agent版本',
      dataIndex: 'agentVersion',
    },
  ];
  return associateHostColumns;
};

export const hostBaseInfo = (info: any) => {
  const hostBaseList: any[] = [
    {
      label: '主机id',
      key: 'id',
    },
    {
      label: '主机名',
      key: 'hostName',
    },
    {
      label: '主机类型',
      key: 'container',
      renderCustom: (t: any) => {
        return hostTypeMap[t] ? hostTypeMap[t] : '-';
      },
    },
    {
      label: '主机所属部门',
      key: 'department',
    },
    {
      label: '主机ip',
      key: 'ip',
    },
    {
      label: '宿主机名',
      key: 'parentHostName',
      invisible: info?.container !== 1,
    },
    {
      label: '所属机房',
      key: 'machineZone',
    },
  ];
  return hostBaseList;
};

export const hosts = {
  container: 0, // 主机类型 0：主机 1：容器
  department: '基础', // 主机所属部门
  id: 1, // 主机id
  hostName: 'wll', // 主机名
  ip: '10.2.10.9', // 主机ip
  machineZone: '杭州', // 主机所属机器单元
  parentHostName: 'mm', // 针对容器场景，表示容器对应宿主机名
};
