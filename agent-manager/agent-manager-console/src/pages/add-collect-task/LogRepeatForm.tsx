import React, { useState, useEffect } from 'react';
import { Row, Col, Select, Form, InputNumber } from '@didi/dcloud-design';
import { yyyyMMDDHHMMss, HHmmssSSS, yyyyMMDDHHMMssSSS, yyyyMMDDHHMMssSS, fuhao } from './dateRegAndGvar';
import './index.less';
import { lastIndexOf } from 'lodash';
import { regLogSliceTimestampPrefixString } from '../../constants/reg';
import { IconFont, Button, Input, Radio, AutoComplete } from '@didi/dcloud-design';
const { TextArea } = Input;
const { Option } = AutoComplete;
import { getSlicePreview } from '../../api/collect';

const LogRepeatForm = (props: any) => {
  const { getFieldDecorator, setFieldsValue, getFieldValue } = props.form;
  const editUrl = window.location.pathname.includes('/edit-task');
  const [slicingRuleLog, setSlicingRuleLog] = useState(0);
  const initial = props?.addFileLog && !!Object.keys(props?.addFileLog)?.length;
  const [contents, setContents] = useState<any>(null); // 鼠标选取内容正则匹配结果
  const [isShow, setShow] = useState(false); // 是否显示日志切片框
  const [slicePre, setSlicePre] = useState<string[]>([]); // 日志切片列表
  const [start, setStart] = useState(0);
  const [options, setOptions] = useState([]);
  const onSlicingRuleLogChange = (e: any) => {
    setSlicingRuleLog(e.target.value);
  };

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

    // setContents(selObj?.match(reg)[0])
  };

  const getSelectionType = () => {
    const selObj = window?.getSelection()?.toString() || document.getSelection()?.toString();
    const userCopyContent: any = getFieldValue(`step2_${props.logType}_selectionType`);
    setShow(false);
    if (selObj === '') return;

    if (userCopyContent) {
      if (selObj?.match(regYymdhmsSSS)) {
        cheageSliceTypeReg(regYymdhmsSSS, 'yyyy-MM-dd HH:mm:ss.SSS', selObj, userCopyContent);
      } else if (selObj?.match(regYymdhmsSS)) {
        cheageSliceTypeReg(regYymdhmsSS, 'yyyy-MM-dd HH:mm:ss,SSS', selObj, userCopyContent);
      } else if (selObj?.match(regYymdhms)) {
        cheageSliceTypeReg(regYymdhms, 'yyyy-MM-dd HH:mm:ss', selObj, userCopyContent);
      } else if (selObj?.match(regHmsS)) {
        cheageSliceTypeReg(regHmsS, 'HH:mm:ss.SSS', selObj, userCopyContent);
      }
      setFieldsValue({
        [`step2_${props.logType}_sliceTimestampPrefixString`]: sliceTimestampPrefixString || '',
        [`step2_${props.logType}_sliceTimestampFormat`]: sliceTimestampFormat,
        [`step2_${props.logType}_sliceTimestampPrefixStringIndex`]: sliceTimestampPrefixStringIndex,
      });
      return;
    }
  };
  /**
   * @method getStrCount:boolean[] 获取字符串中符合条件的字符串并返回布尔值
   * @param content 需要处理的内容
   * @param sliceSymbol 截取的符号内容
   * @param symbolIndex 截取的符号的下标
   * @param sliceFormat 符合的时间格式
   * @param { {[x: string]: any;} } dateType 时间格式类型集合
   */
  const getStrCount = (
    content: any,
    sliceSymbol: string,
    symbolIndex: number,
    sliceFormat: string,
    dateType: { [x: string]: any }
  ): boolean[] => {
    const conformSlice = [];
    const userInterceptionContent = content.split('\n');
    const conformSliceBoolean = userInterceptionContent.map((item: string) => {
      return item.split(sliceSymbol).length - 2 === symbolIndex;
    });
    for (let index = 0; index < userInterceptionContent.length; index++) {
      if (conformSliceBoolean[index] || userInterceptionContent[index]?.match(dateType[sliceFormat])) {
        conformSlice.push(...userInterceptionContent[index]?.match(dateType[sliceFormat]));
      }
    }
    return conformSlice;
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
      content: userCopyContent,
    };
    getSlicePreview(params).then((res) => {
      setSlicePre(res || []);
      setShow(true);
    });
    // const contentList = userCopyContent.split('\n');
    // const resContentLists: any = [];

    // for (let i = 0; i < contentList.length; i++) {
    //   if (contentList[i].match(dateType[sliceFormat])) {
    //     const regStr = contentList[i].match(dateType[sliceFormat])[0];
    //     const startTimeIndex = contentList[i].indexOf(regStr); //时间格式开始的下标
    //     const startStr = contentList[i].slice(0, startTimeIndex); // 时间格式前面的字符串
    //     if (slicePrefixString === '' && slicePrefixStringIndex === 0 && startTimeIndex === 0) {
    //       resContentLists.push(contentList[i]);
    //     } else if (
    //       !!slicePrefixString &&
    //       startStr.slice(-1) === slicePrefixString &&
    //       startStr.split(slicePrefixString).length - 2 === slicePrefixStringIndex
    //     ) {
    //       // if (resContentLists.length > 0) {
    //       //   resContentLists[resContentLists.length - 1] += contentList[i].split(regStr)[0]
    //       // } else {
    //       //   resContentLists[0] = contentList[i].split(regStr)[0]
    //       // }
    //       resContentLists.push(contentList[i].split(regStr)[0] + regStr + contentList[i].split(regStr)[1]);
    //     } else {
    //       if (resContentLists.length > 0) {
    //         resContentLists[resContentLists.length - 1] += contentList[i];
    //       } else {
    //         resContentLists[0] = contentList[i];
    //       }
    //     }
    //   } else {
    //     if (resContentLists.length > 0) {
    //       resContentLists[resContentLists.length - 1] += contentList[i];
    //     } else {
    //       resContentLists[0] = contentList[i];
    //     }
    //   }
    // }
    // if (userCopyContent && sliceFormat && slicePrefixStringIndex >= 0 && resContentLists.length > 0 && dateType[sliceFormat]) {
    //   // setSlicePre(handleArray(userCopyContent, getStrCount(userCopyContent, slicePrefixString, slicePrefixStringIndex, sliceFormat, dateType)))
    //   setSlicePre(resContentLists);
    //   setShow(true);
    // } else {
    //   setSlicePre([]);
    //   setShow(false);
    // }
  };

  /**
   * @method interStrhToArray 将字符串按照筛选条件进行截取,符合返回出一个截取之后的数组
   * @param {string} str 要截取的字符串
   * @param {string[]} rules 截取字符串的
   */
  const handleArray = (str: string, rules: any[]) => {
    let arr: any = [];
    const brr: any = [];

    if (str.trim().length < 1) {
      return;
    }
    if (rules.length < 1) {
      return;
    }
    for (let index = 0; index < rules.length; index++) {
      let pos = str.indexOf(rules[index]);
      while (pos > -1) {
        arr.push(pos);
        pos = str.indexOf(rules[index], pos + 1);
      }
    }

    arr = [...new Set(arr)];
    if (arr.length < 1) return;
    if (arr[0] !== 0) {
      arr.unshift(0);
    }

    for (let index = 0; index < arr.length; index++) {
      brr.push(str.slice(arr[index], arr[index + 1]));
    }
    return brr;
  };

  useEffect(() => {
    if (editUrl) {
      setSlicingRuleLog(props.slicingRuleLog);
    }
  }, [props.slicingRuleLog]);

  useEffect(() => {
    getOptions();
  }, [props.dataFormat]);

  const getOptions = () => {
    const options = props.dataFormat.map((group, index) => (
      <Option key={index} value={group}>
        {group}
      </Option>
    ));
    setOptions(options);
  };
  return (
    <div className="log-repeat-form">
      <Form.Item
        className="col-time-stramp"
        extra="注：填写时间戳，或复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始"
        label="日志切片规则"
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
              initialValue=""
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
              {/* {getFieldDecorator(`step2_${props.logType}_sliceTimestampPrefixString`, {
                // initialValue: contents,
                initialValue: '',
                rules: [
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
                ],
              })(<Input onChange={() => setStart(-1)} className="w-200" placeholder="请输入切片时间戳前缀字符串" />)}{' '} */}
              {/* ,如yyyy-MM-ddMM-dd HH-mm-ss */}
              <Input onChange={() => setStart(-1)} className="w-200" placeholder="请输入切片时间戳前缀字符串" />
            </Form.Item>
          </Col>
          <Col span={6} style={{ margin: '0 10px' }}>
            <Form.Item
              name={`step2_${props.logType}_sliceTimestampFormat`}
              initialValue={Object.keys(dateType)[0]}
              rules={[{ required: true, message: '请选择或者输入时间格式' }]}
            >
              {/* {getFieldDecorator(`step2_${props.logType}_sliceTimestampFormat`, {
                initialValue: Object.keys(dateType)[0],
                rules: [{ required: true, message: '请选择或者输入时间格式' }],
              })(
                <AutoComplete
                  dataSource={options}
                  style={{ width: 180 }}
                  className="step2_file_sliceTimestampFormat"
                  onChange={() => setStart(-1)}
                  // onSelect={onSelect}
                  // onSearch={this.onSearch}
                  // placeholder="input here"
                />
              )} */}
              <AutoComplete
                dataSource={options}
                style={{ width: 180 }}
                className="step2_file_sliceTimestampFormat"
                onChange={() => setStart(-1)}
                // onSelect={onSelect}
                // onSearch={this.onSearch}
                // placeholder="input here"
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
        <Col span={10}>
          {/* {getFieldDecorator(`step2_${props.logType}_selectionType`, {
            // initialValue: initial ? props?.addFileLog[`step2_file_selectionType_${props.getKey}`] : '',
            rules: [{ message: '请输入正则表达式，如.d+' }],
          })(
            <TextArea
              style={{ height: '120px' }}
              onBlur={(e) => {
                setStart(e.target.selectionStart);
              }}
              onClick={getSelectionType}
              className="w-300"
              placeholder="请复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始"
            />
          )} */}
          <Form.Item
            name={`step2_${props.logType}_selectionType`}
            initialValue={initial ? props?.addFileLog[`step2_file_selectionType_${props.getKey}`] : ''}
          >
            <TextArea
              style={{ height: '120px' }}
              onBlur={(e) => {
                setStart(e.target.selectionStart);
              }}
              onClick={getSelectionType}
              className="w-300"
              placeholder="请复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始"
            />
          </Form.Item>
        </Col>
        <Col span={10} style={{ display: 'inline-block' }}>
          {isShow && (
            <div className="slicePreview" style={{ height: '120px', border: '1px solid #CCC' }}>
              {slicePre &&
                slicePre.map((item: any, key) => {
                  if (item === '') {
                    return null;
                  }
                  return <span key={key}>{item?.record}</span>;
                })}
            </div>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default LogRepeatForm;
