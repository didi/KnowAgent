import { DownOutlined } from '@ant-design/icons';
import React, { useState, useEffect, useMemo } from 'react';
import { ProTable, IconFont } from '@didi/dcloud-design';
import filterMap from './filtter';
import { Button, Modal, message, Tooltip } from '@didi/dcloud-design';
import ProFormProject from '../tpl-proform/index';
import { renderTableOpts } from '@didi/dcloud-design/lib/common-pages/render-table-opts';
import '@didi/dcloud-design/lib/style/index.less';
// import { renderOperationBtns as renderTableOpts } from "../../compoments/RenderOperationBtns";
import { ExclamationCircleOutlined } from '@ant-design/icons';
import { debounce, cloneDeep } from 'lodash';
import ModalContainer from './modal-container/ModalContainer';
import TplAutoPage from '../../index';
import { request } from '../../../../request/index';

const { confirm } = Modal;
const methods = ['get', 'post', 'formPost', 'filePost', 'put', 'delete'];
type DataType = {
  age: number;
  address: string;
  name: string;
  time: number;
  key: number;
  description: string;
};

const ProTableMoudle = (props: any) => {
  const columns: any = [];
  const queryFormArray = [];
  const initData = {
    bordered: true,
    loading: false,
    columns,
    newTableColumns: [],
    queryFormArray,
    dataSourceUrlConfig: {
      api: '',
      params: '',
      methods: 'request',
      resCallback: '',
    },
    paginationProps: {
      show: true,
      pageSize: 5,
      current: 1,
      total: 100,
    },
    size: 'small',
    expandable: false,
    tableTitle: '高级表格',
    tooltip: '高级表格 tooltip',
    showHeader: true,
    footer: false,
    rowSelection: {},
    scroll: { x: 'max-content' },
    hasData: true,
    tableLayout: undefined,
    toolBarRender: true,
    tableFunction: {
      reload: false,
      export: false,
      import: false,
      search: false,
      custom: false,
      customCompontend: '',
    },
    search: {
      show: true,
      colConfig: 6,
      showOptionBtns: true,
      labelWidth: 80,
      filterType: 'query',
      layout: 'horizontal',
      searchText: '查询',
      resetText: '重置',
      showCollapseButton: true,
      defaultCollapse: true,
      showQueryForm: false,
    },
    options: {
      show: true,
      density: true,
      fullScreen: true,
      setting: true,
    },
  };
  // ------ mock
  const mockData = [
    {
      hostName: 'default:l',
      hostIp: '172.16.101.69',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'lv',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect2',
      hostIp: '172.16.111.39',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'huang',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect3',
      hostIp: '172.56.101.69',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'hong',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect4',
      hostIp: '172.20.101.69',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'hong',
      machineZone: '第二机房',
      hostCreateTime: 1640589209224,
    },
    {
      hostName: 'default:log-collect5',
      hostIp: '172.16.101.19',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'lv',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect6',
      hostIp: '172.16.101.29',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'huang',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect7',
      hostIp: '171.16.101.69',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'lv',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect8',
      hostIp: '172.16.101.65',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'hong',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect9',
      hostIp: '172.16.101.89',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'lv',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect10',
      hostIp: '172.16.111.69',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'huang',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
    {
      hostName: 'default:log-collect11',
      hostIp: '172.16.101.70',
      containerList: '容器',
      serviceList: ['k8s_test,test1,123123'],
      agentVersion: '1.1.0',
      agentHealthLevel: 'lv',
      machineZone: '第二机房',
      hostCreateTime: 1640589209112,
    },
  ];

  // 获取 操作项相关配置
  const getOperationList = (props?: any) => {
    return [
      {
        label: '编辑',
        type: 'icon-bianji',
        clickFunc: (record) => {
          console.log(record, 'edit click');
        },
      },
      {
        label: '删除',
        type: 'icon-shanchu',
        clickFunc: (record) => {
          console.log(record, 'delete click');
        },
      },
      {
        label: '默认',
        type: 'icon-jiahao',
        clickFunc: (record) => {
          console.log(record, 'default click');
        },
      },
      {
        label: '操作记录',
        type: 'icon-caozuojilu',
        clickFunc: (record) => {
          console.log(record, '测试 click');
        },
      },
      {
        label: '设置',
        type: 'icon-shezhi',
        clickFunc: (record) => {
          console.log(record, '测试 click');
        },
      },
    ];
  };

  const mockColumns = [
    {
      title: '主机名',
      dataIndex: 'hostName',
      key: 'hostName',
      width: 500,
    },
    {
      title: '主机IP',
      dataIndex: 'hostIp',
      key: 'hostIp',
    },
    {
      title: '主机类型',
      dataIndex: 'containerList',
      key: 'containerList',
    },
    {
      title: '承载应用',
      dataIndex: 'serviceList',
      key: 'serviceList',
    },
    {
      title: 'Agent版本号',
      dataIndex: 'agentVersion',
      key: 'agentVersion',
    },
    {
      title: 'Agent健康度',
      dataIndex: 'agentHealthLevel',
      key: 'agentHealthLevel',
      render: (t, r) => {
        return (
          <div style={{ height: '20px' }}>
            <IconFont type={`icon-${t}`} style={{ width: 20, height: 20, fontSize: '20px' }} />
          </div>
        );
      },
    },
    {
      title: '所属机房',
      dataIndex: 'machineZone',
      key: 'machineZone',
    },
    {
      title: '新增时间',
      dataIndex: 'hostCreateTime',
      key: 'hostCreateTime',
      // render: (t: number) => moment(t).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      fixed: 'right',
      render: (t, r) => {
        const btn = getOperationList(r);
        return renderTableOpts(btn, r);
      },
    },
  ];

  // ------ mock
  const [selectRowKeys, setSelectRowKeys] = useState<number[]>([]);
  const [selectRows, setSelectRows] = useState([]);
  const [TableFunctionCustom, setTableFunctionCustom] = useState<any>('');
  const [tableFunObj, setTableFunObj] = useState<any>({});
  const [dataSource, setDataSource] = useState(mockData);
  const [dataSourceUrlConfig, setDataSourceUrlConfig] = useState<any>({});
  const [config, setConfig] = useState<any>({ ...initData, ...props?.config });
  const [timer, setTimer] = useState(null);
  const [currentOperationData, setCurrentOperationData] = useState(null);
  const [operationVisable, setOperationVisable] = useState(false);
  const [tableColumns, setTableColumns] = useState([]);
  const [pageTreeData, setPageTreeData] = useState<any>(null);
  const [CustomContent, setCustomContent] = useState(null);
  const [visible, setVisible] = useState(false);

  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
    // locale: {
    //   items_per_page: '条',
    // },
    // selectComponentClass: CustomSelect,
  });
  // operationArray: [
  //   {
  //     operationName: '编辑1',
  //     operationKey: '12',
  //     baseData:
  //       '{"XFormProps":{"layout":"horizontal","formLayout":{"labelCol":{"span":4},"wrapperCol":{"span":20}},"formItemColSpan":24,"formMap":[{"key":"name","label":"姓名","type":"input","attrs":{"placeholder":"请输入姓名"}},{"key":"age","label":"年龄","type":"input"}],"form":{"__INTERNAL__":{}},"formData":{}},"proFormType":"drawer","needSubmitter":true,"submitterPosition":"right"}',
  //   },
  // ],
  const setCurrentClickData = (data) => {
    setCurrentOperationData(data?.baseData);
  };
  const renderTooltip = (isTooltip, text) => {
    // const figure = num ? num : 16;
    return (
      <>
        {isTooltip ? (
          <Tooltip title={text} placement="bottomLeft">
            {text}
          </Tooltip>
        ) : (
          { text }
        )}
      </>
    );
  };

  const showModalConfirm = (record, options) => {
    const { optConfig } = options;
    const strToJson = (str) => {
      const json = eval('(' + str + ')');
      return json;
    };
    confirm({
      title: optConfig.title || '标题',
      // icon: <ExclamationCircleOutlined />,
      content: optConfig.content || '内容',
      okText: optConfig.okText || '确认',
      cancelText: optConfig.cancelText || '取消',
      onOk() {
        try {
          console.log(record, options, 'OK');
          const paramsCallback = strToJson(optConfig['paramFun']);
          const params: any = typeof paramsCallback === 'function' ? paramsCallback(props) : null;
          request(optConfig['api'], { ...params, method: config.dataSourceUrlConfig['methods'] }).then((res) => {
            console.log(record, options, 'res-OK');
          });
        } catch (error) {
          console.log(record, options, 'error');
        }
      },
    });
  };
  // const getOperationList = (row: any) => {

  //   let OperationMap = {}
  //   newColumns?.forEach(element => {
  //     element?.operationArray?.forEach(item => {
  //       OperationMap[element.dataindex] = element?.operationArray
  //     });
  //   });

  //   return [
  //     {
  //       label: "编辑",
  //       baseData: '{"XFormProps":{"layout":"horizontal","formLayout":{"labelCol":{"span":4},"wrapperCol":{"span":20}},"formItemColSpan":24,"formMap":[{"key":"name","label":"姓名","type":"input","attrs":{"placeholder":"请输入姓名"}},{"key":"age","label":"年龄","type":"input"}],"form":{"__INTERNAL__":{}},"formData":{}},"proFormType":"drawer","needSubmitter":true,"submitterPosition":"right"}',
  //       clickFunc: (row, item) => {
  //         setOperationVisable(true)
  //         setCurrentOperationData(JSON.parse(item.baseData))
  //       },
  //     },
  //   ];
  // };
  const renderOptCol = (value: any, row: any) => {
    const btns = getOperationList(row);
    return renderTableOpts(btns, row);
  };

  const genData = (props?: any, param?: object) => {
    window.clearTimeout(timer);
    setTimer(null);
    setConfig({ ...config, loading: true });
    const newTimer = setTimeout(() => {
      const strToJson = (str) => {
        const json = eval('(' + str + ')');
        return json;
      };
      try {
        const paramsCallback = strToJson(config.dataSourceUrlConfig['paramFun']);
        const params: any = typeof paramsCallback === 'function' ? paramsCallback(props) : props;
        request(config.dataSourceUrlConfig['api'], {
          ...params,
          method: config.dataSourceUrlConfig['methods'],
        })
          .then((data: any) => {
            if (config.dataSourceUrlConfig['resCallback']) {
              const resultCallBack = new Function(`return ${config.dataSourceUrlConfig['resCallback']}`)();
              const result = resultCallBack(data);
              setDataSource(result.bizData);
              setConfig({
                ...config,
                loading: false,
                paginationProps: {
                  current: result?.pageNo || 2,
                  pageSize: result?.pageSize || 10,
                  total: result?.total || 0,
                },
              });
            } else {
              setDataSource(data);
              setConfig({
                ...config,
                loading: false,
                paginationProps: {
                  current: data.pagination?.pageNo || 2,
                  pageSize: data.pagination?.pageSize || 10,
                  total: data.pagination?.total || 200,
                },
              });
            }
          })
          .catch((err) => {
            setConfig({ ...config, loading: false });
            console.log('接口有点问题');
          });
      } catch (err) {
        setConfig({ ...config, loading: false });
      }
    }, 1000);
    setTimer(newTimer);

    //  (data)=>{

    //  }
  };

  //导出
  const handleExport = () => {
    if (selectRowKeys.length < 1) {
      message.warning('请勾选要操作的数据');
      return;
    }
    confirm({
      title: `导出`,
      icon: <ExclamationCircleOutlined />,
      content: <p>{`确认立即导出吗`}</p>,
      maskClosable: true,
      onOk: () => {
        console.log('导出');
        // exportEquipmentSheet(selectRowKeys).then((res) => {
        //   const { dat } = res;
        //   location.href = dat.file_link;
        // });
      },
      onCancel() { },
    });
  };

  const handleContentFormModal = (eee) => {
    setPageTreeData(eee);
    setCustomContent(null);
    // setPageTreeData()
    //pagedata
  };

  const handleCustomContentFormModal = (eee) => {
    setPageTreeData(eee);
    const path = './module';
    import(`${path}`).then((res) => {
      setCustomContent({ CustomContent: res[eee?.customPageName] });
    });
  };
  const handleSubmit = (value) => {
    genData({ serachKey: value });
  };
  //刷新
  const reloadData = () => {
    // genData(config.dataSourceUrlConfig)
  };

  const getOpBtns = () => {
    const arr = [];
    if (config?.tableFunction?.export) {
      arr.push({
        label: '导出',
        className: 'dcloud-btn-primary',
        clickFunc: () => {
          handleExport();
        },
      });
    }
    if (config?.tableFunction?.import) {
      arr.push({
        label: '导入',
        className: 'dcloud-btn-primary',
        clickFunc: () => { },
      });
    }

    if (config?.tableFunction?.tableHeaderBtns?.length > 0) {
      config?.tableFunction?.tableHeaderBtns.forEach((el) => {
        arr.push({
          label: el.buttonText,
          className: `dcloud-btn-${el.buttonType}`,
          clickFunc: () => {
            if (el.customContentFormModal === '_customContent') {
              handleCustomContentFormModal(el);
            } else if (el.customContentFormModal === 'confirm') {
              // handleCustomContentFormModal(el);
              showModalConfirm({}, el);
            } else {
              handleContentFormModal(el);
            }
            setVisible(true);
          },
        });
      });
    }

    return arr;
  };
  useMemo(() => {
    // console.log('memo-------------');
    // genData();
  }, [dataSourceUrlConfig]);

  const rowSelection = {
    selectedRowKeys: selectRowKeys,
    onChange: (selectedRowKeys: number[], selectedRows) => {
      setSelectRowKeys(selectedRowKeys);
      setSelectRows(selectedRows);
    },
  };
  useEffect(() => {
    setDataSourceUrlConfig(config.dataSourceUrlConfig);
    const tableObj: any = {};
    if (config.tableFunction.reload) {
      tableObj.reloadData = reloadData;
    } else {
      delete tableObj.reloadData;
    }
    // if(config.tableFunction.reload){
    //   tableObj.getOpBtns=getOpBtns;
    // }
    if (config.tableFunction.search) {
      tableObj.tableHeaderSearchInput = {
        submit: debounce(handleSubmit, 500),
        placeholder: config.tableFunction.placeholder || '请输入查询内容',
        searchTrigger: config.tableFunction?.searchType || 'enter',
      };
    } else {
      delete tableObj.tableHeaderSearchInput;
    }

    /*
      添加控制queryForm展开收起、自定义列按钮、用于本地存储不展示列
    */
    if (config.tableFunction.tableId) {
      tableObj.tableId = config.tableFunction.tableId;
    } else {
      delete config.tableFunction.tableId;
    }

    if (config.tableFunction.tableScreen) {
      tableObj.tableScreen = config.tableFunction.tableScreen;
    } else {
      delete config.tableFunction.tableScreen;
    }

    if (config.tableFunction.tableCustomColumns) {
      tableObj.tableCustomColumns = config.tableFunction.tableCustomColumns;
    } else {
      delete config.tableFunction.tableCustomColumns;
    }

    //自定义

    if (config.tableFunction.customCompontend) {
      const path = `./module/${config.tableFunction.customCompontend}`;
      import(`${path}`)
        .then((deault) => {
          setTableFunctionCustom(deault.default);
          const CustomFun = deault.default;
          tableObj.getJsxElement = () => {
            return <CustomFun></CustomFun>;
          };
          setTableFunObj(tableObj);
        })
        .catch((err) => {
          console.log('组件有点问题');
        });
    } else {
      delete tableObj.getJsxElement;
      setTableFunObj(tableObj);
    }

    const temporaryColumns = JSON.parse(JSON.stringify(config));
    delete temporaryColumns.columns;
    //整理Columns
    const newTableColumns = config.columns?.map((ele: any) => {
      const element: any = JSON.parse(JSON.stringify(ele));

      if (element?.operationArray && element?.operationArray.length !== 0) {
        element.render = (value: any, row: any) => {
          const btns = element?.operationArray.map((items) => {
            return {
              label: items.operationName,
              baseData: items.baseData,
              type: items.type,
              triggerType: items.triggerType,
              optConfig: items.optConfig,
              // needModalConfirm:items.triggerType==='confirm'?true:false,
              clickFunc: (row, item) => {
                if (item?.triggerType === 'confirm') {
                  showModalConfirm(row, item);
                } else {
                  setOperationVisable(true);
                  setCurrentOperationData(JSON.parse(item.baseData));
                }
              },
            };
          });
          return renderTableOpts(btns, row);
        };
      }

      return {
        ...ele,
        ...element,
        ellipsis: config.ellipsis,
      };
    });
    setConfig({ ...temporaryColumns, newTableColumns: newTableColumns });
  }, []);
  const newRenderOptCol = (operationArray) => {
    return renderOptCol;
  };
  // useEffect(() => {
  //   console.log(tableColumns, 'tableColumns')
  // }, [
  //   tableColumns
  // ])
  useEffect(() => {
    if (!operationVisable) {
      setCurrentOperationData(null);
    }
  }, [operationVisable]);

  const onTableChange = (pagination, filters, sorter) => {
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, sorter });
  };

  const querySearch = (values: any) => {
    genData(values);
  };

  const renderCustomEl = (propsForm) => {
    return <TplAutoPage pageTreeData={pageTreeData?.customContentFormModal} form={propsForm} />;
  };

  return (
    <>
      <ProTable
        isCustomPg={config.isCustomPg}
        showQueryForm={config?.search?.showQueryForm}
        // showQueryForm={true}
        queryFormProps={{
          ...config?.search,
          columns: config?.queryFormArray,
          onSearch: querySearch,
        }}
        tableProps={{
          rowKey: config.rowKey ?? new Date().getTime(),
          columns: config.newTableColumns || mockColumns,
          dataSource: dataSource,
          // tableHeaderTitle: true,
          ...config,
          attrs: {
            ...config,
            // title: config?.tableTitle,
            // title: () => {
            //   return config?.tableTitle;
            // },
            // columns: tableColumns,
            rowSelection: config?.tableFunction?.export ? rowSelection : false,
            footer: config?.footer
              ? () => {
                return config?.footer;
              }
              : false,
            onChange: onTableChange,
          },
          ...tableFunObj,
          getOpBtns: getOpBtns,
        }}
      />
      {currentOperationData && (
        <ProFormProject
          config={{ ...currentOperationData, visible: operationVisable, onVisibleChange: setOperationVisable }}
        ></ProFormProject>
      )}
      {visible && pageTreeData?.customContentFormModal === '_customContent' && CustomContent?.CustomContent && (
        <ModalContainer
          {...props}
          visible={visible}
          setVisible={setVisible}
          containerType={pageTreeData.containerType}
          containerConfig={pageTreeData.containerConfig}
          customContent={(customItem) => {
            const NewCustomContent = CustomContent?.CustomContent;
            return <NewCustomContent {...customItem} />;
          }}
        />
      )}
      {visible && pageTreeData?.customContentFormModal !== '_customContent' && (
        <ModalContainer
          visible={visible}
          containerType={pageTreeData.containerType}
          containerConfig={pageTreeData.containerConfig}
          setVisible={setVisible}
          customContent={renderCustomEl}
        />
      )}
    </>
  );
};

export default ProTableMoudle;
