import { message } from 'antd';
import moment from 'moment';
import { oneDayMillims } from '../constants/common';
import { ICookie, IStringMap } from '../interface/common';
import * as SparkMD5 from 'spark-md5';
import store from '../store';
import * as actions from '../actions';
import {useRef,useEffect,useCallback} from 'react';

export const getCookie = (key: string): string => {
  const map: IStringMap = {};
  document.cookie.split(';').map((kv) => {
    const d = kv.trim().split('=');
    map[d[0]] = d[1];
    return null;
  });
  return map[key];
};

export const setCookie = (cData: ICookie[]) => {
  const date = new Date();
  cData.forEach((ele: any) => {
    date.setTime(date.getTime() + (ele.time * oneDayMillims));
    const expires = 'expires=' + date.toUTCString();
    document.cookie = ele.key + '=' + ele.value + '; ' + expires + '; path=/';
  });
};

export const deleteCookie = (cData: string[]) => {
  setCookie(cData.map(i => ({ key: i, value: '', time: -1 })));
};

export const copyString = (url: any) => {
  const input = document.createElement('textarea');
  input.value = url;
  document.body.appendChild(input);
  input.select();
  if (document.execCommand('copy')) {
    message.success('复制成功');
  }
  input.remove();
};

export const getMoment = () => {
  return moment();
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
export const judgeEmpty = (value: any) => {
  return (value === undefined || value === null || value === '') ? '' : value;
}

export const setLimitUnit = (value: any, v?: number) => {
  const val = Number(value);
  let maxBytesPerLogEvent = v || '';
  let flowunit = 1048576;
  if (val >= 1024) {
    maxBytesPerLogEvent = val / 1024;
    flowunit = 1024;
    if (val >= 1048576) {
      maxBytesPerLogEvent = val / 1024 / 1024;
      flowunit = 1048576;
    }
  }
  return {
    maxBytesPerLogEvent,
    flowunit,
  }
}

export const unique = (arr: any) => { // 去重
  return arr.filter((item: any, index: number, arr: any) => {
    //当前元素，在原始数组中的第一个索引==当前索引值，否则返回当前元素
    return arr.indexOf(item, 0) === index;
  });
}

export const dealPostMessage = (data: any) => {
  const { type } = data;
  switch (type) {
    case 'permissionPoint':
      store.dispatch(actions.setPermissionPoints(data.value));
      break;
    // case 'tenantProject':
    //   store.dispatch(actions.setTenantProject(data.value));
    //   break;
  }
};


/**
   * @method useDebounce 应用于hook的防抖函数
   * @param {() => any} fn 需要处理的函数
   * @param {number} delay 函数出发的时间
   * @param dep 传入空数组，保证useCallback永远返回同一个函数
   */

  export const useDebounce = (fn:any, delay: number, dep = []) => {
    const { current } = useRef<any>({ fn, timer: null });
    useEffect(function () {
      current.fn = fn;
    }, [fn]);

    return useCallback(function f(...args) {
      if (current.timer) {
        clearTimeout(current.timer);
      }
      current.timer = setTimeout(() => {
        current.fn(...args);
      }, delay);
    }, dep)
  }