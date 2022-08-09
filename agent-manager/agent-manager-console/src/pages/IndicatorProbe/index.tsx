import React, { useEffect, useState } from 'react';
import { AppContainer, SingleChart, Utils } from '@didi/dcloud-design';
import ChartContainer from '../../components/chart-container';
import { EventBusTypes } from '../../constants/event-types';
import DragItem from '../../components/DragItem';
import './index.less';

interface propsType {
  show?: boolean;
}

const menuList: any[] = [
  {
    name: 'Agent相关',
    key: '0', // 固定
    url: '/api/v1/normal/metrics/1',
  },
  {
    name: '采集任务相关',
    key: '1', // 固定
    url: '/api/v1/normal/metrics/2',
  },
];
const IndicatorProbe: React.FC<propsType> = () => {
  const headerLeftContent = <>指标探查</>;
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);

  return (
    <div className="indicator-probe">
      <ChartContainer
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
    </div>
  );
};

export default IndicatorProbe;
