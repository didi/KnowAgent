package com.didichuxing.datachannel.agent.node.service.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 执行本地脚本
 * @author: huangjw
 * @Date: 18/7/23 10:44
 */
public class Command extends Handler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class.getName());
    public final static String  URI    = "/log-agent/command";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream in = httpExchange.getRequestBody(); // 获得输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        // 将BufferedReader转化为字符串
        String content = IOUtils.toString(reader);
        String text = URLDecoder.decode(content, "utf-8");
        if (text.contains(LogConfigConstants.COMMAND_TAG)) {
            String command = text.substring(text.indexOf("=") + 1);
            writer(exec(command), httpExchange);
        }
        return;
    }

    /**
     * 脚本执行器
     *
     * @param command
     * @return
     */
    public String exec(String command) {
        try {
            LOGGER.info("begin to exec command.command is " + command);
            Process ps = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            return result;
        } catch (Exception e) {
            LOGGER.warn("command[ " + command + " ] run error!", e);
        }
        return "command[ " + command + " ] run error!";
    }
}
