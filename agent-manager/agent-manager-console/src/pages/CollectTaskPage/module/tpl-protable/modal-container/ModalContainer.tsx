import React, { useState } from 'react';
import { ProTable, Select, Button, IconFont, Form, Modal, Input, Drawer } from '@didi/dcloud-design';
// import { renderTableOpts } from '../../common-pages/render-table-opts'
// import '../style/index.less';
// import moment from "moment";

interface OpType {
  visible?: any;
  buttonText?: string;
  buttonType?: any;
  CustomContent?: any;

  [attributes: string]: any;
}

const ModalContainer = (props: any) => {
  const {
    buttonText = '确认',
    buttonType = '',
    customContent,
    title,
    visible = false,
    setVisible,
    containerType = 'modal',
    containerConfig,
    children,
  } = props;
  console.log(props, 'props--');
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [loading, setLoading] = useState(props.loading);
  const [form] = Form.useForm();
  const showModal = () => {
    setIsModalVisible(true);
  };

  const formHandleOk = () => {
    form.validateFields().then((res) => {
      console.log(res, 'res');
      setVisible(false);
    });
  };

  const handleOk = () => {
    setVisible(false);
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
          onClick={props.onOk ? props.onOk : formHandleOk}
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
          {...containerConfig}
          title={containerConfig?.title || '标题'}
          visible={visible}
          onOk={handleOk}
          onCancel={handleCancel}
          footer={props.footer ? props.footer : renderFooter()}
        >
          {/* {children} */}
          {/* <CustomContent {...{ form, ...props }} /> */}
          {/* {customContent ? customContent({ form, ...props }) : null} */}
          {customContent && customContent({ ...props, form })}
          {/* {children} */}
        </Modal>
      )}
      {containerType && containerType === 'drawer' && (
        <Drawer
          {...containerConfig}
          title={containerConfig?.title || '标题'}
          visible={visible}
          onOk={handleOk}
          onClose={handleCancel}
          footer={props.footer ? props.footer : renderFooter()}
        >
          {/* {children} */}
          {/* <CustomContent {...{ form, ...props }} /> */}
          {/* {customContent ? customContent({ form, ...props }) : null} */}
          {customContent && customContent({ form, ...props })}
        </Drawer>
      )}
    </>
  );
};

export default ModalContainer;
