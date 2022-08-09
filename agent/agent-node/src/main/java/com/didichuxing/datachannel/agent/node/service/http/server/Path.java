package com.didichuxing.datachannel.agent.node.service.http.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path extends Handler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("log-agent");
    public final static String  URI    = "/log-agent/path";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream in = httpExchange.getRequestBody(); // 获得输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        // 将BufferedReader转化为字符串
        String content = IOUtils.toString(reader);
        String text = URLDecoder.decode(content, "utf-8");
        JSONObject json = JSON.parseObject(text);
        String path = json.getString("path");
        String suffix = json.getString("suffixRegular");

        List<String> validFileList = new ArrayList<>();
        if (StringUtils.isBlank(path)) {
            LOGGER.error("path is empty!!");
            writer(
                    JSON.toJSONString(
                            Result.build(
                                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                                    "路径参数不可为空",
                                    JSON.toJSONString(Collections.EMPTY_LIST)
                            )
                    ),
                    httpExchange
            );
            return;
        }
        File fileOrDir = new File(path);

        if (!fileOrDir.exists()) {
            LOGGER.error("file not found, path: {}", path);
            writer(
                    JSON.toJSONString(
                            Result.build(
                                    ErrorCodeEnum.FILE_NOT_EXISTS.getCode(),
                                    "路径不存在",
                                    JSON.toJSONString(Collections.EMPTY_LIST)
                            )
                    ),
                    httpExchange
            );
            return;
        }
        if (fileOrDir.isDirectory()) {
            File[] files = fileOrDir.listFiles();
            if (StringUtils.isBlank(suffix)) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    validFileList.add(file.getName());
                }
            } else {
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    Pattern pattern = Pattern.compile(suffix);
                    String name = file.getName();
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.find()) {
                        validFileList.add(name);
                    }
                }
            }
        } else {
            if (StringUtils.isBlank(suffix)) {
                validFileList.add(fileOrDir.getName());
            } else {
                File parent = fileOrDir.getParentFile();
                File[] files = parent.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    String mainFile = fileOrDir.getName();
                    String name = file.getName();
                    if (name.startsWith(mainFile)) {
                        String suffixName = name.substring(mainFile.length());
                        Pattern pattern = Pattern.compile(suffix);
                        Matcher matcher = pattern.matcher(suffixName);
                        if (matcher.find()) {
                            validFileList.add(name);
                        }
                    }
                }
            }
        }
        writer(
                JSON.toJSONString(
                        Result.buildSucc(
                                validFileList
                        )
                ),
                httpExchange
        );
    }
}
