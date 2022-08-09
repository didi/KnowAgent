package com.didichuxing.datachannel.agentmanager.persistence;

/**
 * 错误日志数据访问接口抽象工厂类
 * @author william.
 */
public interface ErrorLogsDAOFactory {

    /**
     * @return 返回创建的 Agent 错误日志数据访问对象实例
     */
    AgentErrorLogDAO createAgentErrorLogDAO();

}
