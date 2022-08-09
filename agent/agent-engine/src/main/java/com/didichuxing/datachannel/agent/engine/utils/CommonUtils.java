package com.didichuxing.datachannel.agent.engine.utils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 通用工具类
 * @author: huangjw
 * @Date: 18/6/21 20:19
 */
public class CommonUtils {

    private static final Logger             LOGGER           = LoggerFactory
                                                                 .getLogger(CommonUtils.class
                                                                     .getName());

    private static String                   HOSTNAME;

    private static String                   HOSTNAMESUFFIX;

    private static String                   HOSTIP;

    private static Integer                  HOSTNAMEHASHCODE = 0;

    private static String                   DIDIDOMAIN       = ".diditaxi.com";

    private static String                   settingsFile     = "settings.properties";

    private static String                   DIDIENV_ODIN_SU;

    private static ScheduledExecutorService exec             = Executors.newScheduledThreadPool(1);

    static {
        getHostNameAndIP();
        getEnv();
        // 定时执行，以免主机名切换
        try {
            exec.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    getHostNameAndIP();
                }

            }, 60, 60, TimeUnit.SECONDS);
        } catch (Throwable e) {

        }
    }

    private static void getEnv() {
        try {
            Map<String, String> env = System.getenv();
            DIDIENV_ODIN_SU = env.get("DIDIENV_ODIN_SU");
        } catch (Exception e) {
            DIDIENV_ODIN_SU = null;
        }
    }

    /**
     * 获取当前系统
     *
     * @return
     */
    public static String getSystemType() {
        return System.getProperty("os.name").toLowerCase();
    }

    private static void getHostNameAndIP() {
        try {
            String hostname = getHostnameByexec();
            if (StringUtils.isNotBlank(hostname)) {
                HOSTNAME = hostname.trim();
            } else {
                HOSTNAME = InetAddress.getLocalHost().getHostName();
            }

            if (StringUtils.isNotBlank(HOSTNAME)) {
                if (HOSTNAME.contains(DIDIDOMAIN)) {
                    HOSTNAME = HOSTNAME.substring(0, HOSTNAME.length() - DIDIDOMAIN.length());
                }
            }
        } catch (UnknownHostException e) {
            HOSTNAME = "LocalHost";
        }

        if (HOSTNAME.contains(".")) {
            HOSTNAMESUFFIX = ".docker" + HOSTNAME.substring(HOSTNAME.lastIndexOf("."));
        } else {
            HOSTNAMESUFFIX = "";
        }

        try {
            if (HOSTIP == null || HOSTIP.isEmpty()) {
                HOSTIP = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException e) {
            HOSTIP = "127.0.0.1";
        }

        HOSTNAMEHASHCODE = HOSTNAME.hashCode();
        if (HOSTNAMEHASHCODE < 0) {
            HOSTNAMEHASHCODE = 0 - HOSTNAMEHASHCODE;
        }
    }

    private static String getHostnameByexec() {
        StringBuffer buf = new StringBuffer();
        try {
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("hostname");
            BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = br.readLine()) != null) {
                buf.append(s);
            }
            String hostname = buf.toString();
            if (StringUtils.isBlank(hostname) || hostname.contains("localhost")
                || hostname.indexOf("请求超时") != -1) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return buf.toString();
    }

    public static String getHOSTNAME() {
        return HOSTNAME;
    }

    public static String getHOSTNAMESUFFIX() {
        return HOSTNAMESUFFIX;
    }

    public static void setHOSTNAME(String hostname) {
        HOSTNAME = hostname;
    }

    public static String getHOSTIP() {
        return HOSTIP;
    }

    public static String getDidienvOdinSu() {
        return DIDIENV_ODIN_SU;
    }

    /**
     * 根据plainText获取其对应的32位md5编码
     *
     * @param plainText
     * @return
     */
    public static String getMd5(String plainText, String hostname) {
        try {
            if (StringUtils.isNotBlank(hostname)) {
                plainText += hostname + CommonUtils.getHOSTNAMESUFFIX();
            } else {
                plainText += CommonUtils.getHOSTNAME();
            }
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
            LOGGER.error("CommonUtil error!", "get md5 error.string is , {}" + plainText,
                e.getMessage());
        }
        return null;
    }

    /**
     * 把字符串类型的属性转换为Properties 例如：MaxSize=12,MaxthreadNum=10,
     * 例如：sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="44.appId_000058"
     * password="obLUytlmNuCP";
     *
     * @param properties
     * @return
     */
    public static Properties string2Property(String properties) {
        Properties props = new Properties();

        if (StringUtils.isNotEmpty(properties)) {
            String[] propertystrs = properties.split(",");
            for (String propertystr : propertystrs) {
                String key = propertystr.substring(0, propertystr.indexOf("="));
                String value = propertystr.substring(propertystr.indexOf("=") + 1);
                props.put(key.trim(), value.trim());
            }
        }
        return props;
    }

    /**
     * 读取本地配置
     *
     * @return
     * @throws IOException
     */
    public static Map<String, String> readSettings() throws IOException {
        InputStream is = CommonUtils.class.getClassLoader().getResourceAsStream(settingsFile);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            LOGGER
                .error("CommonUtil error!", "load " + settingsFile + " error, {}", e.getMessage());
        } finally {
            is.close();
        }
        return new HashMap<String, String>((Map) prop);
    }

    /**
     * 获取主机名的hash值
     *
     * @return
     */
    public static int getHostNameHashCode() {
        return HOSTNAMEHASHCODE;
    }

    /**
     * 多个topic选择发送
     *
     * @param originalTopics
     */

    // topic轮转值，兼容logx场景
    private static int topicRobinIndex = 0;

    /**
     * 多个topic选择发送
     *
     * @param originalTopics
     * @return
     */
    public static String selectTopic(String originalTopics) {
        if (StringUtils.isBlank(originalTopics)) {
            LOGGER.warn(String.format(
                "originalTopics [%s] is null, please check input param topic.", originalTopics));
        }
        return originalTopics;
    }

    /**
     * base64 解密
     *
     * @param encode
     * @return
     */
    public static String decode(String encode) {
        try {
            return new String(Base64.getDecoder().decode(encode.getBytes()));
        } catch (Exception e) {
            LOGGER.error("CommonUtil error!", "decode error. string is {}, {}", encode,
                e.getMessage());
        }
        return "";
    }

    /**
     * base64 加密
     *
     * @param source
     * @return
     */
    public static String encode(String source) {
        try {
            return new String(Base64.getEncoder().encode(source.getBytes()));
        } catch (Exception e) {
            LOGGER.error("CommonUtil error!", "encode error. string is {}, {}", source,
                e.getMessage());
        }
        return "";
    }

    /**
     * 根据文本内容返回其MD5值
     *
     * @param content
     * @return
     */
    public static String getMd5(String content) {
        try {
            // 加密对象，指定加密方式
            MessageDigest md5 = MessageDigest.getInstance("md5");
            // 准备要加密的数据
            byte[] b = content.getBytes();
            // 加密
            byte[] digest = md5.digest(b);
            // 十六进制的字符
            char[] chars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
                    'C', 'D', 'E', 'F' };
            StringBuffer sb = new StringBuffer();
            // 处理成十六进制的字符串(通常)
            for (byte bb : digest) {
                sb.append(chars[(bb >> 4) & 15]);
                sb.append(chars[bb & 15]);
            }
            // 返回加密结果
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("CommonUtil error!", "get md5 error.string is {}, {}", content,
                e.getMessage());
        }
        return null;
    }
}
