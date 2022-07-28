import { DownOutlined } from '@ant-design/icons';
import React, { useState, useEffect, useMemo } from 'react';
import { ProTable, IconFont } from '@didi/dcloud-design';
import { Button, Modal, message, Tooltip, Select } from '@didi/dcloud-design';
import ProFormProject from '../tpl-proform/index';
import { renderTableOpts } from '@didi/dcloud-design/lib/common-pages/render-table-opts';
import '@didi/dcloud-design/lib/style/index.less';
// import { renderOperationBtns as renderTableOpts } from "../../compoments/RenderOperationBtns";
import { ExclamationCircleOutlined } from '@ant-design/icons';
import debounce from 'lodash/debounce';
import ModalContainer from './modal-container/ModalContainer';
import TplAutoPage from '../tpl-autopage';
import { useLocation, withRouter } from 'react-router-dom';
import { request } from '../../../request/index';
import { defaultPagination } from '../../../constants/common';
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
  const { customHandle, filterMap } = props;
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
  const { state } = useLocation<any>();
  const [selectRowKeys, setSelectRowKeys] = useState<number[]>([]);
  const [selectRows, setSelectRows] = useState([]);
  const [TableFunctionCustom, setTableFunctionCustom] = useState<any>('');
  const [tableFunObj, setTableFunObj] = useState<any>({});
  const [dataSource, setDataSource] = useState([]);
  const [dataSourceUrlConfig, setDataSourceUrlConfig] = useState<any>({});
  const [config, setconfig] = useState<any>({ ...initData, ...props?.config });
  const [newConfig, setNewConfig] = useState(null);
  const [timer, setTimer] = useState(null);
  const [currentOperationData, setCurrentOperationData] = useState(null);
  const [operationVisable, setOperationVisable] = useState(false);
  const [tableColumns, setTableColumns] = useState([]);
  const [pageTreeData, setPageTreeData] = useState<any>(null);
  const [CustomContent, setCustomContent] = useState(null);
  const [visible, setVisible] = useState(false);
  const [allParams, setAllParams] = useState({});
  const [searchResult, setSearchResult] = useState('');
  const testForm: any = React.useRef();

  const [pagination, setPagination] = useState(defaultPagination);
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

  const renderOptCol = (value: any, row: any) => {
    const btns = getOperationList(row);
    return renderTableOpts(btns, row);
  };
  const setConfigData = async (config) => {
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
      setTableFunctionCustom(customHandle[config.tableFunction.customCompontend]);
      const CustomFun = customHandle[config.tableFunction.customCompontend];
      tableObj.getJsxElement = () => {
        return <CustomFun></CustomFun>;
      };
      setTableFunObj(tableObj);
    } else {
      delete tableObj.getJsxElement;
      setTableFunObj(tableObj);
    }

    const temporaryColumns = JSON.parse(JSON.stringify(config));
    delete temporaryColumns.columns;
    //整理Columns
    const newTableColumns = config.columns?.map((ele: any) => {
      const element: any = JSON.parse(JSON.stringify(ele));
      if (element.valueType && filterMap[element.valueType]) {
        // eslint-disable-next-line react/display-name
        element.render = (t: any, record: any) => {
          return element.needTooltip ? (
            <Tooltip placement="bottomLeft" title={filterMap[element.valueType](t, record)}>
              {filterMap[element.valueType](t, record)}
            </Tooltip>
          ) : (
            filterMap[element.valueType](t, record)
          );
        };
      }
      if (element?.operationArray && element?.operationArray.length !== 0) {
        element.render = (value: any, row: any) => {
          const btns = element?.operationArray.map((items) => {
            return {
              label: items.operationKey === 'pause' ? (row.logCollectTaskStatus === 0 ? '继续' : '暂停') : items.operationName || value,
              baseData: items.baseData,
              type: items.type,
              triggerType: items.triggerType,
              itemsData: items,
              renderCustom: items?.renderCustomName && customHandle[items?.renderCustomName] ? customHandle[items?.renderCustomName] : null,
              // needModalConfirm:items.triggerType==='confirm'?true:false,
              clickFunc: (row, item) => {
                if (item?.itemsData?.triggerType === 'confirm') {
                  handleConfirmContentFormModal({ ...item.itemsData, ...row });
                  // showModalConfirm(row, item.itemsData);
                } else if (item?.itemsData?.customContentFormModal === '_customContent') {
                  handleCustomContentFormModal({ ...item.itemsData, ...row });
                  setVisible(true);
                } else {
                  handleContentFormModal({ ...item.itemsData, ...row });
                  setVisible(true);
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

    const queryFormArrayData = JSON.parse(JSON.stringify(config.queryFormArray));
    for (let i = 0; i <= queryFormArrayData.length - 1; i++) {
      const item = queryFormArrayData[i];
      if (item?.type === 'select') {
        if (customHandle[item?.option]) {
          item['options'] = await customHandle[item?.option]();
        }
      }
    }
    return { ...temporaryColumns, queryFormArray: queryFormArrayData, newTableColumns: newTableColumns };
    // setNewConfig({ ...temporaryColumns, queryFormArray: queryFormArrayData, newTableColumns: newTableColumns });
  };

  const genData = async (props?: any, param?: any) => {
    const configureData: any = await setConfigData(config);
    // window.clearTimeout(timer);
    setTimer(null);
    setNewConfig({ ...configureData, loading: true });
    const strToJson = (str) => {
      const json = eval('(' + str + ')');
      return json;
    };
    try {
      const paramsCallback = new Function(`return ${configureData?.dataSourceUrlConfig['params']}`)();
      const defaultParams = strToJson(configureData?.dataSourceUrlConfig?.defaultParams);
      const params: any =
        typeof paramsCallback === 'function' ? paramsCallback({ ...defaultParams, ...props }) : { ...defaultParams, ...props };

      const requestTypeJudge = () => {
        if (configureData?.dataSourceUrlConfig['methods'] == 'post' || configureData?.dataSourceUrlConfig['methods'] == 'put') {
          return {
            data: JSON.stringify(params),
            method: configureData?.dataSourceUrlConfig['methods'],
          };
        } else {
          return {
            params,
            method: configureData?.dataSourceUrlConfig['methods'],
          };
        }
      };
      request(configureData?.dataSourceUrlConfig['api'], requestTypeJudge())
        .then((data: any) => {
          if (configureData?.dataSourceUrlConfig['resCallback']) {
            const resultCallBack = new Function(`return ${configureData?.dataSourceUrlConfig['resCallback']}`)();
            const result = resultCallBack(data);
            setDataSource(result.bizData);
            setNewConfig({
              ...configureData,
              loading: false,
              paginationProps: {
                ...pagination,
                current: result?.pageNo || 2,
                pageSize: result?.pageSize || 10,
                total: result?.total || 0,
              },
            });
          } else {
            // setDataSource(data);
            setNewConfig({
              ...configureData,
              loading: false,
              paginationProps: {
                ...pagination,
                current: data.pagination?.pageNo || 2,
                pageSize: data.pagination?.pageSize || 10,
                total: data.pagination?.total || 200,
              },
            });
          }
        })
        .catch((err) => {
          setNewConfig({ ...configureData, loading: false });
          console.log('接口有点问题');
        });
    } catch (err) {
      setNewConfig({ ...configureData, loading: false });
    }
    // const newTimer = setTimeout(() => {  // 去除再次点击弹窗时触发上一次请求
    // }, 1000);
    // setTimer(newTimer);

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

  const handleContentFormModal = (btnConfig) => {
    let newBtnConfig = {};
    if (btnConfig?.containerConfig?.customTitle) {
      newBtnConfig = {
        ...btnConfig,
        containerConfig: { ...btnConfig.containerConfig, title: customHandle[btnConfig.containerConfig.title]?.(btnConfig) },
      };
    } else {
      newBtnConfig = btnConfig;
    }
    setPageTreeData({ ...newBtnConfig, ...props, genData, selectRowKeys });
    if (selectRowKeys.length < 1 && btnConfig.isBatch) {
      message.warning('请勾选要操作的数据');
      return;
    }
    setCustomContent(null);
    // setPageTreeData()
    //pagedata
  };

  const handleCustomContentFormModal = (btnConfig) => {
    let newBtnConfig = {};
    if (btnConfig?.containerConfig?.customTitle) {
      newBtnConfig = {
        ...btnConfig,
        containerConfig: { ...btnConfig.containerConfig, title: customHandle[btnConfig.containerConfig.title]?.(btnConfig) },
      };
    } else {
      newBtnConfig = btnConfig;
    }

    setPageTreeData({ ...newBtnConfig, ...props, genData, selectRowKeys });
    if (selectRowKeys.length < 1 && btnConfig.isBatch) {
      message.warning('请勾选要操作的数据');
      return;
    }
    setCustomContent({ CustomContent: customHandle[btnConfig?.customPageName] });
  };

  const handleConfirmContentFormModal = (btnConfig) => {
    if (selectRowKeys.length < 1 && btnConfig.isBatch) {
      message.warning('请勾选要操作的数据');
      return;
    }
    if (btnConfig.customRenderConfirm === 1 || btnConfig.containerType === 'custom') {
      customHandle[btnConfig?.customPageName]({ ...btnConfig, ...props, genData, selectRowKeys });
    } else {
      // 非自定义时候的modal.confirm
      showModalConfirm({}, { ...btnConfig, ...props, genData });
      // confirm({
      //   title: btnConfig.title || '标题',
      //   // icon: <ExclamationCircleOutlined />,
      //   content: btnConfig.content || '内容',
      //   okText: btnConfig.okText || '确认',
      //   cancelText: btnConfig.cancelText || '取消',
      //   onOk() {
      //     // try {
      //     //   const paramsCallback = strToJson(optConfig['paramFun']);
      //     //   const params: any = typeof paramsCallback === 'function' ? paramsCallback(props) : null;
      //     //   request(optConfig['api'], { ...params, method: config.dataSourceUrlConfig['methods'] }).then((res) => {
      //     //     console.log(record, options, 'res-OK');
      //     //   });
      //     // } catch (error) {
      //     //   console.log(record, options, 'error');
      //     // }
      //   },
      // });
    }
  };
  // 获取queryForm 的form
  const getFormInstance = (form) => {
    testForm.current = form;
  };

  const handleSubmit = (value) => {
    setSearchResult(value);
    genData({ queryTerm: value, ...testForm?.current.getFieldsValue() });
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
              // 补充判断是否是批量操作的开关
              handleCustomContentFormModal(el);
              setVisible(true);
            } else if (el.containerType && el.containerType === 'custom') {
              handleConfirmContentFormModal(el);
              // showModalConfirm({}, el);
            } else {
              handleContentFormModal(el);
              setVisible(true);
            }
          },
        });
      });
    }

    return arr;
  };
  // useMemo(() => {
  //   // console.log('memo-------------');
  //   genData();
  // }, []);

  const rowSelection = {
    selectedRowKeys: selectRowKeys,
    onChange: (selectedRowKeys: number[], selectedRows) => {
      setSelectRowKeys(selectedRowKeys);
      setSelectRows(selectedRows);
      setNewConfig({ ...newConfig, selectedRowKeys });
    },
  };
  const setQueryFormArray = async (queryFormArray) => {
    const queryFormArrayData = JSON.parse(JSON.stringify(queryFormArray));
    for (let i = 0; i <= queryFormArrayData.length - 1; i++) {
      const item = queryFormArrayData[i];
      if (item?.type === 'select') {
        const handleList = customHandle;
        item.option = handleList[item?.option]();
      }
    }
    return queryFormArrayData;
  };

  useEffect(() => {
    state ? genData({ ...state }) : genData();
  }, [config]);
  const newRenderOptCol = (operationArray) => {
    return renderOptCol;
  };

  useEffect(() => {
    if (!operationVisable) {
      setCurrentOperationData(null);
    }
  }, [operationVisable]);

  // useEffect(() => {
  //   const params=props?.config?.params
  //   setAllParams(params)
  // }, [props?.config?.params]);

  const toLine = (name) => {
    // 处理sorter.field字段由小驼峰转成下划线 Agent项目添加
    return name.replace(/([A-Z])/g, '_$1').toLowerCase();
  };

  const onTableChange = (pagination, filters, sorter) => {
    const asc = sorter.order && sorter.order === 'ascend' ? true : false;
    const sortColumn = sorter.field && toLine(sorter.field);
    genData({ pageNo: pagination.current, pageSize: pagination.pageSize, filters, asc, sortColumn, queryTerm: searchResult, ...allParams });
  };

  const querySearch = (values: any) => {
    genData({ ...values, queryTerm: searchResult, ...newConfig.paginationProps });
    setAllParams(values);
  };

  const renderCustomEl = (propsForm) => {
    return <TplAutoPage pageTreeData={pageTreeData?.customContentFormModal} form={propsForm} />;
  };

  return (
    <>
      {newConfig && (
        <ProTable
          isCustomPg={newConfig.isCustomPg}
          showQueryForm={newConfig?.search?.showQueryForm}
          // showQueryForm={true}
          queryFormProps={{
            ...newConfig?.search,
            columns: newConfig?.queryFormArray,
            onSearch: querySearch,
            getFormInstance: getFormInstance,
            initialValues: state,
          }}
          tableProps={{
            rowKey: newConfig.rowKey ?? new Date().getTime(),
            columns: newConfig.newTableColumns,
            dataSource: dataSource,
            // tableHeaderTitle: true,
            ...newConfig,
            attrs: {
              ...newConfig,
              className: 'frameless-table',
              // title: config?.tableTitle,
              // title: () => {
              //   return config?.tableTitle;
              // },
              // columns: tableColumns,
              rowSelection: newConfig?.rowSelection ? rowSelection : false,
              footer: newConfig?.footer
                ? () => {
                  return newConfig?.footer;
                }
                : false,
              onChange: onTableChange,
            },
            ...tableFunObj,
            getOpBtns: getOpBtns,
          }}
        />
      )}
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
          containerData={{ ...pageTreeData, listData: dataSource }}
          genData={genData}
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

export default withRouter(ProTableMoudle);
