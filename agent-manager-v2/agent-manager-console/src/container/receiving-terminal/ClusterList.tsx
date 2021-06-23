
import * as React from 'react';
import * as actions from '../../actions';
import './index.less';
import '../../index.less';
import { DatePicker, Button, Modal, message } from 'antd';
import { CustomBreadcrumb, CommonHead } from '../../component/CustomComponent';
import { getClusterListColumns, clusterBreadcrumb } from './config';
import { clusterHead } from '../../constants/common';
import { connect } from "react-redux";
import { BasicTable } from 'antd-advanced';
import { IReceivingTerminalParams, IReceivingTerminalVo } from '../../interface/receivingTerminal';
import { getReceivingList, deleteReceive, getAgentId, getLogCollectTaskId } from '../../api/receivingTerminal'

const { RangePicker } = DatePicker;

const queryFormColumns = [
  {
    type: 'input',
    title: '集群名',
    dataIndex: 'kafkaClusterName',
    placeholder: '请输入集群名'
  },
  {
    type: 'custom',
    title: '新增时间',
    dataIndex: 'receiverCreateTimeStart',
    component: (
      <RangePicker showTime className="searchWidth" />
    ),
  },
];

const mapStateToProps = (state: any) => ({
  rdbPoints: state.resPermission.rdbPoints
})

const mapDispatchToProps = (dispatch: any) => ({
  setModalId: (modalId: any, params?: any) => dispatch(actions.setModalId(modalId, params))
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class ClusterList extends React.Component<Props> {

  public state = {
    loading: false,
    isModalVisible: false,
    id: 0,
    name: '',
    deleteTip: false,
    total: 0,
    clusterList: [],
    receivingParams: {
      pageNo: 1,
      pageSize: 20,
      kafkaClusterName: '',
      receiverCreateTimeStart: '',
      receiverCreateTimeEnd: ''
    } as unknown as IReceivingTerminalParams,
  }
  public handleOk = () => {
    deleteReceive(this.state.id).then((res: any) => {
      this.setState({ isModalVisible: false })
      Modal.success({ content: '删除成功！' });
      this.onResetParams();
    }).catch((err: any) => {
      message.error(err.message);
    });
  }

  public handleCancel = () => {
    this.setState({ isModalVisible: false })
  }

  public handleNewCluster = () => {
    this.props.setModalId('ActionCluster', {
      cb: () => this.getReceivingTerminalList(this.state.receivingParams),
    });
  }

  public onResetParams = () => {
    const resetParams = {
      pageNo: 1,
      pageSize: 20,
      kafkaClusterName: '',
      receiverCreateTimeStart: '',
      receiverCreateTimeEnd: ''
    };
    this.setState({ receivingParams: resetParams });
    this.getReceivingTerminalList(resetParams);
  }

  public getReceivingTerminalList = (params: IReceivingTerminalParams, current?: number, size?: number) => {
    params.pageNo = current ? current : this.state.receivingParams.pageNo;
    params.pageSize = size ? size : this.state.receivingParams.pageSize;
    getReceivingList(params).then((res: IReceivingTerminalVo) => {
      const data = res?.resultSet.map(item => {
        return { key: item.id, ...item }
      });
      this.setState({
        clusterList: data,
        loading: false,
        total: res.total,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public onSearchParams = () => {
    this.getReceivingTerminalList(this.state.receivingParams);
  }

  public onChangeParams = (values: IReceivingTerminalParams) => {
    const { pageNo, pageSize } = this.state.receivingParams;
    this.setState({
      receivingParams: {
        pageNo,
        pageSize,
        kafkaClusterName: values.kafkaClusterName,
        receiverCreateTimeStart: values.receiverCreateTimeStart?.length ? values.receiverCreateTimeStart[0]?.valueOf() : '',
        receiverCreateTimeEnd: values.receiverCreateTimeStart?.length ? values.receiverCreateTimeStart[1]?.valueOf() : '',
      }
    })
  }

  public componentDidMount() {
    this.getReceivingTerminalList(this.state.receivingParams);
  }

  public clusterListColumns = () => {
    const columns = getClusterListColumns();
    return columns.map((column) => {
      if (column.dataIndex === "operation") {
        return {
          ...column,
          render: (text: any, record: any) => (
            <span>
              <a
                style={{ marginRight: '6px' }}
                onClick={() => {
                  this.props.setModalId('ActionCluster', {
                    record,
                    cb: () => this.getReceivingTerminalList(this.state.receivingParams),
                  });
                }}
              >
                编辑
              </a>
              <a
                onClick={async () => {
                  let agentId = false;
                  let logCollectTaskId = false;
                  getAgentId(record.id).then((res: any) => {
                    agentId = res
                    getLogCollectTaskId(record.id).then((res: any) => {
                      logCollectTaskId = res
                      this.setState({
                        isModalVisible: true,
                        id: record.id,
                        name: record.kafkaClusterName,
                        deleteTip: agentId === false && logCollectTaskId === false ? false : true
                      })
                    })
                  })
                }}
              >
                删除
              </a>
            </span>
          ),
        };
      }
      return column;
    });
  };

  public render() {
    return (
      <>
        <CustomBreadcrumb btns={clusterBreadcrumb} />
        <div className="cluster-list page-wrapper">
          <CommonHead heads={clusterHead} />
          <BasicTable
            showReloadBtn={false}
            showQueryCollapseButton={true}
            loading={this.state.loading}
            reloadBtnPos="left"
            reloadBtnType="btn"
            filterType="none"
            hideContentBorder={true}
            showSearch={false}
            columns={this.clusterListColumns()}
            dataSource={this.state.clusterList}
            queryFormColumns={queryFormColumns}
            queryFormProps={{
              searchText: '查询',
              resetText: '重置',
              onChange: this.onChangeParams,
              onSearch: this.onSearchParams,
              onReset: this.onResetParams,
            }}
            pagination={{
              current: this.state.receivingParams.pageNo,
              pageSize: this.state.receivingParams.pageSize,
              total: this.state.total,
              showQuickJumper: true,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
              onChange: (current, size) => this.getReceivingTerminalList(this.state.receivingParams, current, size),
              onShowSizeChange: (current, size) => this.getReceivingTerminalList(this.state.receivingParams, current, size),
              showTotal: () => `共 ${this.state.total} 条`,
            }}
            customHeader={
              <div className="table-button">
                <Button type="primary" onClick={this.handleNewCluster}>新增集群</Button>
              </div>
            }
          />
          <Modal title="删除"
            visible={this.state.isModalVisible}
            onOk={this.handleOk}
            onCancel={() => this.setState({ isModalVisible: false })}>
            {this.state.deleteTip ? <p style={{ fontSize: '13px' }}>{this.state.name}存在关联的Agent及采集任务，删除可能导致Agent和采集任务运行异常，是否继续？</p>
              : <p>是否确认删除{this.state.name}？</p>}
            <p>删除操作不可恢复，请谨慎操作！</p>
          </Modal>
        </div>
      </>
    );
  }
};

