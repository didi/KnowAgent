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
import { timeFormat } from '../../constants/time';
import moment from 'moment';
const { Item } = Descriptions;

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const CollectFileInfoDetail = (props: { dispatch: any, params: any }) => {
  const collectFileInfo = props.params
  const handleAssociateCancel = () => {
    props.dispatch(actions.setDrawerId(''));
  }
  console.log(props.params)

  const collectFileConfig = (info: any) => {
    return getCollectFileInfo(info).map((item, index) => {
      return <Row type='flex' key={'info' + index} >
        <Col span={8}>
          <span style={{ fontWeight: "bold" }}>{item.label + ":"}</span>
        </Col>
        <Col span={12}>
          <span>{
            item.key === 'logTime' || item.key === 'lastModifyTime'
              ?
              moment(info[item.key] * 1).format(timeFormat)
              :
              typeof info[item.key] == 'boolean' ?
                info[item.key] ? '是' : '否'
                :
                (info[item.key] || '')
          }</span>
        </Col>
      </Row>
    })
  }

  return (
    <Drawer
      title={'采集文件信息'}
      placement="right"
      closable={false}
      visible={true}
      onClose={handleAssociateCancel}
      width={500}
      className='collectFileInfoDrawer'
    >
      {
        collectFileInfo && collectFileInfo.map((item: any, index: number) => {
          return collectFileConfig(item)
        })
      }
    </Drawer>
  )

};

export default connect(mapStateToProps)(CollectFileInfoDetail);
