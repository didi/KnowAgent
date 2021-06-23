import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { EChartOption } from 'echarts';
import { getHeight, EXPAND_GRID_HEIGHT } from './constants';
import { Spin, Icon } from 'antd';
import LineChart, { hasData } from '../../component/echarts';
import './index.less';

export interface ICommonCurveProps {
  title: string;
  options: EChartOption;
  selfHide: boolean;
}

const mapStateToProps = (state: any) => ({
  loading: state.echarts.loading,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  showContent: (content: JSX.Element) => dispatch(actions.showContent(content)),
  closeContent: () => dispatch(actions.closeContent()),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class CommonCurve extends React.Component<ICommonCurveProps & Props> {

  public getLoading = () => {
    return this.props.loading;
  }

  public expandChange = () => {
    const curveOption = this.getCurveData();
    const options = Object.assign({}, curveOption, {
      grid: {
        ...curveOption.grid,
        height: EXPAND_GRID_HEIGHT,
      },
    });
    const loading = this.getLoading();
    this.props.showContent(this.renderCurve(options, loading, true));
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
    if (expand) return <Icon type="close" onClick={this.props.closeContent} key="close" />;
    return <Icon type="fullscreen" className="ml-17" onClick={this.expandChange} key="full-screen" />;
  }

  public renderTitle = () => {
    return (
      <div className="charts-title" key="title">{this.props.title}</div>
    );
  }

  public getCurveData = () => {
    return this.props.options;
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
    const width = expand ? undefined : 360;
    const height = getHeight(options);
    const data = hasData(options);
    if (loading) return this.renderLoading(height);
    if (!data) return this.renderNoData(height);
    return <LineChart width={width} height={height} options={options} key="chart" />;
  }

  public renderCurve = (options: EChartOption, loading: boolean, expand?: boolean) => {
    const data = hasData(options);
    return (
      <div className="common-chart-wrapper" >
        {this.renderTitle()}
        {this.renderEchart(options, loading, expand)}
        {this.renderOpBtns(options, expand)}
        {data ? this.renderOthers() : null}
      </div>
    );
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

