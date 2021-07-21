

export const regName = /^([-_.a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制

export const regProducerName = /^[-\w]{1,1024}$/; // 最大输入长度为1024位

export const regLogSliceTimestampPrefixString = /^[\s\S]{1,128}$/ // 最大输入长度128位

export const regAdress = /^([-_.:,a-zA-Z0-9\u4e00-\u9fa5]{1,32})$/; // 支持中英文字母、大小写、数字、下划线、点、短横线。32位限制

export const regIp = /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/; // 支持9位数字，一共12位

export const regText = /^(.{0,50})$/;

export const regChar = /^(.{1})$/;

// validator: (rule: any, value: string, callback: any) => {
//   try {
//     if (!!value || new RegExp(regClusterName).test(value)) {
//       throw new Error('请输入集群名称，支持大、小写字母、数字、-、_');
//     }
//   } catch (err) {
//     callback(err);
//     return false;
//   }
// }
