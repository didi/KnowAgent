
import * as React from 'react';
import { BasicTable as Table } from 'antd-advanced';
import { getAssociateHostColumns } from './config';
import { IAgentHostSet } from '../../interface/agent';
import { getHostCollectTaskList } from '../../api/collect'

import './index.less';

interface IAssociateHostProps {
  drawer: any;
  taskId: number;
}

export class AssociateHost extends React.Component<IAssociateHostProps> {

  public state = {
    loading: true,
    hostCollectTasks: [] as IAgentHostSet[],
    pageNo: 1,
    pageSize: 20,
  }

  public getHostCollects() {
    getHostCollectTaskList(this.props.taskId).then((res: IAgentHostSet[]) => {
      const data = res?.map((ele: IAgentHostSet, index: number) => { return { key: index, ...ele } });
      this.setState({
        pageNo: 1,
        pageSize: 20,
        hostCollectTasks: data,
        loading: false,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public componentDidMount() {
    this.getHostCollects();
  }

  public render() {
    const { hostCollectTasks } = this.state;

    return (
      <>
        <Table
          queryMode="compact"
          searchPos="right"
          searchPlaceholder="请输入主机名/IP"
          reloadBtnPos="left"
          reloadBtnType="btn"
          filterType="none"
          loading={this.state.loading}
          showReloadBtn={false}
          showSearch={true}
          columns={getAssociateHostColumns(this.props.drawer)}
          dataSource={hostCollectTasks}
          pagination={{
            current: this.state.pageNo,
            pageSize: this.state.pageSize,
            onChange: (current, size) => {
              this.setState({
                pageNo: current,
                pageSize: size
              })
            },
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </>
    );
  }
}