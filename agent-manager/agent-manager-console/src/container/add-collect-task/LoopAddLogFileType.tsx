import React, { useState, useEffect, useRef, useCallback } from 'react';
import { FormComponentProps } from 'antd/lib/form';
import { Form, Input, Icon, Collapse, Radio } from 'antd';
import { useDynamicList } from 'antd-advanced';
import LogFileType from './LogFileType';
import { judgeEmpty } from '../../lib/utils';
import { setIndexs } from './config';
import { setlogFilePathKey } from './dateRegAndGvar'
import _ from 'lodash'
import './index.less';
import { getCollectPathList } from '../../api/collect';
import { useDebounce } from '../../lib/utils'

const { Panel } = Collapse;
// const logPath: any[] = []
interface ILoopAddLogFileType extends FormComponentProps {
  suffixfilesList: number[];
  filePathList: string[];
  slicingRuleLogList: number[];
  hostNames: any
}

const LoopAddLogFileType = (props: ILoopAddLogFileType | any) => {
  // console.log(props,'props=====');
  const editUrl = window.location.pathname.includes('/edit-task');
  const { list, remove, getKey, push, resetList, replace } = useDynamicList<any>([]);
  const { getFieldDecorator, validateFieldsAndScroll, resetFields, getFieldValue } = props.form;
  const [addFileLog, setAddFileLog] = useState({} as any);
  const [suffixfiles, setSuffixfiles] = useState(0);
  const [slicingRuleLog, setSlicingRuleLog] = useState(0);

  const handlelogSuffixfiles = async (key: number) => {
    // console.log(list, key, "key")

    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`)
    const logFilePath = getFieldValue(`step2_file_path_${key}`)
    const hostName = getFieldValue(`step2_hostName`)
    // console.log(logFilePath, 'logFilePath')
    // replace(key, logFilePath)

    const params = {
      path: logFilePath,
      suffixMatchRegular: logSuffixfilesValue,
      hostName
    }
    setlogFilePathKey(key) // 同步日志路径的key值，防止减少日志路径key值乱
    props.setisNotLogPath(false)
    // if (logFilePath && logSuffixfilesValue) {
    //   getCollectPathList(params).then((res) => {
    //     props.setLogListFile(res)
    //   })
    // }
  }
  const [debouncedCallApi] = useState(() => _.debounce(handlelogSuffixfiles, 0)); // 做事件防抖时，为了防止每次触发都会重新渲染，维护一个state函数，让每次执行的时候都是同一个函数
  // if (!logPath.includes(getFieldValue(`step2_file_path_${0}`))) {
  //   logPath.push(getFieldValue(`step2_file_path_${0}`))
  // }
  const addPush = () => {
    validateFieldsAndScroll((errors: any, values: any) => {
      // console.log(values, 'values')
      if (errors) {
        resetFields(Object.keys(errors));
      }
      const index = Math.max(...setIndexs(values));
      let filelog = {} as any;
      // const i = index + 1;
      // filelog[`step2_file_path_${i}`] = '';// 文件日志路径
      // filelog[`step2_file_suffixSeparationCharacter_${i}`] = values[`step2_file_suffixSeparationCharacter_${index}`]; // 文件名后缀分隔字符
      // filelog[`step2_file_suffixMatchType_${i}`] = values[`step2_file_suffixMatchType_${index}`]; // 采集文件后缀匹配 suffixfiles 0固定格式匹配 1正则匹配
      // filelog[`step2_file_suffixLength_${i}`] = values[`step2_file_suffixLength_${index}`] || '';// 选0出现采集文件后缀匹配
      // filelog[`step2_file_suffixMatchRegular_${i}`] = values[`step2_file_suffixMatchRegular_${index}`] || ''; // 选1出现采集文件后缀匹配
      // filelog[`step2_file_maxBytesPerLogEvent_${i}`] = values[`step2_file_maxBytesPerLogEvent_${index}`];// 单条日志大小上限
      // filelog[`step2_file_flowunit_${i}`] = values[`step2_file_flowunit_${index}`]; // 单位 1024 KB
      // filelog[`step2_file_sliceType_${i}`] = values[`step2_file_sliceType_${index}`]; // 日志切片规则 slicingRuleLog 0时间戳 1正则匹配
      // filelog[`step2_file_sliceTimestampPrefixStringIndex_${i}`] = values[`step2_file_sliceTimestampPrefixStringIndex_${index}`]; // 左起第几个匹配
      // filelog[`step2_file_sliceTimestampPrefixString_${i}`] = values[`step2_file_sliceTimestampPrefixString_${index}`]; // 匹配字符
      // filelog[`step2_file_sliceTimestampFormat_${i}`] = values[`step2_file_sliceTimestampFormat_${index}`];  // 时间戳格式
      // filelog[`step2_file_sliceRegular_${i}`] = values[`step2_file_sliceRegular_${index}`]; // 日志切片规则选1 出现 切片正则
      // setAddFileLog(filelog);

      // if (!logPath.includes(values[`step2_file_path_${index}`])) {

      //   logPath.push(values[`step2_file_path_${index}`])

      // }
      // console.log(index, 'index')
      push(values[`step2_file_path_${index}`]);
      // console.log(list, 'list')
      setSuffixfiles(values[`step2_file_suffixMatchType_${index}`]);
      setSlicingRuleLog(values[`step2_file_sliceType_${index}`]);
    });
  };

  const reset = (index: any) => {
    // console.log(index)

    remove(index)
    // console.log(list, 'resetList')
  }

  const row = (index: any, item: any) => (
    <div key={getKey(index)}>
      {/* <Collapse activeKey={['1']}>
        <Panel header='' key="1" showArrow={false}> */}
      <Form.Item label="日志路径" extra={`${list.length - 1 === index ? '可增加，最多10个, 默认与上一个选择配置项内容保持一致。' : ''}`}>
        {getFieldDecorator(`step2_file_path_${getKey(index)}`, {
          initialValue: item,
          rules: [{
            required: true,
            // message: '请输入日志路径',
            validator: (rule: any, value: string, cb: any) => {
              // console.log(rule, 'rule')
              // console.log(value, 'value')
              // console.log(list, 'list')
              if (!value) {
                rule.message = '请输入日志路径'
                cb(`请输入日志路径${index}`)
              } else {
                cb()
              }
              // if (list.includes(value))
              // return !!value && new RegExp(regName).test(value);
            },
          }],
        })(<Input onInput={() => debouncedCallApi(getKey(index))} className={`w-300 step2_file_path_input${getKey(index)}`} placeholder="如：/home/xiaoju/changjiang/logs/app.log" />)}
        {list.length > 1 && (<Icon type="minus-circle-o" className='ml-10' onClick={() => {
          reset(index)
        }} />)}
        {list.length < 10 && (<Icon type="plus-circle-o" className='ml-10' onClick={() => addPush()} />)}
      </Form.Item>
      {/* </Panel>
      </Collapse> */}
    </div>
  );

  useEffect(() => {
    if (editUrl) {
      resetList(props.filePathList);
    }
  }, [props.filePathList]);

  return (
    <div className='set-up loopaddlog-filetype'>
      {list && list.map((ele, index) => {
        return row(index, ele)
      })}
    </div>
  );
};

export default LoopAddLogFileType;
