import React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { DatePicker, Button, Icon, Radio, Select } from 'antd';
import { timeFormat, valMoments } from '../../constants/time';
import { getCollectTaskHostNameList } from '../../api/collect'
import moment from 'moment';
import './index.less';
import { RadioChangeEvent } from 'antd/lib/radio'

const { RangePicker } = DatePicker;
const Option = Select.Option;

// 事件配置项写死在前端
const tiemOptions = [
  { label: '近15分钟', value: 15},
  { label: '近1小时', value: 60 },
  { label: '近1天', value: 60 * 24 },
];

interface IDataCurveFilter {
  refresh: any;
  judgeUrl: boolean;
  agentid?: number
}

const mapStateToProps = (state: any) => ({
  timeRange: state.agent.timeRange,
  detail: state.collect.detail,
  logCollectPathId: state.collect.logCollectPathId,
  hostName: state.collect.hostName,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setTimeRange: (timeRange: moment.Moment[]) => dispatch(actions.setTimeRange(timeRange)),
  setCollectState: (params: any) => dispatch(actions.setCollectState(params)),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class DataCurveFilter extends React.Component<Props & IDataCurveFilter> {
  public state = {
    hostNameList: [] as any[],
  }
  public handleRangeChange = (dates: any) => {
    this.props.setTimeRange(dates);
    this.props.refresh(dates);
  }

  public refreshChart = () => {
    this.props.refresh([moment().subtract(60, 'minute'), moment()]);
    this.props.setTimeRange([moment().subtract(60, 'minute'), moment()]);
  }

  public timeChange = (event: RadioChangeEvent) => {
    const value = event.target.value;
    this.props.refresh([moment().subtract(value, 'minute'), moment()]);
    this.props.setTimeRange([moment().subtract(value, 'minute'), moment()])
  }

  public selectChange = (key: string, value: number) => {
    this.props.setCollectState({
      [key]: value
    })
  }

  public componentDidMount() {
    getCollectTaskHostNameList(this.props.agentid as number)
      .then(res => {
        this.setState({
          hostNameList: res
        })
      })
  }


  public render() {
    const { hostNameList } = this.state;
    const { logCollectPathId, hostName, detail } = this.props;
    return (
      <div className="time-box">
        <div className="time-box-select-box">
          {
            this.props.judgeUrl ? 
              <div>
                <span>日志采集路径：</span>
                <Select value={logCollectPathId} style={{ width: 180, marginRight: 10 }} onChange={(value: number) => this.selectChange('logCollectPathId', Number(value))}>
                  {(detail.fileLogCollectPathList as any[] || []).map(path => (
                    <Option title={path.hostName} value={path.id} key={path.id}>
                      {path.path}
                    </Option>
                  ))}
                </Select>
                <span>主机名：</span>
                <Select value={hostName} style={{ width: 130, marginRight: 10 }} onChange={(value: number) => this.selectChange('hostName', Number(value))}>
                  {hostNameList.map(hostName => (
                    <Option title={hostName.hostName} value={hostName.hostId} key={hostName.hostId}>
                      {hostName.hostName}
                    </Option>
                  ))}
                </Select>
              </div>
            : null
          }
          <Button onClick={this.refreshChart} type="primary"><Icon type="reload" key="refresh" />刷新</Button>
        </div>
        <div>
          <span>时间：</span>
          <Radio.Group
            onChange={this.timeChange}
            defaultValue={60}
          >
            {
              tiemOptions.map(option => (
                <Radio.Button value={option.value} key={option.value}>{option.label}</Radio.Button>
              ))
            }
          </Radio.Group>
          <RangePicker
            className="ml-10"
            style={{ width: 330 }}
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

