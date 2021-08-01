
import * as React from 'react';
import { Modal, Tag, Input, DatePicker, Select, Tooltip } from 'antd';
import { IBaseInfo } from '../../interface/common';
import { renderOperationBtns, IBtn, NavRouterLink, renderTooltip } from '../../component/CustomComponent';
import { healthMap, hostTypeMap, healthTypes, hostTypes } from '../../constants/common';
import { IAgentHostSet, IAgentVersion, IService, IOperationTasksParams } from '../../interface/agent';
import { deleteHost, getTaskExists, createOperationTasks } from '../../api/agent'
import { timeFormat } from '../../constants/time';
import { cellStyle } from '../../constants/table';
import moment from 'moment';
import store from '../../store'
import './index.less';

const { confirm } = Modal;
const { RangePicker } = DatePicker;
const { Option } = Select;

export const agentBreadcrumb = [{
  label: 'Agent中心',
}, {
  label: 'Agent管理',
}];

export const agentDetailBreadcrumb = [{
  aHref: '/list',
  label: '主机列表',
}, {
  label: '详情',
}];

export const getQueryFormColumns = (agentVersions: IAgentVersion[], versionRef: any, healthRef: any, containerRef: any, form: any) => {
  const queryFormColumns = [
    {
      type: 'custom',
      title: '主机名/IP',
      dataIndex: 'hostNameOrIp',
      component: (<Input placeholder='请输入' />),
    },
    {
      type: 'custom',
      title: 'Agent版本号',
      dataIndex: 'agentVersionIdList',
      component: (
        <Select
          // mode="multiple"
          placeholder='请选择'
          ref={versionRef}
          allowClear={true}
          showArrow={true}
        // onInputKeyDown={() => {
        //   form.resetFields(['agentVersionIdList']);
        //   versionRef.current.blur();
        // }}
        // maxTagCount={0}
        // maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
        >
          {agentVersions.map((d: IAgentVersion, index: number) =>
            <Option value={d.agentVersionId} key={index}>{d.agentVersion}</Option>
          )}
        </Select>
      ),
    },
    {
      type: 'custom',
      title: '健康度',
      dataIndex: 'agentHealthLevelList',
      component: (
        <Select
          mode="multiple"
          placeholder='请选择'
          ref={healthRef}
          allowClear={true}
          showArrow={true}
          onInputKeyDown={() => {
            form.resetFields(['agentHealthLevelList']);
            healthRef.current.blur();
          }}
          maxTagCount={0}
          maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
        >
          {healthTypes.map((d, index) =>
            <Option value={d.value} key={index}>{d.label}</Option>
          )}
        </Select>
      ),
    },
    {
      type: 'custom',
      title: '主机类型',
      dataIndex: 'containerList',
      component: (
        <Select
          // mode="multiple"
          placeholder='请选择'
          ref={containerRef}
          allowClear={true}
          showArrow={true}
        // onInputKeyDown={() => {
        //   form.resetFields(['containerList']);
        //   containerRef.current.blur();
        // }}
        // maxTagCount={0}
        // maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
        >
          {hostTypes.map((d, index) =>
            <Option value={d.value} key={index}>{d.label}</Option>
          )}
        </Select>
      )
    },
    {
      type: 'custom',
      title: '新增时间',
      dataIndex: 'hostCreateTime', // hostCreateTimeStart hostCreateTimeEnd
      component: (
        <RangePicker showTime={{
          defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],
        }} style={{ width: '100%' }} />
      ),
    },
  ];
  return queryFormColumns;
}

export const getAgentListColumns = (cb: any, drawer: any, getData: any) => {
  const agentListColumns = [{
    title: '主机名',
    dataIndex: 'hostName',
    key: 'hostName',
    width: '10%',
    onCell: () => ({
      style: { ...cellStyle, maxWidth: 130, },
    }),
    sorter: (a: IAgentHostSet, b: IAgentHostSet) => a.hostName?.charCodeAt(0) - b.hostName?.charCodeAt(0),
    render: (text: string, record: IAgentHostSet) => (
      <NavRouterLink needToolTip element={text} href={`/detail`} state={{ hostId: `${record.hostId}`, agentId: `${record.agentId || ''}` }} />
    ),
  }, {
    title: '主机IP',
    dataIndex: 'ip',
    key: 'ip',
    width: '10%',
  }, {
    title: '主机类型',
    dataIndex: 'container',
    key: 'container',
    width: '9%',
    sorter: (a: IAgentHostSet, b: IAgentHostSet) => a.container - b.container,
    render: (t: number) => hostTypeMap[t],
  }, {
    title: '承载应用',
    dataIndex: 'serviceList',
    key: 'serviceList',
    width: '10%',
    onCell: () => ({
      style: { ...cellStyle, maxWidth: 130, },
    }),
    render: (text: IService[]) => {
      const servicenames = text.map((ele: IService) => ele.servicename)
      return (<>{renderTooltip(servicenames?.join(','))}</>)
    }
  }, {
    title: 'Agent版本号',
    dataIndex: 'agentVersion',
    key: 'agentVersion',
    width: '10%',
    render: (text: string) => <>{text ? text : '未安装'}</>,
  }, {
    title: 'Agent健康度',
    dataIndex: 'agentHealthLevel',
    key: 'agentHealthLevel',
    width: '14%',
    sorter: (a: IAgentHostSet, b: IAgentHostSet) => a.agentHealthLevel - b.agentHealthLevel,
    render: (t: number, record: IAgentHostSet) => {
      return (<>
        <Tag color={healthMap[t]}>{healthMap[t]}</Tag>
        {(t === 0 || t === 1) && <Tooltip placement="topLeft" title={'更多功能请关注商业版'}>
          <span><a style={{ color: '#00000040' }}>诊断报告</a></span>
        </Tooltip>}
      </>);
    },
  }, {
    title: '所属机房',
    dataIndex: 'machineZone',
    key: 'machineZone',
    width: '9%',
    onCell: () => ({
      style: { ...cellStyle, maxWidth: 130, },
    }),
    sorter: (a: IAgentHostSet, b: IAgentHostSet) => a.machineZone?.charCodeAt(0) - b.machineZone?.charCodeAt(0),
    render: (t: string) => renderTooltip(t),
  }, {
    title: '新增时间',
    dataIndex: 'hostCreateTime',
    key: 'hostCreateTime',
    width: '10%',
    sorter: (a: IAgentHostSet, b: IAgentHostSet) => a.hostCreateTime - b.hostCreateTime,
    render: (t: number) => moment(t).format(timeFormat),
  },
  {
    title: '操作',
    width: '18%',
    render: (text: any, record: IAgentHostSet) => {
      const btns = getAgentListBtns(record, cb, getData);
      return renderOperationBtns(btns, record);
    },
  },
  ];
  return agentListColumns;
};

const handleHost = (cb: any, getData: any, taskType: number, host: IAgentHostSet) => {
  const hosts = [];
  hosts.push(host);
  cb('InstallHost', {
    taskType,
    hosts,
    cb: () => getData,
  });
}

export const getAgentListBtns = (record: IAgentHostSet, cb: any, getData: any): IBtn[] => [{
  label: '编辑',
  clickFunc: (record: IAgentHostSet) => {
    const params = { hostObj: record, getData }
    cb('ModifyHost', params);
  },
}, {
  label: '安装',
  invisible: !!record.agentId,
  needTooltip: true,
  clickFunc: (record: IAgentHostSet) => {
    handleHost(cb, getData, 0, record);
  },
}, {
  label: '升级',
  invisible: !record.agentId,
  needTooltip: true,
  clickFunc: (record: IAgentHostSet) => {
    handleHost(cb, getData, 2, record);
  },
}, {
  label: '卸载',
  invisible: !record.agentId,
  needTooltip: true,
  clickFunc: (record: IAgentHostSet) => {
    const agentIds = [];
    agentIds.push(record.agentId);
    getTaskExists(JSON.stringify(agentIds)).then((res: boolean) => {
      if (res) {
        return Modal.confirm({
          title: <a className='fail'>选中agent有采集任务正在运行，需要操作将会中断采集，是否继续？</a>,
          onOk: () => uninstallHostModal(getData, record, res),
        });
      }
      return uninstallHostModal(getData, record, res);
    }).catch((err: any) => {
      // console.log(err);
    });
  },
},
{
  label: '删除主机',
  clickFunc: (record: IAgentHostSet) => {
    judgeDeleteHost(record, getData);
  },
}
];


export const uninstallHostModal = (getData: any, record: IAgentHostSet, check: boolean) => {
  Modal.confirm({
    title: `确认卸载选中Agent吗？`,
    content: <a className="fail">卸载操作不可恢复，请谨慎操作！</a>,
    onOk: () => uninstallHost(getData, record, check),
  });
}

export const uninstallHost = (getData: any, record: IAgentHostSet, check: boolean) => {
  const params = {
    agentIds: [record.agentId],
    checkAgentCompleteCollect: check ? 1 : 0, // 1检查 0 不检查
    agentVersionId: '',
    hostIds: [],
    taskType: 1,
  } as unknown as IOperationTasksParams;
  createOperationTasks(params).then((res: number) => {
    Modal.success({
      title: <><a href="/agent/operationTasks">{record?.hostName}卸载Agent任务(任务ID：{res}）</a>创建成功！</>,
      content: '可点击标题跳转，或至“Agent中心”>“运维任务”模块查看详情',
      okText: '确认',
      onOk: () => getData(),
    });
  }).catch((err: any) => {
    // console.log(err);
  });
}


//有agent不能删，
//有容器不能删除
const judgeDeleteHost = (record: IAgentHostSet, getData: any) => {
  confirm({
    title: record.agentId ? '该主机已安装Agent，请先卸载' : `是否确认删除${record.hostName}？`,
    content: <span className="fail">{!record.agentId && '删除操作不可恢复，请谨慎操作！'}</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      !record.agentId && deleteHost(record.hostId, 0).then((res: any) => { // 0：不忽略数据未采集完 1：忽略数据未采集完 
        // 删除主机 0：删除成功 
        // 10000：参数错误 ==> 不可删除
        // 23000：待删除主机在系统不存在 ==> 不可删除
        // 23004：主机存在关联的容器导致主机删除失败 ==> 不可删除
        // 22001：Agent存在未采集完的日志 ==> 不可能存在这种情况
        if (res.code === 0) {
          Modal.success({ content: '删除成功！' });
          getData();
        } else if (res.code === 10000 || res.code === 23000 || res.code === 23004) {
          Modal.error({ content: res.message });
        }
      });
    },
  });
}

const getDiagnosisReport = (drawer: any, record: any) => {
  drawer('DiagnosisReport', record);
}

export const hostDetailBaseInfo = (info: IAgentHostSet) => {
  const hostDetailList: IBaseInfo[] = [{
    label: '主机IP',
    key: 'ip',
  }, {
    label: '主机类型',
    key: 'container',
    render: (t: number) => hostTypeMap[t],
  }, {
    label: '宿主机名',
    key: 'parentHostName',
    invisible: info?.container === 0,
  },
  // {
  //   label: 'Agent版本名',
  //   key: 'agentVersion',
  // }, 
  // {
  //   label: '已开启日志采集任务数',
  //   key: 'openedLogCollectTaskNum',
  // }, {
  //   label: '已开启日志采集路径数',
  //   key: 'openedLogPathNum',
  // }, 
  {
    label: '最近 agent 启动时间',
    key: 'lastestAgentStartupTime',
    render: (t: number) => moment(t).format(timeFormat),
  },
  // {
  //   label: '所属机房',
  //   key: 'machineZone',
  // }, 
  {
    label: '新增时间',
    key: 'hostCreateTime',
    render: (t: number) => moment(t).format(timeFormat),
  }];
  return hostDetailList;
}
export const getCollectTaskConfig = (drawer: any, recoud: any) => {
  console.log(recoud, 'recoud')
  const collectTaskConfig: any = [
    {
      title: '采集任务ID',
      dataIndex: 'clusterId',
      key: 'clusterId',
      align: 'center',
    }, {
      title: '采集路径ID',
      dataIndex: 'pathId',
      key: 'pathId',
      align: 'center',
    }, {
      title: '主文件名',
      dataIndex: 'masterFile',
      key: 'masterFile',
      align: 'center',
    }, {
      title: '当前采集流量 & 条数/30s-',
      dataIndex: 'sendByte',
      key: 'sendByte',
      align: 'center',
      render: (text: any, record: any) => {
        return `${text}&${record.sendCount}`
      }
    }, {
      title: '当前最大延迟',
      dataIndex: 'maxTimeGap',
      key: 'maxTimeGap',
      align: 'center',
    }, {
      title: '当前采集时间',
      dataIndex: 'logTime',
      key: 'logTime',
      align: 'center',
      width: 160,
      render: (t: number) => moment(t).format(timeFormat),
    }, {
      title: '文件最近修改时间',
      dataIndex: 'lastModifyTime',
      key: 'lastModifyTime',
      width: 160,
      align: 'center',
      render: (text: any, record: any) => {
        const collectFilesSort = record?.collectFiles.sort((a: any, b: any) => b.lastModifyTime - a.lastModifyTime)
        return moment(collectFilesSort[0].lastModifyTime).format(timeFormat)
      }
    }, {
      title: '限流时长/30s',
      dataIndex: 'limitTime',
      key: 'limitTime',
      align: 'center',
    }, {
      title: '异常截断条数/30s',
      dataIndex: 'filterTooLargeCount',
      key: 'filterTooLargeCount',
      align: 'center',
    }, {
      title: '文件是否存在',
      dataIndex: 'fileExist',
      key: 'fileExist',
      align: 'center',
      render: (t: any) => {
        return t ? '是' : '否'
      },
    }, {
      title: '文件是否存在乱序',
      dataIndex: 'isFileOrder',
      key: 'isFileOrder',
      align: 'center',
      render: (t: any, recoud: any) => {
        const isFileOrder = recoud?.collectFiles && recoud?.collectFiles.filter((item: any) => !item.isFileOrder).length
        return isFileOrder ? '是' : '否'
      },
    }, {
      title: '文件是否存在日志切片错误-',
      dataIndex: 'validTimeConfig',
      key: 'validTimeConfig',
      align: 'center',
      render: (t: any, recoud: any) => {
        const validTimeConfig = recoud?.validTimeConfig && recoud?.validTimeConfig.filter((item: any) => !item.validTimeConfig).length
        return validTimeConfig ? '是' : '否'
      }
    }, {
      title: '文件过滤量/30s',
      dataIndex: 'filterOut',
      key: 'filterOut',
      align: 'center',
    }, {
      title: '近一次心跳时间',
      dataIndex: 'heartbeatTime',
      key: 'heartbeatTime',
      align: 'center',
      width: 160,
      render: (t: number) => moment(t).format(timeFormat),
    }, {
      title: '采集状态',
      dataIndex: 'taskStatus',
      key: 'taskStatus',
      align: 'center',
      render: (text: any) => {
        const status: any = {
          0: '停止',
          1: '运行中',
          2: '完成'
        }
        return status[text]
      }
    }, {
      title: '采集文件信息',
      dataIndex: 'collectFiles',
      key: 'collectFiles',
      // fixed: 'right',
      align: 'center',
      width: 120,
      render: (text: any, record: any) => {
        console.log(text, 'test')
        return <div>
          <span>共{text.length}个</span><a style={{ display: 'inline-block', marginLeft: '15px' }} onClick={() => {
            drawer('CollectFileInfoDetail', text)
          }}>查看</a>
        </div>
      }
    }, {
      title: '近一次指标详情',
      dataIndex: 'MetricDetail',
      key: 'MetricDetail',
      align: 'center',
      // fixed: 'right',
      width: 120,
      render: (text: any, record: any) => {
        return <div>
          <span>共1个</span><a style={{ display: 'inline-block', marginLeft: '15px' }} onClick={() => {
            drawer('MetricDetail', record)
          }}>查看</a>
        </div>
      }
    },
  ]
  return collectTaskConfig
}
