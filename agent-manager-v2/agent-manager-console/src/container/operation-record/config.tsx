import { ListInfo } from '../../interface/common';
import { timeFormat } from '../../constants/time';
import moment from 'moment';
import { renderTooltip } from '../../component/CustomComponent';

export const recordBreadcrumb = [{
  label: '操作记录列表',
}];

export const getRecordListColumns = () => {
  const recordListColumns = [{
    title: '操作ID',
    dataIndex: 'id',
    key: 'id',
    width: 110,
    sorter: (a: ListInfo, b: ListInfo) => a.id - b.id,
  }, {
    title: '操作时间',
    dataIndex: 'operateTime',
    key: 'operateTime',
    sorter: (a: ListInfo, b: ListInfo) => {
      return moment(a.operateTime).diff(moment(a.operateTime).startOf('date')) - moment(b.operateTime).diff(moment(b.operateTime).startOf('date'))
    },
    render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
  }, {
    title: '操作',
    dataIndex: 'operate',
    key: 'operate',
    render: (t: any) => renderTooltip(t),
  }, {
    title: '功能模块',
    dataIndex: 'module',
    key: 'module',
    render: (t: any) => renderTooltip(t),
  }, {
    title: '详细记录',
    dataIndex: 'content',
    key: 'content',
    render: (t: any) => renderTooltip(t),
    width: 300,
    ellipsis: true
  }, {
    title: '操作人',
    dataIndex: 'operator',
    key: 'operator',
  }];
  return recordListColumns;
};



