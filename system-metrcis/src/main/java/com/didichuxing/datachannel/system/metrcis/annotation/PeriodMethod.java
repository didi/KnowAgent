package com.didichuxing.datachannel.system.metrcis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PeriodMethod {

    /**
     * @return 周期函数对应执行周期（单位：毫秒）
     */
    int periodMs();

}
