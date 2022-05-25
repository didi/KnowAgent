package com.didichuxing.datachannel.agentmanager.common.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.TypeUtils;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 根据给定时间戳，将其转化为分钟为单位时间戳
     * @param timestamp 毫秒时间戳
     * @return 返回根据给定时间戳，将其转化为分钟为单位时间戳
     */
    public static Long getMinuteUnitTimeStamp(Long timestamp) {
        return getMinuteUnitTimeStamp(new Date(timestamp));
    }

    /**
     * 根据给定日期/时间，将其转化为分钟为单位时间戳
     * @param time 待转化日期/时间
     * @return 返回根据给定日期/时间，将其转化为分钟为单位时间戳
     */
    public static Long getMinuteUnitTimeStamp(Date time) {
        if (time == null) {
            return null;
        }
        time = org.apache.commons.lang3.time.DateUtils.setSeconds(time, 0);
        time = org.apache.commons.lang3.time.DateUtils.setMilliseconds(time, 0);
        return time.getTime();
    }

    /**
     * 根据给定时间戳，将其转化为分钟为单位时间戳
     * @param time 毫秒时间戳
     * @return 返回根据给定时间戳，将其转化为分钟为单位时间戳
     */
    public static Long getHourUnitTimeStamp(Date time) {

        if (time == null) {
            return null;
        }

        time = org.apache.commons.lang3.time.DateUtils.setMinutes(time, 0);
        time = org.apache.commons.lang3.time.DateUtils.setSeconds(time, 0);
        time = org.apache.commons.lang3.time.DateUtils.setMilliseconds(time, 0);
        return time.getTime();
    }

    /**
     * 根据给定时间戳，将其转化为分钟为单位时间戳
     * @param time 毫秒时间戳
     * @return 返回根据给定时间戳，将其转化为分钟为单位时间戳
     */
    public static Long getHourUnitTimeStamp(Long timestamp) {
        Date time = new Date(timestamp);
        return getHourUnitTimeStamp(time);
    }

    public static Long date2Long(Date time, String formatStr) {

        if (time == null) {
            return null;
        }

        String format;
        if (formatStr == null) {
            format = "yyyyMMddHHmmss";
        } else {
            format = formatStr;
        }

        String timeStr = new SimpleDateFormat(format).format(time);

        return Long.valueOf(timeStr);
    }

    public static Date getYesterday(Date time) {
        return getBeforeDays(time, 1);
    }

    public static Date getYesterday() {
        return getBeforeDays(new Date(), 1);
    }

    public static Date getTodayBegin() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getBeforeDays(Date time, int before) {

        if (time == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.DATE, c.get(Calendar.DATE) - before);

        return c.getTime();
    }

    public static Date getBeforeSeconds(Date time, int before) {

        if (time == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.SECOND, c.get(Calendar.SECOND) - before);

        return c.getTime();
    }

    public static Date getBeforeMonths(Date time, int before) {
        if (time == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) - before);

        return c.getTime();
    }

    /**
     * 获取当前月的最后一天的
     * @param time 时间
     * @return
     */
    public static Date getLastDayOfTheMonth(Date time) {
        if (time == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        c.set(Calendar.DATE, 0);

        return c.getTime();
    }

    public static Date getAfterDays(Date time, int after) {

        if (time == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + after);

        return c.getTime();
    }

    public static int getWeekInfo(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayInfo(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthInfo(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.MONTH);
    }

    public static int getHourInfo(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getYearInfo(Date time) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.YEAR);
    }

    public static Integer date2int(Date time) {

        if (time == null) {
            return null;
        }

        String timeStr = new SimpleDateFormat("yyyyMMddHH").format(time);

        return Integer.valueOf(timeStr);
    }

    public static Integer date2int(Date time, String format) {

        if (time == null) {
            return null;
        }

        if (StringUtils.isBlank(format)) {
            format = "yyyyMMddHH";
        }

        String timeStr = new SimpleDateFormat(format).format(time);

        return Integer.valueOf(timeStr);
    }

    public static Date int2date(int time, String format) {

        if (format == null) {
            return null;
        }

        String timeStr = String.valueOf(time);
        Date result = null;
        try {
            result = new SimpleDateFormat(format).parse(timeStr);
        } catch (ParseException e) {
            return null;
        }

        return result;
    }

    public static String date2Str(Date date, String newFormatStr) {

        if (date == null) {
            return null;
        } else {
            String newformat;
            if (StringUtils.isBlank(newFormatStr)) {
                newformat = "yyyy-MM-dd HH:mm:ss";
            } else {
                newformat = newFormatStr;
            }

            SimpleDateFormat format = new SimpleDateFormat(newformat);
            return format.format(date);
        }
    }

    public static String getDateTimeStr(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date(time));
    }

    public static Date str2Date(String str, String newFormatStr) {
        if (str == null) {
            return null;
        } else {
            String newformat;
            if (StringUtils.isBlank(newFormatStr)) {
                newformat = "yyyy-MM-dd HH:mm:ss";
            } else {
                newformat = newFormatStr;
            }

            SimpleDateFormat format = new SimpleDateFormat(newformat);
            try {
                return format.parse(str);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public static Date getZeroDate() {
        return getZeroDate(new Date());
    }

    public static Date getZeroDate(Date time) {
        if (time == null) {
            return null;
        }

        time = org.apache.commons.lang3.time.DateUtils.setHours(time, 0);
        time = org.apache.commons.lang3.time.DateUtils.setMinutes(time, 0);
        time = org.apache.commons.lang3.time.DateUtils.setSeconds(time, 0);
        time = org.apache.commons.lang3.time.DateUtils.setMilliseconds(time, 0);
        return time;
    }

    public static int getBeforeDayCount(Date before) {
        Date todayZero = getZeroDate();
        long timestamp = todayZero.getTime() - before.getTime();
        if (timestamp < 0) {
            return 0;
        }

        Double result = Math.ceil(timestamp * 1.0 / 1000 / 60 / 60 / 24);
        return result.intValue();
    }

    public static boolean isLastOfMonth(Date date) {

        Date tomorrow = DateUtils.getAfterDays(date, 1);
        int tomorrowMonth = getMonthInfo(tomorrow);
        int todayMonth = getMonthInfo(date);

        return todayMonth != tomorrowMonth;
    }

    public static int getThisMonthRemainDay() {
        Date today = new Date();
        int todayMonth = getMonthInfo(today);
        int i = 1;
        while (getMonthInfo(getAfterDays(today, i)) == todayMonth) {
            i++;
        }
        return i;
    }

    public static Long castToTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof Date) {
            return ((Date) value).getTime();
        }
        if (value instanceof ZonedDateTime) {
            return ((ZonedDateTime) value).toInstant().toEpochMilli();
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        }
        if (value instanceof String) {
            try {
                return TypeUtils.castToDate(value).getTime();
            } catch (JSONException e) {
                throw new ServiceException("无法解析字符串：" + value, ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
            }
        }
        throw new ServiceException("不支持的时间类型：" + value.getClass().getCanonicalName(), ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
    }

    public static Long getDayUnitTimeStamp(Date current) {
        return getZeroDate(current).getTime();
    }

    public static Long getDayUnitTimeStamp(Long time) {
        return getDayUnitTimeStamp(new Date(time));
    }

}
