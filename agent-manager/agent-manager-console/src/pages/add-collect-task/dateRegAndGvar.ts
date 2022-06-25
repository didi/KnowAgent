/*
yyyy-MM-dd HH:mm:ss.SSS
yyyy-MM-dd HH:mm:ss
HH:mm:ss.SSS
*/
export const yyyyMMDDHHMMss = /\d{4}(\-|\/|.)\d{2}\1\d{2}(\s){0,1}\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}/g; //匹配YYYYMMDDHHMMSS时间格式正则

export const yyyyMMDDHHMMssSSS = /\d{4}(\-|\/|.)\d{2}\1\d{2}(\s){0,1}\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}(\.)\d{3}/g; //匹配YYYYMMDDHHMMSS时间格式正则
export const yyyyMMDDHHMMssSS = /\d{4}(\-|\/|.)\d{2}\1\d{2}(\s){0,1}\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}(\,)\d{3}/g; //匹配YYYYMMDDHHMMSS时间格式正则

export const HHmmssSSS = /\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}(.)\d{3}/g;

// export const yyMMDDHHMMSS = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}\1\d{2}(\s|\-|\/|.|:){0,1}\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}/g; //匹配YYMMDDHHMMSS时间格式正则

// export const yyMMDDHHMM = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}\1\d{2}(\s|\-|\/|.|:){0,1}\d{2}(\-|\/|.|:)\d{2}/g; //匹配YYMMDDHHMM时间格式正则

// export const yyyyMMDD = /[^0-9|a-zA-Z|\u4e00-\u9fa5|]?\d{4}(\-|\/|.)\d{2}\1\d{2}/g;  //匹配YYYYMMDD时间格式正则

// export const yyMMDD = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}\1\d{2}/g;  //匹配YYMMDD时间格式正则

export const fuhao = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]/g;
// export const yyMM = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}/g;  //匹配YYMM时间格式正则

// export const yY = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}/g;  //匹配YY时间格式正则

export let logFilePathKey = 0;
export let hostNameList: any = [];

export const setHostNameList = (hostlist: any) => {
  hostNameList = hostlist;
};

export const setlogFilePathKey = (key: number) => {
  logFilePathKey = key;
};
