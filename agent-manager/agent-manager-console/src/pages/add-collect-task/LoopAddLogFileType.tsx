import React, { useState, useEffect, useRef, useCallback } from 'react';
import { IconFont, Button, Input, Radio, Form, Collapse, Row, Col, useDynamicList } from '@didi/dcloud-design';
import LogFileType from './LogFileType';
import { judgeEmpty } from '../../lib/utils';
import { setIndexs } from './config';
import { setlogFilePathKey } from './dateRegAndGvar';
import _, { cloneDeep } from 'lodash';
import './index.less';
// import { getCollectPathList } from '../../api/collect';
// import { useDebounce } from '../../lib/utils';

const { Panel } = Collapse;
// const logPath: any[] = []
// interface ILoopAddLogFileType extends FormComponentProps {
//   suffixfilesList: number[];
//   filePathList: string[];
//   slicingRuleLogList: number[];
//   hostNames: any
// }

const LoopAddLogFileType = (props: any) => {
  const editUrl = window.location.pathname.includes('/edit-task');
  const { list, remove, getKey, push, resetList, replace, sortForm } = useDynamicList<any>(['']);
  const { getFieldValue } = props.form;
  const [addFileLog, setAddFileLog] = useState({} as any);
  const [suffixfiles, setSuffixfiles] = useState(0);
  const [slicingRuleLog, setSlicingRuleLog] = useState(0);
  const logPathList = useRef<any>([]);
  const copylogPathList = useRef<any>([]);
  const allvalidate = useRef<any>(false);
  const arrList: any = [];
  const handlelogSuffixfiles = async (key: number) => {
    const logSuffixfilesValue = getFieldValue(`step2_file_suffixMatchRegular`);
    const logFilePath = await getFieldValue(`step2_file_path_${key}`);
    const hostName = getFieldValue(`step2_hostName`);
    logPathList.current[key] = logFilePath;
    sortForm(logPathList.current);
    setlogFilePathKey(key); // 同步日志路径的key值，防止减少日志路径key值乱
    props.setisNotLogPath(false);
  };
  const [debouncedCallApi] = useState(() => _.debounce(handlelogSuffixfiles, 0)); // 做事件防抖时，为了防止每次触发都会重新渲染，维护一个state函数，让每次执行的时候都是同一个函数
  const addPush = () => {
    // allvalidate.current = true;
    // copylogPathList.current = [];
    push('');
    allvalidate.current = false;

    // validateFieldsAndScroll((errors: any, values: any) => {
    //   if (errors) {
    //     resetFields(Object.keys(errors));
    //     // console.log(errors, copylogPathList.current);
    //     Object.keys(errors).forEach((errkey) => {
    //       if (errkey.includes('step2_file_path_')) {
    //         const index = logPathList.current.indexOf(values[errkey]);
    //         logPathList.current.splice(index, 1);
    //       }
    //     });
    //   }
    //   const index = Math.max(...setIndexs(values));
    //   const logFilePath = getFieldValue(`step2_file_path_${index}`);
    //   push('');
    //   setFieldsValue({ [`step2_file_suffixMatchType_${index}`]: logFilePath });
    //   setSuffixfiles(values[`step2_file_suffixMatchType_${index}`]);
    //   setSlicingRuleLog(values[`step2_file_sliceType_${index}`]);
    //   allvalidate.current = false;
    // });
  };

  const reset = (index: any) => {
    const copylist = cloneDeep(logPathList.current);
    const delValue = copylist.splice(index, 1);
    logPathList.current = copylist;
    const copyPathlist = cloneDeep(copylogPathList.current);
    copyPathlist.splice(copylogPathList.current.indexOf(delValue), 1);
    copylogPathList.current = copyPathlist;
    remove(index);
  };

  const row = (index: any, item: any) => (
    <div key={getKey(index)}>
      {/* <Collapse activeKey={['1']}>
        <Panel header='' key="1" showArrow={false}> */}
      <Form.Item
        key={getKey(index)}
        label="日志路径"
        extra={`${list.length - 1 === index ? '可增加，最多10个, 默认与上一个选择配置项内容保持一致。' : ''}`}
      >
        {/* {getFieldDecorator(`step2_file_path_${getKey(index)}`, {
          initialValue: item,
          rules: [
            {
              required: true,
              validator: (rule: any, value: string, cb: any) => {
                setTimeout(() => {
                  const listFiterLength = logPathList.current.filter((item) => item == value).length;
                  // console.log(value, logPathList.current, listFiterLength, copylogPathList.current, allvalidate.current)
                  if (!value) {
                    rule.message = '请输入日志路径';
                    cb(`请输入日志路径${getKey(index)}`);
                  } else if (listFiterLength > 1 && (allvalidate.current ? copylogPathList.current.includes(value) : true)) {
                    // console.log(listFiterLength > 1 && (allvalidate.current ? copylogPathList.current.includes(value) : true))
                    rule.message = '日志路径不能重复';
                    cb(`请输入日志路径重复`);
                  } else {
                    cb();
                  }
                  copylogPathList.current.push(value);
                }, 100);
              },
            },
          ],
        })(
          <Input
            key={getKey(index)}
            onInput={() => debouncedCallApi(getKey(index))}
            className={`w-300 step2_file_path_input${getKey(index)}`}
            placeholder="如：/home/xiaoju/changjiang/logs/app.log"
          />
        )} */}
        <Row>
          <Col span={12}>
            <Form.Item
              name={`step2_file_path_${getKey(index)}`}
              initialValue={item}
              rules={[
                {
                  required: true,
                  validator: (rule: any, value: string, cb: any) => {
                    setTimeout(() => {
                      const listFiterLength = logPathList.current.filter((item) => item == value).length;
                      // console.log(value, logPathList.current, listFiterLength, copylogPathList.current, allvalidate.current)
                      if (!value) {
                        rule.message = '请输入日志路径';
                        cb(`请输入日志路径${getKey(index)}`);
                      } else if (listFiterLength > 1 && (allvalidate.current ? copylogPathList.current.includes(value) : true)) {
                        // console.log(listFiterLength > 1 && (allvalidate.current ? copylogPathList.current.includes(value) : true))
                        rule.message = '日志路径不能重复';
                        cb(`请输入日志路径重复`);
                      } else {
                        cb();
                      }
                      copylogPathList.current.push(value);
                    }, 100);
                  },
                },
              ]}
            >
              <Input
                key={getKey(index)}
                onInput={() => debouncedCallApi(getKey(index))}
                className={`step2_file_path_input${getKey(index)}`}
                placeholder="如：/home/xiaoju/changjiang/logs/app.log"
                name={`step2_file_path_${getKey(index)}`}
              />
            </Form.Item>
          </Col>
          <Col span={6}>
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
    <div className="set-up loopaddlog-filetype">
      {list &&
        list.map((ele, index) => {
          return row(index, ele);
        })}
    </div>
  );
};

export default LoopAddLogFileType;
