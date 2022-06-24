import React, { useState, useEffect } from 'react';
import { AutoComplete, Select, Form, Input } from '@didi/dcloud-design';
import { contentFormItemLayout } from './config';
// import { NavRouterLink } from '../../component/CustomComponent';
// import MonacoEditor from '../../component/editor/monacoEditor';
import { IReceivers } from '../../interface/agent';
// import { getReceivers, getReceiversTopic } from '../../api/agent';
import { DataSourceItemType } from '../../interface/common';

// interface IReceiverAdvancedProps extends FormComponentProps { }

const ReceiverAdvancedConfiguration = (props: any) => {
  // const { getFieldDecorator } = props.form;
  const [receivers, setReceivers] = useState([] as IReceivers[]);
  const [receiverTopic, setReceiverTopic] = useState([] as DataSourceItemType[]);

  const onReceiverChange = (value: number) => {
    // getReceiverTopic(value); // 等对接Kafka集群时再修复
  };

  const getReceiversList = () => {
    // getReceivers()
    //   .then((res: IReceivers[]) => {
    //     setReceivers(res);
    //   })
    //   .catch((err: any) => {
    //     // console.log(err);
    //   });
  };

  const getReceiverTopic = (id: number) => {
    // getReceiversTopic(id)
    //   .then((res: string[]) => {
    //     const data = res.map((ele) => {
    //       return { text: ele, value: ele };
    //     });
    //     setReceiverTopic(data);
    //   })
    //   .catch((err: any) => {
    //     // console.log(err);
    //   });
  };

  useEffect(() => {
    getReceiversList();
  }, []);

  return (
    <div>
      <Form {...contentFormItemLayout}>
        <Form.Item
          label="Kafka集群"
          extra={
            <>
              如目标集群不在下拉列表，请至
              {/* <a   href="/collect/detail">接收端管理>Kafka集群</a>} */}
              新增
            </>
          }
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
        <Form.Item label="Topic">
          {/* {getFieldDecorator('step4_sendTopic', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: '',
            rules: [{ required: true, message: '请选择Topic' }],
          })(<AutoComplete placeholder="请选择或输入" dataSource={receiverTopic} children={<Input />} />)} */}
          <AutoComplete placeholder="请选择或输入" dataSource={receiverTopic} children={<Input />} />
        </Form.Item>
        <Form.Item label="配置信息">
          {/* {getFieldDecorator('step4_advancedConfigurationJsonString', {
            initialValue: '',
          })(<MonacoEditor {...props} height={120} />)} */}
        </Form.Item>
      </Form>
    </div>
  );
};

export default ReceiverAdvancedConfiguration;
