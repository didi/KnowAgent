import React from 'react';
import { PieChart } from './pieChart';

interface IProps {
  dataSouce: {},
}

export class PieCharts extends React.Component<IProps> {
  public render () {
    return (<div style={{ height: 286 }}>
      <PieChart type="collect" dataSouce={this.props.dataSouce}/>
      <PieChart type="agent" dataSouce={this.props.dataSouce}/>
    </div>)
  }
}