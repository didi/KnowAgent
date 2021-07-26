
import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { Spin } from 'antd';
import { DataCollapseSelect } from '../collapse-select';
import { DataCurveFilter } from '../../component/time-panel';
import { ExpandCard } from '../../component/expand-card';
import { CommonCurve } from '../common-curve';
import { IRdAgentMetrics, IMetricPanels, IMetricOptions } from '../../interface/agent';
import { valMoments } from '../../constants/time';
import { COLLECT_CONFIG } from './../collect-task/chartConfig';
import { AGENT_CONFIG } from './../agent-management/chartConfig';
import moment from 'moment';
import { cloneDeep } from 'lodash';
import './index.less';

interface IAgentOperationIndex {
  id: number
}
interface IState {
  metrics: any[];
}

const mapStateToProps = (state: any) => ({
  timeRange: state.agent.timeRange,
  chartMetrics: state.echarts.chartMetrics,
  loading: state.echarts.loading,
  logCollectPathId: state.collect.logCollectPathId,
  hostName: state.collect.hostName,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setRefresh: (loading: boolean) => dispatch(actions.setRefresh(loading)),
  setChartMetrics: (chartMetrics: IRdAgentMetrics[]) => dispatch(actions.setChartMetrics(chartMetrics)),
  setTimeRange: (timeRange: moment.Moment[]) => dispatch(actions.setTimeRange(timeRange)),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class AgentOperationIndex extends React.Component<Props & IAgentOperationIndex | any> {
  public judgeUrl = window.location.pathname.includes('collect');
  public state: IState = {
    metrics: this.judgeUrl ? cloneDeep(COLLECT_CONFIG) : cloneDeep(AGENT_CONFIG),
  }

  public getCurves = (metricPanelList: IMetricPanels[]) => {
    // const metricPanels = dealMetricPanel(metricPanelList, metricPanelGroupName, this.judgeUrl);
    return metricPanelList.map((ele, index) => {
      const option = cloneDeep(ele);
      return <CommonCurve taskid={this.props.id} judgeUrl={this.judgeUrl} key={option.title + index} title={option.title} options={option} selfHide={option.selfHide} />;
    });
  }

  public setResData = (data: IRdAgentMetrics[]) => {
    data.forEach(ele => {
      ele.groupHide = false;
      ele.metricPanelList.forEach(v => {
        v.selfHide = false;
      })
    });
    return data;
  }

  public getConfig = () => {
    const metrics = this.state.metrics;
    const data = this.setResData(metrics);
    this.setState({ metrics: data });
    this.props.setChartMetrics(data);
    this.props.setRefresh(false);
  }


  public componentDidMount() {
    this.props.setTimeRange([moment().subtract(60, 'minute'), moment()]);
    this.getConfig()
  }

  public componentWillUnmount() {
    this.props.setChartMetrics([]);
  }

  public render() {
    const { metrics } = this.state;
    return (
      <>
        <div className='tc'><Spin spinning={this.props.loading}></Spin></div>
        {!this.props.loading ? <DataCollapseSelect metrics={metrics} {...this.props} /> : null}
        <DataCurveFilter agentid={this.props.id} judgeUrl={this.judgeUrl} {...this.props} />
        <div>
          {this.props.chartMetrics?.length > 0 && this.props.chartMetrics.map((ele: IRdAgentMetrics, index: number) => {
            return <ExpandCard key={index} groupHide={ele.groupHide} title={ele.metricPanelGroupName} charts={this.getCurves(ele.metricPanelList)} />;
          })}
        </div>
      </>
    );
  }
};
