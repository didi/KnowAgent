// import React, { useState } from 'react'
// import { ChartContainer, Imenu, SingleChart } from '@didi/dcloud-design'

// export default (props) => {
//   const defaultConfig = {
//     reloadIconShow: true,
//     isGroup: true,
//     groupCount: 2,
//     groupItemsCount: '4,2',
//     itemCount: 2,
//     isHaveTabInMetricTree: true,
//     menuList: ['0', '1'],
//     componentType: 'SingleChart'
//   }
//   const config = Object.assign({}, defaultConfig, props?.config)

//   const menuList: Imenu[] = (config.isHaveTabInMetricTree ? [
//     {
//       name: "Agent",
//       key: '0', // 固定
//       url: ''
//     },
//     {
//       name: "日志采集",
//       key: '1', // 固定
//       url: ''
//     }
//   ].filter(o => (config?.menuList || []).includes(o.key)) : []) as Imenu[];

//   // 分组的数据
//   let groupsData = []
//   let groupItemsCount = (config?.groupItemsCount || '').split(',').map(s => Number(s))
//   // 不分组的数据
//   let itemsData = []
//   if (config.isGroup) {
//     for (let i = 0; i < config.groupCount || 0; i++) {
//       groupsData.push({
//         groupId: i + 1,
//         groupName: `group${i + 1}`,
//         lists: []
//       })
//       for (let j = 0; j < groupItemsCount[i] || 0; j++) {
//         groupsData[i].lists.push({
//           id: j + 1,
//           name: `${i + 1}-${j + 1}`
//         })
//       }
//     }
//   } else {
//     for (let i = 0; i < config.itemCount || 0; i++) {
//       itemsData.push({
//         id: i + 1,
//         name: `1-${i + 1}`
//       })
//     }
//   }

//   const queryLineData = () => {
//     return new Promise((resolve) => {
//       setTimeout(() => {
//         resolve({
//           code: 0,
//           data: [
//             [
//               {
//                 name: 'host',
//                 timeStampMinute: '星期一',
//                 value: 100
//               },
//             {
//               name: 'host',
//               timeStampMinute: '星期二',
//               value: 200
//             }
//             ],
//                  [
//               {
//                 name: 'topic',
//                 timeStampMinute: '星期一',
//                 value: 80
//               },
//             {
//               name: 'topic',
//               timeStampMinute: '星期二',
//               value: 290
//             }
//             ],
//                    [
//               {
//                 name: 'health',
//                 timeStampMinute: '星期一',
//                 value: 80
//               },
//             {
//               name: 'health',
//               timeStampMinute: '星期二',
//               value: 490
//             }
//             ],
//           ],
//         });
//       }, 2000);
//     });
//   };

//   const handleReqCallback = (data, props) => {
//     const { host, agent, path, ...rest } = data
//     const { code, type } = props
//     const changeObj = type === 'agent' ? {
//       agent
//     } : {
//       host,
//       path
//     }
//     return {
//       ...rest,
//       ...changeObj,
//       code
//     }
//   }

//   const DragItem = (props) => {
//     const { eventBus, title, chartType, type, code} = props;
//     return (
//       <SingleChart
//         title={'测试12'}
        
//         wrapStyle={{
//           width: "100%",
//           height: 300,
//         }}
//         showLargeChart={true}
//         connectEventName="connect"
//         url="/api/test"
//         eventBus={eventBus}
//         request={queryLineData}
//         resCallback={(res: any) => res.data}
//         reqCallback={(data) => handleReqCallback(data, props)}
//         xAxisCallback={((data) => data?.[0].map((item) => item.timeStampMinute))}
//         legendCallback={((data) => data?.map((item) => item[0].name))}
//         seriesCallback={(data) => {
//           return data.map((item, index) => {
//             return {
//               name: data[index][0].name,
//               data: data[index]
//             }
//           })
//         }}
//       />
//     )
//   };
//   return <ChartContainer 
//     reloadModule={{ 
//       reloadIconShow: config.reloadIconShow,
//       lastTimeShow: config.reloadIconShow
//     }}
//     dragModule={{
//       dragItem: <DragItem></DragItem>,
//       isGroup: config.isGroup,
//       groupsData: config.isGroup ? groupsData : itemsData
//     }}
//     indicatorSelectModule={{
//       hide: false,
//       menuList
//     }}>
    
//   </ChartContainer>
// }