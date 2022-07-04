import React, { useEffect } from 'react';
import { AppContainer } from '@didi/dcloud-design';
import { EventBusTypes } from '../constants/event-types';

export const HomePage = () => {
  const headerLeftContent = (
    <>
      表格

    </>
  );
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);
  return <>welcome to D1 world!</>;
};
