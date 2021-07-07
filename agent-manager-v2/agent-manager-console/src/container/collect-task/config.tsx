

import * as React from 'react';
import { Input, Modal, Tag, Popconfirm, DatePicker, Select, InputNumber } from 'antd';
import { IBaseInfo } from '../../interface/common';
import { renderOperationBtns, IBtn, NavRouterLink, renderTooltip } from '../../component/CustomComponent';
import { taskHealthMap, collectModes, taskhealthTypes, hostTypeMap, collectModeMap, healthMap } from '../../constants/common';
import { ISwitchCollectTask, ICollectTask, IReceiverVO, ILogCollectTaskDetail } from '../../interface/collect';
import { switchCollectTask, deleteCollectTask } from '../../api/collect'
import { IService, IAgentHostSet } from '../../interface/agent';
import { timeFormat } from '../../constants/time';
import { cellStyle } from '../../constants/table';
import moment from 'moment';
import store from '../../store'
const { confirm } = Modal;
const { RangePicker } = DatePicker;
const { Option } = Select;

export const collectBreadcrumb = [{
  label: '采集任务列表',
}];

export const collectTaskDetailBreadcrumb = [{
  aHref: '/collect',
  label: '采集任务列表',
}, {
  label: '采集任务详情',
}];

export const collectAddTaskBreadcrumb = [{
  aHref: '/collect',
  label: '采集任务列表',
}, {
  label: '新增采集任务',
}];

export const collectEditTaskBreadcrumb = [{
  aHref: '/collect',
  label: '采集任务列表',
}, {
  label: '编辑采集任务',
}];

export const getCollectFormColumns = (collectRef: any, healthRef: any, form: any) => {
  const collectFormColumns = [{
    type: 'custom',
    title: '采集模式',
    dataIndex: 'logCollectTaskTypeList',
    component: (
      <Select
        mode="multiple"
        placeholder='请选择'
        ref={collectRef}
        allowClear={true}
        showArrow={true}
        onInputKeyDown={() => {
          form.resetFields(['logCollectTaskTypeList']);
          collectRef.current.blur();
        }}
        maxTagCount={0}
        maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
      >
        {collectModes.map((d, index) =>
          <Option value={d.value} key={index}>{d.label}</Option>
        )}
      </Select>
    ),
  },
  {
    type: 'custom',
    title: '健康度',
    dataIndex: 'logCollectTaskHealthLevelList',
    component: (
      <Select
        mode="multiple"
        placeholder='请选择'
        ref={healthRef}
        allowClear={true}
        showArrow={true}
        onInputKeyDown={() => {
          form.resetFields(['logCollectTaskHealthLevelList']);
          healthRef.current.blur();
        }}
        maxTagCount={0}
        maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
      >
        {taskhealthTypes.map((d, index) =>
          <Option value={d.value} key={index}>{d.label}</Option>
        )}
      </Select>
    ),
  },
  {
    type: 'custom',
    title: '任务ID',
    dataIndex: 'logCollectTaskId',
    component: (
      <InputNumber placeholder='请输入' />
    ),
  },
  {
    type: 'custom',
    title: '任务名',
    dataIndex: 'logCollectTaskName',
    component: (
      <Input placeholder='请输入' />
    ),
  },
  {
    type: 'custom',
    title: '创建时间',
    dataIndex: 'locCollectTaskCreateTime', // locCollectTaskCreateTimeStart locCollectTaskCreateTimeEnd
    component: (
      <RangePicker showTime={{
        defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],
      }} style={{ width: '100%' }} />
    ),
  }];
  return collectFormColumns;
}

export const getCollectListColumns = (drawer: any, getData: any) => {
  const collectListColumns = [{
    title: '任务ID',
    dataIndex: 'logCollectTaskId',
    key: 'logCollectTaskId',
    width: '6%',
  }, {
    title: '任务名',
    dataIndex: 'logCollectTaskName',
    key: 'logCollectTaskName',
    width: '11%',
    render: (text: string, record: ICollectTask) => (
      <NavRouterLink element={text} href={`/collect/detail`} state={{ taskId: record.logCollectTaskId }} />
    ),
  }, {
    title: '采集模式',
    dataIndex: 'logCollectTaskType',
    key: 'logCollectTaskType',
    width: '10%',
    render: (t: number) => collectModeMap[t],
  }, {
    title: '采集应用',
    dataIndex: 'serviceList',
    key: 'serviceList',
    width: '10%',
    render: (text: IService[]) => {
      const servicenames = text?.map((ele: IService) => ele.servicename)
      return (<>{renderTooltip(servicenames?.join(','), 50)}</>)
    }
  }, {
    title: '接收端集群',
    dataIndex: 'receiverVO',
    key: 'receiverVO',
    width: '10%',
    style: { ...cellStyle },
    render: (obj: IReceiverVO) => renderTooltip(obj?.kafkaClusterName, 50),
  }, {
    title: '接收端Topic',
    dataIndex: 'receiverTopic',
    key: 'receiverTopic',
    width: '10%',
    style: { ...cellStyle },
    render: (text: string) => renderTooltip(text, 32),
  }, {
    title: '健康度',
    dataIndex: 'logCollectTaskHealthLevel',
    key: 'logCollectTaskHealthLevel',
    width: '12%',
    render: (t: number, record: ICollectTask) => {
      return (<>
        <Tag color={taskHealthMap[t]}>{taskHealthMap[t]}</Tag>
        {/* {(t === 0 || t === 1) ? <a onClick={() => getDiagnosisReport(record, drawer)}>诊断报告</a> : null} */}
      </>);
    },
  }, {
    title: '创建时间',
    dataIndex: 'logCollectTaskCreateTime',
    key: 'logCollectTaskCreateTime',
    width: '10%',
    sorter: (a: ICollectTask, b: ICollectTask) => b.logCollectTaskCreateTime - a.logCollectTaskCreateTime,
    render: (t: number) => t ? moment(t).format(timeFormat) : <Tag />,
  }, {
    title: '结束时间',
    dataIndex: 'logCollectTaskFinishTime',
    key: 'logCollectTaskFinishTime',
    width: '10%',
    sorter: (a: ICollectTask, b: ICollectTask) => b.logCollectTaskFinishTime - a.logCollectTaskFinishTime,
    render: (t: number) => t ? moment(t).format(timeFormat) : <Tag />,
  },
  {
    title: '操作',
    width: '11%',
    render: (text: any, record: ICollectTask) => {
      const btns = getCollectListBtns(record, getData);
      return renderOperationBtns(btns, record);
    },
  },
  ];
  return collectListColumns;
};

export const getCollectListBtns = (record: ICollectTask, getData: any): IBtn[] => [{
  label: '编辑',
  aHref: `/collect/edit-task`,
  state: { taskId: record.logCollectTaskId }
}, {
  invisible: record.logCollectTaskStatus === 2,
  label: <Popconfirm  // 0：暂停 1：运行 2：已完成（2已完成状态仅针对 “按指定时间范围采集” 类型）
    onConfirm={() => switchTask(record, getData)}
    title={`是否确认${record.logCollectTaskStatus === 0 ? '继续' : '暂停'}采集任务？`}
    okText="确认"
    cancelText="取消">
    {record.logCollectTaskStatus === 0 ? '继续' : '暂停'}
  </Popconfirm>,
}, {
  label: '删除',
  clickFunc: (record: ICollectTask) => {
    deleteHostClick(record, getData);
  },
}];


const getDiagnosisReport = (record: any, drawer: any) => {
  drawer('DiagnosisReport', record);
}

const switchTask = (record: ICollectTask, getData: any) => {
  // console.log(record, 'record=====')
  const params = {
    logCollectTaskId: record.logCollectTaskId,
    status: record.logCollectTaskStatus === 0 ? 1 : 0,
  } as ISwitchCollectTask;
  switchCollectTask(params).then((res: any) => {
    Modal.success({
      content: `${record.logCollectTaskStatus === 0 ? '继续' : '暂停'}成功！`,
      okText: '确认',
      onOk: () => { getData() }
    });
  }).catch((err: any) => {
    // console.log(err);
  });
}

const deleteHostClick = (record: ICollectTask, getData: any) => {
  confirm({
    title: '采集任务被删除后将不再继续采集，是否确认？',
    content: <span className="fail">删除操作不可恢复，请谨慎操作！</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      deleteCollectTask(record.logCollectTaskId).then((res: any) => {
        Modal.success({
          content: '删除成功！',
          okText: '确认',
          onOk: () => { getData() }
        });
      }).catch((err: any) => {
        // console.log(err);
      });
    },
  });
}

export const collectTaskDetailBaseInfo = (detail: ILogCollectTaskDetail) => {
  const baseInfo: IBaseInfo[] = [{
    label: '采集任务ID',
    key: 'id',
  },
  // {
  //   label: '所属项目',
  //   key: 'project',
  // }, 
  {
    label: '采集应用名',
    key: 'logCollectTaskName',
  }, {
    label: '采集模式',
    key: 'logCollectTaskType',
    render: (t: number) => collectModeMap[t],
  }, {
    label: '接收端集群',
    key: 'receiver', // kafkaClusterName
    render: (record: IReceiverVO) => renderTooltip(record?.kafkaClusterName),
  }, {
    label: '接收端Topic',
    key: 'sendTopic',
  }, {
    label: '创建时间',
    key: 'collectStartBusinessTime',
    invisible: !detail.collectStartBusinessTime,
    render: (t: number) => moment(t).format(timeFormat),
  }, {
    label: '结束时间',
    key: 'collectEndBusinessTime',
    invisible: !detail.collectEndBusinessTime,
    render: (t: number) => moment(t).format(timeFormat),
  }, {
    label: '创建人',
    key: 'logCollectTaskCreator',
  }];
  return baseInfo;
}


export const getAssociateHostColumns = (drawer: any) => {
  const associateHostColumns = [
    {
      title: '主机名',
      dataIndex: 'hostName',
      render: (text: string, record: IAgentHostSet) => <a onClick={() => getHostDetail(record, drawer)}>{text}</a>,
    }, {
      title: '主机IP',
      dataIndex: 'ip',
    }, {
      title: '主机类型',
      dataIndex: 'container',
      render: (t: number) => hostTypeMap[t],
    }, {
      title: 'Agent健康度',
      dataIndex: 'agentHealthLevel',
      commonSorter: true,
      sorter: (a: IAgentHostSet, b: IAgentHostSet) => a.agentHealthLevel - b.agentHealthLevel,
      render: (t: number) => {
        return <Tag color={healthMap[t]}>{healthMap[t]}</Tag>
      },
    }, {
      title: 'Agent版本',
      dataIndex: 'agentVersion',
    },
  ];
  return associateHostColumns;
}

const getHostDetail = (record: IAgentHostSet, drawer: any) => {
  drawer('AssociateHostDetail', record);
}
