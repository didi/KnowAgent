package com.didichuxing.datachannel.agentmanager.common.util;

import com.didichuxing.datachannel.agentmanager.common.enumeration.DataCenterEnum;

public class EnvUtil {

    private static EnvType        type = EnvType.TEST;

    private static DataCenterEnum dc   = DataCenterEnum.CN;

    public static boolean isOnline() {
        return type == EnvType.ONLINECN;
    }

    public static boolean isPre() {
        return type == EnvType.PRECN;
    }

    public static boolean isDev() {
        return type == EnvType.DEV;
    }

    public static boolean isTest() {
        return type == EnvType.TEST;
    }

    public static boolean isStable() {
        return type == EnvType.STABLE;
    }

    public static boolean isCN() {
        return dc == DataCenterEnum.CN;
    }

    public static boolean isUS01() {
        return dc == DataCenterEnum.US01;
    }

    public static DataCenterEnum getDC() {
        return dc;
    }

    public static EnvType getType(){return type;}

    public static String getStr() {
        return type.getStr() + "-" + dc.getCode();
    }

    /**
     * 设置加载的活跃的profile文件
     */
    public static void setLoadActiveProfiles(String[] activeProfiles) {
        if (activeProfiles == null || activeProfiles.length == 0) {
            return;
        }

        for (String profile : activeProfiles) {
            if (profile.contains("dev")) {
                type = EnvType.DEV;
            } else if (profile.contains("test")) {
                type = EnvType.TEST;
            } else if (profile.contains("stable")) {
                type = EnvType.STABLE;
            } else if (profile.contains("pre")) {
                type = EnvType.PRECN;
            } else if (profile.contains("online")) {
                type = EnvType.ONLINECN;
            }

            if (profile.contains("us01")) {
                dc = DataCenterEnum.US01;
            } else if (profile.contains("ru01")) {
                dc = DataCenterEnum.RU01;
            }
        }
    }

    public enum EnvType {
        DEV("dev"), TEST("test"), STABLE("stable"), PRECN("pre"), ONLINECN("online");

        private String str;

        private EnvType(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public static EnvType getByStr(String str) {
            for (EnvType type : EnvType.values()) {
                if (type.str.equalsIgnoreCase(str)) {
                    return type;
                }
            }

            return null;
        }
    }
}
