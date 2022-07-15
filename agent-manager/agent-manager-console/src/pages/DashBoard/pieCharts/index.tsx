import React, { useEffect, useState } from 'react';
import { createOption } from './constants';
import PieChart from './Pie';

const DashBoardPieCharts = (props: { dashBoardData: Record<string, any> }): JSX.Element => {
  const { dashBoardData } = props;
  const [dataSource, setDataSource] = useState<Record<string, any>>({});

  useEffect(() => {
    setDataSource(dashBoardData);
  }, [dashBoardData]);

  const getOptions = (type) => {
    let health =
      dataSource?.logCollectTaskNum -
      dataSource?.yellowLogCollectTaskNameIdPairList?.length -
      dataSource?.redLogCollectTaskNameIdPairList?.length;

    let warning = dataSource?.yellowLogCollectTaskNameIdPairList?.length;
    let error = dataSource?.redLogCollectTaskNameIdPairList?.length;

    if (type !== 'collect') {
      health = dataSource?.agentNum - dataSource?.yellowAgentHostNameIdPairList?.length - dataSource?.redAgentHostNameIdPairList?.length;
      warning = dataSource?.yellowAgentHostNameIdPairList?.length;
      error = dataSource?.redAgentHostNameIdPairList?.length;
    }

    const data = [
      {
        value: health || 0,
        name: '健康率',
        totalValue: dataSource?.logCollectTaskNum,
      },
      { value: warning || 0, name: '预警率' },
      { value: error || 0, name: '故障率' },
    ];
    return createOption(data);
  };

  const getPieData = (target: string) => {
    const map = dataSource?.[target] || {};
    const data = [];
    for (const key of Object.keys(map)) {
      data.push({
        value: map[key],
        name: key,
      });
    }
    return data;
  };

  return (
    <div className="pie-dashboard-wrap">
      <h3 className="pie-dashboard-wrap-title"></h3>
      <div className="piedashboardbox">
        <PieChart
          id="collect"
          renderLegend={true}
          customOptions={{ legend: { show: false } }}
          dataSource={getPieData('osTypeCountMap')}
          title={'主机操作系统类型分布'}
        />
        <PieChart
          id="agent"
          customOptions={{ legend: { show: false } }}
          renderLegend={true}
          chartClassName="green-bg"
          dataSource={getPieData('agentVersionCountMap')}
          title={'Agent版本类型分布'}
        />
        <PieChart
          id="task"
          tabData={[
            {
              title: '故障任务',
              key: 'error',
              list: dataSource.redLogCollectTaskNameIdPairList,
              href: '/collect/detail',
              getState: (row) => {
                return {
                  taskId: row.value,
                };
              },
            },
            {
              title: '预警任务',
              key: 'warning',
              list: dataSource.yellowLogCollectTaskNameIdPairList,
              href: '/collect/detail',
              getState: (row) => {
                return {
                  taskId: row.value,
                };
              },
            },
          ]}
          customOptions={getOptions('collect')}
          type="donut"
          title={'各健康度类型采集任务分布'}
        />
        <PieChart
          id="health"
          tabData={[
            {
              title: '故障Agent',
              key: 'error',
              href: '/main/detail',
              list: dataSource.redAgentHostNameIdPairList,
              getState: (row) => {
                return {
                  agentId: `${row.value || ''}`,
                  hostName: `${row.key || ''}`,
                };
              },
            },
            {
              title: '预警Agent',
              key: 'warning',
              href: '/main/detail',
              list: dataSource.yellowAgentHostNameIdPairList,
              getState: (row) => {
                return {
                  agentId: `${row.value || ''}`,
                  hostName: `${row.key || ''}`,
                };
              },
            },
          ]}
          customOptions={getOptions('')}
          type="donut"
          chartClassName="red-bg"
          title={'各健康度类型Agent分布'}
        />
      </div>
    </div>
  );
};

export default DashBoardPieCharts;
