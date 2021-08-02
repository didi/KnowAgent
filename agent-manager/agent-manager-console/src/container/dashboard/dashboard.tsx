import React from 'react';
import { Spin } from 'antd';
import { getDashboard } from './../../api/agent';
import { HeaderCard } from './headerCard';
import { PieCharts } from './pieCharts';
import { LineCharts } from './LineCharts';
import ActionClusterModal from './../../modal/receivingTerminal/actionClusterModal';
import moment from 'moment';
import './dashboard.less';

const timeNum = 60;
const upDataTime = 5;

export class Dashboard extends React.Component {
  public state = {
    data: {},
    loading: true,
  }
  mainElement: any
  timer: any;

  getData = () => {
    getDashboard(moment().subtract(timeNum, 'minute').valueOf(), moment().valueOf())
      .then(res => {
        this.setState({data: res, loading: false});
      }).catch((err) => {
        this.setState({loading: false});
      })
  }
  public componentDidMount() {
    this.mainElement = document.querySelector('#ecmc-layout-main');
    this.mainElement.style.backgroundColor = '#F0F2F5';
    this.mainElement.style.padding = '0px';
    this.getData();
    this.timer = setInterval(() => {
      this.getData()
    }, upDataTime * 60 * 1000)
  }

  public componentWillUnmount() {
    this.mainElement.style.backgroundColor = '#fff';
    this.mainElement.style.padding = '24px';
    clearInterval(this.timer);
  }

  public render() {
    const { data, loading } = this.state;
    return (
      <div className="dashboard">
        <ActionClusterModal />
        <Spin spinning={loading}>
          <HeaderCard dataSouce={data}/>
          <PieCharts dataSouce={data} />
          <LineCharts dataSouce={data} />
        </Spin>
      </div>
    )
  }
}