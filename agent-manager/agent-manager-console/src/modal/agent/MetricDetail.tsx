import React, { useState, useEffect } from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { getHostCollectTaskDetails } from '../../api/collect';
import { IHostDetail } from '../../interface/collect';
import { IAgentHostSet } from '../../interface/agent';
import { DescriptionsItems } from '../../component/CustomComponent';
import { Drawer, Descriptions, Row, Col } from 'antd';
import { hosts, getCollectFileInfo } from './config';
import './index.less';
const { Item } = Descriptions;

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const MetricDetail = (props: { dispatch: any, params: any }) => {
  console.log(props, 'MetricDetail')
  const lastMetricDetail = props.params
  let metricDetail = JSON.stringify(lastMetricDetail, null, 2)
  const handleAssociateCancel = () => {
    props.dispatch(actions.setDrawerId(''));
  }

  return (
    <Drawer
      title={'指标详情信息'}
      placement="right"
      closable={false}
      visible={true}
      onClose={handleAssociateCancel}
      width={600}
      className='metricDetail'
    >
      {
        <pre>
          {metricDetail}
        </pre>
      }
    </Drawer>
  )

};

export default connect(mapStateToProps)(MetricDetail);
