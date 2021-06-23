export const yyyyMMDDHHMMSS = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{4}(\-|\/|.)\d{2}\1\d{2}(\s|\-|\/|.|:){0,1}\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}/g;  //匹配YYYYMMDDHHMMSS时间格式正则

export const yyMMDDHHMMSS = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}\1\d{2}(\s|\-|\/|.|:){0,1}\d{2}(\-|\/|.|:)?\d{2}(\-|\/|.|:)?\d{2}/g; //匹配YYMMDDHHMMSS时间格式正则

export const yyMMDDHHMM = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}\1\d{2}(\s|\-|\/|.|:){0,1}\d{2}(\-|\/|.|:)\d{2}/g; //匹配YYMMDDHHMM时间格式正则

export const yyyyMMDD = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{4}(\-|\/|.)\d{2}\1\d{2}/g;  //匹配YYYYMMDD时间格式正则
    
export const yyMMDD = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}\1\d{2}/g;  //匹配YYMMDD时间格式正则
    
// export const yyMM = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}(\-|\/|.)\d{2}/g;  //匹配YYMM时间格式正则
    
// export const yY = /[^0-9|a-zA-Z|\u4e00-\u9fa5|\s]\d{2}/g;  //匹配YY时间格式正则

export const logArr:any = []
export let logFilePathKey = 0;
export let hostNameList:any = []

export const setHostNameList=(hostlist:any)=>{
  hostNameList = hostlist
}

export const setlogFilePathKey=(key:number)=>{
  logFilePathKey = key
}