import { EChartOption } from 'echarts/lib/echarts';
import { timeFormat } from '../../constants/time';
import { IMetricPanels } from '../../interface/agent';
import moment from 'moment';

export const LEGEND_HEIGHT = 18;
export const defaultLegendPadding = 10;
export const GRID_HEIGHT = 192;
export const EXPAND_GRID_HEIGHT = 250;
export const TITLE_HEIGHT = 90;

export const baseLineLegend = {
  itemWidth: 12,
  itemHeight: 2,
  icon: 'rect',
  textStyle: {
    lineHeight: LEGEND_HEIGHT,
  },
};

export const baseLineGrid = {
  left: '0',
  right: '2%',
  bottom: '3%',
  top: TITLE_HEIGHT,
  height: GRID_HEIGHT,
  containLabel: true,
};

export const getHeight = (options: EChartOption) => {
  let grid = options ? options.grid as EChartOption.Grid : null;
  if (!options || !grid) grid = baseLineGrid;
  return Number(grid.height) + getLegendHight(options) + Number(grid.top);
};

export const getLegendHight = (options: EChartOption | any) => {
  if (!options) return 0;
  const legendHight = options.legend.textStyle.lineHeight + defaultLegendPadding;
  return legendHight;
};

export const dealMetricPanel = (metricPanelList: IMetricPanels[]) => {
  return metricPanelList.map(ele => {
    const timestamps = ele.metricList[0]?.metricPonitList?.map(p => moment(p.timestamp).format(timeFormat)); // 对应的时间戳
    const titles = ele.metricList?.map(v => { return v.metricName });
    const series = ele.metricList?.map(v => { // 对应的折线图数据
      return {
        name: v.metricName, // 对应的单个折线标题
        type: 'line',
        stack: '总量',
        data: v.metricPonitList.map(p => p.value),  // 对应的单个折线数据
      };
    });
    return {
      title: ele.panelName,
      selfHide: ele.selfHide,
      metricOptions: {
        title: {
          text: ''
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          ...baseLineLegend,
          data: titles, // 对应的折线图
        },
        grid: {
          ...baseLineGrid,
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: timestamps, // 对应的时间戳
        },
        yAxis: {
          type: 'value'
        },
        series,
      }
    };
  })
}


