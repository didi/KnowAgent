import { EChartOption } from 'echarts/lib/echarts';
import { timeFormat } from '../../constants/time';
import { IMetricPanels } from '../../interface/agent';
import moment from 'moment';
import { byteChange, byteToMB, timeStamp, msecondToSecond, nsTo, ToMs, Tous } from './../../lib/utils';

export const LEGEND_HEIGHT = 18;
export const defaultLegendPadding = 10;
export const GRID_HEIGHT = 192;
export const EXPAND_GRID_HEIGHT = 250;
export const TITLE_HEIGHT = 70;
export const AGENT_TITLE_HEIGHT = 40
export const default_HEIGHT = 40

export const booleanFormat = ['isExistCollectPathChaos', 'islogChopFault', 'isCollectPath'];
export const byteFormat = ['memoryUsage', 'exitSendTraffic', 'inletCollectTraffic', 'logReadBytes', 'logSendBytes'];
export const timesFormat = ['HealthMinCollectBusineTime'];
export const toS = ['HealthMaxDelay'];
export const toolTime = ['logReadConsuming', 'logSendConsuming', 'logFlushMaxConsuming', 'logFlushMeanConsuming', 'logflushMinConsuming', 'logSendMinConsuming', 'logSendMeanConsuming', 'logSendMaxConsuming']
export const Yformat = ['HealthAbnormTrunca', 'logFlushFailTimes', 'DataFilterTimes', 'memoryUsage', 'logFaultOutPutBar', 'fullGcTimes']
export const nsToFormat = ['logEventMaxConsuming', 'logEventMeanConsuming', 'logeventMinConsuming']
export const toUs = ['logSendMinConsuming', 'logSendMeanConsuming', 'logSendMaxConsuming']

export const valueFormatFn = (value: any, ele: IMetricPanels, tool?: boolean) => {
  if (booleanFormat.includes(ele.api)) {
    if (value === 1) {
      return '存在'
    }
    if (value === 0) {
      return '不存在'
    }
    return ''
  } 
  if(timesFormat.includes(ele.api)) {
    return `${moment(value).format('YYYY-MM-DD')} \n${moment(value).format('HH:mm')}`
  }
  if(byteFormat.includes(ele.api)) {
    return tool ? byteChange(value) : byteToMB(value)
  }
  if(toolTime.includes(ele.api) && tool) {
    return nsTo(value);
  }
  if(toUs.includes(ele.api)) {
    return Tous(value);
  }
  if(toolTime.includes(ele.api)) {
    return ToMs(value);
  }
  if (toS.includes(ele.api)) {
    return tool ? timeStamp(value) : msecondToSecond(value);
  }
  if (nsToFormat.includes(ele.api) && tool) {
    return nsTo(value);
  }
  if (ele.api === 'HealthLimitTime' && tool) {
    return value + '秒'
  }
  return value;
}

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
  right: '6%',
  bottom: '3%',
  top: TITLE_HEIGHT,
  height: GRID_HEIGHT,
  containLabel: true,
};

export const getHeight = (options: EChartOption) => {
  let grid = options ? options.grid as EChartOption.Grid : null;
  if (!options || !grid) grid = baseLineGrid;
  // return Number(grid.height) + getLegendHight(options) + Number(grid.top);
  return Number(grid.height) + Number(grid.top) + default_HEIGHT;
};

export const getLegendHight = (options: EChartOption | any) => {
  if (!options) return 0;
  const legendHight = options.legend.textStyle.lineHeight + defaultLegendPadding;
  return legendHight;
};
const colorList = ['#427CB1','#fff', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3']
export const pieOption = (ele: IMetricPanels, data: any) => {
  return {
    tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          return params.name
        }
    },
    color: ['#4382E9', '#D5DBE8'],
    grid: {
      ...baseLineGrid
    },
    series: [
        {
            name: ele.title,
            type: 'pie',
            radius: '50%',
            data: data.map((item: any) => {
              return {
                name: `${item.name}: ${item.value}个`,
                value: item.value
              }
            }),
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ],
    animation: false,
  }
}

export const dealMetricPanel = (ele: IMetricPanels, data: any) => {
  if (ele.isPie) {
    return pieOption(ele, data)
  }
  const timestamps = data?.metricPointList?.map((p) => moment(p.timestamp).format(timeFormat)); // 对应的时间戳
  const series = [
    {
      name: data.name || '', // 对应的单个折线标题
      type: 'line',
      // stack: '总量',
      data: data.metricPointList.map(p => ({
        toolName: data.name || '',
        unit: ele.unit,
        ...p
      })),  // 对应的单个折线数据
    },
  ];
  const option: any = {
    // title: {
    //   text: ''
    // },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        let tip = '';
        if (params != null && params.length > 0) {
          tip += params[0].name + '<br />';
          for (let i = 0; i < params.length; i++) {
            tip += params[i].marker + params[i].data?.toolName + ': ' + valueFormatFn(params[i].value, ele, true) + ' ' +(params[i].data?.unit || '') + '<br />';
          }
        }
        return tip
      }
    },
    grid: {
      ...baseLineGrid,
      // top: AGENT_TITLE_HEIGHT
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: timestamps, // 对应的时间戳
      axisLabel: {
        rotate: -55,
        formatter: (value: any) => {
          return moment(value).format('HH:mm')
        }
      }
    },
    yAxis: {
      type: `value`,
      scale: true,
      axisLabel: {
        formatter: (value: any) => {
          return valueFormatFn(value, ele)
        }
      },
      data: series
    },
    series,
    animation: false,
  }
  if (booleanFormat.includes(ele.api)) {
    option.yAxis.splitNumber = 1;
    option.yAxis.interval = 1;
  }
  if (Yformat.includes(ele.api)) {
    console.log(ele.api)
    option.yAxis = {
      ...option.yAxis,
      minInterval: 1,
      splitNumber: 5,
      min: 0,
      max: 5,
      interval: 1
    }
  }
  return option;
}

export const newdealMetricPanel = (ele: IMetricPanels, data: any, judgeUrl: boolean): any => {
  if (ele.isPie) {
    return pieOption(ele, data)
  }
  let timestamps: any = []
  if (data && data.length) {
    data.forEach((item: any) => {
      item.metricPointList.forEach((p) => {
        if (!timestamps.includes(p.timestamp)) {
          timestamps.push(p.timestamp)
        }
      })
    })
    // timestamps = timestamps.map((p) => moment(p).format(timeFormat))
  }
  // data?.length ? data[0]?.metricPointList?.map((p) => moment(p.timestamp).format(timeFormat)) : []; // 对应的时间戳
  const series = data?.map(v => { // 对应的折线图数据
    return {
      name: v.name || '', // 对应的单个折线标题
      type: 'line',
      // stack: '总量',
      data: v.metricPointList.map(p => ({
        toolName: v.name || '',
        unit: ele.unit,
        ...p
      })),  // 对应的单个折线数据
    };
  });
  const option: any = {
    // title: {
    //   text: ''
    // },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        let tip = '';
        if (params != null && params.length > 0) {
          tip += moment(Number(params[0].name)).format(timeFormat) + '<br />';
          for (let i = 0; i < params.length; i++) {
            tip += params[i].marker + params[i].data?.toolName + ': ' + valueFormatFn(params[i].value, ele, true) + ' ' +(params[i].data?.unit || '') + '<br />';
          }
        }
        return tip
      }
    },
    grid: {
      ...baseLineGrid,
      [!judgeUrl ? 'top': '']: AGENT_TITLE_HEIGHT
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: timestamps, // 对应的时间戳
      axisLabel: {
        rotate: -55,
        formatter: (value: any) => {
          return moment(Number(value)).format('HH:mm')
        }
      }
    },
    yAxis: {
      type: `value`,
      scale: true,
      axisLabel: {
        formatter: (value: any) => {
          return valueFormatFn(value, ele)
        }
      },
      data: series
    },
    series,
    animation: false,
  }
  if (booleanFormat.includes(ele.api)) {
    option.yAxis.splitNumber = 1;
    option.yAxis.interval = 1;
  }
  if (Yformat.includes(ele.api)) {
    option.yAxis = {
      ...option.yAxis,
      minInterval: 1,
      splitNumber: 5,
      min: 0,
      max: 5,
      interval: 1
    }
  }
  return option;
};
