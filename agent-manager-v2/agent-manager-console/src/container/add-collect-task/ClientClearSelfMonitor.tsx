import React, { useState, useEffect } from 'react';
import { connect } from "react-redux";
import { FormComponentProps } from 'antd/lib/form';
import { InputNumber, Form, Radio, Select, Input, AutoComplete, Collapse, Col, Switch } from 'antd';
import { clientFormItemLayout } from './config';
import { NavRouterLink } from '../../../src/component/CustomComponent';
import { IReceivers } from '../../interface/agent';
import { getReceivers, getReceiversTopic } from '../../api/agent';
import { DataSourceItemType } from '../../interface/common';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import MonacoEditor from '../../component/editor/monacoEditor';
import TextArea from 'antd/lib/input/TextArea';

const { Panel } = Collapse;
interface IClientClearSelfMonitorProps extends FormComponentProps {
  openHistory: boolean;
}

const mapStateToProps = (state: any) => ({
  collectType: state.collect.collectType,
  logType: state.collect.logType,
});
type Props = ReturnType<typeof mapStateToProps>

const ClientClearSelfMonitor = (props: Props & IClientClearSelfMonitorProps) => {
  const { getFieldDecorator, getFieldValue } = props.form;
  const [receivers, setReceivers] = useState([] as IReceivers[]);
  const [receiverTopic, setReceiverTopic] = useState([] as DataSourceItemType[]);
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
  }

  const onReceiverChange = (value: number) => {
    getReceiverTopic(value); // 等对接Kafka集群时再修复
  }

  const getReceiversList = () => {
    getReceivers().then((res: IReceivers[]) => {
      setReceivers(res);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  const getReceiverTopic = (id: number) => {
    getReceiversTopic(id).then((res: string[]) => {
      const data = res.map(ele => { return { text: ele, value: ele } });
      setReceiverTopic(data);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  useEffect(() => {
    getReceiversList();

  }, []);

  useEffect(() => {
    setOpenDelay(getFieldValue('step3_opencollectDelay'))
  }, [getFieldValue('step3_opencollectDelay')]);

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  }


  return (
    <div>
      <Form {...clientFormItemLayout}>
        <Form.Item
          label="Kafka集群"
          extra={<>如目标集群不在下拉列表，请至<NavRouterLink needToolTip element={' 接收端管理>Kafka集群 '} href="/collect/detail" />新增</>}
        >
          {getFieldDecorator('step4_kafkaClusterId', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: '',
            rules: [{ required: true, message: '请选择Kafka集群' }],
          })(
            <Select onChange={onReceiverChange} className='w-300' placeholder="请选择集群">
              {receivers.map((ele, index) => {
                return (
                  <Select.Option key={index} value={ele.id}>
                    {ele.kafkaClusterName}
                  </Select.Option>
                );
              })}
            </Select>,
          )}
        </Form.Item>
        <Form.Item label="生产端属性">
          {getFieldDecorator('step3_productionSide', {
            rules: [{ message: '请输入' }],
          })(
            <TextArea placeholder="默认值，如修改，覆盖相应生产端配置" />,
          )}
        </Form.Item>
        <Form.Item label="Topic">
          {getFieldDecorator('step4_sendTopic', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: '',
            rules: [{ required: true, message: '请选择Topic' }],
          })(
            <AutoComplete
              placeholder="请选择或输入"
              dataSource={receiverTopic}
              children={<Input />}
            />,
          )}
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
        {props.logType === 'file' &&
          <Form.Item label="采集延迟监控" extra="注：仅支持对按业务时间顺序进行输出的日志进行延迟监控">
            <Col span={2}>
              {getFieldDecorator('step3_opencollectDelay', {
                initialValue: false,
                valuePropName: 'checked',
                rules: [{ required: true }],
              })(
                <Switch onChange={openHistoryChange} />,
              )}
            </Col>
            <Col span={15}>
              {
                openDelay &&
                <Form.Item >
                  该任务下Agent客户端延迟超过&nbsp;
                {getFieldDecorator('step3_collectDelayThresholdMs', {
                  initialValue: '3',
                  rules: [{ required: true, message: '请输入' }],
                })(
                  <InputNumber min={1} />,
                )}&nbsp;分钟，则视为异常
              </Form.Item>
              }
            </Col>
          </Form.Item>
        }
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

        <Form.Item label="任务保障等级" extra="限流时，资源优先分配给任务保障等级高的采集任务">
          {getFieldDecorator('step3_limitPriority', {
            initialValue: 1,
            rules: [{ required: true, message: '请选择日志内容是否过滤' }],
          })(
            <Radio.Group>
              <Radio value={2}>低</Radio>
              <Radio value={1}>中</Radio>
              <Radio value={0}>高</Radio>
            </Radio.Group>
          )}
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
            extra={<a>{activeKeys?.length ? <>收起&nbsp;<UpOutlined /></> : <>展开&nbsp;<DownOutlined /></>}</a>}
            showArrow={false}
            key="high"
            style={customPanelStyle}
          >
            <Form.Item label="配置信息">
              {getFieldDecorator('step4_advancedConfigurationJsonString', {
                initialValue: '',
              })(
                <MonacoEditor height={120} />
              )}
            </Form.Item>
          </Panel>
        </Collapse>
      </Form>
    </div>
  );
};


export default connect(mapStateToProps)(ClientClearSelfMonitor);
