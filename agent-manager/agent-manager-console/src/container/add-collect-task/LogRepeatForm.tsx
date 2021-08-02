import React, { useState, useEffect } from 'react';
import { FormComponentProps } from 'antd/lib/form';
import { Row, Col, Select, Form, Input, InputNumber, Radio, Button, AutoComplete } from 'antd';
import { flowUnitList } from '../../constants/common';
import { ILabelValue } from '../../interface/common';
import { yyyyMMDDHHMMss, HHmmssSSS, yyyyMMDDHHMMssSSS, yyyyMMDDHHMMssSS, fuhao } from './dateRegAndGvar'
import './index.less';
import { lastIndexOf } from 'lodash';
import { regLogSliceTimestampPrefixString } from '../../constants/reg';

interface ILogRepeatForm extends FormComponentProps {
  getKey?: number | string,
  logType: string,
  slicingRuleLog: number;
  addFileLog?: any;
}

const { TextArea } = Input;
const { Option } = AutoComplete;

const LogRepeatForm = (props: ILogRepeatForm) => {
  const { getFieldDecorator, setFieldsValue, getFieldValue } = props.form;
  const editUrl = window.location.pathname.includes('/edit-task');
  const [slicingRuleLog, setSlicingRuleLog] = useState(0);
  const initial = props?.addFileLog && !!Object.keys(props?.addFileLog)?.length;
  const [contents, setContents] = useState<any>(null); // 鼠标选取内容正则匹配结果
  const [isShow, setShow] = useState(false); // 是否显示日志切片框
  const [slicePre, setSlicePre] = useState<string[]>([]); // 日志切片列表
  const [start, setStart] = useState(0)
  const onSlicingRuleLogChange = (e: any) => {
    setSlicingRuleLog(e.target.value);
  }

  // 匹配时间格式
  const regYymdhmsSSS = new RegExp(yyyyMMDDHHMMssSSS)
  const regYymdhmsSS = new RegExp(yyyyMMDDHHMMssSS)
  const regYymdhms = new RegExp(yyyyMMDDHHMMss)
  const regHmsS = new RegExp(HHmmssSSS)
  // const regYymd = new RegExp(yyyyMMDD)
  // const regYmd = new RegExp(yyMMDD)

  const dateType: any = {
    "yyyy-MM-dd HH:mm:ss.SSS": regYymdhmsSSS,
    "yyyy-MM-dd HH:mm:ss,SSS": regYymdhmsSS,
    "yyyy-MM-dd HH:mm:ss": regYymdhms,
    'HH:mm:ss.SSS': regHmsS,
    // 'YYYY-MM-DD': regYymd,
    // 'YY-MM-DD': regYmd,
  }

  // 获取选中的内容，并将内容匹配对应规则放入时间戳表单中
  let sliceTimestampPrefixString: string = '';
  let sliceTimestampFormat = ''
  let sliceTimestampPrefixStringIndex = 0

  /**
   * @method cheageSliceTypeReg 正则匹配日志时间类型
   * @param reg 需要匹配的正则
   * @param dateType 匹配的时间格式
   * @param selObj 鼠标选中的字符串
   * @param userCopyContent 文本框的内容
   */
  const cheageSliceTypeReg = (reg: any, dateType: string, selObj: string, userCopyContent: string) => {
    sliceTimestampPrefixString = selObj.split(selObj?.match(reg)[0])[0].slice(-1);
    sliceTimestampFormat = dateType
    let aNewlineIs = userCopyContent.slice(0, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0])).lastIndexOf('\n')
    if (sliceTimestampPrefixString === '') {
      if (aNewlineIs === -1) {
        sliceTimestampPrefixStringIndex = userCopyContent.slice(0, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0])).split(`${sliceTimestampPrefixString}`).length - 1
      } else {
        sliceTimestampPrefixStringIndex = userCopyContent.slice(aNewlineIs, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0])).split(sliceTimestampPrefixString).length - 1
      }
    } else {
      if (aNewlineIs === -1) {
        sliceTimestampPrefixStringIndex = userCopyContent.slice(0, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0])).split(`${sliceTimestampPrefixString}`).length - 2
      } else {
        sliceTimestampPrefixStringIndex = userCopyContent.slice(aNewlineIs, userCopyContent.indexOf(selObj) + selObj.indexOf(selObj?.match(reg)[0])).split(sliceTimestampPrefixString).length - 2
      }
    }


    // setContents(selObj?.match(reg)[0])
  }

  const getSelectionType = () => {
    let selObj = window?.getSelection()?.toString() || document.getSelection()?.toString();
    const userCopyContent = getFieldValue(`step2_${props.logType}_selectionType`)
    setShow(false)

    if (selObj === '') return

    if (!!userCopyContent) {
      /*
      12$2018-04-08 asdsad
$123$123$2018-04-09 sqrwqrq
$123$33$2018-01-08 sqrwqrq

      yyyy-MM-dd HH:mm:ss.SSS
      yyyy-MM-dd HH:mm:ss
      HH:mm:ss.SSS

    2021-06-25 14:51:31.798 [main] ERROR logger3 - 4191185 1624603891798
    2021-06-25 14:51:31.798 [main] ERROR logger3 - 4191185 1624603891798
      */

      if (selObj?.match(regYymdhmsSSS)) {
        cheageSliceTypeReg(regYymdhmsSSS, "yyyy-MM-dd HH:mm:ss.SSS", selObj, userCopyContent)
      } else if (selObj?.match(regYymdhmsSS)) {
        cheageSliceTypeReg(regYymdhmsSS, "yyyy-MM-dd HH:mm:ss,SSS", selObj, userCopyContent)
      } else if (selObj?.match(regYymdhms)) {
        cheageSliceTypeReg(regYymdhms, "yyyy-MM-dd HH:mm:ss", selObj, userCopyContent)
      } else if (selObj?.match(regHmsS)) {
        cheageSliceTypeReg(regHmsS, "HH:mm:ss.SSS", selObj, userCopyContent)
      }
      setFieldsValue({
        [`step2_${props.logType}_sliceTimestampPrefixString`]: sliceTimestampPrefixString || '',
        [`step2_${props.logType}_sliceTimestampFormat`]: sliceTimestampFormat,
        [`step2_${props.logType}_sliceTimestampPrefixStringIndex`]: sliceTimestampPrefixStringIndex
      })
      return
    }
  }
  /**
   * @method getStrCount:boolean[] 获取字符串中符合条件的字符串并返回布尔值
   * @param content 需要处理的内容
   * @param sliceSymbol 截取的符号内容
   * @param symbolIndex 截取的符号的下标
   * @param sliceFormat 符合的时间格式
   * @param { {[x: string]: any;} } dateType 时间格式类型集合
   */
  const getStrCount = (content: any, sliceSymbol: string, symbolIndex: number, sliceFormat: string, dateType: { [x: string]: any; }): boolean[] => {
    const conformSlice = []
    const userInterceptionContent = content.split('\n')
    const conformSliceBoolean = userInterceptionContent.map((item: string) => {
      return item.split(sliceSymbol).length - 2 === symbolIndex
    })
    for (let index = 0; index < userInterceptionContent.length; index++) {
      if (conformSliceBoolean[index] || userInterceptionContent[index]?.match(dateType[sliceFormat])) {
        conformSlice.push(...userInterceptionContent[index]?.match(dateType[sliceFormat]))
      }
    }
    return conformSlice
  }
  // 日志预览按钮
  const slicePreview = () => {
    const userCopyContent = getFieldValue(`step2_${props.logType}_selectionType`)
    const slicePrefixString = getFieldValue(`step2_${props.logType}_sliceTimestampPrefixString`)
    const sliceFormat = getFieldValue(`step2_${props.logType}_sliceTimestampFormat`)
    let slicePrefixStringIndex = getFieldValue(`step2_${props.logType}_sliceTimestampPrefixStringIndex`)
    // let newVal = userCopyContent.slice(start < 0 ? 0 : start)
    if (slicePrefixStringIndex < 0) {
      slicePrefixStringIndex = 0
    }
    let contentList = userCopyContent.split('\n')
    let resContentLists: any = []

    for (let i = 0; i < contentList.length; i++) {
      if (contentList[i].match(dateType[sliceFormat])) {
        let regStr = contentList[i].match(dateType[sliceFormat])[0]
        let startTimeIndex = contentList[i].indexOf(regStr) //时间格式开始的下标
        let startStr = contentList[i].slice(0, startTimeIndex) // 时间格式前面的字符串
        if (slicePrefixString === '' && slicePrefixStringIndex === 0 && startTimeIndex === 0) {
          resContentLists.push(contentList[i])
        } else if (!!slicePrefixString && startStr.slice(-1) === slicePrefixString && startStr.split(slicePrefixString).length - 2 === slicePrefixStringIndex) {
          // if (resContentLists.length > 0) {
          //   resContentLists[resContentLists.length - 1] += contentList[i].split(regStr)[0]
          // } else {
          //   resContentLists[0] = contentList[i].split(regStr)[0]
          // }
          resContentLists.push(contentList[i].split(regStr)[0] + regStr + contentList[i].split(regStr)[1])
        } else {
          if (resContentLists.length > 0) {
            resContentLists[resContentLists.length - 1] += contentList[i]
          } else {
            resContentLists[0] = contentList[i]
          }
        }
      } else {
        if (resContentLists.length > 0) {
          resContentLists[resContentLists.length - 1] += contentList[i]
        } else {
          resContentLists[0] = contentList[i]
        }
      }
    }
    if (userCopyContent && sliceFormat && slicePrefixStringIndex >= 0 && resContentLists.length > 0 && dateType[sliceFormat]) {
      // setSlicePre(handleArray(userCopyContent, getStrCount(userCopyContent, slicePrefixString, slicePrefixStringIndex, sliceFormat, dateType)))
      setSlicePre(resContentLists)
      setShow(true)
    } else {
      setSlicePre([])
      setShow(false)
    }
  }

  /**
   * @method interStrhToArray 将字符串按照筛选条件进行截取,符合返回出一个截取之后的数组
   * @param {string} str 要截取的字符串 
   * @param {string[]} rules 截取字符串的
   */
  const handleArray = (str: string, rules: any[]) => {
    let arr: any = []
    let brr: any = []


    if (str.trim().length < 1) {
      return
    }
    if (rules.length < 1) {
      return
    }
    for (let index = 0; index < rules.length; index++) {
      let pos = str.indexOf(rules[index])
      while (pos > -1) {
        arr.push(pos);
        pos = str.indexOf(rules[index], pos + 1);
      }
    }

    arr = [...new Set(arr)]
    if (arr.length < 1) return
    if (arr[0] !== 0) {
      arr.unshift(0)
    }

    for (let index = 0; index < arr.length; index++) {
      brr.push(str.slice(arr[index], arr[index + 1]))
    }
    return brr
  }

  useEffect(() => {
    if (editUrl) {
      setSlicingRuleLog(props.slicingRuleLog);
    }
  }, [props.slicingRuleLog]);


  const options = Object.keys(dateType).map((group, index) => (
    <Option key={index} value={group}>
      {group}
    </Option>
  ))

  return (
    <div className='log-repeat-form'>
      {/* <Form.Item label="单条日志大小上限" className='col-unit-log'>
        <Row>
          <Col span={13}>
            {getFieldDecorator(`step2_${props.logType}_maxBytesPerLogEvent_${props.getKey}`, {
              initialValue: initial ? props?.addFileLog[`step2_file_maxBytesPerLogEvent_${props.getKey}`] : 2,
              rules: [{ required: true, message: '请输入单条日志大小上限' }],
            })(
              <InputNumber className='w-300' min={1} placeholder='请输入数字' />,
            )}
          </Col>
          <Col span={3}>
            <Form.Item>
              {getFieldDecorator(`step2_${props.logType}_flowunit_${props.getKey}`, {
                initialValue: initial ? props?.addFileLog[`step2_file_flowunit_${props.getKey}`] : flowUnitList[1]?.value,
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
      </Form.Item> */}

      {/* <Form.Item label='日志切片规则'>
        {getFieldDecorator(`step2_${props.logType}_sliceType_${props.getKey}`, {
          initialValue: initial ? props?.addFileLog[`step2_file_sliceType_${props.getKey}`] : 0,
          rules: [{ required: true, message: '请选择日志切片规则' }],
        })(
          <Radio.Group onChange={onSlicingRuleLogChange}>
            <Radio value={0}>时间戳</Radio>
            <Radio value={1}>正则匹配</Radio>
          </Radio.Group>,
        )}
      </Form.Item> */}

      {/* {slicingRuleLog === 0 ? */}
      <Form.Item className='col-time-stramp' extra='注：填写时间戳，或复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始' label='日志切片规则'>
        <Row>
          <Col span={6}>
            左起第&nbsp;{getFieldDecorator(`step2_${props.logType}_sliceTimestampPrefixStringIndex`, {
            initialValue: 0,
            rules: [{
              required: true,
              message: '请输入',
              validator: (rule: any, value: any, cb) => {
                if (value != 0 && value == '') {
                  console.log(value)
                  rule.message = '请输入'
                  cb('请输入')
                } else if (!new RegExp(regLogSliceTimestampPrefixString).test(value)) {
                  rule.message = '最大长度限制8位'
                  cb('最大长度限制8位')
                } else {
                  cb()
                }
              },
            }],
          })(<InputNumber style={{ margin: '0 5px', width: '65px' }} min={0} max={99999999} precision={0} />)}&nbsp;个匹配上
            </Col>
          <Col span={6} style={{ margin: '0 10px' }} >
            <Form.Item>
              {getFieldDecorator(`step2_${props.logType}_sliceTimestampPrefixString`, {
                // initialValue: contents,
                initialValue: '',
                rules: [{
                  required: true,
                  //  message: '请输入',
                  validator: (rule: any, value: string, cb) => {
                    if (!value) {
                      rule.message = '请输入切片时间戳前缀字符串'
                      cb('请输入切片时间戳前缀字符串')
                    } else if (!new RegExp(regLogSliceTimestampPrefixString).test(value)) {
                      rule.message = '前缀字符串最大长度为8位'
                      cb('前缀字符串最大长度为8位')
                    } else {
                      cb()
                    }
                  },
                }],
              })(<Input onChange={() => setStart(-1)} className='w-200' placeholder='请输入切片时间戳前缀字符串' />)} {/* ,如yyyy-MM-ddMM-dd HH-mm-ss */}
            </Form.Item>
          </Col>
          <Col span={6} style={{ margin: '0 10px' }} >
            {/* <Form.Item>
              {getFieldDecorator(`step2_${props.logType}_sliceTimestampFormat_${props.getKey}`, {
                initialValue: initial ? props?.addFileLog[`step2_file_sliceTimestampFormat_${props.getKey}`] : '',
                rules: [{ required: true, message: '请输入' }],
              })(<Input className='w-200' placeholder='请输入时间戳格式' />)}
            </Form.Item> */}
            {/* <Form.Item>
              {getFieldDecorator(`step2_${props.logType}_sliceTimestampFormat`, {
                initialValue: initial ? props?.addFileLog[`step2_file_sliceTimestampFormat`] : Object.keys(dateType)[0],
                rules: [{ required: true, message: '请选择或者输入时间格式' }],
              })(<Select className='w-200' placeholder="请选择或者输入时间格式">
                {Object.keys(dateType).map((v: any, index: number) => {
                  return (<Select.Option key={index} value={v}>
                    {v}
                  </Select.Option>)
                })}
              </Select>)}
            </Form.Item> */}
            <Form.Item>
              {getFieldDecorator(`step2_${props.logType}_sliceTimestampFormat`, {
                initialValue: Object.keys(dateType)[0],
                rules: [{ required: true, message: '请选择或者输入时间格式' }],
              })(<AutoComplete
                dataSource={options}
                style={{ width: 180 }}
                className='step2_file_sliceTimestampFormat'
                onChange={() => setStart(-1)}
              // onSelect={onSelect}
              // onSearch={this.onSearch}
              // placeholder="input here"
              />)}
            </Form.Item>

          </Col>
          <Col span={3} style={{ margin: '0 10px' }} >
            <Button type='primary' onClick={slicePreview}>切片预览</Button>
          </Col>
        </Row>
      </Form.Item>
      <Row style={{ marginLeft: '16.5%' }}>
        <Col span={8} >
          {getFieldDecorator(`step2_${props.logType}_selectionType`, {
            // initialValue: initial ? props?.addFileLog[`step2_file_selectionType_${props.getKey}`] : '',
            rules: [{ message: '请输入正则表达式，如.\d+' }],
          })(<TextArea style={{ height: '120px' }} onBlur={e => {
            setStart(e.target.selectionStart)
          }} onClick={getSelectionType} className='w-300' placeholder='请复制日志文本并通过划取时间戳自动填写，复制文本时，为保证正确性，需从日志任一段落行首开始' />)}
        </Col>
        <Col span={8} style={{ display: 'block' }}>
          {
            isShow && <div className='w-300 slicePreview' style={{ height: '120px', border: '1px solid #CCC' }}  >
              {
                slicePre && slicePre.map((item: string, key) => {
                  if (item === '') {
                    return null
                  }
                  return <span key={key}>{item}</span>
                })
              }
            </div>
          }
        </Col>
      </Row>
    </div>
  );
};

export default LogRepeatForm;
