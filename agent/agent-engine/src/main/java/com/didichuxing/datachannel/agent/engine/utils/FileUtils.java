package com.didichuxing.datachannel.agent.engine.utils;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.apache.commons.collections.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * 读取文件的第一行数据
     * @param path
     * @return
     * @throws Exception
     */
    public static String[] readFirstLine(String path) throws Exception {
        String line = null;
        List<String> contents = readFileContent(path, 1);
        if (CollectionUtils.isNotEmpty(contents)) {
            line = contents.get(0);
        }

        if (line == null || line.trim().length() == 0) {
            throw new Exception("line is empty");
        }

        line = line.trim();
        return line.split(" ");
    }

    /**
     * 读取文件给定前 maxNum 行数据
     * @param path   path
     * @param maxNum num of content to read
     * @return result of read
     */
    public static List<String> readFileContent(String path, int maxNum) {
        File file = new File(path);
        List<String> contents = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                contents.add(line);
                if (maxNum != -1) {
                    i++;
                    if (i >= maxNum) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("FileUtil error!", "readFileContent error file is " + file, e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                LogGather.recordErrorLog("FileUtil error!", "BufferedReader close failed, file is " + file, e);
            }
        }
        return contents;
    }
}
