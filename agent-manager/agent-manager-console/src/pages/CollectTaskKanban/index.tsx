import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { AppContainer } from '@didi/dcloud-design';
import ChartContainer from '../../components/chart-container';
import { EventBusTypes } from '../../constants/event-types';
import DragItem from '../../components/DragItem';

interface propsType {
  show?: boolean;
}
interface Istate {
  hostName?: string;
  logCollectTaskId?: string | number;
  pathId?: string | number;
  agent?: string;
}

const menuList: any[] = [
  {
    name: '采集任务相关',
    key: '1', // 固定
    url: '/api/v1/normal/metrics/2',
  },
];
const IndicatorProbe: React.FC<propsType> = () => {
  const { state } = useLocation<Istate>();
  const headerLeftContent = <>指标看板</>;
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);

  return (
    <>
      <ChartContainer
        filterData={{
          hostName: state?.hostName,
          logCollectTaskId: state?.logCollectTaskId,
          pathId: state?.pathId,
          agent: state?.agent,
        }}
        reloadModule={{
          reloadIconShow: true,
          lastTimeShow: true,
        }}
        dragModule={{
          dragItem: <DragItem></DragItem>,
          requstUrl: '/api/v1/normal/metrics/metric',
          isGroup: true,
        }}
        indicatorSelectModule={{
          hide: false,
          menuList,
        }}
      />
    </>
  );
};

export default IndicatorProbe;
