import React, { useState } from 'react';
import * as actions from '../../actions';
import '../../container/data-source/index.less';
import { Modal, Form, Input, Transfer, Tooltip, message } from 'antd';
import { connect } from "react-redux";
import { IFormProps } from '../../interface/common';
import { addService, modifyService, getServiceDetail } from '../../api/dataSource'
import { IDataSource, IApp } from '../../interface/dataSource';
import { getHosts } from '../../api/agent';
import { regName } from '../../constants/reg';

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const ActionApp = (props: { dispatch: any, params: any }) => {
  const ref = React.createRef();

  const handleModifyOk = (e: React.FormEvent) => {
    e.preventDefault();
    const form = ref.current as any;
    form.validateFields(async (err: any, values: any) => {
      if (err) { return false; }
      if (props.params && props.params.id) {
        const params = {
          id: props.params.id,
          hostIdList: values.hostIdList,
          servicename: values.servicename,
        } as unknown as IDataSource;
        return modifyService(params).then((res: any) => {
          message.success('修改成功！');
          props.params?.cb();
          props.dispatch(actions.setModalId(''));
        }).catch((err: any) => {
          message.error(err.message);
        });
      } else {
        const params = {
          hostIdList: values.hostIdList,
          servicename: values.servicename,
        } as unknown as IDataSource;
        return addService(params).then((res: any) => {
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
      title={(props.params?.id ? "修改" : "新增") + "应用"}
      visible={true}
      width={728}
      onOk={handleModifyOk}
      onCancel={handleModifyCancel}
      okText='确认'
    >
      <WrappedAppForm ref={ref} params={props.params} />
    </Modal>
  )
}

const actionAppLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

const ActionAppForm = (props: IFormProps) => {
  const { getFieldDecorator } = props.form;
  const [isFirst, setisFirst] = useState(true);
  const [hostList, setHostList] = useState([]);
  const [appForm, setappForm] = useState({
    serviceName: '',
    targetKeys: []
  });

  const getHostlist = () => {
    getHosts().then((res: any) => {
      const data = res.map((item: any) => {
        return { key: item.id, ...item }
      });
      setHostList(data);
    });
  }

  const filterOption = (inputValue: any, option: any) => {
    return option.hostName.indexOf(inputValue) > -1;
  }

  const handleChange = (targetKeys: any) => {
    setappForm({
      serviceName: appForm.serviceName,
      targetKeys: targetKeys
    });
  };

  if (isFirst) {
    getHostlist();
    if (props.formData && props.formData.id) {
      getServiceDetail(props.formData.id).then((res: IApp) => {
        setappForm({
          serviceName: res.serviceName,
          targetKeys: res.hostList.map((item: any) => item.id)
        });
      });
    }
    setisFirst(false);
  }

  return (
    <Form
      className="new-app"
      {...actionAppLayout}
    >
      <Form.Item label="应用名：">
        {appForm.serviceName ? appForm.serviceName : getFieldDecorator('servicename', {
          initialValue: appForm && appForm.serviceName,
          rules: [{
            required: appForm.serviceName ? false : true,
            message: '请输入应用名，支持大小写中英文字母、数字、下划线、短横线,32位限制',
            validator: (rule: any, value: string) => {
              return !!value && new RegExp(regName).test(value);
            },
          }],
        })(
          <Input placeholder="请输入" />,
        )}
      </Form.Item>
      <Form.Item label="关联主机：">
        {getFieldDecorator('hostIdList', {
          initialValue: appForm.targetKeys.length > 0 ? appForm.targetKeys : [],
          rules: [{ required: true, message: '请选择' }],
        })(
          <Transfer
            dataSource={hostList}
            showSearch
            filterOption={filterOption}
            targetKeys={appForm.targetKeys}
            onChange={handleChange}
            render={item => item.hostName}
          />
        )}
      </Form.Item>
    </Form>
  )
}

const WrappedActionAppForm = Form.create<IFormProps>()(ActionAppForm);

const WrappedAppForm = React.forwardRef<any, any>((props, ref) => (
  <WrappedActionAppForm ref={ref} formData={props.params} />
));

export default connect(mapStateToProps)(ActionApp);

