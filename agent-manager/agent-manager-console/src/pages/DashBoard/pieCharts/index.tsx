import React, { useEffect, useState } from 'react';
import PieChart from './pieChart';

const DashBoardPieCharts = (props: { dashBoardData: Record<string, any> }): JSX.Element => {
  const { dashBoardData } = props;
  const [dataSource, setDataSource] = useState<Record<string, any>>({});

  useEffect(() => {
    setDataSource(dashBoardData);
  }, [dashBoardData]);

  return (
    <div className="pie-dashboard-wrap">
      <h3 className="pie-dashboard-wrap-title">状态监控</h3>
      <div className="piedashboardbox">
        <PieChart type="collect" dataSource={dataSource} />
        <PieChart type="agent" dataSource={dataSource} />
      </div>
    </div>
  );
};

export default DashBoardPieCharts;
