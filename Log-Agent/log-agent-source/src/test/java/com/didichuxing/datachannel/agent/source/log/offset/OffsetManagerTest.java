package com.didichuxing.datachannel.agent.source.log.offset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Ignore;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

public class OffsetManagerTest {

    private static final long   _1_MODEL_ID      = 1;
    private static final long   _1_PATH_ID       = 11;
    private static final long   _1_1_INODE_ID    = 111;
    private static final long   _1_1_CREATE_TIME = 1111;
    private static final long   _1_1_OFFSET      = 11111;
    private static final long   _1_2_INODE_ID    = 112;
    private static final long   _1_2_CREATE_TIME = 1122;
    private static final long   _1_2_OFFSET      = 11222;
    private static final String _1_1_FILEKEY     = "111111";
    private static final String _1_2_FILEKEY     = "111222";

    private static final long   _2_MODEL_ID      = 2;
    private static final long   _2_PATH_ID       = 22;
    private static final long   _2_1_INODE_ID    = 221;
    private static final long   _2_1_CREATE_TIME = 2211;
    private static final long   _2_1_OFFSET      = 22111;
    private static final long   _2_2_INODE_ID    = 222;
    private static final long   _2_2_CREATE_TIME = 2222;
    private static final long   _2_2_OFFSET      = 22222;
    private static final String _2_1_FILEKEY     = "222111";
    private static final String _2_2_FILEKEY     = "222222";

    private static final String DEFAULT_PATH     = System.getProperty("user.home") + File.separator
                                                   + ".logOffSet";
    private static final String TEST_PATH        = System.getProperty("user.home") + File.separator
                                                   + ".test-logOffSet";

    @Test
    public void offsetAdd() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        check(m1.get(_1_1_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_1_FILEKEY, null);
        check(m1.get(_1_2_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_2_FILEKEY, null);

        assertEquals(m2.size(), 2);
        check(m2.get(_2_1_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_1_FILEKEY, null);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);
    }

    @Test
    public void offsetDel() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.stop();
        FileUtils.delFile(new File(offsetConfig.getRootDir()));
        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        // 删除文件 位点信息
        OffsetManager.removeLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null, null);
        OffsetManager.removeFileOffset(_2_MODEL_ID, _2_PATH_ID, null, _2_1_FILEKEY);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertTrue(m1.size() == 0);
        assertEquals(m2.get(_2_1_FILEKEY), null);
        assertEquals(m2.size(), 1);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);
    }

    @Ignore
    @Test
    public void old_version() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.stop();
        FileUtils.delFile(new File(offsetConfig.getRootDir()));
        OffsetManager.init(offsetConfig, null);

        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY, null)
            .setFileKey(null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY, null)
            .setFileKey(null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY, null)
            .setFileKey(null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY, null)
            .setFileKey(null);

        OffsetManager.flush();

        // 检查文件是否ok
        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        OffsetManager.loadOffset(null);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        assertEquals(m1.containsKey("no_inode0"), true);
        assertEquals(m1.containsKey("no_inode1"), true);

        assertEquals(m2.containsKey("no_inode0"), true);
        assertEquals(m2.containsKey("no_inode1"), true);
    }

    @Test
    public void flush_no_dir_no_tmp_no_del() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        OffsetManager.flush();

        // 检查文件是否ok
        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        OffsetManager.loadOffset(null);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        check(m1.get(_1_1_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_1_FILEKEY, null);
        check(m1.get(_1_2_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_2_FILEKEY, null);

        assertEquals(m2.size(), 2);
        check(m2.get(_2_1_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_1_FILEKEY, null);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);
    }

    @Test
    public void flush_have_dir_no_tmp_no_del() throws Exception {
        new File(DEFAULT_PATH).mkdir();

        flush_no_dir_no_tmp_no_del();
    }

    @Test
    public void flush_no_dir_have_tmp_no_del() throws Exception {
        new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).mkdir();

        flush_no_dir_no_tmp_no_del();
    }

    @Test
    public void flush_no_dir_no_tmp_have_del() throws Exception {
        new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).mkdir();

        flush_no_dir_no_tmp_no_del();
    }

    @Test
    public void flush_have_dir_have_tmp_have_del() throws Exception {
        new File(DEFAULT_PATH).mkdir();
        new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).mkdir();
        new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).mkdir();

        flush_no_dir_no_tmp_no_del();
    }

    @Test
    public void flush_old_and_new_path() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(TEST_PATH);

        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        OffsetManager.flush();

        // 检查文件是否ok
        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        // 检查文件是否ok
        if (!new File(TEST_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(TEST_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(TEST_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        OffsetManager.loadOffset(null);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        check(m1.get(_1_1_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_1_FILEKEY, null);
        check(m1.get(_1_2_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_2_FILEKEY, null);

        assertEquals(m2.size(), 2);
        check(m2.get(_2_1_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_1_FILEKEY, null);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);

        OffsetManager.loadOffset(null);

        m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        check(m1.get(_1_1_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_1_FILEKEY, null);
        check(m1.get(_1_2_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_2_FILEKEY, null);

        assertEquals(m2.size(), 2);
        check(m2.get(_2_1_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_1_FILEKEY, null);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);

    }

    @Test
    public void load_empty_dir_no_tmp() {
        OffsetManager.stop();
        FileUtils.delFile(new File(DEFAULT_PATH));
        FileUtils.delFile(new File(OffsetManager.TMP_SUFFIX));
        FileUtils.delFile(new File(OffsetManager.DEL_SUFFIX));

        new File(DEFAULT_PATH).mkdir();

        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);
        OffsetManager.loadOffset(null);

        assertTrue(OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null).size() == 0);
        assertTrue(OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null).size() == 0);
    }

    @Test
    public void load_no_dir_no_tmp() {
        OffsetManager.stop();
        FileUtils.delFile(new File(DEFAULT_PATH));
        FileUtils.delFile(new File(OffsetManager.TMP_SUFFIX));
        FileUtils.delFile(new File(OffsetManager.DEL_SUFFIX));

        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);
        OffsetManager.loadOffset(null);

        assertTrue(OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null).size() == 0);
        assertTrue(OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null).size() == 0);
    }

    @Test
    public void load_have_dir_no_tmp() throws Exception {
        OffsetManager.stop();
        FileUtils.delFile(new File(DEFAULT_PATH));

        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        OffsetManager.flush();

        // 检查文件是否ok
        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        OffsetManager.init(offsetConfig, null);
        OffsetManager.loadOffset(null);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        check(m1.get(_1_1_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_1_FILEKEY, null);
        check(m1.get(_1_2_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_2_FILEKEY, null);

        assertEquals(m2.size(), 2);
        check(m2.get(_2_1_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_1_FILEKEY, null);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);
    }

    @Test
    public void load_no_dir_have_tmp() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        OffsetManager.flush();

        // 检查文件是否ok
        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        new File(DEFAULT_PATH).renameTo(new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX));

        OffsetManager.init(offsetConfig, null);
        OffsetManager.loadOffset(null);

        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        check(m1.get(_1_1_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_1_FILEKEY, null);
        check(m1.get(_1_2_FILEKEY), _1_MODEL_ID, _1_PATH_ID, _1_2_FILEKEY, null);

        assertEquals(m2.size(), 2);
        check(m2.get(_2_1_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_1_FILEKEY, null);
        check(m2.get(_2_2_FILEKEY), _2_MODEL_ID, _2_PATH_ID, _2_2_FILEKEY, null);
    }

    @Test
    public void load_have_dir_have_tmp() throws Exception {
        OffsetManager.stop();
        FileUtils.delFile(new File(DEFAULT_PATH));
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        List<FileNode> fileNodeList = new ArrayList<>();
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_1_FILEKEY,null);
        OffsetManager.getFileOffset(_1_MODEL_ID, _1_PATH_ID, null, null, _1_2_FILEKEY,null);

        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_1_FILEKEY,null);
        OffsetManager.getFileOffset(_2_MODEL_ID, _2_PATH_ID, null, null, _2_2_FILEKEY,null);

        OffsetManager.flush();

        // 检查文件是否ok
        if (!new File(DEFAULT_PATH).exists()) {
            throw new Exception("file not exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.DEL_SUFFIX).exists()) {
            throw new Exception("del file exist");
        }

        if (new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX).exists()) {
            throw new Exception("tmp file exist");
        }

        new File(DEFAULT_PATH).renameTo(new File(DEFAULT_PATH + OffsetManager.TMP_SUFFIX));
        new File(DEFAULT_PATH).mkdir();

        OffsetManager.loadOffset(null);

        Map<String, FileOffSet> m1 = OffsetManager.getLogModel4Path(_1_MODEL_ID, _1_PATH_ID, null);
        Map<String, FileOffSet> m2 = OffsetManager.getLogModel4Path(_2_MODEL_ID, _2_PATH_ID, null);

        assertEquals(m1.size(), 2);
        assertEquals(m2.size(), 2);
    }

    public void check(FileOffSet fileOffSet, Long modelId, Long pathId, String fileKey, Long offset)
                                                                                                    throws Exception {
        if (fileOffSet == null) {
            throw new Exception("file offset not exist");
        }

        if (!fileOffSet.getLogID().equals(modelId)) {
            throw new Exception("not equal");
        }

        if (!fileOffSet.getPathID().equals(pathId)) {
            throw new Exception("not equal");
        }

        if (!fileOffSet.getFileKey().equals(fileKey)) {
            throw new Exception("not equal");
        }

        if (offset != null) {
            if (!fileOffSet.getOffSet().equals(offset)) {
                throw new Exception("not equal");
            }
        }
    }

    /**
     * old -> new
     *
     * @throws Exception
     */
    @Test
    public void testLoadOffset1() throws Exception {
        String rootPath = "/tmp/tmp/offset/";
        File dir = new File(rootPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(rootPath);
        OffsetManager.init(offsetConfig, null);
        Map<Long, List<LogPath>> logPathMap = buildLogPathMap();

        for (Long logId : logPathMap.keySet()) {
            List<String> contents = new ArrayList<>();
            List<LogPath> logPaths = logPathMap.get(logId);
            for (LogPath logPath : logPaths) {
                String filePath = FileUtils.getPathDir(logPath.getRealPath());
                String fileName = FileUtils.getMasterFile(logPath.getRealPath());
                String content1 = buildOldContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  null);
                String content2 = buildOldContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  System.currentTimeMillis());
                contents.add(content1);
                contents.add(content2);
            }
            String fileName = logId + "";
            FileUtils.writeFileContent(new File(rootPath + fileName), contents);
        }

        OffsetManager.loadOffset(logPathMap);

        for (Long logId : logPathMap.keySet()) {
            FileUtils.delFile(new File(rootPath + logId));
        }
    }

    /**
     * new -> new
     * @throws Exception
     */
    @Test
    public void testLoadOffset2() throws Exception {
        String rootPath = "/tmp/tmp/offset/";
        File dir = new File(rootPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(rootPath);
        OffsetManager.init(offsetConfig, null);
        Map<Long, List<LogPath>> logPathMap = buildLogPathMap();

        List<String> fileNames = new ArrayList<>();
        for (Long logId : logPathMap.keySet()) {
            List<LogPath> logPaths = logPathMap.get(logId);
            for (LogPath logPath : logPaths) {
                List<String> contents = new ArrayList<>();
                String filePath = FileUtils.getPathDir(logPath.getRealPath());
                String fileName = FileUtils.getMasterFile(logPath.getRealPath());
                String content1 = buildNewContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  null);
                String content2 = buildNewContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  System.currentTimeMillis());
                contents.add(content1);
                contents.add(content2);

                String file = OffsetManager.getLogMod4PathKey(logId, logPath.getPathId(), logPath.getRealPath());
                FileUtils.writeFileContent(new File(rootPath + file), contents);
                fileNames.add(file);
            }
        }

        OffsetManager.loadOffset(logPathMap);

        for (String file : fileNames) {
            FileUtils.delFile(new File(rootPath + file));
        }
    }

    /**
     * old,new 并存
     * @throws Exception
     */
    @Test
    public void testLoadOffset3() throws Exception {
        String rootPath = "/tmp/tmp/offset/";
        File dir = new File(rootPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(rootPath);
        OffsetManager.init(offsetConfig, null);
        Map<Long, List<LogPath>> logPathMap = buildLogPathMap();
        List<String> fileNames = new ArrayList<>();

        // old
        for (Long logId : logPathMap.keySet()) {
            List<String> contents = new ArrayList<>();
            List<LogPath> logPaths = logPathMap.get(logId);
            for (LogPath logPath : logPaths) {
                String filePath = FileUtils.getPathDir(logPath.getRealPath());
                String fileName = FileUtils.getMasterFile(logPath.getRealPath());
                String content1 = buildOldContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  null);
                String content2 = buildOldContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  System.currentTimeMillis());
                contents.add(content1);
                contents.add(content2);
            }
            String fileName = logId + "";
            fileNames.add(fileName);
            FileUtils.writeFileContent(new File(rootPath + fileName), contents);
        }

        // new
        for (Long logId : logPathMap.keySet()) {
            List<LogPath> logPaths = logPathMap.get(logId);
            for (LogPath logPath : logPaths) {
                List<String> contents = new ArrayList<>();
                String filePath = FileUtils.getPathDir(logPath.getRealPath());
                String fileName = FileUtils.getMasterFile(logPath.getRealPath());
                String content1 = buildNewContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  null);
                String content2 = buildNewContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  System.currentTimeMillis());
                contents.add(content1);
                contents.add(content2);

                String file = OffsetManager.getLogMod4PathKey(logId, logPath.getPathId(), logPath.getRealPath());
                FileUtils.writeFileContent(new File(rootPath + file), contents);
                fileNames.add(file);
            }
        }

        OffsetManager.loadOffset(logPathMap);

        for (Long logId : logPathMap.keySet()) {
            FileUtils.delFile(new File(rootPath + logId));
        }

    }

    @Test
    public void copyOffsetBetweenLogModeTest() throws Exception {
        String rootPath = "/tmp/tmp/offset/";
        File dir = new File(rootPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(rootPath);
        OffsetManager.init(offsetConfig, null);
        Map<Long, List<LogPath>> logPathMap = buildLogPathMap();
        loadOffset(rootPath, logPathMap);

        Long sourceLogModeId1 = 10000L;
        Long sourcePathId1 = 11L;
        LogPath targetLogPath1 = getLogPath1();

        Long sourceLogModeId2 = 20000L;
        Long sourcePathId2 = 21L;
        LogPath targetLogPath2 = getLogPath2();

        // 物理机->物理机
        OffsetManager.copyOffsetBetweenLogModeId("", targetLogPath1, sourceLogModeId1);
        Map<String, FileOffSet> targetOffsetMap1 = OffsetManager.getLogModel4Path(
            targetLogPath1.getLogModelId(), targetLogPath1.getPathId(), targetLogPath1.getPath());
        Map<String, FileOffSet> sourceOffsetMap1 = OffsetManager.getLogModel4Path(sourceLogModeId1,
            sourcePathId1, targetLogPath1.getPath());
        assertTrue(compareOffset(targetOffsetMap1, sourceOffsetMap1));

        // 弹性云宿主->弹性云宿主
        OffsetManager.copyOffsetBetweenLogModeId(CommonUtils.getHOSTNAME(), targetLogPath2,
            sourceLogModeId2);

    }

    @Test
    public void testOffsetWriteAndRead() throws Exception {
        String rootPath = "/tmp/tmp/offset/";
        File dir = new File(rootPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(rootPath);
        OffsetManager.init(offsetConfig, null);
        Map<Long, List<LogPath>> logPathMap = buildLogPathMap();
        loadOffset(rootPath, logPathMap);

        OffsetManager.loadOffset(null);
    }

    private boolean compareOffset(Map<String, FileOffSet> sourceOffsetMap,
                                  Map<String, FileOffSet> targetOffsetMap) {
        if (sourceOffsetMap.size() != targetOffsetMap.size()) {
            return false;
        }
        for (Map.Entry<String, FileOffSet> entry : sourceOffsetMap.entrySet()) {
            FileOffSet sourceOffset = entry.getValue();
            FileOffSet targetOffset = targetOffsetMap.get(entry.getKey());
            if (targetOffset.getLogID().equals(sourceOffset.getLogID())
                || targetOffset.getPathID().equals(sourceOffset.getPathID())) {
                return false;
            }
            if (!targetOffset.getParentPath().equals(sourceOffset.getParentPath())
                || !targetOffset.getFileKey().equals(sourceOffset.getFileKey())
                || !targetOffset.getOffSet().equals(sourceOffset.getOffSet())
                || !targetOffset.getTimeStamp().equals(sourceOffset.getTimeStamp())
                || !targetOffset.getFileName().equals(targetOffset.getFileName())
                || !targetOffset.getLastModifyTime().equals(sourceOffset.getLastModifyTime())) {
                return false;
            }
        }
        return true;
    }

    private void loadOffset(String rootPath, Map<Long, List<LogPath>> logPathMap) throws Exception {
        for (Long logId : logPathMap.keySet()) {
            List<LogPath> logPaths = logPathMap.get(logId);
            for (LogPath logPath : logPaths) {
                List<String> contents = new ArrayList<>();
                String filePath = FileUtils.getPathDir(logPath.getRealPath());
                String fileName = FileUtils.getMasterFile(logPath.getRealPath());
                String content1 = buildNewContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  null);
                String content2 = buildNewContent(logId.toString(), logPath.getPathId().toString(), filePath, fileName,
                                                  System.currentTimeMillis());
                contents.add(content1);
                contents.add(content2);

                String file;
                if (logId == 10000) {
                    file = OffsetManager.getLogMod4PathKey(logId, logPath.getPathId(), logPath.getRealPath());
                } else {
                    throw new RuntimeException("ddcloud not support");
                }
                FileUtils.writeFileContent(new File(rootPath + file), contents);
            }
        }
    }

    private LogPath getLogPath1() {
        Long logModelId1 = 30000L;
        Long pathId31 = 31L;
        String path11 = "/home/xiaoju/path1/path1.log";
        LogPath logPath11 = new LogPath(logModelId1, pathId31, path11);
        return logPath11;
    }

    private LogPath getLogPath2() {
        Long logModelId1 = 30000L;
        Long pathId31 = 31L;
        String path11 = "/home/xiaoju/path2/path1.log";
        String dockerPath = "/docker/test" + path11;
        LogPath logPath11 = new LogPath(logModelId1, pathId31, path11);
        logPath11.setDockerPath(dockerPath);
        return logPath11;
    }

    private Map<Long, List<LogPath>> buildLogPathMap() {
        Map<Long, List<LogPath>> map = new HashMap<>();

        Long logModelId1 = 10000L;
        Long pathId11 = 11L;
        String path11 = "/home/xiaoju/path1/path1.log";
        Long pathId12 = 12L;
        String path12 = "/home/xiaoju/path1/path2.log";
        LogPath logPath11 = new LogPath(logModelId1, pathId11, path11);
        LogPath logPath12 = new LogPath(logModelId1, pathId12, path12);
        List<LogPath> list1 = new ArrayList<>();
        list1.add(logPath11);
        list1.add(logPath12);

        Long logModelId2 = 20000L;
        Long pathId21 = 21L;
        String path21 = "/home/xiaoju/path2/path1.log";
        String dockerPath21 = "/docker/test" + path21;
        Long pathId22 = 22L;
        String path22 = "/home/xiaoju/path2/path2.log";
        String dockerPath22 = "/docker/test" + path22;
        LogPath logPath21 = new LogPath(logModelId2, pathId21, path21);
        logPath21.setDockerPath(dockerPath21);
        LogPath logPath22 = new LogPath(logModelId2, pathId22, path22);
        logPath22.setDockerPath(dockerPath22);
        List<LogPath> list2 = new ArrayList<>();
        list2.add(logPath21);
        list2.add(logPath22);

        map.put(logModelId1, list1);
        map.put(logModelId2, list2);

        return map;
    }

    private String buildOldContent(String logId, String pathId, String path, String fileName,
                                   Long time) {
        String result = "" + logId + "_" + pathId + "_" + time + "###{\"fileName\":\"" + fileName
                        + "\",\"lastModifyTime\":" + time + ",\"logID\":" + logId
                        + ",\"offSet\":621426455,\"parentPath\":\"" + path + "\",\"pathID\":"
                        + pathId + ",\"timeStamp\":1537236601087}";
        return result;
    }

    private String buildNewContent(String logId, String pathId, String path, String fileName,
                                   Long time) {
        if (time == null) {
            time = 0L;
        }
        String result = "" + logId + "_" + pathId + "_" + time
                        + "###{\"timeStamp\":0,\"fileName\":\"" + fileName
                        + "\",\"offSet\":0,\"parentPath\":\"" + path + "\",\"lastModifyTime\":"
                        + time + ",\"logId\":" + logId + ",\"fileKey\":\"2054_2147641007 " + time
                        + "\",\"pathId\":" + pathId + "}";
        return result;
    }

    @Test
    public void loadTest() {
        String path = "/Users/user/package/offset/";
        OffsetConfig offsetConfig = new OffsetConfig();
        offsetConfig.setRootDir(path);
        OffsetManager.init(offsetConfig, null);
    }

    @Test
    public void checkFileKeyExistTest(){
        Long logModelId1 = 10000L;
        Long pathId11 = 11L;
        String path11 = "/home/xiaoju/path1/path1.log";
        String fileKey = "12345_6789";
        LogPath logPath = new LogPath(logModelId1,pathId11,path11);
        Map<String, ConcurrentHashMap<String, FileOffSet>> map1 = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, FileOffSet> map2 = new ConcurrentHashMap<>();

        String k1 = fileKey;
        FileOffSet fileOffSet = new FileOffSet(logModelId1,pathId11,path11,fileKey);
        map2.put(k1,fileOffSet);
        String key1 = OffsetManager.getLogMod4PathKey(logModelId1,pathId11,path11);
        map1.put(key1,map2);

        //OffsetManager.offsetMap.putAll(map1);

        boolean result1 = OffsetManager.checkFileHeadMd5(key1,new File(path11),fileKey);
        System.out.println(result1);


        Boolean result = OffsetManager.checkFileKeyExist(null,logPath,fileKey);
        System.out.println(result);
        assertTrue(result==true);
    }
}
