import React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { DatePicker, Button, Icon } from 'antd';
import { timeFormat, valMoments } from '../../constants/time';
import moment from 'moment';
import './index.less';

const { RangePicker } = DatePicker;

interface IDataCurveFilter {
  refresh: any;
}

const mapStateToProps = (state: any) => ({
  timeRange: state.agent.timeRange,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setTimeRange: (timeRange: moment.Moment[]) => dispatch(actions.setTimeRange(timeRange)),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class DataCurveFilter extends React.Component<Props & IDataCurveFilter> {
  public handleRangeChange = (dates: any) => {
    this.props.setTimeRange(dates);
    this.props.refresh(dates);
  }

  public refreshChart = () => {
    this.props.refresh([moment().subtract(10, 'minute'), moment()]);
    this.props.setTimeRange([moment().subtract(10, 'minute'), moment()]);
  }

  public render() {
    return (
      <div className="time-box">
        <Button onClick={this.refreshChart} type="primary"><Icon type="reload" key="refresh" />刷新</Button>
        <div>
          <span>时间：</span>
          <RangePicker
            className="ml-10"
            defaultValue={this.props.timeRange}
            showTime
            value={this.props.timeRange}
            allowClear={false}
            format={timeFormat}
            onOk={this.handleRangeChange}
          />
        </div>
      </div>
    );
  }
}