import React from 'react';
import Line from './../../../component/echarts';
import { createOptions } from './constants';
import './index.less';
import { Spin } from "antd";
import { getDashboard } from '../../../api/agent';
// import { data } from './mock';

interface IProps {
  configList: any[];
  title: string;
  startTime: number;
  endTime: number;
}

interface Istate {
  width: number | undefined;
  height: number;
  dataSource: any;
  loading: boolean
}

const lineClassName = 'dashboard-lineCard';

export class LineChart extends React.Component<IProps, Istate> {

  public state: Istate = {
    width: document.querySelector(`.dashboard-lineCard-content-linebox-line`)?.clientWidth,
    height: 263,
    dataSource: {},
    loading: true,
  }

  public getData = () => {
    const { startTime, endTime, configList } = this.props;
    const codeList = configList.map(item => item.code);
    getDashboard(startTime, endTime, codeList)
      .then(res => {
        this.setState({dataSource: res, loading: false});
      }).catch((err) => {
        this.setState({loading: false});
      })
  }

  public componentDidMount() {
    this.getData();
  }

  public componentDidUpdate(prevProps: IProps) {
    if (JSON.stringify(prevProps) !== JSON.stringify(this.props)) {
      this.getData();
    }
  }

  public renderLine = (config: any) => {
    const options = createOptions(config, this.state.dataSource[config.api]);
    return (
      <div className={`${lineClassName}-content-linebox`} key={config.title}>
        {/* <div className={`${lineClassName}-content-linebox-title`}>
          {config.title}
        </div> */}
        <div className={`${lineClassName}-content-linebox-line`}>
          <Line width={this.state.width} height={this.state.height} options={options} key={config.title} />
        </div>
      </div>
    );
  }

  public render () {
    return (
      <div className={lineClassName}>
        <Spin spinning={this.state.loading}>
          <div className={`${lineClassName}-title`}>{this.props.title}</div>
          <div className={`${lineClassName}-content`}>
            {this.props.configList.map(config => (
              this.renderLine(config)
            ))}
          </div>
        </Spin>
      </div>
    )
  }
}