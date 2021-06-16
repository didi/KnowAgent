package com.didichuxing.datachannel.swan.agent.node.service.http.server;

import java.io.IOException;
import java.util.Map;

import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Version extends Handler implements HttpHandler {

    private static final ILog  LOGGER  = LogFactory.getLog(Version.class.getName());

    public final static String URI     = "/swan-agent/version";

    String                     version = null;

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
