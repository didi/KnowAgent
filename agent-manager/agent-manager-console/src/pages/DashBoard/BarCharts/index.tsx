import { Empty, Utils } from '@didi/dcloud-design';
import React, { useEffect, useState } from 'react';
import { Bar, ECOptions } from './Bar';
import './index.less';

const valueEnum = {
  1: 'b',
  2: 'mb',
  3: 'ms',
  4: 's',
  6: 'date',
  7: 'ns',
};

const valueFormatFn = (value, baseUnit, displayUnit) => {
  if (displayUnit == 0 || valueEnum[baseUnit] === valueEnum[displayUnit]) {
    return value;
  }
  if (!valueEnum[displayUnit]) {
    return value?.toFixed(2);
  }
  if (valueEnum[displayUnit] === 'mb') {
    return Utils.transBToMB(value);
  }
  if (baseUnit === 3 && displayUnit === 4) {
    return value / 1000;
  }
  return Number(Utils.formatTimeValueByType(value, valueEnum[baseUnit], valueEnum[displayUnit]))?.toFixed(2);
};

const BarCharts = (props: {
  barList: any[];
  type: string;
  dashBoardData: Record<string, any>;
  getKeys?: any;
  linkTo?: any;
  needTrigger?: boolean;
}): JSX.Element => {
  const hasData = (item) => {
    return !!(props.dashBoardData?.[item.key]?.histogramChatValue || []).length;
  };
  const getBarOption = (item) => {
    const data = props.dashBoardData?.[item.key]?.histogramChatValue || [];
    const row = props.dashBoardData?.[item.key];
    let xAxisData = data.map((item) => item.key);
    const clientWidth1920 = document.body.clientWidth === 1920;

    if (props.getKeys) {
      xAxisData = props.getKeys(data);
    }

    const seriesData = data.map((item) => item.value);

    const options = {
      title: {
        subtext: item.title,
        textStyle: {
          color: '#212529',
          fontFamily: 'PingFangSC-Regular',
          fontSize: 14,
        },
        left: 8,
      },
      xAxis: {
        type: 'category',
        data: xAxisData,
        axisTick: {
          lineStyle: {
            color: '#D3D8E4',
          },
        },
        triggerEvent: props?.needTrigger,
        axisLine: {
          lineStyle: {
            color: '#D3D8E4',
          },
        },
        fontFamily: 'PingFangSC-Regular',
        axisLabel: {
          color: '#464646',
          interval: 0,
          overflow: 'truncate',
          width: !clientWidth1920 ? 100 : 145,
          ellipsis: '...',
        },
      },
      yAxis: {
        type: 'value',
        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: '#D3D8E4',
          },
        },
        axisLabel: {
          color: '#464646',
          formatter: (value) => {
            return item.formatter ? item.formatter(value) : valueFormatFn(value, row.baseUnit, row.displayUnit);
          },
        },
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params: any) => {
          const value = params[0].value;
          return item.formatter ? '' + item.formatter(value) : '' + valueFormatFn(value, row.baseUnit, row.displayUnit);
        },
      },
      grid: {
        top: 50,
        left: 16,
        right: 15,
        bottom: 11,
        containLabel: true,
      },
      series: [
        {
          data: seriesData,
          type: 'bar',
          barWidth: 28,
          showBackground: true,
          backgroundStyle: {
            color: '#E5E9F3',
            opacity: 0.4,
          },
          itemStyle: {
            color: '#556EE6',
          },
        },
      ],
    } as ECOptions;

    return options;
  };

  return (
    <>
      <div className="dashboard-bar-chart">
        <h3 className={`title`}>{props.type}</h3>
        <div className="content">
          {props.barList.map((item) =>
            hasData(item) ? (
              <Bar id={item.key} key={item.key} className={'bar-item'} item={item} linkTo={props.linkTo} option={getBarOption(item)} />
            ) : (
              <Empty className="bar-item" />
            )
          )}
        </div>
      </div>
    </>
  );
};

export default BarCharts;
