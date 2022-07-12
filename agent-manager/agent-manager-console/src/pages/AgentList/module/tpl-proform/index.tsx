import React, { useState, useEffect } from 'react';
import { Form, message } from '@didi/dcloud-design';
import TplAutoPage from '../tpl-autopage';
import { request } from '../../../../request';
// mock数据
const basisInfoData = {
  name: 'myj-test-deployment',
  tag: ['app:myj-test-deployment', 'app:myj-test-deployment', 'test:deployment', 'app:myj-test-deployment'],
  create: 'root',
  cluster: 'cluster203',
  annotations: ['boos', 'myj-test-deployment', 'deployment', 'app:myj-test-deployment'],
  updateStrategy: '滚动更新',
  namespace: 'myj-test-deployment',
  description: '测试Nginx应用测试Nginx应用测试Nginx应用测试Nginx应用测试Nginx应用测试Nginx应用',
  selector: 'app:myj-test-deployment',
};

// 计算屏幕下所有详情占用的对应格数(1440~1920,大于1920)
const gridLayout = (column, config, gridTotal = 24) => {
  return config.map((item) => {
    if (item) {
      if (item.span && item.span > gridTotal / column) {
        return gridTotal;
      }
      return item.span ? item.span * column : column;
    }
  });
}; // 处理数据格式问题
const strToJson = (str) => {
  try {
    const json = eval('(' + str + ')');
    return json;
  } catch {
    return {};
  }
};

const TplProForm = (props) => {
  const [timer, setTimer] = useState(null);
  const [dataSource, setDataSource] = useState(basisInfoData);
  const { config } = props;

  const Component = (config, formData, onFinish) => {
    const [result, setResult] = useState<any>();
    const [form] = Form.useForm();

    const renderCustomEl = (propsForm) => {
      const newFormModal = config.customContentFormModal ? JSON.parse(config.customContentFormModal) : {};
      console.log(newFormModal, 'newFormModalnewFormModal');
      return <TplAutoPage pageTreeData={newFormModal} form={propsForm} />;
    };

    const onClose = (e) => {
      console.log(e, 'close callback');
    };

    useEffect(() => {
      import('@didi/dcloud-design')
        .then((data) => {
          setResult(data);
        })
        .catch((err) => console.error(`${err} => 自定义内容失败,(失败原因：组件不存在)`));
    }, [config]);
    const { proFormType, XFormProps } = config;
    try {
      const { DrawerForm, ModalForm, XForm } = result;
      if (proFormType === 'modal') {
        if (config.isCustomContent) {
          return <ModalForm {...{ ...config, modalProps: { onClose }, onFinish, renderCustomForm: renderCustomEl }} />;
        } else {
          return (
            <ModalForm
              {...{ ...config, XFormProps: { ...XFormProps, formData, form }, modalProps: { onClose }, onFinish, renderCustomForm: null }}
            />
          );
        }
      } else if (proFormType === 'drawer') {
        return (
          <DrawerForm
            {...{
              ...config,
              XFormProps: { ...XFormProps, formData, form },
              onFinish,
              drawerProps: { onClose },
              renderCustomForm: config.isCustomContent ? renderCustomEl : null,
            }}
          />
        );
      }
      return <XForm {...{ ...XFormProps, formData, form }} />;
    } catch (error) {
      console.log('报错了');
    }
  };

  /* -------提交接口相关-------- */

  const onFinish = async (values) => {
    try {
      const paramsCallback = strToJson(config.formUrlConfig['paramFun']);
      const params: any = typeof paramsCallback === 'function' ? paramsCallback(props) : null;
      const result = await request(config.formUrlConfig['api'], { ...params, method: config.formUrlConfig['methods'] }).catch((err) => {
        console.log(err, '接口有点问题');
      });

      if (result) {
        message.success('提交成功');
        return true;
      } else {
        return false;
      }
    } catch (err) {
      console.log(err, '接口相关信息没有填写,无法提交');
      return false;
    }
  };

  /* -------提交接口相关-------- */

  const genData = (props, param = {}) => {
    window.clearTimeout(timer);
    setTimer(null);
    const newTimer = setTimeout(() => {
      const strToJson = (str) => {
        const json = eval('(' + str + ')');
        return json;
      };
      try {
        const paramsCallback = strToJson(config.dataSourceUrlConfig['paramFun']);
        const params: any = typeof paramsCallback === 'function' ? paramsCallback(props) : null;
        request(config.dataSourceUrlConfig['api'], { ...params, method: config.dataSourceUrlConfig['methods'] })
          .then((data: any) => {
            if (config.dataSourceUrlConfig['resCallback']) {
              setDataSource(eval(config.dataSourceUrlConfig['resCallback'])(data));
            } else {
              setDataSource(data);
            }
          })
          .catch((err) => {
            console.log('接口有点问题');
          });
      } catch (err) {
        console.log(12231);
      }
    }, 1000);
    setTimer(newTimer);
  };

  // const getRenderCustom = async ({ config, ...rest }) => {
  //   let arr = []
  //   for (let index = 0; index < config.length; index++) {
  //     let item = config[index]
  //     if (item?.tableFunction?.customCompontend) {
  //       const path = `./module/${item.tableFunction.customCompontend}`;
  //       const result = await import(`${path}`).catch(err => console.error(`${item.label} => 自定义内容失败,(失败原因：组件不存在)`));
  //       console.log(result, 'result');
  //       const CustomFun = result.default || null;
  //       delete item.tableFunction;
  //       CustomFun ?
  //         arr.push({
  //           ...item,
  //           renderCustom: (ct) => {
  //             console.log(ct);
  //             return <CustomFun content={ct} {...config} />;
  //           },
  //         })
  //         :
  //         arr.push({
  //           ...item
  //         })
  //     } else {
  //       arr.push({
  //         ...item
  //       })
  //     }
  //   }
  //   // console.log(arr)
  //   setConfigData({ ...rest, config: arr })
  //   // setRenderCustomConfig(arr)
  // };

  useEffect(() => {
    genData(props.dataSourceUrlConfig);
  }, []);

  // useEffect(() => {
  //   getRenderCustom(config);
  // }, [config]);

  return <>{Component(config, dataSource, onFinish)}</>;
};

export default TplProForm;
