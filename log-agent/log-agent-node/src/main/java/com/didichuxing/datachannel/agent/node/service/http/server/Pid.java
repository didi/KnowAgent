package com.didichuxing.datachannel.agent.node.service.http.server;

import java.io.IOException;

import com.didichuxing.datachannel.agent.engine.utils.ProcessUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Pid extends Handler implements HttpHandler {

    public final static String URI = "/log-agent/pid";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        writer(ProcessUtils.getInstance().getPid(), exchange);
    }
}
