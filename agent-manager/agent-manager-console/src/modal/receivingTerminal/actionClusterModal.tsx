import React from 'react'
import { Modal, Icon, Button } from 'antd';
import { withRouter } from "react-router-dom";
import { getMetricsErrorlogs } from '../../api/agent'
import './index.less'

const ActionClusterModal = (props: any) => {
  const [isVisible, setIsVisble] = React.useState(false)
  React.useEffect(() => {
    getMetricsErrorlogs().then(res => {
      console.log(res, 'res')
      if (res.code == 0 || res.code == 200) {
        setIsVisble(false)
      } else {
        setIsVisble(true)
      }
    })
    // normal/receivers/global-agent-errorlogs-metrics-receiver-exists
    console.log('ActionClusterModal')
  }, [])

  const letsGoClick = () => {
    setIsVisble(false)
    props.history.push('/receivingTerminal/clusterList')
  }

  return isVisible ? (
    <Modal
      visible={isVisible}
      keyboard={false}
      closable={false}
      footer={
        <Button type='primary' onClick={letsGoClick}>立即前往</Button>
      }
    >
      <div className='metricsErrorLogsTopic'>
        <div>
          <Icon type="question-circle" theme="filled" style={{ fontSize: '30px', color: '#eb8235' }} />
        </div>
        <div>
          <span>Agent注册前，需要配置默认指标流和错误日志流接收集群</span>
          <span>请前往<a onClick={letsGoClick}>数据源管理-Kafka集群</a>进行统一配置</span>
        </div>
      </div>
    </Modal>
  ) : null
}

export default withRouter(ActionClusterModal)
