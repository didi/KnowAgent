import React from 'react';
import { SingleChart } from '@didi/dcloud-design';
import { createOption } from './constants';
import { IconFont } from '@didi/dcloud-design';
import { TextRouterLink } from '../utils';
import { Link } from 'react-router-dom';
import './style/index.less';

interface IProps {
  type: string;
  dataSource: any;
}

const DashBoardPie = (props: IProps): JSX.Element => {
  const { type, dataSource } = props;
  const className = `dashboard-piechart`;
  const stateKey = type === 'collect' ? 'logCollectTaskHealthLevelList' : 'agentHealthLevelList';

  const renderChart = () => {
    let data = [
      {
        value:
          dataSource?.logCollectTaskNum -
            dataSource?.yellowLogCollectTaskNameIdPairList?.length -
            dataSource?.redLogCollectTaskNameIdPairList?.length || 0,
        name: '健康率',
        totalValue: dataSource?.logCollectTaskNum,
      },
      { value: dataSource?.yellowLogCollectTaskNameIdPairList?.length || 0, name: '预警率' },
      { value: dataSource?.redLogCollectTaskNameIdPairList?.length || 0, name: '故障率' },
    ];
    if (props.type !== 'collect') {
      data = [
        {
          value:
            dataSource?.agentNum - dataSource?.yellowAgentHostNameIdPairList?.length - dataSource?.redAgentHostNameIdPairList?.length || 0,
          name: '健康率',
          totalValue: dataSource?.agentNum,
        },
        { value: dataSource?.yellowAgentHostNameIdPairList?.length || 0, name: '预警率' },
        { value: dataSource?.redAgentHostNameIdPairList?.length || 0, name: '故障率' },
      ];
    }

    const options = createOption(data);
    return (
      <div style={{ marginTop: 40, float: 'left' }}>
        <SingleChart
          chartTypeProp="pie"
          wrapStyle={{
            width: 159,
            height: 159,
            zIndex: 99,
          }}
          option={options}
          propChartData={data}
          key="chart"
        />
      </div>
    );
  };

  const renderFaultTask = () => {
    const list = (props.type === 'collect' ? dataSource.redLogCollectTaskNameIdPairList : dataSource.redAgentHostNameIdPairList) || [];
    return (
      <div className={`${className}-faulttask`}>
        <div className={`${className}-faulttask-icon`}>
          <IconFont type="icon-hong" className={`${className}-faulttask-icon-item`} />
          <span className={`${className}-faulttask-icon-title`}>
            <Link
              to={{
                pathname: type === 'collect' ? '/collect' : '/main',
                state: {
                  [stateKey]: type === 'collect' ? [2] : [0],
                },
              }}
            >
              {props.type === 'collect' ? '故障任务' : '故障Agent'}
            </Link>
          </span>
        </div>
        <div className={`${className}-faulttask-content`}>
          <ul className={`${className}-faulttask-content-ul`}>
            {list.map((item: any, index: number) => {
              return (
                <li className={`${className}-faulttask-content-ul-li`} key={index}>
                  <TextRouterLink
                    textLength={type === 'collect' ? 12 : 26}
                    needToolTip
                    element={item.key}
                    href={`${type === 'collect' ? '/collect' : '/main'}`}
                    state={
                      type === 'collect'
                        ? {
                            taskId: `${item.value || ''}`,
                          }
                        : {
                            agentId: `${item.value || ''}`,
                            hostName: `${item.key || ''}`,
                          }
                    }
                  />
                </li>
              );
            })}
          </ul>
        </div>
      </div>
    );
  };

  const rendeRearlyWarningTask = () => {
    const list =
      (props.type === 'collect' ? dataSource.yellowLogCollectTaskNameIdPairList : dataSource.yellowAgentHostNameIdPairList) || [];
    return (
      <div className={`${className}-faulttask`} style={{ marginLeft: 12 }}>
        <div className={`${className}-faulttask-icon`}>
          <IconFont type="icon-huang" className={`${className}-faulttask-icon-item`} />
          <span className={`${className}-faulttask-icon-title`}>
            <Link
              to={{
                pathname: type === 'collect' ? '/collect' : '/main',
                state: {
                  [stateKey]: [1],
                },
              }}
            >
              {props.type === 'collect' ? '预警任务' : '预警Agent'}{' '}
            </Link>
          </span>
        </div>
        <div className={`${className}-faulttask-content`}>
          <ul className={`${className}-faulttask-content-ul`}>
            {list.map((item: any, index: number) => {
              return (
                <li className={`${className}-faulttask-content-ul-li`} key={index}>
                  <TextRouterLink
                    textLength={type === 'collect' ? 12 : 26}
                    needToolTip
                    element={item.key}
                    href={`${type === 'collect' ? '/collect/detail' : '/main/detail'}`}
                    state={
                      type === 'collect'
                        ? {
                            taskId: `${item.value || ''}`,
                          }
                        : {
                            agentId: `${item.value || ''}`,
                            hostName: `${item.key || ''}`,
                          }
                    }
                  />
                </li>
              );
            })}
          </ul>
        </div>
      </div>
    );
  };

  return (
    <div className={className}>
      <div className={`${className}-header`}>
        <div className={`${className}-header-title`}>{type === 'collect' ? '采集任务状态总览' : 'Agent状态总览'}</div>
      </div>
      {renderChart()}
      {renderFaultTask()}
      {rendeRearlyWarningTask()}
    </div>
  );
};

export default DashBoardPie;
