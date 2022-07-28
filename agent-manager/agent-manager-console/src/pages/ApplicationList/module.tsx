/* eslint-disable react/no-children-prop */
import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Row,
  Button,
  Select,
  AutoComplete,
  Tooltip,
  Upload,
  IconFont,
  Transfer,
  Checkbox,
  AppContainer,
  ProTable,
  ProDescriptions,
  DTable,
  Tag,
  Drawer,
  Modal,
  notification,
} from '@didi/dcloud-design';
import { SearchOutlined } from '@ant-design/icons';
import moment from 'moment';
import { request } from '../../request/index';

export interface HostInfo {
  hostName: string;
  hostType: number; //主机类型 0：主机 1：容器 ,
  ip: string; // ip
}
export interface IK8sDataSource {
  duplicateHostNameHostList: HostInfo; // 主机名重复主机信息集 ,
  duplicateIpHostList: HostInfo; // ip重复主机信息集 ,
  relateHostNum: number; // 关联主机数 ,
  serviceName: string; // 服务名 ,
  syncSuccess: number; // 同步状态 0：失败 1：成功
  [key: string]: any;
}

const hostTypes = [
  {
    value: 0,
    label: '物理机',
  },
  {
    value: 1,
    label: '容器',
  },
];
const { useGlobalValue } = AppContainer;
const { Option } = Select;
const { Search } = Input;
const regName = /^([-_.a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制
const regIp = /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/; // 支持9位数字，一共12位
const regString128 = /^[\s\S]{0,128}$/; // 任意字符最大输入长度128位
const regAdress = /^([-_.:,a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制
const regProducerName = /^[\s\S]{1,1024}$/; // 任意字符最大输入长度为1024位
export const timeFormat = 'YYYY-MM-DD HH:mm:ss';
export const getHosts = () => {
  return request('/api/v1/normal/host/list');
};

export const getServiceDetail = (id: any = 3) => {
  return request(`/api/v1/normal/services/${id}`);
};

export const getk8sSyncReault = () => {
  return request('/api/v1/op/metadata/sync-result');
};

export const deleteService = (agentVersionId: number) => {
  return request(`/api/v1/op/services/${agentVersionId}`, {
    method: 'DELETE',
  });
};

export const getServicesLogCollectTaskId = (LogCollectTaskId: any) => {
  return request(`/api/v1/normal/services/rela-logcollecttask-exists/${LogCollectTaskId}`);
};

export const modifyService = (params: any) => {
  return request('/api/v1/op/services', {
    method: 'PUT',
    data: JSON.stringify(params),
  });
};

export const addService = (params: any) => {
  return request('/api/v1/op/services', {
    method: 'POST',
    data: JSON.stringify(params),
  });
};
// 应用管理 => 新增应用
export const ActionAppForm = (props: any) => {
  console.log(props, 'ActionAppForm');

  const [isFirst, setisFirst] = useState(true);
  const [hostList, setHostList] = useState([]);
  const [appForm, setappForm] = useState({
    serviceName: '',
    targetKeys: [],
  });

  const getHostlist = () => {
    getHosts().then((res: any) => {
      const data = res.map((item: any) => {
        return { key: item.id, ...item };
      });
      setHostList(data);
    });
  };

  const filterOption = (inputValue: any, option: any) => {
    return option.hostName.indexOf(inputValue) > -1;
  };

  const handleChange = (targetKeys: any) => {
    setappForm({
      serviceName: appForm.serviceName,
      targetKeys: targetKeys,
    });
  };

  if (isFirst) {
    getHostlist();
    if (props.containerData && props.containerData.id) {
      getServiceDetail(props.containerData.id).then((res: any) => {
        const hostList = res.hostList || [];
        props.form.setFieldsValue({ hostIdList: hostList.map((item: any) => item.id) });
        setappForm({
          serviceName: res.serviceName,
          targetKeys: hostList.map((item: any) => item.id),
        });
      });
    }
    setisFirst(false);
  }

  useEffect(() => {
    if (props.submitEvent !== 1) {
      props.form.validateFields().then((values) => {
        if (props.containerData && props.containerData.id) {
          const params = {
            id: props.containerData.id,
            hostIdList: values.hostIdList,
            servicename: values.servicename,
          };
          return modifyService(params).then((res: any) => {
            notification.success({
              message: '修改成功！',
              duration: 3,
            });
            props.genData();
            props.setVisible(false);
          });
          // .catch((err: any) => {
          //   message.error(err.message);
          // });
        } else {
          const params = {
            hostIdList: values.hostIdList,
            servicename: values.servicename,
          };
          return addService(params).then((res: any) => {
            notification.success({
              message: '新增成功！',
              duration: 3,
            });
            props.genData();
            props.setVisible(false);
          });
          // .catch((err: any) => {
          //   message.error(err.message);
          // });
        }
      });
    }
  }, [props.submitEvent]);

  return (
    <div style={{ padding: '0 24px' }}>
      <Form
        form={props.form}
        name="actionApp"
        // labelCol={{ span: 4 }}
        // wrapperCol={{ span: 20 }}
        layout="vertical"
      >
        <Form.Item
          label="应用名："
          name="servicename"
          rules={[
            {
              required: appForm.serviceName ? false : true,
              message: '请输入应用名，支持大小写中英文字母、数字、下划线、短横线,32位限制',
              // validator: (rule: any, value: string) => {
              //   return !!value && new RegExp(regName).test(value);
              // },
              validator: (_, v, c) => {
                if (appForm.serviceName || (!!v && new RegExp(regName).test(v))) {
                  c();
                  return;
                }
                c('请输入应用名，支持大小写中英文字母、数字、下划线、短横线,32位限制');
              },
            },
          ]}
          style={{ flexDirection: props.containerData.id ? 'row' : 'column', alignItems: props.containerData.id ? 'baseline' : '' }}
        >
          {appForm.serviceName ? (
            appForm.serviceName
          ) : (
            <Input
              // style={{ width: '590px' }}
              placeholder="请输入"
            />
          )}
        </Form.Item>
        <Form.Item
          label="关联主机："
          name="hostIdList"
          rules={[
            {
              required: true,
              message: '请选择',
              validator: (_, v, c) => {
                console.log(v, 'vv');
                c();
                // if (appForm.serviceName || (!!v && new RegExp(regName).test(v))) {
                //   c();
                //   return;
                // }
                // c('请输入应用名，支持大小写中英文字母、数字、下划线、短横线,32位限制');
              },
            },
          ]}
          initialValue={appForm.targetKeys.length > 0 ? appForm.targetKeys : []}
        >
          <Transfer
            dataSource={hostList}
            showSearch
            filterOption={filterOption}
            targetKeys={appForm.targetKeys}
            onChange={handleChange}
            render={(item) => item.hostName}
            className={'hostIdList-transfer'}
          />
        </Form.Item>
      </Form>
    </div>
  );
};

// 删除应用
export const DeleteActionApp: React.FC = (props: any) => {
  console.log(props, 'DeleteAgentVersion');
  return (
    <>
      <div>是否确认删除[缺少应用名称]</div>
      <div>删除操作不可恢复，请谨慎操作！</div>
    </>
  );
};

export const DeleteActionCluster: React.FC = (props: any) => {
  console.log(props, 'DeleteAgentVersion');
  const { containerData, genData } = props;
  const [deleteTip, setDeleteTip] = useState<any>(null);

  useEffect(() => {
    const Id = !containerData?.isBatch ? containerData?.id : containerData?.selectRowKeys?.join();
    // 缺少校验批量删除时的逻辑
    getServicesLogCollectTaskId(Id)
      .then((res: any) => {
        setDeleteTip(true);
      })
      .catch((err) => setDeleteTip(false));

    // getAgentId(props.containerData?.id)
    //   .then((res: any) => {
    //     getLogCollectTaskId(props.containerData?.id)
    //       .then((res: any) => {
    //         setDeleteTip(true);
    //       })
    //       .catch((err) => setDeleteTip(false));
    //   })
    //   .catch((err) => setDeleteTip(false));
  }, []);

  useEffect(() => {
    if (props.submitEvent !== 1) {
      console.log(props.submitEvent, 'props.submitEvent');
      if (containerData?.id && !containerData?.isBatch) {
        // 删除版本单行操作

        deleteService(props.containerData.id || 1).then((res: any) => {
          props.setVisible(false);
          notification.success({
            message: '删除成功',
            duration: 3,
          });
          props.genData();
        });
        // deleteReceive(props.containerData.id).then((res: any) => {
        //   props.setVisible(false);
        //   props.genData();
        //   notification.success({
        //     message: '删除成功',
        //     duration: 3,
        //   });
        // });
      } else if (containerData?.selectRowKeys.length > 0 && containerData?.isBatch) {
        // 删除版本批量操作 需要替换接口
        deleteService(containerData?.selectRowKeys?.join()).then((res: any) => {
          props.setVisible(false);
          props.genData();
          notification.success({
            message: '删除成功',
            duration: 3,
          });
        });
      }
    }
  }, [props.submitEvent]);
  return (
    <div style={{ padding: '0 24px' }}>
      {deleteTip ? (
        <p style={{ fontSize: '13px' }}>删除应用将会导致该应用相关的采集任务失败，是否确认删除？</p>
      ) : (
        <p>是否确认删除{props.containerData?.serviceName}？</p>
      )}
      <p>删除操作不可恢复，请谨慎操作！</p>
    </div>
  );
};

// export const deleteActionApp = (props: any) => {
//   Modal.confirm({
//     title: props.agentId ? '该主机已安装Agent，请先卸载' : `是否确认删除${props.hostName}？`,
//     content: <span className="fail">{!props.agentId && '删除操作不可恢复，请谨慎操作！'}</span>,
//     okText: '确认',
//     cancelText: '取消',
//     onOk() {
//       !props.agentId &&
//         deleteHost(props.hostId, 0).then((res: any) => {
//           // 0：不忽略数据未采集完 1：忽略数据未采集完
//           // 删除主机 0：删除成功
//           // 10000：参数错误 ==> 不可删除
//           // 23000：待删除主机在系统不存在 ==> 不可删除
//           // 23004：主机存在关联的容器导致主机删除失败 ==> 不可删除
//           // 22001：Agent存在未采集完的日志 ==> 不可能存在这种情况
//           if (res.code === 0) {
//             Modal.success({ content: '删除成功！' });
//             props.genData();
//           } else if (res.code === 10000 || res.code === 23000 || res.code === 23004) {
//             Modal.error({ content: res.message });
//           }
//         });
//     },
//   });
// };

interface TitleConfigType {
  titleStyle?: any;
  titleText: string;
  [name: string]: any;
}

const renderTitle = (titleConfig: TitleConfigType) => {
  const { titleText, titleStyle = { backfround: '#F9F9FA' } } = titleConfig;
  return <div style={titleStyle}>{titleText}</div>;
};

export const renderTooltip = (text: string, num?: number) => {
  const figure = num ? num : 16;
  return (
    <>
      {text ? (
        <Tooltip title={text} placement="bottomLeft">
          {text?.length > figure ? text?.substring(0, figure) + '...' : text}
        </Tooltip>
      ) : (
        '-'
      )}
    </>
  );
};

export const detailColumns = [
  {
    title: '主机名',
    dataIndex: 'hostName',
    key: 'hostName',
  },
  {
    title: '主机IP',
    dataIndex: 'ip',
    key: 'ip',
  },
  {
    title: '主机类型',
    dataIndex: 'container',
    key: 'container',
    sorter: (a, b) => a.container - b.container,
    render: (text: any) => {
      return text == 0 ? '主机' : text == 1 ? '容器' : renderTooltip(text);
    },
  },
];

// 应用详情
export const ActionAppDetail: React.FC = (props: any) => {
  console.log(props, 'ActionAppDetail');
  const [detailList, setDetailList] = useState([]);
  const [filterDetailList, setFilterDetailList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });

  const getAppDetail = (params: any) => {
    getServiceDetail(params)
      .then((res: any) => {
        setDetailList(
          res.hostList?.map((item) => {
            return { key: item.id, ...item };
          })
        );
        setFilterDetailList(
          res.hostList?.map((item) => {
            return { key: item.id, ...item };
          })
        );
        setLoading(false);
        // setState({
        //   detailList: res.hostList?.map(item => {
        //     return { key: item.id, ...item }
        //   }),
        //   loading: false,
        //   total: res.total,
        // });
      })
      .catch((err: any) => {
        setLoading(false);
      });
  };

  const detailConfig = [
    {
      label: '应用名',
      key: 'serviceName',
      // customType: 'edit',
    },
    {
      label: '新增时间',
      key: 'createTime',
      renderCustom: (contnet: any) => {
        // 可自定义渲染及展示的逻辑
        return moment(contnet).format(timeFormat);
      },
    },
  ];

  const onTableChange = (pagination, filters, sorter) => {
    console.log(pagination, 'pagination');
    setPagination((value) => {
      console.log(value, 'valuess');
      return {
        ...value,
        ...pagination,
      };
    });
  };

  const onSearch = (e) => {
    if (e.target.value) {
      const newDetailList = detailList.filter((item) => item.hostName.includes(e.target.value) || item.ip.includes(e.target.value));
      setFilterDetailList(newDetailList);
    } else {
      setFilterDetailList(detailList);
    }
  };

  useEffect(() => {
    getAppDetail(props.containerData.id);
  }, [props]);

  return (
    <>
      <ProDescriptions
        dataSource={props.containerData}
        config={detailConfig}
        column={{ xl: 2, xxl: 2, sm: 2 }}
        labelStyle={{ minWidth: '72px', textAlign: 'right', display: 'block' }}
        customTitle={() => {
          return (
            <div
              style={{
                height: '40px',
                backgroundColor: '#F9F9FA',
                lineHeight: '40px',
                paddingLeft: '20px',
                fontSize: '13px',
              }}
            >
              详情
            </div>
          );
        }}
        bordered={false}
      />
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          height: '40px',
          backgroundColor: '#F9F9FA',
          lineHeight: '40px',
          padding: '0 20px',
          fontSize: '13px',
          color: '#353A40',
          marginTop: '24px',
          fontWeight: 500,
        }}
      >
        <span>关联主机</span>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            fontSize: '13px',
          }}
        >
          <Input
            placeholder={'请输入主机名/IP'}
            style={{ width: '233px', height: '27px', padding: '0 11px' }}
            // onChange={(e) => searchTrigger === 'change' && submit(e.target.value)}
            onChange={onSearch}
            // onBlur={(e: any) => searchTrigger === 'blur' && submit(e.target.value)}
            suffix={<SearchOutlined style={{ color: '#ccc' }} />}
          />
        </div>
      </div>
      <div style={{ padding: '20px' }}>
        <ProTable
          isCustomPg={true}
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'hostName',
            loading: loading,
            columns: detailColumns,
            dataSource: filterDetailList,
            paginationProps: { ...pagination },
            attrs: {
              // className: 'frameless-table', // 纯无边框表格类名
              // bordered: true,   // 表格边框
              onChange: onTableChange,
            },
          }}
        />
      </div>
    </>
  );
};

// K8s同步
export const K8sDetail: React.FC = (props: any) => {
  console.log(props, 'K8sDetail');
  const [detailList, setDetailList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });
  const [failedDetail, setFailedDetail] = useState({});
  const [failedDetailVisible, setfailedDetailVisible] = useState(false);

  const k8sColums = [
    {
      title: '应用名',
      dataIndex: 'serviceName',
    },
    {
      title: '关联主机数',
      dataIndex: 'relateHostNum',
    },
    {
      title: '同步处状态',
      dataIndex: 'syncSuccess',
      sort: (a: IK8sDataSource, b: IK8sDataSource) => a.syncSuccess - b.syncSuccess,
      render: (text: number, record: IK8sDataSource) => {
        return text ? '成功' : '失败';
      },
    },
    {
      title: '操作',
      dataIndex: 'options',
      render: (text, record: IK8sDataSource) => {
        return record.syncSuccess ? '-' : <a onClick={() => openFailedDetail(record)}> 查看详情</a>;
      },
    },
  ];

  const getK8sDetail = () => {
    setLoading(true);
    getk8sSyncReault()
      .then((res: any) => {
        // console.log(res);
        const data = res?.metadataSyncResultPerServiceList?.map((item: IK8sDataSource, index: number) => {
          item.id = index;
          return item;
        });

        setDetailList(data);
        setLoading(false);
        // setState({
        //   detailList: data,
        //   loading: false,
        // });
      })
      .catch((err: any) => {
        // const mockData = [
        //   {
        //     duplicateHostNameHostList: [
        //       {
        //         hostName: 'string',
        //         hostType: 0,
        //         ip: 'string',
        //       },
        //     ],
        //     duplicateIpHostList: [
        //       {
        //         hostName: 'string',
        //         hostType: 0,
        //         ip: 'string',
        //       },
        //     ],
        //     nameDuplicate: 1,
        //     relateHostNum: 0,
        //     serviceName: 'string',
        //     syncSuccess: 0,
        //   },
        // ];
        // setDetailList(mockData);
        setLoading(false);
      });
  };

  const openFailedDetail = (data: IK8sDataSource) => {
    console.log(data, 'mockData');
    setFailedDetail(data);
    setfailedDetailVisible(true);
  };

  const onTableChange = (pagination, filters, sorter) => {
    console.log(pagination, 'pagination');
    setPagination((value) => {
      console.log(value, 'valuess');
      return {
        ...value,
        ...pagination,
      };
    });
  };

  const onCancel = () => {
    setFailedDetail({});
    setfailedDetailVisible(false);
  };

  useEffect(() => {
    getK8sDetail();
  }, []);
  return (
    <div style={{ padding: '24px 20px 0' }}>
      <ProTable
        isCustomPg={true}
        showQueryForm={false}
        tableProps={{
          showHeader: false,
          rowKey: 'serviceName',
          loading: loading,
          columns: k8sColums as any,
          dataSource: detailList,
          paginationProps: { ...pagination },
          attrs: {
            className: 'frameless-table', // 纯无边框表格类名
            // bordered: true,   // 表格边框
            onChange: onTableChange,
          },
        }}
      />
      <FailedDetail isDetailVisible={failedDetailVisible} failedDetail={failedDetail} onClose={onCancel} />
    </div>
  );
};

const typeMap = [
  {
    type: 'duplicateHostNameHostList',
    text: '主机名重复',
    columns: [
      {
        title: '主机名',
        dataIndex: 'hostName',
        key: 'hostName',
      },
      {
        title: '主机IP',
        dataIndex: 'ip',
        key: 'ip',
      },
      {
        title: '主机类型',
        dataIndex: 'hostType',
        key: 'hostType',
        sorter: (a: HostInfo, b: HostInfo) => a.hostType - b.hostType,
        render: (text: any) => {
          return text == 0 ? '主机' : '容器';
        },
      },
    ],
  },
  {
    type: 'duplicateIpHostList',
    text: '主机ip重复',
    columns: [
      {
        title: '主机名',
        dataIndex: 'hostName',
        key: 'hostName',
      },
      {
        title: '主机IP',
        dataIndex: 'ip',
        key: 'ip',
      },
      {
        title: '主机类型',
        dataIndex: 'hostType',
        key: 'hostType',
        sorter: (a: HostInfo, b: HostInfo) => a.hostType - b.hostType,
        render: (text: any) => {
          return text == 0 ? '主机' : '容器';
        },
      },
    ],
  },
  {
    type: 'nameDuplicate',
    text: '应用名重复',
    columns: [
      {
        title: '应用名',
        dataIndex: 'serviceName',
        key: 'serviceName',
      },
    ],
  },
];

const FailedDetail = (props: any) => {
  console.log(props, 'FailedDetail');
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    position: 'bottomRight',
    showSizeChanger: true,
    pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
    showTotal: (total: number) => `共 ${total} 条目`,
  });

  // const onTableChange = (pagination, filters, sorter) => {
  //   console.log(pagination, 'pagination');
  //   setPagination((value) => {
  //     console.log(value, 'valuess');
  //     return {
  //       ...value,
  //       ...pagination,
  //     };
  //   });
  // };

  const renderTable = (item: any, data: HostInfo[] | any, i: number) => {
    if (item.type !== 'nameDuplicate') {
      data = data.map((item: any, index: number) => {
        item.id = index;
        return item;
      });
    }
    return (
      <div key={i}>
        <div style={{ paddingLeft: '20px', height: '40px', display: 'flex', alignItems: 'center', backgroundColor: '#F9F9FA' }}>
          <span>失败原因:</span>
          <Tag style={{ padding: '4px', marginLeft: '8px' }} color="error">
            {item.text}
          </Tag>
        </div>
        {item.type !== 'nameDuplicate' ? (
          <div style={{ padding: '0 20px', margin: '24px 0 8px' }}>
            <ProTable
              isCustomPg={true}
              showQueryForm={false}
              tableProps={{
                showHeader: false,
                rowKey: 'serviceName',
                // loading: loading,
                columns: item.columns,
                dataSource: data,
                // paginationProps: { ...pagination },
                // attrs: {
                //   // className: 'frameless-table', // 纯无边框表格类名
                //   // bordered: true,   // 表格边框
                //   onChange: onTableChange,
                // },
                // filterType="none"
                // showReloadBtn={false}
                // showSearch={false}
                // antProps={{
                //   rowKey: "id"
                // }}
                // columns={item.columns}
                // dataSource={data}
              }}
            />
          </div>
        ) : (
          <div style={{ padding: '0 20px', margin: '24px 0' }}>
            <span>重复应用名:{data}</span>
          </div>
        )}
      </div>
    );
  };

  return (
    <Drawer title="同步失败主机详情" width={520} onClose={props.onClose} visible={props.isDetailVisible} className="container-drawer">
      {typeMap.map((item, index) => {
        if (item.type === 'nameDuplicate' && props?.failedDetail[item.type] === 1) {
          return renderTable(item, props?.failedDetail['serviceName'], index);
        }
        if (props?.failedDetail[item.type] && props?.failedDetail[item.type]?.length) {
          return renderTable(item, props?.failedDetail[item.type], index);
        }
        return '';
      })}
    </Drawer>
  );
};

export default {};
