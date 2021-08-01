
import * as React from 'react';
import * as actions from '../../actions';
import './index.less';
import { Button, Input, Transfer, Select } from 'antd';
import { CustomBreadcrumb, CommonHead, NavRouterLink } from '../../component/CustomComponent';
import { getCollectListColumns, collectBreadcrumb, getCollectFormColumns } from './config';
import { collectHead, empty } from '../../constants/common';
import { getServices } from '../../api/agent'
import { IService } from '../../interface/agent';
import { ICollectTaskParams, ICollectTaskVo, ICollectTask, ICollectQueryFormColumns } from '../../interface/collect';
import { getCollectTask } from '../../api/collect'
import { connect } from "react-redux";
import { BasicTable } from 'antd-advanced';
import { Dispatch } from 'redux';
import { judgeEmpty } from '../../lib/utils';
import { findDOMNode } from 'react-dom';

const mapStateToProps = (state: any) => ({
  rdbPoints: state.resPermission.rdbPoints
})

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setDrawerId: (drawerId: string, params?: any) => dispatch(actions.setDrawerId(drawerId, params)),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class CollectTaskList extends React.Component<Props> {
  public healthRef = null as any;
  public collectRef = null as any;

  constructor(props: any) {
    super(props)
    this.healthRef = React.createRef();
    this.collectRef = React.createRef();
  }

  public state = {
    loading: true,
    servicesList: [],
    collectTaskList: [],
    total: 0,
    hidefer: false,
    targetKeys: [],
    applyInput: '',
    direction: 'left',
    form: '',
    colectParams: {
      pageNo: 1,
      pageSize: 20,
      serviceIdList: [], // number[] 服务id
      logCollectTaskTypeList: [], //  number[] 采集任务类型 0：常规流式采集 1：按指定时间范围采集
      logCollectTaskHealthLevelList: [], //number[] 日志采集任务健康度  0:红 1：黄 2：绿色
      logCollectTaskId: empty, // 日志采集任务id
      logCollectTaskName: empty, // 日志采集任务名
      locCollectTaskCreateTimeEnd: empty, // 日志采集任务创建时间结束检索时间
      locCollectTaskCreateTimeStart: empty, // 日志采集任务创建时间起始检索时间
    } as ICollectTaskParams,
  }

  public collectListColumns() {
    const getData = () => this.getCollectData(this.state.colectParams);
    const columns = getCollectListColumns(this.props.setDrawerId, getData);
    return columns;
  }

  public filterApplyOption = (inputValue: any, option: any) => {
    return option.servicename.indexOf(inputValue) > -1;
  }

  public onApplyChange = (targetKeys: any, direction: any) => {
    console.log(targetKeys, 'targetKeys')
    console.log(direction, 'direction')
    this.setState({ targetKeys: [targetKeys] })
    // this.setState({ applyInput: `已选择${targetKeys?.length}项`, hidefer: true, targetKeys, direction, });
  };

  public onApplySearch = (dir: any, value: any) => {
    // console.log('search:', dir, value);
  };

  public onInputClick = () => {
    const { targetKeys, direction, servicesList } = this.state;
    let keys = [] as number[];
    if (direction === 'right') {
      keys = targetKeys;
    } else if (direction === 'left') {
      if (targetKeys?.length === servicesList?.length) {
        keys = [];
      } else {
        keys = targetKeys;
      }
    }
    this.setState({
      applyInput: keys?.length ? `已选择${keys?.length}项` : '',
      hidefer: true,
      targetKeys: keys,
    });
  }

  public handleClickOutside(e: any) {
    // 组件已挂载且事件触发对象不在div内
    let result = findDOMNode(this.refs.refTest)?.contains(e.target);
    if (!result) {
      this.setState({ hidefer: false });
    }
  }

  public queryFormColumns() {
    const { servicesList, applyInput, hidefer, targetKeys, form } = this.state;
    const applyObj = {
      type: 'custom',
      title: '采集应用',
      dataIndex: 'serviceIdList',
      component: (
        <div className='apply-box'>
          {/* <Input value={applyInput} placeholder='请选择' onClick={this.onInputClick} /> */}
          <Select
            // mode="multiple"
            onChange={this.onApplyChange}
          // onSearch={this.onApplyChange}
          >
            {
              servicesList.map((v: any) => {
                console.log(v)
                return <Select.Option key={v.id} value={v.id}>{v?.servicename}</Select.Option>
              })
            }
          </Select>
          {/* <Transfer
              dataSource={servicesList}
              showSearch
              filterOption={this.filterApplyOption}
              targetKeys={targetKeys}
              onChange={this.onApplyChange}
              onSearch={this.onApplySearch}
              render={item => item.servicename}
              className="customTransfer"
            /> */}
        </div>
      ),
    };
    const collectColumns = getCollectFormColumns(this.collectRef, this.healthRef, form);
    collectColumns.splice(0, 0, applyObj);
    return collectColumns;
  }

  public setServiceIdList = () => {
    const { servicesList, targetKeys } = this.state;
    const serviceIds = [] as number[];
    servicesList.map((ele: IService, index) => {
      console.log(targetKeys?.includes(index as never), 'targetKeys?.includes(index as never)')
      console.log(ele, 'targetKeys?.includes(index as never)')
      if (targetKeys?.includes(index as never)) {
        serviceIds.push(ele.id);
      }
    });
    return serviceIds;
  }

  public onChangeParams = (values: ICollectQueryFormColumns, form: any) => {
    const { pageNo, pageSize } = this.state.colectParams;
    this.setState({
      form,
      colectParams: {
        pageNo,
        pageSize,
        serviceIdList: this.state.targetKeys, // number[] 服务id
        logCollectTaskTypeList: values?.logCollectTaskTypeList || [], //  number[] 采集任务类型 0：常规流式采集 1：按指定时间范围采集
        logCollectTaskHealthLevelList: values?.logCollectTaskHealthLevelList || [], //number[] 日志采集任务健康度  0:红 1：黄 2：绿色
        logCollectTaskId: judgeEmpty(values?.logCollectTaskId), // 日志采集任务id
        logCollectTaskName: values.logCollectTaskName, // 日志采集任务名
        locCollectTaskCreateTimeStart: values?.locCollectTaskCreateTime?.length ? values.locCollectTaskCreateTime[0]?.valueOf() : '',
        locCollectTaskCreateTimeEnd: values?.locCollectTaskCreateTime?.length ? values.locCollectTaskCreateTime[1]?.valueOf() : '',
      }
    })
  }

  public onSearchParams = () => {
    const { colectParams, targetKeys } = this.state;
    if (!colectParams.serviceIdList?.length && targetKeys?.length) {
      colectParams.serviceIdList = targetKeys;
    }
    this.getCollectData(this.state.colectParams);
  }

  public onResetParams = () => {
    const resetParams = {
      pageNo: 1,
      pageSize: 20,
      serviceIdList: [], // number[] 服务id
      logCollectTaskTypeList: [], //  number[] 采集任务类型 0：常规流式采集 1：按指定时间范围采集
      logCollectTaskHealthLevelList: [], //number[] 日志采集任务健康度  0:红 1：黄 2：绿色
      logCollectTaskId: empty, // 日志采集任务id
      logCollectTaskName: empty, // 日志采集任务名
      locCollectTaskCreateTimeEnd: empty, // 日志采集任务创建时间结束检索时间
      locCollectTaskCreateTimeStart: empty, // 日志采集任务创建时间起始检索时间
    } as ICollectTaskParams;
    this.setState({
      colectParams: resetParams,
      targetKeys: [],
      applyInput: '',
      direction: 'left',
    });
    this.getCollectData(resetParams);
  }

  public onPageChange = (current: number, size: number | undefined) => {
    const { colectParams, targetKeys } = this.state;
    const pageParams = {
      serviceIdList: targetKeys,
      logCollectTaskTypeList: colectParams?.logCollectTaskTypeList || [],
      logCollectTaskHealthLevelList: colectParams?.logCollectTaskHealthLevelList || [],
      logCollectTaskId: colectParams?.logCollectTaskId,
      logCollectTaskName: colectParams?.logCollectTaskName,
      locCollectTaskCreateTimeStart: colectParams?.locCollectTaskCreateTimeStart,
      locCollectTaskCreateTimeEnd: colectParams?.locCollectTaskCreateTimeEnd,
    } as ICollectTaskParams;
    this.setState({ colectParams: pageParams });
    this.getCollectData(pageParams, current, size);
  }

  public getCollectData = (params: ICollectTaskParams, current?: number, size?: number) => {
    params.pageNo = current || 1;
    params.pageSize = size || 20;
    this.setState({ loading: true });
    getCollectTask(params).then((res: ICollectTaskVo) => {
      const data = res?.resultSet?.map((ele: ICollectTask, index: number) => { return { key: index, ...ele } });
      this.setState({
        collectTaskList: data,
        loading: false,
        total: res.total,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public getServicesData = () => {
    getServices().then((res: IService[]) => {
      const data = res.map((ele, index) => {
        return { ...ele, key: index, };
      });
      this.setState({ servicesList: data });
    }).catch((err: any) => {
      // console.log(err);
    });
  }

  public componentWillUnmount = () => {
    this.setState = () => { return };
    document.removeEventListener('mousedown', (e) => this.handleClickOutside(e), false);
  }

  public componentDidMount() {
    const { colectParams } = this.state;
    this.getCollectData(colectParams);
    this.getServicesData();
    document.addEventListener('mousedown', (e) => this.handleClickOutside(e), false);
  }

  public render() {
    const { loading, collectTaskList, total } = this.state;
    const { pageNo, pageSize } = this.state.colectParams;

    return (
      <>
        <CustomBreadcrumb btns={collectBreadcrumb} />
        <div className="list collect-list page-wrapper">
          <CommonHead heads={collectHead} />
          <BasicTable
            rowKey='logCollectTaskId'
            showReloadBtn={false}
            showQueryCollapseButton={true}
            loading={loading}
            reloadBtnPos="left"
            reloadBtnType="btn"
            filterType="none"
            hideContentBorder={true}
            showSearch={false}
            columns={this.collectListColumns()}
            dataSource={collectTaskList}
            isQuerySearchOnChange={false}
            queryFormColumns={this.queryFormColumns()}
            pagination={{
              current: pageNo,
              pageSize: pageSize,
              total,
              showQuickJumper: true,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
              onChange: (current, size) => this.onPageChange(current, size),
              onShowSizeChange: (current, size) => this.onPageChange(current, size),
              showTotal: () => `共 ${total} 条`,
            }}
            queryFormProps={{
              searchText: '查询',
              resetText: '重置',
              onChange: this.onChangeParams,
              onSearch: this.onSearchParams,
              onReset: this.onResetParams,
            }}
            customHeader={
              <div className="t-btn">
                <Button type="primary">
                  <NavRouterLink needToolTip element={'新增任务'} href="/collect/add-task" />
                </Button>
              </div>
            }
          />
        </div>
      </>
    );
  }
};
