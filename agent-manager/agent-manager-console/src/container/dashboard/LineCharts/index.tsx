import React from 'react';
import { LineChart } from './LineChart';
import { collectList, agentList } from './../config';

interface IProps {
  dataSouce: {},
}

export class LineCharts extends React.Component<IProps> {
  public render () {
    return (
      <>
        <LineChart configList={collectList} title={'采集任务资源使用情况TOP5'} dataSouce={this.props.dataSouce} />
        <LineChart configList={agentList} title={'Agent运行状态指标TOP5'} dataSouce={this.props.dataSouce} />
      </>
    )
  }
}