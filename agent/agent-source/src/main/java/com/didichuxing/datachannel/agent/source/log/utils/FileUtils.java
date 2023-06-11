package com.didichuxing.datachannel.agent.source.log.utils;

import java.io.*;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.didichuxing.datachannel.agent.common.api.StandardLogType;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.utils.ProcessUtils;
import com.didichuxing.datachannel.agent.source.log.beans.FileDirNode;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;
import org.apache.commons.lang3.StringUtils;

import com.didichuxing.datachannel.agent.common.api.FileMatchType;
import com.didichuxing.datachannel.agent.common.api.FileType;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;
import com.didichuxing.datachannel.agent.source.log.type.PublicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 文件工具类
 * @author: huangjw
 * @Date: 19/7/4 21:11
 */
public class FileUtils {

private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class.getName());private static final String UNDERLINE_SEPARATOR   = "_";

    private static Pattern      NUM_PATTERN           = Pattern.compile("[0-9]");

    private static Pattern      NO_LETTER_PATTERN     = Pattern.compile("[^a-zA-Z]");

    private static Pattern      LETTER_PATTERN        = Pattern.compile("[a-zA-Z]");

    private static final String TAG_GZ_SUFFIX         = ".tar.gz";
    private static final String GZ_SUFFIX             = ".gz";
    private static final String ZIP_SUFFIX            = ".zip";

    private static final String TAG_GZ                = ".tar.gz.";
    private static final String GZ                    = ".gz.";
    private static final String ZIP                   = ".zip.";

    /**
     * 正则表达式
     */
    public static List<String>  defaultRollingSamples = new ArrayList<>();
    private static final String ROLL_HOUR1            = "\\d{4}-\\d{2}-\\d{2}-\\d{2}";
    private static final String ROLL_HOUR2            = "\\d{4}\\d{2}\\d{2}\\d{2}";
    private static final String ROLL_DAY1             = "\\d{4}-\\d{2}-\\d{2}";
    private static final String ROLL_DAY2             = "\\d{4}\\d{2}\\d{2}";

    private static Method indexOfMethod;

    private static boolean jdkVersionGe11;

    public static final byte[] CR_LINE_DELIMITER = {'\r'};
    public static final byte[] LF_LINE_DELIMITER = {'\n'};
    public static final byte[] CRLF_LINE_DELIMITER = {'\r', '\n'};

    static {
        defaultRollingSamples.add(ROLL_HOUR1);
        defaultRollingSamples.add(ROLL_HOUR2);
        defaultRollingSamples.add(ROLL_DAY1);
        defaultRollingSamples.add(ROLL_DAY2);
        jdkVersionGe11 = jdkVersionGe11();
        LOGGER.info(String.format("jdkVersionGe11:%s", String.valueOf(jdkVersionGe11)));
        if(jdkVersionGe11) {
            try {
                Class<?> stringLatin1 = Class.forName("java.lang.StringLatin1");
                indexOfMethod = stringLatin1.getMethod("indexOf", byte[].class, int.class, byte[].class, int.class, int.class);
                indexOfMethod.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @return 返回当前进程对应jdk版本是否大于等于11
     */
    private static boolean jdkVersionGe11() {
        String javaVersion = ProcessUtils.getInstance().getCurrentProcessJdkVersion();
        String[] partOfJavaVersion = StringUtils.split(javaVersion, ".");
        if(StringUtils.isBlank(javaVersion) || null == partOfJavaVersion || partOfJavaVersion.length < 2) {
            LOGGER.warn(
                    String.format("class=FileUtils||method=jdkVersionGe11||msg=%s", "获取当前进程jdk版本失败，系统将默认选择jdk1.8方式")
            );
            return false;
        } else {
            Integer bigVersion = Integer.valueOf(partOfJavaVersion[0]);
            if(11 <= bigVersion) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 获取文件的inode
     *
     * @param path file path
     * @return file's inode
     */
    public static long getInode(String path) {
        File file = new File(path);
        return getInode(file);
    }

    /**
     * 构建 logModelId_pathId key
     *
     * @param logModelId log model id
     * @param pathId log path id
     * @return the key of combo of log model and log path
     */
    public static String buildLogIDAndPathIDKey(Integer logModelId, Integer pathId) {
        return logModelId + UNDERLINE_SEPARATOR + pathId;
    }

    /**
     * 获取文件的inode
     *
     * @param file file to get inode
     * @return file's inode
     */
    public static long getInode(File file) {
        try {
            return (long) Files.getAttribute(file.toPath(), "unix:ino");
        } catch (IOException e) {
            LogGather.recordErrorLog("FileUtil error!", "get file's inode error! file is " + file, e);
        }
        return LogConfigConstants.DEFAULT_INODE;
    }

    /**
     * 获取文件的inode
     *
     * @param file file to get dev
     * @return file's dev
     */
    public static long getDev(File file) {
        try {
            return (long) Files.getAttribute(file.toPath(), "unix:dev");
        } catch (IOException e) {
            LOGGER.error("FileUtil error! get file's inode error! file is " + file, e);
        }
        return LogConfigConstants.DEFAULT_INODE;
    }

    /**
     * 获取文件的创建时间
     *
     * @param file file to get creatTime
     * @return file's create time
     */
    public static long getCreateTime(File file) {
        try {
            Path path = Paths.get(file.getAbsolutePath());
            // 获取文件的属性
            BasicFileAttributes att = Files.readAttributes(path, BasicFileAttributes.class);
            return att.creationTime().toMillis();
        } catch (Exception e) {
            LOGGER.error("FileUtil error!", "get file's createTime error! file is " + file, e);
        }
        return LogConfigConstants.DEFAULT_CREATE_TIME;
    }

    /**
     * 获取文件的创建时间
     *
     * @param file file to get modifyTime
     * @return file's modify time
     */
    public static long getModifyTime(File file) {
        try {
            if (file.exists()) {
                return file.lastModified();
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("FileUtil error!", "get file's modifyTime error! file is " + file, e);
        }
        return LogConfigConstants.DEFAULT_MODIFY_TIME;
    }

    /**
     * 获取所有相关文件
     *
     * @param path file path
     * @param matchConfig match config
     * @return all related files
     */
    public static List<File> getRelatedFiles(final String path, final MatchConfig matchConfig) {
        final List<File> result = new ArrayList<>();
        Set<File> fileSet = new HashSet<>();
        if (StringUtils.isBlank(path)) {
            return null;
        }
        File masterFile = new File(path);
        final String masterFileName = masterFile.getName();
        if (masterFile.exists() && masterFile.isFile()) {
            fileSet.add(masterFile);
        }

        String dirPath = FileUtils.getPathDir(path);
        if (StringUtils.isBlank(dirPath)) {
            return null;
        }

        File masterDir = new File(dirPath);
        File[] rolledFiles = masterDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                File file = new File(dir + File.separator + name);
                if (!file.exists() || file.isDirectory()) {
                    return false;
                }

                // 过滤非文本文件
                if (!checkIsTextFile(name)) {
                    return false;
                }

                if (name.length() < masterFileName.length()) {
                    return false;
                }

                if (matchConfig == null) {
                    return name.startsWith(masterFileName);
                } else {
                    return match(file, path, matchConfig);
                }
            }
        });

        if (rolledFiles != null) {
            for (File file : rolledFiles) {
                fileSet.add(file);
            }
        }

        return new ArrayList<>(fileSet);
    }

    /**
     * 获取最新相关文件
     *
     * @param parentPath log path
     * @param fileName master file name
     * @param matchConfig match config
     * @return files which match the matchconfig
     */
    public static File getLatestRelatedFile(final String parentPath, final String fileName,
                                            final MatchConfig matchConfig) {
        List<File> files = getRelatedFiles(parentPath + File.separator + fileName, matchConfig);
        if (files == null || files.size() == 0) {
            return null;
        }

        File latestFile = files.get(0);
        for (File rollFile : files) {
            if (rollFile.lastModified() > latestFile.lastModified()) {
                latestFile = rollFile;
            }
        }
        return latestFile;
    }

    /**
     * 根据完整路径获取文件目录
     *
     * @param path log path
     * @return file's parent path
     */
    public static String getPathDir(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }

        if (path.contains(File.separator)) {
            return path.substring(0, path.lastIndexOf(File.separator));
        }
        return path;
    }

    /**
     * 根据完整路径获取文件名
     *
     * @param path file path
     * @return master file's path
     */
    public static String getMasterFile(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }

        if (path.contains(File.separator)) {
            return path.substring(path.lastIndexOf(File.separator) + 1);
        } else {
            return path;
        }
    }

    /**
     * 校验是否是有效滚动文件 兼容老版本的文件后缀长度校验
     *
     * @param rolledFileName rolled file name
     * @param fileSuffixSeparate file's suffix separate
     * @param fileSuffixPattern file's suffix pattern
     * @return the result of check
     */
    private static boolean checkIsVaildFile(String masterFile, String rolledFileName, String fileSuffixSeparate,
                                            String fileSuffixPattern) {
        if (StringUtils.isNotBlank(masterFile) && StringUtils.isNotBlank(rolledFileName)) {
            if (!checkIsTextFile(rolledFileName)) {
                return false;
            }

            if (!rolledFileName.startsWith(masterFile)) {
                return false;
            }

            int suffixLength = 0;
            if (StringUtils.isNotBlank(fileSuffixSeparate)) {
                suffixLength += fileSuffixSeparate.length();
            }

            if (StringUtils.isNotBlank(fileSuffixPattern)) {
                suffixLength += fileSuffixPattern.length();
            }

            if (suffixLength != 0) {
                return masterFile.length() + suffixLength == rolledFileName.length()
                       || rolledFileName.equals(masterFile);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否是文本文件
     *
     * @param filePath file path
     * @return result of file is text file.
     */
    public static boolean checkIsTextFile(String filePath) {
        if (filePath.lastIndexOf(TAG_GZ) != -1 || filePath.lastIndexOf(GZ) != -1 || filePath.lastIndexOf(ZIP) != -1
            || filePath.endsWith(TAG_GZ_SUFFIX) || filePath.endsWith(GZ_SUFFIX) || filePath.endsWith(ZIP_SUFFIX)) {
            return false;
        }
        return true;
    }

    /**
     * 根据index进行切割，从而得到对应的timeStamp
     *
     * @param line log content
     * @param logSourceConfig logSourceConfig
     * @return the timestamp in the this content
     */
    public static String getTimeStringFormLineByIndex(String line, LogSourceConfig logSourceConfig) {
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        String timeFormat = logSourceConfig.getTimeFormat();
        int timeFormatLength = logSourceConfig.getTimeFormatLength();
        String startFlag = logSourceConfig.getTimeStartFlag();
        int startFlagIndex = logSourceConfig.getTimeStartFlagIndex();

        String timeString;
        if (StringUtils.isEmpty(startFlag) && startFlagIndex == 0) {
            if (line.length() < timeFormatLength) {
                return null;
            } else {
                timeString = line.substring(0, timeFormatLength);
            }
        } else {
            try {
                // startFlag不为空
                // 对字符创进行切割，得到最终的字符创
                if (!StringUtils.isEmpty(startFlag)) {
                    boolean isVaild = true;
                    int currentIndex = 0;
                    for (int i = 0; i < startFlagIndex + 1; i++) {
                        int startSubIndex = line.indexOf(startFlag, currentIndex);
                        if (startSubIndex >= 0) {
                            currentIndex = startSubIndex + startFlag.length();
                        } else {
                            // 此时说明line中不存在startFlag
                            isVaild = false;
                            break;
                        }
                    }
                    if (isVaild) {
                        if (line.length() < timeFormatLength) {
                            return null;
                        } else {
                            timeString = line.substring(currentIndex, currentIndex + timeFormatLength);
                        }
                    } else {
                        // 此时说明line中不存在startFlag
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.info("timeFormat=" + timeFormat + ", startFlag=" + startFlag + ", startFlagIndex="
                            + startFlagIndex);
                }
                return null;
            }
        }

        if (StringUtils.isBlank(timeString)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("timeFormat=" + timeFormat + ", startFlag=" + startFlag + ", startFlagIndex="
                        + startFlagIndex);
            }
        }

        return timeString;
    }

    /**
     * 构建文件的唯一id
     *
     * @param file file
     * @param logModelId log model's id
     * @param pathId log path's id
     * @return unique key of file
     */
    public static String buildFileNodeUniqueKey(File file, Integer logModelId, Integer pathId) {
        return logModelId + LogConfigConstants.UNDERLINE_SEPARATOR + pathId + LogConfigConstants.UNDERLINE_SEPARATOR
               + getFileKeyByAttrs(file);
    }

    /**
     * 获取文件下的所有子文件
     *
     * @param file file
     * @return files under @file
     */
    public static List<String> listChildren(File file) {
        List<String> ret = new ArrayList<>();

        if (file == null || !file.exists() || !file.isDirectory()) {
            return ret;
        }

        if (file.exists()) {
            String[] array = file.list();
            if (array != null && array.length != 0) {
                for (String s : array) {
                    ret.add(s);
                }
            }
        }
        return ret;
    }

    /**
     * 删除文件
     *
     * @param file file need to delete
     */
    public static void delFile(File file) {
        if (file.isDirectory()) {
            List<String> children = listChildren(file);
            for (String f : children) {
                delFile(new File(file.getAbsolutePath() + File.separator + f));
            }
        }

        file.delete();
    }

//    /**
//     * 写文件
//     *
//     * @param file file
//     * @param contents contents to write
//     */
//    public static void writeFileContent(File file, List<String> contents) throws FileNotFoundException {
//        BufferedWriter bw = null;
//        try {
//            bw = new BufferedWriter(new FileWriter(file));
//            for (String ctn : contents) {
//                bw.write(ctn);
//                bw.newLine();
//            }
//            bw.flush();
//        } catch (FileNotFoundException e) {
//            throw e;
//        } catch (Exception e) {
//            LogGather.recordErrorLog("FileUtil error!", "write file error! file is " + file, e);
//        } finally {
//            try {
//                if (bw != null) {
//                    bw.close();
//                }
//            } catch (IOException e) {
//                LogGather.recordErrorLog("FileUtil error!", "file  close error! file is " + file, e);
//            }
//        }
//    }

    /**
     * 写文件
     *
     * @param file file
     * @param contents contents to write
     */
    public static void writeFileContent(File file, List<String> contents) throws FileNotFoundException{
       FileOutputStream fo = null;
       try{
           fo = new FileOutputStream(file,true);
           for (String ctn : contents) {
               fo.write(ctn.getBytes());
               fo.write(("\r\n").getBytes());
           }
       } catch (FileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LogGather.recordErrorLog("FileUtil error!", "write file error! file is " + file, e);
        } finally {
            try {
                if (fo != null) {
                    // 强制刷新到硬盘
                    fo.getFD().sync();
                    fo.close();
                }
            } catch (IOException e) {
                LogGather.recordErrorLog("FileUtil error!", "file  close error! file is " + file, e);
            }
        }
    }

    /**
     * 读取文件
     *
     * @param file file
     * @param maxNum num of content to read
     * @return result of read
     */
    public static List<String> readFileContent(File file, int maxNum) {
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

    /**
     * 校验文件是否需要采集
     *
     * @param file 实际的路径
     * @param logPath 填写的路径
     * @param matchConfig match config
     * @return match result
     */
    public static boolean match(File file, String logPath, MatchConfig matchConfig) {
        if (!file.exists()) {
            return false;
        }
        if (matchConfig == null) {
            String masterFile = FileUtils.getMasterFile(logPath);
            String fileName = file.getName();
            return checkIsVaildFile(masterFile, fileName, null, null);
        }

        if (matchConfig.getFileType() == FileType.File.getStatus()) {
            // 匹配文件
            if (file.isDirectory()) {
                return false;
            }
            String fileName = file.getName();
            String masterFile = FileUtils.getMasterFile(logPath);
            if (StringUtils.isBlank(masterFile)) {
                return false;
            }
            if (matchConfig.getMatchType() == FileMatchType.Length.getStatus()) {
                // 文件长度匹配
                return checkIsVaildFile(masterFile, fileName, matchConfig.getFileSuffix(), null);
            } else if (matchConfig.getMatchType() == FileMatchType.Regex.getStatus()) {
                // 正则匹配文件
                if (fileName.startsWith(masterFile)) {
                    if (fileName.equals(masterFile)) {
                        return true;
                    }
                    String fileSuffix = file.getName().substring(masterFile.length());
                    return Pattern.matches(matchConfig.getFileSuffix(), fileSuffix);
                } else {
                    return false;
                }
            }
        } else if (matchConfig.getFileType() == FileType.Dir.getStatus()) {
            // 匹配目录
            File logPathFile = new File(logPath);

            if (logPathFile.exists() && logPathFile.isDirectory()) {
                // logPath为目录
                String filePath = file.getAbsolutePath();
                String standardLogPath = getDirStandardPath(logPath);
                if (file.isDirectory()) {
                    filePath = getDirStandardPath(filePath);
                }

                if (filePath.startsWith(standardLogPath)) {
                    return fileFilter(filePath, matchConfig.getFileFilterType(), matchConfig.getFileFilterRules());
                } else {
                    return false;
                }
            } else {
                // logPath不存在 或 为文件
                if (file.isDirectory()) {
                    // file为目录
                    String filePath = getDirStandardPath(file.getAbsolutePath());
                    return fileFilter(filePath, matchConfig.getFileFilterType(), matchConfig.getFileFilterRules());
                } else {
                    // file为文件
                    String masterFilePath = getMasterFilePath(file,
                                                              matchConfig.getRollingSamples() == null ? defaultRollingSamples : matchConfig.getRollingSamples());
                    if (StringUtils.isNotBlank(masterFilePath) && masterFilePath.equals(logPath)) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取path的标准目录格式，即以"/"结尾
     *
     * @param path 路径
     * @return 标准化目录路径
     */
    public static String getDirStandardPath(String path) {
        return path.endsWith("/") ? path : path + "/";
    }

    /**
     * 黑白名单过滤
     *
     * @param filePath file path
     * @param fileFilterType filter type
     * @param filtFilterRules filter rules
     * @return filter result
     */
    private static boolean fileFilter(String filePath, Integer fileFilterType, List<String> filtFilterRules) {
        if (filtFilterRules == null || filtFilterRules.size() == 0) {
            return true;
        }

        boolean isMatch = false;
        for (String filterRule : filtFilterRules) {
            if (StringUtils.isNotBlank(filterRule)) {
                if (Pattern.matches(filterRule, filePath)) {
                    isMatch = true;
                    break;
                }
            }
        }

        if (fileFilterType.equals(LogConfigConstants.FILE_FILTER_TYPE_BLACK)) {
            // 黑名单
            return !isMatch;
        } else {
            // 白名单
            return isMatch;
        }
    }

    /**
     * 获取文件目录下的所有主文件
     *
     * @return masterFiles under the dir;
     */
    public static Set<String> getMasterFliesUnderDir(String dir, String logPath, MatchConfig matchConfig) {
        Set<String> masterFiles = new HashSet<>();
        ergodic(dir, logPath, matchConfig, masterFiles);
        // 主文件不包含数字
        Set<String> newMasterFiles = new HashSet<>();
        if (masterFiles.size() != 0) {
            for (String masterFilePath : masterFiles) {
                String fileName = getMasterFile(masterFilePath);
                if (StringUtils.isNotBlank(fileName) && !isContainNumber(fileName)) {
                    newMasterFiles.add(masterFilePath);
                }
            }
        }
        return newMasterFiles;
    }

    /**
     * 递归获取目录下的所有文件
     *
     * @param dir dir
     * @param logPath log path
     * @param matchConfig match config
     * @param masterFiles master files
     */
    private static void ergodic(String dir, String logPath, MatchConfig matchConfig, Set<String> masterFiles) {
        File fileDir = new File(dir);
        if (fileDir.exists() && fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        ergodic(f.getAbsolutePath(), logPath, matchConfig, masterFiles);
                    } else {
                        List<String> rollingSamples = matchConfig.getRollingSamples();
                        String masterFlagPath = getMasterFilePath(f,
                                                                  rollingSamples == null ? defaultRollingSamples : rollingSamples);
                        if (StringUtils.isBlank(masterFlagPath)) {
                            continue;
                        }
                        if (!masterFiles.contains(masterFlagPath)) {
                            if (matchConfig != null) {
                                if (match(f, logPath, matchConfig)) {
                                    masterFiles.add(masterFlagPath);
                                }
                            } else {
                                masterFiles.add(masterFlagPath);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取dir下所有的路径
     *
     * @param dir current dir
     * @param logPath logPath
     * @param matchConfig match config
     * @return all dirs under dir
     */
    public static Set<String> getAllDirsUnderDir(String dir, String logPath, MatchConfig matchConfig) {
        Set<String> vaildDirs = new HashSet<>();
        FileUtils.ergodicDir(dir, logPath, matchConfig, vaildDirs);
        return vaildDirs;
    }

    /**
     * 递归获取目录下所有的目录
     *
     * @param dir current dir
     * @param logPath logPath
     * @param matchConfig match config
     * @param allDirs dir of ergodic
     */
    private static void ergodicDir(String dir, String logPath, MatchConfig matchConfig, Set<String> allDirs) {
        File fileDir = new File(dir);
        if (fileDir.exists() && fileDir.isDirectory()) {
            allDirs.add(fileDir.getAbsolutePath());
            File[] files = fileDir.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        if (matchConfig != null) {
                            if (match(f, logPath, matchConfig)) {
                                allDirs.add(f.getAbsolutePath());
                            }
                        } else {
                            allDirs.add(f.getAbsolutePath());
                        }
                        ergodicDir(f.getAbsolutePath(), logPath, matchConfig, allDirs);
                    }
                }
            }
        }
    }

    /**
     * 主文件内容
     *
     * @param file file
     * @param rollingSamples rolling sample list
     * @return master flag in file's name
     */
    public static String getMasterFilePath(File file, List<String> rollingSamples) {
        String fileName = file.getName();
        if (fileName.contains(".")) {
            String[] items = fileName.split("\\.");
            StringBuilder sb = new StringBuilder();
            for (String item : items) {
                String content = regexContent(item, rollingSamples);
                if (StringUtils.isNotBlank(content)) {
                    // 匹配
                    int index = item.indexOf(content);
                    item = item.substring(0, index) + item.substring(index + content.length(), item.length());
                }
                if (StringUtils.isNotBlank(item)) {
                    sb.append(item).append(".");
                }
            }
            String result = sb.toString();
            return file.getParent() + File.separator + result.substring(0, result.length() - 1);
        }
        return file.getAbsolutePath();
    }

    /**
     * 寻找partterns中各个正则表达式匹配与input匹配的内容 多个规则时，匹配最长的规则
     *
     * @param input 待匹配字符串
     * @param patterns 正则表达式
     * @return partterns中与input匹配的正则表达式的相关内容
     */
    private static String regexContent(String input, List<String> patterns) {
        if (StringUtils.isBlank(input) || patterns == null || patterns.size() == 0) {
            return null;
        }

        Matcher m, m1;
        String result = null;
        for (String pattern : patterns) {
            // 判断正则表达式是否存在于字符串中
            m = Pattern.compile(".*" + pattern + ".*").matcher(input);
            if (m.find()) {
                // 再切割一次
                m1 = Pattern.compile(pattern).matcher(input);
                m1.find();
                String finalResutl = m1.group();
                if (StringUtils.isBlank(result) || result.length() < finalResutl.length()) {
                    result = finalResutl;
                }
            }
        }
        return result;
    }

    /**
     * 字符串是否均为数字
     *
     * @param input input
     * @return is all char is number
     */
    public static boolean isAllNumber(String input) {
        String regex = "^\\d{" + input.length() + ",}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).find();
    }

    /**
     * 获取文件key
     *
     * @param file file
     * @return file's filekey
     */
    public static String getFileKeyByAttrs(File file) {
        try {
            if (file.exists()) {
                return getDev(file) + UNDERLINE_SEPARATOR + getInode(file);
            } else {
                return null;
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("FileUtil error", "get path[" + file.getAbsolutePath() + "]'s fileKey error", e);
        }
        return null;
    }

    /**
     * 获取文件的key
     *
     * @param path file's path
     * @return file's filekey
     */
    public static String getFileKeyByAttrs(String path) {
        return getFileKeyByAttrs(new File(path));
    }

    /**
     * 根据node key获取对应的filekey nodekey = logId_pathId_fileKey
     *
     * @param nodeKey file's nodekey
     * @return filekey in the node key
     */
    public static String getFileKeyFromNodeKey(String nodeKey) {
        String[] items = nodeKey.split(UNDERLINE_SEPARATOR);
        if (items.length >= 4) {
            return items[2] + UNDERLINE_SEPARATOR + items[3];
        }
        return "";
    }

    /**
     * 是否包含数字
     *
     * @param company input
     * @return result of check
     */
    public static boolean isContainNumber(String company) {
        Matcher m = NUM_PATTERN.matcher(company);
        return m.find();
    }

    /**
     * 是否不包含字母
     *
     * @param input input
     * @return result of check
     */
    public static boolean isNoLetter(String input) {
        Matcher m = NO_LETTER_PATTERN.matcher(input);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 是否包含字母
     *
     * @param input input
     * @return result of check
     */
    public static boolean isContainLetter(String input) {
        Matcher m = LETTER_PATTERN.matcher(input);
        return m.find();
    }

    /**
     * 校验日志是否是对应的标准文件(目前不做日志标准化校验，以后可以根据对应的需求做校验)
     *
     * @param file 待校验文件
     * @param matchConfig matchConfig
     * @return check result
     */
    public static boolean checkStandard(File file, MatchConfig matchConfig) {
        return true;
    }

    /**
     * 校验日志是否是对应的标准文件
     *
     * @param logType logType
     * @return check result
     */
    public static boolean checkStandardLogType(File file, Integer logType) {
        if (logType != null) {
            boolean result;
            StandardLogType type = StandardLogType.getByType(logType);
            if (type == null) {
                return true;
            }
            switch (type) {
                case Normal:
                    result = true;
                    break;
                case Public:
                    PublicType publicType = new PublicType();
                    result = publicType.check(file, null);
                    break;
                default:
                    result = false;
                    break;
            }
            return result;
        } else {
            return true;
        }
    }

    /**
     * 从老版本的offset中提取出其logPath
     * @param path
     * @param fileName
     * @return
     */
    public static String getLogPathFromOffset(String path, String fileName) {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(fileName)) {
            return null;
        }
        if (!path.endsWith("/")) {
            path += File.separator;
        }

        char[] chars = fileName.toCharArray();
        int sub = 0;
        for (int i = 0; i < chars.length; i++) {
            // 当前字符为数字，上一个字符不为数字不为字母时，即可退出
            if (isNumber(chars[i]) && i != 0 && !isNumber(chars[i - 1]) && !isLetter(chars[i - 1])) {
                sub = i;
                break;
            }
        }

        if (sub != 0 && sub >= 2) {
            fileName = fileName.substring(0, sub - 1);
        }
        return path + fileName;
    }

    /**
     * 判断字符是否是数字
     * @param c
     * @return
     */
    private static boolean isNumber(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        }
        return false;
    }

    /**
     * 判断字符是否是字母
     * @param c
     * @return
     */
    private static boolean isLetter(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        }
        return false;
    }

    /**
     * 根据最新时间排序,时间戳递减
     *
     * @param fileNodes filenode need to be sorted
     */
    public static void sortByMTimeDesc(List<FileNode> fileNodes) {
        if (fileNodes != null && fileNodes.size() != 0) {
            // 对nodes按照modifyTime排序, 显示标注
            Collections.sort(fileNodes, new Comparator<FileNode>() {

                @Override
                public int compare(FileNode b1, FileNode b2) {
                    if (b1.getModifyTime() > b2.getModifyTime()) {
                        return -1;
                    }

                    if (b1.getModifyTime() < b2.getModifyTime()) {
                        return 1;
                    }

                    return 0;
                }
            });
        }
    }

    /**
     * 根据最新时间排序,时间戳递增
     *
     * @param fileNodes filenode need to be sorted
     */
    public static void sortByMTime(List<FileNode> fileNodes) {
        if (fileNodes != null && fileNodes.size() != 0) {
            // 对nodes按照modifyTime排序, 显示标注
            Collections.sort(fileNodes, new Comparator<FileNode>() {

                @Override
                public int compare(FileNode b1, FileNode b2) {
                    if (b1.getModifyTime() > b2.getModifyTime()) {
                        return 1;
                    }

                    if (b1.getModifyTime() < b2.getModifyTime()) {
                        return -1;
                    }

                    return 0;
                }
            });
        }
    }

    /**
     * 根据最新时间排序,时间戳递增
     *
     * @param wFileNodes WorkingFileNode need to be sorted
     */
    public static void sortWFNByMTime(List<WorkingFileNode> wFileNodes) {
        if (wFileNodes != null && wFileNodes.size() != 0) {
            // 对nodes按照modifyTime排序, 显示标注
            Collections.sort(wFileNodes, new Comparator<WorkingFileNode>() {

                @Override
                public int compare(WorkingFileNode b1, WorkingFileNode b2) {
                    if (b1.getModifyTime() > b2.getModifyTime()) {
                        return 1;
                    }

                    if (b1.getModifyTime() < b2.getModifyTime()) {
                        return -1;
                    }

                    return 0;
                }
            });
        }
    }

    /**
     * 根据最新时间排序,时间戳递减
     *
     * @param fileOffSets filenode need to be sorted
     */
    public static void sortOffsetByMTimeDesc(List<FileOffSet> fileOffSets) {
        if (fileOffSets != null && fileOffSets.size() != 0) {
            // 对nodes按照modifyTime排序, 显示标注
            Collections.sort(fileOffSets, new Comparator<FileOffSet>() {

                @Override
                public int compare(FileOffSet b1, FileOffSet b2) {
                    if (b1.getTimeStamp() > b2.getTimeStamp()) {
                        return -1;
                    }

                    if (b1.getTimeStamp() < b2.getTimeStamp()) {
                        return 1;
                    }

                    return 0;
                }
            });
        }
    }

    /**
     * 根据filePath获取FileDirNode
     *
     * @param logPath 文件路径
     * @return fileNode
     */
    public static FileDirNode getFileDirNode(LogPath logPath, String filePath) {
        File input = new File(filePath);
        File dirFile;
        String vaildDir;
        if (input.exists() && input.isDirectory()) {
            // filePath为文件目录
            vaildDir = filePath;
            dirFile = input;
        } else {
            // filePath为文件 或 不存在
            vaildDir = FileUtils.getPathDir(filePath);
            if (org.apache.commons.lang.StringUtils.isBlank(vaildDir)) {
                return null;
            }
            dirFile = new File(vaildDir);
        }
        FileDirNode fileDirNode = new FileDirNode(logPath.getLogModelId(), logPath.getPathId(), vaildDir,
                FileUtils.getFileKeyByAttrs(dirFile),
                FileUtils.getModifyTime(dirFile));
        return fileDirNode;
    }

    /**
     * 获取文件后缀，用户目录场景
     * result = targetPath - sourcePath
     * @param sourcePath
     * @param targetPath
     * @return
     */
    public static String getFileSuffix(String sourcePath, String targetPath) {
        if (StringUtils.isBlank(targetPath) || !targetPath.startsWith(sourcePath)) {
            return sourcePath;
        }
        return targetPath.substring(sourcePath.length());
    }

    /**
     * 读取前一行日志转为Md5值,如果1行日志太大截取1k做MD5返回。
     * @param file
     * @return 返回文件头部1k内容的Md5值
     */
    public static String getFileNodeHeadMd5(File file) {
        // 添加文件不存在判断
        if (!file.exists()) {
            return LogConfigConstants.MD5_FAILED_TAG;
        }
        BufferedRandomAccessFile bufferedRandomAccessFile = null;
        byte[] result = null;
        String resultString = null;
        try {
            bufferedRandomAccessFile = new BufferedRandomAccessFile(file, "r");
            // 获取MD5时兼容文件开头为空格以及换行符的情况
            if (file.length() > 0) {
                while ((result = bufferedRandomAccessFile.readNewLine(5L)) != null) {
                    resultString = new String(result, StandardCharsets.ISO_8859_1);
                    if (StringUtils.isNotBlank(resultString)) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("FileUtil error!",
                    "getFileNodeHeadMd5 error! filePath is " + file.getAbsolutePath(), e);
        } finally {
            try {
                if (bufferedRandomAccessFile != null) {
                    bufferedRandomAccessFile.close();
                }
            } catch (IOException e) {
                LogGather.recordErrorLog("FileUtil error!",
                        "getFileNodeHeadMd5: BufferedReader close failed, filePath is "
                                + file.getAbsolutePath(),
                        e);
            }
        }
        // 如果第一行日志长度大于1k，截取1K做MD5,如果拿不到日志返回MD5获取失败标志
        if (StringUtils.isNotBlank(resultString)) {
            String Md5 = resultString.length() > LogConfigConstants.FILE_HEAD_LENGTH ? CommonUtils.getMd5(resultString.substring(0,
                    LogConfigConstants.FILE_HEAD_LENGTH)) : CommonUtils.getMd5(resultString);
            return StringUtils.isBlank(Md5) ? LogConfigConstants.MD5_FAILED_TAG : Md5;
        } else {
            return LogConfigConstants.MD5_FAILED_TAG;
        }
    }

    /**
     * 传入路径获取头部文件的MD5
     * @param path
     * @return
     */
    public static String getFileNodeHeadMd5(String path) {
        return getFileNodeHeadMd5(new File(path));
    }


    /**
     *
     * @param value input
     * @param delimiter 换行符
     * @param length 输入长度
     * @return 换行符位置, -1未找到
     */
    public static int getLineDelimiterIndex(byte[] value, byte[] delimiter, int length) {
        if(jdkVersionGe11) {
            try {
                return (int) indexOfMethod.invoke(null, value, length, delimiter, 1, 0);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            return indexOf(value, length, delimiter, 1, 0);
        }
    }

    public static int indexOf(byte[] value, int valueCount, byte[] str, int strCount, int fromIndex) {
        byte first = str[0];
        int max = (valueCount - strCount);
        for (int i = fromIndex; i <= max; i++) {
            // Look for first character.
            if (value[i] != first) {
                while (++i <= max && value[i] != first);
            }
            // Found first character, now look at the rest of value
            if (i <= max) {
                int j = i + 1;
                int end = j + strCount - 1;
                for (int k = 1; j < end && value[j] == str[k]; j++, k++);
                if (j == end) {
                    // Found whole string.
                    return i;
                }
            }
        }
        return -1;
    }

}
