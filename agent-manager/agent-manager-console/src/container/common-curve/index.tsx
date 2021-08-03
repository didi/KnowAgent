import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { EChartOption } from 'echarts';
import { getHeight } from './constants';
import { Spin, Icon, Button } from 'antd';
import LineChart, { hasData } from '../../component/echarts';
import { IMetricPanels } from '../../interface/agent';
import { getCollectLineData, getCollectPieData } from '../../api/collect';
import { getAgentMetrics, getCollectMetrics, getAgentLineData, getAgentPieData } from '../../api/agent';
import { newdealMetricPanel, dealMetricPanel } from './constants';
import './index.less';

export interface ICommonCurveProps {
  title: string;
  options: IMetricPanels;
  selfHide: boolean;
  judgeUrl: boolean;
  taskid: number;
}

const mapStateToProps = (state: any) => ({
  loading: state.echarts.loading,
  timeRange: state.agent.timeRange,
  logCollectPathId: state.collect.logCollectPathId,
  hostName: state.collect.hostName,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  showContent: (content: JSX.Element) => dispatch(actions.showContent(content)),
  closeContent: () => dispatch(actions.closeContent()),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class CommonCurve extends React.Component<ICommonCurveProps & Props> {
  
  public state = {
    data: {} as any,
    eachHost: false,
    loading: false,
    show: false,
  }
  public getLoading = () => {
    return this.state.loading;
  }

  public expandChange = () => {
    const curveOption = this.getCurveData();
    const loading = this.getLoading();
    const options = Object.assign({}, curveOption, {
      grid: {
        ...curveOption.grid,
        height: Math.floor(document.getElementsByTagName('body')[0].clientHeight * 0.6),
        right: '2%',
      },
    });
    this.props.showContent(this.renderCurve(options, loading, true, Math.random()));
  }

  public renderOpBtns = (options: EChartOption, expand?: boolean) => {
    const data = hasData(options);
    return (
      <div className="charts-op" key="op">
        {data ? this.renderExpand(expand) : null}
      </div>
    );
  }

  public renderExpand = (expand?: boolean) => {
    if (expand) return <Icon type="close" onClick={() => {
      this.props.closeContent();
      this.setState({ show: false }) 
    }} key="close" />;
    return <Icon type="fullscreen" className="ml-17" onClick={() => {
      this.expandChange();
      this.setState({ show: true });
    }} key="full-screen" />;
  }

  public eachHostChange = () => {
    this.setState({
      eachHost: !this.state.eachHost
    }, () => this.getData())
  }

  public renderEachHost = () => {
    if (this.props.judgeUrl && !this.props.options.isPie) {
      if(this.state.eachHost) {
        return <Button className="common-chart-wrapper-eachhost" onClick={this.eachHostChange}>返回</Button>
      }
      return <Button className="common-chart-wrapper-eachhost" onClick={this.eachHostChange}>查看各主机</Button>
    }
    return '';
  }

  public renderTitle = () => {
    return (
      <div className="charts-title" key="title">{this.props.title}</div>
    );
  }

  public getCurveData = () => {
    return this.state.data;
  }

  public renderOthers = () => null as unknown as JSX.Element;

  public renderNoData = (height?: number) => {
    const style = { height: `${height}px`, lineHeight: `${height}px` };
    return <div className="no-data-info" style={{ ...style }} key="noData">暂无数据</div>;
  }

  public renderLoading = (height?: number) => {
    const style = { height: `${height}px`, lineHeight: `${height}px` };
    return <div className="no-data-info" style={{ ...style }} key="loading"><Spin /></div>;
  }

  public renderEchart = (options: EChartOption, loading: boolean, expand?: boolean) => {
    const width = expand ? undefined : 350;
    let height = getHeight(options);
    // todo：后面根据线条做优化
    // const height = 300;
    // console.log(options)
    const data = hasData(options);
    if (loading) return this.renderLoading(height);
    if (!data) return this.renderNoData(height);
    return <LineChart width={width} height={height} options={options} key="chart" />;
  }

  public renderCurve = (options: EChartOption, loading: boolean, expand?: boolean, key?: number) => {
    const data = hasData(options);
    return (
      <div className="common-chart-wrapper" key={key}>
        {this.renderTitle()}
        {this.props.hostName ? null : this.renderEachHost()}
        {this.renderEchart(options, loading, expand)}
        {this.renderOpBtns(options, expand)}
        {data ? this.renderOthers() : null}
      </div>
    );
  }

  public getAgentData = (prevProps: ICommonCurveProps & Props) => {
    const { taskid, timeRange, options, judgeUrl } = prevProps ? prevProps : this.props;
    const params = {
      agentId: taskid,
      startTime: timeRange[0].valueOf(),
      endTime: timeRange[1].valueOf(),
    }
    if (options.isPie) {
      getAgentPieData(options.api, { agentId: taskid })
        .then((res: any) => {
          this.setState({
            data: newdealMetricPanel(options, res, judgeUrl),
            loading: false,
          })
        }).catch((err: any) => {
          this.setState({
            loading: false,
          })
        });
      return;
    }
    getAgentLineData(options.api, params).then((res: any) => {
      const data = dealMetricPanel(options, res);
      // console.log(data)
      this.setState({
        data,
        loading: false,
      })
    }).catch((err: any) => {
      this.setState({
        loaing: false,
      })
    });
  }

  public getCollectData = (prevProps: ICommonCurveProps & Props) => {
    const { taskid, logCollectPathId, hostName, timeRange, options, judgeUrl } = prevProps ? prevProps : this.props;
    const params = {
      taskId: taskid,
      logCollectPathId: logCollectPathId,
      hostName: hostName,
      startTime: timeRange[0].valueOf(),
      endTime: timeRange[1].valueOf(),
      eachHost: this.state.eachHost
    }
    if (this.state.eachHost) {
      params.logCollectPathId = '';
      params.hostName = '';
    }
    if (options.isPie) {
      getCollectPieData(options.api, { taskId: taskid, logCollectPathId })
        .then((res: any) => {
          //todo 返回类型看接口在写
          this.setState({
            data: newdealMetricPanel(options, res, judgeUrl),
            loading: false,
          })
        }).catch((err: any) => {
          this.setState({
            loading: false,
          })
        });
      return;
    }
    getCollectLineData(options.api, params).then((res: any) => {
      const data = newdealMetricPanel(options, res?.metricList, judgeUrl);
      // console.log(data)
      this.setState({
        data,
        loading: false,
      }, () => {
        if (this.state.show) {
          this.expandChange();
          this.forceUpdate();
        }
      })
    }).catch((err: any) => {
      this.setState({
        loading: false,
      })
    });
  }

  public getData = (prevProps?: ICommonCurveProps & Props) => {
    this.setState({
      loading: true
    })
    this.props.judgeUrl ? this.getCollectData(prevProps as ICommonCurveProps & Props)
    : this.getAgentData(prevProps as ICommonCurveProps & Props)
  }

  componentDidMount() {
    this.getData()
  }

  componentWillReceiveProps(prevProps: ICommonCurveProps & Props) {
    if (JSON.stringify(prevProps) !== JSON.stringify(this.props)){
      // console.log('qingqiu',JSON.stringify(prevProps), JSON.stringify(this.props))
      this.getData(prevProps)
    }
  }

  public render() {
    const options = this.getCurveData();
    const loading = this.getLoading();
    return (
      <>
        {!this.props.selfHide &&
          <div className='chart-wrapper'>{this.renderCurve(options, loading)}</div>}
      </>
    );
  }
}
