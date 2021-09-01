package com.didichuxing.datachannel.agentmanager.common.util;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

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
     *         待删除集合：在 sourceList 中存在但在 targetList 中不存在的元素
     *         待创建集合：在 targetList 中存在但在 sourceList 中不存在的元素
     *         待更新集合：在 targetList & sourceList 同时存在但待对比属性集不相同的元素
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    public static <T, K> ListCompareResult<T> compare(List<T> sourceList, List<T> targetList, Comparator<T, K> comparator) throws ServiceException {
        try {
            ListCompareResult<T> listCompareResult = new ListCompareResult<T>();
            if(CollectionUtils.isEmpty(targetList)) {
                targetList = new ArrayList<>();
            }
            if(CollectionUtils.isEmpty(sourceList)) {
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
                if(null != target) {//source 在 targetList 存在
                    if(comparator.compare(source, target)) {//不存在变更
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
     * 校验两个集合元素是否完全相同（相同判断条件为：集合元素对应 equals & hashcode）
     * @param sourceList 源集
     * @param targetList 目标集
     * @param <T> 集合元素类型
     * @return true：相等 false：不相等
     */
    public static <T> boolean listEquals(List<T> sourceList, List<T> targetList) {
        if(null == sourceList && null == targetList) {
            return true;
        }
        if(null == sourceList) {
            return false;
        }
        if(null == targetList) {
            return false;
        }
        if(targetList.size() != sourceList.size()) {
            return false;
        }
        Set<T> sourceSet = new HashSet<>();
        Set<T> targetSet = new HashSet<>();
        sourceSet.addAll(sourceList);
        targetSet.addAll(targetList);
        if(targetSet.size() != sourceSet.size()) {
            return false;
        }
        for (T source : sourceList) {
            if(!targetSet.contains(source)) {
                return false;
            }
        }
        return true;
    }

}
