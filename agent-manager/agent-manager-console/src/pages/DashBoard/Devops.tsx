import React, { useEffect, useState } from 'react';
import { AppContainer, Spin } from '@didi/dcloud-design';
import { EventBusTypes } from '../../constants/event-types';
import HeaderCard from './headerCard';
import PieCharts from './pieCharts';
import { getDevopsDashboard } from './service';
import './style/index.less';
import BackToTop from './BackToTop';
import BarCharts from './BarCharts';
import { agentBarList, taskBarList } from './config';
import { useHistory } from 'react-router-dom';

const HomePage = (): JSX.Element => {
  const headerLeftContent = <>运维大盘</>;
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
    const logCollectTaskId = data.filter((row) => row.key?.logCollectTaskName === name)?.[0]?.key?.id;
    history.push({ pathname: '/monitor/metric', state: { logCollectTaskId } });
  };

  const linkToAgentDetail = (name, item) => {
    const data = dashBoardData?.[item.key]?.histogramChatValue || [];
    const agent = data.filter((row) => row.key === name)?.[0]?.key;
    history.push({
      pathname: '/monitor/agent-kanban',
      state: { agent },
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
              <BarCharts
                needTrigger={true}
                linkTo={linkToAgentDetail}
                barList={agentBarList}
                type="Agent视角 TOP5"
                dashBoardData={dashBoardData}
              />
              <BarCharts
                barList={taskBarList}
                linkTo={linkTo}
                needTrigger={true}
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
