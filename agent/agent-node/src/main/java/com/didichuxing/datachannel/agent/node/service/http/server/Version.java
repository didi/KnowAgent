package com.didichuxing.datachannel.agent.node.service.http.server;

import java.io.IOException;
import java.util.Map;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version extends Handler implements HttpHandler {

    private static final Logger LOGGER  = LoggerFactory.getLogger(Version.class.getName());
    public final static String  URI     = "/log-agent/version";

    String                      version = null;

    public Version() {
        Map<String, String> settings = null;
        try {
            settings = CommonUtils.readSettings();
            if (settings == null) {
                LogGather.recordErrorLog("LogEventUtils error", "get local settings error.");
            }
        } catch (Exception e) {

        }
        this.version = settings.get(LogConfigConstants.MESSSAGE_VERSION);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "{\"status\": \"ok\", \"version\": \"" + version + "\"}";
        writer(response, exchange);
    }
}
