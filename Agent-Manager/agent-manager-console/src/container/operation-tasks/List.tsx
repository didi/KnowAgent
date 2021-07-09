
import * as React from 'react';
import * as actions from '../../actions';
import './index.less';
import '../../index.less';
import { DatePicker, Select } from 'antd';
import { CustomBreadcrumb, CommonHead } from '../../component/CustomComponent';
import { getTaskListColumns, taskBreadcrumb } from './config';
import { taskHead } from '../../constants/common';
import { connect } from "react-redux";
import { BasicTable } from 'antd-advanced';
import { Dispatch } from 'redux';
import { IOperationTaskParams } from '../../interface/operationTask';
import { getOperationTasks } from '../../api/operationTasks';
import moment from 'moment';

const { RangePicker } = DatePicker;
const { Option } = Select;

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setModalId: (modalId: string, params?: any) => dispatch(actions.setModalId(modalId, params)),
  // setDrawerId: (drawerId: string, params?: any) => dispatch(actions.setDrawerId(drawerId, params)),
});

type Props = ReturnType<typeof mapDispatchToProps>;
@connect(null, mapDispatchToProps)
export class TaskList extends React.Component<Props> {

  public state = {
    versionRef: React.createRef(),
    form: '',
    loading: false,
    taskList: [],
    total: 0,
    taskParams: {
      agentOperationTaskCreateTimeStart: '', //任务创建时间开始检索时间
      agentOperationTaskId: '', //任务 id
      agentOperationTaskName: '', //任务名
      agentOperationTaskStatusList: [], //任务状态 100：完成 30：执行中
      agentOperationTaskTypeList: [], //任务类型 0:安装 1：卸载 2：升级
      pageNo: 1,
      pageSize: 20
    }
  }

  public queryFormColumns = (versionRef: any, form: any) => {
    return [
      {
        type: 'custom',
        title: '任务类型',
        dataIndex: 'agentOperationTaskTypeList',
        component: (
          <Select
            className="searchWidth"
            mode="multiple"
            placeholder='请选择'
            ref={versionRef}
            allowClear={true}
            showArrow={true}
            onInputKeyDown={() => {
              form.resetFields(['agentOperationTaskTypeList']);
              versionRef.current.blur();
            }}
            maxTagCount={0}
            maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}>
            <Option value={0} key={0}>安装</Option>
            <Option value={1} key={1}>卸载</Option>
            <Option value={2} key={2}>升级</Option>
          </Select>
        ),
      },
      {
        type: 'custom',
        title: '状态',
        dataIndex: 'agentOperationTaskStatusList',
        component: (
          <Select
            className="searchWidth"
            mode="multiple"
            placeholder='请选择'
            ref={versionRef}
            allowClear={true}
            showArrow={true}
            onInputKeyDown={() => {
              form.resetFields(['agentOperationTaskStatusList']);
              versionRef.current.blur();
            }}
            maxTagCount={0}
            maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}>
            <Option value={0} key={0}>完成</Option>
            <Option value={1} key={1}>执行中</Option>
          </Select>
        ),
      },
      {
        type: 'input',
        title: '任务名',
        dataIndex: 'agentOperationTaskName',
        placeholder: '请输入任务名'
      },
      {
        type: 'input',
        title: '任务ID',
        dataIndex: 'agentOperationTaskId',
        placeholder: '请输入任务ID'
      },
      {
        type: 'custom',
        title: '开始时间',
        dataIndex: 'agentOperationTaskCreateTimeStart',
        component: (
          <RangePicker showTime={{
            defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],
          }} className="searchWidth" />
        ),
      },
    ];
  }

  public taskListColumns() {
    ;
    const columns = getTaskListColumns(this.props.setModalId);

    return columns;
  }

  public onResetParams = () => {
    const resetParams = {
      agentOperationTaskCreateTimeStart: '',
      agentOperationTaskStartTimeEnd: '',
      agentOperationTaskId: '',
      agentOperationTaskName: '',
      agentOperationTaskStatusList: [],
      agentOperationTaskTypeList: [],
      pageNo: 1,
      pageSize: 20
    };
    this.setState({ taskParams: resetParams });
    this.getOperationTaskList(resetParams);
  }

  public getOperationTaskList = (params: IOperationTaskParams, current?: any, size?: any) => {
    params.pageNo = current ? current : 1;
    params.pageSize = size ? size : 20;
    getOperationTasks(params).then((res: any) => {
      this.setState({
        taskList: res?.resultSet.map((item: any) => {
          return { key: item.agentOperationTaskId, ...item }
        }),
        loading: false,
        total: res.total,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public onSearchParams = () => {
    this.getOperationTaskList(this.state.taskParams);
  }

  public onChangeParams = (values: IOperationTaskParams, form: any) => {
    const { pageNo, pageSize } = this.state.taskParams;
    this.setState({
      form,
      taskParams: {
        pageNo,
        pageSize,
        agentOperationTaskCreateTimeStart: values.agentOperationTaskCreateTimeStart?.length ? values.agentOperationTaskCreateTimeStart[0]?.valueOf() : '',
        agentOperationTaskStartTimeEnd: values.agentOperationTaskCreateTimeStart?.length ? values.agentOperationTaskCreateTimeStart[1]?.valueOf() : '',
        agentOperationTaskId: values.agentOperationTaskId, //任务 id
        agentOperationTaskName: values.agentOperationTaskName, //任务名
        agentOperationTaskStatusList: values.agentOperationTaskStatusList, //任务状态 100：完成 30：执行中
        agentOperationTaskTypeList: values.agentOperationTaskTypeList, //任务类型 0:安装 1：卸载 2：升级
      }
    })
  }

  public componentDidMount() {
    this.getOperationTaskList(this.state.taskParams);
  }

  public render() {
    return (
      <>
        <CustomBreadcrumb btns={taskBreadcrumb} />
        <div className="tasks-list page-wrapper">
          <CommonHead heads={taskHead} />
          <BasicTable
            showReloadBtn={true}
            showQueryCollapseButton={true}
            loading={this.state.loading}
            reloadBtnPos="left"
            reloadBtnType="btn"
            onReload={this.onResetParams}
            filterType="none"
            hideContentBorder={true}
            showSearch={false}
            columns={this.taskListColumns()}
            dataSource={this.state.taskList}
            queryFormColumns={this.queryFormColumns(this.state.versionRef, this.state.form)}
            queryFormProps={{
              searchText: '查询',
              resetText: '重置',
              onChange: this.onChangeParams,
              onSearch: this.onSearchParams,
              onReset: this.onResetParams,
            }}
            pagination={{
              current: this.state.taskParams.pageNo,
              pageSize: this.state.taskParams.pageSize,
              total: this.state.total,
              showQuickJumper: true,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
              onChange: (current, size) => this.getOperationTaskList(this.state.taskParams, current, size),
              onShowSizeChange: (current, size) => this.getOperationTaskList(this.state.taskParams, current, size),
              showTotal: () => `共 ${this.state.total} 条`,
            }}
          />
        </div>
      </>
    );
  }
};
