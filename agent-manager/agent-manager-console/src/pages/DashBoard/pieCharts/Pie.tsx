import React from 'react';
import { List, SingleChart, Tabs } from '@didi/dcloud-design';
import { createOption, getPieChartOption } from './constants';
import { IconFont } from '@didi/dcloud-design';
import { TextRouterLink } from '../utils';
import { Link } from 'react-router-dom';
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
}
const className = `dashboard-piechart`;

const PieChart = (props: IProps): JSX.Element => {
  const { id, dataSource = [], title, chartClassName, customOptions, height, tabData } = props;

  const renderChart = () => {
    let totalValue = 0;

    for (const item of dataSource) {
      totalValue += item.value;
    }
    const options = getPieChartOption(dataSource, totalValue, customOptions) as ECOptions;
    return <Bar id={id} height={height || 393} option={options} item={{}} />;
  };

  const renderTab = () => {
    return (
      <Tabs className={`tab-panel ${chartClassName}`}>
        {tabData.map((item) => (
          <TabPane tab={item.title} key={item.key}>
            <List
              dataSource={item.list}
              renderItem={(row: any) => (
                <TextRouterLink textLength={26} needToolTip element={row.key} href={item.href} state={item.getState(row)} />
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
