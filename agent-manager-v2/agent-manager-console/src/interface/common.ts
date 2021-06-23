export interface IStringMap {
  [index: string]: string;
}

export interface INumberMap {
  [index: number]: string;
}

export interface ICookie {
  key: string;
  value?: string;
  time?: number;
}

export interface IPageRouteItem {
  path: string;
  exact: boolean;
  component: React.ComponentType<any>;
  isNoPermission?:boolean;
}

export interface IBaseInfo {
  key: string;
  label: string;
  unit?: string;
  name?: string;
  invisible?: boolean;
  render?: (text: any) => JSX.Element | string;
}
export interface ListInfo {
  id?: any;
  key: string;
  kafkaClusterName?: any;
  createtime?: any;
  createTime?: any;
  serviceName?: any;
  container?: any;
  agentOperationTaskType?: any;
  relationHostCount?: any;
  agentOperationTaskStatus?: any;
  agentOperationTaskStartTime?: any;
  agentOperationTaskEndTime?: any;
  operator?: any;
  agentVersion?: any;
  agentVersionCreateTimeStart?: any;
  operateId?: any;
  operateTime?: any;
  render?: (text: any, record: any) => JSX.Element;
}

export interface ILabelValue {
  value: string | number;
  label?: string;
  text?: string;
}

export interface IFormProps {
  form: any;
  wrappedComponentRef?: any;
  formData?: any;
}

export interface DataSourceItemType {
  text: string;
  value: string;
}