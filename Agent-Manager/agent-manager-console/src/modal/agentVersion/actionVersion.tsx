import React, { useState } from 'react';
import * as actions from '../../actions';
import '../../container/agent-management/index.less';
import { Modal, Form, Input, message, Button, Upload, Icon } from 'antd';
import { connect } from "react-redux";
import { IFormProps } from '../../interface/common';
import { computeChecksumMd5 } from '../../lib/utils';
import { addAgentVersion, modifyAgentVersion } from '../../api/agentVersion';

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const ActionVersion = (props: { dispatch: any, params: any }) => {
  const ref = React.createRef();
  const { getData } = props.params;
  const handleOk = async (e: React.FormEvent) => {
    e.preventDefault();
    const form = ref.current as any;
    if (props.params.record && props.params.record.agentVersionId) {
      const params = {
        id: props.params.record.agentVersionId,
        agentVersionDescription: form.getFieldsValue().agentVersionDescription ? form.getFieldsValue().agentVersionDescription : '',
      };
      return modifyAgentVersion(params).then((res: any) => {
        props.dispatch(actions.setModalId(''));
        message.success('修改成功！');
        getData()
      }).catch((err: any) => {
        message.error(err.message);
      });
    } else {
      form.validateFields(async (err: any, values: any) => {
        if (err) { return false };
        values.file = values.uploadFile.fileList[0].originFileObj;
        return computeChecksumMd5(values.file).then((md5: any) => {
          const params = {
            agentPackageName: values.file.name,
            fileMd5: md5,
            ...values,
            agentVersionDescription: values.agentVersionDescription ? values.agentVersionDescription : '',
          };
          return addAgentVersion(params).then((res: any) => {
            message.success('新增成功！');
            props.params?.cb();
            props.dispatch(actions.setModalId(''));
          })
        });
      });
    };
  }

  const handleCancel = () => {
    props.dispatch(actions.setModalId(''))
  }

  return (
    <Modal
      title={(props.params.record?.agentVersionId ? "修改" : "新增") + "版本"}
      visible={true}
      onOk={handleOk}
      onCancel={handleCancel}
    >
      <WrappedVersionForm ref={ref} params={props.params} />
    </Modal>
  )
}

const actionAppLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

const ActionVersionForm = (props: IFormProps) => {
  const { getFieldDecorator } = props.form;
  const [isFirst, setisFirst] = useState(true);
  const [version, setVersion] = useState({
    agentVersion: '',
    uploadFile: '',
    agentVersionDescription: '',
    agentPackageName: ''
  });
  if (isFirst && props.formData) {
    setVersion({ ...props.formData.record })
    setisFirst(false);
  }
  return (
    <Form
      className="new-host"
      {...actionAppLayout}
    >
      <Form.Item label="版本号：">
        {version.agentVersion ? version.agentVersion : getFieldDecorator('agentVersion', {
          rules: [{
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
              }
              catch (err) {
                callback(err);
              }
            }
          }],
        })(
          <Input placeholder="请输入版本号3位格式的版本号，如1.0.0" />,
        )}
      </Form.Item>
      <Form.Item label="版本包：">
        {version.agentPackageName ? version.agentPackageName : getFieldDecorator('uploadFile', {
          rules: [{
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
          }],
        })(
          <Upload beforeUpload={(file: any) => false}>
            <Button><Icon type="upload" />点击上传</Button>
          </Upload>,
        )}
      </Form.Item>
      <Form.Item label="版本描述：">
        {getFieldDecorator('agentVersionDescription', {
          initialValue: version && version.agentVersionDescription,
          rules: [{
            required: false,
            message: '请输入该版本描述，50字以内',
          }],
        })(
          <Input placeholder="请输入该版本描述，50字以内" maxLength={50} />,
        )}
      </Form.Item>
    </Form>
  )
}

const WrappedActionVersionForm = Form.create<IFormProps>()(ActionVersionForm);

const WrappedVersionForm = React.forwardRef<any, any>((props, ref) => (
  <WrappedActionVersionForm ref={ref} formData={props.params} />
));

export default connect(mapStateToProps)(ActionVersion);
