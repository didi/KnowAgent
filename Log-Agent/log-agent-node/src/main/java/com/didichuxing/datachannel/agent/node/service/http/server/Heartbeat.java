package com.didichuxing.datachannel.agent.node.service.http.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Heartbeat extends Handler implements HttpHandler {
    public final static String URI = "/heartbeat";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        writer("OK", exchange);
    }
}
