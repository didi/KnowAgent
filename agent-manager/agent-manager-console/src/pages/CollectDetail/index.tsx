import React, { useState, useEffect } from 'react';
import { RouteComponentProps, useLocation } from 'react-router-dom';
import { ProDescriptions, ProTable, AppContainer, IconFont } from '@didi/dcloud-design';
import { collectTaskDetailBaseInfo, getAssociateHostColumns } from './config';
import AssociateHostDetail from './AssociateHostDetail';
import fetch from '../../lib/fetch';
import { EventBusTypes } from '../../constants/event-types';
import './index.less';

// export const getAgentHostId = (hostName: string) => {
//   return fetch(`/api/v1/normal/host?hostname=${hostName}`);
// };

// export const getHostDetails = (hostId: number) => {
//   return fetch(`/api/v1/rd/host/${hostId}`);
// };

// export const getAgentCollectList = (hostname: string) => {
//   return fetch(`/api/v1/normal/agent/metrics/collect-tasks?hostname=${hostname}`);
// };

// export const getAgentDetails = (agentId: number) => {
//   return fetch(`/api/v1/rd/agent/${agentId}`);
// };

export const getCollectDetails = (logCollectTaskId: number) => {
  return fetch(`/api/v1/normal/collect-task/${logCollectTaskId}`);
};

export const getHostCollectTaskList = (logCollectTaskId: number) => {
  return fetch(`/api/v1/normal/host/collect-task/${logCollectTaskId}`);
};

const AgentDetail = (props: any) => {
  const { state } = useLocation<any>();
  const [loading, setLoading] = useState(true);
  const [collectDetail, setCollectDetail] = useState<any>({});
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
  const [visibleHost, setVisibleHost] = useState(false);
  const [hostDetailData, setHostDetailData] = useState(false);

  const getDetail = async () => {
    // const hostId = await getAgentHostId(state.hostName as string);
    getCollectDetails(state.taskId)
      .then((res: any) => {
        console.log(res, 'getCollectDetails');
        // this.setState({ loading: false, hostDetail: res });
        setLoading(false);
        setCollectDetail(res);
      })
      .catch((err: any) => {
        setLoading(false);
      });
  };

  const getCollectList = () => {
    getHostCollectTaskList(state?.taskId)
      .then((res: any[]) => {
        const data = res?.map((ele: any, index: number) => {
          return { key: index, ...ele };
        });
        console.log(data);
        setCollectTastData(data);
        setCollectLoading(false);
      })
      .catch((err: any) => {
        setCollectTastData([]);
        setCollectLoading(false);
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

  const getHostDetail = (record) => {
    setVisibleHost(true);
    setHostDetailData(record);
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
        <span>{collectDetail.logCollectTaskName}</span>
        <span style={{ marginLeft: '8px', fontSize: '20px' }}>
          {collectDetail.logCollectTaskHealthLevel ? (
            collectDetail.logCollectTaskHealthLevel == 2 ? (
              <IconFont type="icon-hong" />
            ) : collectDetail.logCollectTaskHealthLevel == 1 ? (
              <IconFont type="icon-huang" />
            ) : (
              <IconFont type="icon-lv" />
            )
          ) : null}
        </span>
      </div>
    );
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, [collectDetail]);

  const headerLeftContent = <>采集任务详情</>;

  useEffect(() => {
    getDetail();
    getCollectList();
    // state?.agentId && getAgentDetail();
  }, []);

  return (
    <div style={{ padding: '24px', backgroundColor: '#F8F8FB' }}>
      <div style={{ padding: '24px', backgroundColor: '#ffffff' }} className="collect_detail">
        <div style={{ marginBottom: '20px' }}>
          <ProDescriptions
            title={'基本信息'}
            labelStyle={{ minWidth: '200px' }}
            contentStyle={{ minWidth: '180px' }}
            column={{
              xxl: 2,
              xl: 2,
              lg: 2,
              md: 2,
              sm: 2,
              xs: 1,
            }}
            dataSource={collectDetail}
            config={collectTaskDetailBaseInfo(collectDetail)}
          />
        </div>
        <div>
          <ProTable
            isCustomPg={true}
            showQueryForm={false}
            tableProps={{
              tableId: 'collectList',
              showHeader: false,
              rowKey: 'hostName',
              loading: collectLoading,
              columns: getAssociateHostColumns(getHostDetail) as any,
              dataSource: collectTastData,
              paginationProps: { ...pagination },
              tableHeaderTitle: true, // 展示表格自定义标题
              tableHeaderTitleText: '关联主机', // 自定义标题文本内容
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
          <AssociateHostDetail visible={visibleHost} setVisible={setVisibleHost} hostDetailData={hostDetailData} />
        </div>
        {/* {state?.agentId ? (
        <div>
          <div style={{ marginBottom: '20px' }}>
            <ProDescriptions
              title={'基础配置信息'}
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
              title={'高级配置信息'}
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
          
        </div>
      ) : (
          <div>该主机未安装Agent</div>
        )} */}
      </div>
    </div>
  );
};

export default AgentDetail;
