import React, { useState, useEffect } from 'react';
import { Select, Form, Input, Switch, Radio, DatePicker, Row, Col, Collapse } from '@didi/dcloud-design';
import { getServices, getHostListbyServiceId } from '../../api/agent';
// import { IService, IHosts } from '../../interface/agent';
import { contentFormItemLayout } from './config';
import { regName, regText } from '../../constants/reg';
import { UpOutlined, DownOutlined } from '@ant-design/icons';
import moment from 'moment';
import { setHostNameList } from './dateRegAndGvar';
import './index.less';

const { RangePicker } = DatePicker;
const { TextArea } = Input;
const { Panel } = Collapse;

// const mapDispatchToProps = (dispatch: Dispatch) => ({
//   setCollectType: (collectType: number) => dispatch(actions.setCollectType(collectType)),
// });

// type Props = ReturnType<typeof mapDispatchToProps>;
const CollectObjectConfiguration = (props: any) => {
  // const { getFieldDecorator, getFieldValue, resetFields } = props.form;
  const editUrl = window.location.pathname.includes('/edit-task');
  const [collectMode, setCollectMode] = useState(0);
  const [openHistory, setOpenHistory] = useState(false);
  const [historyFilter, setHistoryFilter] = useState('current');
  const [hostRange, setHostRange] = useState(0);
  const [hostWhite, setHostWhite] = useState('hostname');
  const [serviceList, setServiceList] = useState([]);
  const [suHostList, setSuHostList] = useState<any>([]);
  const [activeKeys, setActiveKeys] = useState([] as string[]);
  const onModeChange = (e: any) => {
    setCollectMode(e.target.value);
    props.setCollectType(e.target.value);
  };

  const customPanelStyle = {
    border: 0,
    overflow: 'hidden',
    padding: '10px 0 0',
    background: '#fafafa',
  };

  const openHistoryChange = (checked: any) => {
    setOpenHistory(checked);
  };

  const onHistoryFilterChange = (e: any) => {
    setHistoryFilter(e.target.value);
  };

  const onHostRangeChange = (e: any) => {
    setHostRange(e.target.value);
  };

  const onHostWhiteListChange = (e: any) => {
    setHostWhite(e.target.value);
  };

  const getServicesData = () => {
    getServices()
      .then((res: any) => {
        if (Array.isArray(res) && !isNaN(res?.[0]?.id)) {
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
        setSuHostList(res.hostList);
        props.hostNameList(res.hostList);
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const onAppSelectChange = (value: number) => {
    // resetFields(['step1_hostNames'])
    setSuHostList([]);
    setHostNameList([]);
    getSuHostlist(value);
    props.setisNotLogPath(false);
  };
  useEffect(() => {
    getServicesData();
    if (editUrl) {
      setCollectMode(props.collectMode);
      setOpenHistory(props.openHistory);
      setHistoryFilter(props.historyFilter);
      setHostRange(props.hostRange);
      setHostWhite(props.hostWhite);
      setSuHostList([...props.hostNames]);
    }
  }, [props.collectMode, props.openHistory, props.openHistory, props.historyFilter, props.hostRange, props.hostWhite, props.hostNames]);

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  };

  return (
    <div className="set-up collection-object">
      <Form form={props?.form} {...contentFormItemLayout}>
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
        {/* <Form.Item label="所属项目">
          {getFieldDecorator('step1_project', {
            initialValue: '',
          })(<span />)}
        </Form.Item> */}
        {/*<Collapse
          bordered={false}
          expandIconPosition="right"
          onChange={collapseCallBack}
          activeKey={activeKeys?.length ? ['high'] : []}
          destroyInactivePanel
          style={{ background: 'none', padding: '0 50px' }}
        >
          <Panel
            header={<h2 style={{ display: 'inline-block', color: '#a2a2a5', marginLeft: '15px' }}>高级配置</h2>}
            extra={<a>{activeKeys?.length ? <>收起&nbsp;<UpOutlined /></> : <>展开&nbsp;<DownOutlined /></>}</a>}
            showArrow={false}
            key="high"
            style={customPanelStyle}
          >
            <Row className="form-row">
              <Form.Item label="采集模式">
                <Col span={4}>
                  {getFieldDecorator('step1_logCollectTaskType', {
                    initialValue: 0,
                    rules: [{ required: true, message: '请选择采集模式' }],
                  })(
                    <Radio.Group onChange={onModeChange}>
                      <Radio value={0}>流式</Radio>
                      <Radio value={1}>时间段</Radio>
                    </Radio.Group>,
                  )}
                </Col>
                <Col span={8}>
                  {collectMode === 1 &&
                    <Form.Item>
                      {getFieldDecorator('step1_collectBusinessTime', { // collectStartBusinessTime collectEndBusinessTime
                        initialValue: [],
                        rules: [{ required: true, message: '请选择时间段' }],
                      })(<RangePicker showTime format="YYYY/MM/DD HH:mm:ss" />)}
                    </Form.Item>}
                </Col>
              </Form.Item>
            </Row>

            {collectMode === 0 &&
              <Row className="form-row">
                <Form.Item label="历史数据过滤">
                  <Col span={2}>
                    {getFieldDecorator('step1_openHistory', {
                      initialValue: false,
                      valuePropName: 'checked',
                      rules: [{ required: true, message: '是否开启历史数据过滤' }],
                    })(
                      <Switch onChange={openHistoryChange} />,
                    )}
                  </Col>
                  <Col span={8}>
                    {openHistory &&
                      <Form.Item>
                        {getFieldDecorator('step1_historyFilter', {
                          initialValue: 'current',
                          rules: [{ required: true, message: '请选择历史数据过滤' }],
                        })(
                          <Radio.Group onChange={onHistoryFilterChange}>
                            <Radio value={'current'}>从当前开始采集</Radio>
                            <Radio value={'custom'}>自定义采集开始时间</Radio>
                          </Radio.Group>,
                        )}
                      </Form.Item>}
                  </Col>
                  <Col span={8}>
                    {openHistory && historyFilter === 'custom' &&
                      <Form.Item>
                        {getFieldDecorator('step1_collectStartBusinessTime', {
                          initialValue: moment(),
                          rules: [{ required: true, message: '请选择时间' }],
                        })(
                          <DatePicker showTime placeholder="请选择时间" />
                        )}
                      </Form.Item>}
                  </Col>
                </Form.Item>
              </Row>}

            <Form.Item label="主机范围">
              {getFieldDecorator('step1_needHostFilterRule', {
                initialValue: 0,
                rules: [{ required: true, message: '请选择主机范围' }],
              })(
                <Radio.Group onChange={onHostRangeChange}>
                  <Radio value={0}>全部</Radio>
                  <Radio value={1}>部分</Radio>
                </Radio.Group>
              )}
            </Form.Item>

            {hostRange === 1 &&
              <Form.Item label="主机白名单">
                {getFieldDecorator('step1_hostWhiteList', {
                  initialValue: 'hostname',
                  rules: [{ required: true, message: '请选择主机白名单' }],
                })(
                  <Radio.Group onChange={onHostWhiteListChange}>
                    <Row>
                      <Col span={10} className="col-radio-host">
                        <Radio className="radio-host-name" value={'hostname'}>
                          <span className="radio-host-span">主机名：</span>
                          <Form.Item className="radio-form">
                            {getFieldDecorator('step1_hostNames', {
                              initialValue: [],
                              rules: [{ required: hostWhite === 'hostname', message: '请选择主机名' }],
                            })(
                              <Select
                                className='w-300'
                                showSearch={true}
                                optionFilterProp="children"
                                mode="multiple"
                                filterOption={(input: string, option: any) => {
                                  return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
                                }}
                              >
                                {suHostList && suHostList.map((groupOption: any, index: number) => {
                                  return (
                                    <Select.Option key={index} value={groupOption.id}>
                                      {groupOption.hostName}
                                    </Select.Option>
                                  );
                                })}
                              </Select>
                            )}
                          </Form.Item>
                        </Radio>
                      </Col>
                      <Col span={10} className="col-radio-host">
                        <Radio className="radio-host-name" value={'sql'}>
                          <span className="radio-host-span">SQL匹配：</span>
                          <Form.Item className="radio-form">
                            {getFieldDecorator('step1_filterSQL', {
                              initialValue: '',
                              rules: [{ required: hostWhite === 'sql', message: '请输入SQL匹配' }],
                            })(<Input className='w-300' placeholder='请输入' />)}
                          </Form.Item>
                        </Radio>
                      </Col>
                    </Row>
                  </Radio.Group>
                )}
              </Form.Item>}
          </Panel>
        </Collapse>*/}

        {/* <Form.Item label="采集任务描述">
          {getFieldDecorator('step1_logCollectTaskRemark', {
            initialValue: '',
            rules: [{
              required: false,
              message: '请输入对该采集任务的描述，50位限制',
              validator: (rule: any, value: string) => {
                return new RegExp(regText).test(value);
              },
            }],
          })(<TextArea placeholder="请输入对该采集任务的描述" rows={3} />)}
        </Form.Item> */}
      </Form>
    </div>
  );
};

export default CollectObjectConfiguration;
