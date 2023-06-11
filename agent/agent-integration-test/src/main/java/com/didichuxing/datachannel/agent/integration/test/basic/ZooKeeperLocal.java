package com.didichuxing.datachannel.agent.integration.test.basic;

/**
 * @description: 本地zk
 * @author: huangjw
 * @Date: 19/2/12 16:20
 */

import java.io.IOException;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A local Zookeeper server for running unit tests. Reference: https://gist.github.com/fjavieralba/7930018/
 */
public class ZooKeeperLocal {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperLocal.class.getName());
    private ZooKeeperServerMain zooKeeperServer;

    public ZooKeeperLocal(Properties zkProperties) throws IOException {
        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
        try {
            quorumConfiguration.parseProperties(zkProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        zooKeeperServer = new ZooKeeperServerMain();
        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);

        new Thread() {

            @Override
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                    LOGGER.error("Zookeeper startup failed.", e);
                }
            }
        }.start();
    }
}
