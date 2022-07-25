import React, { useState, useEffect, useRef, useCallback } from 'react';
import { IconFont, Button, Input, Select, Form, Row, Col, useDynamicList, Modal, AutoComplete } from '@didi/dcloud-design';
import { setlogFilePathKey } from './dateRegAndGvar';
import { getCollectTaskFiles, getRuleTips, getFileContent, getSliceRule } from '../../api/collect';
import { logFilePathKey } from './dateRegAndGvar';
import _, { cloneDeep } from 'lodash';
import './index.less';

const LoopAddLogFileType = (props: any) => {
  const { list, remove, getKey, push, replace, resetList, sortForm } = useDynamicList<any>(['']);
  const { setFieldsValue, getFieldValue, getFieldsValue } = props.form;
  const logPathList = useRef<any>([]);
  const copylogPathList = useRef<any>([]);
  const allvalidate = useRef<any>(false);
  const [regTips, setRegTips] = useState([]);
  const [hostNameList, setHostNameList] = useState<any>(props.hostList || []);
  const [fileArrList, setFileArrList] = useState([]);
  const [isModalVisible, setVisible] = useState(false);
  const [previewPath, setPreviewPath] = useState([]);
  const [hookPathList, setHookPathList] = useState(['']);
  const [logFileListClass, setLogFileListClass] = useState('');

  const options =
    hostNameList.length > 0 &&
    hostNameList.map((group: any, index: number) => {
      return (
        <Select.Option key={group.id} value={group.hostName}>
          {group.hostName}
        </Select.Option>
      );
    });
  const handlelogSuffixfiles = async (key: number, val) => {
    const logFilePath = await getFieldValue(`step2_file_path_${key}`);
    logPathList.current[key] = logFilePath;
    setlogFilePathKey(key); // 同步日志路径的key值，防止减少日志路径key值乱
  };
  const [debouncedCallApi] = useState(() => _.debounce(handlelogSuffixfiles, 100)); // 做事件防抖时，为了防止每次触发都会重新渲染，维护一个state函数，让每次执行的时候都是同一个函数
  const addPush = () => {
    push('');
    allvalidate.current = false;
  };

  const reset = (index: any) => {
    const copylist = cloneDeep(logPathList.current);
    const delValue = copylist.splice(index, 1);
    logPathList.current = copylist;
    copylogPathList.current = copylist;
    setPreviewPath(logPathList.current);
    props.setFilePathList(logPathList.current);
    const newList = hookPathList.map((path, i) => {
      return index == i ? '' : path;
    });
    remove(index);
    setHookPathList(newList);
    props.setHookPathList(newList);
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
    const logFilePath = getFieldValue(`step2_filePath_0_example`);
    const hostName = getFieldValue(`step2_hostName_example`);
    const params = {
      path: logFilePath,
      suffixRegular: Array.isArray(logSuffixfilesValue) ? logSuffixfilesValue.join(',') : logSuffixfilesValue,
      hostName,
    };
    if (logFilePath && hostName) {
      getCollectTaskFiles(params)
        .then((res) => {
          setLogFileListClass('');
          setFileArrList(res);
        })
        .catch((res) => {
          setLogFileListClass('logFileList_error');
          setFileArrList([]);
        });
    }
  }, 0);

  const handleLogPath = useDebounce(() => {
    const serviceId = getFieldValue(`step1_serviceId`);
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`);
    // 弹窗问题
    setFieldsValue({
      step2_file_suffixMatchRegular_example: getFieldValue('step2_file_suffixMatchRegular'),
      step2_filePath_0_example: getFieldValue('step2_file_path_0'),
      step2_hostName_example: getFieldValue('step2_hostName'),
    });
    setPreviewPath(logPathList.current);
    setVisible(true);
    if (serviceId && logFilePath) {
      props.setisNotLogPath(true);
    }
  }, 100);

  const onSearch = (searchText: string) => {
    if (!searchText) {
      setPreviewPath(logPathList.current);
    } else {
      const filterPath = logPathList.current.filter((item: any) => item.indexOf(searchText) > -1);
      setPreviewPath(filterPath);
    }
  };

  const row = (index: any, item: any) => (
    <div key={getKey(index)}>
      {/* <Collapse activeKey={['1']}>
        <Panel header='' key="1" showArrow={false}> */}
      <Form.Item
        key={getKey(index)}
        label={`${index == 0 ? '日志文件路径' : ''}`}
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
                      logPathList.current[index] = value;
                      if (JSON.stringify(logPathList.current) === JSON.stringify(copylogPathList.current)) {
                        if (
                          value &&
                          logPathList.current.findIndex((p, i) => {
                            return i < index && p == value;
                          }) < 0
                        ) {
                          cb();
                        }
                      }
                      if (!value) {
                        rule.message = '请输入日志文件路径';
                        copylogPathList.current[index] = value;
                        cb(`请输入日志文件路径${getKey(index)}`);
                      } else if (copylogPathList.current.includes(value)) {
                        rule.message = '日志文件路径不能重复';
                        copylogPathList.current[index] = value;
                        cb(`请输入日志文件路径重复`);
                      } else {
                        copylogPathList.current[index] = value;
                        props.setFilePathList(logPathList.current.filter((item: any) => item));
                        cb();
                      }
                      // console.log(logPathList.current, copylogPathList.current, index, value);
                    }, 0);
                  },
                },
              ]}
            >
              <Input
                key={getKey(index)}
                // onChange={(val) => debouncedCallApi(getKey(index), val)}
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
    </div>
  );

  useEffect(() => {
    resetList(props.originFilePathList);
    if (props.editUrl) {
      setHookPathList([...props.originFilePathList]);
      props.setHookPathList([...props.originFilePathList]);
      logPathList.current = [...props.originFilePathList];
      copylogPathList.current = [...props.originFilePathList];
    }
  }, [props.originFilePathList, props.editUrl]);

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
      {list.map((ele, index) => {
        return row(index, ele);
      })}
      <Form.Item label="采集文件后缀名匹配正则" extra="注:如需验证或遇到操作困难,可点击预览,展示日志路径下文件列表">
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
      <Modal
        title="路径预览"
        visible={isModalVisible}
        onOk={() => setVisible(false)}
        onCancel={() => setVisible(false)}
        className={'collect-task'}
      >
        <Row>
          <Col span={22}>
            <Form.Item label="日志文件路径" labelCol={{ span: 5 }} name={`step2_filePath_0_example`}>
              <AutoComplete onSearch={onSearch}>
                {previewPath
                  .filter((item) => item)
                  .map((path: string) => (
                    <AutoComplete.Option key={path} value={path}>
                      {path}
                    </AutoComplete.Option>
                  ))}
              </AutoComplete>
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
                <Form.Item labelCol={{ span: 8 }} label="采集文件后缀名匹配正则" name="step2_file_suffixMatchRegular_example">
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
                <Button type="link" style={{ marginTop: '10px' }} onClick={handlelogFileExample}>
                  预览
                </Button>
              </Col>
            </Row>
            <Row>
              <Col span={23}>
                <Form.Item label="路径预览结果" labelCol={{ span: 5 }}>
                  <ul className={`logfile_list logFileList ${logFileListClass}`}>
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
