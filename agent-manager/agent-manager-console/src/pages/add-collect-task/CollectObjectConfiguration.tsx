import React, { useState, useEffect } from 'react';
import { Select, Form, Input, DatePicker, Collapse } from '@didi/dcloud-design';
import { getServices, getHostListbyServiceId } from '../../api/agent';
import { contentFormItemLayout } from './config';
import { regName } from '../../constants/reg';
import { setHostNameList } from './dateRegAndGvar';
import './index.less';

const CollectObjectConfiguration = (props: any) => {
  const [serviceList, setServiceList] = useState([]);
  const getServicesData = () => {
    getServices()
      .then((res: any) => {
        if (props.form.getFieldValue('step1_serviceId')) {
          getSuHostlist(props.form.getFieldValue('step1_serviceId'));
        } else if (Array.isArray(res) && !isNaN(res?.[0]?.id)) {
          getSuHostlist(res?.[0]?.id);
        }
        setServiceList(res);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const getSuHostlist = (appId: number) => {
    getHostListbyServiceId(appId)
      .then((res: any) => {
        props.hostNameList(res.hostList);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const onAppSelectChange = (value: number) => {
    setHostNameList([]);
    getSuHostlist(value);
    props.setisNotLogPath(false);
  };
  useEffect(() => {
    getServicesData();
  }, [
    props.collectMode,
    props.openHistory,
    props.openHistory,
    props.historyFilter,
    props.hostRange,
    props.hostWhite,
    props.hostNames,
    props.serviceId,
  ]);

  return (
    <div className="set-up collection-object">
      <Form form={props?.form} {...contentFormItemLayout} layout={'vertical'}>
        <Form.Item
          label="日志采集任务名"
          rules={[
            {
              required: true,
              message: '请输入日志采集任务名，支持大小写中英文字母、数字、下划线、点、短横线,32位限制',
            },
            () => ({
              validator(_, value) {
                if (!!value && !new RegExp(regName).test(value)) {
                  return Promise.reject(new Error('请输入日志采集任务名，支持大小写中英文字母、数字、下划线、点、短横线,32位限制'));
                }
                return Promise.resolve();
              },
            }),
          ]}
          name="step1_logCollectTaskName"
          initialValue=""
        >
          <Input placeholder="请输入" />
        </Form.Item>
        <Form.Item initialValue="" name="step1_serviceId" rules={[{ required: true, message: '请选择采集应用' }]} label="采集应用">
          <Select
            className="select"
            showSearch={true}
            onChange={onAppSelectChange}
            filterOption={(input: string, option: any) => {
              if (typeof option.props.children === 'object') {
                const { props } = option.props.children as any;
                return (props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
              }
              return (option.props.children + '').toLowerCase().indexOf(input.toLowerCase()) >= 0;
            }}
            placeholder="请选择应用"
          >
            {serviceList &&
              serviceList.map((groupOption, index) => {
                return (
                  <Select.Option key={index} value={groupOption.id}>
                    {groupOption.servicename}
                  </Select.Option>
                );
              })}
          </Select>
        </Form.Item>
      </Form>
    </div>
  );
};

export default CollectObjectConfiguration;
