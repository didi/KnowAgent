import * as React from 'react';
import { Drawer } from 'antd';
import { BasicTable as Table } from 'antd-advanced';
import { detailColumns } from './config';
import { ILogDataSource, IDataSourceVo } from '../../interface/dataSource';
import { getServiceDetail } from '../../api/dataSource';
import moment from 'moment';
import { timeFormat } from '../../constants/time';

interface Props {
  isDetailVisible: boolean;
  detailParams: any;
  onClose: any;
}

const queryFormColumns = [
  {
    type: 'input',
    title: '关联主机',
    dataIndex: 'hostName' || 'ip',
    placeholder: '请输入主机名/IP',
    className: 'associated-host'
  },
];
export class AppDetail extends React.Component<Props> {
  public state = {
    loading: false,
    visible: false,
    detailList: []
  }
  constructor(props: any) {
    super(props);
  }
  componentWillReceiveProps(nextProps: any) {
    this.setState({
      visible: nextProps.isDetailVisible
    })
    nextProps.isDetailVisible ? this.getAppDetail(nextProps.detailParams.id) : '';
  }
  onClose = () => {
    this.props.onClose();
  };
  dataDetailtColumns = () => {
    return detailColumns.map((column) => {
      return column;
    });
  };

  public getAppDetail = (params: ILogDataSource) => {
    getServiceDetail(params).then((res: IDataSourceVo) => {
      this.setState({
        detailList: res.hostList?.map(item => {
          return { key: item.id, ...item }
        }),
        loading: false,
        total: res.total,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  render() {
    return (
      <Drawer
        title="应用详情"
        width={520}
        onClose={this.onClose}
        visible={this.state.visible}
        className="app-detail"
      >
        <ul className="app-detail-head">
          <li>应用名: {this.props.detailParams && this.props.detailParams.serviceName}</li>
          <li>新增时间: {moment(this.props.detailParams && this.props.detailParams.createTimet).format(timeFormat)}</li>
        </ul>
        <Table
          className="custom-table"
          tableTitle="关联主机列表"
          queryMode="compact"
          searchPos="right"
          searchPlaceholder="请输入主机名/IP"
          reloadBtnPos="left"
          reloadBtnType="btn"
          filterType="none"
          loading={this.state.loading}
          showReloadBtn={false}
          showSearch={true}
          columns={detailColumns}
          dataSource={this.state.detailList}
        />
      </Drawer>
    );
  }
}