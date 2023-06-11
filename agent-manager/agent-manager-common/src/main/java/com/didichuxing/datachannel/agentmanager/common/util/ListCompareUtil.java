package com.didichuxing.datachannel.agentmanager.common.util;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author huqidong
 * @date 2020-09-21
 * 集合对比工具类
 */
public class ListCompareUtil {

    /**
     * @param sourceList 源集
     * @param targetList 目标集
     * @return 返回通过对比集合 sourceList & targetList，获取三个集合
     * 待删除集合：在 sourceList 中存在但在 targetList 中不存在的元素
     * 待创建集合：在 targetList 中存在但在 sourceList 中不存在的元素
     * 待更新集合：在 targetList & sourceList 同时存在但待对比属性集不相同的元素
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    public static <T, K> ListCompareResult<T> compare(List<T> sourceList, List<T> targetList, Comparator<T, K> comparator) throws ServiceException {
        try {
            ListCompareResult<T> listCompareResult = new ListCompareResult<T>();
            if (CollectionUtils.isEmpty(targetList)) {
                targetList = new ArrayList<>();
            }
            if (CollectionUtils.isEmpty(sourceList)) {
                sourceList = new ArrayList<>();
            }
            /*
             *
             * 对比逻辑：
             *  遍历 sourceList，针对每个 source，存在如下几种 cases：
             *  1.）source 在 targetList 存在：判断对象 source & 对应在 targetList 中对象是否存在变更？如存在变更，将对应在 targetList
             *      中对象存放至 "待更新集" ，如不存在变更，do nothing. 将 source 从集合 targetList 删除
             *  2.）source 在 targetList 不存在：将 source 存入 "待删除集"
             *  经上述两步骤，targetList 集中剩下元素集即为 "待新增集"
             *
             */
            //将targetList转map（key：comparator.getKey() value：target）
            Map<K, T> targetKey2TargetMap = new HashMap<>();
            for (T target : targetList) {
                targetKey2TargetMap.put(comparator.getKey(target), target);
            }
            for (T source : sourceList) {
                K sourceKey = comparator.getKey(source);
                T target = targetKey2TargetMap.get(sourceKey);
                if (null != target) {//source 在 targetList 存在
                    if (comparator.compare(source, target)) {//不存在变更
                        //do nothing
                    } else {//存在变更
                        listCompareResult.getModifyList().add(comparator.getModified(source, target));
                    }
                    targetKey2TargetMap.remove(sourceKey);
                } else {//source 在 targetList 不存在
                    listCompareResult.getRemoveList().add(source);
                }
            }
            listCompareResult.getCreateList().addAll(targetKey2TargetMap.values());
            return listCompareResult;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=ListCompareUtil||method=compare||errMsg={%s}",
                            String.format("对比给定入参sourceList={%s} & targetList={%s}过程中出现异常，原因为：%s", JSON.toJSONString(sourceList), JSON.toJSONString(targetList), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    /**
     * 返回两个集合（成员通常为基本类型）的差别
     *
     * @param src 前一个集合
     * @param dst 后一个集合
     * @param <T> 元素类型
     * @return 第一项表示前者比后者多的元素，第二项表示前者比后者少的元素
     */
    public static <T> Set<T>[] compare(Collection<T> src, Collection<T> dst) {
        Set<T> more;
        Set<T> less = new HashSet<>();
        Set<T> total = new HashSet<>(src);
        for (T t : dst) {
            boolean contains = total.add(t);
            if (contains) {
                less.add(t);
            }
        }
        total.removeAll(dst);
        more = total;
        return new Set[]{more, less};
    }

    /**
     * 返回两个集合的元素差，用set数组的形式表示
     *
     * @param src 前一个集合
     * @param dst 后一个集合
     * @param f   获取元素key的方法
     * @param p   比较相同key是否需要修改的方法
     * @param c   对c的修改操作
     * @param <R> key类型
     * @param <T> 元素类型
     * @return 数组第一项表示前者比后者多的元素，第二项表示前者和后者相同的元素，第三项表示key相同但其他内容互不相同的元素（元素来自于后者），第四项表示前者比后者少的元素
     */
    public static <R, T> Set<T>[] compareAndMerge(Collection<T> src, Collection<T> dst, Function<T, R> f, BiPredicate<T, T> p, BiConsumer<T, T> c) {
        Set<T> more;
        Set<T> same = new HashSet<>();
        Set<T> modify = new HashSet<>();
        Set<T> less = new HashSet<>();
        if (f == null) {
            throw new ServiceException("获取元素key的函数非法", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        if (p == null) {
            p = (x, y) -> true;
        }
        Map<R, T> total = new HashMap<>();
        for (T t : src) {
            total.put(f.apply(t), t);
        }
        for (T t : dst) {
            T previous = total.put(f.apply(t), t);
            if (previous == null) {
                less.add(t);
            } else {
                if (p.test(previous, t)) {
                    same.add(t);
                } else {
                    c.accept(previous, t);
                    modify.add(t);
                }
            }
        }
        more = new HashSet<>();
        for (T t : src) {
            T previous = total.remove(f.apply(t));
            if (previous != null) {
                more.add(t);
            }
        }
        return new Set[]{more, same, modify, less};
    }
}
