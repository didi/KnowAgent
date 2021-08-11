import React from 'react';
import { PieChart } from './pieChart';
import { Spin } from "antd";
import { getDashboard } from '../../../api/agent';

interface IProps {
  startTime: number,
  endTime: number,
}

interface Istate {
  dataSource: any;
  loading: boolean
}

export class PieCharts extends React.Component<IProps, Istate> {

  public state: Istate = {
    dataSource: {},
    loading: true,
  }

  public getData = () => {
    const { startTime, endTime } = this.props;
    const codeList = [0, 6, 12, 13, 14, 15]
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

  public render () {
    const { dataSource, loading } = this.state;
    return (<Spin spinning={loading}>
      <div className="piedashboardbox">
        <PieChart type="collect" dataSource={dataSource}/>
        <PieChart type="agent" dataSource={dataSource}/>
      </div>
    </Spin>)
  }
}