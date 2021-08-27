package com.didichuxing.datachannel.agent.source.log.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agent.common.api.StandardLogType;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.api.FileMatchType;
import com.didichuxing.datachannel.agent.common.api.FileType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;

/**
 * @description:
 * @author: huangjw
 * @Date: 18/7/12 17:17
 */
public class FileUtilsTest {

    @Test
    public void match() throws Exception {
        String masterFile = "/tmp/tmp/tmp/tmp/test.log";

        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setMatchType(FileMatchType.Length.getStatus());
        matchConfig.setFileType(FileType.File.getStatus());
        matchConfig.setFileSuffix(".1");

        String path0 = "/tmp/tmp/tmp/tmp/test.log.0";
        createFiles(path0);
        assertTrue(FileUtils.match(new File(path0), masterFile, matchConfig));
        removeFile(path0);

        String path1 = "/tmp/tmp/tmp/tmp/test.log.10";
        createFiles(path1);
        assertTrue(!FileUtils.match(new File(path1), masterFile, matchConfig));
        removeFile(path1);

        String path2 = "/tmp/tmp/tmp/tmp/test.log.11";
        createFiles(path2);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.File.getStatus());
        matchConfig.setFileSuffix(".[0-9]{1}");
        assertTrue(!FileUtils.match(new File(path2), masterFile, matchConfig));
        removeFile(path2);

        String path3 = "/tmp/tmp/tmp/tmp/test.log.12";
        createFiles(path3);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.File.getStatus());
        matchConfig.setFileSuffix(".[0-9]{2}");
        assertTrue(FileUtils.match(new File(path3), masterFile, matchConfig));
        removeFile(path3);

        String path4 = "/tmp/tmp/tmp/tmp/";
        createFiles(path4);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> fileRule = new ArrayList<>();
        fileRule.add("/tmp/tmp/tmp/tmp/test.log.[0-9]{1}");
        fileRule.add("/tmp/tmp/tmp/tmp/test.log.[0-9]{1}1");
        matchConfig.setFileFilterRules(fileRule);
        removeFile(path4);

        File dir = new File(path4);
        File[] files = dir.listFiles();
        boolean isOk = true;
        for (File file : files) {
            if (!file.isDirectory()) {
                if (FileUtils.match(file, path4, matchConfig)) {
                    if (!file.getAbsolutePath().endsWith("1")
                        && file.getAbsolutePath().indexOf(".") == file.getAbsolutePath().length() - 1) {
                        isOk = false;
                    }
                }
            }
        }
        assertTrue(isOk);

        String path5 = "/tmp/tmp/tmp/tmp/";
        String path5_base = "/tmp/tmp/tmp/";
        createFiles(path5);
        createFiles(path5_base);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> fileRule5 = new ArrayList<>();
        fileRule5.add("/tmp/tmp/tmp/tmp/.*");
        matchConfig.setFileFilterRules(fileRule5);
        assertTrue(FileUtils.match(new File(path5), path5_base, matchConfig));
        removeFile(path5);
        removeFile(path5_base);

        String path6 = "/tmp/tmp/tmp/tmp/test.log";
        String path6_base = "/tmp/tmp/tmp/";
        createFiles(path6);
        createFiles(path6_base);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> fileRule6 = new ArrayList<>();
        fileRule6.add("/tmp/tmp/tmp/tmp/.*");
        matchConfig.setFileFilterRules(fileRule6);
        assertTrue(FileUtils.match(new File(path6), path6_base, matchConfig));
        removeFile(path6);
        removeFile(path6_base);

        String path7 = "/tmp/tmp/tmp/tmp/test.log";
        String path7_base = "/tmp/tmp/tmp/";
        createFiles(path7);
        createFiles(path7_base);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> fileRule7 = new ArrayList<>();
        fileRule7.add("/tmp/tmp/tmp/tmp1/.*");
        matchConfig.setFileFilterRules(fileRule7);
        assertTrue(!FileUtils.match(new File(path7), path7_base, matchConfig));
        removeFile(path7);
        removeFile(path7_base);

        String path8 = "/tmp/tmp/tmp/tmp/test.log.20180101";
        String path8_base = "/tmp/tmp/tmp/tmp/test.log";
        createFiles(path8);
        createFiles(path8_base);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> fileRule8 = new ArrayList<>();
        fileRule8.add("/tmp/tmp/tmp/tmp/.*");
        matchConfig.setFileFilterRules(fileRule8);
        assertTrue(FileUtils.match(new File(path8), path8_base, matchConfig));
        removeFile(path8);
        removeFile(path8_base);

        String path9 = "/tmp/tmp/tmp/tmp/test.log.wf.20180101";
        String path9_base = "/tmp/tmp/tmp/tmp/test.log";
        createFiles(path9);
        createFiles(path9_base);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);
        List<String> fileRule9 = new ArrayList<>();
        fileRule9.add("/tmp/tmp/tmp/tmp/.*");
        matchConfig.setFileFilterRules(fileRule9);
        assertTrue(!FileUtils.match(new File(path9), path9_base, matchConfig));
        removeFile(path9);
        removeFile(path9_base);

        String path10 = "/tmp/tmp/tmp/tmp/test.log.2018092719";
        String master10 = "/tmp/tmp/tmp/tmp/test.log";
        createFiles(path10);
        MatchConfig matchConfig10 = new MatchConfig();
        matchConfig10.setMatchType(0);
        matchConfig10.setFileFilterRules(new ArrayList<String>());
        matchConfig10.setFileFilterType(0);
        matchConfig10.setBusinessType(0);
        matchConfig10.setFileType(0);
        matchConfig10.setFileSuffix(".2018010101");

        assertTrue(FileUtils.match(new File(path10), master10, matchConfig10));
        removeFile(path10);

        String path11 = "/tmp/tmp/tmp/tmp/test.log.11";
        String path12 = "/tmp/tmp/tmp/tmp/test.log.0011";
        createFiles(path11);
        createFiles(path12);
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.File.getStatus());
        matchConfig.setFileSuffix(".\\d+");
        assertTrue(FileUtils.match(new File(path11), masterFile, matchConfig));
        assertTrue(FileUtils.match(new File(path12), masterFile, matchConfig));
        removeFile(path11);
        removeFile(path12);
    }

    @Before
    public void init() {
        String filePath = "/tmp/tmp/tmp/tmp/test.log";
        for (int i = 0; i <= 100; i++) {
            if (i % 2 == 0) {
                createFiles("/tmp" + filePath + "." + i);
            } else {
                createFiles(filePath + "." + i);
            }
        }
    }

    // @After
    // public void end() {
    // String filePath = "/tmp/tmp/tmp/tmp/test.log";
    // for (int i = 0; i <= 100; i++) {
    // if (i % 2 == 0) {
    // removeFile("/tmp" + filePath + "." + i);
    // } else {
    // removeFile(filePath + "." + i);
    // }
    // }
    // }

    @Test
    public void ergodic() throws Exception {
        MatchConfig matchConfig = new MatchConfig();
        String path = "/tmp";
        Set<String> masterFiles = FileUtils.getAllDirsUnderDir(path, path, null);
        for (String tmpPath : masterFiles) {
            System.out.println(tmpPath);
        }
    }

    @Test
    public void getInode() throws Exception {
        String filePath = "/tmp/test.log";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        assertTrue(FileUtils.getInode(file.getPath()) != LogConfigConstants.DEFAULT_INODE);
    }

    @Test
    public void getCreateTime() throws Exception {
        String filePath = "/tmp/test.log";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        System.out.println(FileUtils.getCreateTime(file));

        Thread.sleep(3000L);
        List<String> contents = new ArrayList<>();
        contents.add("test");
        FileUtils.writeFileContent(file, contents);
        System.out.println(FileUtils.getCreateTime(file));

        assertTrue(FileUtils.getCreateTime(file) != LogConfigConstants.DEFAULT_CREATE_TIME);
    }

    @Test
    public void getModifyTime() throws Exception {
        String filePath = "/tmp/test.log";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        assertTrue(FileUtils.getModifyTime(file) != LogConfigConstants.DEFAULT_MODIFY_TIME);
    }

    @Test
    public void getFileKey() throws Exception {
        String filePath = "/tmp/test.log";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        assertTrue(StringUtils.isNotBlank(FileUtils.getFileKeyByAttrs(file)));
    }

    @Test
    public void test() {
        String path = "/tmp/tmp/test.txt.01";
        File file = new File(path);
        System.out.println(file.getAbsolutePath());
    }

    public static void removeFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void createFiles(String path) {
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

    @Test
    public void getInodeFromFileKey() {
        String path = "/tmp/tmp/tmp/tmp/test.log.1";
        File file = new File(path);
        createFiles(path);

        Long logModelId = 999L;
        Long logPathId = 111L;
        FileNode fileNode = new FileNode(logModelId, logPathId, FileUtils.getFileKeyByAttrs(file),
            FileUtils.getModifyTime(file), file.getParent(), file.getName(), file.length(), file);
        assertTrue((logModelId + LogConfigConstants.UNDERLINE_SEPARATOR + logPathId
                    + LogConfigConstants.UNDERLINE_SEPARATOR + FileUtils.getFileKeyByAttrs(file))
            .equals(fileNode.getNodeKey()));
        removeFile(path);
    }

    @Test
    public void getFileKeyByAttrs() {
        String path = "/tmp/test.log";
        createFiles(path);
        System.out.println(FileUtils.getFileKeyByAttrs(path));
        System.out.println(FileUtils.getInode(path));
        System.out.println(FileUtils.getDev(new File(path)));
        removeFile(path);
    }

    @Test
    public void checkPublicTest() throws Exception {
        String publicPath1 = "/tmp/standard/public.log.1";
        String publicPath2 = "/tmp/standard/public.log.2";
        String publicPath3 = "/tmp/standard/public.log.3";

        createFiles(publicPath1);
        createFiles(publicPath2);
        createFiles(publicPath3);

        // 测试public日志
        String publicContent1 = "1111 taxi_p_cancel_order||timestamp=2015-11-19 17:00:54||orderid=1414281843||pid=124111424||did=3192541||appversion=||cancel_reason_type=30";
        String publicContent2 = "taxi_p_cancel_order taxi||timestamp=2015-11-19 17:00:54.123||orderid=1414281843||pid=124111424||did=3192541||appversion=1||cancel_reason_type=30";
        String publicContent3 = "test";

        List<String> publicList1 = new ArrayList<>();
        publicList1.add(publicContent1);
        publicList1.add(publicContent2);
        FileUtils.writeFileContent(new File(publicPath1), publicList1);
        assertTrue(FileUtils.checkStandardLogType(new File(publicPath1), StandardLogType.Public.getType()));

        List<String> publicList2 = new ArrayList<>();
        publicList2.add(publicContent1);
        publicList2.add(publicContent2);
        publicList2.add(publicContent3);
        FileUtils.writeFileContent(new File(publicPath3), publicList2);
        assertTrue(!FileUtils.checkStandardLogType(new File(publicPath2), StandardLogType.Public.getType()));

        assertTrue(!FileUtils.checkStandardLogType(new File(publicPath3), StandardLogType.Public.getType()));

        removeFile(publicPath1);
        removeFile(publicPath2);
        removeFile(publicPath3);
    }

    @Test
    public void checkBamaiTest() throws Exception {
        String bamaiPath1 = "/tmp/standard/bamai.log.1";
        String bamaiPath2 = "/tmp/standard/bamai.log.2";
        String bamaiPath3 = "/tmp/standard/bamai.log.3";

        createFiles(bamaiPath1);
        createFiles(bamaiPath2);
        createFiles(bamaiPath3);

        // 把脉日志测试
        String bamaiContent1 = "[INFO][2015-12-02T00:00:07.099+0800][github.com/dataapi/handler.DataApi:212] aaaom_http_success||traceid=valuevaluevaluevaluevaluevalue23||spanid=value2||uri=value3||";
        String bamaiContent2 = "[INsssssFO][2015-12-02T00:00:07.099+0800][github.com/dataapi/handler.DataApi:212] aaaom_http_success||traceid=valuevaluevaluevaluevaluevalue23||spanid=value2||uri=value3||";
        String bamaiContent3 = "test";

        List<String> bamaiList1 = new ArrayList<>();
        bamaiList1.add(bamaiContent1);
        bamaiList1.add(bamaiContent2);
        FileUtils.writeFileContent(new File(bamaiPath1), bamaiList1);

        List<String> bamaiList2 = new ArrayList<>();
        bamaiList2.add(bamaiContent1);
        bamaiList2.add(bamaiContent2);
        bamaiList2.add(bamaiContent3);
        FileUtils.writeFileContent(new File(bamaiPath2), bamaiList2);
        assertTrue(!FileUtils.checkStandardLogType(new File(bamaiPath3), StandardLogType.Public.getType()));

        removeFile(bamaiPath1);
        removeFile(bamaiPath2);
        removeFile(bamaiPath3);
    }

    @Test
    public void checkNormalTest() throws Exception {
        String normalPath = "/tmp/standard/normal.log";

        createFiles(normalPath);
        assertTrue(FileUtils.checkStandardLogType(new File(normalPath),
            StandardLogType.Normal.getType()));
        assertTrue(FileUtils.checkStandardLogType(new File(normalPath), 6));

        removeFile(normalPath);
    }

    @Test
    public void isAllNumberTest() throws Exception {
        String input1 = "201811";
        String input2 = "a201811";
        String input3 = "20181b";
        String input4 = "20181f1";
        String input5 = "20181123232";
        assertTrue(FileUtils.isAllNumber(input1));
        assertTrue(!FileUtils.isAllNumber(input2));
        assertTrue(!FileUtils.isAllNumber(input3));
        assertTrue(!FileUtils.isAllNumber(input4));
        assertTrue(FileUtils.isAllNumber(input5));
    }

    @Test
    public void getMasterFlagTest() throws Exception {
        String pathBase = "/tmp/test.log";
        String path1 = "/tmp/test.log.20180101";
        String path2 = "/tmp/test.log.20180101.log";
        String path3 = "/tmp/test.log.20180101a.log";
        String path4 = "/tmp/20180101.test.log";
        String path5 = "/tmp/test.log-20180101.log";
        String path6 = "/tmp/test.log.20180101.12";
        String path7 = "/tmp/test.20180101.log";
        String path8 = "/tmp/test.20180101.12.log";
        String path9 = "/tmp/test.20180101log";
        String path10 = "/tmp/test20180101.log";
        String path11 = "/tmp/test.log2018010101";
        String path12 = "/tmp/test.log2018-01-01";
        String path13 = "/tmp/test.log2018-01-01-01";
        String path14 = "/tmp/test.log201801011";

        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path1),
            FileUtils.defaultRollingSamples)));
        assertTrue(!pathBase.equals(FileUtils.getMasterFilePath(new File(path2),
            FileUtils.defaultRollingSamples)));
        assertTrue(!pathBase.equals(FileUtils.getMasterFilePath(new File(path3),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path4),
            FileUtils.defaultRollingSamples)));
        assertTrue(!pathBase.equals(FileUtils.getMasterFilePath(new File(path5),
            FileUtils.defaultRollingSamples)));
        assertTrue(!pathBase.equals(FileUtils.getMasterFilePath(new File(path6),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path7),
            FileUtils.defaultRollingSamples)));
        assertTrue(!pathBase.equals(FileUtils.getMasterFilePath(new File(path8),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path9),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path10),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path11),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path12),
            FileUtils.defaultRollingSamples)));
        assertTrue(pathBase.equals(FileUtils.getMasterFilePath(new File(path13),
            FileUtils.defaultRollingSamples)));
        assertTrue(!pathBase.equals(FileUtils.getMasterFilePath(new File(path14),
            FileUtils.defaultRollingSamples)));
    }

    @Test
    public void getRelatedFilesTest() throws Exception {
        String pathBase = "/tmp/tmp/base.log";
        String path1 = "/tmp/tmp/base.log.20180101";
        String path2 = "/tmp/tmp/base.log.wf";
        String path3 = "/tmp/tmp/base.log.wf.1";
        String path4 = "/tmp/tmp/base.log.1";
        createFiles(pathBase);
        createFiles(path1);
        createFiles(path2);
        createFiles(path3);
        createFiles(path4);

        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_WHIAT);

        List<File> files1 = FileUtils.getRelatedFiles(pathBase, matchConfig);
        assertTrue(files1.size() == 2);

        List<String> rollingSample = new ArrayList<>();
        rollingSample.addAll(FileUtils.defaultRollingSamples);
        rollingSample.add("wf");
        matchConfig.setRollingSamples(rollingSample);
        List<File> files2 = FileUtils.getRelatedFiles(pathBase, matchConfig);
        System.out.println();
        assertTrue(files2.size() == 3);

        rollingSample.add("[0-9]{1}");
        List<File> files3 = FileUtils.getRelatedFiles(pathBase, matchConfig);
        assertTrue(files3.size() == 5);

        removeFile(pathBase);
        removeFile(path1);
        removeFile(path2);
        removeFile(path3);
        removeFile(path4);
    }

    @Test
    public void isSymbolicLinkTest() throws Exception {
        String path1 = "/tmp/tmp/";
        String link1 = "/tmp/test/";

        String path2 = "/tmp/tmp/file1";
        String path2_1 = "/tmp/test/file1";

        String file = "/tmp/tmp1/file2";
        String link_file = "/tmp/tmp1/link-file2";

        createFiles(path1);
        createFiles(path2);
        createFiles(file);

        Path existingFilePath = Paths.get(path1);
        Path symLinkPath = Paths.get(link1);
        if (Files.exists(symLinkPath)) {
            Files.delete(symLinkPath);
        }
        Files.createSymbolicLink(symLinkPath, existingFilePath);
        createFiles(path2_1);

        Path fileLink = Paths.get(file);
        Path symLinkFile = Paths.get(link_file);
        if (Files.exists(symLinkFile)) {
            Files.delete(symLinkFile);
        }
        Files.createSymbolicLink(symLinkFile, fileLink);

        System.out.println(FileUtils.getFileKeyByAttrs(path1));
        System.out.println(FileUtils.getFileKeyByAttrs(link1));
        System.out.println(FileUtils.getFileKeyByAttrs(path2));
        System.out.println(FileUtils.getFileKeyByAttrs(path2_1));

        System.out.println(new File(path1).getAbsolutePath());
        System.out.println(new File(link1).getAbsolutePath());
        System.out.println(new File(path2).getAbsolutePath());
        System.out.println(new File(path2_1).getAbsolutePath());

        System.out.println(FileUtils.getFileKeyByAttrs(file));
        System.out.println(FileUtils.getFileKeyByAttrs(link_file));

        removeFile(path2_1);
        removeFile(path2);
        Files.delete(symLinkPath);
        removeFile(path1);
    }

    @Test
    public void getMasterFliesUnderDir() {
        String file1 = "/tmp/tmp/access_log.2018-08-27.log";
        String file2 = "/tmp/tmp/access_log.2018-08-28.log";
        String file3 = "/tmp/tmp/access_log.2018-08-29.log";
        String file4 = "/tmp/tmp/access_log_test.log.2018-09-00";
        createFiles(file1);
        createFiles(file2);
        createFiles(file3);
        createFiles(file4);
        String dir = "/tmp/tmp/";

        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        matchConfig.setFileType(FileType.Dir.getStatus());
        matchConfig.setFileFilterType(LogConfigConstants.FILE_FILTER_TYPE_BLACK);

        Set<String> masterFiles = FileUtils.getMasterFliesUnderDir(dir, dir, matchConfig);
        if (masterFiles != null) {
            for (String file : masterFiles) {
                System.out.println(file);
            }
        }
        removeFile(file1);
        removeFile(file2);
        removeFile(file3);
        removeFile(file4);
    }

    @Test
    public void getLogPathFromOffset() {
        String path = "/tmp/tmp";
        String fileName1 = "access.log";
        String fileName2 = "access.log.20180101";
        String fileName3 = "access.log.2018-01-01";
        String fileName4 = "access.log.2";
        String fileName5 = "access.20180101.log";
        String fileName6 = "access.2018010101.log";
        String fileName7 = "access_2018010101.log";
        String fileName8 = "a.1";
        String fileName9 = "a1.1";
        String fileName10 = "acccess1.log";
        String fileName11 = ".1";

        String result1 = FileUtils.getLogPathFromOffset(path, fileName1);
        String result2 = FileUtils.getLogPathFromOffset(path, fileName2);
        String result3 = FileUtils.getLogPathFromOffset(path, fileName3);
        String result4 = FileUtils.getLogPathFromOffset(path, fileName4);
        String result5 = FileUtils.getLogPathFromOffset(path, fileName5);
        String result6 = FileUtils.getLogPathFromOffset(path, fileName6);
        String result7 = FileUtils.getLogPathFromOffset(path, fileName7);
        String result8 = FileUtils.getLogPathFromOffset(path, fileName8);
        String result9 = FileUtils.getLogPathFromOffset(path, fileName9);
        String result10 = FileUtils.getLogPathFromOffset(path, fileName10);
        String result11 = FileUtils.getLogPathFromOffset(path, fileName11);

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(result4);
        System.out.println(result5);
        System.out.println(result6);
        System.out.println(result7);
        System.out.println(result8);
        System.out.println(result9);
        System.out.println(result10);
        System.out.println(result11);
    }

    @Test
    public void writeFileContent() throws Exception {
        List<String> contents = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                sb.append("line" + i);
            }
            contents.add(sb.toString());
        }

        String path = "/tmp/tmp/tmp/offset.log";
        FileUtils.writeFileContent(new File(path), contents);
    }

    @Test
    public void getFileKeyFromNodeKeyTest() {
        String nodeKey = "16184_19378_2065_7516347939";
        System.out.println(FileUtils.getFileKeyFromNodeKey(nodeKey));
    }

    @Test
    public void sortByMTimeDesc() {
        long max = 100L;
        List<FileNode> fileNodes = new ArrayList<>();
        for (long i = 0; i <= max; i++) {
            fileNodes.add(new FileNode(0L, 0L, "fileKey", i, "/", "file1", 2L, new File("/test")));
        }
        FileUtils.sortByMTimeDesc(fileNodes);
        assertTrue(fileNodes.get(0).getModifyTime() == max);
    }

    @Test
    public void sortByMTime() {
        long max = 100L;
        List<FileNode> fileNodes = new ArrayList<>();
        for (long i = 0; i <= max; i++) {
            fileNodes.add(new FileNode(0L, 0L, "fileKey", i, "/", "file1", 2L, new File("/test")));
        }
        FileUtils.sortByMTime(fileNodes);
        assertTrue(fileNodes.get(0).getModifyTime() == 0);
    }

    @Test
    public void sortWFNByMTime() {
        long max = 100L;
        List<WorkingFileNode> wfns = new ArrayList<>();
        for (long i = 0; i <= max; i++) {
            FileNode fileNode = new FileNode(0L, 0L, "fileKey", i, "/", "file1", 2L, new File("/test"));
            WorkingFileNode workingFileNode = new WorkingFileNode(fileNode, null);
            wfns.add(workingFileNode);
        }
        FileUtils.sortWFNByMTime(wfns);
        assertTrue(wfns.get(0).getModifyTime() == 0);
    }

    @Test
    public void sortOffsetByMTimeDesc() {
        long max = 100L;
        List<FileOffSet> fileOffSets = new ArrayList<>();
        for (long i = 0; i <= max; i++) {
            FileOffSet offSet = new FileOffSet(0L, 0L, "/", "fileKey");
            offSet.setLastModifyTime(i);
            offSet.setTimeStamp(i);
            fileOffSets.add(offSet);
        }
        FileUtils.sortOffsetByMTimeDesc(fileOffSets);
        assertTrue(fileOffSets.get(0).getTimeStamp() == max);
    }

    @Test
    public void getFileSuffix() {
        String sourcePath = "/home/xiaoju/";
        String targetPath = "/home/xiaoju/didi/test.log";
        assertEquals(FileUtils.getFileSuffix(sourcePath, targetPath), "didi/test.log");

        String sourcePath1 = "/home/xiaoju";
        String targetPath1 = "/home/xiaoju/didi/test.log";
        assertEquals(FileUtils.getFileSuffix(sourcePath1, targetPath1), "/didi/test.log");
    }

    @Test
    public void getFileNodeMD5Test() throws IOException {
        String filePath = "/tmp/Md5test.log";
        String filePath1 = "/tmp/Md5test.log.2020-09-11";
        String testContent1 = "\n";
        String testContent2 = "   ";
        String testContent3 = "pera_stat_key=g_u_user_trans_suc_log" ;
        String testContent4 = "asdff";
        List<String> testContentList = new ArrayList<>();
        testContentList.add(testContent1);
        testContentList.add(testContent2);
        testContentList.add(testContent3);
        testContentList.add(testContent4);
        File file = new File(filePath);
        File file1 = new File(filePath1);
        if (!file.exists()) {
            file.createNewFile();
            FileUtils.writeFileContent(new File(filePath), testContentList);
        }

        if (!file1.exists()) {
            file1.createNewFile();
            FileUtils.writeFileContent(new File(filePath), testContentList);
        }

        String Md5 = "";
        String targetMd5 = "36586F1A60124E2D361A869FDBD76153";
        boolean result = true;
        for(int i =0;i<100;i++){
            Md5 = FileUtils.getFileNodeHeadMd5(file);
            if(!Md5.equals(targetMd5)){
                result=false;
            }
        }
        removeFile(filePath);
        assertTrue(result==true);
    }

    @Test
    public void getMd5test() {
        String content = "timeStamp";
        String targetMd5 = "87A3CB5C3554B2D9D8A1A773AD0936BE";
        String md5 = "";
        boolean result = true;
        for (int i = 0; i < 100; i++) {
            md5 = CommonUtils.getMd5(content);
            if (!md5.equals(targetMd5)) {
                result = false;
            }
        }
        assertTrue(result == true);
    }

    @Test
    public void testParseTimestamp() throws Exception {

        List<String> formats = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
            LogConfigConstants.LONG_TIMESTAMP);
        for (String format : formats) {
            Long current = System.currentTimeMillis() / 1000 * 1000;
            String timeString;
            if (format.equals(LogConfigConstants.LONG_TIMESTAMP)) {
                timeString = String.valueOf(current);
            } else {
                timeString = new SimpleDateFormat(format).format(new Date(current));
            }
            String log = String.format(
                "[INFO][LOGGER]mylog||a=123||b=456||timestamp=wrongtime||timestamp=%s", timeString);
            LogSourceConfig config = new LogSourceConfig();
            config.setTimeFormat(format);
            config.setTimeFormatLength(format.replace("'", "").length());
            config.setTimeStartFlag("timestamp=");
            config.setTimeStartFlagIndex(1);
            String parsedString = FileUtils.getTimeStringFormLineByIndex(log, config);
            Long timeStamp = TimeUtils.getLongTimeStamp(parsedString, format);
            assertEquals(current, timeStamp);
        }
    }

}
