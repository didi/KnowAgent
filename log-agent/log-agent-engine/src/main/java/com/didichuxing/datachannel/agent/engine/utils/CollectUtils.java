package com.didichuxing.datachannel.agent.engine.utils;

import java.util.HashSet;
import java.util.Set;

public class CollectUtils {

    public static Set<Long> getSame(Set<Long> o, Set<Long> n) {
        Set<Long> ret = new HashSet<>();

        for(Long k : o) {
            if(n.contains(k)) {
                ret.add(k);
            }
        }

        return ret;
    }

    public static Set<Long> getAdd(Set<Long> o, Set<Long> n) {
        Set<Long> ret = new HashSet<>();

        for(Long k : n) {
            if(!o.contains(k)) {
                ret.add(k);
            }
        }

        return ret;
    }

    public static Set<Long> getDel(Set<Long> o, Set<Long> n) {
        Set<Long> ret = new HashSet<>();

        for(Long k : o) {
            if(!n.contains(k)) {
                ret.add(k);
            }
        }

        return ret;
    }

    public static Set<String> getStrSame(Set<String> o, Set<String> n) {
        Set<String> ret = new HashSet<>();

        for(String k : o) {
            if(n.contains(k)) {
                ret.add(k);
            }
        }

        return ret;
    }

    public static Set<String> getStrAdd(Set<String> o, Set<String> n) {
        Set<String> ret = new HashSet<>();

        for(String k : n) {
            if(!o.contains(k)) {
                ret.add(k);
            }
        }

        return ret;
    }

    public static Set<String> getStrDel(Set<String> o, Set<String> n) {
        Set<String> ret = new HashSet<>();

        for(String k : o) {
            if(!n.contains(k)) {
                ret.add(k);
            }
        }

        return ret;
    }
}
