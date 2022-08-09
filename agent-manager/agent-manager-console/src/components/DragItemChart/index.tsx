import React, { useState, useEffect } from 'react';
import { SingleChart, Spin, Tooltip } from '@didi/dcloud-design';
import type { LineChartProps } from '@didi/dcloud-design/es/extend/single-chart/LineChart';
import type { PieChartProps } from '@didi/dcloud-design/es/extend/single-chart/PieChart';
import { Popover } from '@didi/dcloud-design';
import EnlargedChart from './EnlargedChart';
import { queryChartData } from './service';
import './style/index.less';

type IChartProps = 'singleLine' | 'multLine' | 'label';

function Chart(props: LineChartProps & PieChartProps & { metricDesc: string }): JSX.Element {
  const { propParams, url, reqCallback, resCallback, showLargeChart, title, metricDesc, ...rest } = props;
  const [chartData, setChartData] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [requestParams, setRequestParams] = useState<any>(null);
  const [chartType, setChartType] = useState<IChartProps>('singleLine');
  const { eventBus } = props;
  const getChartData = async (variableParams?: any) => {
    try {
      setLoading(true);
      const mergeParams = {
        ...propParams,
        ...variableParams,
      };
      const params = reqCallback ? reqCallback(mergeParams) : mergeParams;
      setRequestParams(params);
      const res = await queryChartData(url, params);
      if (res) {
        const mergeResult = resCallback ? resCallback(res) : res;
        if (mergeResult.data) {
          setChartData({
            data: mergeResult.data,
            type: mergeResult.type,
          });
          setChartType(mergeResult.type);
        } else {
          setChartData(null);
        }
      }
      setLoading(false);
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  };

  const updateChartData = async (params) => {
    try {
      setLoading(true);
      setRequestParams(params);
      const res = await queryChartData(url, params);
      if (res) {
        const mergeResult = resCallback ? resCallback(res) : res;
        if (mergeResult.data) {
          setChartData({
            data: mergeResult.data,
            type: mergeResult.type,
          });
          setChartType(mergeResult.type);
        } else {
          setChartData(null);
        }
      }
      setLoading(false);
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  };

  const renderRightHeader = () => {
    return (
      showLargeChart && (
        <EnlargedChart
          {...props}
          onSave={(arg) => {
            updateChartData({
              ...requestParams,
              ...arg,
            });
          }}
          requestParams={requestParams}
        ></EnlargedChart>
      )
    );
  };

  useEffect(() => {
    // 区分不同图表的getChartData方法，在组件卸载时删掉指定的图表chartReload事件
    getChartData['type'] = props.title;
    eventBus?.on('chartReload', getChartData);
    return () => {
      eventBus.offByType('chartReload', getChartData);
    };
  }, [props.title]);

  useEffect(() => {
    if (props.connectEventName) {
      const ConnectChartsParams = JSON.parse(localStorage.getItem('ConnectChartsParams')) || {};
      if (ConnectChartsParams && ConnectChartsParams[propParams.metricCode]) {
        delete ConnectChartsParams[propParams.metricCode];
        localStorage.setItem('ConnectChartsParams', JSON.stringify(ConnectChartsParams));
      }
    }
  }, []);

  const renderContent = () => {
    let content: React.ReactElement;
    if (chartType === 'singleLine' || chartType === 'multLine') {
      content = (
        <SingleChart
          chartTypeProp="line"
          {...rest}
          optionMergeProps={{ notMerge: true }}
          propChartData={chartData}
          renderRightHeader={renderRightHeader}
        ></SingleChart>
      );
    } else {
      const popoverContent = (
        <div>
          <p>{chartData?.data?.value}</p>
          <p>{chartData?.data?.subValue}</p>
        </div>
      );
      content = (
        <Popover content={popoverContent} title={title} getPopupContainer={(trigger) => trigger.parentElement}>
          <p className="single-label-content-value">{chartData?.data?.value}</p>
          <p className="single-label-content-subValue">{chartData?.data?.subValue}</p>
        </Popover>
      );
    }

    return (
      <div
        style={{
          ...props.wrapStyle,
          position: 'relative',
          width: '100%',
          opacity: loading ? 0 : 1,
        }}
      >
        <div className="single-label-header">
          <div className="header-title">
            <Tooltip
              overlayClassName="long-title-tip"
              title={
                <>
                  {title}
                  <br />
                  {metricDesc}
                </>
              }
            >
              {title}
            </Tooltip>
          </div>
        </div>
        <div className="single-label-content">{content}</div>
      </div>
    );
  };

  return <Spin spinning={loading}>{renderContent()}</Spin>;
}

export default Chart;
