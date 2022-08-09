import React, { useEffect } from 'react';
import { Form, Input, useDynamicList } from '@didi/dcloud-design';
import { setList } from './config';
import './index.less';

// interface ICatalogPathList extends FormComponentProps {
//   collectLogType: string;
//   cataPathlist: string[],
// }
const CatalogPathList = (props) => {
  const editUrl = window.location.pathname.includes('/edit-task');
  const { list, remove, getKey, push, resetList } = useDynamicList(['']);
  // const { getFieldDecorator, validateFieldsAndScroll } = props.form;

  const addPush = () => {
    // validateFieldsAndScroll((errors, values) => {
    //   const index = Math.max(...setList('catalog_path', values));
    //   push(values[`step2_catalog_path_${index}`]);
    // });
  };

  const Row = (index: any, item: any) => (
    <Form.Item key={getKey(index)} label="目录路径" extra="可增加，最多10个, 默认与上一个配置项内容保持一致。">
      {/* {getFieldDecorator(`step2_catalog_path_${getKey(index)}`, {
        initialValue: item,
        rules: [{ required: true, message: '请输入目录路径' }],
      })(<Input className="w-300" placeholder="如：/home/xiaoju/changjiang/logs" />)} */}
      {/* {list.length > 1 && <Icon type="minus-circle-o" className="ml-10" onClick={() => remove(index)} />}
      {list.length < 10 && <Icon type="plus-circle-o" className="ml-10" onClick={() => addPush()} />} */}
      <Input className="w-300" placeholder="如：/home/xiaoju/changjiang/logs" />
    </Form.Item>
  );

  useEffect(() => {
    if (props.collectLogType === 'file' && editUrl) {
      resetList(props.cataPathlist);
    }
  }, [props.cataPathlist, props.collectLogType]);

  return <div className="set-up">{list.map((ele, index) => Row(index, ele))}</div>;
};

export default CatalogPathList;
