package com.didichuxing.datachannel.agentmanager.common.util;

/**
 * @author huqidong
 * @date 2020-09-21
 * 对象比较器
 */
public interface Comparator<T, K> {

    /**
     * @param t
     * @return 返回对象 t 唯一标识，如两个 t 对象对应该标识相同，则表示两个对象可比较
     */
    K getKey(T t);

    /**
     * @param t1
     * @param t2
     * @return 对象 t1 & t2 待对比字段是否一致，如一致 return true，否则 return false
     */
    boolean compare(T t1, T t2);

    /**
     * 源对象 source & 目标对象 target 对应 getKey 值一样，须将 source 更新为 target
     * @param source 待更新源对象 source
     * @param target 待更新目标对象 target
     * @return 返回根据待更新源对象 source & 待更新目标对象 target 进行merge，得到最终的待更新对象
     */
    T getModified(T source, T target);

}
