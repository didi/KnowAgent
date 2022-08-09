import React, { useState, useEffect } from 'react';
import { Table, Input, IconFont, Select } from '@didi/dcloud-design';

export type linkageTableProps = {
  lineData?: any[];
  requestParams?: any;
  rangeTimeArr?: any;
  clearFlag?: number;
  chartData?: any;
  dispatchSort?: (params: any) => void;
};

export const sortFieldEnum = {
  0: 'last',
  1: 'min',
  2: 'max',
  3: 'mean',
  4: 'std',
  5: 'fiftyFiveQuantile',
  6: 'seventyFiveQuantile',
  7: 'ninetyFiveQuantile',
  8: 'ninetyNineQuantile',
};

const columnsVal = [
  {
    title: '主机名',
    width: 200,
    dataIndex: 'hostName',
    key: 'hostName',
  },
  {
    title: '磁盘路径',
    width: 200,
    dataIndex: 'path',
    key: 'path',
  },
  {
    title: '文件系统类型',
    dataIndex: 'fsType',
    width: 200,
    key: 'fsType',
  },
  {
    title: '设备名',
    width: 150,
    dataIndex: 'device',
    key: 'device',
  },
  {
    title: '网卡mac地址',
    width: 200,
    dataIndex: 'macAddress',
    key: 'macAddress',
  },
  {
    title: '当前值',
    width: 150,
    dataIndex: 'last',
    key: 'last',
    sorter: true,
    render: (val) => {
      let value = val;
      if (value.endsWith('MB')) {
        const num = parseFloat(value);
        if (num >= 1024) {
          value = (num / 1024).toFixed(2) + 'GB';
        }
      }
      return value;
    },
  },
  {
    title: '最大值',
    width: 150,
    dataIndex: 'max',
    key: 'max',
    sorter: true,
  },
  {
    title: '最小值',
    width: 150,
    dataIndex: 'min',
    key: 'min',
    sorter: true,
  },
  {
    title: '平均值',
    width: 150,
    dataIndex: 'mean',
    key: 'mean',
    sorter: true,
  },
  {
    title: '55%',
    width: 150,
    dataIndex: 'fiftyFiveQuantile',
    key: 'fiftyFiveQuantile',
    sorter: true,
  },
  {
    title: '75%',
    width: 150,
    dataIndex: 'seventyFiveQuantile',
    key: 'seventyFiveQuantile',
    sorter: true,
  },
  {
    title: '95%',
    width: 150,
    dataIndex: 'ninetyFiveQuantile',
    key: 'ninetyFiveQuantile',
    sorter: true,
  },
  {
    title: '99%',
    width: 150,
    dataIndex: 'ninetyNineQuantile',
    key: 'ninetyNineQuantile',
    sorter: true,
  },
];

const LinkageTable = (props: linkageTableProps): JSX.Element => {
  const { lineData = [], dispatchSort, requestParams, clearFlag, chartData = [] } = props;

  const [keyWord, setKeyword] = useState<string>('');
  const [dataSource, setDataSource] = React.useState(lineData);
  const [current, setCurrent] = React.useState(1);
  const [columns, setColumns] = React.useState<any>(columnsVal);
  const [sortedInfo, setSortedInfo] = useState<any>({
    columnKey: sortFieldEnum[requestParams?.sortMetricType],
    order: 'ascend',
  });

  const tablePagination = {
    defaultCurrent: 1,
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '30', '40', '50'],
    showTotal: (total) => `共 ${total} 个条目`,
  };

  useEffect(() => {
    const data = keyWord ? lineData.filter((d) => d?.path?.includes(keyWord as string)) : lineData;
    setDataSource(data);
    filterColumns(data);
  }, [lineData]);

  const filterColumns = (data) => {
    const colunmArr = [];
    const dataObj = Array.isArray(data[0]) ? data[0][0] : data[0];
    columnsVal.forEach((item) => {
      const key = item.key;
      if (dataObj?.[key] || dataObj?.[key] === 0) {
        colunmArr.push(item);
      }
    });
    setColumns(colunmArr);
  };

  useEffect(() => {
    const hasMinValue = chartData.every((item) => item.min !== null);
    if (!hasMinValue) {
      setColumns((data) => {
        return data.map((item) => {
          if (item.sorter && item.dataIndex !== 'last') {
            item.sorter = false;
          }
          return {
            ...item,
          };
        });
      });
    }
  }, [chartData]);

  useEffect(() => {
    setColumns((data) => {
      return data.map((item) => {
        if (item.sorter) {
          item.sortOrder = sortedInfo.columnKey === item.key && sortedInfo.order;
        }
        return {
          ...item,
        };
      });
    });
  }, [sortedInfo]);

  const onSearch = (e) => {
    const searchKey = e.target.value;
    const key = columns?.[0].key;
    const data = searchKey ? lineData.filter((d) => d?.[key]?.includes(searchKey as string)) : lineData;
    setDataSource(data);
  };

  React.useEffect(() => {
    setKeyword('');
    setDataSource([]);
    setCurrent(1);
  }, [clearFlag]);

  const onChange = (pagination, filters, sorter, extra) => {
    pagination && setCurrent(pagination.current);
    if (sorter.order && sorter.columnKey !== sortedInfo.columnKey) {
      setCurrent(1);
      setSortedInfo(sorter);
      dispatchSort(sorter);
    }
    if (!sorter.order) {
      setCurrent(1);
      dispatchSort(sortedInfo);
    }
    setKeyword('');
    // setDataSource([]);
  };

  function onShowSizeChange(current, pageSize) {
    console.log(current, pageSize);
  }

  const SelectComponent = (props) => {
    return (
      <>
        <span>每页显示</span>
        <Select bordered={false} suffixIcon={<IconFont type="icon-xiala" />} {...props} />
      </>
    );
  };

  SelectComponent.Option = Select.Option;

  const pagination = {
    locale: {
      items_per_page: '条',
    },
    className: 'pro-table-pagination-custom',
    selectComponentClass: SelectComponent,
  };

  return (
    <>
      <Input
        placeholder={`请输入${columns?.[0]?.title || ''}`}
        prefix={<IconFont type="icon-sousuo" style={{ fontSize: 13 }} />}
        style={{ width: 290, marginBottom: 10 }}
        value={keyWord}
        onChange={(e) => setKeyword(e.target.value)}
        onPressEnter={onSearch}
      />
      <Table
        scroll={{ x: '100%' }}
        sortDirections={['ascend']}
        showSorterTooltip={false}
        rowKey={'name'}
        columns={columns}
        pagination={{ ...tablePagination, onShowSizeChange, current, ...pagination }}
        dataSource={dataSource}
        onChange={onChange}
      />
    </>
  );
};

export default LinkageTable;
