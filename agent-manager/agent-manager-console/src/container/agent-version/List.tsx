
import * as React from 'react';
import * as actions from '../../actions';
import './index.less';
import '../../index.less';
import { DatePicker, Button, Select, Tooltip } from 'antd';
import { CustomBreadcrumb, CommonHead } from '../../component/CustomComponent';
import { getVersionListColumns, versionBreadcrumb } from './config';
import { versionHead } from '../../constants/common';
import { connect } from "react-redux";
import { BasicTable } from 'antd-advanced';
import { Dispatch } from 'redux';
import { IAgentVersionParams, IVersion, IAgentVersionVo } from '../../interface/agentVersion';
import { getAgentVersion, getVersion } from '../../api/agentVersion';
import moment from 'moment';

const { Option } = Select;
const { RangePicker } = DatePicker;

const mapStateToProps = (state: any) => ({
  rdbPoints: state.resPermission.rdbPoints
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setModalId: (modalId: string, params?: any) => dispatch(actions.setModalId(modalId, params)),
  setDrawerId: (drawerId: string, params?: any) => dispatch(actions.setDrawerId(drawerId, params)),
});


type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;

@connect(mapStateToProps, mapDispatchToProps)

// export 
export class VerSionList extends React.Component<Props> {
  public state = {
    versionRef: React.createRef(),
    form: '',
    loading: false,
    includedInstalled: false,
    noInstalledVersions: [],
    versionList: [],
    version: [],
    total: 0,
    versionParams: {
      pageNo: 1,
      pageSize: 20,
      agentVersionList: [],
      agentPackageName: '',
      agentVersionCreateTimeStart: '',
      agentVersionCreateTimeEnd: ''
    } as unknown as IAgentVersionParams,
  }

  public versionListColumns() {
    const getData = () => this.getVersionList(this.state.versionParams);
    const columns = getVersionListColumns(this.props.setModalId, this.props.setDrawerId, getData);
    return columns;
  }

  public handleNewVersion = () => {
    this.props.setModalId('ActionVersion');
    this.props.setModalId('ActionVersion', {
      cb: () => this.getVersionList(this.state.versionParams),
    });
  }

  public queryFormColumns = (versionRef: any, form: any) => {
    return [
      {
        type: 'custom',
        title: '版本号',
        dataIndex: 'agentVersionList',
        component: (
          <Select
            className="searchWidth"
            // mode="multiple"
            placeholder="请选择"
            ref={versionRef}
            allowClear={true}
            showArrow={true}
          // onInputKeyDown={() => {
          //   form.resetFields(['agentVersionList']);
          //   versionRef.current.blur();
          // }}
          // maxTagCount={0}
          // maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
          >
            {/* <Option value="">请选择</Option> */}
            {this.state.version.map((d: any) =>
              <Option value={d.agentVersion} key={d.agentVersionId}>{d.agentVersion}</Option>
            )}
          </Select>
        ),
      },
      {
        type: 'input',
        title: '版本包',
        dataIndex: 'agentPackageName',
        placeholder: "请输入版本包"
      },
      {
        type: 'custom',
        title: '新增时间',
        dataIndex: 'agentVersionCreateTimeStart',
        component: (
          <RangePicker showTime={{
            defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],
          }} style={{ width: '100%' }} className="searchWidth" />
        ),
      },
    ];
  }

  public onResetParams = () => {
    const resetParams = {
      pageNo: 1,
      pageSize: 20,
      agentVersionList: [],
      agentPackageName: '',
      agentVersionCreateTimeStart: '',
      agentVersionCreateTimeEnd: ''
    };
    this.setState({ versionParams: resetParams });
    this.getVersionList(resetParams);
  }

  public getVersionData = () => {
    getVersion().then((res: IVersion) => {
      this.setState({
        version: res,
        loading: false,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public getVersionList = (params: IAgentVersionParams, current?: any, size?: any) => {
    params.pageNo = current ? current : 1;
    params.pageSize = size ? size : 20;
    getAgentVersion(params).then((res: IAgentVersionVo) => {
      const data = res?.resultSet.map(item => {
        return { key: item.agentVersion, ...item }
      });
      this.setState({
        versionList: data,
        loading: false,
        total: res.total,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public onSearchParams = () => {
    this.getVersionList(this.state.versionParams);
  }

  public onChangeParams = (values: IAgentVersionParams, form: any) => {
    const { pageNo, pageSize } = this.state.versionParams;
    this.setState({
      form,
      versionParams: {
        pageNo,
        pageSize,
        agentVersionList: values.agentVersionList ? [values.agentVersionList] : [],
        agentPackageName: values.agentPackageName,
        agentVersionCreateTimeStart: values.agentVersionCreateTimeStart?.length ? values.agentVersionCreateTimeStart[0]?.valueOf() : '',
        agentVersionCreateTimeEnd: values.agentVersionCreateTimeStart?.length ? values.agentVersionCreateTimeStart[1]?.valueOf() : '',
      }
    })
  }

  public componentDidMount() {
    const { versionParams } = this.state;
    this.getVersionList(versionParams);
    this.getVersionData();
  }

  public render() {
    const { rdbPoints } = this.props
    return (
      <>
        <CustomBreadcrumb btns={versionBreadcrumb} />
        <div className="version-list page-wrapper">
          <CommonHead heads={versionHead} />
          <BasicTable
            showReloadBtn={false}
            // showQueryCollapseButton={true}
            showQueryCollapseButton={this.versionListColumns().length > 2 ? true : false}
            loading={this.state.loading}
            reloadBtnPos="left"
            reloadBtnType="btn"
            filterType="none"
            hideContentBorder={true}
            showSearch={false}
            columns={this.versionListColumns()}
            dataSource={this.state.versionList}
            queryFormColumns={this.queryFormColumns(this.state.versionRef, this.state.form)}
            pagination={{
              current: this.state.versionParams.pageNo,
              pageSize: this.state.versionParams.pageSize,
              total: this.state.total,
              showQuickJumper: true,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
              onChange: (current, size) => this.getVersionList(this.state.versionParams, current, size),
              onShowSizeChange: (current, size) => this.getVersionList(this.state.versionParams, current, size),
              showTotal: () => `共 ${this.state.total} 条`,
            }}
            queryFormProps={{
              searchText: '查询',
              resetText: '重置',
              onChange: this.onChangeParams,
              onSearch: this.onSearchParams,
              onReset: this.onResetParams,
            }}
            customHeader={!rdbPoints?.agent_version_add ?
              <div className="table-button">
                <Tooltip title='更多功能请关注商业版' placement="top">
                  <Button disabled type="primary" onClick={this.handleNewVersion}>新增版本</Button>
                </Tooltip>
              </div> : null
            }
          />
        </div>
      </>
    );
  }
};
