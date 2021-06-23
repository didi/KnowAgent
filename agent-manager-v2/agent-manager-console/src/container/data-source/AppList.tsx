
import * as React from 'react';
import * as actions from '../../actions';
import './index.less';
import '../../index.less';
import { DatePicker, Button, Modal, message } from 'antd';
import { CustomBreadcrumb, CommonHead } from '../../component/CustomComponent';
import { getDataSourceListColumns, dataSourceBreadcrumb } from './config';
import { dataSourceHead } from '../../constants/common';
import { connect } from "react-redux";
import { BasicTable } from 'antd-advanced';
import { AppDetail } from './AppDetail';
import { IDataSourceParams, IDataSourceVo } from '../../interface/dataSource';
import { getDataSources, getServices, deleteService, getServicesAgentId, getServicesLogCollectTaskId } from '../../api/dataSource';

const { RangePicker } = DatePicker;

const queryFormColumns = [
  {
    type: 'input',
    title: '应用名',
    dataIndex: 'serviceName',
    placeholder: '请输入应用名',
  },
  {
    type: 'custom',
    title: '新增时间',
    dataIndex: 'serviceCreateTimeStart',
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
export class AppList extends React.Component<Props> {

  public state = {
    loading: false,
    isDelete: false,
    isDetailVisible: false,
    detailParams: {},
    serviceList: [],
    id: 0,
    name: '',
    deleteTip: false,
    appList: [],
    total: 0,
    appParams: {
      pageNo: 1,
      pageSize: 20,
      servicename: '',
      serviceCreateTimeStart: '',
      serviceCreateTimeEnd: ''
    } as unknown as IDataSourceParams,
  }

  public handleOk = () => {
    deleteService(this.state.id).then((res: any) => {
      this.setState({ isDelete: false })
      Modal.success({ content: '删除成功！' });
      this.onResetParams();
    }).catch((err: any) => {
      message.error(err.message);
    });
  }

  public handleNewApp = () => {
    this.props.setModalId('ActionApp', {
      cb: () => this.getAppList(this.state.appParams),
    });
  }

  public dataSourceListColumns = () => {
    const columns = getDataSourceListColumns();
    return columns.map((column, index) => {
      if (column.dataIndex === "operation") {
        return {
          ...column,
          render: (text: any, record: any) => (
            <span key={index}>
              <a
                style={{ marginRight: '6px' }}
                onClick={() => {
                  // this.props.setModalId('ActionApp', record);
                  this.props.setModalId('ActionApp', {
                    id: record.id,
                    cb: () => this.getAppList(this.state.appParams),
                  });
                }}
              >
                编辑
              </a>
              <a
                onClick={() => {
                  // let agentId = false;
                  let logCollectTaskId = false;
                  // getServicesAgentId(record.id).then((res: any) => {
                  //   agentId = res
                  getServicesLogCollectTaskId(record.id).then((res: any) => {
                    logCollectTaskId = res
                    this.setState({
                      isDelete: true,
                      id: record.id,
                      name: record.serviceName,
                      // deleteTip: agentId === false && logCollectTaskId === false ? false : true
                      deleteTip: logCollectTaskId === false ? false : true
                    })
                  })
                  // .catch((err: any) => {
                  //   console.log(err);
                  // });
                  // }).catch((err: any) => {
                  //  console.log(err);
                  // });
                }}
              >
                删除
              </a>
            </span>
          ),
        };
      } else if (column.dataIndex === "serviceName") {
        return {
          ...column,
          render: (text: any, record: any) => (
            <span key={index}>
              <a
                style={{ marginRight: '6px' }}
                onClick={() => {
                  this.setState({ isDetailVisible: true, detailParams: record })
                }}
              >
                {text}
              </a>
            </span>
          ),
        };
      }
      return column;
    });
  };
  public onClose = () => {
    this.setState({
      isDetailVisible: false,
    });
  };
  public onResetParams = () => {
    const resetParams = {
      pageNo: 1,
      pageSize: 20,
      servicename: '',
      serviceCreateTimeStart: '',
      serviceCreateTimeEnd: ''
    };
    this.setState({ appParams: resetParams });
    this.getAppList(resetParams);
  }

  public getserviceList = () => {
    getServices().then((res: IDataSourceVo) => {
      const data = res?.resultSet;
      this.setState({
        serviceList: data,
        loading: false,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public getAppList = (params: IDataSourceParams, current?: number, size?: number) => {
    params.pageNo = current ? current : 1;
    params.pageSize = size ? size : 20;
    getDataSources(params).then((res: IDataSourceVo) => {
      const data = res?.resultSet.map(item => {
        return { key: item.id, ...item }
      });
      this.setState({
        appList: data,
        loading: false,
        total: res.total,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public onSearchParams = () => {
    this.getAppList(this.state.appParams);
  }

  public onChangeParams = (values: IDataSourceParams) => {
    const { pageNo, pageSize } = this.state.appParams;
    this.setState({
      appParams: {
        pageNo,
        pageSize,
        servicename: values.serviceName,
        serviceCreateTimeStart: values.serviceCreateTimeStart?.length ? values.serviceCreateTimeStart[0]?.valueOf() : '',
        serviceCreateTimeEnd: values.serviceCreateTimeStart?.length ? values.serviceCreateTimeStart[1]?.valueOf() : '',
      }
    })
  }

  public componentDidMount() {
    this.getAppList(this.state.appParams);
    this.getserviceList();
  }

  public render() {

    return (
      <>
        <CustomBreadcrumb btns={dataSourceBreadcrumb} />
        <div className="dataSource-list page-wrapper">
          <CommonHead heads={dataSourceHead} />
          <BasicTable
            showReloadBtn={false}
            showQueryCollapseButton={true}
            loading={this.state.loading}
            reloadBtnPos="left"
            reloadBtnType="btn"
            filterType="none"
            hideContentBorder={true}
            showSearch={false}
            columns={this.dataSourceListColumns()}
            dataSource={this.state.appList}
            queryFormColumns={queryFormColumns}
            queryFormProps={{
              searchText: '查询',
              resetText: '重置',
              onChange: this.onChangeParams,
              onSearch: this.onSearchParams,
              onReset: this.onResetParams,
            }}
            pagination={{
              current: this.state.appParams.pageNo,
              pageSize: this.state.appParams.pageSize,
              total: this.state.total,
              showQuickJumper: true,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
              onChange: (current, size) => this.getAppList(this.state.appParams, current, size),
              onShowSizeChange: (current, size) => this.getAppList(this.state.appParams, current, size),
              showTotal: () => `共 ${this.state.total} 条`,
            }}
            customHeader={
              <div className="table-button">
                <Button type="primary" onClick={this.handleNewApp}>新增应用</Button>
              </div>
            }
          />
          <Modal title="删除"
            visible={this.state.isDelete}
            onOk={this.handleOk}
            onCancel={() => this.setState({ isDelete: false })}>
            {this.state.deleteTip ? <p>删除应用将会导致该应用相关的采集任务失败，是否确认删除？</p>
              : <p>是否确认删除{this.state.name}应用？</p>}
            <p>删除操作不可恢复，请谨慎操作！</p>
          </Modal>
          <AppDetail isDetailVisible={this.state.isDetailVisible} onClose={this.onClose} detailParams={this.state.detailParams} />
        </div>
      </>
    );
  }
};
