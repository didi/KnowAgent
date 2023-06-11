package com.didichuxing.datachannel.agent.source.log.offset;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.source.log.beans.FileNode;

public class FileNodeInfoTest {

    private static final long   START_TIME     = 0L;
    private static final long   _ONE_HOUR      = 60 * 60 * 1000;
    private static final long   _HALF_ONE_HOUR = 30 * 60 * 1000;

    private static final long   _MODEL_ID      = 1;
    private static final long   _PATH_ID       = 1;

    private static final long   _1_INODE_ID    = 1;
    private static final long   _1_CREATE_TIME = START_TIME;
    private static final long   _1_OFFSET      = 111;
    private static long         _1_TIME_STAMP  = START_TIME;
    private static final String _1_FILE_KEY    = "1-fileKey";

    private static final long   _2_INODE_ID    = 2;
    private static final long   _2_CREATE_TIME = START_TIME + _ONE_HOUR;
    private static final long   _2_OFFSET      = 222;
    private static long         _2_TIME_STAMP  = START_TIME + _ONE_HOUR;
    private static final String _2_FILE_KEY    = "2-fileKey";

    private static final long   _3_INODE_ID    = 3;
    private static final long   _3_CREATE_TIME = START_TIME + 10 * _ONE_HOUR;
    private static final long   _3_OFFSET      = 333;
    private static long         _3_TIME_STAMP  = START_TIME + 11 * _ONE_HOUR;
    private static final String _3_FILE_KEY    = "3-fileKey";

    private static final long   _4_INODE_ID    = 4;
    private static final long   _4_CREATE_TIME = START_TIME + 11 * _ONE_HOUR;
    private static final long   _4_OFFSET      = 444;
    private static long         _4_TIME_STAMP  = START_TIME + 12 * _ONE_HOUR;
    private static final String _4_FILE_KEY    = "4-fileKey";

    private static final long   _5_INODE_ID    = 5;
    private static final long   _5_CREATE_TIME = START_TIME + 12 * _ONE_HOUR;
    private static final long   _5_OFFSET      = 555;
    private static long         _5_TIME_STAMP  = START_TIME + 15 * _ONE_HOUR;
    private static final String _5_FILE_KEY    = "5-fileKey";

    private static final long   _6_INODE_ID    = 6;
    private static final long   _6_CREATE_TIME = START_TIME + 15 * _ONE_HOUR;
    private static final long   _6_OFFSET      = 666;
    private static long         _6_TIME_STAMP  = START_TIME + 18 * _ONE_HOUR;
    private static final String _6_FILE_KEY    = "6-fileKey";

    private static String       masterPath     = "/home/xiaoju/test/test.log";

    private static final String NO_INODE       = "no_inode";

    // 所有offset都没有inode
    @Test
    public void no_inode() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        addNoInodeOffset(_1_OFFSET, _1_TIME_STAMP, NO_INODE + "1");
        addNoInodeOffset(_2_OFFSET, _2_TIME_STAMP, NO_INODE + "2");
        addNoInodeOffset(_3_OFFSET, _3_TIME_STAMP, NO_INODE + "3");
        addNoInodeOffset(_4_OFFSET, _4_TIME_STAMP, NO_INODE + "4");
        addNoInodeOffset(_5_OFFSET, _5_TIME_STAMP, NO_INODE + "5");
        addNoInodeOffset(_6_OFFSET, _6_TIME_STAMP, NO_INODE + "6");

        OffsetManager.flush();

        OffsetManager loader = new OffsetManager();
        loader.loadOffset(null);

        List<FileNode> fileNodes = new ArrayList<>();
        // 剔除1 ，2
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, START_TIME + 6 * _ONE_HOUR, null, null, 1111L,
                                   null));

        // 匹配3 4
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, START_TIME + 13 * _ONE_HOUR, null, null, 2222L,
                                   null));

        // 模拟没有offset对应的关系
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, START_TIME + 14 * _ONE_HOUR, null, null, 3333L,
                                   null));

        // 匹配5
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, START_TIME + 15 * _ONE_HOUR, null, null, 4444L,
                                   null));

        // 匹配6
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, START_TIME + 17 * _ONE_HOUR, null, null, 5555L,
                                   null));

        // 最新文件，没有匹配相关
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, START_TIME + 20 * _ONE_HOUR, null, null, 6666L,
                                   null));

        loader.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        System.out.println("hello world");

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        // 保证1 2 3被剔除
        for (FileNode fileNode : fileNodes) {
            System.out.println(fileNode);
        }
    }

    // offset存储为一定时间以前，晚于文件
    @Test
    public void no_inode1() throws Exception {
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig, null);

        addNoInodeOffset(_1_OFFSET, _1_TIME_STAMP, NO_INODE + "1");
        addNoInodeOffset(_2_OFFSET, _2_TIME_STAMP, NO_INODE + "2");
        addNoInodeOffset(_3_OFFSET, _3_TIME_STAMP, NO_INODE + "3");
        addNoInodeOffset(_4_OFFSET, _4_TIME_STAMP, NO_INODE + "4");
        addNoInodeOffset(_5_OFFSET, _5_TIME_STAMP, NO_INODE + "5");
        addNoInodeOffset(_6_OFFSET, _6_TIME_STAMP, NO_INODE + "6");

        OffsetManager.flush();

        OffsetManager loader = new OffsetManager();
        loader.loadOffset(null);

        List<FileNode> fileNodes = new ArrayList<>();
        // 剔除1 ，2
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, START_TIME + 6 * _ONE_HOUR, null, null, 1111L,
                                   null));

        // 匹配3 4
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, START_TIME + 13 * _ONE_HOUR, null, null, 2222L,
                                   null));

        // 模拟没有offset对应的关系
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, START_TIME + 14 * _ONE_HOUR, null, null, 3333L,
                                   null));

        // 匹配5
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, START_TIME + 15 * _ONE_HOUR, null, null, 4444L,
                                   null));

        // 匹配6
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, START_TIME + 17 * _ONE_HOUR, null, null, 5555L,
                                   null));

        // 最新文件，没有匹配相关
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, START_TIME + 20 * _ONE_HOUR, null, null, 6666L,
                                   null));

        loader.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        System.out.println("hello world");

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        // 保证1 2 3被剔除
        for (FileNode fileNode : fileNodes) {
            System.out.println(fileNode);
        }
    }

    /**
     * offset modify time与最新文件相匹配 但是 存在历史offset
     * 
     * @throws Exception
     */
    @Test
    public void no_inode2() throws Exception {
        long now = System.currentTimeMillis();
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.init(offsetConfig,null);

        addNoInodeOffset(_1_OFFSET, now - 1 * _ONE_HOUR, NO_INODE + "1");
        addNoInodeOffset(_2_OFFSET, now - 2 * _ONE_HOUR, NO_INODE + "2");
        addNoInodeOffset(_3_OFFSET, now - 3 * _ONE_HOUR, NO_INODE + "3");
        addNoInodeOffset(_4_OFFSET, now - 4 * _ONE_HOUR, NO_INODE + "4");
        addNoInodeOffset(_5_OFFSET, now - 18 * _ONE_HOUR, NO_INODE + "5");
        addNoInodeOffset(_6_OFFSET, now - 30 * _ONE_HOUR, NO_INODE + "6");

        List<FileNode> fileNodes = new ArrayList<>();
        // 匹配1
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, now - 1 * _ONE_HOUR, null, null, 1111L, null));

        // 匹配2
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, now - 2 * _ONE_HOUR, null, null, 2222L, null));

        // 匹配4
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, now - 3 * _ONE_HOUR, null, null, 3333L, null));

        // 匹配4
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, now - 4 * _ONE_HOUR, null, null, 4444L, null));

        // 匹配6
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, now - 5 * _ONE_HOUR, null, null, 5555L, null));

        // 无匹配，文件长度
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, now - 6 * _ONE_HOUR, null, null, 6666L, null));

        OffsetManager.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        for (FileNode fileNode : fileNodes) {
            System.out.println(fileNode);
        }

        int num = 0;
        // for (FileNode fileNode : fileNodes) {
        // if (fileNode.getFileKey().equals(_1_FILE_KEY)) {
        // if (!fileNode.getOffset().equals(_1_OFFSET)) {
        // num++;
        // }
        // } else if (fileNode.getFileKey().equals(_2_FILE_KEY)) {
        // if (!fileNode.getOffset().equals(0L)) {
        // num++;
        // }
        // } else if (fileNode.getFileKey().equals(_3_FILE_KEY)) {
        // if (!fileNode.getOffset().equals(_3_OFFSET)) {
        // num++;
        // }
        // } else if (fileNode.getFileKey().equals(_4_FILE_KEY)) {
        // if (!fileNode.getOffset().equals(_3_OFFSET)) {
        // num++;
        // }
        // } else if (fileNode.getFileKey().equals(_5_FILE_KEY)) {
        // if (!fileNode.getOffset().equals(_5_OFFSET)) {
        // num++;
        // }
        // } else if (fileNode.getFileKey().equals(_6_FILE_KEY)) {
        // if (!fileNode.getOffset().equals(6666L)) {
        // num++;
        // }
        // }
        // }

        System.out.println(num);
        assertTrue(num == 0);
    }

    /**
     * offset只存在1个，且晚于当前最新文件 但是 存在历史offset
     * 
     * @throws Exception
     */
    @Test
    public void no_inode3() throws Exception {
        long now = System.currentTimeMillis();
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.stop();
        FileUtils.delFile(new File(offsetConfig.getRootDir()));
        OffsetManager.init(offsetConfig,null);

        addNoInodeOffset(_1_OFFSET, now - 2 * _ONE_HOUR, NO_INODE + "1");

        List<FileNode> fileNodes = new ArrayList<>();
        // 从0开始
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, now - 1 * _ONE_HOUR, null, null, 1111L, null));

        // 匹配1
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, now - 2 * _ONE_HOUR, null, null, 2222L, null));

        // 文件长度
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, now - 3 * _ONE_HOUR, null, null, 3333L, null));

        // 文件长度
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, now - 5 * _ONE_HOUR, null, null, 4444L, null));

        // 文件长度
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, now - 20 * _ONE_HOUR, null, null, 5555L, null));

        // 文件长度
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, now - 31 * _ONE_HOUR, null, null, 6666L, null));

        OffsetManager.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        int num = 0;
        for (FileNode fileNode : fileNodes) {
            if (fileNode.getFileKey().equals(_1_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_2_FILE_KEY)) {
                if (!fileNode.getOffset().equals(_1_OFFSET)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_3_FILE_KEY)) {
                if (!fileNode.getOffset().equals(3333L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_4_FILE_KEY)) {
                if (!fileNode.getOffset().equals(4444L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_5_FILE_KEY)) {
                if (!fileNode.getOffset().equals(5555L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_6_FILE_KEY)) {
                if (!fileNode.getOffset().equals(6666L)) {
                    num++;
                }
            }
        }

        System.out.println(num);
        assertTrue(num == 0);
    }

    /**
     * offset只存在2个，1个为当前的offset,另一个为历史offset
     *
     * @throws Exception
     */
    @Test
    public void no_inode4() throws Exception {
        long now = System.currentTimeMillis();
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.stop();
        FileUtils.delFile(new File(offsetConfig.getRootDir()));
        OffsetManager.init(offsetConfig,null);

        addNoInodeOffset(_1_OFFSET, now - 1 * _ONE_HOUR - 1 * _HALF_ONE_HOUR, NO_INODE + "1");
        addNoInodeOffset(_2_OFFSET, now - 6 * _ONE_HOUR, NO_INODE + "2");

        List<FileNode> fileNodes = new ArrayList<>();
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, now - 1 * _ONE_HOUR, null, null, 1111L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, now - 2 * _ONE_HOUR, null, null, 2222L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, now - 3 * _ONE_HOUR, null, null, 3333L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, now - 5 * _ONE_HOUR, null, null, 4444L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, now - 20 * _ONE_HOUR, null, null, 5555L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, now - 31 * _ONE_HOUR, null, null, 6666L, null));

        OffsetManager.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        for (FileNode fileNode : fileNodes) {
            System.out.println(fileNode);
        }

        int num = 0;
        for (FileNode fileNode : fileNodes) {
            if (fileNode.getFileKey().equals(_1_FILE_KEY)) {
                if (!fileNode.getOffset().equals(_1_OFFSET)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_2_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_3_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_4_FILE_KEY)) {
                if (!fileNode.getOffset().equals(_2_OFFSET)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_5_FILE_KEY)) {
                if (!fileNode.getOffset().equals(5555L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_6_FILE_KEY)) {
                if (!fileNode.getOffset().equals(6666L)) {
                    num++;
                }
            }
        }

        System.out.println(num);
        assertTrue(num == 0);
    }

    /**
     * offset只存在2个，1个为当前的offset,另一个为历史的offset
     *
     * @throws Exception
     */
    @Test
    public void no_inode5() throws Exception {
        long now = System.currentTimeMillis();
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.stop();
        FileUtils.delFile(new File(offsetConfig.getRootDir()));
        OffsetManager.init(offsetConfig,null);

        addNoInodeOffset(_1_OFFSET, now - 1 * _HALF_ONE_HOUR, NO_INODE + "1");
        addNoInodeOffset(_2_OFFSET, now - 4 * _ONE_HOUR, NO_INODE + "2");

        List<FileNode> fileNodes = new ArrayList<>();
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, now - 1 * _ONE_HOUR, null, null, 1111L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, now - 2 * _ONE_HOUR, null, null, 2222L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, now - 3 * _ONE_HOUR, null, null, 3333L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, now - 5 * _ONE_HOUR, null, null, 4444L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, now - 20 * _ONE_HOUR, null, null, 5555L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, now - 31 * _ONE_HOUR, null, null, 6666L, null));

        OffsetManager.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        for (FileNode fileNode : fileNodes) {
            System.out.println(fileNode);
        }

        int num = 0;
        for (FileNode fileNode : fileNodes) {
            if (fileNode.getFileKey().equals(_1_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_2_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_3_FILE_KEY)) {
                if (!fileNode.getOffset().equals(_2_OFFSET)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_4_FILE_KEY)) {
                if (!fileNode.getOffset().equals(4444L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_5_FILE_KEY)) {
                if (!fileNode.getOffset().equals(5555L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_6_FILE_KEY)) {
                if (!fileNode.getOffset().equals(6666L)) {
                    num++;
                }
            }
        }

        System.out.println(num);
        assertTrue(num == 0);
    }

    /**
     * offset只存在3个，
     * 1为当前的offset,
     * 2为历史offset
     * 3为超时offset
     *
     * @throws Exception
     */
    @Test
    public void no_inode6() throws Exception {
        long now = System.currentTimeMillis();
        OffsetConfig offsetConfig = new OffsetConfig();
        OffsetManager.stop();
        FileUtils.delFile(new File(offsetConfig.getRootDir()));
        OffsetManager.init(offsetConfig,null);

        addNoInodeOffset(_1_OFFSET, now - 1 * _ONE_HOUR - 1 * _HALF_ONE_HOUR, NO_INODE + "1");
        addNoInodeOffset(_2_OFFSET, now - 6 * _ONE_HOUR, NO_INODE + "2");
        addNoInodeOffset(_3_OFFSET, now - 36 * _ONE_HOUR, NO_INODE + "3");

        List<FileNode> fileNodes = new ArrayList<>();
        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _1_FILE_KEY, now - 1 * _ONE_HOUR, null, null, 1111L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _2_FILE_KEY, now - 2 * _ONE_HOUR, null, null, 2222L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _3_FILE_KEY, now - 3 * _ONE_HOUR, null, null, 3333L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _4_FILE_KEY, now - 5 * _ONE_HOUR, null, null, 4444L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _5_FILE_KEY, now - 20 * _ONE_HOUR, null, null, 5555L, null));

        fileNodes.add(new FileNode(_MODEL_ID, _PATH_ID, _6_FILE_KEY, now - 31 * _ONE_HOUR, null, null, 6666L, null));

        OffsetManager.getOffsetInfo(_MODEL_ID, _PATH_ID, masterPath, fileNodes);

        // 保证递增
        for (int i = 1; i < fileNodes.size(); i++) {
            if (fileNodes.get(i).getModifyTime() < fileNodes.get(i - 1).getModifyTime()) {
                throw new Exception("not sorted");
            }
        }

        for (FileNode fileNode : fileNodes) {
            System.out.println(fileNode);
        }

        int num = 0;
        for (FileNode fileNode : fileNodes) {
            if (fileNode.getFileKey().equals(_1_FILE_KEY)) {
                if (!fileNode.getOffset().equals(_1_OFFSET)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_2_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_3_FILE_KEY)) {
                if (!fileNode.getOffset().equals(0L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_4_FILE_KEY)) {
                if (!fileNode.getOffset().equals(_2_OFFSET)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_5_FILE_KEY)) {
                if (!fileNode.getOffset().equals(5555L)) {
                    num++;
                }
            } else if (fileNode.getFileKey().equals(_6_FILE_KEY)) {
                if (!fileNode.getOffset().equals(6666L)) {
                    num++;
                }
            }
        }

        System.out.println(num);
        assertTrue(num == 0);
    }

    @Test
    public void new_offset() {

    }

    private void addNoInodeOffset(Long offset, Long timeStamp, String fileKey) {
        FileOffSet fileOffSet = OffsetManager.getFileOffset(_MODEL_ID, _PATH_ID, masterPath, null,
            fileKey, null);

        fileOffSet.setFileKey(fileKey);
        fileOffSet.setOffSet(offset);
        fileOffSet.setTimeStamp(timeStamp);
    }

    private void addOffset(OffsetManager offsetManager, String fileKey, Long offset, Long modifyTime) {
        FileOffSet fileOffSet = offsetManager.getFileOffset(_MODEL_ID, _PATH_ID, masterPath, null,
            fileKey, null);

        fileOffSet.setFileKey(fileKey);
        fileOffSet.setOffSet(offset);
        fileOffSet.setLastModifyTime(modifyTime);
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
}
