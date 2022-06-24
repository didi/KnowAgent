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
} from '@didi/dcloud-design';
import { request } from '../../../../request/index';

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

// 模块列表
export const getRecordModules = () => {
  return request('/api/v1/op/record/listModules');
};

export const NewHostForm: React.FC = (props: any) => {
  console.log(props, '----');
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
    // testHost(hostName)
    //   .then((res: any) => {
    //     setTestHide(true);
    //     setTestLoading(false);
    //     setTestResult(true);
    //   })
    //   .catch((err: any) => {
    //     setTestHide(true);
    //     setTestLoading(false);
    //     setTestResult(false);
    //   });
  };

  const getMachineZones = () => {
    // getHostMachineZone()
    //   .then((res: string[]) => {
    //     const zones = res.filter((ele) => !!judgeEmpty(ele));
    //     setMachineZones(zones);
    //   })
    //   .catch((err: any) => {
    //     // console.log(err);
    //   });
  };

  useEffect(() => {
    getMachineZones();
  }, []);

  return (
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
      <Form.Item
        label="所属机房"
        name="machineZone"
        rules={[
          {
            required: false,
            message: '请输入正确IP地址',
            validator: (_, v, c) => {
              if (!new RegExp(regString128).test(v)) {
                _.message = '最大长度限制128位';
                c('最大长度限制128位');
              } else {
                c();
              }
            },
          },
        ]}
      >
        <AutoComplete
          placeholder="请选择或输入"
          // dataSource={getMachineZonesList()}
          children={<Input style={{ border: 'none' }} />}
        />
      </Form.Item>
      <Row>
        <Button htmlType="submit" type="primary" loading={testLoading} onClick={connectTestHost}>
          连接测试
        </Button>
        &nbsp;&nbsp;
        {testHide && <span className={testResult ? 'success' : 'fail'}>{testResult ? '测试成功！' : '测试失败！'}</span>}
      </Row>
    </Form>
  );
};

const agentVersions = [
  {
    key: 1,
    agentVersion: '1.0.1',
    agentVersionId: '999999',
  },
  {
    key: 2,
    agentVersion: '1.0.2',
    agentVersionId: '999998',
  },
];

// 选中的Agent没有采集任务时候调用（安装Agent、升级Agent）
export const InstallHostForm: React.FC = (props: any) => {
  console.log(props, '--InstallHostForm--');
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

// 卸载Agent
// export const UninstallHost: React.FC=(){
//   return
// }

// Agent版本 => 新增版本
export const ActionVersionForm: React.FC = (props: any) => {
  console.log(props, 'ActionVersionForm');
  const [isFirst, setisFirst] = useState(true);
  const [version, setVersion] = useState({
    agentVersion: '',
    uploadFile: '',
    agentVersionDescription: '',
    agentPackageName: '',
  });
  if (isFirst && props.formData) {
    setVersion({ ...props.formData.record });
    setisFirst(false);
  }
  return (
    <Form
      form={props.form}
      name="actionVersion"
      // labelCol={{ span: 4 }}
      // wrapperCol={{ span: 20 }}
      layout="vertical"
    >
      <Form.Item
        label="版本号："
        name="agentVersion"
        rules={[
          {
            required: true,
            message: '请输入版本号3位格式的版本号，如1.0.0',
            validator: (rule: any, value: any, callback: any) => {
              try {
                if (value == '') {
                  callback('请输入版本号');
                } else {
                  if (!/^([1-9]+\.[0-9]+\.[0-9]+)$/.test(value)) {
                    callback('请输入版本号3位格式的版本号，如1.0.0');
                  } else {
                    callback();
                  }
                }
              } catch (err) {
                callback(err);
              }
            },
          },
        ]}
      >
        {version.agentVersion ? version.agentVersion : <Input placeholder="请输入版本号3位格式的版本号，如1.0.0" />}
      </Form.Item>
      <Form.Item
        label="版本包："
        name="uploadFile"
        rules={[
          {
            required: true,
            validator: (rule: any, value: any, callback: any) => {
              if (value?.fileList?.length) {
                if (value.fileList.length > 1) {
                  callback('一次仅支持上传一份文件！');
                } else {
                  callback();
                }
              } else {
                callback(`请上传文件`);
              }
              callback();
            },
          },
        ]}
      >
        {version.agentPackageName ? (
          version.agentPackageName
        ) : (
          <Upload beforeUpload={(file: any) => false}>
            <Button>
              <IconFont type="upload" />
              点击上传
            </Button>
          </Upload>
        )}
      </Form.Item>
      <Form.Item
        label="版本描述："
        name="agentVersionDescription"
        rules={[
          {
            required: false,
            message: '请输入该版本描述，50字以内',
          },
        ]}
      >
        <Input placeholder="请输入该版本描述，50字以内" maxLength={50} />
      </Form.Item>
    </Form>
  );
};

// 删除Agent版本
export const DeleteAgentVersion: React.FC = (props: any) => {
  console.log(props, 'DeleteAgentVersion');
  return <div>确认删除该Agent版本吗？</div>;
};

// 应用管理 => 新增应用
export const ActionAppForm = (props: any) => {
  console.log(props, 'ActionAppForm');

  const [isFirst, setisFirst] = useState(true);
  const [hostList, setHostList] = useState([]);
  const [appForm, setappForm] = useState({
    serviceName: '',
    targetKeys: [],
  });

  const getHostlist = () => {
    getHosts().then((res: any) => {
      const data = res.map((item: any) => {
        return { key: item.id, ...item };
      });
      setHostList(data);
    });
  };

  const filterOption = (inputValue: any, option: any) => {
    return option.hostName.indexOf(inputValue) > -1;
  };

  const handleChange = (targetKeys: any) => {
    setappForm({
      serviceName: appForm.serviceName,
      targetKeys: targetKeys,
    });
  };

  if (isFirst) {
    getHostlist();
    if (props.formData && props.formData.id) {
      // getServiceDetail(props.formData.id).then((res: IApp) => {
      //   setappForm({
      //     serviceName: res.serviceName,
      //     targetKeys: res.hostList.map((item: any) => item.id),
      //   });
      // });
    }
    setisFirst(false);
  }

  return (
    <Form
      form={props.form}
      name="actionApp"
      // labelCol={{ span: 4 }}
      // wrapperCol={{ span: 20 }}
      layout="vertical"
    >
      <Form.Item
        label="应用名："
        name="servicename"
        rules={[
          {
            required: appForm.serviceName ? false : true,
            message: '请输入应用名，支持大小写中英文字母、数字、下划线、短横线,32位限制',
            // validator: (rule: any, value: string) => {
            //   return !!value && new RegExp(regName).test(value);
            // },
            validator: (_, v, c) => {
              if (!!v && new RegExp(regName).test(v)) {
                c();
                return;
              }
              c('请输入应用名，支持大小写中英文字母、数字、下划线、短横线,32位限制');
            },
          },
        ]}
      >
        {appForm.serviceName ? (
          appForm.serviceName
        ) : (
          <Input
            // style={{ width: '590px' }}
            placeholder="请输入"
          />
        )}
      </Form.Item>
      <Form.Item label="关联主机：" name="hostIdList" rules={[{ required: true, message: '请选择' }]}>
        <Transfer
          dataSource={hostList}
          showSearch
          filterOption={filterOption}
          targetKeys={appForm.targetKeys}
          onChange={handleChange}
          render={(item) => item.hostName}
          style={{ width: '100%' }}
          // initialValue: appForm.targetKeys.length > 0 ? appForm.targetKeys : [],
        />
      </Form.Item>
    </Form>
  );
};

// 删除应用
export const DeleteActionApp: React.FC = (props: any) => {
  console.log(props, 'DeleteAgentVersion');
  return (
    <>
      <div>是否确认删除[缺少应用名称]</div>
      <div>删除操作不可恢复，请谨慎操作！</div>
    </>
  );
};

// kafka集群 => 新增集群
const mockClister = {
  agentMetricsTopic: 'agentMetricsTopic',
  agentErrorLogsTopic: 'agentErrorLogsTopic',
};
export const ActionClusterForm = (props: any) => {
  console.log(props, 'ActionClusterForm');

  const { formData } = props;
  // const [cluster, setHcluster] = useState(formData.record);
  const [cluster, setHcluster] = useState(mockClister);
  const [checkValue, setCheckValue] = useState<any[]>([]);
  // const [agentMetricsTopic, setAgentMetricsTopic] = useState<any>(formData.agentMetricsTopic );
  const [agentMetricsTopic, setAgentMetricsTopic] = useState<any>(0);
  // const [agentErrorLogsTopic, setAgentErrorLogsTopic] = useState<any>(formData.agentErrorLogsTopic);
  const [agentErrorLogsTopic, setAgentErrorLogsTopic] = useState<any>(0);
  const checkOption = [
    {
      label: '设置为默认指标流接受集群',
      value: 1,
    },
    {
      label: '设置为默认错误日志流接受集群',
      value: 2,
    },
  ];
  const handleChange = (e: any) => {
    setCheckValue(e);
  };
  useEffect(() => {
    if (cluster) {
      setCheckValue([
        cluster.agentMetricsTopic && agentMetricsTopic.length ? 1 : 0,
        cluster.agentErrorLogsTopic && agentErrorLogsTopic.length ? 2 : 0,
      ]);
    }
  }, []);
  return (
    <Form
      form={props.form}
      name="actionApp"
      // labelCol={{ span: 4 }}
      // wrapperCol={{ span: 20 }}
      layout="vertical"
    >
      <Form.Item
        label="集群名："
        name="kafkaClusterName"
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
        <Checkbox.Group
          value={[...checkValue]}
          style={{ width: '100%', display: 'flex', justifyContent: 'center' }}
          onChange={handleChange}
        >
          <Tooltip
            title={
              agentMetricsTopic.length && !cluster?.agentMetricsTopic
                ? `当前存在默认指标流接收集群：${agentMetricsTopic[0]?.agentMetricsTopic || '-'}，请取消选定后再设置新集群。`
                : null
            }
          >
            <Checkbox style={{ marginRight: '30px' }} disabled={agentMetricsTopic.length && !cluster?.agentMetricsTopic} value={1}>
              设置为默认指标流接受集群
            </Checkbox>
          </Tooltip>
          <Tooltip
            title={
              agentErrorLogsTopic.length && !cluster?.agentErrorLogsTopic
                ? `当前存在默认错误日志流接受集群：${agentErrorLogsTopic[0]?.agentErrorLogsTopic || '-'}，请取消选定后再设置新集群。`
                : null
            }
          >
            <Checkbox disabled={agentErrorLogsTopic.length && !cluster?.agentErrorLogsTopic} value={2}>
              设置为默认错误日志流接受集群
            </Checkbox>
          </Tooltip>
        </Checkbox.Group>
      </div>
      {checkValue.includes(1) && (
        <Form.Item
          label="指标流接收Topic"
          name="agentMetricsTopic"
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
          <Input placeholder="请输入" />
        </Form.Item>
      )}
      {checkValue.includes(2) && (
        <Form.Item
          label="错误日志流接收Topic"
          name="agentErrorLogsTopic"
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
          <Input placeholder="请输入" />
        </Form.Item>
      )}
    </Form>
  );
};
const operateArr = () => {
  return [
    { title: '新增', value: 1 },
    { title: '删除', value: 2 },
    { title: '编辑', value: 3 },
  ];
};

const recordModuleList = async () => {
  const result: any = await getRecordModules();
  return result?.map((item: any) => {
    return { key: item.code, title: item.desc, value: item.code };
  });
};

export default { operateArr, recordModuleList };
