import { IRdAgentMetrics } from '../interface/agent';

interface IUser {
  id: number;
  name: string;
}

type UserState = {
  user: IUser;
  loading: boolean;
}

type UserAction = {
  type: string
  article: IUser
}

type DispatchType = (args: UserAction) => UserAction


type ModalState = {
  loading: boolean;
  modalId: string;
  drawerId: string;
  params: any;
}

type AgentState = {
  timeRange: moment.Moment[];
}

type CollectState = {
  collectType: number;
  logType: string;
}

type fullScreenState = {
  content: JSX.Element;
}

type echartsState = {
  loading: boolean;
  chartMetrics: IRdAgentMetrics[];
}

interface IPermPoint {
  [key: string]: boolean;
}

type PermPointState = {
  points: IPermPoint
}
interface IProject {
  id: number;
  ident: string;
  path: string;
  pwd?: string;
}

interface ITenant {
  id: number;
  ident: string;
}

type TenantProjectState = {
  loading: boolean,
  project: IProject,
  tenant: ITenant
}