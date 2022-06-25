export const createOption = (data: any) => {
  const text = !isNaN(Number((data[2].value / data[0].totalValue).toFixed(2)) * 100)
    ? Number((data[2].value / data[0].totalValue).toFixed(2)) * 100 + '%'
    : '0%';
  const options = {
    title: {
      text,
      subtext: '故障率',
      textAlign: 'center',
      textVerticalAlign: 'center',
      itemGap: 0,
      textStyle: {
        fontSize: 28,
        color: '#212529',
      },
      subtextStyle: {
        fontSize: 14,
        color: '#495057',
      },
      left: '47%',
      top: '42%',
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
        radius: ['100%', '60%'],
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
