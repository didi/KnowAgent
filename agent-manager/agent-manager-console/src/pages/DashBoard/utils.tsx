import * as React from 'react';
import { Tooltip } from '@didi/dcloud-design';
import { Link } from 'react-router-dom';

export function byteToMB(limit: number): any {
  const nums = limit / (1024 * 1024);
  if (nums === 0) {
    return 0;
  }
  if (nums < 0.01 && nums > 0) {
    return limit;
  }
  return nums.toFixed(2);
}

export function byteToGB(limit: number): any {
  const nums = limit / 1024;
  if (nums === 0) {
    return 0;
  }
  if (nums < 0.01 && nums > 0) {
    return limit;
  }
  return nums.toFixed(2);
}

export function countChange(limit: number): string {
  let size = '';
  if (limit < 100 * 1000) {
    //小于100KB
    size = limit + '';
  } else if (limit < 100 * 1000 * 1000) {
    //小于100M，则转化成K
    size = (limit / 1000).toFixed(1) + 'K';
  } else if (limit < 100 * 1000 * 1000 * 1000) {
    //小于100G，则转化成MB
    size = (limit / (1000 * 1000)).toFixed(1) + 'M';
  }

  const sizeStr = size + ''; //转成字符串
  const index = sizeStr.indexOf('.'); //获取小数点处的索引
  const dou = sizeStr.substr(index + 1, 2); //获取小数点后两位的值
  if (dou == '00') {
    //判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return sizeStr;
}

/**参数说明：
 * 根据长度截取先使用字符串，超长部分追加…
 * str 对象字符串
 * len 目标字节长度
 * 返回值： 处理结果字符串
 */

export function cutString(str: string, len: number): string {
  //length属性读出来的汉字长度为1
  if (str.length * 2 <= len) {
    return str;
  }

  let strlen = 0;
  let s = '';

  for (let i = 0; i < str.length; i++) {
    s = s + str.charAt(i);
    if (str.charCodeAt(i) > 128) {
      strlen = strlen + 2;
      if (strlen >= len) {
        return s.substring(0, s.length - 1) + '...';
      }
    } else {
      strlen = strlen + 1;
      if (strlen >= len) {
        return s.substring(0, s.length - 2) + '...';
      }
    }
  }
  return s;
}

interface INavRouterLinkProps {
  element: JSX.Element | string;
  href: string;
  needToolTip?: boolean;
  state?: any;
  key?: any;
  textLength?: number;
}

export const TextRouterLink = (props: INavRouterLinkProps): any => {
  return (
    <Link
      to={{
        pathname: props.href,
        state: props.state,
      }}
    >
      {props.needToolTip ? (
        <Tooltip key={props.key} placement="bottomLeft" title={props.element}>
          {typeof props.element === 'string'
            ? props?.element?.length > 26
              ? props?.element.substring(0, 26) + '...'
              : props?.element
            : props.element}
        </Tooltip>
      ) : (
        props.element
      )}
    </Link>
  );
};
