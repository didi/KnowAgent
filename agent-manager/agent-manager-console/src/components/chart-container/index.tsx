/* eslint-disable react/prop-types */
import React, { useEffect, useState } from 'react';
import { Collapse, Button, Radio, Tooltip, Empty } from '@didi/dcloud-design';
const { Panel } = Collapse;
import { arrayMoveImmutable } from 'array-move';
import { CaretRightOutlined, ReloadOutlined } from '@ant-design/icons';
import { IconFont } from '@didi/dcloud-design';
import moment from 'moment';
import { DragGroup } from '@didi/dcloud-design';
import TimeModule from './TimeModule';
import IndicatorDrawer from './IndicatorDrawer';
import QueryModule from './QueryModule';
import { Utils } from '@didi/dcloud-design';
import { request } from '../../request/index';
import './style/index.less';

const { EventBus } = Utils;
// EventBus 实例
export const eventBus = new EventBus();

interface Ireload {
  reloadIconShow?: boolean;
  lastTimeShow?: boolean;
}

interface IdragModule {
  dragItem: React.ReactElement;
  requstUrl?: string;
  isGroup?: boolean;
  groupsData?: any[];
}

export interface Imenu {
  key: '0' | '1';
  name: string;
  url: string;
}
export interface IindicatorSelectModule {
  hide?: boolean;
  drawerTitle?: string;
  menuList?: Imenu[];
}

export interface IfilterData {
  hostName?: string;
  logCollectTaskId?: string | number;
  pathId?: string | number;
  agent?: string;
}
interface propsType {
  dragModule: IdragModule;
  reloadModule: Ireload;
  indicatorSelectModule?: IindicatorSelectModule;
  isGold?: boolean;
  filterData?: IfilterData;
}

const SizeOptions = [
  {
    label: 'S',
    value: 8,
  },
  {
    label: 'M',
    value: 12,
  },
  {
    label: 'L',
    value: 24,
  },
];

let relativeTimer;

// 渲染表格数据时,为防止后端数据未生成,将时间提前
const advanceTime = {
  THIRTY: 90000,
};

const ChartContainer: React.FC<propsType> = ({ filterData, dragModule, reloadModule, indicatorSelectModule, isGold = false }) => {
  const [groups, setGroups] = useState<any[]>(dragModule.groupsData);
  const clientWidth = document.body.clientWidth;

  const [gridNum, setGridNum] = useState<number>(clientWidth < 1920 ? 12 : 8);
  const [gutterNum, setGutterNum] = useState<any>([16, 16]);
  const [dateStrings, setDateStrings] = useState<number[]>([
    moment().valueOf() - 60 * 60 * 1000 - advanceTime.THIRTY,
    moment().valueOf() - advanceTime.THIRTY,
  ]);
  const [lastTime, setLastTime] = useState<string>(moment().format('YYYY.MM.DD.hh:mm:ss'));
  const [indicatorDrawerVisible, setIndicatorDrawerVisible] = useState(false);
  const [queryData, setQueryData] = useState({});

  const [collectTaskList, setCollectTaskList] = useState<any[]>([]);
  const [agentList, setAgentList] = useState([]);
  const [isRelative, setIsRelative] = useState(true);
  const [maskCount, setMaskCount] = useState(1);
  useEffect(() => {
    // setTimeout(() => {
    //   eventBus.emit('chartInit', {
    //     dateStrings,
    //   });
    // })
    eventBus.on('queryChartContainerChange', (data) => {
      const res = JSON.parse(JSON.stringify(queryData));
      data?.agent ? (res.agent = data?.agent) : '';
      if (data?.logCollectTaskId) {
        res.logCollectTaskId = data.logCollectTaskId;
        res.hostName = data.hostName;
        res.pathId = data.pathId;
      }

      setQueryData(res);
      if (indicatorSelectModule?.menuList?.length !== 2) {
        setTimeout(() => {
          eventBus.emit('chartReload', {
            dateStrings,
            ...res,
          });
        }, 150);
      }
    });
    if (!isGold) {
      indicatorSelectModule.menuList.forEach((item) => {
        if (item.key === '0') {
          getAgent();
        } else {
          getTaskList();
        }
      });
    }

    return () => {
      eventBus.removeAll('queryChartContainerChange');
      relativeTimer && window.clearInterval(relativeTimer);
    };
  }, []);

  useEffect(() => {
    const localMaskFlag = Utils.getLocalStorage('maskFlag');
    !localMaskFlag && Utils.setLocalStorage('maskFlag', false);
  }, [Utils.getLocalStorage('maskFlag')]);

  useEffect(() => {
    if (filterData?.agent || filterData?.logCollectTaskId) {
      setQueryData(filterData);
      setTimeout(() => {
        eventBus.emit('chartReload', {
          dateStrings,
          ...filterData,
        });
      }, 150);
    }
  }, [filterData]);

  useEffect(() => {
    eventBus.emit('queryListChange', {
      agentList,
      collectTaskList,
      isCollect: true,
    });
    setQueryData({
      ...queryData,
      logCollectTaskId: filterData?.logCollectTaskId || collectTaskList[0]?.id,
    });
  }, [collectTaskList]);

  useEffect(() => {
    eventBus.emit('queryListChange', {
      agentList,
      collectTaskList,
      isCollect: false,
    });
    setQueryData({
      ...queryData,
      agent: filterData?.agent || agentList[0]?.hostName,
    });
  }, [agentList]);

  useEffect(() => {
    setGroups(dragModule.groupsData);
  }, [dragModule.groupsData]);

  useEffect(() => {
    if (isRelative) {
      // 取消自动刷新功能
      // relativeTimer = window.setInterval(() => {
      //   reload();
      // }, 1 * 60 * 1000);
    } else {
      relativeTimer && window.clearInterval(relativeTimer);
    }
    return () => {
      relativeTimer && window.clearInterval(relativeTimer);
    };
  }, [isRelative, dateStrings, queryData]);

  const dragEnd = ({ oldIndex, newIndex, collection, isKeySorting }, e) => {
    // console.log(oldIndex, newIndex, collection, isKeySorting, e);
    let _groups = groups;
    if ((indicatorSelectModule?.menuList?.length !== 2 && dragModule.isGroup) || indicatorSelectModule?.menuList?.length === 1) {
      for (let i = 0; i < groups.length; i++) {
        const item = groups[i];
        if (item.groupId == collection) {
          item.lists = arrayMoveImmutable(item.lists, oldIndex, newIndex);
          break;
        }
      }
    } else {
      _groups = arrayMoveImmutable(groups, oldIndex, newIndex);
    }
    setGroups(JSON.parse(JSON.stringify(_groups)));
    reload();
  };

  const sizeChange = (e) => {
    setGridNum(e.target.value);
    eventBus.emit('chartResize');
  };

  const timeChange = (dateStringsArr, isRelative) => {
    const timeArr = JSON.parse(JSON.stringify(dateStringsArr));
    setDateStrings([timeArr[0] - advanceTime.THIRTY, timeArr[1] - advanceTime.THIRTY]);
    const ortherData = Object.assign(filterData || {}, queryData);
    setTimeout(() => {
      eventBus.emit('chartReload', {
        dateStrings: dateStringsArr,
        ...ortherData,
      });
    }, 150);
    setIsRelative(isRelative);
  };

  const reload = () => {
    const timeLen = dateStrings[1] - dateStrings[0] || 0;
    setLastTime(moment().format('YYYY.MM.DD.hh:mm:ss'));
    setDateStrings([moment().valueOf() - timeLen - advanceTime.THIRTY, moment().valueOf() - advanceTime.THIRTY]);
    const ortherData = Object.assign(filterData || {}, queryData);
    if (ortherData.agent || ortherData.logCollectTaskId) {
      setTimeout(() => {
        eventBus.emit('chartReload', {
          dateStrings,
          ...ortherData,
        });
      }, 150);
    }
  };

  const indicatorSelect = () => {
    setIndicatorDrawerVisible(true);
    // eventBus.emit('queryListChange', {
    //   agentList,
    //   collectTaskList
    // });
  };

  const IndicatorDrawerClose = () => {
    setIndicatorDrawerVisible(false);
  };

  const indicatorSelectSure = (groups) => {
    setGroups(groups);
    IndicatorDrawerClose();
  };

  const getTaskList = async () => {
    const res: any = await request('/api/v1/normal/collect-task'); // 待修改
    const data = res || [];
    if (data.length > 0) {
      const processedData = data?.map((item) => {
        return {
          ...item,
          value: item.id,
          title: item.logCollectTaskName,
        };
      });
      setCollectTaskList(processedData);
    }
  };

  const getAgent = async () => {
    const res: any = await request('/api/v1/op/agent');
    const data = res || [];
    if (data.length > 0) {
      const processedData = data?.map((item) => {
        return {
          ...item,
          value: item.hostName,
          title: item.hostName,
        };
      });

      setAgentList(processedData);
    }
  };

  const handleEmitReload = () => {
    reload();
  };

  return (
    <>
      <div className="dd-chart-container">
        {indicatorSelectModule?.menuList?.length <= 1 && !isGold && (
          <div className="query-module-container">
            <QueryModule
              layout="horizontal"
              filterData={filterData}
              indicatorSelectModule={indicatorSelectModule}
              tabKey={indicatorSelectModule?.menuList[0]?.key}
            />
          </div>
        )}

        <div className={`dd-chart-container-header clearfix`}>
          <div className={`dd-chart-container-header-mask ${!Utils.getLocalStorage('maskFlag') ? 'mask-flag' : ''}`}>
            {!Utils.getLocalStorage('maskFlag') && (
              <div className="mask-flag-occlusion" style={{ background: maskCount === 2 && 'rgba(0, 0, 0, 0.4)' }}></div>
            )}
            {!Utils.getLocalStorage('maskFlag') && maskCount === 1 && (
              <div className="mask-flag-popover">
                <div className="mask-flag-popover-top">
                  <div className="mask-flag-popover-top-link">
                    <div></div>
                    <div></div>
                  </div>
                </div>
                <div className="mask-flag-popover-content">
                  <h3>图表操作</h3>
                  <p>图表操作都放在这里了</p>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>1/2</span>
                    <Button type="primary" onClick={() => setMaskCount(2)}>
                      下一步
                    </Button>
                  </div>
                </div>
              </div>
            )}
            {!Utils.getLocalStorage('maskFlag') && maskCount === 2 && (
              <div className="mask-flag-popover">
                <div className="mask-flag-popover-top">
                  <div className="mask-flag-popover-top-link">
                    <div></div>
                    <div></div>
                  </div>
                </div>
                <div className="mask-flag-popover-content">
                  <h3>选择指标</h3>
                  <p>选择指标放在这里了，可以增加或者减少指标图表</p>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>2/2</span>
                    <Button
                      type="primary"
                      onClick={() => {
                        Utils.setLocalStorage('maskFlag', true);
                        setMaskCount(0);
                      }}
                    >
                      知道了
                    </Button>
                  </div>
                </div>
              </div>
            )}
            <div className="dd-chart-container-header-r">
              {reloadModule && reloadModule.reloadIconShow && (
                <div className="reload-module">
                  <Button type="link" icon={<ReloadOutlined />} onClick={reload}>
                    刷新
                  </Button>
                  {reloadModule && reloadModule.lastTimeShow && <span className="last-time">上次刷新时间: {lastTime}</span>}
                </div>
              )}

              <TimeModule timeChange={timeChange} rangeTimeArr={dateStrings} />
              <Radio.Group optionType="button" buttonStyle="solid" options={SizeOptions} onChange={sizeChange} value={gridNum} />

              {(!indicatorSelectModule?.hide || indicatorSelectModule?.menuList?.length > 0) && !isGold && (
                <div className={`dd-chart-container-header-r-opBtn ${maskCount === 2 ? 'mask-flag-opBtn' : ''}`}>
                  <Tooltip title="点击指标筛选，可选择指标" placement="bottomRight">
                    <Button className="button-zhibiaoshaixuan" icon={<IconFont type="icon-zhibiaoshaixuan" />} onClick={indicatorSelect} />
                  </Tooltip>
                  {!Utils.getLocalStorage('maskFlag') && maskCount === 2 && <div className="mask-flag-occlusion"></div>}
                </div>
              )}
            </div>
          </div>
        </div>
        {groups?.length > 0 ? (
          (indicatorSelectModule?.menuList?.length !== 2 && dragModule.isGroup) || indicatorSelectModule?.menuList?.length === 1 ? (
            groups.map(
              (item, index) =>
                item?.lists?.length > 0 && (
                  <Collapse
                    key={index}
                    defaultActiveKey={['1']}
                    ghost={true}
                    expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
                  >
                    <Panel header={item.groupName} key="1">
                      <DragGroup
                        dragContainerProps={{
                          onSortEnd: dragEnd,
                          axis: 'xy',
                          // useDragHandle: true
                        }}
                        dragItemProps={{
                          collection: item.groupId,
                        }}
                        containerProps={{
                          grid: gridNum,
                          gutter: gutterNum,
                        }}
                      >
                        {item?.lists?.map((item, index) =>
                          React.cloneElement(dragModule.dragItem, {
                            ...item,
                            code: item.code,
                            key: item.title,
                            requstUrl: dragModule.requstUrl,
                            eventBus,
                            showLargeChart: !isGold,
                          })
                        )}
                      </DragGroup>
                    </Panel>
                  </Collapse>
                )
            )
          ) : (
            <div className="no-group-con">
              <DragGroup
                dragContainerProps={{
                  onSortEnd: dragEnd,
                  axis: 'xy',
                }}
                dragItemProps={
                  {
                    // collection: Math.random(),
                  }
                }
                containerProps={{
                  grid: gridNum,
                  gutter: gutterNum,
                }}
              >
                {groups.map((item, index) =>
                  React.cloneElement(dragModule.dragItem, {
                    ...item,
                    code: item.code,
                    key: item.title,
                    requstUrl: dragModule.requstUrl,
                    eventBus,
                    showLargeChart: !isGold,
                  })
                )}
              </DragGroup>
            </div>
          )
        ) : (
          <div>
            <Empty
              description={
                <span>
                  数据为空，请去<a onClick={() => setIndicatorDrawerVisible(true)}>指标筛选</a>页面添加指标
                </span>
              }
              image={Empty.PRESENTED_IMAGE_CUSTOM}
            />
          </div>
        )}
      </div>
      {(!indicatorSelectModule?.hide || isGold) && (
        <IndicatorDrawer
          visible={indicatorDrawerVisible}
          emitReload={handleEmitReload}
          onClose={IndicatorDrawerClose}
          onSure={indicatorSelectSure}
          isGroup={dragModule.isGroup}
          isGold={isGold}
          indicatorSelectModule={indicatorSelectModule}
        />
      )}
      {!Utils.getLocalStorage('maskFlag') && (
        <div style={{ position: 'fixed', zIndex: 99997, background: 'rgba(0,0,0,0.4)', top: 0, left: 0, bottom: 0, right: 0 }}></div>
      )}
    </>
  );
};

export default ChartContainer;
