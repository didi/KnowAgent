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
  Utils,
  AppContainer,
  Modal,
  message,
  IconFont,
  Divider,
  InputNumber,
  Collapse,
  Col,
  ProTable,
  notification,
} from '@didi/dcloud-design';
import { arrayMoveImmutable } from 'array-move';
// import { Imenu } from '../index';
import moment from 'moment';
import { Link, withRouter } from 'react-router-dom';
import ChartContainer from '../../components/chart-container';
import DragItem from '../../components/DragItem';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import { getAgent, getServices } from '../../api/agent';
import { request } from '../../request/index';
import './index.less';

const hostTypes = [
  {
    value: 0,
    label: '物理机',
  },
];

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
const { useGlobalValue } = AppContainer;
const { Option } = Select;
const { TextArea } = Input;
const { Panel } = Collapse;
const regName = /^([-_.a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制
const regIp = /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/; // 支持9位数字，一共12位
const regString128 = /^[\s\S]{0,128}$/; // 任意字符最大输入长度128位
const regAdress = /^([-_.:,a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制
const regProducerName = /^[\s\S]{1,1024}$/; // 任意字符最大输入长度为1024位

export const getHosts = () => {
  return request('/api/v1/normal/host/list');
};

export const getAgentVersion = () => {
  return request('/api/v1/rd/version/list');
};

export const testHost = (hostname: string) => {
  return request(`/api/v1/rd/host/connectivity/${hostname}`);
};

export const addOpHosts = (params: any) => {
  console.log(params, 'params');
  return request('/api/v1/op/hosts', {
    method: 'POST',
    data: JSON.stringify(params),
    needDuration: true,
  });
};

export const getHostMachineZone = () => {
  return request('/api/v1/normal/host/machine-zones');
};

export const judgeEmpty = (value: any) => {
  return value === undefined || value === null || value === '' ? '' : value;
};

export const getTaskExists = (agentIdListJsonString: string) => {
  return request(`/api/v1/op/agent/collect-task-exists?agentIdListJsonString=${agentIdListJsonString}`);
};

export const createOperationTasks = (params: any) => {
  return request('/api/v1/op/operation-tasks', {
    method: 'POST',
    data: JSON.stringify(params),
  });
};

export const editOpHosts = (params: any) => {
  return request('/api/v1/op/hosts', {
    method: 'PUT',
    data: JSON.stringify(params),
  });
};

export const getHostDetails = (hostId: number) => {
  return request(`/api/v1/rd/host/${hostId}`);
};

export const getAgentDetails = (agentId: number) => {
  return request(`/api/v1/rd/agent/${agentId}`);
};

export const getReceiversTopic = (receiverId: number) => {
  return request(`/api/v1/normal/receivers/${receiverId}/topics`);
};

export const getReceivers = () => {
  return request('/api/v1/normal/receivers/list');
};

export const editOpAgent = (params: any) => {
  return request('/api/v1/op/agent', {
    method: 'PUT',
    data: JSON.stringify(params),
  });
};
export const deleteAgentAPI = (agentId: number) => {
  return request(`/api/v1/op/agent/${agentId}`, {
    method: 'DELETE',
  });
};

export const deleteHost = (hostId: number, ignoreUncompleteCollect: number) => {
  return request(`/api/v1/op/hosts/${hostId}?ignoreUncompleteCollect=${ignoreUncompleteCollect}`, {
    method: 'DELETE',
    init: {
      needCode: true,
    },
  });
};

export const NewHostForm: React.FC = (props: any) => {
  const [hostType, setHostTypes] = useState(null);
  const [machineZones, setMachineZones] = useState([]);
  const [suHostList, setSuHostList] = useState([]);
  const [testLoading, setTestLoading] = useState(false);
  const [testHide, setTestHide] = useState(false);
  const [testResult, setTestResult] = useState(false);
  const getMachineZonesList = () => {
    const zonesList = machineZones.map((ele: string) => {
      return { value: ele, text: ele };
    });
    return zonesList;
  };

  const changeHostType = (val: any) => {
    setHostTypes(val);
    getSuHostlist();
  };

  const getSuHostlist = () => {
    getHosts()
      .then((res: any[]) => {
        const data = res.filter((ele) => ele.container === 0);
        setSuHostList(data);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const connectTestHost = () => {
    props.form.validateFields().then((res) => {
      console.log(res, 'hostName');
      getTestHost(res.hostName);
    });
  };

  const getTestHost = (hostName: string) => {
    setTestLoading(true);
    testHost(hostName)
      .then((res: any) => {
        setTestHide(true);
        setTestLoading(false);
        setTestResult(true);
      })
      .catch((err: any) => {
        setTestHide(true);
        setTestLoading(false);
        setTestResult(false);
      });
  };

  const getMachineZones = () => {
    getHostMachineZone()
      .then((res: string[]) => {
        const zones = res.filter((ele) => !!judgeEmpty(ele));
        setMachineZones(zones);
      })
      .catch((err: any) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getMachineZones();
  }, []);

  useEffect(() => {
    if (props.submitEvent !== 1) {
      props.form.validateFields().then((values) => {
        const addParams = {
          container: values?.container,
          hostName: values?.hostName,
          ip: values?.ip,
          parentHostName: values?.parentHostName || '',
          machineZone: values?.machineZone || '',
          department: '',
          id: '',
        };
        console.log(addParams, 'addParams');
        addOpHosts(addParams)
          .then((res: any) => {
            props.genData(); // 调用genData,但是没有传入此项
            props.setVisible(false);
            Modal.success({
              content: '新增成功！',
              okText: '确认',
            });
          })
          .catch((err: any) => {
            // console.log(err);
          });
        // getTestHost(res.hostName);
        // props.setVisible(false);
      });
    }
  }, [props.submitEvent]);

  return (
    <div style={{ padding: '0 24px' }}>
      <Form
        form={props.form}
        name="newHostForm"
        // labelCol={{ span: 4 }}
        // wrapperCol={{ span: 20 }}
        layout="vertical"
      >
        <Form.Item label="主机类型" name="container" rules={[{ required: true, message: '请选择' }]}>
          <Select showSearch placeholder="请选择" onChange={changeHostType}>
            {hostTypes.map((v, index) => (
              <Option key={index} value={v.value}>
                {v.label}
              </Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          label="主机名"
          name="hostName"
          rules={[
            {
              required: true,
              message: '请输入主机名，支持大小写中英文字母、数字、下划线、点、短横线,32位限制',
              validator: (_, v, c) => {
                if (!!v && new RegExp(regName).test(v)) {
                  c();
                  return;
                }
                c('请输入主机名，支持大小写中英文字母、数字、下划线、点、短横线,32位限制');
              },
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
        <Form.Item
          label="主机IP"
          name="ip"
          rules={[
            {
              required: true,
              message: '请输入正确IP地址',
              validator: (_, v, c) => {
                if (!!v && new RegExp(regName).test(v)) {
                  c();
                  return;
                }
                c('请输入正确IP地址');
              },
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
        {hostType === 1 ? ( // 选择容器时，接口则需筛选展示主机数据
          <Form.Item label="宿主机名" name="parentHostName" rules={[{ required: true, message: '请输入' }]}>
            <Select placeholder="请选择">
              {suHostList.map((v: any, index: number) => (
                <Option key={index} value={v.hostName}>
                  {v.hostName.length > 15 ? (
                    <Tooltip placement="bottomLeft" title={v.hostName}>
                      {v.hostName}
                    </Tooltip>
                  ) : (
                    v.hostName
                  )}
                </Option>
              ))}
            </Select>
          </Form.Item>
        ) : null}
        <Row style={{ alignItems: 'center' }}>
          <Button htmlType="submit" type="primary" loading={testLoading} onClick={connectTestHost}>
            连接测试
          </Button>
          &nbsp;&nbsp;
          {testHide && (
            <span
              style={{ color: testResult ? '#2fc25b' : '#f5222d' }}
              // className={testResult ? 'success' : 'fail'}
            >
              {testResult ? '测试成功！' : '测试失败！'}
            </span>
          )}
        </Row>
      </Form>
    </div>
  );
};

// const agentVersions = [
//   {
//     key: 1,
//     agentVersion: '1.0.1',
//     agentVersionId: '999999',
//   },
//   {
//     key: 2,
//     agentVersion: '1.0.2',
//     agentVersionId: '999998',
//   },
// ];

// 选中的Agent没有采集任务时候调用（安装Agent、升级Agent）
export const InstallHostForm: React.FC = (props: any) => {
  console.log(props, '--InstallHostForm--');
  const [agentVersions, setAgentVersions] = useState([] as any[]);
  const getAgentVersionData = () => {
    getAgentVersion()
      .then((res: any[]) => {
        setAgentVersions(res);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  useEffect(() => {
    getAgentVersionData();
  }, []);

  useEffect(() => {
    if (props.submitEvent !== 1) {
      props.form.validateFields().then((res) => {
        console.log(res, '子组件触发父组件的确认标识');
        // getTestHost(res.hostName);
        // props.setVisible(false);
      });
    }
  }, [props.submitEvent]);

  return (
    <Form
      form={props.form}
      name="InstallHostForm"
      // labelCol={{ span: 4 }}
      // wrapperCol={{ span: 20 }}
      layout="vertical"
    >
      <Form.Item label="Agent版本" name="agentVersionId" rules={[{ required: true, message: '请选择' }]}>
        <Select showSearch placeholder="请选择">
          {agentVersions?.map((v, index) => (
            <Option key={index} value={v.agentVersionId}>
              {v.agentVersion}
            </Option>
          ))}
          {/* {props.agentVersions?.map((v, index) => (
            <Option key={index} value={v.agentVersionId}>
              {v.agentVersion}
            </Option>
          ))} */}
        </Select>
      </Form.Item>
    </Form>
  );
};

// 卸载
// export const

export const openUninstallHost = (props: any) => {
  const { agentIds = [1] } = props;
  getTaskExists(JSON.stringify(agentIds))
    .then((res: boolean) => {
      if (!res) {
        return Modal.confirm({
          title: <a className="fail">选中agent有采集任务正在运行，需要操作将会中断采集，是否继续？</a>,
          onOk: () => uninstallHostModal({ check: res, props }),
        });
      }
      return uninstallHostModal({ check: res, props });
    })
    .catch((err: any) => {
      // console.log(err);
    });
};

const uninstallHostModal = (props: any) => {
  Modal.confirm({
    title: `确认卸载选中Agent吗？`,
    content: <a className="fail">卸载操作不可恢复，请谨慎操作！</a>,
    onOk: () => uninstallHost(props),
  });
};

const uninstallHost = (props) => {
  const { agentIds, installedHosts, check } = props;
  const params = {
    agentIds,
    checkAgentCompleteCollect: check ? 1 : 0, // 1检查 0 不检查
    agentVersionId: '',
    hostIds: [],
    taskType: 1,
  };
  createOperationTasks(params)
    .then((res: number) => {
      Modal.success({
        title: (
          <>
            <a href="/agent/operationTasks">
              {agentIds?.length > 1 ? '批量' : installedHosts[0]?.hostName}卸载Agent任务(任务ID：{res}）
            </a>
            创建成功！
          </>
        ),
        content: '可点击标题跳转，或至“Agent中心”>“运维任务”模块查看详情',
        okText: '确认',
        onOk: () => {
          // this.setState({ selectedRowKeys: [] });
          // this.getAgentData(this.state.queryParams);
        },
      });
    })
    .catch((err: any) => {
      // console.log(err);
    });
};

// 操作项-黄金指标
const menuLists = [
  {
    name: 'Agent',
    key: '0', // 固定
    url: '/api/v1/normal/metrics/1',
  },
];

export const Containers = (props: any): JSX.Element => {
  const [isGroup, setIsgroup] = useState(true);
  const [menuList, setMenuList] = useState<any[]>(menuLists);
  return (
    <div className="test-chartcontain">
      <ChartContainer
        isGold={true}
        filterData={{
          agent: props?.containerData?.hostName,
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
          <Link to={{ pathname: '/monitor/agent-kanban', state: { agent: props?.containerData?.hostName } }} style={{ color: '#ffffff' }}>
            前往Agent指标看板
          </Link>
        </Button>
      </div>
    </div>
  );
};

// Agent管理列表操作项-编辑（主机配置）
const HostConfigurationForm = (props: any) => {
  const { containerData = {}, genData } = props;
  const [hostDetail, setHostDetail] = useState(containerData);
  const [machineZones, setMachineZones] = useState([] as string[]);

  const getMachineZonesList = () => {
    const zonesList = machineZones.map((ele: string) => {
      return { value: ele, text: ele };
    });
    return zonesList;
  };

  const handleHostSubmit = (e: any) => {
    e.preventDefault();
    props.form.validateFields().then((values: any) => {
      // if (err) {
      //   return false;
      // }
      console.log(values, 'values');
      const params = {
        department: hostDetail?.department || '',
        id: hostDetail?.hostId,
        machineZone: values?.machineZone || '',
      };
      return editOpHosts(params)
        .then((res: any) => {
          Modal.success({ title: '保存成功！', okText: '确认', onOk: () => genData() });
          props.setVisible(false);
        })
        .catch((err: any) => {
          // console.log(err);
        });
    });
  };

  const getMachineZones = () => {
    getHostMachineZone()
      .then((res: string[]) => {
        const zones = res.filter((ele) => !!judgeEmpty(ele));
        setMachineZones(zones);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const getHostDetail = () => {
    getHostDetails(containerData?.hostId || 0)
      .then((res: any) => {
        // setHostDetail(res); // 这里获取详情接口，待确定后在开启
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  useEffect(() => {
    getHostDetail();
    getMachineZones();
  }, []);

  return (
    <Form
      form={props.form}
      name="hostConfigurationForm"
      // labelCol={{ span: 4 }}
      // wrapperCol={{ span: 20 }}
      layout="horizontal"
      // onFinish={handleHostSubmit}
    >
      <div
        className="agent-list-head"
        style={{ display: 'flex', justifyContent: 'space-between', padding: '0 24px', alignItems: 'center' }}
      >
        <b>主机配置</b>
      </div>
      <Divider style={{ margin: '10px 0' }} />
      <div style={{ padding: '0 24px' }}>
        <Row gutter={[16, 0]}>
          <Col span={12}>
            <Form.Item
              label="主机名"
              name="hostName"
              rules={[
                {
                  // initialValue: hostDetail?.hostName,
                },
              ]}
            >
              <Tooltip title={hostDetail?.hostName}>
                <span className="agent-list-hostname">{hostDetail?.hostName}</span>
              </Tooltip>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label="主机IP"
              name="ip"
              // initialValue={hostDetail?.ip}
              rules={[
                {
                  // initialValue: hostDetail?.ip,
                },
              ]}
            >
              <span>{hostDetail?.ip}</span>
            </Form.Item>
          </Col>
          {hostDetail?.container === 1 && (
            <Col span={12}>
              <Form.Item
                label="宿主机名"
                name="parentHostName"
                rules={[
                  {
                    // initialValue: hostDetail?.parentHostName,
                  },
                ]}
              >
                <span>{hostDetail?.parentHostName}</span>
              </Form.Item>
            </Col>
          )}
        </Row>
      </div>
    </Form>
  );
};

// Agent管理列表操作项-编辑（高级配置）
const AgentConfigurationForm = (props: any) => {
  const { containerData, genData } = props;
  const [activeKeys, setActiveKeys] = useState([] as string[]);
  const [agentDetail, setAgentDetail] = useState({} as any);
  const [receivers, setReceivers] = useState([] as any[]);
  const [errorReceivers, setErrorReceivers] = useState([] as any[]);
  const [receiverTopic, setReceiverTopic] = useState([] as any[]);
  const [errorTopic, setErrorTopic] = useState([] as any[]);

  useEffect(() => {
    if (props.submitEvent !== 1) {
      handleAgentSubmit();
    }
  }, [props.submitEvent]);

  const handleAgentSubmit = () => {
    // console.log(e);
    // e.preventDefault();
    props.form.validateFields().then((values: any) => {
      // if (err) {
      //   collapseCallBack(['high']);
      //   return false;
      // }
      const params = {
        metricsProducerConfiguration: values?.metricsProducerConfiguration,
        errorLogsProducerConfiguration: values?.errorLogsProducerConfiguration,
        advancedConfigurationJsonString: values?.advancedConfigurationJsonString,
        byteLimitThreshold: values?.byteLimitThreshold * values?.unit,
        cpuLimitThreshold: values?.cpuLimitThreshold * 100,
        errorLogsSendReceiverId: values?.errorLogsSendReceiverId,
        errorLogsSendTopic: values?.errorLogsSendTopic,
        metricsSendReceiverId: values?.metricsSendReceiverId,
        metricsSendTopic: values?.metricsSendTopic,
        id: agentDetail.id,
      };
      return editOpAgent(params)
        .then((res: any) => {
          Modal.success({ title: '保存成功！', okText: '确认', onOk: () => genData() });
          props.setVisible(false);
        })
        .catch((err: any) => {
          // console.log(err);
        });
    });
  };

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  };

  const onReceiverChange = (value: number) => {
    // getReceiverTopic(value); // 等对接Kafka集群时再修复
  };

  const onErrorChange = (value: number) => {
    // getReceiverTopic(value, true); // 等对接Kafka集群时再修复
  };

  const getReceiversList = () => {
    getReceivers()
      .then((res: any[]) => {
        setReceivers(res);
        setErrorReceivers(res);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const getReceiverTopic = (id: number, judge?: boolean) => {
    getReceiversTopic(id)
      .then((res: string[]) => {
        const topics = res?.map((ele) => {
          return { text: ele, value: ele };
        });
        judge ? setErrorTopic(topics) : setReceiverTopic(topics);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const getAgentDetail = () => {
    getAgentDetails(containerData?.agentId)
      .then((res: any) => {
        setAgentDetail(res); // 数据中未包含AgentId 暂时无数据回显
        res.cpuLimitThreshold && props.form.setFieldsValue({ cpuLimitThreshold: res.cpuLimitThreshold / 100 });
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  useEffect(() => {
    if (containerData.agentId) {
      getAgentDetail();
      getReceiversList();
    }
  }, []);

  return (
    <Form
      form={props.form}
      name="agentConfigurationForm"
      // labelCol={{ span: 4 }}
      // wrapperCol={{ span: 20 }}
      layout="vertical"
    >
      <div
        className="agent-list-head"
        style={{ display: 'flex', justifyContent: 'space-between', padding: '0 24px', alignItems: 'center' }}
      >
        <b>Agent配置</b>
      </div>
      <Divider style={{ margin: '10px 0' }} />
      <div style={{ padding: '0 24px' }}>
        {containerData.agentId ? (
          <>
            <Form.Item label="CPU核数上限" className="cpuLimitThreshold">
              <Form.Item
                name="cpuLimitThreshold"
                rules={[
                  {
                    required: true,
                    message: '请输入',
                    // initialValue: agentDetail?.cpuLimitThreshold,
                  },
                ]}
              >
                <InputNumber min={1} placeholder="请输入" />
              </Form.Item>
              <span className="cpuLimitThreshold-unit">&nbsp;核</span>
            </Form.Item>
            <Collapse
              bordered={false}
              expandIconPosition="right"
              onChange={collapseCallBack}
              activeKey={activeKeys?.length ? ['high'] : []}
              ghost
            >
              <Panel
                header={
                  <div
                    style={{
                      display: 'flex',
                      flex: 'auto',
                      alignItems: 'center',
                      marginRight: '80px',
                      marginLeft: '-10px',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    高级配置
                    <Divider plain></Divider>
                    <a style={{ display: 'flex', alignItems: 'center' }}>
                      {activeKeys?.length ? (
                        <>
                          收起&nbsp;
                          <UpOutlined />
                        </>
                      ) : (
                        <>
                          展开&nbsp;
                          <DownOutlined />
                        </>
                      )}
                    </a>
                  </div>
                }
                showArrow={false}
                key="high"
              >
                <Row gutter={[16, 0]}>
                  {/* <Col span={12}>
                    <Form.Item
                      label="指标流接收集群"
                      name="metricsSendReceiverId"
                      initialValue={agentDetail?.metricsSendReceiverId}
                      rules={[
                        {
                          required: true,
                          message: '请选择',
                          // initialValue: agentDetail?.metricsSendReceiverId,
                        },
                      ]}
                    >
                      <Select onChange={onReceiverChange}>
                        {receivers.map((v: any, index: number) => (
                          <Option key={index} value={v.id}>
                            {v.kafkaClusterName.length > 15 ? (
                              <Tooltip placement="bottomLeft" title={v.kafkaClusterName}>
                                {v.kafkaClusterName}
                              </Tooltip>
                            ) : (
                              v.kafkaClusterName
                            )}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      label="错误日志接收集群"
                      name="errorLogsSendReceiverId"
                      initialValue={agentDetail?.errorLogsSendReceiverId}
                      rules={[
                        {
                          required: true,
                          message: '请选择',
                          // initialValue: agentDetail?.errorLogsSendReceiverId,
                        },
                      ]}
                    >
                      <Select onChange={onErrorChange}>
                        {errorReceivers.map((v: any, index: number) => (
                          <Option key={index} value={v.id}>
                            {v.kafkaClusterName.length > 15 ? (
                              <Tooltip placement="bottomLeft" title={v.kafkaClusterName}>
                                {v.kafkaClusterName}
                              </Tooltip>
                            ) : (
                              v.kafkaClusterName
                            )}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      label="指标流接收Topic"
                      name="metricsSendTopic"
                      initialValue={agentDetail?.metricsSendTopic}
                      rules={[
                        {
                          required: true,
                          message: '请输入',
                          // initialValue: agentDetail?.metricsSendTopic,
                        },
                      ]}
                    >
                      <AutoComplete placeholder="请选择或输入" options={receiverTopic} children={<Input style={{ border: 'none' }} />} />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      label="错误日志接收Topic"
                      name="errorLogsSendTopic"
                      initialValue={agentDetail?.errorLogsSendTopic}
                      rules={[
                        {
                          required: true,
                          message: '请输入',
                          // initialValue: agentDetail?.errorLogsSendTopic,
                        },
                      ]}
                    >
                      <AutoComplete placeholder="请选择或输入" options={errorTopic} children={<Input style={{ border: 'none' }} />} />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      label="生产端属性"
                      name="metricsProducerConfiguration"
                      initialValue={agentDetail?.metricsProducerConfiguration}
                      rules={[
                        {
                          message: '请输入',
                          pattern: /^[-\w]{1,1024}$/,
                          // initialValue: agentDetail?.metricsProducerConfiguration
                        },
                      ]}
                    >
                      <TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      label="生产端属性"
                      name="errorLogsProducerConfiguration"
                      initialValue={agentDetail?.metricsProducerConfiguration}
                      rules={[
                        {
                          message: '请输入',
                          pattern: /^[-\w]{1,1024}$/,
                          // initialValue: agentDetail?.metricsProducerConfiguration,
                        },
                      ]}
                    >
                      <TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />
                    </Form.Item>
                  </Col> */}
                  <Col span={24}>
                    <Form.Item
                      label="配置信息"
                      name="advancedConfigurationJsonString"
                      initialValue={agentDetail?.advancedConfigurationJsonString}
                    >
                      {/* <MonacoEditor {...props} /> */}
                      <TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />
                    </Form.Item>
                  </Col>
                </Row>
              </Panel>
            </Collapse>
          </>
        ) : (
          <p className="agent-installed">该主机未安装Agent</p>
        )}
      </div>
    </Form>
  );
};

// 编辑主机
export const ModifyHost: React.FC = (props: any) => {
  return (
    <div className="modify-agent-list">
      <HostConfigurationForm {...props} />
      <AgentConfigurationForm {...props} />
    </div>
  );
};

// 删除主机
export const deleteAgentHost = (props: any) => {
  Modal.confirm({
    title: `是否确认删除主机名为${props.hostName}的${props.agentId ? 'Agent' : ''}与主机？`,
    content: <span className="fail">{props.agentId ? '请先卸载或停止 agent 进程' : '删除操作不可恢复，请谨慎操作！'}</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      deleteHost(props.hostId, 0).then((res: any) => {
        // 0：不忽略数据未采集完 1：忽略数据未采集完
        // 删除主机 0：删除成功
        // 10000：参数错误 ==> 不可删除
        // 23000：待删除主机在系统不存在 ==> 不可删除
        // 23004：主机存在关联的容器导致主机删除失败 ==> 不可删除
        // 22001：Agent存在未采集完的日志 ==> 不可能存在这种情况
        if (res.code === 0) {
          notification.success({
            message: '成功',
            duration: 3,
            description: res.message || '删除成功！',
          });
          props.genData();
        } else if (res.code === 10000 || res.code === 23000 || res.code === 23004) {
          notification.error({
            message: '错误',
            duration: 3,
            description: res.message,
          });
        } else {
          notification.error({ message: '错误', duration: 3, description: res.message });
        }
      });
    },
  });
};

// 删除Agent
export const deleteAgent = (props: any) => {
  Modal.confirm({
    title: `是否确认删除主机名为${props.hostName}的Agent？`,
    content: <span className="fail">{'请先卸载或停止 agent 进程'}</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      deleteAgentAPI(props.agentId).then((res: any) => {
        // 0：不忽略数据未采集完 1：忽略数据未采集完
        // 删除主机 0：删除成功
        // 10000：参数错误 ==> 不可删除
        // 23000：待删除主机在系统不存在 ==> 不可删除
        // 23004：主机存在关联的容器导致主机删除失败 ==> 不可删除
        // 22001：Agent存在未采集完的日志 ==> 不可能存在这种情况
        if (res.code === 0) {
          notification.success({
            message: '成功',
            duration: 3,
            description: res.message || '删除成功！',
          });
          props.genData();
        } else if (res.code === 10000 || res.code === 23000 || res.code === 23004) {
          notification.error({
            message: '错误',
            duration: 3,
            description: res.message,
          });
        } else {
          notification.error({
            message: '错误',
            duration: 3,
            description: res.message,
          });
        }
      });
    },
  });
};

// 跳转AgentDetail
export const jumpAgentDetail = (props: any) => {
  // window.history.pushState(props, '', '/detail');
  props.history.push({
    pathname: '/main/detail',
    state: { hostId: `${props.hostId}`, agentId: `${props.agentId || ''}`, hostName: `${props.hostName || ''}` },
  });
};
// agent版本号
export const agentvisionArr = async () => {
  const agentVersionList: any = await getAgentVersion();
  return agentVersionList.map((item) => {
    return { title: item.agentVersion, value: item.agentVersionId };
  });
};

// 主机名列表
export const hostNameArr = async () => {
  const hostNameList: any = await getHosts();
  return hostNameList.map((item) => {
    return { title: item.hostName, value: item.hostName };
  });
};

export const servicesList = async () => {
  const ServicesList: any = await getServices();
  return ServicesList.map((item) => {
    return { title: item.servicename, value: item.id };
  });
};

export const hostNameList = async () => {
  return [
    {
      value: 0,
      title: '物理机',
    },
    // {
    //   value: 1,
    //   title: '容器',
    // },
    // {
    //   value: 2,
    //   label: 'VM虚拟机',
    // }
  ];
};

export const healthList = () => {
  return healthTypes.map((item) => {
    return { title: item.label, value: item.value };
  });
};

const getColmns = (solveClick) => {
  const columns = [
    {
      title: '主机名',
      dataIndex: 'hostName',
      key: 'hostName',
    },
    {
      title: '错误日志数量',
      dataIndex: 'errorLogsCount',
      key: 'errorLogsCount',
    },
    {
      title: '错误日志',
      dataIndex: 'errorLogs',
      key: 'errorLogs',
      width: 200,
      needTooltip: true,
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

const getAgentHealthInspectionCodeList = (params: any) => {
  return request(`/api/v1/normal/agent/health/error-detail?hostName=${params.hostName}`);
};

const getHealthSolve = (agentMetricId: number) => {
  return request(`/api/v1/normal/agent/health?agentMetricId=${agentMetricId}`, {
    method: 'PUT',
  });
};

export const DiagnosisContent = (props: any) => {
  const { containerData } = props;
  const [solveData, setSolveData] = useState([]);
  const [loading, setLoading] = useState(false);

  // 获取故障信息列表
  const getSolveListData = (toast?: boolean) => {
    setLoading(true);
    getAgentHealthInspectionCodeList(containerData)
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
      });
  };

  const solveClick = (record) => {
    getHealthSolve(record.agentMetricId)
      .then(() => {
        getSolveListData(true);
      })
      .catch((err) => {
        notification.error({
          message: '失败',
          duration: 3,
        });
      });
    console.log(record, 'records');
  };

  useEffect(() => {
    getSolveListData();
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
      <div style={{ padding: '20px 30px' }}>{containerData.agentHealthDescription}</div>
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
          }}
        />
      </div>
    </>
  );
};

export const agentHealthLevel = (record, value) => {
  return record.agentHealthLevel || record.agentHealthLevel == 0 ? (
    record.agentHealthLevel === 0 || record.agentHealthLevel === 1 ? (
      <Tooltip title={record.agentHealthDescription}>
        <span>
          <span style={{ fontSize: '20px' }}>
            <IconFont type={record.agentHealthLevel === 1 ? 'icon-huang' : 'icon-hong'} />
          </span>
          {record.agentHealthInspectionResultType === 14 && (
            <a style={{ marginLeft: '10px' }} onClick={() => value.clickFunc(record, value)}>
              故障修复
            </a>
          )}
        </span>
      </Tooltip>
    ) : (
      <span style={{ fontSize: '20px' }}>
        <IconFont type="icon-lv" />
      </span>
    )
  ) : (
    '-'
  );
};

export const goldMetricIcon = (record, value) => {
  return (
    <span>
      <IconFont type={record.agentId ? 'icon-huangjinzhibiao' : 'icon-huangjinzhibiao1'} />
    </span>
  );
};

export const HealthMap = (props: any) => {
  return (
    <div>
      <span style={{ marginRight: '10px' }}>{props?.hostName || ''}</span>
      <span>
        {props.agentHealthLevel || props.agentHealthLevel == 0 ? (
          props.agentHealthLevel === 0 || props.agentHealthLevel === 1 ? (
            <span>
              <span style={{ fontSize: '20px' }}>
                <IconFont type={props.agentHealthLevel === 1 ? 'icon-huang' : 'icon-hong'} />
              </span>
              {/* <a style={{ marginLeft: '20px' }} onClick={() => props.clickFunc(props, props)}>
          故障诊断
        </a> */}
            </span>
          ) : (
            <span style={{ fontSize: '20px' }}>
              <IconFont type="icon-lv" />
            </span>
          )
        ) : (
          ''
        )}
      </span>
    </div>
  );
};

export default {
  hostNameArr,
  agentvisionArr,
  servicesList,
  hostNameList,
  healthList,
  agentHealthLevel,
  HealthMap,
  deleteAgentHost,
  deleteAgent,
  goldMetricIcon,
};
