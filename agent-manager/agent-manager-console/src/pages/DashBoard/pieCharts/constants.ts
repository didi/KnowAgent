import { numberToFixed } from '../../../lib/utils';

export const pieColors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'];

export const createOption = (data: any) => {
  const text = !isNaN(Number((data[2].value / data[0].totalValue).toFixed(2)) * 100)
    ? Number((data[2].value / data[0].totalValue).toFixed(2)) * 100 + '%'
    : '0%';
  const clientWidth1440 = document.body.clientWidth === 1440;
  const options = {
    title: {
      text,
      subtext: '故障率',
      textAlign: 'center',
      textVerticalAlign: 'center',
      itemGap: 0,
      textStyle: {
        fontSize: 26,
        color: '#212529',
      },
      subtextStyle: {
        fontSize: 13,
        color: '#495057',
      },
      left: '49%',
      top: clientWidth1440 ? '22%' : '24%',
    },
    tooltip: {
      trigger: 'item',
      confine: true,
      textStyle: {
        fontSize: 12,
        color: '#495057',
        fontWeight: 'normal',
      },
      extraCssText: 'box-shadow: 0 2px 12px 0 rgba(31,50,82,0.18); border-radius: 2px; border: none; height: 44px; line-height: 25px;',
      formatter: (params: any) => {
        const val = Number((params.value / data[0].totalValue).toFixed(2));
        const formatVal = isNaN(val) ? 0 : val * 100;
        const tip =
          `<span style="display:inline-block;margin-right:8px;border-radius:50%; width:6px; height:6px; background-color:${params.color};"></span>` +
          params.name +
          ':&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
          formatVal.toFixed() +
          '%';
        return tip;
      },
    },
    color: ['#34C38F', '#F1B44C', '#FF6969'],
    grid: {
      left: 10,
      top: 0,
    },
    legend: {
      show: false,
    },
    series: [
      {
        type: 'pie',
        radius: clientWidth1440 ? ['58.9%', '40%'] : ['47%', '33%'],
        center: clientWidth1440 ? ['50%', '26%'] : ['50%', '28%'],
        data,
        avoidLabelOverlap: false,
        hoverAnimation: false,
        label: {
          show: false,
          position: 'center',
        },
        animation: false,
        labelLine: {
          show: false,
        },
      },
    ],
  };
  return options;
};

export const getPieChartOption = (data: any, totalValue = 0, customOptions = {}) => {
  const clientWidth1440 = document.body.clientWidth === 1440;

  const options = {
    tooltip: {
      trigger: 'item',
      confine: true,
      textStyle: {
        fontSize: 12,
        color: '#495057',
        fontWeight: 'normal',
      },
      extraCssText: 'box-shadow: 0 2px 12px 0 rgba(31,50,82,0.18); border-radius: 2px; border: none; height: 44px; line-height: 25px;',
      formatter: (params: any) => {
        const val = numberToFixed((params?.value / totalValue) * 100);
        const tip =
          `<span style="display:inline-block;margin-right:8px;border-radius:50%; width:6px; height:6px; background-color:${params.color};"></span>` +
          params.name +
          '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
          val +
          '%';
        return tip;
      },
    },
    legend: {
      orient: 'vertical',
      bottom: 13,
      type: 'scroll',
      height: 148,
      left: 0,
      itemHeight: 8,
      itemWidth: 8,
      formatter: (name) => {
        const item = data.find((row) => row.name);
        return name + '\t' + numberToFixed((item?.value / totalValue) * 100) + '%';
      },
    },
    series: [
      {
        type: 'pie',
        radius: clientWidth1440 ? '58.9%' : '47%',
        center: clientWidth1440 ? ['50%', '26%'] : ['50%', '28%'],
        data,
        hoverAnimation: false,
        label: {
          show: false,
          position: 'center',
        },
        animation: false,
        labelLine: {
          show: false,
        },
      },
    ],
    ...customOptions,
  };
  return options;
};
