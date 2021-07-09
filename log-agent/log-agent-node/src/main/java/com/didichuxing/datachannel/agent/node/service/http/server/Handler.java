package com.didichuxing.datachannel.agent.node.service.http.server;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public abstract class Handler {
    public void writer(String response, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
