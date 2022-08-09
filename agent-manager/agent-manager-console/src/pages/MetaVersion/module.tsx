/* eslint-disable react/no-children-prop */
import React, { useState, useEffect } from 'react';
import { Form, Input, ProTable, Tooltip, Upload, IconFont, Button, notification, message } from '@didi/dcloud-design';
import { request } from '../../request/index';
import * as SparkMD5 from 'spark-md5';
import { formFetch } from '../../lib/fetch';

const { TextArea } = Input;
const regProducerName = /^[\s\S]{1,1024}$/; // 任意字符最大输入长度为1024位

// 上传 metadata excel 文件 & 描述信息，返回元数据上传记录 id
export const addMetadata = (params: any) => {
  const formData = new FormData();
  formData.append('uploadFile', params.uploadFile.fileList[0].originFileObj);
  formData.append('fileMd5', params.fileMd5);
  formData.append('description', params.description);
  console.log(formData, 'formData');
  return formFetch('/api/v1/op/metadata', {
    method: 'POST',
    body: formData,
  });
};

// 根据元数据上传记录 id 删除对应 metadata excel 文件上传记录
export const deleteMetadata = (id: number) => {
  return request(`/api/v1/op/metadata/${id}`, {
    method: 'DELETE',
  });
};

// 根据元数据上传记录 id 获取对应 metadata excel 文件内容信息
export const getFileContent = (id: number) => {
  return request(`/api/v1/op/metadata/file-content/${id}`);
};

// 根据元数据上传记录 id 导入 metadata excel 文件中元数据内容
export const getImportResult = (id: number) => {
  return request(`/api/v1/op/metadata/import-result/${id}`);
};

// 返回 meta data excel 文件模板下载请求对应链接，端口号后拼该链接即为 excel 模板文件下载链接
export const getExcelTemplate = () => {
  return request(`/api/v1/op/metadata/meta-data-excel-template`);
};

export const computeChecksumMd5 = (file: File) => {
  return new Promise((resolve, reject) => {
    const chunkSize = 2097152; // Read in chunks of 2MB
    const spark = new SparkMD5.ArrayBuffer();
    const fileReader = new FileReader();

    let cursor = 0; // current cursor in file

    fileReader.onerror = () => {
      reject('MD5 computation failed - error reading the file');
    };

    function processChunk(chunkStart: number) {
      const chunkEnd = Math.min(file.size, chunkStart + chunkSize);
      fileReader.readAsArrayBuffer(file.slice(chunkStart, chunkEnd));
    }

    fileReader.onload = (e: any) => {
      spark.append(e.target.result); // Accumulate chunk to md5 computation
      cursor += chunkSize; // Move past this chunk

      if (cursor < file.size) {
        processChunk(cursor);
      } else {
        resolve(spark.end());
      }
    };

    processChunk(0);
  });
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

export const hostColumns = [
  {
    title: '主机名',
    dataIndex: 'hostName',
    key: 'hostName',
    render: (_, record: string[]) => record[0],
  },
  {
    title: '主机IP',
    dataIndex: 'ip',
    key: 'ip',
    render: (_, record) => record[1],
  },
  {
    title: '主机类型',
    dataIndex: 'type',
    key: 'type',
    render: (_, record) => record[2],
  },
];

export const applicationColumns = [
  {
    title: '应用名',
    dataIndex: 'applicationName',
    key: 'applicationName',
    width: '50%',
    render: (_, record) => record[0],
  },
  {
    title: '关联主机对应主机名',
    dataIndex: 'connectHostname',
    key: 'connectHostname',
    width: '50%',
    render: (_, record) => record[1],
  },
];

interface IImportResultType {
  applicationTable: [];
  hostTable: [];
}

const handleDownload = (url: string) => {
  const link = document.createElement('a');
  link.style.display = 'none';
  link.href = url;
  link.download = url;
  document.body.appendChild(link);
  link.click();
  URL.revokeObjectURL(link.href);
  document.body.removeChild(link);
};

// 上传文件
export const ActionFileForm = (props: any) => {
  useEffect(() => {
    if (props.submitEvent !== 1) {
      props.form.validateFields().then((values) => {
        values.file = values.uploadFile.fileList[0].originFileObj;
        return computeChecksumMd5(values.file).then((md5: any) => {
          const params = {
            ...values,
            fileMd5: md5,
            description: values.description,
          };
          addMetadata(params).then((res) => {
            message.success('新增成功！');
            props.genData(); //调用传入的genData函数更新列表数据
            props.setVisible(false);
          });
        });
      });
    }
  }, [props.submitEvent]);

  const _getExcelTemplate = async () => {
    const url = await getExcelTemplate();
    const origin = window.location.origin;
    const fileurl = origin + url;
    handleDownload(fileurl);
  };

  return (
    <div style={{ padding: '0 24px' }}>
      <Form form={props.form} name="actionApp" layout="vertical">
        <Form.Item
          label={
            <div className="version-label">
              <span>元数据Excel文件：</span>
              <Button type="link" onClick={_getExcelTemplate}>
                元数据Excel模板文件下载
              </Button>
            </div>
          }
          name="uploadFile"
          rules={[
            {
              required: true,
              validator: (rule: any, value: any, callback: any) => {
                if (value?.fileList?.length) {
                  if (value.fileList.length > 1) {
                    callback('一次仅支持上传一份文件！');
                  } else {
                    callback();
                  }
                } else {
                  callback(`请上传文件`);
                }
                callback();
              },
            },
          ]}
        >
          <Upload beforeUpload={(file: any) => false} accept=".xls,.xlsx">
            <Button>
              <IconFont type="icon-shangchuan" />
              点击上传
            </Button>
          </Upload>
        </Form.Item>
        <Form.Item
          label="描述："
          name="description"
          rules={[
            {
              required: true,
              validator: (rule: any, value: string, cb: any) => {
                if (!value) cb('请输入文件描述');
                if (!new RegExp(regProducerName).test(value)) {
                  cb('最大输入长度为1024位');
                }
                cb();
              },
            },
          ]}
        >
          <TextArea placeholder="请输入" />
        </Form.Item>
      </Form>
    </div>
  );
};

// 预览文件
export const previewFile: React.FC = (props: any) => {
  const { containerData } = props;
  const [loading, setLoading] = useState(false);
  const [datasource, setDatasource] = useState({} as IImportResultType);

  useEffect(() => {
    _getFileContent();
  }, []);

  const _getFileContent = async () => {
    setLoading(true);
    try {
      const res = (await getFileContent(containerData.id)) as IImportResultType;
      res && setDatasource(res);
    } catch (error) {
      console.log('error', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
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
        <span>主机信息</span>
      </div>
      <div className="host-table">
        <ProTable
          isCustomPg={true}
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'hostName',
            loading: loading,
            columns: hostColumns,
            dataSource: datasource?.hostTable || [],
          }}
        />
      </div>
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
        <span>应用信息</span>
      </div>
      <div className="application-table">
        <ProTable
          isCustomPg={true}
          showQueryForm={false}
          tableProps={{
            showHeader: false,
            rowKey: 'hostName',
            loading: loading,
            columns: applicationColumns,
            dataSource: datasource?.applicationTable || [],
          }}
        />
      </div>
    </>
  );
};

// 删除文件
export const DeleteFile: React.FC = (props: any) => {
  const { containerData, genData, setVisible } = props;

  useEffect(() => {
    console.log(props.submitEvent, 'props.submitEvent');

    if (props.submitEvent !== 1) {
      if (containerData?.id && !containerData?.isBatch) {
        deleteMetadata(props.containerData.id)
          .then((res: any) => {
            setVisible(false);
            genData();
            notification.success({
              message: '删除成功',
              duration: 3,
            });
          })
          .finally(() => props.setVisible(false));
      }
    }
  }, [props.submitEvent]);

  return (
    <div style={{ padding: '0 24px' }}>
      <p>是否确认删除{props.containerData?.fileName}？</p>
      <p>删除操作不可恢复，请谨慎操作！</p>
    </div>
  );
};

// 导入元数据
export const importMeta: React.FC = (props: any) => {
  const { containerData, genData, setVisible } = props;

  useEffect(() => {
    if (props.submitEvent !== 1) {
      if (containerData?.id && !containerData?.isBatch) {
        getImportResult(props.containerData.id)
          .then((res: any) => {
            setVisible(false);
            genData();
            notification.success({
              message: '导入成功',
              duration: 3,
            });
          })
          .finally(() => props.setVisible(false));
      }
    }
  }, [props.submitEvent]);

  return (
    <div style={{ padding: '0 24px' }}>
      <p>是否确认导入元数据{props.containerData?.fileName}？</p>
    </div>
  );
};
