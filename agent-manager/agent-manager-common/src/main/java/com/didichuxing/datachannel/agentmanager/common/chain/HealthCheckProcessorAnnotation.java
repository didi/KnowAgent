package com.didichuxing.datachannel.agentmanager.common.chain;

import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HealthCheckProcessorAnnotation {

    public int seq();

    public HealthCheckProcessorEnum type();

}
