export const createOption = (data: any) => {
  const options = {
    title: {
      text: '故障率',
      subtext: Number((data[2].value / data[0].oldValue).toFixed(2)) * 100 + '%'  ,
      textAlign: "center",
      textVerticalAlign: "center",
      textStyle: {
        fontSize: 14,
        color: 'rgba(0,0,0,0.45)'
      },
      subtextStyle: {
        fontSize: 16,
        color: 'rgba(0,0,0,0.85)'
      },
      left: "47%",
      top: "40%",
    },
    tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          let tip = params.marker + params.name + ': ' + (Number((params.value / data[0].oldValue).toFixed(2)) * 100) + '%';
          return tip
        }
    },
    color: ['#21CAB8', '#F0BC18', '#FF8686'],
    grid: {
      left: 10,
      top: 0
    },
    series: [
        {
            type: 'pie',
            radius: ['100%', '60%'],
            avoidLabelOverlap: false,
            hoverAnimation: false,
            label: {
                show: false,
                position: 'center'
            },
            animation: false,
            labelLine: {
                show: false
            },
            data: data,
        }
    ]
  };
  return options;
}