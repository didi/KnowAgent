package com.didichuxing.datachannel.agent.engine.service;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 默认线程池
 * @author: huangjw
 * @Date: 19/7/3 14:25
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolId = new AtomicInteger();
    private final AtomicInteger        nextId;
    private final String               prefix;

    public DefaultThreadFactory(String poolName) {
        this.nextId = new AtomicInteger();
        if (poolName == null) {
            throw new NullPointerException("poolName");
        } else {
            this.prefix = poolName + '-' + poolId.incrementAndGet() + '-';
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, this.prefix + this.nextId.incrementAndGet());
        return t;
    }
}
