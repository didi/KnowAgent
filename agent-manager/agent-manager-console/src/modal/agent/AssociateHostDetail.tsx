import React, { useState, useEffect } from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { getHostCollectTaskDetails } from '../../api/collect';
import { IHostDetail } from '../../interface/collect';
import { IAgentHostSet } from '../../interface/agent';
import { DescriptionsItems } from '../../component/CustomComponent';
import { Drawer } from 'antd';
import { hosts, hostBaseInfo } from './config';
import './index.less';

const mapStateToProps = (state: any) => ({
  params: state.modal.params,
});

const AssociateHostDetail = (props: { dispatch: any, params: IAgentHostSet }) => {
  // console.log('props---', props.params);
  const [hostDetail, setHostDetail] = useState(hosts);
  const [loading, setLoading] = useState(true);

  const handleAssociateCancel = () => {
    props.dispatch(actions.setDrawerId(''));
  }

  const getHostDetail = () => {
    getHostCollectTaskDetails(props.params.hostId).then((res: IHostDetail) => {
      setHostDetail(res);
      setLoading(false);
    }).catch((err: any) => {
      setLoading(false);
    });
  }

  useEffect(() => {
    getHostDetail();
  }, []);

  return (
    <Drawer
      title={hostDetail.hostName}
      placement="right"
      closable={false}
      visible={true}
      onClose={handleAssociateCancel}
      width={600}
    >
      <DescriptionsItems
        column={2}
        haveClass={true}
        loading={loading}
        baseInfo={hostBaseInfo(hostDetail)}
        baseData={hostDetail}
      />
    </Drawer>
  )

};

export default connect(mapStateToProps)(AssociateHostDetail);
