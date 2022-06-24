import { Utils } from '@didi/dcloud-design';
const { formatDate, formatDuration, formatSize } = Utils;

const K = 1024;
const M = 1024 * K;
const G = 1024 * M;
const T = 1024 * G;

const seconds = 1000;
const minutes = 60 * seconds;
const hours = 60 * minutes;
const day = 24 * hours;
const week = 7 * day;
const month = 30 * day;
const quarter = 90 * day;
const year = 365 * day;

const formatList = {
  Ys: 'YYYY-MM-DD HH:mm:ss',
  Yd: 'YYYY-MM-DD',
  Md: 'MM-DD',
  Hs: 'HH:mm:ss',
  Ms: 'MM-DD HH:mm:ss',
};
export const getTimeType = (data, format = 'Ys') => {
  return formatDate(data, formatList[format]);
};

export const getTargetValueByUnit = (value: number, unit = '') => {
  if (value === null || isNaN(value)) return '-';
  let result = value;
  switch (unit) {
    case 'K':
      result = value / K;
      break;
    case 'M':
      result = value / M;
      break;
    case 'G':
      result = value / G;
      break;
    case 'T':
      result = value / T;
      break;
  }
  return numberToFixed(result);
};

export const getTargetValueTime = (value: number, unit = '', fixed = 2) => {
  if (value === null || isNaN(value)) return '-';
  let result = value;
  switch (unit) {
    case 's':
      result = value / seconds;
      break;
    case 'm':
      result = value / minutes;
      break;
    case 'h':
      result = value / hours;
      break;
    case 'd':
      result = value / day;
      break;
    case 'w':
      result = value / week;
      break;
    case 'M':
      result = value / month;
      break;
    case 'Q':
      result = value / quarter;
      break;
    case 'Y':
      result = value / year;
      break;
  }
  return numberToFixed(result, fixed);
};

export const numberToFixed = (value: number, num = 2) => {
  if (value === null || isNaN(value)) return '-';
  value = Number(value);
  return Number.isInteger(+value) ? +value : (+value).toFixed(num);
};

export const formatMap = {
  password: (t: number, record) => {
    return '';
  },
  money: (t: number, record) => {
    return '';
  },
  textarea: (t: number, record) => {
    return '';
  },
  option: (t: number, record) => {
    return '';
  },
  date: (t: number, record) => {
    return '';
  },
  dateWeek: (t: number, record) => {
    return t ? getTargetValueTime(t, 'w', 0) : '-';
  },
  dateMonth: (t: number, record) => {
    return t ? getTargetValueTime(t, 'M', 0) : '-';
  },
  dateQuarter: (t: number, record) => {
    return t ? getTargetValueTime(t, 'Q', 0) : '-';
  },
  dateYear: (t: number, record) => {
    return t ? getTargetValueTime(t, 'Y', 0) : '-';
  },
  dateRange: (t: number, record) => {
    return '';
  },
  dateTimeRange: (t: number, record) => {
    return '';
  },
  dateTime: (t: number, record) => {
    return getTimeType(t);
  },
  time: (t: number, record) => {
    return t ? formatDuration(t) : '-';
  },
  timeRange: (t: number, record) => {
    return '';
  },
  text: (t: number, record) => {
    return '';
  },
  select: (t: number, record) => {
    return '';
  },
  checkbox: (t: number, record) => {
    return '';
  },
  rate: (t: number, record) => {
    return '';
  },
  radio: (t: number, record) => {
    return '';
  },
  radioButton: (t: number, record) => {
    return '';
  },
  index: (t: number, record) => {
    return '';
  },
  indexBorder: (t: number, record) => {
    return '';
  },
  progress: (t: number, record) => {
    return '';
  },
  percent: (t: number, record) => {
    return '';
  },
  digit: (t: number, record) => {
    return '';
  },
  second: (t: number, record) => {
    return '';
  },
  avatar: (t: number, record) => {
    return '';
  },
  code: (t: number, record) => {
    return '';
  },
  switch: (t: number, record) => {
    return '';
  },
  fromNow: (t: number, record) => {
    return '';
  },
  image: (t: number, record) => {
    return '';
  },
  jsonCode: (t: number, record) => {
    return '';
  },
  'sizeB-K': (t: number, record) => {
    return t ? getTargetValueByUnit(t, 'K') : '-';
  },
  'sizeB-M': (t: number, record) => {
    return t ? getTargetValueByUnit(t, 'M') : '-';
  },
  'sizeB-G': (t: number, record) => {
    return t ? getTargetValueByUnit(t, 'G') : '-';
  },
  formatSize: (t: number, record) => {
    return t ? formatSize(t) : '-';
  },
  custom: (t: number, record) => {
    return '';
  },
};
