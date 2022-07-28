import React, { useState, useEffect } from 'react';
import { RouteComponentProps, useLocation, withRouter } from 'react-router-dom';
import { ProDescriptions, AppContainer, IconFont } from '@didi/dcloud-design';
import { getHostInfo, getAgentInfo, getAgentSeniorInfo, getCollectTaskConfig } from './config';
import { EventBusTypes } from '../../constants/event-types';
import fetch from '../../lib/fetch';

export const getAgentHostId = (hostName: string) => {
  return fetch(`/api/v1/normal/host?hostname=${hostName}`);
};

export const getHostDetails = (hostId: number) => {
  return fetch(`/api/v1/rd/host/${hostId}`);
};

export const getAgentCollectList = (hostname: string) => {
  return fetch(`/api/v1/normal/agent/metrics/collect-tasks?hostname=${hostname}`);
};

export const getAgentDetails = (agentId: number) => {
  return fetch(`/api/v1/rd/agent/${agentId}`);
};

const AgentDetail = (props: any) => {
  const { state } = useLocation<any>();
  const [loading, setLoading] = useState(true);
  const [hostDetail, setHostDetail] = useState<any>({});
  const [agentDetail, setAgentDetail] = React.useState<any>({});
  const [collectTastData, setCollectTastData] = useState<any>([]);
  const [collectLoading, setCollectLoading] = useState(true);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });
  const getDetail = async () => {
    const hostId = await getAgentHostId(state.hostName as string);
    getHostDetails(state.hostId || hostId.id)
      .then((res: any) => {
        // this.setState({ loading: false, hostDetail: res });
        setLoading(false);
        setHostDetail(res);
      })
      .catch((err: any) => {
        setLoading(false);
      });
  };

  const getCollectList = () => {
    getAgentCollectList(state?.hostName)
      .then((res) => {
        console.log(res, 'sssss');
        setCollectTastData(res || []);
        setCollectLoading(false);
        // console.log(res, 'res')
      })
      .catch((err) => {
        setCollectTastData([]);
      });
  };

  const getAgentDetail = () => {
    getAgentDetails(state?.agentId).then((res) => {
      res.cpuLimitThreshold = res.cpuLimitThreshold / 100;
      setAgentDetail(res);
    });
  };

  const onTableChange = (pagination, filters, sorter) => {
    console.log(pagination, 'pagination');
    setPagination((value) => {
      console.log(value, 'valuess');
      return {
        ...value,
        ...pagination,
      };
    });
  };

  const backClick = () => {
    // console.log(props);
    props.history.goBack();
  };

  useEffect(() => {
    const headerLeftContent = (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <span style={{ marginRight: '16px', fontSize: '13px', cursor: 'pointer' }} onClick={backClick}>
          <IconFont type="icon-fanhui" /> 返回
        </span>
        <span>{hostDetail.hostName}</span>
        <span style={{ marginLeft: '8px', fontSize: '20px' }}>
          {hostDetail.agentHealthLevel || hostDetail.agentHealthLevel == 0 ? (
            hostDetail.agentHealthLevel == 0 ? (
              <IconFont type="icon-hong" />
            ) : hostDetail.agentHealthLevel == 1 ? (
              <IconFont type="icon-huang" />
            ) : (
              <IconFont type="icon-lv" />
            )
          ) : null}
        </span>
      </div>
    );
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, [hostDetail]);

  useEffect(() => {
    getDetail();
    // state?.agentId && getCollectList();
    state?.agentId && getAgentDetail();
  }, []);

  return (
    <div style={{ padding: '24px', backgroundColor: '#F8F8FB' }}>
      <div className={'agemt-detail-box'} style={{ padding: '24px', backgroundColor: '#ffffff' }}>
        <div style={{ marginBottom: '20px' }}>
          <ProDescriptions
            labelStyle={{ minWidth: '200px' }}
            contentStyle={{ minWidth: '200px' }}
            title={'主机信息'}
            column={{
              xxl: 2,
              xl: 2,
              lg: 2,
              md: 2,
              sm: 2,
              xs: 1,
            }}
            dataSource={hostDetail}
            config={getHostInfo(hostDetail)}
          />
        </div>
        {state?.agentId ? (
          <div>
            <div style={{ marginBottom: '20px' }}>
              <ProDescriptions
                title={'Agent 基础配置信息'}
                labelStyle={{ minWidth: '200px' }}
                contentStyle={{ minWidth: '200px' }}
                column={{
                  xxl: 2,
                  xl: 2,
                  lg: 2,
                  md: 2,
                  sm: 2,
                  xs: 1,
                }}
                dataSource={agentDetail}
                config={getAgentInfo(agentDetail)}
              />
            </div>
            <div style={{ marginBottom: '20px' }}>
              <ProDescriptions
                title={'Agent 高级配置信息'}
                labelStyle={{ minWidth: '200px' }}
                contentStyle={{ minWidth: '200px' }}
                column={{
                  xxl: 2,
                  xl: 2,
                  lg: 2,
                  md: 2,
                  sm: 2,
                  xs: 1,
                }}
                dataSource={agentDetail}
                config={getAgentSeniorInfo(agentDetail)}
              />
            </div>
            {/* <div>
            <ProTable
              isCustomPg={true}
              showQueryForm={false}
              tableProps={{
                tableId: 'collectList',
                showHeader: false,
                rowKey: 'hostName',
                loading: collectLoading,
                columns: getCollectTaskConfig('11'),
                dataSource: [],
                paginationProps: { ...pagination },
                tableHeaderTitle: true, // 展示表格自定义标题
                tableHeaderTitleText: '采集任务', // 自定义标题文本内容
                tableHeaderCustomColumns: true, // 表格Header右侧自定义列
                attrs: {
                  // className: 'frameless-table', // 纯无边框表格类名
                  // bordered: true,   // 表格边框
                  onChange: onTableChange,
                  scroll: {
                    x: 'max-content',
                  },
                },
              }}
            />
          </div> */}
          </div>
        ) : (
          <div>该主机未安装Agent</div>
        )}
      </div>
    </div>
  );
};

export default withRouter(AgentDetail);
