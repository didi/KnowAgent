import React from 'react';
import moment from 'moment';
import { Tag, Tooltip, IconFont } from '@didi/dcloud-design';

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
    {
      label: '采集任务名',
      key: 'logCollectTaskName',
    },
    {
      label: '采集应用',
      key: 'services',
      renderCustom: (t: any) => {
        try {
          const services = JSON.parse(t);
          return services[0].servicename;
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '关联Agent数',
      key: 'relateAgentNum',
    },
    {
      label: '采集路径',
      key: 'fileLogCollectPathList',
      renderCustom: (t: any) => {
        try {
          const fileLogCollectPathList = JSON.parse(t);
          const pathString = fileLogCollectPathList.map((path: any) => path?.path || '');
          return (
            <Tooltip title={pathString.join('\n')} placement="bottomLeft">
              <span>{pathString.join(';')}</span>
            </Tooltip>
          );
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '采集文件后缀名匹配正则',
      key: 'fileNameSuffixMatchRule',
      renderCustom: (t: any) => {
        try {
          const fileNameSuffixMatchRule = JSON.parse(t);
          return fileNameSuffixMatchRule.suffixMatchRegular || `-`;
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '切片规则',
      key: 'logContentSliceRule',
      renderCustom: (t: any) => {
        try {
          const rule = JSON.parse(t);
          return `左起第${rule.sliceTimestampPrefixStringIndex}个${rule.sliceTimestampPrefixString} 匹配上 ${rule.sliceTimestampFormat}`;
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '接收端集群',
      key: 'receiver', // kafkaClusterName
      renderCustom: (t: any) => {
        try {
          const receiver = JSON.parse(t);
          return renderTooltip(receiver?.kafkaClusterName);
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '接收端Topic',
      key: 'sendTopic',
    },
    {
      label: '采集延迟监控',
      key: 'collectDelayThresholdMs',
      renderCustom: (t: any) => {
        try {
          const collectDelayThresholdMs = Number(t);
          return collectDelayThresholdMs / 60 / 1000 + '分钟';
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '任务保障等级',
      key: 'limitPriority',
      renderCustom: (t: any) => {
        try {
          const limitPriority = Number(t);
          return limitPriority == 0 ? '高' : limitPriority == 1 ? '中' : '低';
        } catch (error) {
          return '-';
        }
      },
    },
    {
      label: '高级配置',
      key: 'advancedConfigurationJsonString',
    },
    {
      label: '数据接收端生产者属性',
      key: 'kafkaProducerConfiguration',
    },
    {
      label: '采集任务健康度',
      key: 'logCollectTaskHealthLevel',
      renderCustom: (t: any) => {
        const render: JSX.Element = (
          <span style={{ fontSize: '20px' }}>
            {t == 2 ? <IconFont type="icon-hong" /> : t == 1 ? <IconFont type="icon-huang" /> : t == 0 ? <IconFont type="icon-lv" /> : null}
          </span>
        );
        return render;
      },
    },
    {
      label: '采集任务健康度描述',
      key: 'logCollectTaskHealthDescription',
    },
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
      // render: (text: string, record: any) => <a onClick={() => getHostDetail(record)}>{text}</a>,
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
      title: 'Agent健康度描述',
      dataIndex: 'agentHealthDescription',
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
