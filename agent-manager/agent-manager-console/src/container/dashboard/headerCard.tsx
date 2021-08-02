import React from 'react';
import { cardList } from './config';
import { data } from './LineCharts/mock';

interface IProps {
  dataSouce: any,
}

const heeaderStyle = 'dashboardHeader'

export class HeaderCard extends React.Component<IProps> {
  public render () {
    const { dataSouce } = this.props;
    return (
      <div className={heeaderStyle}>
        {cardList.map((item, index)=> (
          <div className={`${heeaderStyle}-item`} key={index}>
            <div className={`${heeaderStyle}-item-icon`}>
              <img className={`${heeaderStyle}-item-icon-item`} src={item.icon}></img>
            </div>
            <div className={`${heeaderStyle}-item-content`}>
              <div className={`${heeaderStyle}-item-content-title`}>{item.title}</div>
              <div className={`${heeaderStyle}-item-content-context`}><span className={`${heeaderStyle}-item-content-context-num`}>{dataSouce[item.api] && item.format ? item.format(dataSouce[item.api]) : dataSouce[item.api] }</span><span className={`${heeaderStyle}-item-content-context-span`}>{item.unit ? item.unit : '个'}</span></div>
              {item.text ? 
                <div className={`${heeaderStyle}-item-content-text`}>
                  {item.text}: <span className={`${heeaderStyle}-item-content-text-span`}>{dataSouce[item.textApi as string]}个</span>
                </div>
                : null
              }
            </div>
          </div>
        ))}
      </div>
    )
  }
}