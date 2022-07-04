import React, { useEffect, useState } from 'react';
import { AppContainer, Spin } from '@didi/dcloud-design';
import { EventBusTypes } from '../../constants/event-types';
import HeaderCard from './headerCard';
import LineCharts from './lineCharts';
import { getOperatingDashboard } from './service';
import './style/index.less';
import BackToTop from './BackToTop';

const HomePage = (): JSX.Element => {
  const headerLeftContent = <>我的工作台</>;

  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);

  const [dashBoardData, setDashBoardData] = useState<Record<string, any>>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const getData = () => {
    setLoading(true);
    getOperatingDashboard()
      .then((res: any) => {
        setDashBoardData(res);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
      });
  };

  useEffect(() => {
    getData();
  }, []);

  return (
    <>
      <Spin spinning={loading}>
        <div className="dashboard" id="dashboardWrap">
          {dashBoardData && (
            <>
              <HeaderCard dashBoardData={dashBoardData} />
              {/* <LineCharts dashBoardData={dashBoardData} /> */}
            </>
          )}
        </div>
      </Spin>
      <BackToTop></BackToTop>
    </>
  );
};

export default HomePage;
