package com.didichuxing.datachannel.agent.source.log.utils;

public class Test {
    public static void main(String[] args) {

        byte[] value = new byte[4096];

        byte[] content = "fadasdasdasdasdasdasdasdasdasdasdasdasd\n".getBytes();
        for (int i = 0; i < content.length; i++) {
            value[i] = content[i];
        }

        byte[] delimiter = FileUtils.LF_LINE_DELIMITER;
//        FileUtils.CR_LINE_DELIMITER;
        int length = -1;

        int result = FileUtils.getLineDelimiterIndex(value, delimiter, length);

        System.err.println(result);

    }
}
