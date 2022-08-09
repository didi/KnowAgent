import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Row, Form, Input, Radio, InputNumber, Button, AutoComplete, Select, Col, Tooltip } from '@didi/dcloud-design';
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
  // 'YYYY-MM-DD': regYymd,
  // 'YY-MM-DD': regYmd,
};

// 获取选中的内容，并将内容匹配对应规则放入时间戳表单中
let sliceTimestampPrefixString = '';
let sliceTimestampFormat = '';
let sliceTimestampPrefixStringIndex = 0;

/**
 * @method cheageSliceTypeReg 正则匹配日志时间类型
 * @param reg 需要匹配的正则
 * @param dateType 匹配的时间格式
 * @param selObj 鼠标选中的字符串
 * @param userCopyContent 文本框的内容
 */
const cheageSliceTypeReg = (reg: any, dateType: string, selObj: string, userCopyContent: string) => {
  sliceTimestampPrefixString = selObj.split(selObj?.match(reg)[0])[0].slice(-1);
  sliceTimestampFormat = dateType;
  const aNewlineIs = userCopyContent.slice(0, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0])).lastIndexOf('\n');
  if (sliceTimestampPrefixString === '') {
    if (aNewlineIs === -1) {
      sliceTimestampPrefixStringIndex =
        userCopyContent
          .slice(0, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0]))
          .split(`${sliceTimestampPrefixString}`).length - 1;
    } else {
      sliceTimestampPrefixStringIndex =
        userCopyContent
          .slice(aNewlineIs, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0]))
          .split(sliceTimestampPrefixString).length - 1;
    }
  } else {
    if (aNewlineIs === -1) {
      sliceTimestampPrefixStringIndex =
        userCopyContent
          .slice(0, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0]))
          .split(`${sliceTimestampPrefixString}`).length - 2;
    } else {
      sliceTimestampPrefixStringIndex =
        userCopyContent
          .slice(aNewlineIs, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0]))
          .split(sliceTimestampPrefixString).length - 2;
    }
  }
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
  const { setFieldsValue, getFieldValue } = props.form;

  const initial = props?.addFileLog && !!Object.keys(props?.addFileLog)?.length;
  const options =
    hostNameList.length > 0 &&
    hostNameList.map((group: any, index: number) => {
      return (
        <Option key={group.id} value={group.hostName}>
          {group.hostName}
        </Option>
      );
    });
  const onSuffixfilesChange = (e: any) => {
    setSuffixfiles(e.target.value);
  };
  const handleLogPath = useDebounce(() => {
    const serviceId = getFieldValue(`step1_serviceId`);
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`);
    // const hostName = getFieldValue(`step2_hostName`)
    if (serviceId && logFilePath) {
      props.setisNotLogPath(true);
      handlelogSuffixfiles();
      setContentVisible(false);
    }
  }, 100);
  const handleContentPre = async () => {
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`); //getFieldValue(`step2_file_path_${logFilePathKey}`);
    const hostName = getFieldValue(`step2_hostName`); //getFieldValue(`step2_hostName`);
    const params = {
      path: encodeURIComponent(logFilePath),
      hostName,
    };
    const res = await getFileContent(params);
    setContentVisible(true);
    setContent(res);
    setFieldsValue({ 'step2_${props.logType}_selectionType': res });
  };

  const handlelogSuffixfiles = useDebounce(() => {
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`); // getFieldValue(`step2_file_suffixMatchRegular`);
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`); //getFieldValue(`step2_file_path_${logFilePathKey}`);
    const hostName = getFieldValue(`step2_hostName`); //getFieldValue(`step2_hostName`);
    const params = {
      path: logFilePath,
      suffixRegular: logSuffixfilesValue?.[0],
      hostName,
    };
    if (logFilePath && hostName) {
      getCollectTaskFiles(params).then((res) => {
        // logArr[key] = res.message.split()
        setFileArrList(res);
      });
    }
  }, 0);
  const onLogFilterChange = (e: any) => {
    handlelogSuffixfiles();
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

  const onSelectChange = (value) => {
    const res = value?.[value.length - 1];
    setFieldsValue({ step2_file_suffixMatchRegular: [res] });
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
      {/* 文件型 */}
      {/* <Form.Item label='后缀匹配符'>
        {getFieldDecorator(`step2_file_suffixSeparationCharacter_${props.getKey}`, {
          initialValue: initial ? props?.addFileLog[`step2_file_suffixSeparationCharacter_${props.getKey}`] : '',
          rules: [{
            required: true,
            message: '请输入后缀匹配符，如._/，仅支持填写一种',
            validator: (rule: any, value: string) => {
              return !!value && new RegExp(regChar).test(value);
            },
          }],
        })(
          <Input placeholder='请输入后缀分隔符，如._/，仅支持填写一种' />,
        )}
      </Form.Item> */}

      {/* <Form.Item label='采集文件后缀匹配'>
        {getFieldDecorator(`step2_file_suffixMatchType_${props.getKey}`, {
          initialValue: initial ? props?.addFileLog[`step2_file_suffixMatchType_${props.getKey}`] : 1,
          rules: [{ required: true, message: '请选择采集文件后缀匹配' }],
        })(
          <Radio.Group onChange={onSuffixfilesChange}>
            <Radio value={0}>固定格式匹配</Radio>
            <Radio checked value={1}>正则匹配</Radio>
          </Radio.Group>,
        )}
      </Form.Item> */}

      {/* <Form.Item label="后缀位数" className={suffixfiles === 0 ? '' : 'hide'}>
        {getFieldDecorator(`step2_file_suffixLength_${props.getKey}`, {
          initialValue: initial ? props?.addFileLog[`step2_file_suffixLength_${props.getKey}`] : '',
          rules: [{ required: suffixfiles === 0, message: '请输入后缀位数' }],
        })(<InputNumber min={0} placeholder='请输入' />)}
      </Form.Item> */}
      {/* <Form.Item label="后缀样式" className={suffixfiles === 1 ? '' : 'hide'}> */}
      <Form.Item label="采集文件后缀匹配样式" extra="注:如需验证或遇到操作困难,可点击预览,展示日志路径下文件列表">
        <Row>
          <Col span={12}>
            <Form.Item name="step2_file_suffixMatchRegular" initialValue={[]}>
              <Select mode="tags" onChange={onSelectChange} placeholder="请输入后缀的正则匹配，不包括分隔符。如：^([\d]{0,6})$">
                {regTips.map((item) => (
                  <Select.Option key={item} value={item}>
                    {item}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col span={8}>
            <Button onClick={handleLogPath} type="primary" style={{ marginLeft: '20px' }}>
              路径预览
            </Button>
          </Col>
        </Row>
      </Form.Item>
      {hostNameList.length > 0 && props.isNotLogPath ? (
        <Form.Item
          label="映射主机"
          initialValue={props.hostList[0]?.hostName}
          name="step2_hostName"
          rules={[{ message: '请选择映射主机名称' }]}
        >
          {/* {getFieldDecorator(`step2_hostName`, {
            initialValue: hostNameList[0].hostName,
            rules: [{ message: '请选择映射主机名称' }],
          })(
            <Select showSearch style={{ width: 200 }} placeholder="请选择主机" optionFilterProp="children" onChange={handlelogSuffixfiles}>
              {options}
            </Select>
            // <Radio.Group onChange={onLogFilterChange}>
            //   {
            //     hostNameList?.map((ele: any, index: number) => {
            //       return <Radio key={ele.id} value={ele.id}>{ele.hostName}</Radio>
            //     })
            //   }
            // </Radio.Group>
          )} */}
          <Select showSearch style={{ width: 200 }} placeholder="请选择主机" optionFilterProp="children" onChange={handlelogSuffixfiles}>
            {options}
          </Select>
        </Form.Item>
      ) : null}
      {hostNameList.length > 0 && props.isNotLogPath && (
        <div>
          <Form.Item label="路径预览">
            <ul className={`logfile_list logFileList`}>
              {fileArrList && fileArrList?.map((logfile: string, key: number) => <li key={key}>{logfile}</li>)}
            </ul>
          </Form.Item>
          <div className="pre-content">
            <Row>
              <Col span={4} className="right-text">
                <Button className="btn" onClick={handleContentPre} type="primary">
                  内容预览
                </Button>
              </Col>
            </Row>
            {contentVisible && (
              // <Form.Item label="内容预览" name={`step2_${props.logType}_selectionType`}>
              <div>
                <Row>
                  <Col span={4} className="right-text">
                    <span>内容预览:</span>
                  </Col>
                  <Col span={15}>
                    <TextArea
                      value={content}
                      className="pre-content-text"
                      onBlur={(e) => {
                        setStart(e.target.selectionStart);
                      }}
                      onClick={getSelectionType}
                    />
                  </Col>
                </Row>
              </div>
            )}

            {/* <TextArea className="pre-content-text" value={content}></TextArea> */}
            {/* </Form.Item> */}
          </div>
        </div>
      )}
      <div className="log-repeat-form">
        <Form.Item
          className="col-time-stramp"
          extra="注：填写时间戳，或复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始"
          label={
            <div>
              日志切片规则
              <Tooltip title="tips：在日志预览框划取日期/时间字符串，可自动获取日志切片规则参数" placement="topRight">
                <IconFont type="icon-tishi"></IconFont>
              </Tooltip>
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
