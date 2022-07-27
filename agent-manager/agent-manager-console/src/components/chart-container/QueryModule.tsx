import React, { useState, useEffect } from 'react';
import { Select, Row, Col, message, Tooltip } from '@didi/dcloud-design';
const { Option } = Select;
import './style/query-module.less';
import { eventBus } from './index';
import { IconFont } from '@didi/dcloud-design';
import { IindicatorSelectModule, IfilterData } from './index';
import { request } from '../../request/index';

interface propsType extends React.HTMLAttributes<HTMLDivElement> {
  tabKey: string;
  indicatorSelectModule: IindicatorSelectModule;
  layout?: 'horizontal' | 'vertical';
  filterData?: IfilterData;
  queryChange?: (params: any) => any;
}

const QueryModule: React.FC<propsType> = ({ indicatorSelectModule, layout, filterData, queryChange, tabKey }: propsType) => {
  const [collectTaskList, setCollectTaskList] = useState<any[]>([]);
  const [pathList, setPathList] = useState<any[]>([]);
  const [hostList, setHostList] = useState<any[]>([]);
  const [agentList, setAgentList] = useState<any[]>([]);
  const [logCollectTaskId, setlogCollectTaskId] = useState<number | string>(null);
  const [hostName, setHostName] = useState<string>(null);
  const [pathId, setPathId] = useState<number | string>(null);
  const [agent, setAgent] = useState<number | string>(null);

  const [logCollectTaskCur, setlogCollectTaskCur] = useState<any>(null);
  const [hostNameCur, setHostNameCur] = useState<any>(null);
  const [pathIdCur, setPathIdCur] = useState<any>(null);
  const [agentCur, setAgentCur] = useState<any>(null);

  useEffect(() => {
    eventBus.on('queryListChange', (val) => {
      if (tabKey === '1') {
        setCollectTaskList(val.collectTaskList);
      } else {
        setAgentList(val.agentList);
        setAgentCur({
          label: val.agentList[0]?.title,
          value: val.agentList[0]?.value,
        });
        setAgent(val.agentList[0]?.value || null);
      }
    });
    return () => {
      eventBus.removeAll('queryListChange');
    };
  }, []);

  useEffect(() => {
    if (indicatorSelectModule?.menuList?.length < 2) {
      if (collectTaskList[0]?.value) {
        setlogCollectTaskId(filterData?.logCollectTaskId || collectTaskList[0]?.value);
        setlogCollectTaskCur({
          label: collectTaskList[0]?.title,
          value: collectTaskList[0]?.value,
        });
      } else {
        setlogCollectTaskId(null);
      }
    }
  }, [collectTaskList[0]?.value]);

  useEffect(() => {
    if (indicatorSelectModule?.menuList?.length < 2) {
      if (agentList[0]?.value) {
        setAgent(filterData?.agent || agentList[0]?.value);
        setAgentCur({
          label: agentList[0]?.title,
          value: agentList[0]?.value,
        });
      } else {
        setAgent(null);
      }
    }
  }, [agentList[0]?.value]);

  useEffect(() => {
    filterData?.pathId && setPathId(filterData.pathId);
    filterData?.hostName && setHostName(filterData.hostName);
    filterData?.agent && setAgent(filterData.agent);
    filterData?.logCollectTaskId && setlogCollectTaskId(filterData.logCollectTaskId);
  }, [filterData]);

  useEffect(() => {
    if (logCollectTaskId) {
      getHostList();
      getPathList();
    }
  }, [logCollectTaskId]);

  useEffect(() => {
    eventBus.emit('queryChartContainerChange', {
      logCollectTaskId,
      hostName,
      pathId,
      agent,
    });
    queryChange &&
      queryChange({
        logCollectTaskCur,
        hostNameCur,
        pathIdCur,
        agentCur,
      });
  }, [logCollectTaskId, hostName, pathId, agent]);

  const getHostList = async () => {
    if (!logCollectTaskId) {
      message.warning('请先选择采集任务');
      return;
    }

    const res: any = await request(`/api/v1/normal/host/collect-task/${logCollectTaskId}`);
    const data = res.data || res;
    const processedData = data?.map((item) => {
      return {
        ...item,
        value: item.hostName,
        title: item.hostName,
      };
    });
    setHostList(processedData);
  };
  const getPathList = async () => {
    const res: any = await request(`/api/v1/normal/collect-task/${logCollectTaskId}`);
    const data = res.data || res || [];
    const processedData = data?.fileLogCollectPathList?.map((item) => {
      return {
        ...item,
        value: item.id,
        title: item.path,
      };
    });
    setPathList(processedData);
  };

  const logCollectTaskIdChange = (vals) => {
    setlogCollectTaskId(vals?.value || null);
    setlogCollectTaskCur(vals);
    setPathId(null);
    setHostName(null);
  };
  const hostChange = (vals) => {
    setHostName(vals?.value || null);
    setHostNameCur(vals);
  };
  const pathChange = (vals) => {
    setPathId(vals?.value || null);
    setPathIdCur(vals);
  };
  const agentChange = (vals) => {
    setAgent(vals?.value || null);
    setAgentCur(vals);
  };

  const pathFocus = () => {
    if (!logCollectTaskId) {
      message.warning('请先选择采集任务');
      return;
    }
  };
  const hostFocus = () => {
    if (!logCollectTaskId) {
      message.warning('请先选择采集任务');
      return;
    }
  };
  return (
    <>
      <div className="query-select">
        <div className={layout === 'horizontal' ? 'horizontal' : 'vertical'}>
          {tabKey === '1' && (
            <Row gutter={[16, 16]}>
              <Col span={8}>
                <div className="label-name">采集任务名：</div>
                <Select
                  showSearch
                  allowClear
                  suffixIcon={<IconFont type="icon-xiala" />}
                  placeholder="请选择采集任务名"
                  labelInValue={true}
                  value={{ value: logCollectTaskId }}
                  optionFilterProp="label"
                  onChange={logCollectTaskIdChange}
                  filterOption={(text, option) => {
                    return option.props.label?.toLowerCase().indexOf(text.toLowerCase()) >= 0;
                  }}
                >
                  {collectTaskList?.map((item) => (
                    <Option key={item.value} value={item.value} label={item.title}>
                      {item.title}
                    </Option>
                  ))}
                </Select>
              </Col>
              <Col span={8}>
                <div className="label-name">采集路径：</div>
                {logCollectTaskId ? (
                  <Select
                    showSearch
                    allowClear
                    suffixIcon={<IconFont type="icon-xiala" />}
                    placeholder="请选择采集路径"
                    labelInValue={true}
                    value={{ value: pathId }}
                    disabled={logCollectTaskId !== null ? false : true}
                    optionFilterProp="label"
                    onChange={pathChange}
                    onFocus={pathFocus}
                    filterOption={(text, option) => {
                      return option.props.label?.toLowerCase().indexOf(text.toLowerCase()) >= 0;
                    }}
                  >
                    {pathList?.map((item) => (
                      <Option key={item.value} value={item.value} label={item.title}>
                        {item.title}
                      </Option>
                    ))}
                  </Select>
                ) : (
                  <Tooltip title="请先选择采集任务">
                    <Select
                      showSearch
                      allowClear
                      suffixIcon={<IconFont type="icon-xiala" />}
                      placeholder="请选择采集路径"
                      labelInValue={true}
                      value={{ value: pathId }}
                      disabled={logCollectTaskId !== null ? false : true}
                      optionFilterProp="label"
                      onChange={pathChange}
                      onFocus={pathFocus}
                      filterOption={(text, option) => {
                        return option.props.label?.toLowerCase().indexOf(text.toLowerCase()) >= 0;
                      }}
                    >
                      {pathList?.map((item) => (
                        <Option key={item.value} value={item.value} label={item.title}>
                          {item.title}
                        </Option>
                      ))}
                    </Select>
                  </Tooltip>
                )}
              </Col>
              <Col span={8}>
                <div className="label-name">主机名：</div>
                {logCollectTaskId ? (
                  <Select
                    showSearch
                    allowClear
                    suffixIcon={<IconFont type="icon-xiala" />}
                    placeholder="请选择主机名"
                    labelInValue={true}
                    value={{ value: hostName }}
                    disabled={logCollectTaskId !== null ? false : true}
                    optionFilterProp="label"
                    onChange={hostChange}
                    onFocus={hostFocus}
                    filterOption={(text, option) => {
                      return option.props.label?.toLowerCase().indexOf(text.toLowerCase()) >= 0;
                    }}
                  >
                    {hostList?.map((item) => (
                      <Option key={item.value} value={item.value} label={item.title}>
                        {item.title}
                      </Option>
                    ))}
                  </Select>
                ) : (
                  <Tooltip title="请先选择采集任务">
                    <Select
                      showSearch
                      allowClear
                      suffixIcon={<IconFont type="icon-xiala" />}
                      placeholder="请选择主机名"
                      labelInValue={true}
                      value={{ value: hostName }}
                      disabled={logCollectTaskId !== null ? false : true}
                      optionFilterProp="label"
                      onChange={hostChange}
                      onFocus={hostFocus}
                      filterOption={(text, option) => {
                        return option.props.label?.toLowerCase().indexOf(text.toLowerCase()) >= 0;
                      }}
                    >
                      {hostList?.map((item) => (
                        <Option key={item.value} value={item.value} label={item.title}>
                          {item.title}
                        </Option>
                      ))}
                    </Select>
                  </Tooltip>
                )}
              </Col>
            </Row>
          )}
          {tabKey === '0' && (
            <Row gutter={[16, 16]}>
              <Col span={indicatorSelectModule?.menuList?.length > 1 ? 24 : 8}>
                <div className="label-name">Agent 主机名：</div>
                <Select
                  showSearch
                  allowClear
                  suffixIcon={<IconFont type="icon-xiala" />}
                  placeholder="请选择Agent主机名"
                  style={{ width: indicatorSelectModule?.menuList?.length > 1 ? '224px' : 'auto' }}
                  labelInValue={true}
                  value={{ value: agent }}
                  optionFilterProp="label"
                  onChange={agentChange}
                  filterOption={(text, option) => {
                    return option.props.label?.toLowerCase().indexOf(text.toLowerCase()) >= 0;
                  }}
                >
                  {agentList?.map((item) => (
                    <Option key={item.value} value={item.value} label={item.title}>
                      {item.title}
                    </Option>
                  ))}
                </Select>
              </Col>
            </Row>
          )}
        </div>
      </div>
    </>
  );
};

export default QueryModule;
