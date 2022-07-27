/* eslint-disable react/no-children-prop */
import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Row,
  Button,
  Select,
  AutoComplete,
  Tooltip,
  Upload,
  IconFont,
  Transfer,
  Checkbox,
  AppContainer,
  Col,
  Radio,
  Space,
  ProTable,
  Modal,
} from '@didi/dcloud-design';
import ChartContainer from '../../components/chart-container';
import { Link } from 'react-router-dom';
import DragItem from '../../components/DragItem';
import { SearchOutlined } from '@ant-design/icons';
import { Drawer, notification } from '@didi/dcloud-design';
import { getAgent, getServices } from '../../api/agent';
import { request } from '../../request/index';
const hostTypes = [
  {
    value: 0,
    label: '物理机',
  },
  {
    value: 1,
    label: '容器',
  },
];

export const taskHealthMap = {
  0: 'green',
  1: 'yellow',
  2: 'red',
};

export const healthTypes = [
  {
    value: 0,
    label: 'red',
  },
  {
    value: 1,
    label: 'yellow',
  },
  {
    value: 2,
    label: 'green',
  },
];

export const taskhealthTypes = [
  {
    value: 0,
    label: 'green',
  },
  {
    value: 1,
    label: 'yellow',
  },
  {
    value: 2,
    label: 'red',
  },
];

const { useGlobalValue } = AppContainer;
const { Option } = Select;
const { TextArea } = Input;
const regName = /^([-_.a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制
const regIp = /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/; // 支持9位数字，一共12位
const regString128 = /^[\s\S]{0,128}$/; // 任意字符最大输入长度128位
const regAdress = /^([-_.:,a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制
const regProducerName = /^[\s\S]{1,1024}$/; // 任意字符最大输入长度为1024位

export const getHosts = () => {
  return request('/api/v1/normal/host/list');
};

export const getHealthSolve = (params: any) => {
  return request('/api/v1/normal/log-collect-task/health', {
    method: 'PUT',
    data: JSON.stringify(params),
  });
};

export const deleteCollectTask = (logCollectTaskId: number) => {
  return request(`/api/v1/normal/collect-task/${logCollectTaskId}`, {
    method: 'DELETE',
  });
};

export const switchCollectTask = (params: any) => {
  return request(`/api/v1/normal/collect-task/switch?logCollectTaskId=${params.logCollectTaskId}&status=${params.status}`);
};

export const getPathHostNameList = (params: any) => {
  return request(
    `/api/v1/normal/log-collect-task/health/error-detail/path-host/${params.logCollectTaskId}/${params.logCollectTaskHealthInspectionCode}`
  );
};
export const getLogCollectTaskHealthInspectionCodeList = (params: any) => {
  return request(
    `/api/v1/normal/log-collect-task/health/error-detail?logCollectTaskId=${params.logCollectTaskId}&pathId=${params.pathId}&hostName=${params.hostName}&logCollectTaskHealthInspectionCode=${params.logCollectTaskHealthInspectionCode}`
  );
};
//

// 采集任务指标看板
const menuLists = [
  {
    name: 'Agent',
    key: '1', // 固定
    url: '/api/v1/normal/metrics/2',
  },
];

export const Containers = (props: any): JSX.Element => {
  console.log(props, 'Containers');
  const [isGroup, setIsgroup] = useState(true);
  const [menuList, setMenuList] = useState<any[]>(menuLists);
  useEffect(() => {
    setTimeout(() => {
      // const list = [
      //   {
      //     name: "Agent",
      //     key: '0', // 固定
      //     url: ''
      //   }
      // ];
      // setMenuList(list);
      // setIsgroup(true);
    }, 2000);
  }, []);

  return (
    <div className="test-chartcontain">
      <ChartContainer
        isGold={true}
        filterData={{
          // hostName: '主机名',
          logCollectTaskId: props.containerData.logCollectTaskId,
          // pathId: '采集路径id',
        }}
        reloadModule={{
          reloadIconShow: true,
          lastTimeShow: true,
        }}
        dragModule={{
          dragItem: <DragItem></DragItem>,
          requstUrl: '/api/v1/normal/metrics/metric',
          isGroup: isGroup,
        }}
        indicatorSelectModule={{
          hide: true,
          menuList,
        }}
      ></ChartContainer>
      <div style={{ width: '100%', display: 'block', height: '56px', backgroundColor: '#ffffff' }} />
      <div
        style={{
          position: 'fixed',
          bottom: 0,
          width: '1080px',
          display: 'flex',
          alignItems: 'center',
          height: '56px',
          backgroundColor: '#ffffff',
          zIndex: 1999,
          paddingLeft: '20px',
          // borderTop: '1px solid #CED4DA',
          boxShadow: '0px 1px 10px #CED4DA',
        }}
      >
        <Button type={'primary'} icon={<IconFont type="icon-tishi" />}>
          <Link
            to={{
              pathname: '/monitor/metric',
              state: {
                logCollectTaskId: props.containerData?.logCollectTaskId,
              },
            }}
            style={{ color: '#ffffff' }}
          >
            前往采集任务指标看板
          </Link>
        </Button>
      </div>
    </div>
  );
};
// 采集任务诊断
const getColmns = (solveClick) => {
  const columns = [
    {
      title: '任务id ',
      dataIndex: 'logCollectTaskId',
      key: 'logCollectTaskId',
    },
    {
      title: '路径',
      dataIndex: 'path',
      key: 'path',
    },
    {
      title: '主机名',
      dataIndex: 'hostName',
      key: 'hostName',
    },
    {
      title: 'IP',
      dataIndex: 'ip',
      key: 'ip',
    },
    {
      title: '日志时间',
      dataIndex: 'logTime',
      key: 'logTime',
    },
    {
      title: '文件信息',
      dataIndex: 'collectFiles',
      key: 'collectFiles',
      width: 200,
    },
    {
      title: '心跳时间',
      dataIndex: 'heartbeatTime',
      key: 'heartbeatTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      key: 'option',
      render: (t, r) => <a onClick={() => solveClick(r)}>解决</a>,
    },
  ];
  return columns;
};

const collectFaultMap = {
  2: '采集路径对应的待采集文件在主机上不存在',
  3: '采集路径对应的待采集文件在主机上存在乱序',
  4: '采集任务，采集路径，主机在存在心跳周期内不存在心跳',
  5: '采集路径对应的待采集文件在主机上存在数据过长导致截断行为',
};
export const DiagnosisContent = (props: any) => {
  const { containerData } = props;
  const [form] = Form.useForm();
  const [solveData, setSolveData] = useState([]);
  const [pathHostNameList, setPathHostNameList] = useState([]);
  const [hostNameList, setHostNameList] = useState([]);
  const [pathId, setPathId] = useState<any>(null);
  const [hostName, setHostName] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  // 获取故障信息列表
  const getSolveListData = (op?: any, toast?: boolean) => {
    setLoading(true);
    getLogCollectTaskHealthInspectionCodeList(op)
      .then((res: any) => {
        setSolveData(res);
        setLoading(false);
        toast &&
          notification.success({
            message: '成功',
            duration: 3,
          });
      })
      .catch((err) => {
        setLoading(false);

        // setSolveData(mockData);
      });
  };

  const solveClick = (record) => {
    getHealthSolve({
      logCollectTaskMetricId: record.logCollectTaskMetricId,
      logCollectTaskHealthInspectionCode: containerData.logCollectTaskHealthInspectionResultType,
    })
      .then((res) => {
        getSolveListData(
          {
            logCollectTaskId: containerData.logCollectTaskId,
            logCollectTaskHealthInspectionCode: containerData.logCollectTaskHealthInspectionResultType,
            pathId,
            hostName,
          },
          true
        );
      })
      .catch((err) => {
        notification.error({
          message: '失败',
          duration: 3,
        });
      });
    console.log(record, 'records');
  };

  const onPathSelect = (e) => {
    console.log(e);
    setPathId(e);
    form.resetFields(['hostname']);
    const newHostNameList = pathHostNameList.filter((item) => item.pathId == Number(e) || (item.pathId == 0 && Number(e) == 0));
    console.log(newHostNameList, 'newHostNameList');
    setHostNameList(newHostNameList[0]?.hostNameList);
  };

  const onHostNameSelect = (e) => {
    console.log(e);
    setHostName(e);
    getSolveListData({
      logCollectTaskId: containerData.logCollectTaskId,
      logCollectTaskHealthInspectionCode: containerData.logCollectTaskHealthInspectionResultType,
      pathId,
      hostName: e,
    });
  };

  const getPathHostNameListInfo = () => {
    getPathHostNameList({
      logCollectTaskHealthInspectionCode: containerData.logCollectTaskHealthInspectionResultType,
      logCollectTaskId: containerData.logCollectTaskId,
    }).then((res: any) => {
      setPathHostNameList(res);
    });
  };

  useEffect(() => {
    getPathHostNameListInfo();
  }, [props]);

  return (
    <>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          // justifyContent: 'space-between',
          height: '40px',
          backgroundColor: '#F9F9FA',
          lineHeight: '40px',
          padding: '0 20px',
          fontSize: '13px',
          color: '#353A40',
          fontWeight: 500,
        }}
      >
        <span>故障原因:</span>
      </div>
      <div style={{ padding: '20px 30px' }}>{props?.containerData?.logCollectTaskHealthDescription}</div>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          height: '40px',
          backgroundColor: '#F9F9FA',
          lineHeight: '40px',
          padding: '0 20px',
          fontSize: '13px',
          color: '#353A40',
          fontWeight: 500,
        }}
      >
        <span>解决路径:</span>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            fontSize: '13px',
          }}
        >
          <Form form={form} layout={'inline'}>
            <Form.Item name="pathId" label={'Path'}>
              <Select onChange={onPathSelect} style={{ width: '150px' }} placeholder={'请选择'} suffixIcon={<IconFont type="icon-xiala" />}>
                {pathHostNameList.map((item, key) => {
                  return (
                    <Option key={key} value={item.pathId}>
                      {item.path}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>
            {console.log(hostNameList, 'hostNameList')}
            <Form.Item name="hostname" label={'HostName'}>
              <Select
                onChange={onHostNameSelect}
                style={{ width: '150px' }}
                placeholder={'请选择'}
                suffixIcon={<IconFont type="icon-xiala" />}
              >
                {(pathId || pathId == 0) &&
                  hostNameList.length > 0 &&
                  hostNameList.map((item, key) => {
                    return (
                      <Option key={key} value={item}>
                        {item}
                      </Option>
                    );
                  })}
              </Select>
            </Form.Item>
          </Form>
          {/* <Input
            placeholder={'请输入主机名/IP'}
            style={{ width: '233px', height: '27px', padding: '0 11px' }}
            // onChange={(e) => searchTrigger === 'change' && submit(e.target.value)}
            onPressEnter={onSearch}
            // onBlur={(e: any) => searchTrigger === 'blur' && submit(e.target.value)}
            suffix={<SearchOutlined style={{ color: '#ccc' }} />}
          /> */}
        </div>
      </div>
      <div style={{ padding: '20px' }}>
        <ProTable
          isCustomPg={true}
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'path',
            loading: loading,
            columns: getColmns(solveClick),
            dataSource: solveData,
            // paginationProps: { ...pagination },
            // attrs: {
            //   // className: 'frameless-table', // 纯无边框表格类名
            //   // bordered: true,   // 表格边框
            //   onChange: onTableChange,
            // },
          }}
        />
      </div>
    </>
  );
};

//删除采集任务
export const deleteHostClick = (props: any) => {
  Modal.confirm({
    title: '采集任务被删除后将不再继续采集，是否确认？',
    content: <span className="fail">删除操作不可恢复，请谨慎操作！</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      deleteCollectTask(props.logCollectTaskId)
        .then((res: any) => {
          Modal.success({
            content: '删除成功！',
            okText: '确认',
            onOk: () => {
              props.genData();
            },
          });
        })
        .catch((err: any) => {
          // console.log(err);
        });
    },
  });
};
// 采集任务暂停、继续
export const switchTask = (props: any) => {
  Modal.confirm({
    title: `${props.logCollectTaskStatus === 0 ? '继续' : '暂停'}`,
    content: <span className="fail">{`是否确认${props.logCollectTaskStatus === 0 ? '继续' : '暂停'}采集任务？`}</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      const params = {
        logCollectTaskId: props.logCollectTaskId,
        status: props.logCollectTaskStatus === 0 ? 1 : 0,
      } as any;
      switchCollectTask(params)
        .then((res: any) => {
          Modal.success({
            content: `${props.logCollectTaskStatus === 0 ? '继续' : '暂停'}成功！`,
            okText: '确认',
            onOk: () => {
              props.genData();
            },
          });
        })
        .catch((err: any) => {
          // console.log(err);
        });
    },
  });
};

export const showDiagnosisContent = (props: any) => {
  props.history.push({ pathname: '/collect/detail', state: { taskId: props.logCollectTaskId } });
};

export const addCollectTask = (props: any) => {
  props.history.push({ pathname: '/collect/add-task' });
};

export const editCollectTask = (props: any) => {
  props.history.push({ pathname: '/collect/add-task', state: { taskId: props.logCollectTaskId } });
};

export const servicesList = async () => {
  const ServicesList: any = await getServices();
  return ServicesList.map((item) => {
    return { title: item.servicename, value: item.id };
  });
};

export const collectStatusList = () => {
  return [
    { title: '暂停', value: 0 },
    { title: '运行', value: 1 },
    { title: '已完成', value: 2 },
  ];
};

// 主机
export const healthList = () => {
  return taskhealthTypes.map((item) => {
    return { title: item.label, value: item.value };
  });
};

// 采集任务健康度列
const logCollectTaskHealthInspectionCode = [2, 3, 4, 5];

export const logCollectTaskHealthLevel = (record, value) => {
  // record.logCollectTaskHealthLevel 为 0 时，record.logCollectTaskHealthLevel 判断结果为 false
  // 故需增加 record.logCollectTaskHealthLevel === 0 的判断
  return record.logCollectTaskHealthLevel || record.logCollectTaskHealthLevel === 0 ? (
    record.logCollectTaskHealthLevel == 2 || record.logCollectTaskHealthLevel === 1 ? (
      <Tooltip title={record.logCollectTaskHealthDescription}>
        <span>
          <span style={{ fontSize: '20px' }}>
            <IconFont type={record.logCollectTaskHealthLevel === 1 ? 'icon-huang' : 'icon-hong'} />
          </span>
          {logCollectTaskHealthInspectionCode.includes(record.logCollectTaskHealthInspectionResultType) && (
            <a style={{ marginLeft: '10px' }} onClick={() => value.clickFunc(record, value)}>
              故障修复
            </a>
          )}
        </span>
      </Tooltip>
    ) : (
      <span>
        <span style={{ fontSize: '20px' }}>
          <IconFont type={'icon-lv'} />
        </span>
        {logCollectTaskHealthInspectionCode.includes(record.logCollectTaskHealthInspectionResultType) && (
          <a style={{ marginLeft: '10px' }} onClick={() => value.clickFunc(record, value)}>
            故障修复
          </a>
        )}
      </span>
    )
  ) : (
    '-'
  );
};

export const logCollectTaskStatus = (record, value) => {
  const status = record.logCollectTaskStatus;
  return status == 0 ? '暂停' : status == 1 ? '运行' : status == 2 ? '已完成' : '-';
};

// 黄金指标标题 + 健康度自定义Title
export const HealthMap = (props: any) => {
  console.log('HealthMap', props);
  return (
    <div>
      <span style={{ marginRight: '10px' }}>{props?.logCollectTaskName || ''}</span>
      <span>
        {props.logCollectTaskHealthLevel ? (
          props.logCollectTaskHealthLevel === 2 || props.logCollectTaskHealthLevel === 1 ? (
            <span>
              <span style={{ fontSize: '20px' }}>
                <IconFont type={props.logCollectTaskHealthLevel === 1 ? 'icon-huang' : 'icon-hong'} />
              </span>
              {/* <a style={{ marginLeft: '20px' }} onClick={() => props.clickFunc(props, props)}>
                故障修复
              </a> */}
            </span>
          ) : (
            <IconFont type="icon-lv" />
          )
        ) : (
          ''
        )}
      </span>
    </div>
  );
};

export default { servicesList, collectStatusList, switchTask, healthList, logCollectTaskHealthLevel, HealthMap };
