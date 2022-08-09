import React, { useEffect, useState } from 'react';
import { Tabs } from '@didi/dcloud-design';
import { chartListData } from './config';
import List from './list';
import './style/index.less';
const { TabPane } = Tabs;

const lineCharts = (props: { dashBoardData: Record<string, any> }): JSX.Element => {
  const [dataSource, setDataSource] = useState<any>(null);
  const [activeKey, setActiveKey] = useState(chartListData[0].key);

  useEffect(() => {
    setDataSource(props.dashBoardData);
  }, [props.dashBoardData]);

  return (
    <div className="dashboard-line-chart">
      <Tabs
        activeKey={activeKey}
        onChange={(activeKey) => {
          setActiveKey(activeKey);
        }}
      >
        {chartListData.map((item) => (
          <TabPane tab={item.title} key={item.key}>
            {activeKey === item.key && dataSource && <List dataSource={dataSource} list={item.data as any}></List>}
          </TabPane>
        ))}
      </Tabs>
    </div>
  );
};

export default lineCharts;
