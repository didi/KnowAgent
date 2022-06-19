package com.didichuxing.datachannel.agent.sink.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @description: 字符串过滤类
 * @author: huangjw
 * @Date: 18/6/21 21:55
 */
public class StringFilter {

    /**
     * 判断字符串是否被过滤规则过滤 过滤规则写法：a&&b||c&&d
     *
     * @param filterRule
     * @param filterContent
     * @return
     */
    public static boolean doFilter(String filterRule, String filterContent) {
        if (!StringUtils.isNotBlank(filterRule)) {
            return true;
        }

        if (filterContent == null) {
            return false;
        }

        String[] orRules = StringUtils.splitByWholeSeparatorPreserveAllTokens(filterRule, "||");
        for (String orRule : orRules) {

            if (StringUtils.isNotBlank(orRule)) {
                String[] andRules = StringUtils
                    .splitByWholeSeparatorPreserveAllTokens(orRule, "&&");

                int flag = 0;
                for (String andRule : andRules) {
                    if (filterContent.contains(andRule)) {
                        flag++;
                    } else {
                        break;
                    }
                }
                if (flag == andRules.length) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断字符串是否被过滤规则过滤 过滤规则写法：a&&b||c&&d,filterOprType 表示包含filterRule 被过滤还是不被过滤
     *
     * @param filterRule
     * @param filterOprType 0表示包含 1表示不包含
     * @param filterContent
     * @return
     */
    public static boolean doFilter(String filterRule, int filterOprType, String filterContent) {
        boolean result = true;

        if (filterOprType == 0) {
            result = doFilter(filterRule, filterContent);
        } else {
            result = !doFilter(filterRule, filterContent);
        }

        return result;
    }
}
