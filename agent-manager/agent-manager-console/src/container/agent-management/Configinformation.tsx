
import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { Spin, Descriptions } from 'antd';
import { IRdAgentMetrics } from '../../interface/agent';
import moment from 'moment';
import './index.less';
import { getAgentDetails } from '../../api/agent';

interface IAgentOperationIndex {
  id: number
}
const { Item } = Descriptions;

const mapStateToProps = (state: any) => ({
  timeRange: state.agent.timeRange,
  chartMetrics: state.echarts.chartMetrics,
  loading: state.echarts.loading,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setRefresh: (loading: boolean) => dispatch(actions.setRefresh(loading)),
  setChartMetrics: (chartMetrics: IRdAgentMetrics[]) => dispatch(actions.setChartMetrics(chartMetrics)),
  setTimeRange: (timeRange: moment.Moment[]) => dispatch(actions.setTimeRange(timeRange)),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
// @connect(mapStateToProps, mapDispatchToProps)
export const AgentConfigInfo = (props: any) => {
  const { hostDetail } = props
  const [agentDetail, setAgentDetail] = React.useState<any>({})
  console.log(props, 'props')
  React.useEffect(() => {
    try {
      getAgentDetails(hostDetail?.agentId).then(res => {
        setAgentDetail(res)
        console.log(res)
      })
    } catch (error) {
      console.log(error)
    }
  }, [])
  return <div className='agentConfigInfo'>
    <Descriptions column={2} title={<h3 style={{ color: '#7e7e7e', padding: '10px 0', borderBottom: '2px solid #7e7e7e' }}>基础配置信息</h3>}>
      <Descriptions.Item className='agentConfigInfoLeft' label="Agent版本号">{agentDetail.version || '-'}</Descriptions.Item>
      <Descriptions.Item label="版本描述">{agentDetail.described || '-'}</Descriptions.Item>
      <Descriptions.Item className='agentConfigInfoLeft' label="CPU核数上限">{agentDetail.cpuLimitThreshold || '-'}</Descriptions.Item>
    </Descriptions>
    <Descriptions column={2} title={<h3 style={{ color: '#7e7e7e', padding: '30px 0 10px', borderBottom: '2px solid #7e7e7e' }}>高级配置信息</h3>}>
      <Descriptions.Item className='agentConfigInfoLeft' label="指标流接收集群">{agentDetail.metricsSendReceiverId || '-'}</Descriptions.Item>
      <Descriptions.Item label="错误日志接收集群">{agentDetail.errorLogsSendReceiverId || '-'}</Descriptions.Item>
      <Descriptions.Item className='agentConfigInfoLeft' label="指标流接收Topic">{agentDetail.cpuLimitThreshold || '-'}</Descriptions.Item>
      <Descriptions.Item label="错误日志接收Topic">{agentDetail.errorLogsSendTopic || '-'}</Descriptions.Item>
      <Descriptions.Item className='agentConfigInfoLeft' label="指标流生产端属性">{agentDetail.metricsProducerConfiguration || '-'}</Descriptions.Item>
      <Descriptions.Item label="错误日志生产端属性">{agentDetail.errorLogsProducerConfiguration || '-'}</Descriptions.Item>
      <Descriptions.Item className='agentConfigInfoLeft' label="配置信息"><pre>{agentDetail.advancedConfigurationJsonString || '-'}</pre></Descriptions.Item>
    </Descriptions>
  </div>
}