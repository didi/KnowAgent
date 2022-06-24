import { notification } from '@didi/dcloud-design';
// import { csrfTokenMethod } from '../api/api';
// import store from '../store';

const window = self.window;

export interface IRes {
  code: number;
  message: string;
  data: any;
}

const checkStatus = (res: Response) => {
  if (res.status === 401) {
    notification.error({ message: '无权限访问' });
    return null;
  }

  if (res.status === 405 || res.status === 403) {
    // location.href = '/403';
    return null;
  }

  if (res.status == 500) {
    notification.error({
      message: '错误',
      duration: 3,
      description: '服务错误，请重试！',
    });
    throw res;
  }

  return res;
};

const filter = (init: IInit) => (res: IRes) => {
  if (init.needCode || res.code === undefined) {
    return res;
  }

  if (res.code !== 0 && res.code !== 200) {
    if (res.code === 21001) return;
    if (!init.errorNoTips) {
      notification.error({
        message: '错误',
        duration: init.needDuration ? null : 3,
        description: res.message || '服务错误，请重试！',
      });
    }

    throw res;
  }

  return res.data;
};

interface IInit extends RequestInit {
  errorNoTips?: boolean;
  body?: BodyInit | null | any;
  query?: any;
  needCode?: boolean;
  needDuration?: boolean;
}

export default function fetch(url: string, init?: IInit) {
  if (!init) init = {};

  if (!init.credentials) init.credentials = 'include';
  if (init.body && typeof init.body === 'object') init.body = JSON.stringify(init.body);
  if (init.body && !init.method) init.method = 'POST';
  if (init.method) init.method = init.method.toUpperCase();

  init.headers = Object.assign({}, init.headers, {
    'Content-Type': 'application/json',
  });

  // if (csrfTokenMethod.includes(init.method || 'GET')) {
  //   init.headers = Object.assign({}, init.headers || {
  //     'Content-Type': 'application/json',
  //   });
  // }

  return window
    .fetch(url, init)
    .then((res) => checkStatus(res))
    .then((res) => res?.json())
    .then(filter(init));
}

export function formFetch(url: string, init?: IInit) {
  if (!init) init = {};

  init.headers = Object.assign({}, init.headers);

  return window
    .fetch(url, init)
    .then((res) => checkStatus(res))
    .then((res) => res?.json())
    .then(filter(init));
}
