//package com.didi;
//
//import com.google.common.util.concurrent.RateLimiter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Properties;
//
//public class LogTest {
//
//    private static double threshold = 64;
//    private static int logSize = 256;
//    private static RateLimiter limiter;
//
//    static {
//        Properties properties = new Properties();
//        try {
//            FileInputStream fis = new FileInputStream(new File("./rate.properties"));
//            properties.load(fis);
//            threshold = Double.parseDouble((String) properties.getOrDefault("rate", 1024));
//            logSize = Integer.parseInt((String) properties.getOrDefault("size", 1024));
//        } catch (IOException e) {
////            e.printStackTrace();
//        }
//        limiter = RateLimiter.create(threshold);
//    }
//
//    private static final Logger defaultLogger = LoggerFactory.getLogger("default");
//    private static final Logger logger1 = LoggerFactory.getLogger("logger1");
//    private static final Logger logger2 = LoggerFactory.getLogger("logger2");
//    private static final Logger logger3 = LoggerFactory.getLogger("logger3");
//
//    public static void main(String[] args) {
//        int i = 0;
//        while (true) {
//            String number = String.format("%11d", i);
//            int size = logSize - 74 - 2;
//            String content = String.format("%" + size + "d", i % 10).intern();
//            limiter.acquire();
////            defaultLogger.info("{} {}{}", number, System.currentTimeMillis(), content);
//            logger1.debug("{} {}{}", number, System.currentTimeMillis(), content);
//            logger2.info("{} {}{}", number, System.currentTimeMillis(), content);
//            logger3.error("{} {}{}", number, System.currentTimeMillis(), content);
//            i++;
//        }
//    }
//
//    private static void printStack() {
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        for (StackTraceElement stackTraceElement : stackTraceElements) {
//            logger3.error("{}", stackTraceElement);
//        }
//    }
//
//}
