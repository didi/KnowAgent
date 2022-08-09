import React, { useState, useEffect } from 'react';
import { getHostCollectTaskDetails } from '../../api/collect';
import { IHostDetail } from '../../interface/collect';
import { ProDescriptions, Drawer } from '@didi/dcloud-design';
import { hostBaseInfo } from './config';

const AssociateHostDetail = (props: any) => {
  // console.log('props---', props.params);
  const [hostDetailData, setHostDetail] = useState<any>({});
  // const [loading, setLoading] = useState(true);
  console.log(props);
  const { setVisible, visible } = props;

  const handleAssociateCancel = () => {
    setVisible(false); // 关闭弹窗
  };

  const getHostDetail = () => {
    getHostCollectTaskDetails(props.hostDetailData?.hostId)
      .then((res: IHostDetail) => {
        // console.log(res, 'sadasd');
        setHostDetail(res);
        // setLoading(false);
      })
      .catch((err: any) => {
        // setLoading(false);
      });
  };

  useEffect(() => {
    visible && getHostDetail();
  }, [props]);

  return (
    <Drawer title={hostDetailData.hostName} placement="right" visible={visible} onClose={handleAssociateCancel} width={600}>
      <ProDescriptions column={{ xxl: 2 }} config={hostBaseInfo(hostDetailData)} dataSource={hostDetailData} />
    </Drawer>
  );
};

export default AssociateHostDetail;
