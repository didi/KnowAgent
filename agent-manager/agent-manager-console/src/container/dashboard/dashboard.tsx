import React from 'react';
import { HeaderCard } from './headerCard';
import { PieCharts } from './pieCharts';
import { LineCharts } from './LineCharts';
import ActionClusterModal from './../../modal/receivingTerminal/actionClusterModal';
import moment from 'moment';
import './dashboard.less';

const timeNum = 60;
const upDataTime = 1;

export class Dashboard extends React.Component {
  public state = {
    startTime: moment().subtract(timeNum, 'minute').valueOf(),
    endTime: moment().valueOf(),
  }
  mainElement: any
  timer: any;

  public componentDidMount() {
    this.mainElement = document.querySelector('#ecmc-layout-main');
    this.mainElement.style.backgroundColor = '#F0F2F5';
    this.mainElement.style.padding = '0px';
    this.timer = setInterval(() => {
      this.setState({
        startTime: moment().subtract(timeNum, 'minute').valueOf(),
        endTime: moment().valueOf(),
      })
    }, upDataTime * 60 * 1000)
  }

  public componentWillUnmount() {
    this.mainElement.style.backgroundColor = '#fff';
    this.mainElement.style.padding = '24px';
    clearInterval(this.timer);
  }

  public render() {
    const { startTime, endTime } = this.state;
    return (
      <div className="dashboard">
        <ActionClusterModal />
        <HeaderCard startTime={startTime} endTime={endTime}/>
        <PieCharts startTime={startTime} endTime={endTime}/>
        <LineCharts startTime={startTime} endTime={endTime}/>
      </div>
    )
  }
}