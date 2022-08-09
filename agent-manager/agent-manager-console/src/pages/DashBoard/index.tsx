import React, { useEffect, useState } from 'react';
import { AppContainer, Spin } from '@didi/dcloud-design';
import { EventBusTypes } from '../../constants/event-types';
import HeaderCard from './headerCard';
import { getOperatingDashboard } from './service';
import './style/index.less';
import BackToTop from './BackToTop';
import BarCharts from './BarCharts';
import { appBarList } from './config';

const HomePage = (): JSX.Element => {
  const headerLeftContent = <>运营大盘</>;

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
              <HeaderCard type="yunying" dashBoardData={dashBoardData} />
              <BarCharts needTrigger={true} barList={appBarList} type="" dashBoardData={dashBoardData} />
            </>
          )}
        </div>
      </Spin>
      <BackToTop></BackToTop>
    </>
  );
};

export default HomePage;
