import React, { useState, useEffect } from 'react';
import * as actions from '../../actions';
import { Modal, Form, Input, Select, Tooltip, Button, Row, AutoComplete, message } from 'antd';
import { connect } from "react-redux";
import { hostTypes } from '../../constants/common';
import { IFormProps } from '../../interface/common';
import { regName, regIp } from '../../constants/reg';
import { addOpHosts, testHost, getHosts, getHostMachineZone } from '../../api/agent'
import { IAddOpHostsParams, IHosts } from '../../interface/agent';
import { judgeEmpty } from '../../lib/utils';

const { Option } = Select;

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const NewHost = (props: { dispatch: any, params: any }) => {
  const ref = React.createRef();

  const handleModifyOk = (e: React.FormEvent) => {
    e.preventDefault();
    const form = ref.current as any;
    form.validateFields(async (err: any, values: any) => {
      if (err) { return false; }
      const addParams = {
        container: values?.container,
        hostName: values?.hostName,
        ip: values?.ip,
        parentHostName: values?.parentHostName || '',
        machineZone: values?.machineZone || '',
        department: '',
        id: '',
      } as unknown as IAddOpHostsParams;
      return addOpHosts(addParams).then((res: any) => {
        Modal.success({
          content: '新增成功！',
          okText: '确认',
        });
        props.params?.cb();
        props.dispatch(actions.setModalId(''))
      }).catch((err: any) => {
        // console.log(err);
      });
    });
  }

  const handleModifyCancel = () => {
    props.dispatch(actions.setModalId(''))
  }

  return (
    <Modal
      title="新增主机"
      visible={true}
      onOk={handleModifyOk}
      onCancel={handleModifyCancel}
    >
      <WrappedAddHostForm ref={ref} />
    </Modal>
  )
}

const newHostLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

const NewHostForm = (props: IFormProps) => {
  const { getFieldDecorator } = props.form;
  const [hostType, setHostTypes] = useState(null);
  const [machineZones, setMachineZones] = useState([] as string[]);
  const [suHostList, setSuHostList] = useState([] as IHosts[]);
  const [testLoading, setTestLoading] = useState(false);
  const [testHide, setTestHide] = useState(false);
  const [testResult, setTestResult] = useState(false);

  const getMachineZonesList = () => {
    const zonesList = machineZones.map((ele: string) => { return { value: ele, text: ele } });
    return zonesList;
  }

  const changeHostType = (val: any) => {
    setHostTypes(val);
    getSuHostlist();
  }

  const getSuHostlist = () => {
    getHosts().then((res: IHosts[]) => {
      const data = res.filter(ele => ele.container === 0);
      setSuHostList(data);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  const connectTestHost = () => {
    props.form.validateFields((err: any, values: any) => {
      if (err) { return false; }
      return getTestHost(values.hostName);
    });
  }

  const getTestHost = (hostName: string) => {
    setTestLoading(true);
    testHost(hostName).then((res: any) => {
      setTestHide(true);
      setTestLoading(false);
      setTestResult(true);
    }).catch((err: any) => {
      setTestHide(true);
      setTestLoading(false);
      setTestResult(false);
    });
  }

  const getMachineZones = () => {
    getHostMachineZone().then((res: string[]) => {
      const zones = res.filter(ele => !!judgeEmpty(ele));
      setMachineZones(zones);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  useEffect(() => {
    getMachineZones();
  }, []);

  return (
    <Form
      {...newHostLayout}
    >
      <Form.Item label="主机类型">
        {getFieldDecorator('container', {
          rules: [{ required: true, message: '请选择' }],
        })(
          <Select showSearch placeholder="请选择" onChange={changeHostType}>
            {hostTypes.map((v, index) => (
              <Option key={index} value={v.value}>{v.label}</Option>
            ))}
          </Select>,
        )}
      </Form.Item>
      <Form.Item label="主机名">
        {getFieldDecorator('hostName', {
          rules: [{
            required: true,
            message: '请输入主机名，支持大小写中英文字母、数字、下划线、点、短横线,32位限制',
            validator: (rule: any, value: string) => {
              return !!value && new RegExp(regName).test(value);
            },
          }],
        })(
          <Input placeholder="请输入" />,
        )}
      </Form.Item>
      <Form.Item label="主机IP">
        {getFieldDecorator('ip', {
          rules: [{
            required: true,
            message: '请输入正确IP地址',
            validator: (rule: any, value: string) => {
              return !!value && new RegExp(regIp).test(value);
            },
          }],
        })(
          <Input placeholder="请输入" />,
        )}
      </Form.Item>
      {hostType === 1 ? // 选择容器时，接口则需筛选展示主机数据
        <Form.Item label="宿主机名">
          {getFieldDecorator('parentHostName', {
            rules: [{ required: true, message: '请输入' }],
          })(
            <Select placeholder="请选择" >
              {suHostList.map((v: any, index: number) => (
                <Option key={index} value={v.hostName}>
                  {v.hostName.length > 15 ? <Tooltip placement="bottomLeft" title={v.hostName}>{v.hostName}</Tooltip> : v.hostName}
                </Option>
              ))}
            </Select>,
          )}
        </Form.Item>
        : null}
      <Form.Item label="所属机房">
        {getFieldDecorator('machineZone', {
          rules: [{ required: false }],
        })(
          <AutoComplete
            placeholder="请选择或输入"
            dataSource={getMachineZonesList()}
            children={<Input />}
          />,
        )}
      </Form.Item>
      <Row>
        <Button htmlType="submit" type="primary" loading={testLoading} onClick={connectTestHost}>连接测试</Button>&nbsp;&nbsp;
        {testHide && <span className={testResult ? 'success' : 'fail'}>{testResult ? '测试成功！' : '测试失败！'}</span>}
      </Row>
    </Form>
  )
}

const WrappedNewHostForm = Form.create<IFormProps>()(NewHostForm);
type FormRefProps = {
  ref: any;
}

const WrappedAddHostForm = React.forwardRef<any, FormRefProps>((props, ref) => (
  <WrappedNewHostForm ref={ref} />
));


export default connect(mapStateToProps)(NewHost);
