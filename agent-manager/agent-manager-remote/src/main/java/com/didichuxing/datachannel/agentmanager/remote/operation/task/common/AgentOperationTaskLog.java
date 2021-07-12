package com.didichuxing.datachannel.agentmanager.remote.operation.task.common;

public class AgentOperationTaskLog {
    private String stdout;

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    @Override
    public String toString() {
        return "AgentOperationTaskLog{" +
                "stdout='" + stdout + '\'' +
                '}';
    }
}
