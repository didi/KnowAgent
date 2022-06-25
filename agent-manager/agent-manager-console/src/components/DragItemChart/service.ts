import { Utils } from '@didi/dcloud-design';
export const queryChartData = (url: string, params: Record<string, any>): any => {
  return Utils.post(url, params);
};
