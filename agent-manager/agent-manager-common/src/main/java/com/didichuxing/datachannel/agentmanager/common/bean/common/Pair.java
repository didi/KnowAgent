package com.didichuxing.datachannel.agentmanager.common.bean.common;

/**
 * @author huqidong
 * @date 2020-09-21
 * 表示一个 <k, v> 对
 */
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Pair() {

    }

    public K getKey() {
        return key;
    }
    public V getValue() {
        return value;
    }
    public void setKey(K key) {
        this.key = key;
    }
    public void setValue(V value) {
        this.value = value;
    }

}
