import React from 'react';
import { LineChart } from './LineChart';
import { collectList, agentList } from './../config';

interface IProps {
  startTime: number,
  endTime: number,
}

export class LineCharts extends React.Component<IProps> {
  public render () {
    const { startTime, endTime } = this.props;
    return (
      <>
        <LineChart configList={collectList} title={'采集任务资源使用情况TOP5'} startTime={startTime} endTime={endTime}/>
        <LineChart configList={agentList} title={'Agent运行状态指标TOP5'} startTime={startTime} endTime={endTime}/>
      </>
    )
  }
}