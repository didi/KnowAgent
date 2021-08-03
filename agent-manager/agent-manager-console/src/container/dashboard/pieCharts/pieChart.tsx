import React from 'react';
import LineChart, { hasData } from '../../../component/echarts';
import { TextRouterLink } from '../../../component/CustomComponent';
import { createOption } from './constants';
import OneAlart from '../../../assets/onealart.png';
import TwoAlart from '../../../assets/twoalart.png';
import './index.less';

interface IProps {
  type: string;
  dataSouce: any;
}

export class PieChart extends React.Component<IProps> {
  className: string;
  public constructor (props: IProps) {
    super(props);
    this.className = `${this.props.type}-piechart`;
  } 
  
  public renderChart = () => {
    const { dataSouce } = this.props;
    let data = [
      {value: ((dataSouce?.logCollectTaskNum - dataSouce?.yellowLogCollectTaskNameIdPairList?.length) - dataSouce?.redLogCollectTaskNameIdPairList?.length ) || 0, name: '健康率', oldValue: dataSouce?.logCollectTaskNum},
      {value: dataSouce?.yellowLogCollectTaskNameIdPairList?.length || 0, name: '预警率'},
      {value: dataSouce?.redLogCollectTaskNameIdPairList?.length || 0, name: '故障率'},
    ]
    if (this.props.type !== 'collect') {
      data = [
        {value: ((dataSouce?.agentNum - dataSouce?.yellowAgentHostNameIdPairList?.length) - dataSouce?.redAgentHostNameIdPairList?.length ) || 0, name: '健康率', oldValue: dataSouce?.agentNum},
        {value: dataSouce?.yellowAgentHostNameIdPairList?.length || 0, name: '预警率'},
        {value: dataSouce?.redAgentHostNameIdPairList?.length || 0, name: '故障率'},
      ]
    }
    const options = createOption(data);
    return (
      <div style={{ marginTop: 40, float: 'left' }}>
        <LineChart isResize={true} width={162} height={162} options={options} key="chart"/>
      </div>
    )
  }

  public renderFaultTask = () => {
    const { dataSouce } = this.props;
    const list = (this.props.type === 'collect' ? dataSouce.redLogCollectTaskNameIdPairList : dataSouce.redAgentHostNameIdPairList) || [];
    return (
      <div className={`${this.className}-faulttask`}>
        <div className={`${this.className}-faulttask-icon`}>
          <img className={`${this.className}-faulttask-icon-item`} src={OneAlart}></img>
          <span className={`${this.className}-faulttask-icon-title`}>{this.props.type === 'collect' ? '故障任务' : '故障Agent'}</span>
        </div>
        <div className={`${this.className}-faulttask-content`}>
          <ul>
            {list.map((item: any, index: any) => {
              return <li className={`${this.className}-faulttask-content-li`} key={index}>
                  <TextRouterLink textLength={this.props.type === 'collect' ? 12 : 26} needToolTip element={item.key} href={`${this.props.type === 'collect' ? '/collect/detail' : '/detail'}`} state={this.props.type === 'collect' ? {
                    taskId: `${item.value || ''}`,
                    tabs: 2,
                  } : {
                    agentId: `${item.value || ''}`,
                    hostName: `${item.key || ''}`,
                  }} />
                </li>
            })}
          </ul>
        </div>
      </div>
    )
  }

  public rendeRearlyWarningTask = () => {
    const { dataSouce } = this.props;
    const list = (this.props.type === 'collect' ? dataSouce.yellowLogCollectTaskNameIdPairList : dataSouce.yellowAgentHostNameIdPairList) || [];
    return (
      <div className={`${this.className}-faulttask`} style={{ marginLeft: this.props.type === 'collect' ? 60 : 37}}>
        <div className={`${this.className}-faulttask-icon`}>
          <img className={`${this.className}-faulttask-icon-item`} src={TwoAlart}></img>
          <span className={`${this.className}-faulttask-icon-title`}>{this.props.type === 'collect' ? '预警任务' : '预警Agent'}</span>
        </div>
        <div className={`${this.className}-faulttask-content`}>
          <ul>
            {list.map((item: any, index: any) => {
              return <li className={`${this.className}-faulttask-content-li`} key={index}>
                  <TextRouterLink textLength={this.props.type === 'collect' ? 12 : 26} needToolTip element={item.key} href={`${this.props.type === 'collect' ? '/collect/detail' : '/detail'}`} state={this.props.type === 'collect' ? {
                    taskId: `${item.value || ''}`,
                    tabs: 2,
                  } : {
                    agentId: `${item.value || ''}`,
                    hostName: `${item.key || ''}`,
                  }} />
                </li>
            })}
          </ul>
        </div>
      </div>
    )
  }

  public render () {
    const { type } = this.props;
    return (
      <div className={this.className}>
        <div className={`${this.className}-header`}>
          <div className={`${this.className}-header-title`}>
            {type === 'collect' ? '采集任务状态总览' : 'Agent状态总览'}
          </div>
        </div>
        {this.renderChart()}
        {this.renderFaultTask()}
        {this.rendeRearlyWarningTask()}
      </div>
    )
  }
}
