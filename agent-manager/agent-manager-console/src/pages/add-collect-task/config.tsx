// import { ILabelValue } from '../../interface/common';
import {
  IDirectoryLogCollectPath,
  IFileLogCollectPath,
  IKeyValue,
  ILogSliceRule,
  ILogCollectTask,
  ILogCollectTaskDetail,
} from '../../interface/collect';
import { judgeEmpty } from '../../lib/utils';

export const collectLogTypes = [
  {
    label: '文件',
    value: 'file',
  },
  {
    label: '目录',
    value: 'catalog',
  },
];

export const codingFormatTypes = [
  {
    label: 'UTF-8',
    value: 'UTF-8',
  },
  {
    label: 'ASCII',
    value: 'ASCII',
  },
];

export const contentFormItemLayout = {
  labelCol: { span: 12 },
  wrapperCol: { span: 24 },
};

export const clientFormItemLayout = {
  labelCol: { span: 20 },
  wrapperCol: { span: 24 },
};

export const collectLogFormItemLayout = {
  labelCol: { span: 12 },
  wrapperCol: { span: 24 },
};
export const collectSpliceRuleItemLayout = {
  labelCol: { span: 12 },
  wrapperCol: { span: 22 },
};

export const getParams = (obj: any, field: string, index: number) => {
  for (const key in obj) {
    if (key.includes(`step2_file_${field}_${index}`)) {
      return judgeEmpty(obj[key]);
    }
  }
};

export const stepOneParams = (values: any) => {
  let oldDataFilterType = '' as unknown;
  let collectStartBusinessTime = 0 as number;
  const collectEndBusinessTime = 0 as number;
  if (values.step1_logCollectTaskType === 0) {
    if (values.step1_openHistory) {
      if (values.step1_historyFilter === 'current') {
        oldDataFilterType = 1;
        collectStartBusinessTime = new Date().getTime();
      } else {
        oldDataFilterType = 2;
        collectStartBusinessTime = Date.parse(values.step1_collectStartBusinessTime);
      }
    } else {
      oldDataFilterType = 0;
    }
  } else {
    oldDataFilterType = 0;
    // collectStartBusinessTime = Date.parse(values.step1_collectBusinessTime[0]);
    // collectEndBusinessTime = Date.parse(values.step1_collectBusinessTime[1]);
  }
  let hostNames = [] as string[];
  let filterSQL = '' as string;
  if (values.step1_hostWhiteList === 'hostname') {
    hostNames = values.step1_hostNames;
  } else {
    filterSQL = values.step1_filterSQL;
  }
  return {
    oldDataFilterType,
    collectStartBusinessTime,
    collectEndBusinessTime,
    filterSQL,
    hostNames,
  };
};

export const setIndexs = (values: any) => {
  const indexs = [] as number[];
  for (const key in values) {
    if (key.includes('file_path')) {
      const index = Number(key.substring(key.lastIndexOf('_') + 1));
      indexs.push(index);
    }
  }
  return indexs;
};

export const setValues = (values: any, str: string) => {
  const data = [] as string[];
  for (const key in values) {
    if (key.includes(str)) {
      data.push(values[key]);
    }
  }
  return data;
};

export const stepTwoParams = (values: any) => {
  let cataLogList = [] as IFileLogCollectPath[];
  let fileList = [] as IDirectoryLogCollectPath[];

  if (values.step2_collectionLogType === 'catalog') {
    const paths = setValues(values, 'catalog_path')?.filter(Boolean);
    const filterRuleChain = [
      { key: 0, value: values?.step2_catalog_collectwhitelist },
      { key: 1, value: values?.step2_catalog_collectblacklist },
    ] as IKeyValue[];
    const logSliceRuleDTO = {
      sliceType: values.step2_catalog_sliceType_, // 日志内容切片类型 0：时间戳切片 1：正则匹配切片
      sliceTimestampPrefixStringIndex: values.step2_catalog_sliceTimestampPrefixStringIndex_, // 切片时间戳前缀字符串左起第几个，index计数从1开始
      sliceTimestampPrefixString: values.step2_catalog_sliceTimestampPrefixString_, //切片时间戳前缀字符串
      sliceTimestampFormat: values.step2_catalog_sliceTimestampFormat_, //切片 时间戳格式
      sliceRegular: values.step2_catalog_sliceRegular_, // 正则匹配 ———— 切片正则
    } as unknown as ILogSliceRule;
    cataLogList = paths?.map((ele) => {
      return {
        path: ele, // 路径 ———— 待采集路径
        charset: values.step2_charset, // 编码格式 ———— 待采集文件字符集
        maxBytesPerLogEvent: values.step2_catalog_maxBytesPerLogEvent_ * values.step2_catalog_flowunit_, // 单条日志大小上限 KB 1024 MB 1024 * 1024 ———— 单个日志切片最大大小 单位：字节 注：单个日志切片大小超过该值后，采集端将以该值进行截断采集
        logSliceRuleDTO, // 日志切片规则
        // fdOffsetExpirationTimeMs: values.step3_fdOffsetExpirationTimeMs, // 客户端offset（采集位点记录）清理  ———— 待采集文件 offset 有效期 单位：ms 注：待采集文件自最后一次写入时间 ~ 当前时间间隔 > fdOffset时，采集端将删除其维护的该文件对应 offset 信息，如此时，该文件仍存在于待采集目录下，将被重新采集
        id: '', // 采集路径id 添加时不填，更新时必填
        logCollectTaskId: '', // 采集路径关联的日志采集任务id
        directoryCollectDepth: values.step2_catalog_directoryCollectDepth, // 采集深度 ———— 目录采集深度
        filterRuleChain, // 采集文件黑/白名单 ———— 存储有序的文件筛选规则集。pair.key：表示黑/白名单类型，0：白名单，1：黑名单；pair.value：表示过滤规则表达式
      } as unknown as IFileLogCollectPath;
    });
  } else {
    const fileIndexs = setIndexs(values) as number[];
    fileList = fileIndexs?.map((index: number) => {
      const obj = {
        // charset: values.step2_charset, // 编码格式 ———— 待采集文件字符集
        path: getParams(values, 'path', index) || '', // 路径 ———— 待采集路径
        // fdOffsetExpirationTimeMs: values.step3_fdOffsetExpirationTimeMs, // 客户端offset（采集位点记录）清理  ———— 待采集文件 offset 有效期 单位：ms 注：待采集文件自最后一次写入时间 ~ 当前时间间隔 > fdOffset时，采集端将删除其维护的该文件对应 offset 信息，如此时，该文件仍存在于待采集目录下，将被重新采集
        // collectDelayThresholdMs: values.step3_collectDelayThresholdMs * 1 * 60 * 1000, // 采集延迟监控 ———— 该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~ 当前时间间隔
        id: '', // 采集路径id 添加时不填，更新时必填
        logCollectTaskId: '', // 采集路径关联的日志采集任务id
      } as unknown as IDirectoryLogCollectPath;
      return obj;
    });
  }

  return {
    cataLogList,
    fileList,
  };
};

// 处理新增传参
export const setStepParams = (values: any) => {
  const stepOne = stepOneParams(values); // step1
  const stepTwo = stepTwoParams(values); // step2
  const params = {
    id: '', // 日志采集任务id 添加时不填，更新时必填
    // logCollectTaskRemark: values.step1_logCollectTaskRemark, // 日志采集任务备注
    // directoryLogCollectPathList: stepTwo.cataLogList,	// 目录类型采集路径集
    logCollectTaskExecuteTimeoutMs: values.step3_logCollectTaskExecuteTimeoutMs || 0, // 采集完成时间限制 ———— 日志采集任务执行超时时间，注意：该字段仅在日志采集任务类型为类型"按指定时间范围采集"时才存在值
    // >>>>>>>> Step1 <<<<<<<<<
    logCollectTaskName: values.step1_logCollectTaskName, // 日志采集任务名 Yes
    serviceIdList: [values.step1_serviceId], // 采集应用 ———— 采集服务集 Yes
    logCollectTaskType: values.step1_logCollectTaskType || 0, // 采集模式 ———— 采集任务类型 0：常规流式采集 1：按指定时间范围采集 Yes
    oldDataFilterType: stepOne.oldDataFilterType || 0, // 历史数据过滤 ———— 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值
    collectStartBusinessTime: stepOne.collectStartBusinessTime || 0, // 日志采集任务对应采集开始业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 & oldDataFilterTyp = 2 时，该值必填
    collectEndBusinessTime: stepOne.collectEndBusinessTime || 0, // 日志采集任务对应采集结束业务时间 注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填
    hostFilterRuleDTO: {
      needHostFilterRule: values.step1_needHostFilterRule || 0, // 0否-全部 1是-部分 ———— 是否需要主机过滤规则 0：否 1：是
      filterSQL: stepOne.filterSQL, // sql ———— 主机筛选命中sql，白名单
      hostNames: stepOne.hostNames,
    }, // 主机范围 ———— 主机过滤规则
    // >>>>>>>> Step1 <<<<<<<<<

    // >>>>>>>> Step2 <<<<<<<<<

    fileLogCollectPathList: stepTwo.fileList, // 文件类型路径采集配置
    maxBytesPerLogEvent: Number(values.step2_maxBytesPerLogEvent) * Number(values.step2_flowunit) || 2, //step2_maxBytesPerLogEvent 单条日志大小上限 ———— 单个日志切片最大大小 单位：字节 注：单个日志切片大小超过该值后，采集端将以该值进行截断采集
    logContentFilterLogicDTO: {
      logContentFilterExpression: values.step2_logContentFilterExpression, // 日志内容过滤表达式，needLogContentFilter为1时必填
      logContentFilterType: values.step2_logContentFilterType, // 日志内容过滤类型 0：包含 1：不包含，needLogContentFilter为1时必填
      needLogContentFilter: values.step2_needLogContentFilter || 0, // 是否需要对日志内容进行过滤 0：否 1：是
    }, // 日志过滤内容规则
    fileNameSuffixMatchRuleDTO: {
      // suffixSeparationCharacter: getParams(values, 'suffixSeparationCharacter', index) || '', // 文件名后缀分隔字符
      // suffixMatchType: getParams(values, 'suffixMatchType', index), // 文件名后缀匹配类型 0：长度 1：正则
      // suffixLength: getParams(values, 'suffixLength', index) || '', // 文件名后缀长度 suffixMatchType为0时必填
      suffixMatchRegular: Array.isArray(values.step2_file_suffixMatchRegular)
        ? values.step2_file_suffixMatchRegular[0]
        : values.step2_file_suffixMatchRegular || '', //文件名后缀长度 suffixMatchType为1时必填
    }, // 采集文件名后缀匹配规则
    logSliceRuleDTO: {
      // sliceType: getParams(values, 'sliceType', index), // 日志内容切片类型 0：时间戳切片 1：正则匹配切片
      sliceTimestampPrefixStringIndex: values.step3_file_sliceTimestampPrefixStringIndex, // 切片时间戳前缀字符串左起第几个，index计数从1开始
      sliceTimestampPrefixString: values.step3_file_sliceTimestampPrefixString || '', //切片时间戳前缀字符串
      sliceTimestampFormat: values.step3_file_sliceTimestampFormat || '', //切片 时间戳格式
      sliceRegular: values.step3_file_sliceRegular || '', // 正则匹配 ———— 切片正则
    }, // 日志切片规则
    // >>>>>>>> Step2 <<<<<<<<<

    // >>>>>>>> Step3 <<<<<<<<<
    kafkaClusterId: values.step4_kafkaClusterId, // kafka集群 ———— 采集任务采集的日志需要发往的对应Kafka集群信息id
    kafkaProducerConfiguration: values.step4_productionSide || '', // kafka生产端属性
    sendTopic: values.step4_sendTopic, // Topic ———— 采集任务采集的日志需要发往的topic名 (values.step3_collectDelayThresholdMs ?? 0) * 60 * 1000
    opencollectDelay: values.step4_opencollectDelay, // 是否开启采集延时监控
    collectDelayThresholdMs: values.step4_opencollectDelay ? values?.step4_collectDelayThresholdMs * 60 * 1000 || 10 * 60 * 1000 : 0, //采集延迟监控 ———— 该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~当前时间间隔
    limitPriority: values.step4_limitPriority, // 采集任务限流保障优先级 0：高 1：中 2：低
    advancedConfigurationJsonString: values.step4_advancedConfigurationJsonString, //高级配置信息 ———— 采集任务高级配置项集，为json形式字符串
    // >>>>>>>> Step3 <<<<<<<<<
  } as unknown as ILogCollectTask;
  return params;
};

//处理编辑传参 EditStep3
export const setEditThreeParams = (objs: ILogCollectTaskDetail) => {
  let step3_fdOffsetExpirationTimeMs = '' as unknown;
  let step4_collectDelayThresholdMs = '' as unknown;
  let step4_opencollectDelay = false;
  if (objs.directoryLogCollectPathList?.length) {
    step3_fdOffsetExpirationTimeMs = objs.directoryLogCollectPathList[0]?.fdOffsetExpirationTimeMs;
  } else {
    step3_fdOffsetExpirationTimeMs = objs.fileLogCollectPathList[0]?.fdOffsetExpirationTimeMs;
    step4_opencollectDelay = objs.collectDelayThresholdMs > 0 ? true : false;
    step4_collectDelayThresholdMs = (objs.collectDelayThresholdMs * 1) / 60 / 1000;
  }
  return {
    // step3_fdOffsetExpirationTimeMs, // 客户端offset（采集位点记录）清理
    step4_collectDelayThresholdMs, // 采集延迟监控
    step4_opencollectDelay, // 是否开启采集延迟监控
    step4_productionSide: objs.kafkaProducerConfiguration, // kafka生产端属性
    step3_logCollectTaskExecuteTimeoutMs: objs.logCollectTaskExecuteTimeoutMs || 0, // 采集完成时间限制
    step4_limitPriority: objs.limitPriority, // 采集任务限流保障优先级 0：高 1：中 2：低
  } as any;
};
//处理编辑传参 EditStep4
export const setEditFourParams = (objs: ILogCollectTaskDetail) => {
  return {
    step4_kafkaClusterId: objs.receiver.id, // Kafka集群
    step4_sendTopic: objs.sendTopic, // 选择Topic
    step4_advancedConfigurationJsonString: objs.advancedConfigurationJsonString, // 配置信息
  } as any;
};

export const setList = (type: string, values: any) => {
  const indexs = [] as number[];
  for (const key in values) {
    if (key.includes(`${type}`)) {
      const index = Number(key.substring(key.lastIndexOf('_') + 1));
      indexs.push(index);
    }
  }
  return indexs;
};
