/* eslint-disable react/prop-types */
import React, { useState, useEffect } from 'react';
import { Radio, DatePicker } from '@didi/dcloud-design';
import { IconFont } from '@didi/dcloud-design';
const RangePicker = DatePicker.RangePicker as any;
import moment, { Moment } from 'moment';

import './style/time-module.less';

interface propsType extends React.HTMLAttributes<HTMLDivElement> {
  timeChange: (timer, bool) => void;
  rangeTimeArr?: number[];
}

const TimeOptions = [
  {
    label: '近15分钟',
    value: 15 * 60 * 1000,
  },
  {
    label: '近1小时',
    value: 60 * 60 * 1000,
  },
  {
    label: '近1天',
    value: 24 * 60 * 60 * 1000,
  },
];

const TimeModule: React.FC<propsType> = ({ timeChange, rangeTimeArr }) => {
  const [time, setTime] = useState<number>(60 * 60 * 1000);
  const [rangeTime, setrangeTime] = useState<[Moment, Moment]>([moment(new Date().getTime() - time), moment(new Date().getTime())]);
  const [isRelative, setIsRelative] = useState(true);

  useEffect(() => {
    if (rangeTimeArr?.length > 0) {
      setrangeTime([moment(rangeTimeArr[0]), moment(rangeTimeArr[1])]);
      const rangeTimeLen = rangeTimeArr[1] - rangeTimeArr[0];
      setTime(Math.floor(rangeTimeLen / 1000) * 1000);
    }
  }, [rangeTimeArr]);

  const periodtimeChange = (e) => {
    const time = e.target.value;
    setTime(time);
    setrangeTime([moment(new Date().getTime() - time), moment(new Date().getTime())]);
    timeChange([new Date().getTime() - time, new Date().getTime()], true);
    setIsRelative(true);
  };
  const rangeTimeChange = (dates, dateStrings) => {
    timeChange([moment(dateStrings[0]).valueOf(), moment(dateStrings[1]).valueOf()], false); // 毫秒数
    setTime(null);
    setIsRelative(false);
  };
  return (
    <>
      <div className="dd-time-module">
        {/* <span>时间：</span> */}
        <Radio.Group
          optionType="button"
          buttonStyle="solid"
          className="time-radio-group"
          options={TimeOptions}
          onChange={periodtimeChange}
          value={time}
        />
        <RangePicker
          showTime
          separator="~"
          suffixIcon={<IconFont type="icon-riqi" style={{ color: '#74788D' }}></IconFont>}
          value={rangeTime}
          onChange={rangeTimeChange}
        />
      </div>
    </>
  );
};

export default TimeModule;
