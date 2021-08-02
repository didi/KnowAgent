import { timeFormat } from '../../../constants/time';
import { cutString } from './../../../lib/utils'
import { byteChange, byteToMB } from '../../../lib/utils';
import moment from 'moment';

export const color = ['#5B73F0', '#5998FF', '#3DDCDC', '#21CAB8', '#89D9CA', '#CBD681', '#FFEE8F', '#F3D930', '#F0BC18', '#FF8686']
export const baiFormat = ['agentListCpuUsageTop5', 'agentListCpuLoadTop5'];
export const byteFormat = ['logCollectTaskListMemoryUsageTop5', 'logCollectTaskListCollectBytesTop5', 'agentListMemoryUsageTop5', 'agentListCollectBytesTop5']
export const YFormat = ['agentListFullGcCountTop5', 'agentListRelateLogCollectTasksTop5']

export const valueFormatFn = (value: any, config: any, tool?: boolean) => {
  if(byteFormat.includes(config.api)) {
    return tool ? byteChange(value) : byteToMB(value)
  }
  if (baiFormat.includes(config.api)) {
    return tool ? value * 100 + '%' : value * 100;
  }
  return value;
}


export const createOptions = (config: any, data: any[]) => {
  const title = data?.map(item => item.name);
  const timestamps = !!data ? data[0]?.metricPointList?.map((p) => moment(p.timestamp).format(timeFormat)) : []; // 对应的时间戳
  const series = data?.map(v => { // 对应的折线图数据
    return {
      name: v.name || '', // 对应的单个折线标题
      type: 'line',
      // stack: '总量',
      data: v.metricPointList,  // 对应的单个折线数据
    };
  });
  const option: any = {
    title: {
        text: config.title,
        formatter: (value: any) => {
          return value
        },
    },
    tooltip: {
        trigger: 'axis',
        formatter: (params: any) => {
          // console.log(params)
          let tip = '';
          if (params != null && params.length > 0) {
            tip += params[0].name + '<br />';
            for (let i = 0; i < params.length; i++) {
              tip += params[i].marker + params[i]?.seriesName + ': ' + valueFormatFn(params[i].value, config, true) + ' ' +(params[i].data?.unit || '') + '<br />';
            }
          }
          return tip
        }
    },
    color: color,
    legend: {
        right: '-5',
        top: '49',
        width: 90,
        orient: 'verticalAlign',
        data: title,
        textStyle: {
          color: 'rgba(0,0,0,0.65)'
        },
        icon: 'line',
        tooltip: {
          show: true
        },
        formatter: (name: string) => {
          return cutString(name, 13)
        },
    },
    grid: {
        left: '0%',
        right: '115',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: timestamps, // 对应的时间戳
      axisLabel: {
        formatter: (value: any) => {
          return moment(value).format('HH:mm')
        },
        color: 'rgba(0,0,0,0.45)',
      },
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        },
      },
      axisTick: {
        show: true,
        alignWithLabel: true,
        lineStyle: {
          color: '#DCDFE6'
        },
      }
    },
    yAxis: {
        type: 'value',
        axisLine: {
          show: false,
        },
        axisLabel: {
          show: true,
          color: 'rgba(0,0,0,0.45)',
          formatter: (value: any) => {
            return valueFormatFn(value, config)
          }
        },
        splitLine: {
          lineStyle: {
            color: "#E4E7ED",
            type: 'dashed',
          },
        },
        axisTick: {
          show: false
        },
    },
    series,
    animation: false,
  };

  if (YFormat.includes(config.api)) {
    option.yAxis = {
      ...option.yAxis,
      minInterval: 1,
      splitNumber: 5,
      min: 0,
      max: 5,
      interval: 1
    }
  }
  // try {
  //   let flag = false;
  //   data?.forEach(item => {
  //     item?.metricPointList.forEach((val: any) => {
  //       if (val.value) {
  //         flag = true;
  //       }
  //     })
  //   })
  //   console.log(flag)
  //   if (!flag) {
  //     
  //   }
  // } catch (err) {
  //   console.log(err)
  // }
  // console.log(option)
  return option;
}