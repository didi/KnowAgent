
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
import { getAgentMetrics, getCollectMetrics, getAgentLineData, getAgentPieData } from '../../api/agent';
import { getCollectLineData, getCollectPieData } from '../../api/collect';
import { dealMetricPanel } from '../common-curve/constants';
import { valMoments } from '../../constants/time';
import { COLLECT_CONFIG } from './../collect-task/chartConfig';
import { AGENT_CONFIG } from './../agent-management/chartConfig';
import moment from 'moment';
import { cloneDeep, forEach } from 'lodash';
import './index.less';
import mockdata from './mock';

interface IAgentOperationIndex {
  id: number
}
interface IState {
  metrics: any[];
  apiData: any;
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
    apiData: {},
  }

  public getCurves = (metricPanelList: IMetricPanels[], metricPanelGroupName: string, apiData: any) => {
    const metricPanels = dealMetricPanel(metricPanelList, metricPanelGroupName, apiData);
    return metricPanels.map((o, index) => {
      console.log(o, index)
      return <CommonCurve key={o.title + index} title={o.title} options={o.metricOptions} eachHost={o.eachHost} selfHide={o.selfHide} {...this.props} />;
    });
  }

  public getMetrics = (timeRange: moment.Moment[]) => {
    const startTime = timeRange[0]?.valueOf();
    const endTime = timeRange[1]?.valueOf();
    this.props.setRefresh(true);
    this.judgeUrl ? this.getCollect(startTime, endTime) : this.getAgent(startTime, endTime);
  }

  public setCollectResData = (data: IRdAgentMetrics[]) => {
    const { id, logCollectPathId, hostName, timeRange, setChartMetrics, setRefresh } = this.props;
    data.forEach(ele => {
      ele.groupHide = false;
      ele.metricPanelList.forEach(v => {
        const params = {
          taskId: id,
          logCollectPathId,
          hostName,
          startTime: timeRange[0]?.valueOf(),
          endTime: timeRange[1]?.valueOf(),
          eachHost: v.eachHost
        }
        v.selfHide = false;
        if (this.judgeUrl && !v.isPie) v.eachHost = true;
        if (v.isPie) {
          getCollectPieData(v.api, { taskId: this.props.id, logCollectPathId: this.props.logCollectPathId })
            .then((res: any) => {
              //todo 返回类型看接口在写
              // console.log(v)
              this.setState((state: IState) => {
                const cloneApiData = cloneDeep(state.apiData);
                cloneApiData[v.api] = [
                  {
                    "metricName": v.title,
                    "metricPointList": [
                      {"value":1,"name":"不存在心跳主机"},
                      {"value":2,"name":"存在心跳主机"},
                    ]
                  }
                ]
                return {
                  apiData: cloneApiData
                }
              })
            })
          setRefresh(false);
          return;
        }
        // getColalectPieData(v.api, params).then((res: IRdAgentMetrics[]) => {
          this.setState((state: IState) => {
            const cloneApiData = cloneDeep(state.apiData);
            cloneApiData[v.api] = mockdata;
            return {
              apiData: cloneApiData
            }
          })
          console.log(v)
          setRefresh(false);
        // }).catch((err: any) => {
        //   this.props.setRefresh(false);
        // });
        // getCollectLineData(v.api, params).then(function(res: any) {
        //   //todo 返回类型看接口在写
        //   v.metricList = [
        //     {"metricPointList":[
        //       {"timestamp":1626258240000,"value":0.1},
        //     ]},
        //   ]
        //   setChartMetrics(data);
        //   setRefresh(false);
        // }).catch((err: any) => {
        //   this.props.setRefresh(false);
        // });
      })
    });
    return data;
  }

  public getAgent = (startTime: number, endTime: number) => {
    // getAgentMetrics(this.props.id, startTime, endTime).then((res: IRdAgentMetrics[]) => {
    //   const data = this.setResData(res);
    //   this.setState({ metrics: data });
    //   this.props.setChartMetrics(data);
    //   this.props.setRefresh(false);
    // }).catch((err: any) => {
    //   this.props.setRefresh(false);
    // });
    const metrics = this.state.metrics;
    console.log(metrics)
    const data = this.setCollectResData(metrics);
    this.setState({ metrics });
    this.props.setChartMetrics(data);
  }

  public getCollect = (startTime: number, endTime: number) => {
    // this.setState({ metrics: COLLECT_CONFIG });
    const metrics = this.state.metrics;
    console.log(metrics)
    const data = this.setCollectResData(metrics);
    this.setState({ metrics });
    this.props.setChartMetrics(data);
    getCollectMetrics(this.props.id, startTime, endTime).then((res: IRdAgentMetrics[]) => {
      
      
      
    }).catch((err: any) => {
      this.props.setRefresh(false);
    });
  }


  public componentDidMount() {
    this.props.setTimeRange([moment().subtract(60, 'minute'), moment()]);
    this.getMetrics([moment().subtract(60, 'minute'), moment()]);
  }

  public render() {
    const { metrics, apiData } = this.state;
    console.log(metrics, this.props.chartMetrics, apiData)
    return (
      <>
        <div className='tc'><Spin spinning={this.props.loading}></Spin></div>
        {!this.props.loading ? <DataCollapseSelect metrics={metrics} {...this.props} /> : null}
        <DataCurveFilter agentid={this.props.id} judgeUrl={this.judgeUrl} refresh={this.getMetrics} {...this.props} />
        <div>
          {this.props.chartMetrics?.length > 0 && this.props.chartMetrics.map((ele: IRdAgentMetrics, index: number) => {
            return <ExpandCard key={index} groupHide={ele.groupHide} title={ele.metricPanelGroupName} charts={this.getCurves(ele.metricPanelList, ele.metricPanelGroupName, apiData)} />;
          })}
        </div>
      </>
    );
  }
};
