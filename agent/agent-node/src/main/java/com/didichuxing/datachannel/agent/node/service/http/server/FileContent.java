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

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileContent extends Handler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("log-agent");
    public final static String  URI    = "/log-agent/file-content";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream in = httpExchange.getRequestBody(); // 获得输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        // 将BufferedReader转化为字符串
        String content = IOUtils.toString(reader);
        String text = URLDecoder.decode(content, "utf-8");
        JSONObject json = JSON.parseObject(text);
        String path = json.getString("path");
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
                                    "待加载文件内容的文件不存在",
                                    JSON.toJSONString(Collections.EMPTY_LIST)
                            )
                    ),
                    httpExchange
            );
            return;
        }
        if (fileOrDir.isDirectory()) {
            LOGGER.error("file is directory, path: {}", path);
            writer(
                    JSON.toJSONString(
                            Result.build(
                                    ErrorCodeEnum.FILE_IS_DIRECTORY.getCode(),
                                    "待加载文件内容的文件为目录",
                                    JSON.toJSONString(Collections.EMPTY_LIST)
                            )
                    ),
                    httpExchange
            );
            return;
        }
        /*
         * 读取文件内容，防止文件过大，最多读取100行文件内容
         */
        writer(
                JSON.toJSONString(
                        Result.buildSucc(
                                getFileContent(fileOrDir)
                        )
                ),
                httpExchange
        );
    }

    private String getFileContent(File file) {
        BufferedReader br = null;
        String fileContent = "";
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            Integer lines = 0;
            while ((line = br.readLine()) != null && lines < 100) {
                fileContent += line + System.getProperty("line.separator", "\n");
                lines++;
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("file not found, path: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("file read error, path: {}", file.getAbsolutePath());
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException ex) {
                    LOGGER.error("file close error, path: {}", file.getAbsolutePath(), ex);
                }
            }
        }
        return fileContent;
    }

}
