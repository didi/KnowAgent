import React from 'react';
import { cardList } from './config';
import { Tooltip, Spin } from "antd";
import { getDashboard } from './../../api/agent';

interface IProps {
  startTime: number,
  endTime: number,
}

interface Istate {
  dataSource: any;
  loading: boolean
}

const heeaderStyle = 'dashboardHeader'

export class HeaderCard extends React.Component<IProps, Istate> {

  public state: Istate = {
    dataSource: {},
    loading: true,
  }

  public getData = () => {
    const { startTime, endTime } = this.props;
    const codeList: number[] = [];
    cardList.forEach(item => {
      if (item.textCode) {
        codeList.push(item.textCode);
      }
      codeList.push(item.code);
    });
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
    // console.log(prevProps, this.props)
    if (JSON.stringify(prevProps) !== JSON.stringify(this.props)) {
      this.getData();
    }
  }

  public render () {
    const { dataSource, loading } = this.state;
    return (
      <Spin spinning={loading}>
        <div className={heeaderStyle}>
          {cardList.map((item, index)=> (
            <div className={`${heeaderStyle}-item`} key={index}>
              <div className={`${heeaderStyle}-item-icon`}>
                <img className={`${heeaderStyle}-item-icon-item`} src={item.icon}></img>
              </div>
              <div className={`${heeaderStyle}-item-content`}>
                <div className={`${heeaderStyle}-item-content-title`}>{item.title}</div>
                <div className={`${heeaderStyle}-item-content-context`}>
                  <Tooltip title={dataSource[item.api] + item.tip}>
                    <span className={`${heeaderStyle}-item-content-context-num`}>{dataSource[item.api] && item.format ? item.format(dataSource[item.api]) : dataSource[item.api] }</span><span className={`${heeaderStyle}-item-content-context-span`}>{item.unit ? item.unit : '个'}</span>
                  </Tooltip>
                </div>
                {item.text ? 
                  <div className={`${heeaderStyle}-item-content-text`}>
                    {item.text}: <span className={`${heeaderStyle}-item-content-text-span`}>{dataSource[item.textApi as string]}个</span>
                  </div>
                  : null
                }
              </div>
            </div>
          ))}
        </div>
      </Spin>
    )
  }
}