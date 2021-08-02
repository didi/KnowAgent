
import * as React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { Spin, Descriptions, Table } from 'antd';
import { IRdAgentMetrics } from '../../interface/agent';
import moment from 'moment';
import './index.less';
import { getAgentCollectList, getAgentDetails } from '../../api/agent';
import { getCollectTaskConfig } from './config';

interface IAgentOperationIndex {
  id: number
}
const { Item } = Descriptions;

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setModalId: (modalId: string, params?: any) => dispatch(actions.setModalId(modalId, params)),
  setDrawerId: (drawerId: string, params?: any) => dispatch(actions.setDrawerId(drawerId, params)),
});

const dataSource = [
  {
    collectTaskId: '采集任务ID',
    collectPathId: '采集路径ID',
    fileName: '主文件名',
    currentCollectTraffic: '当前采集流量 & 条数/30s',
    currentMaxDelay: '当前最大延迟',
    currentCollectTime: '当前采集时间',
    fileModifyTime: '文件最近修改时间',
    currentLimitTime: '限流时长/30s',
    abnormalTruncationBar: '异常截断条数/30s',
    fileExists: '文件是否存在',
    fileExistsOutofOrder: '文件是否存在乱序',
    fileExistsLogSectionError: '文件是否存在日志切片错误',
    fileFilter: '文件过滤量/30s',
    lastHeartbeatTime: '近一次心跳时间',
    collectStatus: '采集状态',
    collectFileInfo: {
      collectFileName: 'test.log',
      sliceLogTimeStamp: '是',
      logStayCollectTime: '1625285912000',
      whetherSequentialFile: '是',
      whetherCollectFileLast: '是',
      fileNewModifyTime: '1625285912000',
      collectProgress: '已完成'
    },
    lastMetricDetail: {
      "readTimeMean": 35561,
      "filterRemained": 0,
      "channelCapacity": "0",
      "isFileExist": "true",
      "pathId": "70392",
      "type": "hdfs",
      "readCount": 5012,
      "sendTimeMean": 6359,
      "masterFile": "didi.log",
    }
  }
]

type Props = ReturnType<typeof mapDispatchToProps>;
// @connect(mapStateToProps, mapDispatchToProps)
const CollectTask = (props: any) => {
  // console.log(props, 'props')
  const { hostDetail } = props
  const [collectTastData, setCollectTastData] = React.useState<any>([])
  const [loading, setLoading] = React.useState(true)
  // console.log(hostDetail.hostName)
  React.useEffect(() => {
    getAgentCollectList(hostDetail?.hostName).then(res => {
      setCollectTastData(res)
      setLoading(false)
      // console.log(res, 'res')
    })
    // try {
    //   getAgentDetails(hostDetail?.agentId).then(res => {
    //     setAgentDetail(res)
    //     console.log(res)
    //   })
    // } catch (error) {
    //   console.log(error)
    // }
  }, [])
  return <div className='collectTask'>
    <Spin spinning={loading}>
      <Table
        dataSource={collectTastData}
        bordered
        columns={getCollectTaskConfig(props.setDrawerId)}
        scroll={{ x: 2100 }}
        pagination={false}
      />
    </Spin>
  </div>
}

export default connect(null, mapDispatchToProps)(CollectTask)