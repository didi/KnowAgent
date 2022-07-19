import React, { useState } from 'react';
import { List, Tabs } from '@didi/dcloud-design';
import { getPieChartOption } from './constants';
import { IconFont } from '@didi/dcloud-design';
import { TextRouterLink } from '../utils';
import './style/index.less';
import { Bar, ECOptions } from '../BarCharts/Bar';
const { TabPane } = Tabs;

interface IProps {
  type?: string;
  id: string;
  title: string;
  dataSource?: any;
  tabData?: any[];
  chartClassName?: string;
  height?: number;
  customOptions?: any;
  renderLegend?: any;
}
const className = `dashboard-piechart`;

const PieChart = (props: IProps): JSX.Element => {
  const { id, dataSource = [], title, chartClassName, customOptions, height, tabData, renderLegend } = props;
  const [activeKey, setActiveKey] = useState('error');
  // TODO: 业务放到外层
  const linkTo = (name, item) => {
    if (name.includes('故障')) {
      setActiveKey('error');
    }
    if (name.includes('预警')) {
      setActiveKey('warning');
    }
  };

  const renderChart = () => {
    let totalValue = 0;

    for (const item of dataSource) {
      totalValue += item.value;
    }
    const options = getPieChartOption(dataSource, totalValue, customOptions) as ECOptions;
    return (
      <Bar
        id={id}
        linkTo={linkTo}
        height={height || 393}
        renderLegend={renderLegend}
        totalValue={totalValue}
        legendData={dataSource}
        option={options}
        item={{}}
      />
    );
  };

  const onTabChange = (key: string) => {
    setActiveKey(key);
  };

  const renderTab = () => {
    const clientWidth1440 = document.body.clientWidth === 1440;

    return (
      <Tabs activeKey={activeKey} onChange={onTabChange} className={`tab-panel ${chartClassName} ${clientWidth1440 ? 'width-1440' : ''}`}>
        {tabData.map((item) => (
          <TabPane tab={item.title} key={item.key}>
            <List
              dataSource={item.list}
              renderItem={(row: any) => (
                <div className={`list-item ${chartClassName} ${activeKey}`}>
                  <IconFont className={`link-icon ${chartClassName || ''}`} type="icon-link-o" />
                  <TextRouterLink textLength={26} needToolTip element={row.key} href={item.href} state={item.getState(row)} />
                </div>
              )}
            ></List>
          </TabPane>
        ))}
      </Tabs>
    );
  };

  return (
    <div className={`${className} ${chartClassName}`}>
      <div className={`${className}-header`}>
        <div className={`${className}-header-title`}>{title}</div>
      </div>
      {renderChart()}
      {tabData ? renderTab() : null}
    </div>
  );
};

export default PieChart;
