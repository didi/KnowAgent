import * as React from 'react';
import * as actions from '../../actions';
import './index.less';
import '../../index.less';
import { DatePicker, Select } from 'antd';
import { CustomBreadcrumb, CommonHead } from '../../component/CustomComponent';
import { getRecordListColumns, recordBreadcrumb } from './config';
import { recordHead } from '../../constants/common';
import { connect } from "react-redux";
import { BasicTable } from 'antd-advanced';
import { IOperationRecordParams } from '../../interface/operationRecord';
import { getRecordList, getRecordModules } from '../../api/operationRecord';

const { Option } = Select;
const { RangePicker } = DatePicker;

const mapDispatchToProps = (dispatch: any) => ({
  setModalId: (modalId: any, params?: any) => dispatch(actions.setModalId(modalId, params))
});

type Props = ReturnType<typeof mapDispatchToProps>;
@connect(null, mapDispatchToProps)
export class OperationRecordList extends React.Component<Props> {

  public state = {
    versionRef: React.createRef(),
    form: '',
    loading: false,
    modulesList: [],
    recordList: [],
    isModalVisible: false,
    total: 0,
    recordParams: {
      pageNo: 1,
      pageSize: 20,
      // moduleId: '',
      // operateId: '',
      // operator: '',
      // beginTime: '',
      // endTime: '',
      // operateTime: ''
      beginTime: "",
      endTime: "",
      moduleId: "",
      operateId: "",
    } as unknown as IOperationRecordParams,
  }
  public handleOk() {
    this.setState({ isModalVisible: false })
  }
  public handleCancel() {
    this.setState({ isModalVisible: false })
  }

  public handleNewRecord = () => {
    this.props.setModalId('ActionRecord');
  }

  public queryFormColumns = (versionRef: any, form: any) => {
    return [
      {
        type: 'custom',
        title: '功能模块',
        dataIndex: 'moduleId',
        component: (
          <Select
            className="searchWidth"
            // mode="multiple"
            placeholder='请选择'
          // ref={versionRef}
          // allowClear={true}
          // showArrow={true}
          // onInputKeyDown={() => {
          //   form.resetFields(['agentOperationTaskStatusList']);
          //   versionRef.current.blur();
          // }}
          // maxTagCount={0}
          // maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
          >
            {/* <Option value={''} key={''}>请选择</Option> */}
            {this.state.modulesList?.map((d: any) =>
              <Option value={d.code} key={d.code}> {d.desc}</Option>
            )}
          </Select>
        ),
      },
      {
        type: 'custom',
        title: '操作',
        dataIndex: 'operateId',
        component: (
          <Select
            className="searchWidth"
            // mode="multiple"
            placeholder='请选择'
          // ref={versionRef}
          // allowClear={true}
          // showArrow={true}
          // onInputKeyDown={() => {
          //   form.resetFields(['agentOperationTaskStatusList']);
          //   versionRef.current.blur();
          // }}
          // maxTagCount={0}
          // maxTagPlaceholder={(values) => values?.length ? `已选择${values?.length}项` : '请选择'}
          >
            {/* <Option value={''} key={''}>请选择</Option> */}
            <Option value={1} key={1}>新增</Option>
            <Option value={2} key={2}>删除</Option>
            <Option value={3} key={3}>编辑</Option>
          </Select>
        ),
      },
      {
        type: 'input',
        title: '操作人',
        placeholder: '请输入操作人',
        dataIndex: 'operator',
      },
      {
        type: 'custom',
        title: '操作时间',
        dataIndex: 'beginTime',
        component: (
          <RangePicker showTime className="searchWidth" />
        ),
      },
    ];
  }

  public onResetParams = () => {
    const resetParams = {
      pageNo: 1,
      pageSize: 20,
      moduleId: '',
      operateId: '',
      // operator: '',
      beginTime: '',
      endTime: '',
      // operateTime: ''
    };
    this.setState({ recordParams: resetParams });
    this.getOperationRecordList(resetParams);
  }

  public getModules = () => {
    getRecordModules().then((res: any) => {
      this.setState({
        modulesList: res?.map((item: any) => {
          return { key: item.code, ...item }
        }),
        loading: false,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public getOperationRecordList = (params: any) => {
    getRecordList(params).then((res: any) => {
      this.setState({
        pageNo: 1,
        pageSize: 20,
        recordList: res?.map((item: any) => {
          return { key: item.id, ...item }
        }),
        loading: false,
        total: res.length,
      });
    }).catch((err: any) => {
      this.setState({ loading: false });
    });
  }

  public onSearchParams = () => {

    this.getOperationRecordList(this.state.recordParams);
  }

  public onChangeParams = (values: IOperationRecordParams, form: any) => {
    const { pageNo, pageSize } = this.state.recordParams;
    this.setState({
      form,
      recordParams: {
        pageNo,
        pageSize,
        moduleId: values.moduleId,
        operateId: values.operateId,
        [`${values.operator?.length > 0 && 'operator'}`]: values.operator,
        // operateTime: values.beginTime?.length ? values.beginTime[0]?.valueOf() : '',
        beginTime: values.beginTime?.length ? values.beginTime[0]?.valueOf() : '',
        endTime: values.beginTime?.length ? values.beginTime[1]?.valueOf() : '',
      }
    })
  }

  public componentDidMount() {
    const { recordParams } = this.state;
    this.getModules();
    this.getOperationRecordList(recordParams);
  }

  public render() {
    return (
      <>
        <CustomBreadcrumb btns={recordBreadcrumb} />
        <div className="record-list page-wrapper">
          <CommonHead heads={recordHead} />
          <BasicTable
            showReloadBtn={false}
            showQueryCollapseButton={true}
            loading={this.state.loading}
            reloadBtnPos="left"
            reloadBtnType="btn"
            filterType="none"
            hideContentBorder={true}
            showSearch={false}
            columns={getRecordListColumns()}
            dataSource={this.state.recordList}
            queryFormColumns={this.queryFormColumns(this.state.versionRef, this.state.form)}
            queryFormProps={{
              searchText: '查询',
              resetText: '重置',
              onChange: this.onChangeParams,
              onSearch: this.onSearchParams,
              onReset: this.onResetParams,
            }}
            pagination={{
              // current: this.state.pageNo,
              // pageSize: this.state.pageSize,
              onChange: (current, size) => {
                this.setState({
                  pageNo: current,
                  pageSize: size
                })
              },
              showTotal: () => `共 ${this.state.total} 条`,
            }}
          />
        </div>
      </>
    );
  }
};

