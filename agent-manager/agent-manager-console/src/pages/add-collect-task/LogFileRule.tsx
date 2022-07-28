import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Row, Form, Input, Radio, InputNumber, Button, AutoComplete, Select, Col, Tooltip, Modal } from '@didi/dcloud-design';
import { IconFont } from '@didi/dcloud-design';
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

const exampleValue = `[2022-06-28 22:01:26,464] INFO [ProducerStateManager partition=data-0]Writing producer snapshot at offset 21957007 (kafka.log.ProducerStateManager)\n[2022-06-28 22:01:26,467] INFO [Log Partition partition=data-0, dir=/data0/kafka-logs] Rolled new log segment at offset 21957007 in 6ms.(kafka.Log)\n`;

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
  const [exampaleVisbile, setExampleVisible] = useState(false);

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
    const logFilePath = getFieldValue(`step3_logPath_test`); //getFieldValue(`step2_file_path_${logFilePathKey}`);
    const hostName = getFieldValue(`step2_hostName`); //getFieldValue(`step2_hostName`);
    const params = {
      path: encodeURIComponent(logFilePath),
      hostName,
    };
    const res = await getFileContent(params);
    setContent(res);
    setFieldsValue({ 'step3_${props.logType}_selectionType': res });
    setShowFileLoad(false);
  };
  const getSelectionType = async (e) => {
    const text = document.querySelector('.pre-content-text');
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const start = text.selectionStart;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const end = text.selectionEnd;
    const selObj = window?.getSelection()?.toString() || document.getSelection()?.toString();
    if (!selObj) return;
    let startIndex = content.lastIndexOf('\n', start);
    if (startIndex === -1) {
      startIndex = 0;
    }
    let endIndex = content.indexOf('\n', end);
    if (endIndex === -1) {
      endIndex = content.length - 1;
    }
    const sliceLine = content.slice(startIndex, endIndex);
    const params = {
      content: sliceLine,
      sliceDateTimeStringStartIndex: sliceLine.indexOf(selObj) > -1 ? sliceLine.indexOf(selObj) : 0,
      sliceDateTimeStringEndIndex: sliceLine.indexOf(selObj) + selObj.length - 1,
    };
    const res = await getSliceRule(params);
    if (res) {
      setFieldsValue({
        [`step3_${props.logType}_sliceTimestampPrefixString`]: res.sliceTimestampPrefixString || '',
        [`step3_${props.logType}_sliceTimestampFormat`]: res.sliceTimestampFormat,
        [`step3_${props.logType}_sliceTimestampPrefixStringIndex`]: res.sliceTimestampPrefixStringIndex,
      });
    }
  };
  const handleContentChange = (e) => {
    setContent(e.currentTarget.value);
    // onHandleContentPre();
  };

  // 日志预览按钮
  const slicePreview = () => {
    const userCopyContent = getFieldValue(`step3_${props.logType}_selectionType`);
    const slicePrefixString = getFieldValue(`step3_${props.logType}_sliceTimestampPrefixString`);
    const sliceFormat = getFieldValue(`step3_${props.logType}_sliceTimestampFormat`);
    let slicePrefixStringIndex = getFieldValue(`step3_${props.logType}_sliceTimestampPrefixStringIndex`);
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
        [`step3_${props.logType}_sliceTimestampPrefixString`]: props.sliceRule.sliceTimestampPrefixString || '',
        [`step3_${props.logType}_sliceTimestampFormat`]: props.sliceRule.sliceTimestampFormat,
        [`step3_${props.logType}_sliceTimestampPrefixStringIndex`]: props.sliceRule.sliceTimestampPrefixStringIndex,
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
        <Form.Item label="切片配置划取方法说明：" style={{ marginBottom: '-20px' }}>
          <span style={{ position: 'absolute', top: '-28px', left: '150px', width: '560px' }}>
            请用光标在原始日志文本框中划取用于区分两条不同日志的日期/时间字符串，系统将根据您划取的日期/时间模式串自动识别，并在下方自动配置好日志切片规则。
          </span>
        </Form.Item>
        <Form.Item label="原始日志：">
          <Row>
            <Col span={22}>
              <TextArea
                value={content}
                onChange={handleContentChange}
                className="pre-content-text"
                onBlur={(e) => {
                  setStart(e.target.selectionStart);
                }}
                onSelect={getSelectionType}
              />
            </Col>
            <Button onClick={handleContentPre} style={{ marginLeft: '10px' }}>
              加载远程日志
            </Button>
          </Row>
        </Form.Item>
        <Form.Item
          className="col-time-stramp"
          extra="注：填写时间戳，或复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始"
          labelCol={{ span: 12 }}
          label={
            <div>
              日志切片规则
              <Tooltip title="tips：在日志预览框划取日期/时间字符串，可自动获取日志切片规则参数" placement="right">
                <IconFont type="icon-tishi"></IconFont>
              </Tooltip>
              ：
              <Button type="link" size="small" onClick={slicePreview}>
                日志切片结果预览
              </Button>
            </div>
          }
        >
          <Row>
            <Col span={6}>
              <Row style={{ display: 'flex', alignItems: 'baseline' }}>
                <Col span={8}>
                  <span>左起第</span>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name={`step3_${props.logType}_sliceTimestampPrefixStringIndex`}
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
                    <InputNumber style={{ width: '90%' }} min={0} max={99999} precision={0} placeholder="从0开始计数" />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <span>个匹配上</span>
                </Col>
              </Row>
            </Col>
            <Col span={7} style={{ margin: '0 10px' }}>
              <Form.Item
                name={`step3_${props.logType}_sliceTimestampPrefixString`}
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
                <Input onChange={() => setStart(-1)} placeholder="请输入标记切片的日期/时间字符串前缀字符" />
              </Form.Item>
            </Col>
            <Col span={8} style={{ margin: '0 20px 0 10px' }}>
              <Form.Item
                name={`step3_${props.logType}_sliceTimestampFormat`}
                rules={[{ required: true, message: '请选择或者输入时间格式' }]}
              >
                <AutoComplete
                  className="step2_file_sliceTimestampFormat"
                  onChange={() => setStart(-1)}
                  placeholder="请输入标记切片的日期/时间模式串"
                />
              </Form.Item>
            </Col>
            <Button type="link" onClick={() => setExampleVisible(true)} style={{ marginLeft: '-20px' }}>
              切片规则配置样例
            </Button>
          </Row>
        </Form.Item>
        <Modal
          width={720}
          title="切片规则配置样例"
          visible={exampaleVisbile}
          onOk={() => setExampleVisible(false)}
          onCancel={() => setExampleVisible(false)}
        >
          <Form.Item label="原始日志内容" labelCol={{ span: 3 }} wrapperCol={{ span: 24 }}>
            <TextArea readOnly value={exampleValue} style={{ height: '150px' }}></TextArea>
          </Form.Item>
          <Form.Item label="日志切片规则" labelCol={{ span: 3 }} wrapperCol={{ span: 24 }}>
            <Row>
              <Col span={8}>
                <Row style={{ display: 'flex', alignItems: 'baseline' }}>
                  <Col span={6}>
                    <span>左起第</span>
                  </Col>
                  <Col span={10}>
                    <Form.Item>
                      <InputNumber value={0} style={{ margin: '0 5px', width: '70px' }} readOnly />
                    </Form.Item>
                  </Col>
                  <Col span={8}>
                    <span>个匹配上</span>
                  </Col>
                </Row>
              </Col>
              <Col span={5} style={{ padding: '0 10px' }}>
                <Form.Item>
                  <Input value={'['} readOnly placeholder="请输入切片时间戳前缀字符串" />
                </Form.Item>
              </Col>
              <Col span={11}>
                <Form.Item>
                  <Input value="yyyy-MM-dd HH:mm:ss,SSS" readOnly />
                </Form.Item>
              </Col>
            </Row>
          </Form.Item>
        </Modal>
        <Row className="slice-pre">
          <Col span={20} style={{ display: 'inline-block', marginTop: '10px' }}>
            {isShow && (
              <>
                <div>
                  日志切片结果：
                  <Tooltip
                    title="切片结果中黑色字体表示切片正常的日志数据行，红色字体表示因无法匹配日志切片规则导致切片错误的日志数据行"
                    placement="right"
                  >
                    <IconFont type="icon-tishi"></IconFont>
                  </Tooltip>
                </div>
                <div className="slicePreview" style={{ height: '200px', background: '#fff', border: '1px solid #CCC' }}>
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
              </>
            )}
          </Col>
        </Row>
        <Modal title="远程主机日志加载" visible={showFileLoad} onOk={onHandleContentPre} onCancel={() => setShowFileLoad(false)}>
          <Form form={props.form}>
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
            <Form.Item label="日志路径" name="step3_logPath_test" initialValue={props.filePathList[0]}>
              <Select defaultValue={props.filePathList[0]}>
                {props.filePathList.map((item) => (
                  <Select.Option key={item} value={item}>
                    {item}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default LogFileType;
