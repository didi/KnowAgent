import React, { useState, useEffect } from 'react';
import { ProDescriptions } from '@didi/dcloud-design';
import { request } from '../../../../request/index';
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

const BasicInfo = (props) => {
  console.log(props, 'props-BasocInfo');
  const [timer, setTimer] = useState(null);
  const [dataSource, setDataSource] = useState(basisInfoData);
  const { config } = props;
  const [configData, setConfigData] = useState<any>({});

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

  const getRenderCustom = async (props) => {
    // const { config, ...rest }=props
    const newConfig = props?.config || [];
    const arr = [];
    for (let index = 0; index < newConfig.length; index++) {
      const item = newConfig[index];
      if (item?.tableFunction?.customCompontend) {
        const path = `./module/${item.tableFunction.customCompontend}`;
        const result = await import(`${path}`).catch((err) => console.error(`${item.label} => 自定义内容失败,(失败原因：组件不存在)`));
        console.log(result, 'result');
        const CustomFun = result.default || null;
        delete item.tableFunction;
        CustomFun
          ? arr.push({
              ...item,
              renderCustom: (ct) => {
                console.log(ct);
                return <CustomFun content={ct} {...newConfig} />;
              },
            })
          : arr.push({
              ...item,
            });
      } else {
        arr.push({
          ...item,
        });
      }
    }
    // console.log(arr)
    setConfigData({ ...props, config: arr });
    // setRenderCustomConfig(arr)
  };

  useEffect(() => {
    genData(props.dataSourceUrlConfig);
  }, []);

  useEffect(() => {
    getRenderCustom(config);
    console.log(config, 'config');
  }, [config]);

  return (
    <>
      <ProDescriptions {...{ config: [], ...configData, dataSource: dataSource }} />
    </>
  );
};

export default BasicInfo;
