package com.didichuxing.datachannel.agent.node.service.http.server;

import org.junit.Test;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-30 10:43
 */
public class CommandTest {

    @Test
    public void exec() {
        Command command = new Command();
        String command1 = "ls -l";
        System.out.println("command1:" + command.exec(command1));

        String command2 = "cat /Users/user/SoftWare/apache-tomcat-7.0.73/bin/\\~/logs/manager.log | grep \"2018-12-21 11:56:47\" | wc -l";
        System.out.println("command2:" + command.exec(command2));

        String command4 = "cat /Users/user/SoftWare/apache-tomcat-7.0.73/bin/\\~/logs/manager.log | grep \"2018-12-21 11:56:47\" > /Users/user/SoftWare/apache-tomcat-7.0.73/bin/\\\n"
                          + "~/logs/result.log";
        System.out.println("command4:" + command.exec(command4));

        String command5 = "ls -l /Users/user/SoftWare/apache-tomcat-7.0.73/bin/\\~/logs/r* | grep -v 'result'";
        System.out.println("command5:" + command.exec(command5));
    }
}
