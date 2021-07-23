
import { IBaseInfo } from '../../interface/common';
import { hostTypeMap } from '../../constants/common';
import { IHostDetail } from '../../interface/collect';

export const reportInfo: IBaseInfo[] = [{
  label: '主机名',
  key: 'name',
}, {
  label: '主机IP',
  key: 'ip',
}, {
  label: '主机类型',
  key: 'type',
}, {
  label: '健康度',
  key: 'health',
}, {
  label: '诊断时间',
  key: 'time',
}];

export const getReportColumns = () => {

  const reportColumns = [
    {
      title: '异常指标',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '指标值',
      dataIndex: 'age',
      key: 'age',
    },
    {
      title: '安全阀值',
      dataIndex: 'address',
      key: 'address',
    },
    {
      title: '潜在风险',
      dataIndex: 'di',
      key: 'di',
    },
    {
      title: '风险原因',
      dataIndex: 'base',
      key: 'base',
    },
    {
      title: '处理意见',
      dataIndex: 'view',
      key: 'view',
    },
  ];

  return reportColumns;
};


export const hosts = {
  container: 0, // 主机类型 0：主机 1：容器
  department: '基础', // 主机所属部门
  id: 1, // 主机id
  hostName: 'wll', // 主机名
  ip: '10.2.10.9', // 主机ip
  machineZone: '杭州', // 主机所属机器单元
  parentHostName: 'mm', // 针对容器场景，表示容器对应宿主机名
}

export const hostBaseInfo = (info: IHostDetail) => {
  const hostBaseList: IBaseInfo[] = [{
    label: '主机id',
    key: 'id',
  }, {
    label: '主机名',
    key: 'hostName',
  }, {
    label: '主机类型',
    key: 'container',
    render: (t: number) => hostTypeMap[t],
  }, {
    label: '主机所属部门',
    key: 'department',
  }, {
    label: '主机ip',
    key: 'ip',
  }, {
    label: '宿主机名',
    key: 'parentHostName',
    invisible: info?.container === 0,
  }, {
    label: '所属机房',
    key: 'machineZone',
  }];
  return hostBaseList;
}

export const getCollectFileInfo = (info: any) => {
  const CollectFileInfoList = [
    {
      label: '采集文件名',
      key: 'collectFileName',
    },
    {
      label: '切片时间戳配置是否合理',
      key: 'sliceLogTimeStamp',
    },
    {
      label: '日志待采集时间',
      key: 'logStayCollectTime',
    },
    {
      label: '是否为顺序文件',
      key: 'whetherSequentialFile',
    },
    {
      label: '是否采集至文件末尾',
      key: 'whetherCollectFileLast',
    },
    {
      label: '文件最新修改时间',
      key: 'fileNewModifyTime',
    },
    {
      label: '采集进度',
      key: 'collectProgress',
    },
  ]
  return CollectFileInfoList
}
