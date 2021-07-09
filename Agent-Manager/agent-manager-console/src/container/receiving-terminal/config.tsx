
import { ListInfo } from '../../interface/common';
import { timeFormat } from '../../constants/time';
import { renderTooltip } from '../../component/CustomComponent';
import moment from 'moment';

export const clusterBreadcrumb = [{
  label: '接收端管理',
}, {
  label: '集群列表',
}];

export const getClusterListColumns = () => {
  const clusterListColumns = [{
    title: '集群名',
    dataIndex: 'kafkaClusterName',
    key: 'kafkaClusterName',
    sorter: (a: ListInfo, b: ListInfo) => a.kafkaClusterName.charCodeAt(0) - b.kafkaClusterName.charCodeAt(0),
  }, {
    title: '集群地址',
    dataIndex: 'kafkaClusterBrokerConfiguration',
    key: 'kafkaClusterBrokerConfiguration',
  }, {
    title: '生产端初始化属性',
    dataIndex: 'kafkaClusterProducerInitConfiguration',
    key: 'kafkaClusterProducerInitConfiguration',
    with: '45%',
    ellipsis: true,
    render: (t: any) => renderTooltip(t),
  }, {
    title: '新增时间',
    dataIndex: 'createTime',
    key: 'createTime',
    render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
    sorter: (a: ListInfo, b: ListInfo) => a.createTime - b.createTime,
  }, {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
  }];
  return clusterListColumns;
};



