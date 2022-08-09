import { useRef, useEffect, useCallback } from 'react';
import { message } from '@didi/dcloud-design';
import moment from 'moment';
import { oneDayMillims } from '../constants/common';
import { ICookie, IStringMap } from '../interface/common';
import * as SparkMD5 from 'spark-md5';
// import store from '../store';
// import * as actions from '../actions';

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
    date.setTime(date.getTime() + ele.time * oneDayMillims);
    const expires = 'expires=' + date.toUTCString();
    document.cookie = ele.key + '=' + ele.value + '; ' + expires + '; path=/';
  });
};

export const deleteCookie = (cData: string[]) => {
  setCookie(cData.map((i) => ({ key: i, value: '', time: -1 })));
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
  return value === undefined || value === null || value === '' ? '' : value;
};

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
  };
};

export const unique = (arr: any) => {
  // 去重
  return arr.filter((item: any, index: number, arr: any) => {
    //当前元素，在原始数组中的第一个索引==当前索引值，否则返回当前元素
    return arr.indexOf(item, 0) === index;
  });
};

// export const dealPostMessage = (data: any) => {
//   const { type } = data;
//   switch (type) {
//     case 'permissionPoint':
//       store.dispatch(actions.setPermissionPoints(data.value));
//       break;
//     // case 'tenantProject':
//     //   store.dispatch(actions.setTenantProject(data.value));
//     //   break;
//   }
// };

/**
 * @method useDebounce 应用于hook的防抖函数
 * @param {() => any} fn 需要处理的函数
 * @param {number} delay 函数出发的时间
 * @param dep 传入空数组，保证useCallback永远返回同一个函数
 */

export const useDebounce = (fn: any, delay: number, dep = []) => {
  const { current } = useRef<any>({ fn, timer: null });
  useEffect(
    function () {
      current.fn = fn;
    },
    [fn]
  );

  return useCallback(function f(...args) {
    if (current.timer) {
      clearTimeout(current.timer);
    }
    current.timer = setTimeout(() => {
      current.fn(...args);
    }, delay);
  }, dep);
};

/**
 * @description: 字节转换单位
 * @param {*} limit: number 字节
 * @return {*} string 转换后的单位
 */
export function byteChange(limit: number) {
  let size = '';
  if (limit < 1 * 1024) {
    //小于0.1KB，则转化成B
    size = limit.toFixed(2) + 'B';
  } else if (limit < 1 * 1024 * 1024) {
    //小于0.1MB，则转化成KB
    size = (limit / 1024).toFixed(2) + 'KB';
  } else if (limit < 1 * 1024 * 1024 * 1024) {
    //小于0.1GB，则转化成MB
    size = (limit / (1024 * 1024)).toFixed(2) + 'MB';
  } else {
    //其他转化成GB
    size = (limit / (1024 * 1024 * 1024)).toFixed(2) + 'GB';
  }

  const sizeStr = size + ''; //转成字符串
  const index = sizeStr.indexOf('.'); //获取小数点处的索引
  const dou = sizeStr.substr(index + 1, 2); //获取小数点后两位的值
  if (dou == '00') {
    //判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return size;
}

/**
 * @description: 字节转换成MB
 * @param {number} limit
 * @return {*}
 */
export function byteToMB(limit: number) {
  const nums = limit / (1024 * 1024);
  if (nums === 0) {
    return 0;
  }
  if (nums < 0.01 && nums > 0) {
    return limit;
  }
  return nums.toFixed(2);
}

// export function nsTo(ns: number) {
//   if (ns < 1000) {
//     return ns + 'ns';
//   }
//   if (ns < 1000 * 10000) {
//     return Number(ns / 1000).toFixed(2) + 'μs';
//   }
//   if (ns < 1000 * 1000 * 10000) {
//     return timeStamp(Number(ns / 1000 / 1000));
//   }
//   return ns + 'ns';
// }

export function ToMs(value: number) {
  const size = (value / 1000 / 1000).toFixed(2);
  const sizeStr = size + ''; //转成字符串
  const index = sizeStr.indexOf('.'); //获取小数点处的索引
  const dou = sizeStr.substr(index + 1, 2); //获取小数点后两位的值
  if (dou == '00') {
    //判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return sizeStr;
}

export function Tous(value: number) {
  return Number(value / 1000).toFixed(2);
}

/**
 * @description: ms转成为最大为 月份的单位
 * @param {*} second_time
 * @return {*}
 */
// export function timeStamp(mtime: number) {
//   if (mtime < 1000) {
//     return mtime.toFixed(2) + 'ms';
//   }
//   let second_time: any = mtime / 1000;
//   let time = second_time.toFixed(2) + 's';
//   if (parseInt(second_time) > 60) {
//     let second = parseInt(second_time) % 60;
//     let min: any = parseInt(second_time / 60);
//     time = min + '分' + second + '秒';

//     if (min > 60) {
//       min = parseInt(second_time / 60) % 60;
//       let hour = parseInt(parseInt(second_time / 60) / 60);
//       time = hour + '小时' + min + '分' + second + '秒';

//       if (hour > 24) {
//         hour = parseInt(parseInt(second_time / 60) / 60) % 24;
//         let day = parseInt(parseInt(parseInt(second_time / 60) / 60) / 24);
//         time = day + '天' + hour + '小时' + min + '分' + second + '秒';
//         if (day > 30) {
//           day = parseInt(parseInt(parseInt(second_time / 60) / 60) / 24) % 30;
//           let m = parseInt(parseInt(parseInt(second_time / 60) / 60) / 24 / 30);
//           time = m + '月' + day + '天' + hour + '小时' + min + '分' + second + '秒';
//         }
//       }
//     }
//   }

//   return time;
// }

export function PercentageConversion(value: number, isTool: string): number | string {
  if (isTool) return value + '%';
  return value * 100;
}

export function msecondToSecond(m: number) {
  const s = (m / 1000).toFixed(2) + '';
  const arr = s.split('.');
  if (arr[1] === '00') return arr[0];
  if (arr[1] === '50') return arr[0] + '.5';
  return s;
}

/**参数说明： 
 
* 根据长度截取先使用字符串，超长部分追加… 

* str 对象字符串 

* len 目标字节长度 

* 返回值： 处理结果字符串 

*/

export function cutString(str: string, len: number) {
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

export function countChange(limit: number) {
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

export const numberToFixed = (value: number, num = 2) => {
  if (value === null || isNaN(value)) return '-';
  value = Number(value);
  return Number.isInteger(+value) ? +value : (+value).toFixed(num);
};

export function numTrans(value) {
  if (value < 10000) {
    return value;
  } else if (value < 10000 * 10000) {
    return numberToFixed(value / 10000) + '万';
  } else {
    return numberToFixed(value / (10000 * 10000)) + '亿';
  }
}
