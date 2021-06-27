import React, { useState, useEffect } from 'react';
import { AutoComplete, Modal, Form, Input, Divider, Select, Button, Tooltip, Row, Col, InputNumber, Collapse } from 'antd';
import * as actions from '../../actions';
import '../../container/agent-management/index.less';
import { connect } from "react-redux";
import { IFormProps, DataSourceItemType } from '../../interface/common';
import { flowUnitList } from '../../constants/common';
import { IAgentHostSet, IEditOpHostsParams, IOpAgent, IReceivers } from '../../interface/agent';
import { getHostDetails, getHostMachineZone, editOpHosts, getAgentDetails, editOpAgent, getReceivers, getReceiversTopic } from '../../api/agent'
import MonacoEditor from '../../component/editor/monacoEditor';
import { setLimitUnit, judgeEmpty } from '../../lib/utils';
import { UpOutlined, DownOutlined } from '@ant-design/icons';

const { Panel } = Collapse;
const { Option } = Select;
const { TextArea } = Input;

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

interface IModifyHostParams {
  hostObj: IAgentHostSet;
  getData: any;
}

const ModifyHost = (props: { dispatch: any, params: IModifyHostParams }) => {
  // console.log('props---', props.params);

  const handleModifyCancel = () => {
    props.dispatch(actions.setModalId(''));
  }

  return (
    <Modal
      title="编辑"
      visible={true}
      footer={null}
      onCancel={handleModifyCancel}
    >
      <div className="modify-agent-list">
        <WrappedHostConfigurationForm
          dispatch={props.dispatch}
          params={props.params}
        />
        <WrappedAgentConfigurationForm
          dispatch={props.dispatch}
          params={props.params}
        />
      </div>
    </Modal>
  )
}

const modifyAgentListLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 16 },
};

interface IDispatch {
  dispatch: any;
  params: IModifyHostParams;
}

const HostConfigurationForm = (props: IFormProps & IDispatch) => {
  const { getFieldDecorator } = props.form;
  let { hostObj, getData } = props.params;
  const [hostDetail, setHostDetail] = useState(hostObj);
  const [machineZones, setMachineZones] = useState([] as string[]);

  const getMachineZonesList = () => {
    const zonesList = machineZones.map((ele: string) => { return { value: ele, text: ele } });
    return zonesList;
  }

  const handleHostSubmit = (e: any) => {
    e.preventDefault();
    props.form.validateFields((err: any, values: any) => {
      if (err) { return false; }
      const params = {
        department: hostDetail?.department || '',
        id: hostDetail?.hostId,
        machineZone: values?.machineZone || '',
      } as IEditOpHostsParams;
      return editOpHosts(params).then((res: any) => {
        props.dispatch(actions.setModalId(''));
        Modal.success({ title: '保存成功！', okText: '确认', onOk: () => getData() });
      }).catch((err: any) => {
        // console.log(err);
      });
    });
  };

  const getMachineZones = () => {
    getHostMachineZone().then((res: string[]) => {
      const zones = res.filter(ele => !!judgeEmpty(ele));
      setMachineZones(zones);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  const getHostDetail = () => {
    getHostDetails(hostObj?.hostId).then((res: IAgentHostSet) => {
      setHostDetail(res);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  useEffect(() => {
    getHostDetail();
    getMachineZones();
  }, []);

  return (
    <Form
      className="host-configuration"
      {...modifyAgentListLayout}
      onSubmit={handleHostSubmit}
    >
      <div className="agent-list-head">
        <b>主机配置</b>
        <Button type="primary" htmlType="submit">保存</Button>
      </div>
      <Divider />
      <Form.Item label="主机名">
        {getFieldDecorator('hostName', {
          initialValue: hostDetail?.hostName,
        })(
          <span>{hostDetail?.hostName}</span>,
        )}
      </Form.Item>
      <Form.Item label="主机IP">
        {getFieldDecorator('ip', {
          initialValue: hostDetail?.ip,
        })(
          <span>{hostDetail?.ip}</span>,
        )}
      </Form.Item>
      {hostDetail?.container === 1 &&
        <Form.Item label="宿主机名">
          {getFieldDecorator('parentHostName', {
            initialValue: hostDetail?.parentHostName,
          })(
            <span>{hostDetail?.parentHostName}</span>,
          )}
        </Form.Item>}
      <Form.Item label="所属机房">
        {getFieldDecorator('machineZone', {
          initialValue: hostDetail?.machineZone,
          rules: [{ required: false }],
        })(
          <AutoComplete
            placeholder="请选择或输入"
            dataSource={getMachineZonesList()}
            children={<Input />}
          />,
        )}
      </Form.Item>
    </Form>
  )
}

const WrappedHostConfigurationForm = Form.create<IFormProps & IDispatch>()(HostConfigurationForm);

const AgentConfigurationForm = (props: IFormProps & IDispatch) => {
  const { getFieldDecorator } = props.form;
  const { hostObj, getData } = props.params;
  const [activeKeys, setActiveKeys] = useState([] as string[]);
  const [agentDetail, setAgentDetail] = useState({} as IOpAgent);
  const [receivers, setReceivers] = useState([] as IReceivers[]);
  const [errorReceivers, setErrorReceivers] = useState([] as IReceivers[]);
  const [receiverTopic, setReceiverTopic] = useState([] as DataSourceItemType[]);
  const [errorTopic, setErrorTopic] = useState([] as DataSourceItemType[]);

  const handleAgentSubmit = (e: any) => {
    e.preventDefault();
    props.form.validateFields((err: any, values: any) => {
      if (err) {
        collapseCallBack(['high']);
        return false;
      }
      const params = {
        metricsProducerConfiguration: values?.metricsProducerConfiguration,
        errorLogsProducerConfiguration: values?.errorLogsProducerConfiguration,
        advancedConfigurationJsonString: values?.advancedConfigurationJsonString,
        byteLimitThreshold: values?.byteLimitThreshold * values?.unit,
        cpuLimitThreshold: values?.cpuLimitThreshold,
        errorLogsSendReceiverId: values?.errorLogsSendReceiverId,
        errorLogsSendTopic: values?.errorLogsSendTopic,
        metricsSendReceiverId: values?.metricsSendReceiverId,
        metricsSendTopic: values?.metricsSendTopic,
        id: agentDetail.id,
      } as IOpAgent;
      return editOpAgent(params).then((res: IOpAgent) => {
        props.dispatch(actions.setModalId(''));
        Modal.success({ title: '保存成功！', okText: '确认', onOk: () => getData() });
      }).catch((err: any) => {
        // console.log(err);
      });
    });
  };

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  }

  const onReceiverChange = (value: number) => {
    // getReceiverTopic(value); // 等对接Kafka集群时再修复
  }

  const onErrorChange = (value: number) => {
    // getReceiverTopic(value, true); // 等对接Kafka集群时再修复
  }

  const getReceiversList = () => {
    getReceivers().then((res: IReceivers[]) => {
      setReceivers(res);
      setErrorReceivers(res);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  const getReceiverTopic = (id: number, judge?: boolean) => {
    getReceiversTopic(id).then((res: string[]) => {
      const topics = res?.map(ele => { return { text: ele, value: ele } });
      judge ? setErrorTopic(topics) : setReceiverTopic(topics);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  const getAgentDetail = () => {
    getAgentDetails(hostObj?.agentId).then((res: IOpAgent) => {
      setAgentDetail(res);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  useEffect(() => {
    if (hostObj.agentId) {
      getAgentDetail();
      getReceiversList();
    }
  }, []);

  return (
    <Form
      className="agent-configuration"
      {...modifyAgentListLayout}
      onSubmit={handleAgentSubmit}
    >
      <div className="agent-list-head">
        <b>Agent配置</b>
        {hostObj.agentId && <Button type="primary" htmlType="submit">保存</Button>}
      </div>
      <Divider />
      {hostObj.agentId ? <>
        <Form.Item label="CPU核数上限">
          {getFieldDecorator('cpuLimitThreshold', {
            initialValue: agentDetail?.cpuLimitThreshold,
            rules: [{ required: true, message: '请输入' }],
          })(
            <InputNumber min={1} placeholder="请输入" />,
          )}
          <span>&nbsp;核</span>
        </Form.Item>
        {/* <Form.Item label="出口流量上限">
          <Row>
            <Col span={8}>
              {getFieldDecorator('byteLimitThreshold', {
                initialValue: setLimitUnit(agentDetail?.byteLimitThreshold, 1)?.maxBytesPerLogEvent,
                rules: [{ required: true, message: '请输入' }],
              })(
                <InputNumber min={1} placeholder="请输入" />,
              )}
            </Col>
            <Col span={6}>
              <Form.Item>
                {getFieldDecorator('unit', {
                  initialValue: setLimitUnit(agentDetail?.byteLimitThreshold, 1)?.flowunit,
                  rules: [{ required: true, message: '请输入' }],
                })(
                  <Select>
                    {flowUnitList.map((v, index) => (
                      <Option key={index} value={v.value}>{v.label}</Option>
                    ))}
                  </Select>,
                )}
              </Form.Item>
            </Col>
          </Row>
        </Form.Item> */}
        <Collapse
          bordered={false}
          expandIconPosition="right"
          onChange={collapseCallBack}
          activeKey={activeKeys?.length ? ['high'] : []}
        >
          <Panel
            header={<h3>高级配置</h3>}
            extra={<a>{activeKeys?.length ? <>收起&nbsp;<UpOutlined /></> : <>展开&nbsp;<DownOutlined /></>}</a>}
            showArrow={false}
            key="high"
          >
            <Row>
              <Form.Item label="指标流接收集群">
                {getFieldDecorator('metricsSendReceiverId', {
                  initialValue: agentDetail?.metricsSendReceiverId,
                  rules: [{ required: true, message: '请选择' }],
                })(
                  <Select onChange={onReceiverChange}>
                    {receivers.map((v: IReceivers, index: number) => (
                      <Option key={index} value={v.id}>
                        {v.kafkaClusterName.length > 15 ? <Tooltip placement="bottomLeft" title={v.kafkaClusterName}>{v.kafkaClusterName}</Tooltip> : v.kafkaClusterName}
                      </Option>
                    ))}
                  </Select>,
                )}
              </Form.Item>
              <Form.Item label="指标流接收Topic">
                {getFieldDecorator('metricsSendTopic', {
                  initialValue: agentDetail?.metricsSendTopic,
                  rules: [{ required: true, message: '请输入' }],
                })(
                  <AutoComplete
                    placeholder="请选择或输入"
                    dataSource={receiverTopic}
                    children={<Input />}
                  />,
                )}
              </Form.Item>
              <Form.Item label="生产端属性">
                {getFieldDecorator('metricsProducerConfiguration', {
                  initialValue: agentDetail?.metricsProducerConfiguration,
                  rules: [{ message: '请输入' }],
                })(
                  <TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />,
                )}
              </Form.Item>
              <Form.Item label="错误日志接收集群">
                {getFieldDecorator('errorLogsSendReceiverId', {
                  initialValue: agentDetail?.errorLogsSendReceiverId,
                  rules: [{ required: true, message: '请选择' }],
                })(
                  <Select onChange={onErrorChange}>
                    {errorReceivers.map((v: IReceivers, index: number) => (
                      <Option key={index} value={v.id}>
                        {v.kafkaClusterName.length > 15 ? <Tooltip placement="bottomLeft" title={v.kafkaClusterName}>{v.kafkaClusterName}</Tooltip> : v.kafkaClusterName}
                      </Option>
                    ))}
                  </Select>,
                )}
              </Form.Item>
              <Form.Item label="错误日志接收Topic">
                {getFieldDecorator('errorLogsSendTopic', {
                  initialValue: agentDetail?.errorLogsSendTopic,
                  rules: [{ required: true, message: '请输入' }],
                })(
                  <AutoComplete
                    placeholder="请选择或输入"
                    dataSource={errorTopic}
                    children={<Input />}
                  />,
                )}
              </Form.Item>
              <Form.Item label="生产端属性">
                {getFieldDecorator('errorLogsProducerConfiguration', {
                  initialValue: agentDetail?.metricsProducerConfiguration,
                  rules: [{ message: '请输入' }],
                })(
                  <TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />,
                )}
              </Form.Item>
              <Form.Item label="配置信息">
                {getFieldDecorator('advancedConfigurationJsonString', {
                  initialValue: agentDetail?.advancedConfigurationJsonString || '',
                  rules: [{ required: true, message: '请输入' }],
                })(
                  <MonacoEditor {...props} />
                )}
              </Form.Item>
            </Row>
          </Panel>
        </Collapse>
      </> : <p className='agent-installed'>该主机未安装Agent</p>}
    </Form>
  )
}

const WrappedAgentConfigurationForm = Form.create<IFormProps & IDispatch>()(AgentConfigurationForm);

export default connect(mapStateToProps)(ModifyHost);


