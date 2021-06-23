
import * as React from 'react';
import { Spin, Tabs, Descriptions, Divider } from 'antd';
import { ILogCollectTaskDetail, IKeyValue, IDirectoryLogCollectPath } from '../../interface/collect';
import { timeFormat } from '../../constants/time';
import { collectModeMap, oldDataFilterMap, hostRangeMap, logSliceRuleMap, suffixMatchMap, logFilter, logFilterType, limitType } from '../../constants/common';
import { renderTooltip } from '../../component/CustomComponent';
import { setLimitUnit } from '../../lib/utils';
import moment from 'moment';
import './index.less';

const { TabPane } = Tabs;
const { Item } = Descriptions;

interface IAcquisitionConfiguration {
  loading: boolean;
  detail: ILogCollectTaskDetail;
}

export class AcquisitionConfiguration extends React.Component<IAcquisitionConfiguration> {

  public renderCollectObj = (detail: ILogCollectTaskDetail) => {
    return (<>
      <Descriptions className='mt-10'>
        <Item label="采集任务名">{renderTooltip(detail?.logCollectTaskName)}</Item>
        <Item label="采集应用">{renderTooltip(detail?.services[0]?.servicename)}</Item>
        <Item label="采集模式">{collectModeMap[detail?.logCollectTaskType]}</Item>
        {detail?.logCollectTaskType === 0 &&
          <Item label="历史数据过滤">{oldDataFilterMap[detail?.oldDataFilterType]}</Item>}
        {(detail?.logCollectTaskType === 0 && detail?.oldDataFilterType !== 0) || detail?.logCollectTaskType !== 0 ?
          <Item label="采集开始时间">{moment(detail?.collectStartBusinessTime).format(timeFormat)}</Item> : ''}
        {detail?.logCollectTaskType !== 0 &&
          <Item label="采集结束时间">{moment(detail?.collectEndBusinessTime).format(timeFormat)}</Item>}
        <Item label="主机范围">{hostRangeMap[detail?.hostFilterRuleVO?.needHostFilterRule]}</Item>
        {detail?.hostFilterRuleVO?.needHostFilterRule === 1 && <>
          {detail?.hostFilterRuleVO?.filterSQL ? <>
            <Item label="主机白名单">SQL匹配</Item>
            <Item label="SQL匹配">{detail?.hostFilterRuleVO?.filterSQL}</Item>
          </> : <>
              <Item label="主机白名单">主机名</Item>
              <Item label="主机名">{detail?.hostFilterRuleVO?.hostNames}</Item>
            </>}
        </>}
      </Descriptions>
      {/* <Descriptions column={1}>
        <Item label="采集任务描述">{detail?.logCollectTaskRemark}</Item>
      </Descriptions> */}
    </>)
  }

  public renderCollectLog = (detail: ILogCollectTaskDetail) => {
    const cataFile = !!detail?.directoryLogCollectPathList?.length;
    const cataLog = [] as IDirectoryLogCollectPath[];
    cataLog?.push(detail?.directoryLogCollectPathList[0]);
    const log = cataFile ? cataLog : detail?.fileLogCollectPathList as any;
    const paths = detail?.directoryLogCollectPathList?.map(ele => ele.path);
    return (<>
      {log?.map((ele: any, index: number) => {
        const whites = ele?.filterRuleChain?.filter((ele: IKeyValue) => ele?.key === 0)?.map((ele: IKeyValue) => ele?.value) || [];
        const blacks = ele?.filterRuleChain?.filter((ele: IKeyValue) => ele?.key === 1)?.map((ele: IKeyValue) => ele?.value) || [];
        const unitText = setLimitUnit(ele?.maxBytesPerLogEvent, 2)?.flowunit === 1024 ? 'KB' : 'MB';
        return (<div key={index} className='mt-10'>
          {index > 0 && <Divider />}
          <Descriptions column={2}>
            <Item label="采集日志类型">{cataFile ? '目录型' : '文件型'}</Item>
            {/* <Item label="编码格式">{ele?.charset}</Item> */}
            <Item label="日志内容过滤">{logFilter[detail?.logContentFilterRuleVO?.needLogContentFilter]}</Item>
            {detail?.logContentFilterRuleVO?.needLogContentFilter === 1 && <>
              <Item label="过滤类型">{logFilterType[detail?.logContentFilterRuleVO?.logContentFilterType]}</Item>
              <Item label="过滤规则">{detail?.logContentFilterRuleVO?.logContentFilterExpression}</Item>
            </>}
            {!cataFile && <Item label="目录路径">{renderTooltip(ele?.path, 60)}</Item>}
            {cataFile ? <>
              <Item label="采集深度">{ele?.directoryCollectDepth}</Item>
              <Item label="采集文件白名单">{renderTooltip(whites[0], 60)}</Item>
              <Item label="采集文件黑名单">{renderTooltip(blacks[0], 60)}</Item>
            </> : <>
                {/* <Item label="文件名后缀分隔字符">{ele?.fileNameSuffixMatchRuleVO?.suffixSeparationCharacter}</Item> */}
                {/* <Item label="采集文件后缀匹配">{suffixMatchMap[ele?.fileNameSuffixMatchRuleVO?.suffixMatchType]}</Item> */}
                {ele?.fileNameSuffixMatchRuleVO?.suffixMatchType === 0 ?
                  <Item label="固定格式匹配">{ele?.fileNameSuffixMatchRuleVO?.suffixLength}</Item>
                  : <Item label="采集文件后缀匹配样式">{detail?.fileNameSuffixMatchRule?.suffixMatchRegular}</Item>}
              </>}
            <Item label="单机日志大小上限">{setLimitUnit(ele?.maxBytesPerLogEvent, 2)?.maxBytesPerLogEvent}{unitText}</Item>
            {/* <Item label="日志切片规则">{logSliceRuleMap[ele?.logSliceRuleVO?.sliceType]}</Item> */}
            {detail?.logContentSliceRule?.sliceType !== 0 ? <>
              <Item label="左起第几个匹配">{detail?.logContentSliceRule?.sliceTimestampPrefixStringIndex}</Item>
              {detail?.logContentSliceRule?.sliceTimestampPrefixString
                && <Item label="切片时间戳前缀字符串">{detail?.logContentSliceRule?.sliceTimestampPrefixString}</Item>}
              <Item label="时间戳格式">{detail?.logContentSliceRule?.sliceTimestampFormat}</Item>
            </> :
              <Item label="切片正则">{ele?.logContentSliceRule?.sliceRegular}</Item>}
          </Descriptions>
          {cataFile && <Descriptions column={1}>
            <Item label="目录路径">{paths?.join(',')}</Item>
          </Descriptions>}
        </div>)
      })}
    </>);
  }

  public renderClientMonitor = (detail: ILogCollectTaskDetail) => {
    const judge = !!detail?.directoryLogCollectPathList?.length;
    const fdOffset = judge ? detail?.directoryLogCollectPathList[0]?.fdOffsetExpirationTimeMs : detail?.fileLogCollectPathList[0]?.fdOffsetExpirationTimeMs;
    const collectDelay = (detail?.fileLogCollectPathList[0]?.collectDelayThresholdMs || 0) / 60 / 1000;
    return (<>
      <Descriptions column={1} className='mt-10'>
        {/* <Item label="客户端offset（采集位点记录）清理">超过 {fdOffset} 天无数据写入，则在Agent客户端删除该文件的offset</Item> */}
        {!judge && <Item label="采集延迟监控">该任务下Agent客户端延迟超过 {collectDelay} 分钟，则视为异常 注：仅支持对按业务时间顺序进行输出的日志进行延迟监控</Item>}
        {/* {detail?.logCollectTaskType === 0 && <Item label="采集完成时间限制">该任务超过 {detail?.logCollectTaskExecuteTimeoutMs} 分钟未完成，则视为异常</Item>} */}
        <Item label="任务保障等级">{limitType[detail?.limitPriority]}</Item>
      </Descriptions>
    </>)
  }

  public renderReceiveEnd = (detail: ILogCollectTaskDetail) => {
    const collectDelay = (detail?.collectDelayThresholdMs || 0) / 60 / 1000;
    return (<>
      <Descriptions column={1} className='mt-10'>
        <Item label="Kafka集群">{renderTooltip(detail?.receiver?.kafkaClusterName, 60)}</Item>
        {
          detail.kafkaProducerConfiguration && <Item label="生产端属性">{detail.kafkaProducerConfiguration}</Item>
        }
        <Item label="Topic">{renderTooltip(detail?.sendTopic, 60)}</Item>
        <Item label="采集延迟监控">该任务下Agent客户端延迟超过 {collectDelay} 分钟，则视为异常 注：仅支持对按业务时间顺序进行输出的日志进行延迟监控</Item>
        <Item label="任务保障等级">{limitType[detail?.limitPriority]}</Item>
      </Descriptions>
    </>)
  }

  public renderAdvancedConfig = (detail: ILogCollectTaskDetail) => {
    return (<>
      <Descriptions className='mt-10'>
        <Item label="高级配置信息">{detail?.advancedConfigurationJsonString}</Item>
      </Descriptions>
    </>)
  }

  public render() {
    const { detail, loading } = this.props;
    return (
      <Spin spinning={loading}>{Object.keys(detail).length !== 0 &&
        <div>
          <Tabs type='card' defaultActiveKey="object">
            <TabPane tab="采集对象配置" key="object">
              {this.renderCollectObj(detail)}
            </TabPane>
            <TabPane tab="采集日志配置" key="journal">
              {this.renderCollectLog(detail)}
            </TabPane>
            {/* <TabPane tab="客户端清理与自监控" key="client">
              {this.renderClientMonitor(detail)}
            </TabPane> */}
            <TabPane tab="接收端配置与监控" key="receive">
              {this.renderReceiveEnd(detail)}
            </TabPane>
            <TabPane tab="高级配置" key="senior">
              {this.renderAdvancedConfig(detail)}
            </TabPane>
          </Tabs>
        </div>
      }</Spin>
    );
  }
}