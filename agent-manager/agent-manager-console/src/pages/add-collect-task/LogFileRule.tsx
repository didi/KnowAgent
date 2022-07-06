import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Row, Form, Input, Radio, InputNumber, Button, AutoComplete, Select, Col, Tooltip, Modal } from '@didi/dcloud-design';
import { IconFont } from '@didi/dcloud-design/es/pkgs/icon-project';
import { yyyyMMDDHHMMss, HHmmssSSS, yyyyMMDDHHMMssSSS, yyyyMMDDHHMMssSS, fuhao } from './dateRegAndGvar';
import LogRepeatForm from './LogRepeatForm';
// import { regChar } from '../../constants/reg';
import { logFilePathKey } from './dateRegAndGvar';
import { getCollectPathList, getCollectTaskFiles, getFileContent, getRuleTips } from '../../api/collect';
import './index.less';
import { getSliceRule } from '../../api/collect';
import { regLogSliceTimestampPrefixString } from '../../constants/reg';
import { getSlicePreview } from '../../api/collect';

// 匹配时间格式
const regYymdhmsSSS = new RegExp(yyyyMMDDHHMMssSSS);
const regYymdhmsSS = new RegExp(yyyyMMDDHHMMssSS);
const regYymdhms = new RegExp(yyyyMMDDHHMMss);
const regHmsS = new RegExp(HHmmssSSS);
// const regYymd = new RegExp(yyyyMMDD)
// const regYmd = new RegExp(yyMMDD)

const dateType: any = {
  'yyyy-MM-dd HH:mm:ss.SSS': regYymdhmsSSS,
  'yyyy-MM-dd HH:mm:ss,SSS': regYymdhmsSS,
  'yyyy-MM-dd HH:mm:ss': regYymdhms,
  'HH:mm:ss.SSS': regHmsS,
};

const { TextArea } = Input;
const { Option } = Select;
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
const LogFileType = (props: any) => {
  const editUrl = window.location.pathname.includes('/edit-task');
  const [suffixfiles, setSuffixfiles] = useState(0);
  const [isNotLogPath, setIsNotLogPath] = useState(false);
  const [fileArrList, setFileArrList] = useState([]);
  const [content, setContent] = useState('');
  const [contentVisible, setContentVisible] = useState(false);
  const [hostNameList, setHostNameList] = useState<any>(props.hostList || []);
  const [start, setStart] = useState(0);
  const [slicePre, setSlicePre] = useState<string[]>([]); // 日志切片列表
  const [isShow, setShow] = useState(false); // 是否显示日志切片框
  const [regTips, setRegTips] = useState([]);
  const [showFileLoad, setShowFileLoad] = useState(false);
  const { setFieldsValue, getFieldValue } = props.form;

  const options =
    hostNameList.length > 0 &&
    hostNameList.map((group: any, index: number) => {
      return (
        <Option key={group.id} value={group.hostName}>
          {group.hostName}
        </Option>
      );
    });
  const handleContentPre = async () => {
    setShowFileLoad(true);
  };
  const onHandleContentPre = async () => {
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`); //getFieldValue(`step2_file_path_${logFilePathKey}`);
    const hostName = getFieldValue(`step2_hostName`); //getFieldValue(`step2_hostName`);
    const params = {
      path: encodeURIComponent(logFilePath),
      hostName,
    };
    const res = await getFileContent(params);
    setContent(res);
    setFieldsValue({ 'step2_${props.logType}_selectionType': res });
    setShowFileLoad(false);
  };
  const getSelectionType = async () => {
    const text = document.querySelector('.pre-content-text');
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const start = text.selectionStart;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const end = text.selectionEnd;
    const selObj = window?.getSelection()?.toString() || document.getSelection()?.toString();
    let startIndex = content.lastIndexOf('\n', start);
    if (startIndex === -1) {
      startIndex = 0;
    }
    const endIndex = content.indexOf('\n', end);
    const sliceLine = content.slice(startIndex, endIndex);
    const params = {
      content: sliceLine,
      sliceDateTimeStringStartIndex: sliceLine.indexOf(selObj),
      sliceDateTimeStringEndIndex: sliceLine.indexOf(selObj) + selObj.length - 1,
    };
    const res = await getSliceRule(params);
    if (res) {
      setFieldsValue({
        [`step2_${props.logType}_sliceTimestampPrefixString`]: res.sliceTimestampPrefixString || '',
        [`step2_${props.logType}_sliceTimestampFormat`]: res.sliceTimestampFormat,
        [`step2_${props.logType}_sliceTimestampPrefixStringIndex`]: res.sliceTimestampPrefixStringIndex,
      });
    }
  };
  // 日志预览按钮
  const slicePreview = () => {
    const userCopyContent = getFieldValue(`step2_${props.logType}_selectionType`);
    const slicePrefixString = getFieldValue(`step2_${props.logType}_sliceTimestampPrefixString`);
    const sliceFormat = getFieldValue(`step2_${props.logType}_sliceTimestampFormat`);
    let slicePrefixStringIndex = getFieldValue(`step2_${props.logType}_sliceTimestampPrefixStringIndex`);
    // let newVal = userCopyContent.slice(start < 0 ? 0 : start)
    if (slicePrefixStringIndex < 0) {
      slicePrefixStringIndex = 0;
    }
    const params = {
      sliceTimestampPrefixStringIndex: slicePrefixStringIndex,
      sliceTimestampPrefixString: slicePrefixString,
      sliceTimestampFormat: sliceFormat,
      content: content,
    };
    getSlicePreview(params).then((res) => {
      setSlicePre(res || []);
      setShow(true);
    });
  };

  useEffect(() => {
    if (props.edit) {
      setFieldsValue({
        [`step2_${props.logType}_sliceTimestampPrefixString`]: props.sliceRule.sliceTimestampPrefixString || '',
        [`step2_${props.logType}_sliceTimestampFormat`]: props.sliceRule.sliceTimestampFormat,
        [`step2_${props.logType}_sliceTimestampPrefixStringIndex`]: props.sliceRule.sliceTimestampPrefixStringIndex,
      });
    }
  }, [props.sliceRule]);

  useEffect(() => {
    getRuleTips().then((res) => {
      setRegTips(Object.values(res));
    });
  }, []);

  useEffect(() => {
    if (editUrl) {
      setSuffixfiles(props.suffixfiles);
    }
  }, [props.suffixfiles]);

  useEffect(() => {
    setFileArrList(props.logListFile);
  }, [props.logListFile]);

  useEffect(() => {
    setIsNotLogPath(props.isNotLogPath);
  }, [props.isNotLogPath]);

  useEffect(() => {
    setHostNameList(props.hostList || []);
  }, [props.hostList]);

  return (
    <div className="set-up" key={props.getKey}>
      <div className="log-repeat-form">
        <Form.Item label="切片配置划取方法说明：">
          <span>
            请用光标在原始日志文本框中划取用于区分两条不同日志的日期/时间字符串，系统将根据您划取的日期/时间模式串自动识别，并在下方自动配置好日志切片规则。
          </span>
        </Form.Item>
        <Form.Item label="原始日志：">
          <Row>
            <Col span={22}>
              <TextArea
                value={content}
                className="pre-content-text"
                onBlur={(e) => {
                  setStart(e.target.selectionStart);
                }}
                onClick={getSelectionType}
              />
            </Col>
            <Button onClick={handleContentPre} style={{ marginLeft: '10px' }}>
              加载远程日志
            </Button>
          </Row>
        </Form.Item>
        <Modal title="远程主机日志加载" visible={showFileLoad} onOk={onHandleContentPre} onCancel={() => setShowFileLoad(false)}>
          <Form.Item
            label="关联主机"
            initialValue={props.hostList[0]?.hostName}
            name="step2_hostName"
            rules={[{ message: '请选择映射主机名称' }]}
          >
            <Select showSearch style={{ width: 200 }} placeholder="请选择主机" optionFilterProp="children">
              {options}
            </Select>
          </Form.Item>
          <Form.Item label="日志路径" name={`step2_file_path_0`}>
            <Select>
              {props.filePathList.map((item) => (
                <Select.Option key={item} value={item}>
                  {item}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Modal>
        <Form.Item
          className="col-time-stramp"
          extra="注：填写时间戳，或复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始"
          label={
            <div>
              日志切片规则
              <Tooltip title="tips：在日志预览框划取日期/时间字符串，可自动获取日志切片规则参数" placement="topRight">
                <IconFont type="icon-tishi"></IconFont>
              </Tooltip>
              ：
            </div>
          }
        >
          <Row>
            <Col span={6}>
              <Row style={{ display: 'flex', alignItems: 'baseline' }}>
                <Col span={6}>
                  <span>左起第</span>
                </Col>
                <Col span={10}>
                  <Form.Item
                    name={`step2_${props.logType}_sliceTimestampPrefixStringIndex`}
                    initialValue={0}
                    rules={[
                      {
                        required: true,
                        message: '请输入',
                        validator: (rule: any, value: any, cb) => {
                          if (value != 0 && value == '') {
                            console.log(value);
                            rule.message = '请输入';
                            cb('请输入');
                          } else if (!new RegExp(regLogSliceTimestampPrefixString).test(value)) {
                            rule.message = '最大长度限制8位';
                            cb('最大长度限制8位');
                          } else {
                            cb();
                          }
                        },
                      },
                    ]}
                  >
                    <InputNumber style={{ margin: '0 5px', width: '65px' }} min={0} max={99999999} precision={0} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <span>个匹配上</span>
                </Col>
              </Row>
            </Col>
            <Col span={6} style={{ margin: '0 10px' }}>
              <Form.Item
                name={`step2_${props.logType}_sliceTimestampPrefixString`}
                rules={[
                  {
                    // required: true,
                    //  message: '请输入',
                    validator: (rule: any, value: string, cb) => {
                      if (!value) {
                        cb();
                      } else if (!new RegExp(regLogSliceTimestampPrefixString).test(value)) {
                        rule.message = '前缀字符串最大长度为8位';
                        cb('前缀字符串最大长度为8位');
                      } else {
                        cb();
                      }
                    },
                  },
                ]}
              >
                <Input onChange={() => setStart(-1)} className="w-200" placeholder="请输入切片时间戳前缀字符串" />
              </Form.Item>
            </Col>
            <Col span={6} style={{ margin: '0 10px' }}>
              <Form.Item
                name={`step2_${props.logType}_sliceTimestampFormat`}
                initialValue={Object.keys(dateType)[0]}
                rules={[{ required: true, message: '请选择或者输入时间格式' }]}
              >
                <AutoComplete
                  dataSource={options}
                  style={{ width: 180 }}
                  className="step2_file_sliceTimestampFormat"
                  onChange={() => setStart(-1)}
                />
              </Form.Item>
            </Col>
            <Col span={3} style={{ margin: '0 10px' }}>
              <Button type="primary" onClick={slicePreview}>
                切片预览
              </Button>
            </Col>
          </Row>
        </Form.Item>
        <Row style={{ marginLeft: '16.5%' }} className="slice-pre">
          <Col span={10} style={{ display: 'inline-block' }}>
            {isShow && (
              <div className="slicePreview" style={{ height: '120px', border: '1px solid #CCC' }}>
                {slicePre &&
                  slicePre.map((item: any, key) => {
                    if (item === '') {
                      return null;
                    }
                    return (
                      <span key={key} style={{ color: item.valid === 0 ? 'red' : 'black' }}>
                        {item?.record}
                      </span>
                    );
                  })}
              </div>
            )}
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default LogFileType;
