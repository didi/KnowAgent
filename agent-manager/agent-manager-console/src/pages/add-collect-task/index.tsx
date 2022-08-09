import React, { useState, useEffect } from 'react';
import classNames from 'classnames';
import { Modal, Steps, Form, Button, Spin, AppContainer } from '@didi/dcloud-design';
import CollectObjectConfiguration from './CollectObjectConfiguration';
import CollectLogPathConfiguration from './CollectLogPathConfiguration';
import CollectLogRuleConfiguration from './CollectLogRuleConfiguration';
import ClientClearSelfMonitor from './ClientClearSelfMonitor';
import { NavRouterLink } from '../../components/CustomComponent';
import { addCollectTask, getCollectDetails, editCollectTask, getDataFormat } from '../../api/collect';
import { useLocation } from 'react-router-dom';
import { setStepParams, setEditThreeParams, setEditFourParams } from './config';
import { setLimitUnit, useDebounce } from '../../lib/utils';
import moment from 'moment';
import './index.less';
import { getHostListbyServiceId } from '../../api/agent';
import { setHostNameList, setlogFilePathKey } from './dateRegAndGvar';
import { EventBusTypes } from '../../constants/event-types';
const { Step } = Steps;

const validateStepRegex = {
  0: /^step1_/,
  1: /^step2_/,
  2: /^step3_/,
  3: /^step4_/,
} as any;

// const mapDispatchToProps = (dispatch: Dispatch) => ({
//   setCollectType: (collectType: number) => dispatch(actions.setCollectType(collectType)),
//   setLogType: (logType: string) => dispatch(actions.setLogType(logType)),
// });
// type Props = ReturnType<typeof mapDispatchToProps>;
const StepsForm = (props: any) => {
  const [form] = Form.useForm();
  const { state } = useLocation<any>();
  const editUrl = state?.taskId;
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const { validateFields, resetFields, setFieldsValue, getFieldValue } = form;
  const [serviceId, setServiceId] = useState(0);
  // step1 编辑时复原页面1的动作
  const [collectMode, setCollectMode] = useState(0);
  const [openHistory, setOpenHistory] = useState(false);
  const [historyFilter, setHistoryFilter] = useState('current');
  const [hostRange, setHostRange] = useState(0);
  const [hostWhite, setHostWhite] = useState('hostname');
  const [hostNames, setHostNames] = useState([]);
  // step2 编辑时复原页面2的动作
  const [collectLogType, setCollectLogType] = useState('file');
  const [logFilter, setLogFilter] = useState(0);
  const [cataPathlist, setCataPathlist] = useState([] as string[]);

  const [slicingRuleLog, setSlicingRuleLog] = useState(0); // LogRepeatForm cata (cata/file重复)页面
  const [filePathList, setFilePathList] = useState(['']);
  const [originFilePathList, setOriginFilePathList] = useState(['']);
  const [hookPathList, setHookPathList] = useState(['']);
  const [slicingRuleLogList, setSlicingRuleLogList] = useState([] as number[]); // LogRepeatForm file 循环 (cata/file重复)页面
  const [suffixfilesList, setSuffixfilesList] = useState([] as number[]); // LogFileType （file里面的）循环 页面
  const [isNotLogPath, setisNotLogPath] = useState(false);
  const [fileLogCollectDetail, setFileLogCollectDetail] = useState([{}] as [{ id: number; logCollectTaskId: number }]); // 编辑时文件类型路径采集配置
  const [hostList, setHostList] = useState([]);
  const [dataFormat, setDataFormat] = useState([]);
  const [sliceRule, setSliceRule] = useState({});
  const [collectDetail, setCollectDetail] = useState({});

  const steps = [
    {
      title: '采集对象配置',
      content: (
        <CollectObjectConfiguration
          hostNames={hostNames}
          form={form}
          collectMode={collectMode}
          openHistory={openHistory}
          historyFilter={historyFilter}
          hostRange={hostRange}
          hostWhite={hostWhite}
          isNotLogPath={isNotLogPath}
          setisNotLogPath={setisNotLogPath}
          hostNameList={(list) => {
            setHostList(list);
          }}
          serviceId={serviceId}
        />
      ),
    },
    {
      title: '采集路径配置',
      content: (
        <CollectLogPathConfiguration
          form={form}
          hostNames={hostNames}
          collectLogType={collectLogType}
          logFilter={logFilter}
          cataPathlist={cataPathlist}
          slicingRuleLog={slicingRuleLog}
          originFilePathList={originFilePathList}
          setFilePathList={setFilePathList}
          setHookPathList={setHookPathList}
          slicingRuleLogList={slicingRuleLogList}
          suffixfilesList={suffixfilesList}
          isNotLogPath={isNotLogPath}
          setisNotLogPath={setisNotLogPath}
          hostList={hostList}
          dataFormat={dataFormat}
          sliceRule={sliceRule}
          editUrl={editUrl}
        />
      ),
    },
    {
      title: '切片规则配置',
      content: (
        <CollectLogRuleConfiguration
          form={form}
          hostNames={hostNames}
          collectLogType={collectLogType}
          logFilter={logFilter}
          cataPathlist={cataPathlist}
          slicingRuleLog={slicingRuleLog}
          filePathList={filePathList}
          slicingRuleLogList={slicingRuleLogList}
          suffixfilesList={suffixfilesList}
          isNotLogPath={isNotLogPath}
          setisNotLogPath={setisNotLogPath}
          hostList={hostList}
          dataFormat={dataFormat}
          sliceRule={sliceRule}
          editUrl={editUrl}
        />
      ),
    },
    {
      title: '接收端配置与监控',
      content: <ClientClearSelfMonitor form={form} logType="file" editUrl={editUrl} collectDetail={collectDetail} />,
    },
  ];

  const onStepsChange = (current: number) => {
    if (current === 1 || current === 3) {
      //校验每一步的参数
      form
        .validateFields()
        .then(() => {
          setCurrentStep(current);
        })
        .catch((errors) => {
          if (errors) {
            const errorReg = validateStepRegex[currentStep];
            const err = {} as any;
            const contentErrorKey = errors.errorFields.find((errorItem) => {
              if (errorReg.test(errorItem.name[0])) {
                return true;
              }
              if (currentStep == 2 && validateStepRegex[1].test(errorItem.name[0])) {
                return true;
              }
              return false;
            });
            // 只有相应的步骤出现未填写参数的情况，才会阻断下一步
            if (contentErrorKey) {
              return;
            }
            setCurrentStep(current);
          }
        });
    }
  };

  const handleNext = () => {
    //校验每一步的参数
    const currentFilelds = Object.keys(form.getFieldsValue()).filter((field) => validateStepRegex[currentStep].test(field));
    form
      .validateFields(currentFilelds)
      .then(() => {
        setCurrentStep((prevStep) => {
          return prevStep + 1;
        });
      })
      .catch((errors) => {
        if (errors) {
          console.log(errors);
          const errorReg = validateStepRegex[currentStep];
          const err = {} as any;
          const contentErrorKey = errors.errorFields.find((errorItem) => {
            if (errorReg.test(errorItem.name[0])) {
              return true;
            }
            if (currentStep == 2 && validateStepRegex[1].test(errorItem.name[0])) {
              return true;
            }
            return false;
          });
          // 只有相应的步骤出现未填写参数的情况，才会阻断下一步
          if (contentErrorKey) {
            return;
          }
          setCurrentStep((prevStep) => {
            return prevStep + 1;
          });
        }
      });
  };

  const handlePrev = () => {
    setCurrentStep((prevStep) => {
      return prevStep - 1;
    });
    // form
    //   .validateFields()
    //   .then(() => {
    //     setCurrentStep((prevStep) => {
    //       return prevStep - 1;
    //     });
    //   })
    //   .catch((errors) => {
    //     if (errors) {
    //       console.log(errors);
    //       const errorReg = validateStepRegex[currentStep];
    //       const err = {} as any;
    //       const contentErrorKey = errors.errorFields.find((errorItem) => {
    //         if (errorReg.test(errorItem.name[0])) {
    //           return true;
    //         }
    //         if (currentStep == 2 && validateStepRegex[1].test(errorItem.name[0])) {
    //           return true;
    //         }
    //         return false;
    //       });
    //       // 只有相应的步骤出现未填写参数的情况，才会阻断下一步
    //       if (contentErrorKey) {
    //         return;
    //       }
    //       setCurrentStep((prevStep) => {
    //         return prevStep - 1;
    //       });
    //     }
    //   });
  };

  const processParameters = (values: any) => {
    const params = setStepParams(values);
    if (editUrl) {
      //新增路径对象的id和任务id
      params.fileLogCollectPathList = params.fileLogCollectPathList.map((fileDetail: any, index: number) => {
        const findIndex = hookPathList.findIndex((path) => path == fileDetail.path);
        fileDetail.id = findIndex > -1 ? fileLogCollectDetail[findIndex]?.id : '';
        fileDetail.logCollectTaskId = fileLogCollectDetail[0].logCollectTaskId;
        return fileDetail;
      });
      params.id = state.taskId;
      return editTask(params);
    }
    return addTask(params);
  };

  const addTask = (params) => {
    addCollectTask(params)
      .then((res: any) => {
        if (!res.stateus) {
          Modal.success({
            content: '新增成功！',
            okText: '确认',
            onOk: () => props.history.push({ pathname: '/collect' }),
          });
        } else {
          Modal.error({
            content: `${res.message}`,
            okText: '确认',
            // onCancel: () => {},
          });
        }
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const editTask = (params) => {
    editCollectTask(params)
      .then((res: any) => {
        Modal.success({
          content: '修改成功！',
          okText: '确认',
          onOk: () => props.history.push({ pathname: '/collect' }),
        });
      })
      .catch((err: any) => {
        // console.log(err);
      });
  };

  const handleSubmit = () => {
    form
      .validateFields()
      .then((values) => {
        processParameters(values);
      })
      .catch((errors) => {
        if (errors) {
          const errorReg = validateStepRegex[currentStep];
          // form.resetFields(Object.keys(err));
          const contentErrorKey = errors.errorFields.find((errorItem) => {
            // 产品变更字段命名未变
            if (errorReg.test(errorItem.name[0]) || (currentStep === 2 && validateStepRegex[currentStep + 1]).test(errorItem.name[0])) {
              return true;
            }
            if (currentStep == 2 && validateStepRegex[1].test(errorItem.name[0])) {
              return true;
            }
            return false;
          });
          // 只有相应的步骤出现未填写参数的情况，才会阻断下一步
          if (contentErrorKey) {
            return;
          }
          const stepMap = {
            step1: 0,
            step2: 1,
            step3: 2,
            step4: 2,
          } as any;
          const _currentStep = stepMap[contentErrorKey?.name?.[0].split('_')[0]];
          setCurrentStep(_currentStep);
        }
      });
  };

  const _getDataFormat = () => {
    getDataFormat().then((res) => {
      setDataFormat(res);
    });
  };
  const handleAddTaskSubmit = useDebounce(() => {
    // 新建按钮
    handleSubmit();
  }, 600);

  const handleEditTaskSubmit = useDebounce(() => {
    // 编辑按钮
    handleSubmit();
  }, 600);

  const getCollectDetail = () => {
    setLoading(true);
    getCollectDetails(state.taskId)
      .then((res: any) => {
        setCollectDetail(res);
        assignmentParameters(res);
        setFileLogCollectDetail(res.fileLogCollectPathList);
        setLoading(false);
        setSliceRule(res.logContentSliceRule);
      })
      .catch((err: any) => {
        // console.log(err);
        setLoading(false);
      });
  };

  const setPathObj = (list: string[]) => {
    const arr = list?.map((ele, index) => {
      const pathlog = {} as any;
      pathlog[`step2_catalog_path_${index}`] = ele;
      return pathlog;
    });
    let objs = {};
    arr?.forEach((ele) => {
      objs = { ...objs, ...ele };
    });
    return objs;
  };
  //处理编辑传参 EditStep1
  const setEditOneParams = async (objs: any) => {
    const serviceIdList = objs.services?.map((ele) => ele.id) as number[];
    let step1_collectBusinessTime = [] as unknown;
    let step1_openHistory = false as boolean;
    let step1_historyFilter = '' as string;
    let step1_collectStartBusinessTime = moment();
    setCollectMode(objs.logCollectTaskType);
    setServiceId(serviceIdList[0]);
    //props.setCollectType(objs.logCollectTaskType);
    if (objs.logCollectTaskType === 0) {
      if (objs.oldDataFilterType === 0) {
        step1_collectBusinessTime = [];
      } else if (objs.oldDataFilterType === 1) {
        step1_openHistory = true;
        setOpenHistory(step1_openHistory);
        step1_historyFilter = 'current';
        setHistoryFilter(step1_historyFilter);
        step1_collectStartBusinessTime = moment(new Date(objs.collectStartBusinessTime));
      } else if (objs.oldDataFilterType === 2) {
        step1_openHistory = true;
        setOpenHistory(step1_openHistory);
        step1_historyFilter = 'custom';
        setHistoryFilter(step1_historyFilter);
        step1_collectStartBusinessTime = moment(new Date(objs.collectStartBusinessTime));
      }
    } else {
      step1_collectBusinessTime = [moment(new Date(objs.collectStartBusinessTime)), moment(new Date(objs.collectEndBusinessTime))];
      step1_openHistory = false;
      setOpenHistory(step1_openHistory);
    }
    const step1_needHostFilterRule = objs.hostFilterRuleVO?.needHostFilterRule;
    setHostRange(step1_needHostFilterRule);
    let step1_hostWhiteList = '' as string;
    let step1_hostNames: any = [];
    let step1_filterSQL = '' as string;
    let result: any = [];
    if (step1_needHostFilterRule === 1) {
      if (objs.hostFilterRuleVO?.filterSQL) {
        step1_hostWhiteList = 'sql';
        setHostWhite(step1_hostWhiteList);
        step1_filterSQL = objs.hostFilterRuleVO?.filterSQL;
      } else {
        step1_hostWhiteList = 'hostname';
        setHostWhite(step1_hostWhiteList);
        result = await getHostListbyServiceId(serviceIdList[0]);
        step1_hostNames = objs.hostFilterRuleVO?.hostNames?.map((ele) => Number(ele));
      }
    }

    if (result?.hostList?.length) {
      setHostNameList([...result.hostList]);
      setHostNames(result.hostList);
    }
    return {
      step1_logCollectTaskName: objs.logCollectTaskName, // 日志采集任务名
      step1_serviceId: serviceIdList[0], // 采集应用 number[]
      step1_logCollectTaskType: objs.logCollectTaskType, // 采集模式 // 0:流式 1:时间段
      step1_collectBusinessTime, // collectStartBusinessTime logCollectTaskType = 1 时间段
      step1_openHistory, // 历史数据过滤 默认false 开启true
      step1_historyFilter, // 选择历史数据过滤 // current从当前开始采集 custom自定义采集开始时间
      step1_collectStartBusinessTime, // 默认自定义采集开始时间
      step1_needHostFilterRule, // 0全部 1部分
      step1_hostWhiteList, // 选择主机白名单 // hostname主机名  sqlSQL
      step1_hostNames, //.filter((item: any) => step1_hostNames.includes(item.id)), // 主机名
      step1_filterSQL, // SQL匹配
      // step1_logCollectTaskRemark: objs.logCollectTaskRemark, // 日志采集任务备注
    } as any;
  };
  //处理编辑传参 EditStep2
  const setEditTwoParams = (objs: any) => {
    const logObj = {
      step2_logContentFilterExpression: objs.logContentFilterRuleVO?.logContentFilterExpression, // 日志内容过滤表达式，needLogContentFilter为1时必填
      step2_logContentFilterType: objs.logContentFilterRuleVO?.logContentFilterType, // 日志内容过滤类型 0：包含 1：不包含，needLogContentFilter为1时必填
      step2_needLogContentFilter: objs.logContentFilterRuleVO?.needLogContentFilter, // 是否需要对日志内容进行过滤 0：否 1：是
      // step2_maxBytesPerLogEvent: objs.maxBytesPerLogEvent * 1 > flowUnitList[1].value ? (objs.maxBytesPerLogEvent * 1 / Number(flowUnitList[1].value)) : (objs.maxBytesPerLogEvent * 1 / Number(flowUnitList[0].value)),// 单条日志大小上限
      // Number(values.step2_maxBytesPerLogEvent) * Number(values.step2_flowunit)
      step2_maxBytesPerLogEvent: setLimitUnit(objs.maxBytesPerLogEvent)?.maxBytesPerLogEvent, // 单条日志大小上限
      step3_file_sliceTimestampPrefixStringIndex: objs.logContentSliceRule?.sliceTimestampPrefixStringIndex, // 左起第几个匹配
      step3_file_sliceTimestampPrefixString: objs.logContentSliceRule?.sliceTimestampPrefixString, // 左起第几个匹配
      step3_file_sliceTimestampFormat: objs.logContentSliceRule?.sliceTimestampFormat, // 时间戳格式
      step3_file_sliceRegular: objs.logContentSliceRule?.sliceRegular, // 日志切片规则选1 出现 切片正则
      step2_file_suffixMatchRegular: objs.fileNameSuffixMatchRule?.suffixMatchRegular || '', // 选1出现采集文件后缀匹配
    };
    setLogFilter(logObj.step2_needLogContentFilter);
    // collect
    if (objs.directoryLogCollectPathList?.length) {
      setCollectLogType('catalog');
      // props.setLogType('catalog');
      let cataBase = {} as unknown;
      const cata = objs.directoryLogCollectPathList[0];
      const collectwhites = cata.filterRuleChain?.filter((ele) => ele.key === 0);
      const collectblacks = cata.filterRuleChain?.filter((ele) => ele.key === 1);
      const paths = objs.directoryLogCollectPathList.map((ele) => ele.path);
      setCataPathlist(paths);
      const pathObj = setPathObj(paths);
      cata.logSliceRuleVO?.sliceRegular ? setSlicingRuleLog(1) : setSlicingRuleLog(0);
      cataBase = {
        step2_collectionLogType: 'catalog', // 采集日志类型
        step2_charset: cata.charset, // 编码格式
        step2_catalog_path: cata.path, // 目录路径
        step2_catalog_directoryCollectDepth: cata.directoryCollectDepth, // 采集深度
        step2_catalog_collectwhitelist: collectwhites[0]?.value, // 采集文件白名单
        step2_catalog_collectblacklist: collectblacks[0]?.value, // 采集文件黑名单
        step2_catalog_maxBytesPerLogEvent_: setLimitUnit(cata?.maxBytesPerLogEvent, 2)?.maxBytesPerLogEvent, // 单条日志大小上限
        step2_catalog_flowunit_: setLimitUnit(cata?.maxBytesPerLogEvent, 2)?.flowunit, // 单位 1024 KB
        step2_catalog_sliceType_: cata.logSliceRuleVO?.sliceType, // 日志切片规则 0时间戳 1正则匹配
        step3_catalog_sliceTimestampPrefixStringIndex_: cata.logSliceRuleVO?.sliceTimestampPrefixStringIndex || 0, // 左起第几个匹配
        step3_catalog_sliceTimestampPrefixString_: cata.logSliceRuleVO?.sliceTimestampPrefixString || '', // 切片时间戳前缀字符串
        step3_catalog_sliceTimestampFormat_: cata.logSliceRuleVO?.sliceTimestampFormat || '', // 时间戳格式
        step3_catalog_sliceRegular_: cata.logSliceRuleVO?.sliceRegular || '', // 日志切片规则选1 出现 切片正则
      };
      const cataObj = {};
      Object.assign(cataObj, logObj, pathObj, cataBase);
      return cataObj;
    } else {
      // file
      setCollectLogType('file');
      // props.setLogType('file');
      const file = objs.fileLogCollectPathList;
      const filePathArr = [] as string[];
      const slicingRuleLogArr = [] as number[];
      const suffixLengthArr = [] as number[];
      const fileBase = {
        step2_collectionLogType: 'file', // 采集日志类型
        step2_charset: file[0].charset, // 编码格式
      };

      const fileArr = file?.map((ele, index) => {
        const filelog = {} as any;
        filePathArr.push(ele.path);
        slicingRuleLogArr.push(ele.logSliceRuleVO?.sliceType ? 1 : 0);
        suffixLengthArr.push(ele.fileNameSuffixMatchRuleVO?.suffixMatchType);
        filelog[`step2_file_path_${index}`] = ele.path; // 文件日志路径
        filelog[`step2_file_suffixSeparationCharacter_${index}`] = ele.fileNameSuffixMatchRuleVO?.suffixSeparationCharacter; // 文件名后缀分隔字符
        // filelog[`step2_file_suffixMatchType_${index}`] = ele.fileNameSuffixMatchRuleVO?.suffixMatchType; // 采集文件后缀匹配 0固定格式匹配 1正则匹配
        filelog[`step3_file_suffixLength_${index}`] = ele.fileNameSuffixMatchRuleVO?.suffixLength || ''; // 选0出现采集文件后缀匹配
        filelog[`step3_file_maxBytesPerLogEvent_${index}`] = setLimitUnit(ele?.maxBytesPerLogEvent, 2)?.maxBytesPerLogEvent; // 单条日志大小上限
        filelog[`step3_file_flowunit_${index}`] = setLimitUnit(ele?.maxBytesPerLogEvent, 2)?.flowunit; // 单位 1024 KB
        filelog[`step3_file_sliceType_${index}`] = ele.logSliceRuleVO?.sliceType; // 日志切片规则 0时间戳 1正则匹配
        return filelog;
      });
      setFilePathList(filePathArr);
      setOriginFilePathList(filePathArr);
      setSlicingRuleLogList(slicingRuleLogArr);
      setSuffixfilesList(suffixLengthArr);
      let fileOth = {};
      fileArr?.forEach((ele) => {
        fileOth = { ...fileOth, ...ele };
      });
      const fileObj = {};
      Object.assign(fileObj, logObj, fileBase, fileOth);
      return fileObj;
    }
  };

  const assignmentParameters = async (objs: any) => {
    const obj_step1 = await setEditOneParams(objs); // step1
    const obj_step2 = await setEditTwoParams(objs); // step2
    const obj_step3 = await setEditThreeParams(objs); // step3
    const obj_step4 = await setEditFourParams(objs); // step4
    const params = {} as any;
    Object.assign(params, obj_step1, obj_step2, obj_step3, obj_step4);
    setFieldsValue(params);
  };
  const headerLeftContent = (
    <>
      <NavRouterLink element="采集任务列表" href="/collect" />
      <span>/{`${editUrl ? '编辑' : '新增'}采集任务`}</span>
    </>
  );
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
    if (editUrl) {
      getCollectDetail();
    } else {
      setFieldsValue({ step4_opencollectDelay: 10 });
    }
    _getDataFormat();
    setHostNameList([]); // 清除映射主机列表，进入新增任务时不显示
    setlogFilePathKey(0); // 清除保存日志路径的下标
    return;
  }, []);

  return (
    <Spin spinning={loading}>
      <div className="p-steps-form steps-form">
        <div className="p-steps-form-wrapper">
          <Steps className="fixed-step" current={currentStep} onChange={onStepsChange} labelPlacement={'vertical'}>
            {steps?.map((item) => (
              <Step key={item.title} title={item.title} />
            ))}
          </Steps>
        </div>
        <div className="p-steps-form-content-wrapper">
          <div className="p-steps-form-content">
            {steps?.map((item, itemIdx) => {
              const itemCls = classNames('p-steps-form-content-item', currentStep === itemIdx && 'p-steps-form-content-item-active');
              return (
                <div key={`step-${itemIdx}`} className={itemCls}>
                  {item.content}
                </div>
              );
            })}
            {/* <div>{steps[currentStep].content}</div> */}
          </div>
          <div className={`p-steps-form-action p-steps-edit   p-steps-edit_${currentStep} `}>
            {!editUrl ? (
              <>
                {currentStep > 0 && (
                  <Button className="mr-10" onClick={handlePrev}>
                    上一步
                  </Button>
                )}
                {currentStep < steps.length - 1 && (
                  <Button type="primary" onClick={handleNext}>
                    下一步
                  </Button>
                )}
                {currentStep === steps.length - 1 && (
                  <Button type="primary" onClick={handleAddTaskSubmit}>
                    完成
                  </Button>
                )}
                <div className="edit-btns">
                  <Button>
                    <NavRouterLink element="取消" href="/collect" />
                  </Button>
                </div>
              </>
            ) : (
              <>
                {currentStep > 0 && (
                  <Button className="mr-10" onClick={handlePrev}>
                    上一步
                  </Button>
                )}
                {currentStep < steps.length - 1 && (
                  <Button type="primary" onClick={handleNext}>
                    下一步
                  </Button>
                )}
                {/* {currentStep === steps.length - 1 && (<Button type="primary" onClick={handleAddTaskSubmit}>完成</Button>)} */}
                <div className="edit-btns">
                  <Button type="primary" className="mr-10" onClick={handleEditTaskSubmit}>
                    确认
                  </Button>
                  <Button>
                    <NavRouterLink element="取消" href="/collect" />
                  </Button>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </Spin>
  );
};

export default StepsForm;
