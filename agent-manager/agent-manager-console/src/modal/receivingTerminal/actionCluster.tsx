import React, { useState } from "react";
import * as actions from '../../actions';
import '../../container/agent-management/index.less';
import { Modal, Form, Input, message } from 'antd';
import { connect } from "react-redux";
import { IFormProps } from '../../interface/common';
import { addReceive, modifyReceive } from '../../api/receivingTerminal'
import { IReceivingTerminal } from '../../interface/receivingTerminal';
import { regName, regAdress, regProducerName } from '../../constants/reg';
import TextArea from "antd/lib/input/TextArea";

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const ActionCluster = (props: { dispatch: any, params: any }) => {
  const ref = React.createRef();

  const handleModifyOk = (e: React.FormEvent) => {
    e.preventDefault();
    const form = ref.current as any;
    form.validateFields(async (err: any, values: any) => {
      if (err) { return false; }
      if (props.params.record && props.params.record.id) {
        const params = {
          id: props.params.record.id,
          kafkaClusterName: values.kafkaClusterName,
          kafkaClusterBrokerConfiguration: values.kafkaClusterBrokerConfiguration,
          kafkaClusterProducerInitConfiguration: values.kafkaClusterProducerInitConfiguration,
        } as unknown as IReceivingTerminal;
        return modifyReceive(params).then((res: any) => {
          message.success('修改成功！');
          props.params?.cb();
          props.dispatch(actions.setModalId(''));
        }).catch((err: any) => {
          message.error(err.message);
        });
      } else {
        const params = {
          kafkaClusterName: values.kafkaClusterName,
          kafkaClusterBrokerConfiguration: values.kafkaClusterBrokerConfiguration,
          kafkaClusterProducerInitConfiguration: values.kafkaClusterProducerInitConfiguration,
        } as unknown as IReceivingTerminal;
        return addReceive(params).then((res: any) => {

          message.success('新增成功！');
          props.params?.cb();
          props.dispatch(actions.setModalId(''));
        }).catch((err: any) => {
          message.error(err.message);
        });
      }
    });
  }

  const handleModifyCancel = () => {
    props.dispatch(actions.setModalId(''))
  }

  return (
    <Modal
      title={(props.params.record?.id ? "修改" : "新增") + "集群"}
      visible={true}
      onOk={handleModifyOk}
      onCancel={handleModifyCancel}
    >
      <WrappedClusterForm ref={ref} params={props.params} />
    </Modal>
  )
}

const actionClusterLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};


const ActionClusterForm = (props: IFormProps) => {
  const { getFieldDecorator } = props.form;
  let { formData } = props;
  const [cluster, setHcluster] = useState(formData.record);

  return (
    <Form
      className="new-host"
      {...actionClusterLayout}
    >
      <Form.Item label="集群名：">
        {getFieldDecorator('kafkaClusterName', {
          initialValue: cluster && cluster.kafkaClusterName,
          rules: [{
            required: true,
            message: '请输入集群名，支持大小写中英文字母、数字、下划线、短横线，32位限制',
            validator: (rule: any, value: string) => {
              return !!value && new RegExp(regName).test(value);
            },
          }],
        })(
          <Input placeholder="请输入" />,
        )}
      </Form.Item>
      <Form.Item label="集群地址：">
        {getFieldDecorator('kafkaClusterBrokerConfiguration', {
          initialValue: cluster && cluster.kafkaClusterBrokerConfiguration,
          rules: [{
            required: true,
            message: '请输入集群地址，支持限制中英文字母、大小写、数字、下划线、短横线、点、冒号、逗号，32位限制',
            validator: (rule: any, value: string) => {
              return !!value && new RegExp(regAdress).test(value);
            },
          }],
        })(
          <Input placeholder="请输入" />,
        )}
      </Form.Item>
      <Form.Item label="生产端初始化属性：">
        {getFieldDecorator('kafkaClusterProducerInitConfiguration', {
          initialValue: cluster && cluster.kafkaClusterProducerInitConfiguration,
          rules: [{
            required: true,
            // message: '请输入生产端初始化属性',
            validator: (rule: any, value: string, cb: any) => {
              if (!value) cb('请输入生产端初始化属性')
              if (!new RegExp(regProducerName).test(value)) {
                cb('最大输入长度为1024位')
              }
              cb()
            },
          }],
        })(
          <TextArea placeholder="请输入" />,
        )}
      </Form.Item>
    </Form>
  )
}

const WrappedActionClusterForm = Form.create<IFormProps>()(ActionClusterForm);

const WrappedClusterForm = React.forwardRef<any, any>((props, ref) => (
  <WrappedActionClusterForm ref={ref} formData={props.params} />
));

export default connect(mapStateToProps)(ActionCluster);
