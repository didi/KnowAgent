package com.didichuxing.datachannel.agent.integration.test.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/2/12 18:18
 */
public class Md5Util {

    /**
     * 根据plainText获取其对应的32位md5编码
     *
     * @param plainText
     * @return
     */
    public static String getMd5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            LogGather
                .recordErrorLog("CommonUtil error!", "get md5 error.string is " + plainText, e);
        }
        return null;
    }
}
