package com.didichuxing.datachannel.agent.channel.log;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ChannelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.source.log.LogSource;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-23 14:43
 */
public class LogChannelTest {

    @Test
    public void onChange() {
        LogChannel logChannel = getChannel();

        ChannelConfig newOne = new ChannelConfig();
        newOne.setMaxBytes(10000L);
        ModelConfig modelConfig = new ModelConfig("log2Kafka");
        modelConfig.setChannelConfig(newOne);

        logChannel.onChange(modelConfig);

        assertTrue(logChannel.getChannelConfig().toString().equals(newOne.toString()));
    }

    @Test
    public void tryAppend() {
        LogChannel logChannel = getChannel();
        LogEvent logEvent = new LogEvent();
        logChannel.tryAppend(logEvent);
        assertTrue(logChannel.getQueue().size() == 1);

        LogEvent logEvent1 = new LogEvent();
        logEvent1.setBytes(new byte[1000]);
        ChannelConfig newOne = new ChannelConfig();
        newOne.setMaxBytes(500L);
        ModelConfig modelConfig = new ModelConfig("log2Kafka");
        modelConfig.setChannelConfig(newOne);
        logChannel.onChange(modelConfig);
        logChannel.tryAppend(logEvent1);

        assertTrue(logChannel.getQueue().size() == 2);
        try {
            LogEvent event1 = logChannel.getQueue().take();
            LogEvent event2 = logChannel.getQueue().take();
            assertTrue(event1.length() + event2.length() <= 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tryGet() {
        LogChannel logChannel = getChannel();
        LogEvent logEvent = new LogEvent();
        logEvent.setFileNodeKey("fileNodeKey");
        logEvent.setOffset(100L);
        logEvent.setLogTime(System.currentTimeMillis());
        logChannel.tryAppend(logEvent);
        assertTrue(logChannel.getQueue().size() == 1);

        Event event1 = logChannel.tryGet(1000);
        Event event2 = logChannel.tryGet(1000);
        assertTrue(event1 != null && event2 == null);
        assertTrue(((LogEvent) event1).toString().equals(logEvent.toString()));
    }

    private LogChannel getChannel() {
        LogPath logPath = new LogPath(1L, 1L, "/home/test/test.log");
        LogSource logSource = new LogSource(new ModelConfig("log2Kafka"), logPath);
        LogChannel logChannel = new LogChannel(logSource, new ChannelConfig());
        return logChannel;
    }
}
