import React, { useState } from 'react';
import ChartContainer from '../../../../components/chart-container';
import DragItem from '../../../../components/DragItem';

export default (props) => {
  const defaultConfig = {
    reloadIconShow: true,
    isGroup: true,
    groupCount: 2,
    groupItemsCount: '4,2',
    itemCount: 2,
    isHaveTabInMetricTree: true,
    menuList: ['0', '1'],
    componentType: 'SingleChart',
  };
  const config = Object.assign({}, defaultConfig, props?.config);

  const menuList: any[] = (
    config.isHaveTabInMetricTree
      ? [
        // {
        //   name: "Agent",
        //   key: '0', // 固定
        //   url: ''
        // },
        {
          name: '日志采集',
          key: '1', // 固定
          url: '/api/v1/normal/metrics/2',
        },
      ].filter((o) => (config?.menuList || []).includes(o.key))
      : []
  ) as any[];

  // 分组的数据
  let groupsData = [];
  let groupItemsCount = (config?.groupItemsCount || '').split(',').map((s) => Number(s));
  // 不分组的数据
  let itemsData = [];
  if (config.isGroup) {
    for (let i = 0; i < config.groupCount || 0; i++) {
      groupsData.push({
        groupId: i + 1,
        groupName: `group${i + 1}`,
        lists: [],
      });
      for (let j = 0; j < groupItemsCount[i] || 0; j++) {
        groupsData[i].lists.push({
          id: j + 1,
          name: `${i + 1}-${j + 1}`,
        });
      }
    }
  } else {
    for (let i = 0; i < config.itemCount || 0; i++) {
      itemsData.push({
        id: i + 1,
        name: `1-${i + 1}`,
      });
    }
  }

  return (
    <ChartContainer
      reloadModule={{
        reloadIconShow: config.reloadIconShow,
        lastTimeShow: config.reloadIconShow,
      }}
      dragModule={{
        dragItem: <DragItem></DragItem>,
        isGroup: config.isGroup,
        groupsData: config.isGroup ? groupsData : itemsData,
      }}
      indicatorSelectModule={{
        hide: false,
        menuList,
      }}
    ></ChartContainer>
  );
};
