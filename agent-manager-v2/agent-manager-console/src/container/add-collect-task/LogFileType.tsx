import React, { useState, useEffect, useRef, useCallback } from 'react';
import { FormComponentProps } from 'antd/lib/form';
import { Form, Input, Radio, InputNumber, Button, AutoComplete, Select } from 'antd';
import LogRepeatForm from './LogRepeatForm';
import { regChar } from '../../constants/reg';
import { hostNameList, logArr, logFilePathKey } from './dateRegAndGvar';
import { getCollectPathList } from '../../api/collect';
import { useDebounce } from '../../lib/utils'
import './index.less';


interface ILogFileTypeProps extends FormComponentProps {
  getKey: number,
  suffixfiles: number;
  slicingRuleLog: number;
  addFileLog?: any;
}

const { TextArea } = Input;
const { Option } = Select;

const LogFileType = (props: any | ILogFileTypeProps) => {
  const { getFieldDecorator, getFieldValue, setFieldsValue } = props.form;
  const editUrl = window.location.pathname.includes('/edit-task');
  const [suffixfiles, setSuffixfiles] = useState(0);
  const [isNotLogPath, setIsNotLogPath] = useState(true);
  // const initial = props?.addFileLog && !!Object.keys(props?.addFileLog)?.length;
  const options = hostNameList.length > 0 && hostNameList.map((group: any, index: number) => {
    return <Option key={group.id} value={group.hostName}>
      {group.hostName}
    </Option>
  })

  const onSuffixfilesChange = (e: any) => {
    setSuffixfiles(e.target.value);
  }
  const handlelogSuffixfiles = useDebounce(() => {
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`)
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`)
    const hostName = getFieldValue(`step2_hostName`)
    const params = {
      path: logFilePath,
      suffixMatchRegular: logSuffixfilesValue,
      hostName
    }
    if (logFilePath && hostName) {
      getCollectPathList(params).then((res) => {
        // logArr[key] = res.massage.split()
        console.log(res, 'res')
        logArr.push(...res.massage.split())
      })
    }
  }, 700)

  const onLogFilterChange = (e: any) => {
    handlelogSuffixfiles()
  }

  useEffect(() => {
    if (editUrl) {
      setSuffixfiles(props.suffixfiles);
    }
  }, [props.suffixfiles]);

  return (
    <div className='set-up' key={props.getKey}>
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
      {hostNameList.length > 0 ? <Form.Item label="映射主机">
        {getFieldDecorator(`step2_hostName`, {
          initialValue: hostNameList[0]?.hostName,
          rules: [{ required: true, message: '请选择映射主机名称' }],
        })(
          <Select
            showSearch
            style={{ width: 200 }}
            placeholder="Select a person"
            optionFilterProp="children"
            onChange={onLogFilterChange}
          // onFocus={onFocus}
          // onBlur={onBlur}
          // onSearch={onSearch}
          // filterOption={(input, option) =>
          //   option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
          // }
          >
            {options}
          </Select>
          // <Radio.Group onChange={onLogFilterChange}>
          //   {
          //     hostNameList?.map((ele: any, index: number) => {
          //       return <Radio key={ele.id} value={ele.id}>{ele.hostName}</Radio>
          //     })
          //   }
          // </Radio.Group>
        )}
      </Form.Item> : null}
      <Form.Item label="采集文件后缀匹配样式">
        {getFieldDecorator(`step2_file_suffixMatchRegular`, {
          initialValue: '',
          rules: [{ required: true, message: '请输入后缀的正则匹配' }],
        })(<Input style={{ width: '400px' }} onChange={handlelogSuffixfiles} placeholder='请输入后缀的正则匹配，不包括分隔符。如：^([\d]{0,6})$' />)}
        {/* <Button onClick={() => handlelogSuffixfiles(props.getKey)} type="primary" style={{ marginLeft: '20px' }}>查看日志路径下所有文件</Button> */}
      </Form.Item>
      <Form.Item>
        <ul className={`logfile_list logFileList`}>
          {
            logArr.length > 0 && logArr.map((logfile: string, key: number) => <li key={key}>{logfile}</li>)
          }
        </ul>
      </Form.Item>

      {/* 重复表单 */}
    </div>
  );
};

export default LogFileType;
