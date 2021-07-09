import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Drawer, Descriptions, Tag, Table } from 'antd';
import { IBaseInfo } from '../../interface/common';
import { NavRouterLink } from '../../component/CustomComponent';
import { reportInfo, getReportColumns } from './config';
import './index.less';

const { Item } = Descriptions;

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const data: any = {
  title: 'es-newcluster4',
  ip: '1',
  type: '2',
  name: '3',
  agent: '4',
  health: 'yellow',
  time: '2020-11-12-09:12:08',
};

const dataSource = [
  {
    key: '1',
    name: 'xxxx是否存在',
    age: '不存在',
    address: '存在',
    di: '其上采集任务运行异常',
    base: '路径未配置',
    view: '至“Agent管理”配置路径',
  },
];

const DiagnosisReport = (props: { dispatch: any, params: any }) => {
  // console.log('props---', props.params);

  const handleModifyCancel = () => {
    props.dispatch(actions.setDrawerId(''))
  }

  return (
    <Drawer
      title="诊断报告"
      placement="right"
      closable={false}
      visible={true}
      onClose={handleModifyCancel}
      width={800}
    >
      <div className="diagnosis-report">
        <Descriptions>
          {reportInfo.map((ele: IBaseInfo, index: number) => {
            const value = data?.[ele.key];
            return (
              <Item key={index} label={ele.label}>
                {ele.label === '健康度' ? <Tag color='orange'>{value}</Tag> : value}
              </Item>
            )
          })}
        </Descriptions>
        <h3>异常指标及处理建议</h3>
        <Table dataSource={dataSource} columns={getReportColumns()} />
        <div onClick={handleModifyCancel}>
          <NavRouterLink needToolTip element='查看更多指标数据>>' href="/detail" />
        </div>
      </div>
    </Drawer>
  )

};

export default connect(mapStateToProps)(DiagnosisReport);
