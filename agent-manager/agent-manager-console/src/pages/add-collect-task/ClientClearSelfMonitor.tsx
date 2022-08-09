import React, { useState, useEffect } from 'react';
import { InputNumber, Form, Radio, Select, Input, AutoComplete, Collapse, Row, Col, Switch, Divider } from '@didi/dcloud-design';
import { clientFormItemLayout } from './config';
import { NavRouterLink } from '../../components/CustomComponent';
import { IReceivers } from '../../interface/agent';
import { getReceivers, getTopics } from '../../api/agent';
import { DataSourceItemType } from '../../interface/common';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
// import MonacoEditor from '../../components/editor/monacoEditor';
const { TextArea } = Input;
import { regLogSliceTimestampPrefixString } from '../../constants/reg';
import './index';

const { Panel } = Collapse;

const ClientClearSelfMonitor = (props: any) => {
  const { getFieldValue } = props.form;
  const [receivers, setReceivers] = useState([] as any);
  const [receiverTopic, setReceiverTopic] = useState([] as any);
  const [originReceiverTopic, setOriginReceiverTopic] = useState([] as any);
  const [activeKeys, setActiveKeys] = useState([] as string[]);
  const [openDelay, setOpenDelay] = useState(false);

  const customPanelStyle = {
    border: 0,
    overflow: 'hidden',
    padding: '10px 0 0',
  };

  const openHistoryChange = (checked: any) => {
    setOpenDelay(checked);
  };

  const getReceiversList = () => {
    getReceivers()
      .then((res: IReceivers[]) => {
        setReceivers(res);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const getReceiverTopic = async () => {
    const res = await getTopics();
    const data = res.map((ele) => {
      return { label: ele, value: ele };
    });
    setReceiverTopic(data);
    setOriginReceiverTopic(data);
  };
  const onSearch = (searchText: string) => {
    if (!searchText) {
      setReceiverTopic(originReceiverTopic);
    } else {
      const filterTopic = originReceiverTopic.filter((item: any) => item.label.indexOf(searchText) > -1);
      setReceiverTopic(filterTopic);
    }
  };

  useEffect(() => {
    getReceiversList();
    getReceiverTopic();
  }, []);

  useEffect(() => {
    setOpenDelay(getFieldValue('step4_opencollectDelay'));
  }, [getFieldValue('step4_opencollectDelay')]);

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  };

  return (
    <div>
      <Form form={props.form} {...clientFormItemLayout} layout="vertical">
        <Form.Item
          label={
            <>
              Kafka集群： 如目标集群不在下拉列表，请至
              <NavRouterLink needToolTip element={' 接收端管理>Kafka集群 '} href="/meta/receivingTerminal" />
              新增
            </>
          }
          name="step4_kafkaClusterId"
          initialValue=""
          rules={[{ required: true, message: '请选择Kafka集群' }]}
        >
          <Select className="w-300" placeholder="请选择集群">
            {receivers.map((ele, index) => {
              return (
                <Select.Option key={index} value={ele.id}>
                  {ele.kafkaClusterName}
                </Select.Option>
              );
            })}
          </Select>
        </Form.Item>
        <Form.Item
          name="step4_productionSide"
          label="生产端属性"
          initialValue={
            !props.editUrl
              ? 'acks=-1,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer,max.in.flight.requests.per.connection=1,compression.type=lz4'
              : ''
          }
          rules={[
            {
              // required: true,
              validator: (rule: any, value: string, cb) => {
                if (!new RegExp(/^[\s\S]{1,1024}$/).test(value)) {
                  rule.message = '生产端属性最大长度为1024位';
                  cb('生产端属性最大长度为1024位');
                } else {
                  cb();
                }
              },
            },
          ]}
        >
          <TextArea style={{ height: 100 }} placeholder="默认值，如修改，覆盖相应生产端配置" />
        </Form.Item>
        <Form.Item
          name="step4_sendTopic"
          initialValue=""
          label="Topic"
          validateTrigger={['onChange', 'onBlur']}
          rules={[{ required: true, message: '请选择Topic' }]}
        >
          <AutoComplete placeholder="请选择或输入" options={receiverTopic} onSearch={onSearch} />
        </Form.Item>
        <Row>
          <Col span={15}>
            {props.logType === 'file' && (
              <>
                <Form.Item
                  name="step4_opencollectDelay"
                  label="采集延迟监控"
                  className="collectDelayWrap"
                  extra="注：仅支持对按业务时间顺序进行输出的日志进行延迟监控"
                >
                  <Switch onChange={openHistoryChange} checked={getFieldValue('step4_opencollectDelay')} />
                </Form.Item>
                {openDelay && (
                  <div className="collectDelayThresholdMs">
                    <div>该任务下Agent客户端延迟超过</div>
                    <Form.Item
                      className="step4_collectDelayThresholdMs"
                      name="step4_collectDelayThresholdMs"
                      initialValue={10}
                      rules={[
                        {
                          required: true,
                          validator: (rule: any, value: string, cb) => {
                            if (!value) {
                              rule.message = '请输入';
                              cb('请输入');
                            } else if (!new RegExp(regLogSliceTimestampPrefixString).test(value)) {
                              rule.message = '最大长度限制8位';
                              cb('最大长度限制8位');
                            } else {
                              cb();
                            }
                          },
                        },
                      ]}
                    >
                      <InputNumber min={10} max={99999999} precision={0} />
                    </Form.Item>
                    <div className="delay-text">分钟，则视为异常</div>
                  </div>
                )}
              </>
            )}
          </Col>
          <Col span={9}>
            <Form.Item
              name="step4_limitPriority"
              initialValue={1}
              label="任务保障等级"
              extra="限流时，资源优先分配给任务保障等级高的采集任务"
              rules={[{ required: true, message: '请选择日志内容是否过滤' }]}
            >
              <Radio.Group>
                <Radio value={2}>低</Radio>
                <Radio value={1}>中</Radio>
                <Radio value={0}>高</Radio>
              </Radio.Group>
            </Form.Item>
          </Col>
        </Row>
        <Collapse
          bordered={false}
          expandIconPosition="right"
          onChange={collapseCallBack}
          activeKey={activeKeys?.length ? ['high'] : []}
          destroyInactivePanel
          ghost
          style={{ background: 'none' }}
        >
          <Panel
            header={
              <div
                style={{
                  display: 'flex',
                  flex: 'auto',
                  alignItems: 'center',
                  marginLeft: '-10px',
                  whiteSpace: 'nowrap',
                }}
              >
                高级配置
                <Divider plain style={{ width: '750px', minWidth: '0' }}></Divider>
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
            style={customPanelStyle}
          >
            <Form.Item name="step4_advancedConfigurationJsonString" initialValue="" wrapperCol={{ span: 24 }}>
              <TextArea style={{ height: 120, marginTop: -20 }} placeholder="请输入配置信息" />
            </Form.Item>
          </Panel>
        </Collapse>
      </Form>
    </div>
  );
};

export default ClientClearSelfMonitor;
