import React from 'react';
import { SingleChart, Utils } from '@didi/dcloud-design';

interface IProps {
  title: string;
  dataSource: any;
}

const ChartItem = (props: IProps): JSX.Element => {
  const { title, dataSource } = props;
  const { data: propChartData, type, displayUnit } = dataSource;

  const unitEnum = {
    0: '',
    1: 'Byte',
    2: 'MB',
    3: 'MS',
    4: 'S',
    5: '%',
    6: '',
    7: 'NS',
  };

  const unitFormatFn = (val: string | number) => val + unitEnum[displayUnit];

  return (
    <SingleChart
      chartTypeProp="line"
      title={title}
      wrapStyle={{
        width: '100%',
        height: 307,
        background: '#FFFFFF',
        boxShadow: '0 2px 4px 0 rgba(0,0,0,0.01), 0 3px 6px 3px rgba(0,0,0,0.01), 0 2px 6px 0 rgba(0,0,0,0.03)',
        borderRadius: 4,
        marginBottom: 24,
      }}
      option={{
        yAxis: {
          axisLabel: {
            formatter: (value) => `${unitFormatFn(value)}`,
          },
        },
        legend: {
          right: 0,
          formatter: (name: string) => Utils.cutString(name, 10),
        },
        tooltip: {
          formatter: (params: any) => {
            return (
              `<div style="font-size: 12px;color: #212529;line-height: 20px; margin-top: 2px; margin-bottom: 3px;">${params[0].axisValue}</div>` +
              params
                .map((item) => {
                  return `<div style="display: flex; min-width: 140px; justify-content: space-between;line-height: 20px;color: #495057;">
                <div style="margin-right: 20px;"><span style="display:inline-block;margin-right:8px;border-radius:50%;width:6px;height:6px;background-color:${
                  item.color
                };"></span><span>${item.name}</span></div>
                <div>${unitFormatFn(item.value)}</div>
              </div>`;
                })
                .join('')
            );
          },
        },
        grid: {
          right: 133,
        },
      }}
      propChartData={propChartData}
      xAxisCallback={(data) => {
        if (type === 'singleLine') {
          return data?.map((item) => item.timeStampMinute);
        }
        return data?.[0]?.map((item) => item.timeStampMinute);
      }}
      seriesCallback={(data) => {
        if (data) {
          if (type === 'singleLine') {
            return [
              {
                name: data?.[0]?.name,
                data,
              },
            ];
          }
          return (
            data.map((item, index) => {
              return {
                name: data[index]?.[0]?.name,
                data: data[index],
              };
            }) || []
          );
        }
      }}
    ></SingleChart>
  );
};

export default ChartItem;
