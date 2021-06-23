import { ListInfo } from '../../interface/common';
import moment from 'moment';
import { timeFormat } from '../../constants/time';
import { renderTooltip } from '../../component/CustomComponent';

export const dataSourceBreadcrumb = [{
  label: '数据源管理',
}, {
  label: '应用列表',
}];

export const agentDetailBreadcrumb = [{
  aHref: '/list',
  label: '主机列表',
}, {
  label: '详情',
}];

export const getDataSourceListColumns = () => {
  const dataSourceListColumns = [{
    title: '应用名',
    dataIndex: 'serviceName',
    key: 'serviceName',
    sorter: (a: ListInfo, b: ListInfo) => a.serviceName.charCodeAt(0) - b.serviceName.charCodeAt(0),
  }, {
    title: '关联主机数',
    dataIndex: 'relationHostCount',
    key: 'relationHostCount',
    render: (t: any) => renderTooltip(t),
    sorter: (a: ListInfo, b: ListInfo) => a.relationHostCount - b.relationHostCount,
  }, {
    title: '新增时间',
    dataIndex: 'createTime',
    key: 'createTime',
    sorter: (a: ListInfo, b: ListInfo) => a.createTime - b.createTime,
    render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
  }, {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
  }];
  return dataSourceListColumns;
};

export const detailColumns = [{
  title: '主机名',
  dataIndex: 'hostName',
  key: 'hostName',
}, {
  title: '主机IP',
  dataIndex: 'ip',
  key: 'ip',
}, {
  title: '主机类型',
  dataIndex: 'container',
  key: 'container',
  sorter: (a: ListInfo, b: ListInfo) => a.container - b.container,
  render: (text: any) => {
    return text == 0 ? '主机' : text == 0 ? '容器' : renderTooltip(text)
  }
}];


