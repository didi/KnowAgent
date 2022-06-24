import React, { useEffect } from 'react';
import { AppContainer } from '@didi/dcloud-design';
import { EventBusTypes } from '../constants/event-types';

export const DemoPage = () => {
  const headerLeftContent = (
    <>
      Demo
      <span>页面</span>
    </>
  );
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);
  return <>This is a demo page. </>;
};
