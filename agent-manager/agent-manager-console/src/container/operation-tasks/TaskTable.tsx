import * as React from 'react';
import { BasicTable as Table } from 'antd-advanced';
import { Drawer } from 'antd';
import { getTaskTableColumns } from './config';
import './index.less';
import { getTaskHost } from '../../api/operationTasks';

interface Props {
  detailList: any;
  total: any;
  agentOperationTaskId: any;
}
export class TaskTable extends React.Component<Props> {
  public state = {
    loading: false,
    visible: false,
    total: {
      execution: 0,
      failed: 0,
      pending: 0,
      successful: 0,
      total: 0
    },
    details: '',
    detailList: [],
    agentOperationTaskId: '',
  }
  constructor(props: any) {
    super(props);
  }
  componentWillReceiveProps(nextProps: any) {
    this.setState({
      total: nextProps.total,
      detailList: nextProps.detailList,
      agentOperationTaskId: nextProps.agentOperationTaskId,
    })
  }
  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  public dataSourceListColumns = () => {
    const columns = getTaskTableColumns();
    return columns.map((column) => {
      if (column.dataIndex === "operation") {
        return {
          ...column,
          render: (text: any, record: any) => (
            <span>
              <a
                style={{ marginRight: '6px' }}
                onClick={() => {
                  this.setState({ visible: true })
                  this.getOperationTaskList(record.hostName)
                }}
              >
                查看日志
              </a>
            </span>
          ),
        };
      }
      return column;
    });
  };
  public getOperationTaskList = (hostName: any) => {
    getTaskHost(this.state.agentOperationTaskId, hostName).then((res: any) => {
      this.setState({ details: res, loading: false })
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public render() {
    return (
      <>
        <Table
          queryMode="compact"
          className="task-table"
          searchPos="right"
          searchPlaceholder="请输入主机名/IP"
          reloadBtnPos="left"
          reloadBtnType="btn"
          filterType="none"
          loading={this.state.loading}
          showReloadBtn={false}
          showSearch={true}
          columns={this.dataSourceListColumns()}
          dataSource={this.state.detailList}
          customHeader={
            <div className="table-button">
              <h2>
                执行进度
              </h2>
              <ul>
                <li>总数：{this.state.total.total}</li>
                <li><span className="green-dot"></span>成功：{this.state.total.successful}</li>
                <li><span className="red-dot"></span>失败：{this.state.total.failed}</li>
                <li><span className="blue-dot"></span>执行中：{this.state.total.execution}</li>
                <li><span className="skyblue-dot"></span>待执行：{this.state.total.pending}</li>
              </ul>
            </div>
          }
        />
        <Drawer
          title="日志详情"
          width={520}
          onClose={this.onClose}
          visible={this.state.visible}>
          <pre style={{ whiteSpace: "pre-wrap" }}>
            {this.state.details}
          </pre>
        </Drawer>
      </>
    );
  }
}