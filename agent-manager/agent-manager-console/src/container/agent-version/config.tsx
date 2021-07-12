
import * as React from 'react';
import { Modal, message } from 'antd';
import { ListInfo } from '../../interface/common';
import { renderOperationBtns, IBtn, renderTooltip } from '../../component/CustomComponent';
import { download, deleteAgentVersion } from '../../api/agentVersion';
import moment from 'moment';
import { timeFormat } from '../../constants/time';
import store from '../../store';
const { confirm } = Modal;

export const versionBreadcrumb = [{
  label: 'Agent中心',
}, {
  label: 'Agent版本',
}];

export const getVersionListColumns = (cb: any, drawer: any, getData: any) => {
  const versionListColumns = [{
    title: '版本号',
    dataIndex: 'agentVersion',
    key: 'agentVersion',
    width: '10%',
    sorter: (a: ListInfo, b: ListInfo) => a.agentVersion - b.agentVersion,
  }, {
    title: '版本包',
    dataIndex: 'agentPackageName',
    key: 'agentPackageName',
    width: '10%',
    ellipsis: true,
    render: (t: string) => renderTooltip(t),

  }, {
    title: '版本描述',
    dataIndex: 'agentVersionDescription',
    key: 'agentVersionDescription',
    width: '20%',
    ellipsis: true,
    render: (t: string) => renderTooltip(t),
  }, {
    title: '新增时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: '10%',
    render: (t: any) => t ? moment(t).format(timeFormat) : renderTooltip(t),
    sorter: (a: ListInfo, b: ListInfo) => a.createTime - b.createTime,
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
    width: '15%',
    render: (text: any, record: any) => {
      const btns = getVersionListBtns(record, cb, getData);
      return renderOperationBtns(btns, record);
    },
  },
  ];
  return versionListColumns;
};

export const getVersionListBtns = (record: any, cb: any, getData: any): IBtn[] => [{
  label: '编辑',
  needTooltip: true,
  // invisible: !store.getState()?.resPermission.rdbPoints?.agent_version_edit,
  // clickFunc: (record: any) => {
  //   const params = { record, getData }
  //   cb('ActionVersion', params);
  // },
}, {
  label: '下载',
  clickFunc: (record: any) => {
    download(record.agentVersionId).then((res: any) => {
      // window.open(`${window.origin}/api/v1/op/version/${record.agentVersionId}`)
      window.open(res)
    }).catch((err: any) => {
      message.error(err.message);
    });
  },
}, {
  label: '删除',
  needTooltip: true
  // invisible: !store.getState()?.resPermission.rdbPoints?.agent_version_delete,
  // clickFunc: (record: any) => {
  //   deleteVersionClick(record, getData);
  // },
}];

const deleteVersionClick = (record: any, getData: any) => {
  confirm({
    title: `是否确认删除${record.agentVersion}？`,
    content: <span className="fail">删除操作不可恢复，请谨慎操作！</span>,
    okText: '确认',
    cancelText: '取消',
    onOk() {
      deleteAgentVersion(record.agentVersionId).then((res: any) => {
        message.success('删除成功');
        getData();
      }).catch((err: any) => {
        message.error('该版本包有Agent使用！如需删除，请先更改Agent版本');
      });
    }
  });
}

