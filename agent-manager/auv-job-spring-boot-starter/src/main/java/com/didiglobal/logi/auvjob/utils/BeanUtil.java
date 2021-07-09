package com.didiglobal.logi.auvjob.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {
  private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 对象转换为目标类对象.
   *
   * @param source 源
   * @param targetClass 目标类
   * @param <T> 目标对象
   * @return 转换到的对象
   */
  public static <T> T convertTo(Object source, Class<T> targetClass) {
    T instance = null;
    try {
      instance = targetClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      logger.error("class=BeanUtil||method=convertTo||url=||msg={}", e);
    }
    copyProperties(source, instance);
    return instance;
  }

  /**
   * 对象转换为目标类对象.
   *
   * @param source 源
   * @param targetClass 目标类
   * @param <T> 目标对象
   * @return 转换到的对象
   */
  public static <T> List<T> convertToList(String source, Class<T> targetClass) {
    try {
      JavaType javaType = objectMapper.getTypeFactory()
              .constructCollectionType(List.class, targetClass);
      return objectMapper.readValue(source, javaType);
    } catch (Exception e) {
      logger.error("", e);
      return null;
    }
  }

  /**
   * 对象转json 字符串.
   *
   * @param source source
   * @return str
   */
  public static String convertToJson(Object source) {
    try {
      return source == null ? null : objectMapper.writeValueAsString(source);
    } catch (JsonProcessingException e) {
      logger.error("source to json error, e->", e);
      return null;
    }
  }

  /**
   * copy属性.
   *
   * @param source 源
   * @param target 目标
   */
  private static void copyProperties(Object source, Object target) {
    Assert.notNull(source, "Source must not be null");
    Assert.notNull(target, "Target must not be null");

    Class<?> sourceClass = source.getClass();
    Field[] sourceFields = sourceClass.getDeclaredFields();
    Map<String, Object> sourceFieldMap = new HashMap<>();
    try {
      for (Field sourceField : sourceFields) {
        sourceField.setAccessible(true);
        sourceFieldMap.put(sourceField.getName(), sourceField.get(source));
      }
    } catch (IllegalAccessException e) {
      logger.error("class=BeanUtil||method=copyProperties||url=||msg={}", e);
    }

    Field[] targetFields = target.getClass().getDeclaredFields();
    try {
      for (Field targetField : targetFields) {
        String targetFieldName = targetField.getName();
        if (sourceFieldMap.containsKey(targetFieldName)) {
          targetField.setAccessible(true);
          targetField.set(target, sourceFieldMap.get(targetFieldName));
        }
      }
    } catch (Exception e) {
      logger.error("class=BeanUtil||method=copyProperties||url=||msg={}", e);
    }
  }
}