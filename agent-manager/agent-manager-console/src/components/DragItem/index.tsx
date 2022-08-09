import React, { useState } from 'react';
import { Utils } from '@didi/dcloud-design';
import DragItemChart from '../DragItemChart';
import moment from 'moment';
export const unitEnum = {
  0: '',
  1: 'Byte',
  2: 'MB',
  3: 'MS',
  4: 'S',
  5: '%',
  6: '',
  7: 'NS',
};

export const valueEnum = {
  1: 'b',
  2: 'mb',
  3: 'ms',
  4: 's',
  6: 'date',
  7: 'ns',
};

export const typeEnum = {
  1: 'label',
  2: 'multLine',
  3: 'singleLine',
};
interface IProps {
  code?: number | string;
  eventBus?: any;
  title?: string;
  requstUrl?: string;
  type?: string;
  agent?: string;
  hostName?: string;
  logCollectTaskId?: string;
  showLargeChart?: boolean;
  metricDesc?: string;
}

const DragItem = (props: IProps): JSX.Element => {
  const { code: metricCode, eventBus, title, requstUrl, type, agent, hostName, logCollectTaskId, showLargeChart, metricDesc } = props;
  const [unitDataObj, setUnitDataObj] = useState<Record<string, any>>();
  const getPropParams = () => {
    const ConnectChartsParams = JSON.parse(localStorage.getItem('ConnectChartsParams'));
    const chartParams = ConnectChartsParams?.metricCode;
    // 排序字段 默认平均值
    const sortMetricType = chartParams?.sortMetricType || 0;
    const sortTime = chartParams?.sortTime || '';
    return {
      type,
      metricCode,
      sortTime,
      sortMetricType,
      agent: agent || '',
      hostName: hostName || '',
      logCollectTaskId: logCollectTaskId || '',
    };
  };

  const reqCallback = (params) => {
    const { dateStrings, type, agent, hostName, logCollectTaskId, pathId, sortTime, ...rest } = params;
    const changeObj =
      type === '0'
        ? {
            hostName: agent,
            pathId: '',
            logCollectTaskId: '',
          }
        : {
            hostName: hostName || null,
            pathId: pathId || '',
            logCollectTaskId: logCollectTaskId || '',
          };

    const mergeParams = {
      ...rest,
      ...changeObj,
      sortTime: sortTime || moment(dateStrings?.[1]).startOf('minute').unix() * 1000,
      startTime: dateStrings?.[0],
      endTime: dateStrings?.[1],
      topN: 6, // 获取top几的数据
    };
    return mergeParams;
  };

  const transformNS = (val) => {
    if (val > 1000) {
      val = val / 1000;
      if (val > 1000) {
        val = val / 1000;
        if (val > 1000) {
          val = val / 1000;
          return val.toFixed(2) + 'S';
        } else {
          return val.toFixed(2) + 'MS';
        }
      } else {
        return val.toFixed(2) + 'μs';
      }
    }
    return val + 'NS';
  };

  const transformByte = (val) => {
    if (val > 1024) {
      val = val / 1024;
      if (val > 1024) {
        val = val / 1024;
        if (val > 1024) {
          val = val / 1024;
          return val.toFixed(2) + 'GB';
        } else {
          return val.toFixed(2) + 'MB';
        }
      } else {
        return val.toFixed(2) + 'KB';
      }
    }
    return val + 'Byte';
  };

  const unitFormatFn = (val, type?: string) => {
    if (type === 'tip') {
      if (unitEnum[unitDataObj.displayUnit] === 'MB') {
        if (val > 1024) {
          val = val / 1024;
          return val.toFixed(2) + 'GB';
        } else {
          return val + 'MB';
        }
      }
      if (unitEnum[unitDataObj.displayUnit] === 'NS') {
        return transformNS(val);
      }
      if (unitEnum[unitDataObj.displayUnit] === 'Byte') {
        return transformByte(val);
      }
    }
    return val + unitEnum[unitDataObj.displayUnit];
  };

  const valueFormatFn = (value, baseUnit, displayUnit) => {
    if (displayUnit == 0 || valueEnum[baseUnit] === valueEnum[displayUnit]) {
      return value;
    }
    if (!valueEnum[displayUnit]) {
      return Number(value)?.toFixed(2);
    }
    if (valueEnum[displayUnit] === 'mb') {
      return Utils.transBToMB(value);
    }
    if (baseUnit === 3 && displayUnit === 4) {
      return value / 1000;
    }
    return Number(Utils.formatTimeValueByType(value, valueEnum[baseUnit], valueEnum[displayUnit]))?.toFixed(2);
  };

  const getLabelValue = ({ lableValue, baseUnit, displayUnit }) => {
    const unit = valueEnum[displayUnit];
    let value: number | string =
      unit === 'date'
        ? Utils.formatDate(lableValue, 'HH : mm : ss')
        : `${valueFormatFn(lableValue, baseUnit, displayUnit)} ${unitEnum[displayUnit]}`;
    // 若当前单位为 ms，转换单位为s，判断能否进一步转换，最高单位为日
    if (baseUnit === 3 && displayUnit === 4) {
      value = lableValue / 1000;
      if (value > 60) {
        value = value / 60;
        if (value > 60) {
          value = value / 60;
          if (value > 24) {
            value = value / 24;
            value = Number(value)?.toFixed(2) + 'D';
          } else {
            value = Number(value)?.toFixed(2) + 'h';
          }
        } else {
          value = Number(value)?.toFixed(2) + 'Min';
        }
      } else {
        value = value + 'S';
      }
    }
    const subValue = unit === 'date' ? Utils.formatDate(lableValue, 'YYYY-MM-DD') : '';
    return {
      value,
      subValue,
    };
  };

  return (
    <DragItemChart
      title={title}
      metricDesc={metricDesc}
      wrapStyle={{
        width: '100%',
        height: 307,
      }}
      option={{
        tooltip: {
          formatter: (params: any[]) => {
            return (
              `<div style="font-size: 12px;color: #212529;line-height: 20px; margin-top: 2px; margin-bottom: 3px;">${params[0].axisValue}</div>` +
              params
                .map((item) => {
                  return `<div style="display: flex; min-width: 140px; justify-content: space-between;line-height: 20px;color: #495057;">
                    <div style="margin-right: 20px;"><span style="display:inline-block;margin-right:8px;border-radius:50%;width:6px;height:6px;background-color:${
                      item.color
                    };"></span><span>${item.name}</span></div>
                    <div>${unitFormatFn(item.value, 'tip')}</div>
                  </div>`;
                })
                .join('')
            );
          },
        },
        legend: {
          right: 0,
          formatter: (name: string) => Utils.cutString(name, 10),
        },
        yAxis: {
          axisLabel: {
            formatter: (value: string) => `${unitFormatFn(value)}`,
          },
        },
        grid: {
          right: 133,
        },
      }}
      showLargeChart={showLargeChart}
      connectEventName={'connect'}
      url={requstUrl}
      eventBus={eventBus}
      reqCallback={reqCallback}
      propParams={getPropParams()}
      resCallback={(res: any) => {
        const { type, baseUnit, displayUnit, lableValue, singleLineChatValue, multiLineChatValue } = res;
        if (
          !lableValue &&
          (!singleLineChatValue ||
            !Array.isArray(singleLineChatValue?.metricPointList) ||
            singleLineChatValue?.metricPointList.length < 1) &&
          (!multiLineChatValue || multiLineChatValue.length < 1)
        ) {
          return {
            data: null,
          };
        }
        const data =
          type === 1
            ? getLabelValue({
                lableValue,
                baseUnit,
                displayUnit,
              })
            : type === 3
            ? singleLineChatValue?.metricPointList?.map((item: any) => {
                return {
                  ...item,
                  name: singleLineChatValue?.name,
                  timeStampMinute: moment(item.timeStampMinute).format('HH:mm'),
                  value: valueFormatFn(item.last, baseUnit, displayUnit),
                };
              })
            : multiLineChatValue?.map((item) => {
                return item?.metricPointList?.map((el) => {
                  return {
                    ...el,
                    timeStampMinute: moment(el.timeStampMinute).format('HH:mm'),
                    name: item?.name,
                    value: valueFormatFn(el.last, baseUnit, displayUnit),
                  };
                });
              });

        setUnitDataObj({
          baseUnit,
          displayUnit,
        });
        return {
          data,
          type: typeEnum[type],
        };
      }}
      xAxisCallback={({ type, data }) => {
        if (type === 'singleLine') {
          return data?.map((item) => item.timeStampMinute);
        }
        return data?.[0]?.map((item) => item.timeStampMinute);
      }}
      seriesCallback={({ data, type }) => {
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
            data.map((item: any, index: number) => {
              return {
                name: data[index]?.[0]?.name,
                data: data[index],
              };
            }) || []
          );
        }
      }}
    />
  );
};

export default DragItem;
