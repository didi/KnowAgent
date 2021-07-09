package com.didiglobal.logi.auvjob.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {
  //LocalDate -> Date
  public static Date toDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  //LocalDateTime -> Date
  public static Date toDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  //long -> Date
  public static Date toDate(Long time) {
    return new Date(time);
  }

  //Date -> LocalDate
  public static LocalDate toLocalDate(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }

  //Date -> LocalDateTime
  public static LocalDateTime toLocalDateTime(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }


  //Date -> LocalDateTime
  public static Long toLong(Date date) {
    return date.getTime();
  }
}