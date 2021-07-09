import React, { useState, useEffect, useRef, useCallback } from 'react';
import { FormComponentProps } from 'antd/lib/form';
import { Form, Input, Radio, InputNumber, Button, AutoComplete, Select } from 'antd';
import LogRepeatForm from './LogRepeatForm';
import { regChar } from '../../constants/reg';
import { logFilePathKey } from './dateRegAndGvar';
import { getCollectPathList } from '../../api/collect';
import { useDebounce } from '../../lib/utils'
import './index.less';
import { getHostListbyServiceId } from '../../api';


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
  const [isNotLogPath, setIsNotLogPath] = useState(false);
  const [fileArrList, setFileArrList] = useState([])
  const [hostNameList, setHostNameList] = useState<any>([])
  // const initial = props?.addFileLog && !!Object.keys(props?.addFileLog)?.length;
  const options = hostNameList.length > 0 && hostNameList.map((group: any, index: number) => {
    return <Option key={group.id} value={group.hostName}>
      {group.hostName}
    </Option>
  })
  const onSuffixfilesChange = (e: any) => {
    setSuffixfiles(e.target.value);
  }

  const handleLogPath = useDebounce(() => {
    const serviceId = getFieldValue(`step1_serviceId`)
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`)
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`)
    // const hostName = getFieldValue(`step2_hostName`)
    if (serviceId && logSuffixfilesValue && logFilePath) {
      getHostListbyServiceId(serviceId).then((res: any) => {
        if (res?.hostList?.length > 0) {
          props.setisNotLogPath(true)
          setHostNameList(res?.hostList)
          handlelogSuffixfiles()
        } else {
          props.setisNotLogPath(false)
          setHostNameList([])
        }
      })
    }
  }, 200)

  const handlelogSuffixfiles = useDebounce(() => {
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`)
    const logFilePath = getFieldValue(`step2_file_path_${logFilePathKey}`)
    const hostName = getFieldValue(`step2_hostName`)
    const params = {
      path: logFilePath,
      suffixMatchRegular: logSuffixfilesValue,
      hostName
    }
    if (logFilePath && logSuffixfilesValue && hostName) {
      getCollectPathList(params).then((res) => {
        // logArr[key] = res.massage.split()
        setFileArrList(res)
      })
    }
  }, 0)
  const onLogFilterChange = (e: any) => {
    handlelogSuffixfiles()
  }

  useEffect(() => {
    if (editUrl) {
      setSuffixfiles(props.suffixfiles);
    }
  }, [props.suffixfiles]);

  useEffect(() => {
    setFileArrList(props.logListFile);
  }, [props.logListFile]);

  useEffect(() => {
    setIsNotLogPath(props.isNotLogPath)
  }, [props.isNotLogPath]);

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
      <Form.Item label="采集文件后缀匹配样式" extra='注:如需验证或遇到操作困难,可点击预览,展示日志路径下文件列表'>
        {getFieldDecorator(`step2_file_suffixMatchRegular`, {
          initialValue: '',
          rules: [{ required: true, message: '请输入后缀的正则匹配' }],
        })(<Input style={{ width: '400px' }} onChange={() => {
          setIsNotLogPath(false)
          setHostNameList([])
        }} placeholder='请输入后缀的正则匹配，不包括分隔符。如：^([\d]{0,6})$' />)}
        <Button onClick={handleLogPath} type="primary" style={{ marginLeft: '20px' }}>预览</Button>
      </Form.Item>
      {hostNameList.length > 0 && props.isNotLogPath ? <Form.Item label="映射主机">
        {getFieldDecorator(`step2_hostName`, {
          initialValue: hostNameList[0].hostName,
          rules: [{ message: '请选择映射主机名称' }],
        })(
          <Select
            showSearch
            style={{ width: 200 }}
            placeholder="请选择主机"
            optionFilterProp="children"
            onChange={handlelogSuffixfiles}
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
      {
        hostNameList.length > 0 && props.isNotLogPath && <Form.Item>
          <ul className={`logfile_list logFileList`}>
            {
              fileArrList && fileArrList?.map((logfile: string, key: number) => <li key={key}>{logfile}</li>)
            }
          </ul>
        </Form.Item>
      }
      {/* 重复表单 */}
    </div>
  );
};

export default LogFileType;
