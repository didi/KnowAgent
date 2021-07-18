package com.didichuxing.datachannel.agentmanager.common.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author huqidong
 * @date 2020-09-21
 * 对象转换工具类
 */
public class ConvertUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertUtil.class);public static <T> T obj2ObjByJSON(Object srcObj, Class<T> tgtClass) {
        return JSON.parseObject(JSON.toJSONString(srcObj), tgtClass);
    }

    public static <T> T string2Obj(String srcObj, Class<T> tgtClass) {
        return JSON.parseObject(srcObj, tgtClass);
    }

    public static <T> List<T> string2ArrObj(String srcObj, Class<T> tgtClass) {
        return JSON.parseArray(srcObj, tgtClass);
    }

    public static String obj2String(Object srcObj) {
        if (srcObj == null) {
            return "";
        }

        return JSON.toJSONString(srcObj);
    }

    public static String list2String(List<?> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            sb.append(item).append(separator);
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public static <K, V> Map<K, V> list2Map(List<V> list, Function<? super V, ? extends K> mapper) {
        Map<K, V> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(list)) {
            for (V v : list) {
                map.put(mapper.apply(v), v);
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> list2MapParallel(List<V> list, Function<? super V, ? extends K> mapper) {
        Map<K, V> map = new ConcurrentHashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.parallelStream().forEach(v -> map.put(mapper.apply(v), v));
        }
        return map;
    }

    public static <K, V, O> Map<K, V> list2Map(List<O> list,
                                               Function<? super O, ? extends K> keyMapper,
                                               Function<? super O, ? extends V> valueMapper) {
        Map<K, V> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(list)) {
            for (O o : list) {
                map.put(keyMapper.apply(o), valueMapper.apply(o));
            }
        }
        return map;
    }

    public static <K, V> Multimap<K, V> list2MulMap(List<V> list,
                                                    Function<? super V, ? extends K> mapper) {
        Multimap<K, V> multimap = ArrayListMultimap.create();
        if (CollectionUtils.isNotEmpty(list)) {
            for (V v : list) {
                multimap.put(mapper.apply(v), v);
            }
        }
        return multimap;
    }

    public static <K, V, O> Multimap<K, V> list2MulMap(List<O> list,
                                                       Function<? super O, ? extends K> keyMapper,
                                                       Function<? super O, ? extends V> valueMapper) {
        Multimap<K, V> multimap = ArrayListMultimap.create();
        if (CollectionUtils.isNotEmpty(list)) {
            for (O o : list) {
                multimap.put(keyMapper.apply(o), valueMapper.apply(o));
            }
        }
        return multimap;
    }

    public static <K, V> Set<K> list2Set(List<V> list, Function<? super V, ? extends K> mapper) {
        Set<K> set = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(list)) {
            for (V v : list) {
                set.add(mapper.apply(v));
            }
        }
        return set;
    }

    public static <T> List<T> list2List(List list, Class<T> tClass) {
        return list2List(list, tClass, (t) -> {
        });
    }

    public static <T> List<T> list2List(List list, Class<T> tClass, Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }

        List<T> result = Lists.newArrayList();

        for (Object object : list) {
            T t = obj2Obj(object, tClass, consumer);
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * 对象转换工具
     * @param srcObj 元对象
     * @param tgtClass 目标对象类
     * @param <T> 泛型
     * @return 目标对象
     */
    @Nullable
    public static <T> T obj2Obj(final Object srcObj, Class<T> tgtClass, String... ignoreProperties) {
        return obj2Obj(srcObj, tgtClass, (t) -> {
        }, ignoreProperties);
    }

    @Nullable
    public static <T> T obj2Obj(final Object srcObj, Class<T> tgtClass, Consumer<T> consumer, String... ignoreProperties) {
        if (srcObj == null) {
            return null;
        }

        T tgt = null;
        try {
            Constructor<T> constructor = tgtClass.getConstructor();
            tgt = constructor.newInstance();
            BeanUtils.copyProperties(srcObj, tgt, ignoreProperties);
            consumer.accept(tgt);
        } catch (Exception e) {
            LOGGER.warn("convert obj2Obj error||msg={}", e.getMessage(), e);
        }

        return tgt;
    }

    public static <K, V> Map<K, V> mergeMapList(List<Map<K, V>> mapList) {
        Map<K, V> result = Maps.newHashMap();
        for (Map<K, V> map : mapList) {
            result.putAll(map);
        }
        return result;
    }

    /**
     * 将map转换为object，排除指定key
     * @param map
     * @param t
     * @param excludeKeys
     * @param <T>
     * @return
     */
    public static <T> T mapToObject(Map<String, String> map, T t, String[] excludeKeys) {
        Class beanClass = t.getClass();
        String[] declaredFieldsName = getDeclaredFieldsName(beanClass);
        if (ArrayUtils.isNotEmpty(excludeKeys)) {
            ConvertUtil.removeEntries(map, excludeKeys);
        }
        for (Object k : map.keySet()) {
            Object v = map.get(k);
            if (ArrayUtils.contains(declaredFieldsName, k.toString())) {
                try {
                    Field field = beanClass.getDeclaredField(k.toString());
                    field.setAccessible(true);
                    field.set(t, v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    public static String[] getDeclaredFieldsName(Class beanClass) {
        Field[] fields = beanClass.getDeclaredFields();
        int size = fields.length;
        String[] fieldsName = new String[size];
        for (int i = 0; i < size; i++) {
            fieldsName[i] = fields[i].getName();
        }
        return fieldsName;
    }

    /**
     * Map中根据key批量删除键值对
     * @param map
     * @param excludeKeys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map removeEntries(Map<K, V> map, K[] excludeKeys) {
        Iterator<K> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            K k = iterator.next();
            // 如果k刚好在要排除的key的范围中
            if (ArrayUtils.contains(excludeKeys, k)) {
                iterator.remove();
                map.remove(k);
            }
        }
        return map;
    }

    /**
     *
     * Map转String
     * @param map
     * @return
     */
    public static String mapToString(Map<String, String> map, String keyLabel, String valueLabel) {
        Set<String> keySet = map.keySet();
        //将set集合转换为数组
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        //给数组排序(升序)
        Arrays.sort(keyArray);
        //因为String拼接效率会很低的，所以转用StringBuilder
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyArray.length; i++) {
            // 参数值为空，则不参与签名 这个方法trim()是去空格
            if ((String.valueOf(map.get(keyArray[i]))).trim().length() > 0) {
                sb.append(keyLabel).append(keyArray[i]).append(keyLabel).append(":")
                        .append(valueLabel).append(String.valueOf(map.get(keyArray[i])).trim())
                        .append(valueLabel);
            }
            if (i != keyArray.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
