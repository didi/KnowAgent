package com.didichuxing.datachannel.agent.source.log.type;

import java.io.File;

import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;

/**
 * @description: 日志匹配公用类
 * @author: huangjw
 * @Date: 18/8/9 14:47
 */
public abstract class AbstractLogType {

    Integer type;

    public AbstractLogType(Integer type) {
        this.type = type;
    }

    public abstract boolean check(File file, MatchConfig matchConfig);
}
