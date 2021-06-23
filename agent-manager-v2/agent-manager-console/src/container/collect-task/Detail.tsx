
import * as React from 'react';
import * as actions from '../../actions';
import { CustomBreadcrumb, DescriptionsItems } from '../../component/CustomComponent';
import { collectTaskDetailBaseInfo, collectTaskDetailBreadcrumb } from './config';
import { AcquisitionConfiguration } from './AcquisitionConfiguration';
import { AssociateHost } from './AssociateHost';
import { AgentOperationIndex } from '../agent-management/OperationIndex';
import { taskHealthMap } from '../../constants/common';
import { ILogCollectTaskDetail } from '../../interface/collect';
import { getCollectDetails } from '../../api/collect'
import Url from '../../lib/url-parser';
import { Dispatch } from 'redux';
import { connect } from "react-redux";
import { Tabs, Tag } from 'antd';
import './index.less';

const { TabPane } = Tabs;

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setDrawerId: (drawerId: string, params?: any) => dispatch(actions.setDrawerId(drawerId, params)),
});

type Props = ReturnType<typeof mapDispatchToProps>;
@connect(null, mapDispatchToProps)
export class CollectTaskDetail extends React.Component<Props> {
  public taskId: number;

  public state = {
    loading: true,
    collectDetail: {} as ILogCollectTaskDetail,
  }

  constructor(props: any) {
    super(props);
    const url = Url();
    this.taskId = Number(props.location.state?.taskId);
  }

  public getDetail = () => {
    getCollectDetails(this.taskId).then((res: ILogCollectTaskDetail) => {
      this.setState({ collectDetail: res, loading: false });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public componentDidMount() {
    this.getDetail();
  }

  public render() {
    const { loading, collectDetail } = this.state;
    const health = taskHealthMap[collectDetail?.logCollectTaskHealthLevel];
    return (
      <>
        <CustomBreadcrumb btns={collectTaskDetailBreadcrumb} />
        <DescriptionsItems
          loading={loading}
          title={`${collectDetail.logCollectTaskName}应用采集任务`}
          column={4}
          subTitle={health ? <Tag color={health}>{health}</Tag> : ''}
          baseInfo={collectTaskDetailBaseInfo(collectDetail)}
          baseData={collectDetail}
        />
        <div className="detail-wrapper">
          <Tabs animated={false} defaultActiveKey="1">
            <TabPane tab="采集配置" key="1">
              <AcquisitionConfiguration detail={collectDetail} loading={loading} />
            </TabPane>
            <TabPane tab="运行指标" key="2">
              <AgentOperationIndex id={this.taskId} {...this.props} />
            </TabPane>
            <TabPane tab="关联主机" key="3">
              <AssociateHost drawer={this.props.setDrawerId} taskId={this.taskId} />
            </TabPane>
          </Tabs>
        </div>
      </>
    );
  }
}