import React, { useEffect, useState } from 'react';
import { AppContainer, Spin } from '@didi/dcloud-design';
import { EventBusTypes } from '../../constants/event-types';
import HeaderCard from './headerCard';
import PieCharts from './pieCharts';
import LineCharts from './lineCharts';
import { getDevopsDashboard } from './service';
import './style/index.less';
import BackToTop from './BackToTop';
import BarCharts from './BarCharts';
import { agentBarList, taskBarList } from './config';
import { useHistory } from 'react-router-dom';

const HomePage = (): JSX.Element => {
  const headerLeftContent = <>我的工作台</>;
  const history = useHistory();
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);

  const [dashBoardData, setDashBoardData] = useState<Record<string, any>>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const getData = () => {
    setLoading(true);
    getDevopsDashboard()
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

  const getBarXData = (data) => {
    return data.map((item) => item.key?.logCollectTaskName);
  };

  const linkTo = (name, item) => {
    const data = dashBoardData?.[item.key]?.histogramChatValue || [];
    const taskId = data.filter((row) => row.key?.logCollectTaskName === name)?.[0]?.key?.id;
    history.push({ pathname: '/collect/detail', state: { taskId } });
  };

  const linkToAgentDetail = (name, item) => {
    const data = dashBoardData?.[item.key]?.histogramChatValue || [];
    const hostName = data.filter((row) => row.key === name)?.[0]?.key;
    history.push({
      pathname: '/main/detail',
      state: { hostName },
    });
  };

  return (
    <>
      <Spin spinning={loading}>
        <div className="dashboard" id="dashboardWrap">
          {dashBoardData && (
            <>
              <HeaderCard type="yunwei" dashBoardData={dashBoardData} />
              <PieCharts dashBoardData={dashBoardData} />
              <BarCharts linkTo={linkToAgentDetail} barList={agentBarList} type="Agent视角 TOP5" dashBoardData={dashBoardData} />
              <BarCharts
                barList={taskBarList}
                linkTo={linkTo}
                getKeys={getBarXData}
                type="采集任务视角 TOP5"
                dashBoardData={dashBoardData}
              />
            </>
          )}
        </div>
      </Spin>
      <BackToTop></BackToTop>
    </>
  );
};

export default HomePage;
