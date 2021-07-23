
import * as React from 'react';
import { CustomBreadcrumb, DescriptionsItems } from '../../component/CustomComponent';
import { hostDetailBaseInfo, agentDetailBreadcrumb } from './config';
import { AgentOperationIndex } from './OperationIndex';
import { AgentConfigInfo } from './Configinformation';
import CollectTask from './CollectTask';
import { healthMap } from '../../constants/common';
import Url from '../../lib/url-parser';
import { IAgentHostSet } from '../../interface/agent';
import { getHostDetails } from '../../api/agent'
import { Tabs, Tag } from 'antd';
import './index.less';


const { TabPane } = Tabs;

export class AgentDetail extends React.Component<any> {
  public agentId: number;
  public hostId: number;
  public node: any;

  public state = {
    loading: true,
    hostDetail: {} as IAgentHostSet,
  }

  constructor(props: any) {
    super(props);
    const url = Url();
    this.hostId = Number(props.location.state?.hostId);
    this.agentId = Number(props.location.state?.agentId);
  }

  public getDetail = () => {
    getHostDetails(this.hostId).then((res: IAgentHostSet) => {
      this.setState({ loading: false, hostDetail: res });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public componentDidMount() {
    this.node.scrollIntoView();
    this.getDetail();
  }

  public render() {
    const { loading, hostDetail } = this.state;
    const health = healthMap[hostDetail?.agentHealthLevel];
    return (
      <div ref={node => this.node = node}>
        <CustomBreadcrumb btns={agentDetailBreadcrumb} />
        <DescriptionsItems
          loading={loading}
          title={hostDetail?.hostName}
          column={3}
          subTitle={health ? <Tag color={health}>{health}</Tag> : ''}
          baseInfo={hostDetailBaseInfo(hostDetail)}
          baseData={hostDetail}
        />
        <div className="detail-wrapper">
          <Tabs animated={false} defaultActiveKey="1">
            <TabPane tab="Agent运行指标" key="1">
              {this.agentId ? <AgentOperationIndex id={this.agentId} {...this.props} /> : <p className='agent-installed'>该主机未安装Agent</p>}
            </TabPane>
            <TabPane tab="Agent配置信息" key="2">
              {this.agentId ? <AgentConfigInfo hostDetail={hostDetail} {...this.props} /> : <p className='agent-installed'>该主机未安装Agent</p>}

            </TabPane>
            <TabPane tab="采集任务" key="3">
              {this.agentId ? <CollectTask hostDetail={hostDetail} {...this.props} /> : <p className='agent-installed'>该主机未安装Agent</p>}
            </TabPane>
          </Tabs>
        </div>
      </div>
    );
  }
}