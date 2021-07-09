import React, { useState, useEffect } from 'react';
import * as actions from '../../actions';
import { Modal, Form, Select } from 'antd';
import { connect } from "react-redux";
import { agentTaskType } from '../../constants/common';
import { IFormProps } from '../../interface/common';
import { createOperationTasks, getAgentVersion } from '../../api/agent';
import { IAgentHostSet, IOperationTasksParams, IAgentVersion } from '../../interface/agent';

const { Option } = Select;

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const InstallHost = (props: { dispatch: any, params: any }) => {
  const ref = React.createRef();
  // console.log('props---', props.params);
  const { taskType, hosts, cb } = props.params;
  const [agentVersions, setAgentVersions] = useState([] as IAgentVersion[]);

  const handleModifyOk = (e: React.FormEvent) => {
    e.preventDefault();
    const form = ref.current as any;
    form.validateFields((err: any, values: any) => {
      if (err) { return false };
      return createTasks(values);
    });
  }

  const createTasks = (values: any) => {
    const hostIds = taskType === 0 ? hosts.map((ele: IAgentHostSet) => ele.hostId) : []; // 安装 0
    const agentIds = taskType === 2 ? hosts.map((ele: IAgentHostSet) => ele.agentId) : []; // 升级 2
    const params = {
      agentIds,
      checkAgentCompleteCollect: '', // 1检查 0 不检查
      agentVersionId: values.agentVersionId, // 安装 升级
      hostIds,
      taskType,
    } as unknown as IOperationTasksParams;
    createOperationTasks(params).then((res: number) => {
      props.dispatch(actions.setModalId(''));
      const judge = taskType === 0 ? hostIds : agentIds;
      Modal.success({
        title: <><a href="/agent/operationTasks">{judge?.length > 1 ? '批量' : hosts[0]?.hostName}{agentTaskType[taskType]}Agent任务(任务ID：{res}）</a>创建成功！</>,
        content: '可点击标题跳转，或至“Agent中心”>“运维任务”模块查看详情',
        okText: '确认',
        onOk: () => {
          cb();
          props.dispatch(actions.setModalId(''));
        },
      });
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  const handleModifyCancel = () => {
    props.dispatch(actions.setModalId(''));
  }

  const getAgentVersionData = () => {
    getAgentVersion().then((res: IAgentVersion[]) => {
      setAgentVersions(res);
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  useEffect(() => {
    getAgentVersionData();
  }, []);

  return (
    <Modal
      title={`${agentTaskType[taskType]}agent`}
      visible={true}
      onOk={handleModifyOk}
      onCancel={handleModifyCancel}
    >
      <WrappedInstallHostForm
        ref={ref}
        taskType={taskType}
        agentVersions={agentVersions}
      />
    </Modal>
  )
}

const newHostLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

const InstallHostForm = (props: IFormProps & IInstallHostProps) => {
  const { getFieldDecorator } = props.form;

  return (
    <Form
      {...newHostLayout}
    >
      <Form.Item label="Agent版本">
        {getFieldDecorator('agentVersionId', {
          rules: [{ required: true, message: '请选择' }],
        })(
          <Select showSearch placeholder="请选择">
            {props.agentVersions.map((v, index) => (
              <Option key={index} value={v.agentVersionId}>{v.agentVersion}</Option>
            ))}
          </Select>,
        )}
      </Form.Item>
    </Form>
  )
}
interface IInstallHostProps {
  taskType: number;
  agentVersions: IAgentVersion[];
}

const OuterInstallHostForm = Form.create<IFormProps & IInstallHostProps>()(InstallHostForm);

type FormRefProps = {
  ref: any;
}
const WrappedInstallHostForm = React.forwardRef<any, FormRefProps & IInstallHostProps>((props, ref) => (
  <OuterInstallHostForm ref={ref} taskType={props.taskType} agentVersions={props.agentVersions} />
));


export default connect(mapStateToProps)(InstallHost);
