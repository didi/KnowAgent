import React, { useState, useEffect, useRef, useCallback } from 'react';
import { IconFont, Button, Input, Select, Form, Row, Col, useDynamicList, Modal } from '@didi/dcloud-design';
import { setlogFilePathKey } from './dateRegAndGvar';
import { getCollectTaskFiles, getRuleTips, getFileContent, getSliceRule } from '../../api/collect';
import { logFilePathKey } from './dateRegAndGvar';
import _, { cloneDeep } from 'lodash';
import './index.less';


const LoopAddLogFileType = (props: any) => {
  const { list, remove, getKey, push, resetList, sortForm } = useDynamicList<any>(['']);
  const { setFieldsValue, getFieldValue } = props.form;
  const logPathList = useRef<any>([]);
  const copylogPathList = useRef<any>([]);
  const allvalidate = useRef<any>(false);
  const [regTips, setRegTips] = useState([]);
  const [hostNameList, setHostNameList] = useState<any>(props.hostList || []);
  const [fileArrList, setFileArrList] = useState([]);
  const [isModalVisible, setVisible] = useState(false);

  const options =
    hostNameList.length > 0 &&
    hostNameList.map((group: any, index: number) => {
      return (
        <Select.Option key={group.id} value={group.hostName}>
          {group.hostName}
        </Select.Option>
      );
    });
  const handlelogSuffixfiles = async (key: number) => {
    const logFilePath = await getFieldValue(`step2_file_path_${key}`);
    logPathList.current[key] = logFilePath;
    sortForm(logPathList.current);
    setlogFilePathKey(key); // 同步日志路径的key值，防止减少日志路径key值乱
    props.setisNotLogPath(false);
  };
  const [debouncedCallApi] = useState(() => _.debounce(handlelogSuffixfiles, 0)); // 做事件防抖时，为了防止每次触发都会重新渲染，维护一个state函数，让每次执行的时候都是同一个函数
  const addPush = () => {
    push('');
    allvalidate.current = false;
  };

  const reset = (index: any) => {
    const copylist = cloneDeep(logPathList.current);
    const delValue = copylist.splice(index, 1);
    logPathList.current = copylist;
    const copyPathlist = cloneDeep(copylogPathList.current);
    copyPathlist.splice(copylogPathList.current.indexOf(delValue), 1);
    copylogPathList.current = copyPathlist;
    remove(index);
    props.setFilePathList(logPathList.current.filter((item: any) => item));
  };

  const onSelectChange = (value) => {
    const res = value?.[value.length - 1];
    setFieldsValue({ step2_file_suffixMatchRegular: [res] });
  };

  const useDebounce = (fn: any, delay: number, dep = []) => {
    const { current } = useRef<any>({ fn, timer: null });
    useEffect(
      function () {
        current.fn = fn;
      },
      [fn]
    );

    return useCallback(function f(...args) {
      if (current.timer) {
        clearTimeout(current.timer);
      }
      current.timer = setTimeout(() => {
        current.fn(...args);
      }, delay);
    }, dep);
  };

  const handlelogFileExample = useDebounce(() => {
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular_example`);
    const logFilePath = getFieldValue(`step2_file_path_0_example`);
    const hostName = getFieldValue(`step2_hostName_example`);
    const params = {
      path: logFilePath,
      suffixRegular: logSuffixfilesValue?.[0],
      hostName,
    };
    if (logFilePath && hostName) {
      getCollectTaskFiles(params).then((res) => {
        setFileArrList(res);
      });
    }
  }, 0);

  const handleLogPath = useDebounce(() => {
    const serviceId = getFieldValue(`step1_serviceId`);
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`);
    // 弹窗问题
    setFieldsValue({
      step2_file_suffixMatchRegular_example: getFieldValue('step2_file_suffixMatchRegular'),
      step2_file_path_0_example: getFieldValue('step2_file_path_0'),
      step2_hostName_example: getFieldValue('step2_hostName'),
    });
    setVisible(true);
    if (serviceId && logFilePath) {
      props.setisNotLogPath(true);
    }
  }, 100);

  const row = (index: any, item: any) => (
    <div key={getKey(index)}>
      {/* <Collapse activeKey={['1']}>
        <Panel header='' key="1" showArrow={false}> */}
      <Form.Item
        key={getKey(index)}
        label={`${index == 0 ? '日志路径' : ''}`}
        extra={`${list.length - 1 === index ? '可增加，最多10个, 默认与上一个选择配置项内容保持一致。' : ''}`}
      >
        <Row>
          <Col span={21}>
            <Form.Item
              name={`step2_file_path_${getKey(index)}`}
              initialValue={item}
              rules={[
                {
                  required: true,
                  validator: (rule: any, value: string, cb: any) => {
                    setTimeout(() => {
                      const listFiterLength = logPathList.current.filter((item) => item == value).length;
                      if (!value) {
                        rule.message = '请输入日志路径';
                        cb(`请输入日志路径${getKey(index)}`);
                      } else if (listFiterLength > 1 && (allvalidate.current ? copylogPathList.current.includes(value) : true)) {
                        rule.message = '日志路径不能重复';
                        cb(`请输入日志路径重复`);
                      } else {
                        props.setFilePathList(logPathList.current.filter((item: any) => item));
                        cb();
                      }
                      copylogPathList.current.push(value);
                    }, 100);
                  },
                },
              ]}
            >
              <Input
                key={getKey(index)}
                onInput={() => debouncedCallApi(getKey(index))}
                className={`step2_file_path_input${getKey(index)}`}
                placeholder="如：/home/xiaoju/changjiang/logs/app.log"
                name={`step2_file_path_${getKey(index)}`}
              />
            </Form.Item>
          </Col>
          <Col span={3}>
            {list.length > 1 && (
              <Button
                style={{ marginLeft: 8 }}
                onClick={() => {
                  reset(index);
                }}
                icon={<IconFont type="icon-jian" className="ml-10" />}
              />
            )}
            {list.length < 10 && (
              <Button style={{ marginLeft: 8 }} onClick={() => addPush()} icon={<IconFont type="icon-jiahao" className="ml-10" />} />
            )}
          </Col>
        </Row>
      </Form.Item>
    </div >
  );

  useEffect(() => {
    resetList(props.filePathList);
    if (props.editUrl) {
      logPathList.current = [...props.filePathList];
    }
  }, [props.filePathList, props.editUrl]);

  useEffect(() => {
    getRuleTips().then((res) => {
      setRegTips(
        Object.keys(res).map((key: any) => {
          return { value: res[key], desc: `${res[key]}（${key}）` };
        })
      );
    });
  }, []);

  useEffect(() => {
    setHostNameList(props.hostList || []);
  }, [props.hostList]);

  useEffect(() => {
    setFileArrList(props.logListFile);
  }, [props.logListFile]);

  return (
    <div className="set-up loopaddlog-filetype">
      {list &&
        list.map((ele, index) => {
          return row(index, ele);
        })}
      <Form.Item label="采集文件后缀匹配样式" extra="注:如需验证或遇到操作困难,可点击预览,展示日志路径下文件列表">
        <Row>
          <Col span={21}>
            <Form.Item name="step2_file_suffixMatchRegular" initialValue={[]}>
              <Select mode="tags" onChange={onSelectChange} placeholder="请输入后缀的正则匹配，不包括分隔符。如：^([\d]{0,6})$">
                {regTips.map((item) => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.desc}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col span={3}>
            <Button onClick={handleLogPath} type="primary" style={{ marginLeft: '10px' }}>
              路径预览
            </Button>
          </Col>
        </Row>
      </Form.Item>
      <Modal title="路径预览" visible={isModalVisible} onOk={() => setVisible(false)} onCancel={() => setVisible(false)}>
        <Row>
          <Col span={22}>
            <Form.Item label="日志路径" name={`step2_file_path_0_example`}>
              <Select>
                {logPathList.current.map((item) => (
                  <Select.Option key={item} value={item}>
                    {item}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
        </Row>
        {hostNameList.length > 0 && props.isNotLogPath ? (
          <>
            <Row>
              <Col span={22}>
                <Form.Item
                  label="映射主机"
                  initialValue={props.hostList[0]?.hostName}
                  name="step2_hostName_example"
                  rules={[{ message: '请选择映射主机名称' }]}
                >
                  <Select showSearch style={{ width: 200 }} placeholder="请选择主机" optionFilterProp="children">
                    {options}
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            <Row align="middle">
              <Col span={22}>
                <Form.Item labelCol={{ span: 8 }} label="采集文件后缀匹配样式" name="step2_file_suffixMatchRegular_example">
                  <Select mode="tags" placeholder="请输入后缀的正则匹配，不包括分隔符。如：^([\d]{0,6})$">
                    {regTips.map((item) => (
                      <Select.Option key={item.value} value={item.value}>
                        {item.desc}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col span={2}>
                <Button type="link" style={{ marginLeft: '-40px', marginTop: '10px' }} onClick={handlelogFileExample}>
                  预览
                </Button>
              </Col>
            </Row>
            <Row>
              <Col span={23}>
                <Form.Item label="路径预览结果">
                  <ul className={`logfile_list logFileList`}>
                    {fileArrList && fileArrList?.map((logfile: string, key: number) => <li key={key}>{logfile}</li>)}
                  </ul>
                </Form.Item>
              </Col>
            </Row>
          </>
        ) : null}
      </Modal>
    </div>
  );
};

export default LoopAddLogFileType;
