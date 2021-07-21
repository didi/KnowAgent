
import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { Spin, Descriptions } from 'antd';
import { IRdAgentMetrics } from '../../interface/agent';
import moment from 'moment';
import './index.less';

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

  console.log(props, 'props')
  React.useEffect(() => {
    console.log('我执行了')
  })
  return <div>
    <Descriptions column={2} className='aaasss' title="基础配置信息">
      <Descriptions.Item label="Agent版本号">{'1.1.1'}</Descriptions.Item>
      <Descriptions.Item label="UserName">Zhou Maomao</Descriptions.Item>
      <Descriptions.Item label="Telephone">1810000000</Descriptions.Item>
      <Descriptions.Item label="Live">Hangzhou, Zhejiang</Descriptions.Item>
      <Descriptions.Item label="Remark">empty</Descriptions.Item>
      <Descriptions.Item label="Address">
        No. 18, Wantang Road, Xihu District, Hangzhou, Zhejiang, China
    </Descriptions.Item>
    </Descriptions>
  </div>
}

// export default connect(mapStateToProps, mapDispatchToProps)
// export class AgentConfigInfo extends React.Component<Props & IAgentOperationIndex | any> {
//   public state = {
//     metrics: [],
//   }

//   public getCurves = (metricPanelList: IMetricPanels[], metricPanelGroupName: string) => {
//     const metricPanels = dealMetricPanel(metricPanelList, metricPanelGroupName);
//     return metricPanels.map((o, index) => {
//       return <CommonCurve key={o.title + index} title={o.title} options={o.metricOptions} selfHide={o.selfHide} {...this.props} />;
//     });
//   }

//   public getMetrics = (timeRange: moment.Moment[]) => {
//     const startTime = timeRange[0]?.valueOf();
//     const endTime = timeRange[1]?.valueOf();
//     this.props.setRefresh(true);
//     const judgeUrl = window.location.pathname.includes('collect');
//     judgeUrl ? this.getCollect(startTime, endTime) : this.getAgent(startTime, endTime);
//   }

//   public setResData = (data: IRdAgentMetrics[]) => {
//     data.forEach(ele => {
//       ele.groupHide = false;
//       ele.metricPanelList.forEach(v => {
//         v.selfHide = false;
//       })
//     });
//     return data;
//   }

//   public getAgent = (startTime: number, endTime: number) => {
//     getAgentMetrics(this.props.id, startTime, endTime).then((res: IRdAgentMetrics[]) => {
//       const data = this.setResData(res);
//       this.setState({ metrics: data });
//       this.props.setChartMetrics(data);
//       this.props.setRefresh(false);
//     }).catch((err: any) => {
//       this.props.setRefresh(false);
//     });
//   }

//   public getCollect = (startTime: number, endTime: number) => {
//     getCollectMetrics(this.props.id, startTime, endTime).then((res: IRdAgentMetrics[]) => {
//       const data = this.setResData(res);
//       this.setState({ metrics: data });
//       this.props.setChartMetrics(data);
//       this.props.setRefresh(false);
//     }).catch((err: any) => {
//       this.props.setRefresh(false);
//     });
//   }


//   public componentDidMount() {
//     this.props.setTimeRange([moment().subtract(10, 'minute'), moment()]);
//     this.getMetrics([moment().subtract(10, 'minute'), moment()]);
//   }

//   public render() {
//     return (
//       <>
//         <div className='tc'><Spin spinning={this.props.loading}></Spin></div>
//         {!this.props.loading ? <DataCollapseSelect metrics={this.state.metrics} {...this.props} /> : null}
//         <DataCurveFilter refresh={this.getMetrics} {...this.props} />
//         <div>
//           {this.props.chartMetrics?.length > 0 && this.props.chartMetrics.map((ele: IRdAgentMetrics, index: number) => {
//             return <ExpandCard key={index} groupHide={ele.groupHide} title={ele.metricPanelGroupName} charts={this.getCurves(ele.metricPanelList, ele.metricPanelGroupName)} />;
//           })}
//         </div>
//       </>
//     );
//   }
// };
