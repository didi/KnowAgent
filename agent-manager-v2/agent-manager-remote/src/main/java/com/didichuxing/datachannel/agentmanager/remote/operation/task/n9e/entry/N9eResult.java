package com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry;

/**
 * @author zengqiao
 * @date 20/9/3
 */
public class N9eResult {
    private String err;

    private Object dat;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public Object getDat() {
        return dat;
    }

    public void setDat(Object dat) {
        this.dat = dat;
    }

    @Override
    public String toString() {
        return "N9eResult{" +
                "err='" + err + '\'' +
                ", dat=" + dat +
                '}';
    }
}
