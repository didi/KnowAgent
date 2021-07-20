const data: any = []
for (let i = 0; i <= 10; i++) {
  data[i] = {
    "metricName": '13131.2313.' + i,
    metricPointList: [],
  };
  for (let j = 0; j <= 60; j++) {
    data[i].metricPointList.push({"timestamp":1626238240000 + j * 60 * 1000,"value": i},)
  }
}
export default data;
// [
//   {
//       "metricName": '13131.2313.31313',
//       "metricPointList": [
//         {"timestamp":1626238240000,"value":0.1},
//         {"timestamp":1626248246000,"value":2},
//         {"timestamp":1626258266000,"value":3},
//         {"timestamp":1626268270000,"value":22},
//         {"timestamp":1626258240000 + 120 * 10000,"value":1},
//         {"timestamp":1626258240000 + 180 * 10000,"value":1},
//         {"timestamp":1626258240000 + 240 * 10000,"value":1},
//         {"timestamp":1626258240000 + 300 * 10000,"value":1},
//         {"timestamp":1626258240000 + 360 * 10000,"value":1},
//         {"timestamp":1626258240000 + 420 * 10000,"value":1},
//         {"timestamp":1626258240000 + 480 * 10000,"value":1},
//         {"timestamp":1626258240000 + 540 * 10000,"value":1},
//         {"timestamp":1626258240000 + 600 * 10000,"value":13},
//         {"timestamp":1626258240000 + 660 * 10000,"value":1},
//         {"timestamp":1626258240000 + 720 * 10000,"value":12},
//         {"timestamp":1626258240000 + 780 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//         {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       ]
//   },
//   {
//     "metricName": '41414.31.312312',
//     "metricPointList": [
//       {"timestamp":1626238240000,"value":0.1},
//       {"timestamp":1626248246000,"value":10},
//       {"timestamp":1626258266000,"value":23},
//       {"timestamp":1626268270000,"value":40},
//       {"timestamp":1626258240000 + 120 * 10000,"value":1},
//       {"timestamp":1626258240000 + 180 * 10000,"value":1},
//       {"timestamp":1626258240000 + 240 * 10000,"value":1},
//       {"timestamp":1626258240000 + 300 * 10000,"value":1},
//       {"timestamp":1626258240000 + 360 * 10000,"value":1},
//       {"timestamp":1626258240000 + 420 * 10000,"value":1},
//       {"timestamp":1626258240000 + 480 * 10000,"value":11},
//       {"timestamp":1626258240000 + 540 * 10000,"value":1},
//       {"timestamp":1626258240000 + 600 * 10000,"value":13},
//       {"timestamp":1626258240000 + 660 * 10000,"value":1},
//       {"timestamp":1626258240000 + 720 * 10000,"value":12},
//       {"timestamp":1626258240000 + 780 * 10000,"value":13},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//     ]
//   },
//   {
//     "metricName": '41414.31.312312',
//     "metricPointList": [
//       {"timestamp":1626238240000,"value":0.1},
//       {"timestamp":1626248246000,"value":10},
//       {"timestamp":1626258266000,"value":23},
//       {"timestamp":1626268270000,"value":40},
//       {"timestamp":1626258240000 + 120 * 10000,"value":1},
//       {"timestamp":1626258240000 + 180 * 10000,"value":1},
//       {"timestamp":1626258240000 + 240 * 10000,"value":1},
//       {"timestamp":1626258240000 + 300 * 10000,"value":1},
//       {"timestamp":1626258240000 + 360 * 10000,"value":1},
//       {"timestamp":1626258240000 + 420 * 10000,"value":1},
//       {"timestamp":1626258240000 + 480 * 10000,"value":11},
//       {"timestamp":1626258240000 + 540 * 10000,"value":1},
//       {"timestamp":1626258240000 + 600 * 10000,"value":13},
//       {"timestamp":1626258240000 + 660 * 10000,"value":1},
//       {"timestamp":1626258240000 + 720 * 10000,"value":12},
//       {"timestamp":1626258240000 + 780 * 10000,"value":13},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//       {"timestamp":1626258240000 + 840 * 10000,"value":1},
//     ]
//   },
// ]