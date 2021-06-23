import React, { useState, useEffect, useRef, useCallback } from 'react';
import { FormComponentProps } from 'antd/lib/form';
import { Form, Input, Icon, Collapse, Radio } from 'antd';
import { useDynamicList } from 'antd-advanced';
import LogFileType from './LogFileType';
import { judgeEmpty } from '../../lib/utils';
import { setIndexs } from './config';
import { hostNameList, logArr, logFilePathKey, setlogFilePathKey } from './dateRegAndGvar'
import _ from 'lodash'
import './index.less';
import { getCollectPathList } from '../../api/collect';
import { useDebounce } from '../../lib/utils'

const { Panel } = Collapse;

interface ILoopAddLogFileType extends FormComponentProps {
  suffixfilesList: number[];
  filePathList: string[];
  slicingRuleLogList: number[];
  hostNames: any
}

const LoopAddLogFileType = (props: ILoopAddLogFileType) => {
  // console.log(props,'props=====');
  const editUrl = window.location.pathname.includes('/edit-task');
  const { list, remove, getKey, push, resetList } = useDynamicList(['']);
  const { getFieldDecorator, validateFieldsAndScroll, resetFields, getFieldValue } = props.form;
  const [addFileLog, setAddFileLog] = useState({} as any);
  const [suffixfiles, setSuffixfiles] = useState(0);
  const [slicingRuleLog, setSlicingRuleLog] = useState(0);

  const handlelogSuffixfiles = (key: number) => {
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`)
    const logFilePath = getFieldValue(`step2_file_path_${key}`)
    const hostName = getFieldValue(`step2_hostName`)
    const params = {
      path: logFilePath,
      suffixMatchRegular: logSuffixfilesValue,
      hostName
    }
    setlogFilePathKey(key) // 同步日志路径的key值，防止减少日志路径key值乱
    if (logFilePath && hostName) {
      getCollectPathList(params).then((res) => {
        // logArr[key] = res.massage.split()
        logArr.push(...res.massage.split())
      })
    }
  }
  const [debouncedCallApi] = useState(() => _.debounce(handlelogSuffixfiles, 700)); // 做事件防抖时，为了防止每次触发都会重新渲染，维护一个state函数，让每次执行的时候都是同一个函数

  const addPush = () => {
    validateFieldsAndScroll((errors, values) => {
      // console.log(values, 'values========');
      if (errors) {
        resetFields(Object.keys(errors));
      }
      const index = Math.max(...setIndexs(values));
      let filelog = {} as any;
      const i = index + 1;
      filelog[`step2_file_path_${i}`] = values[`step2_file_path_${index}`];// 文件日志路径
      filelog[`step2_file_suffixSeparationCharacter_${i}`] = values[`step2_file_suffixSeparationCharacter_${index}`]; // 文件名后缀分隔字符
      filelog[`step2_file_suffixMatchType_${i}`] = values[`step2_file_suffixMatchType_${index}`]; // 采集文件后缀匹配 suffixfiles 0固定格式匹配 1正则匹配
      filelog[`step2_file_suffixLength_${i}`] = values[`step2_file_suffixLength_${index}`] || '';// 选0出现采集文件后缀匹配
      filelog[`step2_file_suffixMatchRegular_${i}`] = values[`step2_file_suffixMatchRegular_${index}`] || ''; // 选1出现采集文件后缀匹配
      filelog[`step2_file_maxBytesPerLogEvent_${i}`] = values[`step2_file_maxBytesPerLogEvent_${index}`];// 单条日志大小上限
      filelog[`step2_file_flowunit_${i}`] = values[`step2_file_flowunit_${index}`]; // 单位 1024 KB
      filelog[`step2_file_sliceType_${i}`] = values[`step2_file_sliceType_${index}`]; // 日志切片规则 slicingRuleLog 0时间戳 1正则匹配
      // filelog[`step2_file_sliceTimestampPrefixStringIndex_${i}`] = values[`step2_file_sliceTimestampPrefixStringIndex_${index}`]; // 左起第几个匹配
      // filelog[`step2_file_sliceTimestampPrefixString_${i}`] = values[`step2_file_sliceTimestampPrefixString_${index}`]; // 匹配字符
      // filelog[`step2_file_sliceTimestampFormat_${i}`] = values[`step2_file_sliceTimestampFormat_${index}`];  // 时间戳格式
      // filelog[`step2_file_sliceRegular_${i}`] = values[`step2_file_sliceRegular_${index}`]; // 日志切片规则选1 出现 切片正则
      setAddFileLog(filelog);
      push(values[`step2_file_path_${index}`]);
      setSuffixfiles(values[`step2_file_suffixMatchType_${index}`]);
      setSlicingRuleLog(values[`step2_file_sliceType_${index}`]);
    });
  };



  const row = (index: any, item: any) => (
    <div key={getKey(index)}>
      {/* <Collapse activeKey={['1']}>
        <Panel header='' key="1" showArrow={false}> */}
      <Form.Item label="日志路径" extra='可增加，最多10个, 默认与上一个选择配置项内容保持一致。'>
        {getFieldDecorator(`step2_file_path_${getKey(index)}`, {
          initialValue: item,
          rules: [{ required: true, message: '请输入日志路径' }],
        })(<Input onChange={() => debouncedCallApi(getKey(index))} className={`w-300 step2_file_path_input${getKey(index)}`} placeholder="如：/home/xiaoju/changjiang/logs/app.log" />)}
        {list.length > 1 && (<Icon type="minus-circle-o" className='ml-10' onClick={() => remove(getKey(index))} />)}
        {list.length < 11 && (<Icon type="plus-circle-o" className='ml-10' onClick={() => addPush()} />)}
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
