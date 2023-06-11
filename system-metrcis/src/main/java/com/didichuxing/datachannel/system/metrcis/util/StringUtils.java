package com.didichuxing.datachannel.system.metrcis.util;

import java.util.List;

public class StringUtils {

    public static Boolean contains(List<String> stringList, String target) {
        for (String str : stringList) {
            return str.contains(target);
        }
        return false;
    }

}
