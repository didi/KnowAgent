import React, { useState, useEffect } from 'react';
import _, { isArray } from 'lodash';
import { Button, Drawer, IconFont, Utils, SingleChart } from '@didi/dcloud-design';
import moment from 'moment';
import type { LineChartProps } from '@didi/dcloud-design/es/extend/single-chart/LineChart';
import LinkageTable from './linkageTable';
const { EventBus } = Utils;
const busInstance = new EventBus();

export const sortCodeEnum = {
  last: 0,
  min: 1,
  max: 2,
  mean: 3,
  std: 4,
  fiftyFiveQuantile: 5,
  seventyFiveQuantile: 6,
  ninetyFiveQuantile: 7,
  ninetyNineQuantile: 8,
};

const EnlargedChart = (
  props: LineChartProps & {
    requestParams?: any;
    onSave?: any;
  }
): JSX.Element => {
  const { title, requestParams, url, onSave } = props;
  const [rangeTimeArr, setRangeTimeArr] = useState<number[]>();
  const [propParams, setPropParams] = useState<Record<string, any>>();
  //   const [lastTime, setLastTime] = useState<string>(moment().format('YYYY.MM.DD.hh:mm:ss'));
  const [visible, setVisible] = useState(false);
  const [lineData, setlineData] = useState([]);
  const [sortMetricType, setSortMetricType] = useState<number>();
  const [chartData, setChartData] = useState<Record<string, any>>();
  const [clearFlag, setClearFlag] = useState<number>(0);
  const [curXAxisData, setCurXAxisData] = useState<Record<string, any>>(null);
  const [unitDataObj, setUnitDataObj] = useState<Record<string, any>>();

  const typeEnum = {
    1: 'label',
    2: 'multLine',
    3: 'singleLine',
  };

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

  const valueEnum = {
    1: 'b',
    2: 'mb',
    3: 'ms',
    4: 's',
    6: 'date',
    7: 'ns',
  };

  const unitFormatFn = (val, displayUnit?: string) => {
    return val + unitEnum[displayUnit || unitDataObj.displayUnit];
  };
  const valueFormatFn = (value, baseUnit, displayUnit) => {
    // 数据无需转换时直接返回 value
    if (baseUnit === displayUnit) return value;
    // ms 转换为 s
    if (baseUnit === 3 && displayUnit === 4) return value / 1000;
    if (!valueEnum[displayUnit]) {
      return Number(value)?.toFixed(2);
    }
    if (valueEnum[displayUnit] === 'mb') {
      return Utils.transBToMB(value);
    }
    return Number(Utils.formatTimeValueByType(value, valueEnum[baseUnit], valueEnum[displayUnit]))?.toFixed(2);
  };

  const showDrawer = () => {
    setVisible(true);
  };

  const onClose = () => {
    setVisible(false);
  };

  const handleSave = (isClose?: boolean) => {
    const { sortTime: originSortTime } = requestParams;
    const sortTime = curXAxisData ? curXAxisData.timeStampMinute : originSortTime;
    const ConnectChartsParams = JSON.parse(localStorage.getItem('ConnectChartsParams')) || {};
    ConnectChartsParams[propParams.metricCode] = {
      sortTime,
      sortMetricType,
    };
    localStorage.setItem('ConnectChartsParams', JSON.stringify(ConnectChartsParams));

    onSave({
      sortTime,
      sortMetricType,
    });

    isClose && setVisible(false);
  };

  const handleSortChange = (sortObj) => {
    const sortMetricTypeVal = sortCodeEnum[sortObj.columnKey];
    setSortMetricType(sortMetricTypeVal);
    busInstance.emit('singleReload', {
      ...propParams,
      startTime: rangeTimeArr[0],
      endTime: rangeTimeArr[1],
      sortTime: curXAxisData ? curXAxisData.timeStampMinute : requestParams.sortTime,
      sortMetricType: sortMetricTypeVal,
    });
  };

  // const handleRefresh = () => {
  //   setClearFlag((data) => {
  //     return data + 1;
  //   });
  //   busInstance.emit('singleReload', {
  //     ...propParams,
  //     startTime: rangeTimeArr[0],
  //     endTime: rangeTimeArr[1],
  //     sortMetricType,
  //   });
  // };

  // const timeChange = (val) => {
  //   setRangeTimeArr(val);
  //   setClearFlag((data) => {
  //     return data + 1;
  //   });
  //   busInstance.emit('singleReload', {
  //     ...propParams,
  //     startTime: val[0],
  //     endTime: val[1],
  //     sortMetricType,
  //   });
  // };

  useEffect(() => {
    if (visible && requestParams) {
      const { startTime, endTime, sortMetricType, ...rest } = requestParams;
      setPropParams({
        ...rest,
        topN: 0, // 获取全部的数据
      });
      setRangeTimeArr([startTime, endTime]);
      setSortMetricType(sortMetricType);
      busInstance.emit('singleReload', {
        ...rest,
        topN: 0, // 获取全部的数据
        sortMetricType,
        startTime,
        endTime,
      });
    }
  }, [visible, requestParams]);

  const getTableData = (type, lineColor) => {
    // 通过X轴过滤全部数据
    let arr = [];
    if (isArray(chartData)) {
      // 二维数组
      arr = []
        .concat(...chartData)
        .filter((item) => {
          return item.timeMinute === type;
        })
        .map((i, index) => {
          return {
            ...i,
            color: lineColor[index] || '',
          };
        });
    }
    return arr;
  };

  return (
    <>
      <IconFont
        type="icon-shaixuan"
        onClick={showDrawer}
        style={{
          fontSize: 14,
          border: '0.5px solid #CED4DA',
          borderRadius: 2,
          padding: 2,
        }}
      />
      <Drawer
        width={1080}
        title={title}
        placement="right"
        onClose={onClose}
        visible={visible}
        footer={
          <div
            style={{
              textAlign: 'left',
            }}
          >
            <Button onClick={onClose}>取消</Button>
            <Button onClick={() => handleSave(true)} type="primary" style={{ marginLeft: 10 }}>
              保存
            </Button>
          </div>
        }
      >
        {/* <Space>
          <div className="reload-module">
            <Button type="text" icon={<ReloadOutlined />} onClick={handleRefresh}>
              刷新
            </Button>
            <span className="last-time">上次刷新时间: {lastTime}</span>
          </div>
          <TimeModule timeChange={timeChange} rangeTimeArr={rangeTimeArr} />
        </Space> */}
        {visible && (
          <SingleChart
            showHeader={false}
            chartTypeProp="line"
            wrapStyle={{
              width: '100%',
              height: 300,
            }}
            option={{
              tooltip: {
                formatter: (params) => {
                  let str = '';
                  str += `<div style="font-size: 12px;color: #212529;line-height: 20px; margin-top: 4px; margin-bottom: 4px;">${params[0].axisValue}</div>`;
                  const lineColor = params.map((item) => {
                    str += `<div style="display: flex; min-width: 140px; justify-content: space-between;line-height: 20px;color: #495057;">
                  <div style="margin-right: 20px;"><span style="display:inline-block;margin-right:8px;border-radius:50%;width:6px;height:6px;background-color:${item.color
                      };"></span><span>${item.name}</span></div>
                  <div>${unitFormatFn(item.value)}</div>
                </div>`;
                    const color = item.marker?.split('background-color:')[1]?.slice(0, 7);
                    return color;
                  });
                  setlineData && setlineData(getTableData(params[0]?.axisValue, lineColor));
                  setCurXAxisData({
                    index: params[0]?.dataIndex,
                    value: params[0]?.axisValue,
                    timeStampMinute: params[0]?.data?.timeStampMinute,
                  });
                  return str;
                },
                triggerOn: 'click',
                alwaysShowContent: true,
              },
              yAxis: {
                axisLabel: {
                  formatter: (value) => `${unitFormatFn(value)}`,
                },
              },
              legend: {
                top: 0,
                right: 0,
                formatter: (name: string) => Utils.cutString(name, 12),
              },
              grid: {
                top: 10,
                bottom: 20,
                right: 133,
              },
            }}
            eventBus={busInstance}
            url={url}
            resCallback={(res: any) => {
              const { type, baseUnit, displayUnit, lableValue, singleLineChatValue, multiLineChatValue } = res;
              if (
                !lableValue &&
                (!singleLineChatValue ||
                  !Array.isArray(singleLineChatValue?.metricPointList) ||
                  singleLineChatValue?.metricPointList.length < 1) &&
                (!multiLineChatValue || multiLineChatValue.length < 1)
              ) {
                return null;
              }
              const data =
                type === 3
                  ? singleLineChatValue?.metricPointList?.map((item: any) => {
                    return {
                      ...item,
                      name: singleLineChatValue?.name,
                      timeMinute: moment(item.timeStampMinute).format('HH:mm'),
                      value: valueFormatFn(item.last, baseUnit, displayUnit),
                      last: valueFormatFn(item.last, baseUnit, displayUnit) + unitEnum[displayUnit],
                    };
                  })
                  : multiLineChatValue?.map((item) => {
                    return item?.metricPointList.map((el) => {
                      return {
                        ...el,
                        timeMinute: moment(el.timeStampMinute).format('HH:mm'),
                        name: item?.name,
                        value: valueFormatFn(el.last, baseUnit, displayUnit),
                        last: valueFormatFn(el.last, baseUnit, displayUnit) + unitEnum[displayUnit],
                      };
                    });
                  });
              setUnitDataObj({
                baseUnit,
                displayUnit,
              });
              setChartData(data);
              type === 3 ? setlineData([data[data.length - 1]]) : setlineData(data.map((item) => item[item.length - 1]));
              return {
                data,
                type: typeEnum[type],
              };
            }}
            curXAxisData={curXAxisData}
            xAxisCallback={({ type, data }) => {
              if (data) {
                if (type === 'singleLine') {
                  return data?.map((item) => item.timeMinute);
                }
                return data?.[0]?.map((item) => item.timeMinute);
              }
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
        )}
        {visible && (
          <LinkageTable
            clearFlag={clearFlag}
            dispatchSort={handleSortChange}
            rangeTimeArr={rangeTimeArr}
            requestParams={requestParams}
            lineData={lineData}
            chartData={chartData}
          />
        )}
      </Drawer>
    </>
  );
};

export default EnlargedChart;
