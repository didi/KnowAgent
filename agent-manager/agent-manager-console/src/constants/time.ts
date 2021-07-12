
import moment from 'moment';

export interface IPeriod {
  label: string;
  key: string;
  dateRange: [moment.Moment, moment.Moment];
}

export const timeFormat = 'YYYY-MM-DD HH:mm:ss';

export const timeDate = 'YYYY-MM-DD';

export const valMoments = [moment().subtract(10, 'minute'), moment()] as [moment.Moment, moment.Moment];
