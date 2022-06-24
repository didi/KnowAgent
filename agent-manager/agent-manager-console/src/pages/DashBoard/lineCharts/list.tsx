import React from 'react';
import { Container, Utils } from '@didi/dcloud-design';
import './style/index.less';
import moment from 'moment';
import ChartItem from './Item';
interface IProps {
  dataSource: Record<string, any>;
  list: [];
}

const LineChartList = (props: IProps): JSX.Element => {
  const { dataSource, list } = props;

  const valueEnum = {
    1: 'b',
    2: 'mb',
    3: 'ms',
    4: 's',
    6: 'date',
    7: 'ns',
  };

  const typeEnum = {
    1: 'label',
    2: 'multLine',
    3: 'singleLine',
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

  const getChartData = (api: string) => {
    if (dataSource[api]) {
      try {
        const { type, baseUnit, displayUnit, lableValue, singleLineChatValue, multiLineChatValue } = dataSource[api];
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
          type === 3
            ? singleLineChatValue?.metricPointList?.map((item: any) => {
                return {
                  ...item,
                  timeStampMinute: moment(item.timeStampMinute).format('HH:mm'),
                  name: singleLineChatValue?.name,
                  value: valueFormatFn(item.last, baseUnit, displayUnit),
                };
              })
            : multiLineChatValue?.map((item) => {
                return (
                  Array.isArray(item?.metricPointList) &&
                  item?.metricPointList?.map((el) => {
                    return {
                      ...el,
                      timeStampMinute: moment(el.timeStampMinute).format('HH:mm'),
                      // name: el.logCollectTaskId || el.device || el.hostName || el.path,
                      name: item.name,
                      value: valueFormatFn(el.last, baseUnit, displayUnit),
                    };
                  })
                );
              });
        return {
          data,
          baseUnit,
          displayUnit,
          type: typeEnum[type],
        };
      } catch (error) {
        console.log(error);
      }
    }
  };

  return (
    <Container gutter={24} grid={12}>
      {list.map((item: any) => {
        return <div key={item.title}>{getChartData(item.api) && <ChartItem title={item.title} dataSource={getChartData(item.api)} />}</div>;
      })}
    </Container>
  );
};

export default LineChartList;
