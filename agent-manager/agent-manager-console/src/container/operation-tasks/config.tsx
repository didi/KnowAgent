
import * as React from 'react';
import { NavRouterLink } from '../../component/CustomComponent';
import { ListInfo } from '../../interface/common';
import moment from 'moment';
import { timeFormat } from '../../constants/time';
import './index.less';
import { renderTooltip } from '../../component/CustomComponent';

export const taskBreadcrumb = [{
  label: 'Agent中心',
}, {
  label: '运维任务',
}];

export const taskDetailBreadcrumb = [{
  label: 'Agent中心',
}, {
  aHref: '/operationTasks',
  label: '运维任务',
}, {
  label: '任务详情',
}];

export const getTaskListColumns = (cb: any) => {
  const taskListColumns = [{
    title: '任务ID',
    dataIndex: 'agentOperationTaskId',
    key: 'agentOperationTaskId',
    width: '8%',
  }, {
    title: '任务名',
    dataIndex: 'agentOperationTaskName',
    key: 'agentOperationTaskName',
    width: '10%',
    render: (text: string, record: any) => (
      <NavRouterLink needToolTip element={text} state={record} href="operationTasks/taskDetail" />
    ),
  }, {
    title: '任务类型',
    dataIndex: 'agentOperationTaskType',
    key: 'agentOperationTaskType',
    width: '10%',
    render: (text: any) => {
      return text == 0 ? '安装' : text == 1 ? '卸载' : text == 2 ? '升级' : renderTooltip(text)
    },
    sorter: (a: ListInfo, b: ListInfo) => a.agentOperationTaskType - b.agentOperationTaskType,
  }, {
    title: '主机数量',
    dataIndex: 'relationHostCount',
    key: 'relationHostCount',
    width: '10%',
    render: (t: any) => renderTooltip(t),
    sorter: (a: ListInfo, b: ListInfo) => a.relationHostCount - b.relationHostCount,
  }, {
    title: '状态',
    dataIndex: 'agentOperationTaskStatus',
    key: 'agentOperationTaskStatus',
    width: '8%',
    render: (text: any) => {
      return text == 100 ? '完成' : text == 30 ? '执行中' : renderTooltip(text)
    },
    sorter: (a: ListInfo, b: ListInfo) => a.agentOperationTaskStatus - b.agentOperationTaskStatus,
  }, {
    title: '执行概览',
    dataIndex: 'successful',
    key: 'successful',
    width: '18%',
    render: (text: any, record: any) => {
      return (
        <div className="dot">
          <span className="green-dot"></span>成功：{record.successful}
          <span className="red-dot" style={{ marginLeft: "8px" }}></span>失败：{record.failed}
        </div>
      )
    },
  }, {
    title: '开始时间',
    dataIndex: 'agentOperationTaskStartTime',
    key: 'agentOperationTaskStartTime',
    width: '13%',
    render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
    sorter: (a: ListInfo, b: ListInfo) => a.agentOperationTaskStartTime - b.agentOperationTaskStartTime,
  }, {
    title: '结束时间',
    dataIndex: 'agentOperationTaskEndTime',
    key: 'agentOperationTaskEndTime',
    width: '13%',
    render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
    sorter: (a: ListInfo, b: ListInfo) => a.agentOperationTaskEndTime - b.agentOperationTaskEndTime,
  }, {
    title: '创建人',
    dataIndex: 'operator',
    key: 'operator',
    width: '12%',
    sorter: (a: ListInfo, b: ListInfo) => a.operator.charCodeAt(0) - b.operator.charCodeAt(0),
  },
  ];
  return taskListColumns;
};

export const getTaskTableColumns = () => {
  const taskTableColumns = [
    {
      title: '主机名',
      dataIndex: 'hostName',
      // commonSearch: true,
    }, {
      title: '主机IP',
      dataIndex: 'ip',
      // commonSearch: true,
    }, {
      title: '主机类型',
      dataIndex: 'container',
      render: (text: any) => {
        return text == 0 ? '主机' : '容器'
      }
    }, {
      title: '执行状态',
      dataIndex: 'executeStatus',
      commonSorter: true,
      render: (text: any) => {
        return text == 0 ? '待执行' : text == 1 ? '执行中' : text == 2 ? '执行成功' : text == 3 ? '执行失败' : renderTooltip(text)
      }
    }, {
      title: '原Agent版本',
      dataIndex: 'sourceAgentVersion',
    }, {
      title: '开始时间',
      dataIndex: 'executeStartTime',
      render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
    }, {
      title: '完成时间',
      dataIndex: 'executeEndTime',
      render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
    }, {
      title: '操作',
      dataIndex: 'operation',
    },
  ];
  return taskTableColumns;
}

