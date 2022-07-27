/* eslint-disable react/no-children-prop */
import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Upload, IconFont, message, notification } from '@didi/dcloud-design';
import * as SparkMD5 from 'spark-md5';
import { getAgentVersion } from '../../api/agent';
import { formFetch } from '../../lib/fetch';
import { request } from '../../request/index';

export const modifyAgentVersion = (params: any) => {
  return request('/api/v1/op/version' + `?agentVersionDescription=${params.agentVersionDescription}&id=${params.id}`, {
    method: 'PUT',
    // body: JSON.stringify(params),
  });
};

export const addAgentVersion = (params: any) => {
  const formData = new FormData();
  formData.append('uploadFile', params.uploadFile.fileList[0].originFileObj);
  formData.append('agentPackageName', params.agentPackageName);
  formData.append('agentVersion', params.agentVersion);
  formData.append('agentVersionDescription', params.agentVersionDescription);
  formData.append('fileMd5', params.fileMd5);
  console.log(formData, 'formData');
  return formFetch('/api/v1/op/version', {
    method: 'POST',
    body: formData,
  });
};

export const deleteAgentVersion = (agentVersionId: number) => {
  return request(`/api/v1/op/version/${agentVersionId}`, {
    method: 'DELETE',
    init: {
      errorNoTips: true,
    },
  });
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
        // Computation ended, last chunk has been processed. Return as Promise value.
        // This returns the base64 encoded md5 hash, which is what
        // Rails ActiveStorage or cloud services expect
        // resolve(btoa(spark.end(true)));

        // If you prefer the hexdigest form (looking like
        // '7cf530335b8547945f1a48880bc421b2'), replace the above line with:
        // resolve(spark.end());
        resolve(spark.end());
      }
    };

    processChunk(0);
  });
};

// Agent版本 => 新增版本
export const ActionVersionForm: React.FC = (props: any) => {
  console.log(props, 'ActionVersionForm');
  const [isFirst, setisFirst] = useState(true);
  const [version, setVersion] = useState({
    agentVersion: '',
    uploadFile: '',
    agentVersionDescription: '',
    agentPackageName: '',
  });
  if (isFirst && props.containerData) {
    setVersion({ ...props.containerData });
    setisFirst(false);
  }

  useEffect(() => {
    if (props.submitEvent !== 1) {
      if (props.containerData && props.containerData?.agentVersionId) {
        console.log('edit执行');
        const params = {
          id: props.containerData.agentVersionId,
          agentVersionDescription: props.form.getFieldsValue().agentVersionDescription
            ? props.form.getFieldsValue().agentVersionDescription
            : '',
        };
        modifyAgentVersion(params)
          .then((res: any) => {
            message.success('修改成功！');
            props.genData();
            //调用传入的genData函数更新列表数据
            props.setVisible(false);
          })
          .catch((err: any) => {
            message.error(err.message);
          });
      } else {
        props.form.validateFields().then((values) => {
          values.file = values.uploadFile.fileList[0].originFileObj;

          return computeChecksumMd5(values.file).then((md5: any) => {
            const params = {
              agentPackageName: values.file.name,
              fileMd5: md5,
              ...values,
              agentVersionDescription: values.agentVersionDescription ? values.agentVersionDescription : '',
            };
            addAgentVersion(params).then((res: any) => {
              message.success('新增成功！');
              props.genData(); //调用传入的genData函数更新列表数据
              props.setVisible(false);
            });
          });
        });
      }
    }
  }, [props.submitEvent]);
  return (
    <div style={{ padding: '0 24px' }}>
      <Form
        form={props.form}
        name="actionVersion"
        // labelCol={{ span: 4 }}
        // wrapperCol={{ span: 20 }}
        layout="vertical"
      >
        <Form.Item
          label="版本号："
          name="agentVersion"
          rules={[
            {
              required: true,
              message: '请输入版本号3位格式的版本号，如1.0.0',
              validator: (rule: any, value: any, callback: any) => {
                try {
                  if (value == '') {
                    callback('请输入版本号');
                  } else {
                    if (!/^([1-9]+\.[0-9]+\.[0-9]+)$/.test(value)) {
                      callback('请输入版本号3位格式的版本号，如1.0.0');
                    } else {
                      callback();
                    }
                  }
                } catch (err) {
                  callback(err);
                }
              },
            },
          ]}
        >
          {version.agentVersion ? version.agentVersion : <Input placeholder="请输入版本号3位格式的版本号，如1.0.0" />}
        </Form.Item>
        <Form.Item
          label="版本包："
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
          {version.agentPackageName ? (
            version.agentPackageName
          ) : (
            <Upload beforeUpload={(file: any) => false}>
              <Button>
                <IconFont type="icon-shangchuan" />
                点击上传
              </Button>
            </Upload>
          )}
        </Form.Item>
        <Form.Item
          label="版本描述："
          name="agentVersionDescription"
          initialValue={version && version.agentVersionDescription}
          rules={[
            {
              required: false,
              message: '请输入该版本描述，50字以内',
            },
          ]}
        >
          <Input placeholder="请输入该版本描述，50字以内" maxLength={50} />
        </Form.Item>
      </Form>
    </div>
  );
};

// 删除Agent版本
export const DeleteAgentVersion: React.FC = (props: any) => {
  const { containerData, genData } = props;
  useEffect(() => {
    if (props.submitEvent !== 1) {
      if (containerData?.agentVersionId && !containerData?.isBatch) {
        // 删除版本单行操作
        deleteAgentVersion(props.containerData.agentVersionId)
          .then((res: any) => {
            notification.success({
              message: '删除成功',
              duration: 3,
            });
            genData();
            props.setVisible(false);
          })
          .catch((err: any) => {
            props.setVisible(false);
            notification.error({
              message: '错误',
              duration: 3,
              description: err?.message || '该版本包有Agent使用！如需删除，请先更改Agent版本',
            });
          });
      } else if (containerData?.selectRowKeys.length > 0 && containerData?.isBatch) {
        // 删除版本批量操作 需要替换接口
        deleteAgentVersion(containerData?.selectRowKeys?.join())
          .then((res: any) => {
            // message.success('删除成功');
            notification.success({
              message: '删除成功',
              duration: 3,
            });
            genData();
            props.setVisible(false);
          })
          .catch((err: any) => {
            console.log(err);
            props.setVisible(false);
            notification.error({
              message: '错误',
              duration: 3,
              description: err?.message || '该版本包有Agent使用！如需删除，请先更改Agent版本',
            });
          });
      }
    }
  }, [props.submitEvent]);
  return <div style={{ padding: '0 24px' }}>确认删除Agent版本吗？</div>;
};

// agent版本好
export const agentvisionArr = async () => {
  const agentVersionList: any = await getAgentVersion();
  return agentVersionList.map((item) => {
    return { title: item.agentVersion, value: item.agentVersion };
  });
};
export default { agentvisionArr };
