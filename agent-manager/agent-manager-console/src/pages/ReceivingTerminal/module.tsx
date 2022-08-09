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
  notification,
  message,
  Modal,
} from '@didi/dcloud-design';
import { request } from '../../request/index';
import { getTopics } from '../../api/agent';

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

export const getAgentId = (AgentId: number) => {
  return request(`/api/v1/normal/receivers/rela-agent-exists/${AgentId}`);
};

export const getLogCollectTaskId = (LogCollectTaskId: number) => {
  return request(`/api/v1/normal/receivers/rela-logcollecttask-exists/${LogCollectTaskId}`);
};

export const deleteReceive = (id: number) => {
  return request(`/api/v1/op/receivers/${id}`, {
    method: 'DELETE',
  });
};

export const modifyReceive = (params: any) => {
  return request('/api/v1/op/receivers', {
    method: 'PUT',
    data: JSON.stringify(params),
  });
};

export const addReceive = (params: any) => {
  return request('/api/v1/op/receivers', {
    method: 'POST',
    data: JSON.stringify(params),
  });
};

// kafka集群 => 新增集群
const mockClister = {
  agentMetricsTopic: 'agentMetricsTopic',
  agentErrorLogsTopic: 'agentErrorLogsTopic',
};
export const ActionClusterForm = (props: any) => {
  // const [cluster, setHcluster] = useState(formData.record);
  const [cluster, setHcluster] = useState(props.containerData);
  const [checkValue, setCheckValue] = useState<any[]>([]);
  const [agentMetricsTopic, setAgentMetricsTopic] = useState<any>(props.containerData.agentMetricsTopic);
  // const [agentMetricsTopic, setAgentMetricsTopic] = useState<any>(0);
  const [agentErrorLogsTopic, setAgentErrorLogsTopic] = useState<any>(props.containerData.agentErrorLogsTopic);
  // const [agentErrorLogsTopic, setAgentErrorLogsTopic] = useState<any>(0);
  const agentMetricsTopicList = cluster?.listData && cluster.listData.filter((item: { agentMetricsTopic: any }) => item.agentMetricsTopic);
  const agentErrorLogsTopicList =
    cluster.listData && cluster.listData.filter((item: { agentErrorLogsTopic: any }) => item.agentErrorLogsTopic);
  const [edit, setEdit] = useState(false);
  const [receiverTopic, setReceiverTopic] = useState([] as any);
  const [receiverErrorTopic, setErrorReceiverTopic] = useState([] as any);
  const [originReceiverTopic, setOriginReceiverTopic] = useState([] as any);

  const handleChange = (e: any) => {
    setCheckValue(e);
    if (!edit) {
      if (e.length) {
        props.form.setFieldsValue({
          kafkaClusterProducerInitConfiguration:
            'acks=-1,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer,max.in.flight.requests.per.connection=1',
        });
      } else {
        props.form.setFieldsValue({
          kafkaClusterProducerInitConfiguration:
            'acks=-1,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer,max.in.flight.requests.per.connection=1,compression.type=lz4',
        });
      }
    }
  };
  useEffect(() => {
    if (cluster) {
      setCheckValue([
        cluster.agentMetricsTopic && agentMetricsTopic.length ? 1 : 0,
        cluster.agentErrorLogsTopic && agentErrorLogsTopic.length ? 2 : 0,
      ]);
    }
    if (props.containerData?.operationName === '编辑') {
      setEdit(true);
    }
    _getTopics();
  }, []);

  useEffect(() => {
    if (props.submitEvent !== 1) {
      props.form.validateFields().then((values) => {
        console.log(values, '子组件触发父组件的确认标识');
        if (props.containerData && props.containerData.id) {
          const params = {
            id: props.containerData.id,
            kafkaClusterName: values.kafkaClusterName,
            kafkaClusterBrokerConfiguration: values.kafkaClusterBrokerConfiguration,
            kafkaClusterProducerInitConfiguration: values.kafkaClusterProducerInitConfiguration,
            agentMetricsTopic: values.agentMetricsTopic,
            agentErrorLogsTopic: values.agentErrorLogsTopic,
          };
          return modifyReceive(params)
            .then((res: any) => {
              message.success('修改成功！');
              props.genData(); //调用传入的genData函数更新列表数据
              props.setVisible(false);
            })
            .catch((err: any) => {
              message.error(err.message);
            });
        } else {
          const params = {
            kafkaClusterName: values.kafkaClusterName,
            kafkaClusterBrokerConfiguration: values.kafkaClusterBrokerConfiguration,
            kafkaClusterProducerInitConfiguration: values.kafkaClusterProducerInitConfiguration,
            agentMetricsTopic: values.agentMetricsTopic,
            agentErrorLogsTopic: values.agentErrorLogsTopic,
            receiverType: 0,
          };
          return addReceive(params)
            .then((res: any) => {
              message.success('新增成功！');
              props.genData(); //调用传入的genData函数更新列表数据
              props.setVisible(false);
            })
            .catch((err: any) => {
              message.error(err.message);
            });
        }
      });
    }
  }, [props.submitEvent]);

  useEffect(() => {
    if (!edit) {
      props.form.setFieldsValue({
        kafkaClusterProducerInitConfiguration:
          'acks=-1,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer,max.in.flight.requests.per.connection=1,compression.type=lz4',
      });
    }
  }, [edit]);

  const _getTopics = async () => {
    const res = await getTopics();
    const data = res.map((ele) => {
      return { label: ele, value: ele };
    });
    setReceiverTopic(data);
    setErrorReceiverTopic(data);
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
  const onSearchTopic = (searchText: string) => {
    if (!searchText) {
      setErrorReceiverTopic(originReceiverTopic);
    } else {
      const filterTopic = originReceiverTopic.filter((item: any) => item.label.indexOf(searchText) > -1);
      setErrorReceiverTopic(filterTopic);
    }
  };

  return (
    <div style={{ padding: '0 24px' }}>
      <Form form={props.form} name="actionApp" layout="vertical">
        <Form.Item
          label="集群名："
          name="kafkaClusterName"
          initialValue={cluster && cluster.kafkaClusterName}
          rules={[
            {
              required: true,
              message: '请输入集群名，支持大小写中英文字母、数字、下划线、短横线，32位限制',
              // validator: (rule: any, value: string) => {
              //   return !!value && new RegExp(regName).test(value);
              // },
              validator: (_, v, c) => {
                if (!!v && new RegExp(regName).test(v)) {
                  c();
                  return;
                }
                c('请输入集群名，支持大小写中英文字母、数字、下划线、短横线，32位限制');
              },
              // initialValue: cluster && cluster.kafkaClusterName,
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
        <Form.Item
          label="集群地址："
          name="kafkaClusterBrokerConfiguration"
          initialValue={cluster && cluster.kafkaClusterBrokerConfiguration}
          rules={[
            {
              required: true,
              message: '请输入集群地址，支持限制中英文字母、大小写、数字、下划线、短横线、点、冒号、逗号，32位限制',
              // validator: (rule: any, value: string) => {
              //   return !!value && new RegExp(regAdress).test(value);
              // },
              validator: (_, v, c) => {
                if (!!v && new RegExp(regAdress).test(v)) {
                  c();
                  return;
                }
                c('请输入集群地址，支持限制中英文字母、大小写、数字、下划线、短横线、点、冒号、逗号，32位限制');
              },
              // initialValue: cluster && cluster.kafkaClusterBrokerConfiguration,
            },
          ]}
        >
          <Input placeholder="请输入" />
        </Form.Item>
        <Form.Item
          label="生产端初始化属性："
          name="kafkaClusterProducerInitConfiguration"
          initialValue={cluster && cluster.kafkaClusterProducerInitConfiguration}
          rules={[
            {
              required: true,
              // message: '请输入生产端初始化属性',
              validator: (rule: any, value: string, cb: any) => {
                if (!value) cb('请输入生产端初始化属性');
                if (!new RegExp(regProducerName).test(value)) {
                  cb('最大输入长度为1024位');
                }
                cb();
              },
              // initialValue: cluster && cluster.kafkaClusterProducerInitConfiguration,
            },
          ]}
        >
          <TextArea placeholder="请输入" />
        </Form.Item>
        {/* 设置默认接受集群 需要做判断是否存在禁用按钮 */}
        <div className="metricsCheck" style={{ marginBottom: '20px' }}>
          <Checkbox.Group value={[...checkValue]} style={{ width: '100%', display: 'flex' }} onChange={handleChange}>
            {agentMetricsTopicList?.length && !cluster?.agentMetricsTopic ? null : (
              <Tooltip
                title={
                  agentMetricsTopicList?.length && !cluster?.agentMetricsTopic
                    ? `当前存在默认指标流接收集群：${agentMetricsTopicList[0]?.agentMetricsTopic || '-'}，请取消选定后再设置新集群。`
                    : null
                }
              >
                <Checkbox style={{ marginRight: '30px' }} disabled={agentMetricsTopicList?.length && !cluster?.agentMetricsTopic} value={1}>
                  设置为默认指标流接受集群
                </Checkbox>
              </Tooltip>
            )}
            {agentErrorLogsTopicList?.length && !cluster?.agentErrorLogsTopic ? null : (
              <Tooltip
                title={
                  agentErrorLogsTopicList?.length && !cluster?.agentErrorLogsTopic
                    ? `当前存在默认错误日志流接受集群：${agentErrorLogsTopicList[0]?.agentErrorLogsTopic || '-'
                    }，请取消选定后再设置新集群。`
                    : null
                }
              >
                <Checkbox disabled={agentErrorLogsTopicList?.length && !cluster?.agentErrorLogsTopic} value={2}>
                  设置为默认错误日志流接受集群
                </Checkbox>
              </Tooltip>
            )}
          </Checkbox.Group>
        </div>
        {checkValue.includes(1) && (
          <Form.Item
            label="指标流接收Topic"
            name="agentMetricsTopic"
            initialValue={cluster && cluster.agentMetricsTopic}
            rules={[
              {
                required: true,
                message: '请输入',
                // validator: (rule: any, value: string) => {
                //   return !!value && new RegExp(regAdress).test(value);
                // },
                validator: (_, v, c) => {
                  if (!!v && new RegExp(regAdress).test(v)) {
                    c();
                    return;
                  }
                  c('请输入');
                },
                // initialValue: cluster && cluster.agentMetricsTopic,
              },
            ]}
          >
            <AutoComplete placeholder="请选择或输入" options={receiverTopic} onSearch={onSearch} />
          </Form.Item>
        )}
        {checkValue.includes(2) && (
          <Form.Item
            label="错误日志流接收Topic"
            name="agentErrorLogsTopic"
            initialValue={cluster && cluster.agentErrorLogsTopic}
            rules={[
              {
                required: true,
                message: '请输入',
                // validator: (rule: any, value: string) => {
                //   return !!value && new RegExp(regAdress).test(value);
                // },
                validator: (_, v, c) => {
                  if (!!v && new RegExp(regAdress).test(v)) {
                    c();
                    return;
                  }
                  c('请输入');
                },
                // initialValue: cluster && cluster.agentErrorLogsTopic,
              },
            ]}
          >
            <AutoComplete placeholder="请选择或输入" options={receiverErrorTopic} onSearch={onSearchTopic} />
          </Form.Item>
        )}
      </Form>
    </div>
  );
};

// 删除集群
export const DeleteActionCluster: React.FC = (props: any) => {
  console.log(props, 'DeleteAgentVersion');
  const { containerData, genData } = props;
  const [deleteTip, setDeleteTip] = useState<any>(null);

  useEffect(() => {
    const agentId = !containerData?.isBatch ? containerData?.id : containerData?.selectRowKeys?.join();
    getAgentId(agentId)
      .then((res: any) => {
        getLogCollectTaskId(agentId)
          .then((res: any) => {
            setDeleteTip(true);
          })
          .catch((err) => setDeleteTip(false));
      })
      .catch((err) => setDeleteTip(false));
  }, []);

  useEffect(() => {
    if (props.submitEvent !== 1) {
      console.log(props.submitEvent, 'props.submitEvent');
      if (containerData?.id && !containerData?.isBatch) {
        // 删除版本单行操作
        deleteReceive(props.containerData.id)
          .then((res: any) => {
            props.setVisible(false);
            props.genData();
            notification.success({
              message: '删除成功',
              duration: 3,
            });
          })
          .finally(() => props.setVisible(false));
      } else if (containerData?.selectRowKeys.length > 0 && containerData?.isBatch) {
        // 删除版本批量操作 需要替换接口
        deleteReceive(containerData?.selectRowKeys?.join())
          .then((res: any) => {
            props.setVisible(false);
            props.genData();
            notification.success({
              message: '删除成功',
              duration: 3,
            });
          })
          .finally(() => props.setVisible(false));
      }
    }
  }, [props.submitEvent]);
  return (
    <div style={{ padding: '0 24px' }}>
      {deleteTip ? (
        <p style={{ fontSize: '13px' }}>
          {props.containerData?.kafkaClusterName}存在关联的Agent及采集任务，删除可能导致Agent和采集任务运行异常，是否继续？
        </p>
      ) : (
        <p>是否确认删除{props.containerData?.kafkaClusterName}？</p>
      )}
      <p>删除操作不可恢复，请谨慎操作！</p>
    </div>
  );
};

export default {};
