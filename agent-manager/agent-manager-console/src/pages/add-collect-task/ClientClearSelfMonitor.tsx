import React, { useState, useEffect } from 'react';
import { InputNumber, Form, Radio, Select, Input, AutoComplete, Collapse, Col, Switch } from '@didi/dcloud-design';
import { clientFormItemLayout } from './config';
import { NavRouterLink } from '../../components/CustomComponent';
import { IReceivers } from '../../interface/agent';
import { getReceivers, getReceiversTopic } from '../../api/agent';
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
  const [activeKeys, setActiveKeys] = useState([] as string[]);
  const [openDelay, setOpenDelay] = useState(false);

  const customPanelStyle = {
    // background: '#f7f7f7',
    border: 0,
    overflow: 'hidden',
    padding: '10px 0 0',
    // display: 'flex',
    // justifyContent: 'space-between',
    // alignItems: 'center',
    background: '#fafafa',
    // borderBottom: '1px solid #a2a2a5',
  };

  const openHistoryChange = (checked: any) => {
    setOpenDelay(checked);
  };

  const onReceiverChange = (value: number) => {
    getReceiverTopic(value); // 等对接Kafka集群时再修复
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

  const getReceiverTopic = (id: number) => {
    getReceiversTopic(id)
      .then((res: string[]) => {
        const data = res.map((ele) => {
          return { text: ele, value: ele };
        });
        setReceiverTopic(data);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  useEffect(() => {
    getReceiversList();
  }, []);

  useEffect(() => {
    setOpenDelay(getFieldValue('step3_opencollectDelay'));
  }, [getFieldValue('step3_opencollectDelay')]);

  useEffect(() => {
    const id = getFieldValue('step4_kafkaClusterId');
    if (id || id === 0) {
      getReceiverTopic(id);
    }
  }, [getFieldValue('step4_kafkaClusterId')]);

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  };

  return (
    <div>
      <Form form={props.form} {...clientFormItemLayout}>
        <Form.Item
          label="Kafka集群"
          extra={
            <>
              如目标集群不在下拉列表，请至
              <NavRouterLink needToolTip element={' 接收端管理>Kafka集群 '} href="/receivingTerminal/clusterList" />
              新增
            </>
          }
          name="step4_kafkaClusterId"
          initialValue=""
          rules={[{ required: true, message: '请选择Kafka集群' }]}
        >
          {/* {getFieldDecorator('step4_kafkaClusterId', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: '',
            rules: [{ required: true, message: '请选择Kafka集群' }],
          })(
            <Select onChange={onReceiverChange} className="w-300" placeholder="请选择集群">
              {receivers.map((ele, index) => {
                return (
                  <Select.Option key={index} value={ele.id}>
                    {ele.kafkaClusterName}
                  </Select.Option>
                );
              })}
            </Select>
          )} */}
          <Select onChange={onReceiverChange} className="w-300" placeholder="请选择集群">
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
          name="step3_productionSide"
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
          {/* {getFieldDecorator('step3_productionSide', {
            rules: [
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
            ],
          })(<TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />)} */}
          <TextArea style={{ height: 100 }} placeholder="默认值，如修改，覆盖相应生产端配置" />
        </Form.Item>
        <Form.Item
          name="step4_sendTopic"
          initialValue=""
          label="Topic"
          validateTrigger={['onChange', 'onBlur']}
          rules={[{ required: true, message: '请选择Topic' }]}
        >
          {/* {getFieldDecorator('step4_sendTopic', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: '',
            rules: [{ required: true, message: '请选择Topic' }],
          })(<AutoComplete placeholder="请选择或输入" dataSource={receiverTopic} children={<Input />} />)} */}
          <AutoComplete placeholder="请选择或输入" dataSource={receiverTopic} children={<Input />} />
        </Form.Item>
        {/* <Form.Item label="客户端offset（采集位点记录）清理">
          超过&nbsp;
          {getFieldDecorator('step3_fdOffsetExpirationTimeMs', {
          initialValue: '7',
          rules: [{ required: true, message: '请输入' }],
        })(
          <InputNumber min={1} />,
        )}&nbsp;天无数据写入，则在Agent客户端删除该文件的offset
        </Form.Item> */}
        {props.logType === 'file' && (
          <>
            <Form.Item
              name="step3_opencollectDelay"
              label="采集延迟监控"
              extra="注：仅支持对按业务时间顺序进行输出的日志进行延迟监控"
              // rules={[{ required: true }]}
            >
              <Switch onChange={openHistoryChange} checked={getFieldValue('step3_opencollectDelay')} />
            </Form.Item>
            {openDelay && (
              <div className="collectDelayThresholdMs">
                <Form.Item label="该任务下Agent客户端延迟超过">
                  <Form.Item
                    className="step3_collectDelayThresholdMs"
                    name="step3_collectDelayThresholdMs"
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
                </Form.Item>
              </div>
            )}
          </>
        )}
        {/* {props.logType === 'file' &&
          <Form.Item label="采集延迟监控" extra="注：仅支持对按业务时间顺序进行输出的日志进行延迟监控">
            该任务下Agent客户端延迟超过&nbsp;
          {getFieldDecorator('step3_collectDelayThresholdMs', {
            initialValue: '3',
            rules: [{ required: true, message: '请输入' }],
          })(
            <InputNumber min={1} />,
          )}&nbsp;分钟，则视为异常
        </Form.Item>} */}
        {/* {props.collectType !== 0 &&
          <Form.Item label="采集完成时间限制">
            该任务超过&nbsp;
          {getFieldDecorator('step3_logCollectTaskExecuteTimeoutMs', {
            initialValue: '6',
            rules: [{ required: true, message: '请输入' }],
          })(
            <InputNumber min={1} />,
          )}&nbsp;分钟未完成，则视为异常
        </Form.Item>} */}

        <Form.Item
          name="step3_limitPriority"
          initialValue={1}
          label="任务保障等级"
          extra="限流时，资源优先分配给任务保障等级高的采集任务"
          rules={[{ required: true, message: '请选择日志内容是否过滤' }]}
        >
          {/* {getFieldDecorator('step3_limitPriority', {
            initialValue: 1,
            rules: [{ required: true, message: '请选择日志内容是否过滤' }],
          })(
            <Radio.Group>
              <Radio value={2}>低</Radio>
              <Radio value={1}>中</Radio>
              <Radio value={0}>高</Radio>
            </Radio.Group>
          )} */}
          <Radio.Group>
            <Radio value={2}>低</Radio>
            <Radio value={1}>中</Radio>
            <Radio value={0}>高</Radio>
          </Radio.Group>
        </Form.Item>
        <Collapse
          bordered={false}
          expandIconPosition="right"
          onChange={collapseCallBack}
          activeKey={activeKeys?.length ? ['high'] : []}
          destroyInactivePanel
          style={{ background: 'none', padding: '0 140px 0 100px' }}
        >
          <Panel
            header={<h2 style={{ display: 'inline-block', color: '#a2a2a5', marginLeft: '15px' }}>高级配置</h2>}
            extra={
              <a>
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
            }
            showArrow={false}
            key="high"
            style={customPanelStyle}
          >
            <Form.Item name="step4_advancedConfigurationJsonString" initialValue="" label="配置信息">
              {/* {getFieldDecorator('step4_advancedConfigurationJsonString', {
                initialValue: '',
              })(<MonacoEditor height={120} />)} */}
              {/* <MonacoEditor height={120} /> */}
              <TextArea style={{ height: 120 }} placeholder="请输入配置信息" />
            </Form.Item>
          </Panel>
        </Collapse>
      </Form>
    </div>
  );
};

export default ClientClearSelfMonitor;
