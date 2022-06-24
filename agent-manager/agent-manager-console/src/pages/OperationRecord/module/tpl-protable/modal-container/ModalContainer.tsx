import React, { useState } from 'react';
import { ProTable, Select, Button, IconFont, Form, Modal, Input, Drawer } from '@didi/dcloud-design';
// import { renderTableOpts } from '../../common-pages/render-table-opts'
// import '../style/index.less';
// import moment from "moment";
import './index.less';
interface OpType {
  visible?: any;
  buttonText?: string;
  buttonType?: any;
  CustomContent?: any;

  [attributes: string]: any;
}

const ModalContainer = (props: any) => {
  const { customContent, visible = false, setVisible, containerType = 'modal', containerConfig } = props;
  console.log(props, 'props--');
  const [loading, setLoading] = useState(props.loading);
  const [submitEvent, setSubmitEvent] = useState<number>(1);
  const [form] = Form.useForm();

  const formHandleOk = () => {
    form.validateFields().then((res) => {
      console.log(res, 'res');
      setVisible(false);
    });
  };

  const handleOk = () => {
    setSubmitEvent(Math.random());
  };

  const handleCancel = () => {
    console.log('[handleCancel');
    setVisible(false);
  };

  const renderFooter = () => {
    return (
      <div className={`footer-buttons ${props?.footerLeft ? 'left-layout' : ''}`}>
        <Button style={{ marginRight: '8px' }} onClick={props.onCancel ? props.onCancel : handleCancel}>
          {props.cancelText || '取消'}
        </Button>
        <Button
          loading={props.needLoading ? loading : undefined}
          {...props.footerAttr}
          className="footer-buttons-left"
          type="primary"
          onClick={props.onOk ? props.onOk : handleOk}
        >
          {props.confirmText || '确定'}
        </Button>
      </div>
    );
  };

  return (
    <>
      {containerType && containerType === 'modal' && (
        <Modal
          className={'container-modal'}
          {...containerConfig}
          title={containerConfig?.title || '标题'}
          visible={visible}
          onOk={handleOk}
          onCancel={handleCancel}
          footer={props.footer ? props.footer : renderFooter()}
        >
          {customContent && customContent({ submitEvent, setVisible, form, ...props })}
        </Modal>
      )}
      {containerType && containerType === 'drawer' && (
        <Drawer
          className={'container-drawer'}
          {...containerConfig}
          title={containerConfig?.title || '标题'}
          visible={visible}
          onOk={handleOk}
          onClose={handleCancel}
          // footer={false}
          footer={props.footer ? props.footer : renderFooter()}
          width={1080}
          bodyStyle={{ padding: 0 }}
        >
          {customContent && customContent({ submitEvent, form, setVisible, ...props })}
        </Drawer>
      )}
    </>
  );
};

export default ModalContainer;
