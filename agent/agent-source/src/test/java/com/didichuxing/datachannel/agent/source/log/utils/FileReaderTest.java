package com.didichuxing.datachannel.agent.source.log.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.api.FileReadType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-23 16:33
 */
public class FileReaderTest {

    private static final String filePath1      = "/tmp/new-log-agent/file1";
    private static final String filePath2      = "/tmp/new-log-agent/file2";
    private static final String filePath3      = "/tmp/new-log-agent/file3";
    private static final String filePath3_1    = "/tmp/new-log-agent/file3_1";
    private static final String filePath4      = "/tmp/new-log-agent/file4";
    private static final String filePath5      = "/tmp/new-log-agent/file5";
    private static final String filePath5_back = "/tmp/new-log-agent/file5_back";
    private static final String filePath6_1    = "/tmp/new-log-agent/file6_1";
    private static final String filePath6_2    = "/tmp/new-log-agent/file6_2";
    private static final String filePath7      = "/tmp/new-log-agent/file7";

    private static final int    MAX_LINE       = 100;
    private static final int    MOD            = 7;

    @Before
    public void before() {
        initFile(filePath1);
        initFile(filePath2);
        initFile(filePath3);
        initFileForTimeStamp(filePath3_1);
        initBusFile(filePath4);
        initFile(filePath5);
        initFileWithFileName(filePath6_1);
        initFileWithFileNameForSmall(filePath6_2);

        initFileForOneLine(filePath7);
    }

    @After
    public void after() {
        deleteFile(filePath1);
        deleteFile(filePath2);
        deleteFile(filePath3);
        deleteFile(filePath4);
        deleteFile(filePath5);
        deleteFile(filePath5_back);
        deleteFile(filePath6_1);
        deleteFile(filePath6_2);

        deleteFile(filePath7);
    }

    @Test
    public void openFileForFileNameChange() {
        File originalFile = new File(filePath5);

        LogSource logSource = buildNoLogTimeLogSourceFilePath5();
        WorkingFileNode workingFileNode = getWorkingFileNode(filePath5, logSource);
        try {
            assertTrue(workingFileNode.open(0, 3));
        } catch (Exception e) {

        } finally {
            workingFileNode.close();
        }

        originalFile.renameTo(new File(filePath5_back));
        workingFileNode.getFileNode().setFileName("file5_back");
        try {
            assertTrue(workingFileNode.open(0, 3));
            workingFileNode.close();
        } catch (Exception e) {

        } finally {
            workingFileNode.close();
        }
    }

    @Test
    public void readByNoTime() {
        LogSource logSource = buildNoLogTimeLogSource();
        FileReader reader = new FileReader(logSource);

        Set<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < MAX_LINE; i++) {
            resultSet.add(i);
        }

        WorkingFileNode workingFileNode = getWorkingFileNode(filePath1, logSource);
        workingFileNode.open(0, 3);
        int i = 0;
        while (true) {
            LogEvent event = reader.readEvent(workingFileNode);
            if (event != null) {
                if (event.getContent().equals(new String(event.getBytes()))) {
                    resultSet.remove(getLineNum(event.getContent()));
                }
            } else {
                break;
            }
        }

        workingFileNode.close();
        assertTrue(resultSet.size() == 0);
    }

    @Test
    public void readByNoTime2() {
        LogSource logSource = buildNoLogTimeLogSource();
        FileReader reader = new FileReader(logSource);

        Set<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < MAX_LINE; i++) {
            resultSet.add(i);
        }

        WorkingFileNode workingFileNode = getWorkingFileNode(filePath1, logSource);
        workingFileNode.open(0, 3);
        int i = 0;
        while (true) {
            LogEvent event = reader.readEvent(workingFileNode);
            if (event != null) {
                if (event.getContent().equals(new String(event.getBytes()))) {
                    resultSet.remove(getLineNum(event.getContent()));
                }
            } else {
                break;
            }
        }

        workingFileNode.close();
        assertTrue(resultSet.size() == 0);
    }

    @Test
    public void changeTimeFormat() {
        LogSource logSource = buildNoLogTimeLogSource();
        FileReader reader = new FileReader(logSource);

        Set<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < MAX_LINE; i++) {
            resultSet.add(i);
        }

        WorkingFileNode workingFileNode = getWorkingFileNode(filePath1, logSource);
        workingFileNode.open(0, 3);
        int i = 0;
        int changeNum = 10;
        while (true) {
            LogEvent event = reader.readEvent(workingFileNode);
            if (event != null) {
                if (event.getContent().equals(new String(event.getBytes()))) {
                    resultSet.remove(getLineNum(event.getContent()));
                }
                if (i == changeNum) {
                    logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);
                    logSource.getLogSourceConfig().setTimeStartFlag("timestamp=");
                }
            } else {
                break;
            }
            i++;
        }

        workingFileNode.close();
        assertTrue(resultSet.size() != 0);
    }

    @Test
    public void deleteFileWhenRead() {
        LogSource logSource = buildSingleLogSource();
        FileReader reader = new FileReader(logSource);

        Set<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < MAX_LINE; i++) {
            resultSet.add(i);
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(20 * 1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                deleteFile(filePath2);
            }
        });
        thread.start();

        WorkingFileNode workingFileNode = getWorkingFileNode(filePath2, logSource);
        workingFileNode.open(0, 3);
        int i = 0;
        while (true) {
            try {
                LogEvent event = reader.readEvent(workingFileNode);
                if (event != null) {
                    if (event.getContent().equals(new String(event.getBytes()))) {
                        resultSet.remove(getLineNum(event.getContent()));
                    }
                } else {
                    break;
                }
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        workingFileNode.close();
        assertTrue(resultSet.size() == 0);
    }

    @Test
    public void readBySingeLine() {
        LogSource logSource = buildSingleLogSource();
        FileReader reader = new FileReader(logSource);

        Set<Integer> resultSet = new HashSet<>();
        for (int i = 0; i < MAX_LINE; i++) {
            resultSet.add(i);
        }

        WorkingFileNode workingFileNode = getWorkingFileNode(filePath2, logSource);
        workingFileNode.open(0, 3);
        int i = 0;
        while (true) {
            LogEvent event = reader.readEvent(workingFileNode);
            if (event != null) {
                if (event.getContent().equals(new String(event.getBytes()))) {
                    resultSet.remove(getLineNum(event.getContent()));
                }
            } else {
                break;
            }
        }

        workingFileNode.close();
        assertTrue(resultSet.size() == 0);
    }

    @Test
    public void readByMutilLine() {
        LogSource logSource = buildMutilLogSource();
        FileReader reader = new FileReader(logSource);

        Map<Integer, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < MAX_LINE; i++) {
            if (i % MOD == 0) {
                resultMap.put(i, 3);
            }
        }

        WorkingFileNode wfn1 = getWorkingFileNode(filePath1, logSource);
        WorkingFileNode wfn2 = getWorkingFileNode(filePath2, logSource);
        WorkingFileNode wfn3 = getWorkingFileNode(filePath3, logSource);

        logSource.setMasterFileName(FileUtils.getMasterFile(filePath1));
        wfn1.open(0, 3);

        logSource.setMasterFileName(FileUtils.getMasterFile(filePath2));
        wfn2.open(0, 3);

        logSource.setMasterFileName(FileUtils.getMasterFile(filePath3));
        wfn3.open(0, 3);
        List<WorkingFileNode> fileNodes = new ArrayList<>();
        fileNodes.add(wfn1);
        fileNodes.add(wfn2);
        fileNodes.add(wfn3);
        for (WorkingFileNode wfn : fileNodes) {
            while (true) {
                LogEvent event = reader.readEvent(wfn);
                if (event != null) {
                    if (event.getContent().equals(new String(event.getBytes()))) {
                        int num = getLineNum(event.getContent());
                        resultMap.put(num, resultMap.get(num) - 1);
                        if (resultMap.get(num) == 0) {
                            resultMap.remove(num);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        wfn1.close();
        wfn2.close();
        wfn3.close();
        assertTrue(resultMap.size() == 0);
    }

    @Test
    public void readByMutilLine2() throws Exception {
        LogSource logSource = buildMutilLogSource2();
        logSource.getLogSourceConfig().setMaxErrorLineNum(2);

        //反射为字段设置值
        FileReader reader = new FileReader(logSource);
        Field field = reader.getClass().getDeclaredField("timeStamp");
        field.setAccessible(true);
        field.set(reader,10086L);

        Field field2 = reader.getClass().getDeclaredField("timeString");
        field2.setAccessible(true);
        field2.set(reader,"2020-10-26 09:00:00");

        Map<Integer, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < MAX_LINE; i++) {
            if (i % MOD == 0) {
                resultMap.put(i, 3);
            }
        }

        WorkingFileNode wfn4 = getWorkingFileNode(filePath4, logSource);
        //通过设置不同的offset位置，使得任务从什么位置开始采集
        wfn4.getFileNode().getFileOffSet().setOffSet(0L);
        wfn4.open(0, 3);
        List<WorkingFileNode> fileNodes = new ArrayList<>();
        fileNodes.add(wfn4);
        for (WorkingFileNode wfn : fileNodes) {
            while (true) {
                LogEvent event = reader.readEvent(wfn);
                if (event != null) {
                    System.out.println(event.getContent());
                    System.out.println(event.toString());
                } else {
                    break;
                }
            }
        }

        wfn4.close();
    }

    @Test
    public void readByMutilLine3() {
        LogSource logSource = buildMutilLogSource();
        FileReader reader = new FileReader(logSource);

        WorkingFileNode wfn3 = getWorkingFileNode(filePath3_1, logSource);
        logSource.setCurWFileNode(wfn3);
        wfn3.open(0, 3);
        List<WorkingFileNode> fileNodes = new ArrayList<>();
        fileNodes.add(wfn3);
        EventParser eventParser = new EventParser();
        for (WorkingFileNode wfn : fileNodes) {
            while (true) {
                LogEvent event = reader.readEvent(wfn);
                if (event != null) {
                    eventParser.parse(logSource, wfn3, event);
                } else {
                    break;
                }
            }
        }
        wfn3.close();
    }

    @Test
    public void readByMutilLineWhenCurWFNChanged() {
        LogSource logSource = buildMutilLogSource();
        FileReader reader = new FileReader(logSource);

        WorkingFileNode wfn5 = getWorkingFileNode(filePath6_1, logSource);
        String wfn5FileName = FileUtils.getMasterFile(filePath6_1);
        wfn5.open(0, 3);

        WorkingFileNode wfn6 = getWorkingFileNode(filePath6_2, logSource);
        String wfn6FileName = FileUtils.getMasterFile(filePath6_2);
        wfn6.open(0, 3);

        int i = 0;
        while (true) {
            if (i > MAX_LINE * 2) {
                break;
            }
            LogEvent event1 = reader.readEvent(wfn5);
            LogEvent event2 = reader.readEvent(wfn6);
            if (event1 == null && event2 == null) {
                break;
            } else {
                if (event1 != null) {
                    wfn5.setCurOffset(event1.getOffset());
                }

                if (event2 != null) {
                    wfn6.setCurOffset(event2.getOffset());
                }
            }
            if (event1.getContent().contains(wfn5FileName)
                && event2.getContent().contains(wfn6FileName)) {
                i++;
            } else {
                i = MAX_LINE * 3;
                break;
            }
        }

        wfn5.close();
        wfn6.close();

        assertTrue(i <= MAX_LINE * 2);
    }

    @Ignore
    @Test
    public void readByMutilLineWhenCurWFNCleared() {
        LogSource logSource = buildMutilLogSource();
        FileReader reader = new FileReader(logSource);

        WorkingFileNode wfn7 = getWorkingFileNode(filePath7, logSource);
        String wfn7FileName = FileUtils.getMasterFile(filePath7);
        wfn7.open(0, 3);

        LogEvent event1 = reader.readEvent(wfn7);
        if (event1 != null) {
            wfn7.setCurOffset(event1.getOffset());
        }

        for (int i = 0; i < 10000; i++) {
            LogEvent event2 = reader.readEvent(wfn7);
            if (event2 != null) {
                wfn7.setCurOffset(event2.getOffset());
            }
        }
    }

    WorkingFileNode getWorkingFileNode(String filePath, LogSource logSource) {
        File file = new File(filePath);
        FileNode fileNode = new FileNode(0L, 0L, FileUtils.getFileKeyByAttrs(file),
            file.lastModified(), file.getParent(), file.getName(), file.length(), file);
        FileOffSet fileOffSet = new FileOffSet(0L, 0L, filePath, fileNode.getFileKey());
        fileNode.setFileOffSet(fileOffSet);

        WorkingFileNode workingFileNode = new WorkingFileNode(fileNode, logSource);

        return workingFileNode;
    }

    LogSource buildNoLogTimeLogSource() {
        LogSource logSource = getLogSource(filePath1, FileReadType.MultiRow.getType());
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        return logSource;
    }

    LogSource buildNoLogTimeLogSourceFilePath5() {
        LogSource logSource = getLogSource(filePath5, FileReadType.MultiRow.getType());
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.NO_LOG_TIME);
        return logSource;
    }

    LogSource buildSingleLogSource() {
        LogSource logSource = getLogSource(filePath2, FileReadType.SingleRow.getType());
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);
        logSource.getLogSourceConfig().setTimeStartFlag("timestamp=");
        return logSource;
    }

    LogSource buildMutilLogSource() {
        LogSource logSource = getLogSource(filePath3, FileReadType.MultiRow.getType());
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);
        logSource.getLogSourceConfig().setTimeStartFlag("time=");
        return logSource;
    }

    LogSource buildMutilLogSource(String filePath) {
        LogSource logSource = getLogSource(filePath, FileReadType.MultiRow.getType());
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);
        logSource.getLogSourceConfig().setTimeStartFlag("time=");
        return logSource;
    }

    LogSource buildMutilLogSource3() {
        LogSource logSource = getLogSource(filePath3_1, FileReadType.MultiRow.getType());
        logSource.getLogSourceConfig().setTimeFormat(LogConfigConstants.LONG_TIMESTAMP);
        logSource.getLogSourceConfig().setTimeStartFlag("time=");
        return logSource;
    }

    LogSource buildMutilLogSource2() {
        LogSource logSource = getLogSource(filePath4, FileReadType.MultiRow.getType());
        logSource.getLogSourceConfig().setTimeFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        logSource.getLogSourceConfig().setTimeStartFlag("][");
        return logSource;
    }

    LogSource getLogSource(String filepath, int collectType) {
        LogPath logPath = new LogPath(filepath);
        LogSourceConfig logSourceConfig = new LogSourceConfig();
        List<LogPath> logPaths = new ArrayList<>();
        logPaths.add(logPath);
        logSourceConfig.setLogPaths(logPaths);
        logSourceConfig.setReadFileType(collectType);

        ModelConfig modelConfig = new ModelConfig("log");
        modelConfig.setSourceConfig(logSourceConfig);
        CommonConfig commonConfig = new CommonConfig();
        modelConfig.setCommonConfig(commonConfig);

        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileFilterType(0);
        matchConfig.setFileSuffix(".2017010101");
        matchConfig.setFileType(0);
        matchConfig.setMatchType(0);
        logSourceConfig.setMatchConfig(matchConfig);

        LogSource logSource = new LogSource(modelConfig, logPath);
        return logSource;
    }

    private void initFile(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < MAX_LINE; i++) {
                if (i % MOD == 0) {
                    lines.add("time=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine);
                } else {
                    lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine);
                }
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFileForOneLine(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            lines.add("time=" + System.currentTimeMillis() + ",line=" + 1 + ",bigLine");
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + 0 + "," + bigLine);

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFileForTimeStamp(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            lines.add("time=" + System.currentTimeMillis() + ",line=" + 1 + ",bigLine");
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < MAX_LINE; i++) {
                lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFileWithFileName(String path) {
        File file = new File(path);
        createFiles(path);
        String fileName = FileUtils.getMasterFile(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline12345678901234567890";
            for (int i = 0; i < MAX_LINE; i++) {
                lines.add("fileName is " + fileName + ", time=" + System.currentTimeMillis() + ",line=" + i + ","
                          + bigLine);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFileWithFileNameForSmall(String path) {
        File file = new File(path);
        createFiles(path);
        String fileName = FileUtils.getMasterFile(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < MAX_LINE; i++) {
                lines.add("fileName is " + fileName + ", time=" + System.currentTimeMillis() + ",line=" + i + ","
                          + bigLine);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBusFile(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String line1 = "[INFO[2019-07-25T00:01:43.724][CouponModel.cpp:81] _undef||traceid=6bd3ea5136084913500e804110520303||spanid=28707e9545272837||[D]_msg=fc coupon info||SmartCouponExp {";
            String line2 = "  01: UserID (i64) = 369435969289669,";
            String line3 = "  02: CityID (i64) = 52140500,";
            String line4 = "  03: CouponBatchID (i64) = 55152043,";
            String line5 = "  04: StartTime (i64) = 1563944400,";
            String line6 = "  05: EndTime (i64) = 1564030799,";
            String line7 = "  06: ExpID (string) = \"activity_level_exp_treshold_8\",";
            String line8 = "  07: ActivityID (i64) = 5619631,";
            String line9 = "  08: ProductID (i64) = 247,";
            String line10 = " }";
            //String line11 = "[INFO]2019-07-25T00:01:43.724][CouponServiceHandler.cpp:51] _com_request_out||traceid=6bd3ea5136084913500e804110520303||spanid=28707e9545272837||[D]uri=GetCoupon||proc_time=0.002||errno=0||errmsg=OK||response=CouponParams(userId=369435969289669, cityId=52140500, origCouponList=[CouponInfo(CouponBatchID=55145803, ActivityID=5615183, ProductID=247)])CouponRsp(header=ResponseHeader(errCode=0, errMsg=OK, trace={cspanid: 28707e9545272837, hintCode: 0, hintContent: {\"app_timeout_ms\":15000,\"lng\":-103.36171422536209,\"lang\":\"es-MX\",\"lat\":20.649672067570993,\"utc_offset\":-300}, locale: es-MX, requestId: , runid: f_5d3880e7acb4b, spanid: 9507d80c061b7255, timeout: 80000000, timezone: America/Mexico_City, traceid: 6bd3ea5136084913500e804110520303}), retCouponList=[CouponInfo(CouponBatchID=55152043, ActivityID=5619631, ProductID=247)])";
            String line11 = "[INFO[2020-10-13T18:21:56.724][CouponServiceHandler.cpp:51] _com_request_out||traceid=6bd3ea5136084913500e804110520303||spanid=28707e9545272837||[D]uri=GetCoupon||proc_time=0.002||errno=0||errmsg=OK||response=CouponParams(userId=369435969289669, cityId=52140500, origCouponList=[CouponInfo(CouponBatchID=55145803, ActivityID=5615183, ProductID=247)])CouponRsp(header=ResponseHeader(errCode=0, errMsg=OK, trace={cspanid: 28707e9545272837, hintCode: 0, hintContent: {\"app_timeout_ms\":15000,\"lng\":-103.36171422536209,\"lang\":\"es-MX\",\"lat\":20.649672067570993,\"utc_offset\":-300}, locale: es-MX, requestId: , runid: f_5d3880e7acb4b, spanid: 9507d80c061b7255, timeout: 80000000, timezone: America/Mexico_City, traceid: 6bd3ea5136084913500e804110520303}), retCouponList=[CouponInfo(CouponBatchID=55152043, ActivityID=5619631, ProductID=247)])";
            lines.add(line1);
            lines.add(line2);
            lines.add(line3);
            lines.add(line4);
            lines.add(line5);
            lines.add(line6);
            lines.add(line7);
            lines.add(line8);
            lines.add(line9);
            lines.add(line10);
            lines.add(line11);
            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getLineNum(String line) {
        if (StringUtils.isNotBlank(line)) {
            line = line.substring(line.indexOf("line=") + 5);
            line = line.substring(0, line.indexOf(","));
            return Integer.parseInt(line);
        }
        return -1;
    }

    private void createFiles(String path) {
        File file = new File(path);
        File dir = new File(path.substring(0, path.lastIndexOf(File.separator) + 1));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {

            }
        }
    }

    @Test
    public void EstimateLogFilterTest() throws Exception {
        String content1 = "g_order_estimate_info_shadow||timestamp=2020-12-15 00:00:00||trace_id=0aaa51bc5fd78bfec5a8001f753f7099||pspan_id=554ec511087787b1||span_id=554ec511087787b1||opera_stat_key=g_order_estimate_info_shadow||pLang=zh-CN||estimate_id=bae128be253c0ab87d2a747fa6ea4556||area=10006||product_id=207||require_level=4970||origin_combo_type=0||combo_type=0||is_default=||select_type=0||recommend_type=0||sub_group_id=0||has_operation=0";
        String content2 = "g_order_estimate_info||timestamp=2020-12-15 00:00:00||trace_id=0aaa51bc5fd78bfec5a8001f753f7099||pspan_id=554ec511087787b1||span_id=554ec511087787b1||opera_stat_key=g_order_estimate_info_shadow||pLang=zh-CN||estimate_id=bae128be253c0ab87d2a747fa6ea4556||area=10006||product_id=207||require_level=4970||origin_combo_type=0||combo_type=0||is_default=||select_type=0||recommend_type=0||sub_group_id=0||has_operation=0";
        String content3 = "g_order_estimate_info_shadow";
        LogSource logSource = buildMutilLogSource2();
        logSource.getLogSourceConfig().setMaxErrorLineNum(2);

        // 反射执行私有方法
        FileReader reader = new FileReader(logSource);
        Method method = reader.getClass().getDeclaredMethod("isNeedFilter", String.class);
        method.setAccessible(true);
        boolean result1 = (boolean) method.invoke(reader, content1);
        boolean result2 = (boolean) method.invoke(reader, content2);
        boolean result3 = (boolean) method.invoke(reader, content3);

        assertTrue(!result1);//true
        assertTrue(!result2);//false
        assertTrue(!result3);//false
    }
}
