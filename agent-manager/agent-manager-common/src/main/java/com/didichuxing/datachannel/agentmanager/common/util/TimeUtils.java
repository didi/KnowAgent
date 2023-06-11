package com.didichuxing.datachannel.agentmanager.common.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zhangliang
 * @version $Id: TimeUtil.java, v 0.1 Jun 20, 2014 9:09:40 PM zhangliang Exp $
 */
public class TimeUtils {

    public static final String                              TIME_YYYY_MM_DD_HH_MM    = "yyyy-MM-dd HH:mm";
    public static final String                              TIME_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String                              TIME_YYYYMMDDHHMMSS      = "yyyyMMddHHmmss";

    public static final String  LONG_TIMESTAMP                     = "LongType-Time";

    public static ConcurrentHashMap<String, FastDateFormat> dateFormatMap            = new ConcurrentHashMap<String, FastDateFormat>();

    public static String getTimeFormatString(Date date, String format) {
        FastDateFormat f = getFastDateFormat(format);
        return f.format(date);
    }

    public static String getCurrentTime(String format) {
        String returnStr = null;
        FastDateFormat f = getFastDateFormat(format);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }

    public static int getCurrentHour() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMinute() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取具体分钟数
     * @param timestamp
     * @return
     */
    public static int getSpecificMinute(Long timestamp) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(timestamp));
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 从礼拜天开始=1，礼拜六=7
     * @return
     */
    public static int getCurrentWeekDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getStaticPointInPeriod(int period) {
        return ((getCurrentWeekDay() - 1) * 24 * 60 + (getCurrentHour()) * 60 + getCurrentMinute())
               % period;
    }

    /**
     * 获取当前时间多少分钟之前的数据
     *
     * @param timeSpan
     *            时间跨度
     * @return
     */
    public static String getTimeSpanBefore(int timeSpan) {
        return getTimeSpanBefore(timeSpan, TIME_YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当前时间多少分钟之前的数据
     *
     * @param timeSpan
     *            时间跨度
     * @return
     */
    public static String getTimeSpanBefore(int timeSpan, String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis() / (60000L) * (60000L)));
        calendar.add(Calendar.MINUTE, 0 - timeSpan);

        Date date = calendar.getTime();
        if (format.equals(LONG_TIMESTAMP)) {
            return String.valueOf(date.getTime());
        }
        FastDateFormat f = getFastDateFormat(format);
        return f.format(date);
    }

    /**
     * 获取当前时间多少分钟之前的数据，返回long类型字符串
     * @param timeSpan
     *            时间跨度
     * @return
     */
    public static long getLongTimeSpanBefore(int timeSpan) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis() / (60000L) * (60000L)));
        calendar.add(Calendar.MINUTE, 0 - timeSpan);

        Date date = calendar.getTime();
        return date.getTime();
    }

    public static boolean isTimeFormat(String timeStr, String timeFormat) {

        if (timeStr == null || timeStr.isEmpty()) {
            return false;
        }
        if (timeFormat.equals(LONG_TIMESTAMP)) {
            try {
                Long.parseLong(timeStr);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        boolean result = true;
        try {
            FastDateFormat dFormat = getFastDateFormat(timeFormat);
            dFormat.parse(timeStr);
        } catch (Throwable e) {
            result = false;
        }

        return result;
    }

    public static Long getLongTimeStamp(String timeStr, String timeFormat) {
        if (timeStr == null || timeStr.equals("") || timeFormat == null || timeFormat.equals("")) {
            return null;
        }
        if (timeFormat.equals(LONG_TIMESTAMP)) {
            Long result = null;
            try {
                result = Long.parseLong(timeStr);
            } catch (Exception e) {
                return null;
            }
            return result;
        }
        FastDateFormat f = getFastDateFormat(timeFormat);
        Date date = null;
        try {
            date = f.parse(timeStr);
        } catch (Throwable e) {
            return null;
        }

        return date.getTime();
    }

    /**
     * 把毫秒数转换成最近的一分钟，向前一分钟取值
     *
     * @param timeStampMili
     * @return
     */
    public static long getTimeStampMinute(long timeStampMili) {
        return timeStampMili / 60000 * 60000;
    }

    /**
     * 将时间戳转换为分钟的字符串形式
     *
     * @param timeStampMili
     * @return
     */
    public static String getStringMinute(long timeStampMili) {
        FastDateFormat formatter = getFastDateFormat(TIME_YYYY_MM_DD_HH_MM);
        String timeMinuteString;
        try {
            timeMinuteString = formatter.format(new Date(timeStampMili));
        } catch (Exception e) {
            timeMinuteString = null;
        }
        return timeMinuteString;
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss 类型的时间字符串
     *
     * @param timeStamp
     * @return
     */
    public static String getStandardTimeString(long timeStamp) {
        FastDateFormat formatter = getFastDateFormat(TIME_YYYY_MM_DD_HH_MM_SS);
        String timeMinuteString;
        try {
            timeMinuteString = formatter.format(new Date(timeStamp));
        } catch (Exception e) {
            timeMinuteString = null;
        }
        return timeMinuteString;
    }

    /**
     * 获取timeFormat类型的时间字符串
     *
     * @param timeStamp
     * @return
     */
    public static String getTimeString(long timeStamp, String timeFormat) {
        FastDateFormat formatter = getFastDateFormat(timeFormat);
        String timeMinuteString;
        try {
            timeMinuteString = formatter.format(new Date(timeStamp));
        } catch (Exception e) {
            timeMinuteString = null;
        }
        return timeMinuteString;
    }

    public static FastDateFormat getFastDateFormat(String format) {
        if (dateFormatMap.containsKey(format)) {
            return dateFormatMap.get(format);
        } else {
            synchronized (dateFormatMap) {
                if (!dateFormatMap.containsKey(format)) {
                    dateFormatMap.put(format, FastDateFormat.getInstance(format));
                }
                return dateFormatMap.get(format);
            }
        }
    }

    /**
     * 获取当前微妙级时间
     * @return 微妙级时间
     */
    public static Long getNanoTime() {
        // Long cutime = System.currentTimeMillis() * 1000; // 微秒
        // Long nanoTime = System.nanoTime(); // 纳秒
        // return cutime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
        return System.nanoTime();
    }
}
