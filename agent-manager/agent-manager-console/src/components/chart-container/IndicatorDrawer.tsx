/* eslint-disable react/prop-types */
import React, { useState, useRef, useEffect } from 'react';
import { Drawer, Button, Menu } from '@didi/dcloud-design';
import { IindicatorSelectModule, eventBus } from './index';
import IndicatorModule from './IndicatorModule';
import './style/indicator-drawer.less';

interface propsType extends React.HTMLAttributes<HTMLDivElement> {
  onClose: () => void;
  onSure: (value: any[]) => void;
  emitReload: () => void;
  visible: boolean;
  isGroup?: boolean; // 是否分组
  isGold?: boolean;
  indicatorSelectModule: IindicatorSelectModule;
}
let timer;
const IndicatorDrawer: React.FC<propsType> = ({ onClose, onSure, emitReload, visible, isGold, indicatorSelectModule }) => {
  const [currentKey, setCurrentKey] = useState(
    indicatorSelectModule?.menuList?.length > 0 ? indicatorSelectModule?.menuList[0]?.key : null
  );
  const childRef = useRef([]);
  const [queryData, setQueryData] = useState<any>({});

  useEffect(() => {
    eventBus.on('trggierMetricInit', () => {
      timer = setTimeout(() => {
        if (indicatorSelectModule?.menuList?.length !== 2) {
          sure(false); // !isGold ? true : false
        }
      }, 0);
    });
    eventBus.on('queryChartContainerChange', (data) => {
      const res = JSON.parse(JSON.stringify(queryData));
      data?.agent ? (res.agent = data?.agent) : '';
      if (data?.logCollectTaskId) {
        res.logCollectTaskId = data.logCollectTaskId;
        res.hostName = data.hostName;
        res.pathId = data.pathId;
      }

      setQueryData(res);
    });
    return () => {
      clearTimeout(timer);
      eventBus.removeAll('queryChartContainerChange');
      eventBus.removeAll('trggierMetricInit');
      localStorage.removeItem('metricTreeMaps0');
      localStorage.removeItem('metricTreeMaps1');
    };
  }, []);

  const menuSelect = ({ key }) => {
    setCurrentKey(key);
  };

  const sure = (isFirstRender: boolean) => {
    const resMap = {};
    let isStop = false;
    Object.keys(childRef.current).forEach((key) => {
      let res = childRef.current[key].getGroups();
      if (res) {
        res = res.sort((a, b) => b.groupId - a.groupId);
        resMap[key] = res;
      }

      if (!res) {
        isStop = true;
      }
    });
    if (isStop) return;
    let groups = [];
    if (indicatorSelectModule?.menuList?.length <= 1) {
      // 分组数据格式（agnet或采集任务）
      switch (currentKey) {
        case '0':
          groups = resMap[0];
          break;
        case '1':
          groups = resMap[1];
          break;
      }
    } else {
      // 不分组数据格式
      Object.keys(resMap).forEach((key) => {
        const lres = resMap[key]?.reduce((total, current) => {
          total = total.concat(current.lists);
          return total;
        }, []);
        groups = groups.concat(lres || []);
      });
    }
    onSure(groups);
    if (!isFirstRender) {
      emitReload();
    }
  };

  return (
    <>
      <Drawer
        title={indicatorSelectModule.drawerTitle || '指标筛选'}
        width="868px"
        className={indicatorSelectModule?.menuList?.length > 1 ? 'dd-indicator-drawer contain-tab' : 'dd-indicator-drawer'}
        forceRender={true}
        onClose={onClose}
        visible={visible}
        footer={
          <div
            style={{
              display: 'flex',
              justifyContent: 'flex-start',
            }}
          >
            <Button type="primary" style={{ marginRight: '8px', marginLeft: '8px' }} onClick={() => sure(false)}>
              确认
            </Button>
            <Button onClick={onClose}>取消</Button>
          </div>
        }
      >
        {indicatorSelectModule?.menuList?.length > 1 && (
          <Menu selectedKeys={[currentKey]} onSelect={menuSelect} mode="horizontal">
            {indicatorSelectModule?.menuList?.map((item) => (
              <Menu.Item key={item.key}>{item.name}</Menu.Item>
            ))}
          </Menu>
        )}

        {indicatorSelectModule?.menuList?.map((item) => {
          return (
            <IndicatorModule
              hide={currentKey != item.key ? true : false}
              currentKey={currentKey}
              tabKey={item.key}
              key={item.key}
              requestUrl={item.url}
              indicatorSelectModule={indicatorSelectModule}
              cRef={(f) => (childRef.current[item.key] = f)}
              isGold={isGold}
              isKanban={!isGold && indicatorSelectModule?.menuList?.length !== 2}
            />
          );
        })}
      </Drawer>
    </>
  );
};

export default IndicatorDrawer;
