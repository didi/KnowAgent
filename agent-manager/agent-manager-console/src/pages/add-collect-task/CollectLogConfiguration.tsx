import React, { useState, useEffect } from 'react';
import { Select, Form, Input, Radio, Row, Col, Collapse, InputNumber } from '@didi/dcloud-design';
import { collectLogTypes, codingFormatTypes, collectLogFormItemLayout } from './config';
import LoopAddLogFileType from './LoopAddLogFileType';
import LogRepeatForm from './LogRepeatForm';
import CatalogPathList from './CatalogPathList';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
// import { flowUnitList } from '../../constants/common';
// import { ILabelValue } from '../../interface/common';
import './index.less';
import LogFileType from './LogFileType';

const { Panel } = Collapse;
// interface ICollectLogProps extends FormComponentProps {
//   collectLogType: string;
//   logFilter: number;
//   cataPathlist: string[];
//   slicingRuleLog: number;
//   filePathList: string[];
//   slicingRuleLogList: number[];
//   suffixfilesList: number[];
//   hostNames: any;
//   isNotLogPath: any;
//   setisNotLogPath: any;
// }

// const mapDispatchToProps = (dispatch: Dispatch) => ({
//   setLogType: (logType: string) => dispatch(actions.setLogType(logType)),
// });
// type Props = ReturnType<typeof mapDispatchToProps>;
const CollectLogConfiguration = (props: any) => {
  // const { getFieldDecorator, getFieldValue } = props.form;
  const editUrl = window.location.pathname.includes('/edit-task');
  const [collectLogType, setCollectLogType] = useState('file');
  const [logFilter, setLogFilter] = useState(0);
  const [activeKeys, setActiveKeys] = useState([] as string[]);
  // const initial = props?.addFileLog && !!Object.keys(props?.addFileLog)?.length;
  const [logListFile, setLogListFile] = useState([]);
  // const [isNotLogPath, setisNotLogPath] = useState(false)

  const customPanelStyle = {
    border: 0,
    overflow: 'hidden',
    padding: '10px 0 0',
    background: '#fafafa',
  };

  const getCollectLogType = (e: string) => {
    setCollectLogType(e);
    props.setLogType(e);
  };

  const onLogFilterChange = (e: any) => {
    setLogFilter(e.target.value);
  };
  useEffect(() => {
    if (editUrl) {
      setCollectLogType(props.collectLogType);
      setLogFilter(props.logFilter);
    }
  }, [props.collectLogType, props.logFilter]);

  const collapseCallBack = (key: any) => {
    setActiveKeys(key);
  };

  return (
    <div className="set-up collect-log-config">
      <Form form={props.form} {...collectLogFormItemLayout}>
        {/* <Form.Item label="采集日志类型">
          {getFieldDecorator('step2_collectionLogType', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: collectLogTypes[0]?.value,
            rules: [{ required: true, message: '请选择采集日志类型' }],
          })(
            <Select className="select" onChange={getCollectLogType}>
              {collectLogTypes.map(ele => {
                return (
                  <Select.Option key={ele.value} value={ele.value}>
                    {ele.label}
                  </Select.Option>
                );
              })}
            </Select>,
          )}
        </Form.Item> */}

        {/* <Form.Item label="编码格式">
          {getFieldDecorator('step2_charset', {
            validateTrigger: ['onChange', 'onBlur'],
            initialValue: codingFormatTypes[0]?.value,
            rules: [{ required: true, message: '请选择编码格式' }],
          })(
            <Select className="select">
              {codingFormatTypes.map(ele => {
                return (
                  <Select.Option key={ele.value} value={ele.value}>
                    {ele.label}
                  </Select.Option>
                );
              })}
            </Select>,
          )}
        </Form.Item> */}

        <LoopAddLogFileType
          form={props.form}
          hostNames={props.hostNames}
          suffixfilesList={props.suffixfilesList}
          filePathList={props.filePathList}
          slicingRuleLogList={props.slicingRuleLogList}
          setLogListFile={setLogListFile}
          logListFile={logListFile}
          isNotLogPath={props.isNotLogPath}
          setisNotLogPath={props.setisNotLogPath}
        />
        <LogFileType
          form={props.form}
          setLogListFile={setLogListFile}
          logListFile={logListFile}
          isNotLogPath={props.isNotLogPath}
          setisNotLogPath={props.setisNotLogPath}
          hostList={props.hostList}
          slicingRuleLogList={props.slicingRuleLogList}
          sliceRule={props.sliceRule}
          edit={props.editUrl}
          logType="file"
        />
        {/* <LogRepeatForm logType="file" getKey={0} form={props.form} slicingRuleLog={props.slicingRuleLog} dataFormat={props.dataFormat} /> */}
        {/* <Collapse
          bordered={false}
          expandIconPosition="right"
          onChange={collapseCallBack}
          activeKey={activeKeys?.length ? ['high'] : []}
          destroyInactivePanel
          style={{ background: 'none', padding: '10px 0 0' }}
        >
          <Panel
            header={<h2 style={{ display: 'inline-block', color: '#a2a2a5', marginLeft: '15px' }}>高级配置</h2>}
            extra={<a>{activeKeys?.length ? <>收起&nbsp;<UpOutlined /></> : <>展开&nbsp;<DownOutlined /></>}</a>}
            showArrow={false}
            key="high"
            style={customPanelStyle}
          >
            <Form.Item label="日志内容过滤">
              {getFieldDecorator('step2_needLogContentFilter', {
                initialValue: 0,
                rules: [{ required: true, message: '请选择日志内容是否过滤' }],
              })(
                <Radio.Group onChange={onLogFilterChange}>
                  <Radio value={0}>否</Radio>
                  <Radio value={1}>是</Radio>
                </Radio.Group>
              )}
            </Form.Item>

            {logFilter === 1 && <>
              <Row className="form-row">
                <Form.Item label="过滤规则">
                  <Col span={6}>
                    {getFieldDecorator('step2_logContentFilterType', {
                      initialValue: 0,
                      rules: [{ required: true, message: '请选择过滤类型' }],
                    })(
                      <Radio.Group>
                        <Radio value={0}>包含</Radio>
                        <Radio value={1}>不包含</Radio>
                      </Radio.Group>
                    )}
                  </Col>
                  <Col span={8}>
                    <Form.Item>
                      {getFieldDecorator('step2_logContentFilterExpression', {
                        initialValue: '',
                        rules: [{ required: true, message: `请输入${getFieldValue('step2_logContentFilterType') === 0 ? '包含' : '不包含'}的过滤类型` }],
                      })(<Input placeholder='支持&&和||类似：str1&&str2||str3' className='w-300' />)}
                    </Form.Item>
                  </Col>
                </Form.Item>
              </Row>
            </>}
            <Form.Item label="单条日志大小上限" className='col-unit-log'>
              <Row>
                <Col span={7}>
                  {getFieldDecorator(`step2_maxBytesPerLogEvent`, {
                    initialValue: 2,
                    rules: [{ required: true, message: '请输入单条日志大小上限' }],
                  })(
                    <InputNumber className='w-200' min={1} placeholder='请输入数字' />,
                  )}
                </Col>
                <Col span={5}>
                  <Form.Item>
                    {getFieldDecorator(`step2_flowunit`, {
                      initialValue: flowUnitList[1]?.value,
                      rules: [{ required: true, message: '请选择' }],
                    })(
                      <Select className='w-100'>
                        {flowUnitList.map((v: ILabelValue, index: number) => (
                          <Select.Option key={index} value={v.value}>
                            {v.label}
                          </Select.Option>
                        ))}
                      </Select>,
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </Form.Item>
          </Panel>
        </Collapse> */}
        {/* 文件型 file */}
        {/* {collectLogType === 'file' ?
          <LoopAddLogFileType
            form={props.form}
            suffixfilesList={props.suffixfilesList}
            filePathList={props.filePathList}
            slicingRuleLogList={props.slicingRuleLogList}
          />
          : <> 
            <CatalogPathList form={props.form} cataPathlist={props.cataPathlist} collectLogType={collectLogType} />

            <Form.Item label="采集深度" extra='请输入需要遍历的深度。默认为1，表示仅遍历当前层级'>
              {getFieldDecorator('step2_catalog_directoryCollectDepth', {
                initialValue: '1',
                rules: [{ required: true, message: '请输入需要遍历的深度' }],
              })(<Input placeholder='请输入需要遍历的深度' />)}
            </Form.Item>

            <Form.Item label="采集文件白名单">
              {getFieldDecorator(`step2_catalog_collectwhitelist`, {
                initialValue: '',
                rules: [{ required: false }],
              })(<Input className='w-300' placeholder='请输入正则表达式' />)}
            </Form.Item>

            <Form.Item label="采集文件黑名单">
              {getFieldDecorator(`step2_catalog_collectblacklist`, {
                initialValue: '',
                rules: [{ required: false }],
              })(<Input className='w-300' placeholder='请输入正则表达式' />)}
            </Form.Item>

            <LogRepeatForm
              logType='catalog'
              getKey={''}
              form={props.form}
              slicingRuleLog={props.slicingRuleLog}
            />
          </>} */}
      </Form>
    </div>
  );
};

export default CollectLogConfiguration;
