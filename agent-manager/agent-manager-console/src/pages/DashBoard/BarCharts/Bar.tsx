import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import * as echarts from 'echarts/core';

import {
  BarChart,
  // 系列类型的定义后缀都为 SeriesOption
  BarSeriesOption,
  LineChart,
  LineSeriesOption,
} from 'echarts/charts';

import {
  TitleComponent,
  // 组件类型的定义后缀都为 ComponentOption
  TitleComponentOption,
  TooltipComponent,
  TooltipComponentOption,
  GridComponent,
  GridComponentOption,
  LegendComponent,
  LegendComponentOption,
  MarkLineComponent,
  MarkLineComponentOption,
  DataZoomComponent,
  DataZoomComponentOption,
  ToolboxComponent,
  ToolboxComponentOption,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import throttle from 'lodash/throttle';
import { pieColors } from '../pieCharts/constants';
import { numberToFixed } from '../../../lib/utils';
import { Tooltip } from '@didi/dcloud-design';

// 通过 ComposeOption 来组合出一个只有必须组件和图表的 Option 类型
export type ECOptions = echarts.ComposeOption<
  | BarSeriesOption
  | LineSeriesOption
  | TitleComponentOption
  | TooltipComponentOption
  | GridComponentOption
  | LegendComponentOption
  | MarkLineComponentOption
  | DataZoomComponentOption
  | ToolboxComponentOption
>;

// 注册必须的组件
echarts.use([
  TitleComponent,
  LegendComponent,
  TooltipComponent,
  GridComponent,
  BarChart,
  LineChart,
  CanvasRenderer,
  MarkLineComponent,
  DataZoomComponent,
  ToolboxComponent,
]);

/*
 *@ 教程 https://echarts.apache.org/zh/index.html
 *@ 配置按需引入
 */
export interface ILine {
  id: string;
  option: ECOptions;
  item: any;
  className?: string;
  width?: number | string;
  height?: number;
  linkTo?: any;
  renderLegend?: any;
  legendData?: any[];
  totalValue?: number;
}

export const Bar = (props: ILine) => {
  const { option, height, id, linkTo, item, legendData, className, totalValue } = props;
  const [legendChecked, setLegendChecked] = useState<Record<string, boolean>>({});

  const myChart = useRef(null);

  useEffect(() => {
    myChart.current = echarts.init(document.getElementById(props.id) as HTMLElement);

    myChart.current.on('click', function (params) {
      linkTo && linkTo(params.value, item);
    });

    myChart.current.on('mouseover', function (params) {
      if (params.componentType === 'xAxis') {
        const tooltip = document.getElementById(`extension-${props.id}`);

        tooltip.classList.add('x-mouse-in');
        tooltip.style.top = '-6px';
        tooltip.style.left = `${params.event.offsetX - 10}px`;
        tooltip.style.display = 'inline';
        tooltip.innerText = params.value;
      }
    });

    myChart.current.on('mouseout', function (params) {
      if (params.componentType === 'xAxis') {
        const tooltip = document.getElementById(`extension-${props.id}`);

        tooltip.style.display = 'none';
      }
    });

    return () => {
      myChart.current?.dispose();
      document.getElementById(props.id) && echarts.dispose(document.getElementById(props.id));
    };
  }, [props.id]);

  useEffect(() => {
    option && myChart.current?.setOption(option, true);

    const resize = throttle(() => {
      const el: HTMLElement = document.getElementById(id);
      // 表示该dom未进入可视区
      if (!el.getBoundingClientRect().width) {
        return;
      }
      myChart.current?.resize();
    }, 300);

    window.addEventListener('resize', resize);

    return () => {
      window.removeEventListener('resize', resize);
    };
  }, [option]);

  const renderLegends = (chartInstance) => {
    const dispatchEchartsAction = (name) => {
      chartInstance.dispatchAction({
        type: !legendChecked[name] ? 'legendUnSelect' : 'legendSelect',
        name,
      });
      setLegendChecked({
        ...legendChecked,
        [name]: !legendChecked[name],
      });
    };

    const hoverOrLeave = (name: string, type: string) => {
      chartInstance.dispatchAction({
        type: type === 'over' ? 'highlight' : 'downplay',
        name,
      });
    };
    const clientWidth1440 = document.body.clientWidth === 1440;

    return (
      <div className={`custom-legend ${clientWidth1440 ? 'width-1440' : ''}`}>
        {legendData.map((item, index) => (
          <div
            className="item"
            onMouseOver={() => hoverOrLeave(item.name, 'over')}
            onMouseLeave={() => hoverOrLeave(item.name, 'leave')}
            onClick={() => dispatchEchartsAction(item.name)}
            key={item.name}
          >
            <div className="dot">
              <div className="rect" style={{ background: legendChecked[item.name] ? '#ccc' : pieColors[index % 9] }}></div>
              <Tooltip title={item.name}>
                <div
                  className={`title ${clientWidth1440 ? 'width-1440' : ''}`}
                  style={{ color: legendChecked[item.name] ? '#ccc' : '#495057' }}
                >
                  <span>{item.name}</span>
                </div>
              </Tooltip>
            </div>
            <span className="unit" style={{ color: legendChecked[item.name] ? '#ccc' : '#495057' }}>
              {numberToFixed((item?.value / totalValue) * 100) + '%'}
            </span>
          </div>
        ))}
      </div>
    );
  };

  const renderChart = useMemo(() => {
    return (
      <div className={props.className}>
        <div
          id={props.id}
          key={props.id}
          style={{
            // width: props.width || 380,
            height: props.height || 260,
          }}
        ></div>
        <div id={`extension-${props.id}`}></div>
      </div>
    );
  }, [id, className, height]);
  return (
    <>
      {renderChart}
      {props.renderLegend && renderLegends(myChart.current)}
    </>
  );
};
