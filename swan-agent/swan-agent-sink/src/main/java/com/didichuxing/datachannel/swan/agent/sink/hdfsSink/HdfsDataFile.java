package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import java.io.IOException;
import java.util.EnumSet;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.xerial.snappy.SnappyOutputStream;

import com.didichuxing.datachannel.swan.agent.common.api.HdfsCompression;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

import net.jpountz.lz4.LZ4BlockOutputStream;

/**
 * @description: hdfs date file对应hdfs的文件
 * @author: huangjw
 * @Date: 2019-07-11 14:50
 */
public class HdfsDataFile {

    private static final ILog    LOGGER      = LogFactory.getLog(HdfsDataFile.class.getName());

    private static final ILog    HDFS_LOGGER = LogFactory.getLog("hdfs-auth");

    // HdfsDataFile
    protected String             uniqueKey;

    // 文件名
    protected String             fileName;

    // hdfs文件路径
    protected String             hdfsPath;

    protected FSDataOutputStream stream;
    protected HdfsSink           hdfsSink;
    protected HdfsFileSystem     hdfsFileSystem;
    protected String             codec;

    protected volatile boolean   closed      = false;

    public HdfsDataFile(HdfsSink hdfsSink, String hdfsPath, String fileName, String sourceKey,
                        HdfsFileSystem hdfsFileSystem, String codec) {
        this.hdfsSink = hdfsSink;
        this.hdfsPath = hdfsPath;
        this.fileName = fileName;
        this.hdfsFileSystem = hdfsFileSystem;
        this.codec = codec;
        setUniqueKey(hdfsPath + fileName + sourceKey);
    }

    public boolean init() throws Exception {
        String remoteFile = hdfsPath + fileName;
        LOGGER.info("begin to create hdfs file" + remoteFile + ",codec is " + codec);
        FSDataOutputStream outputStream = this.hdfsSink.getHdfsFileSystem().getFileSystem()
            .create(new Path(remoteFile), false, 4096);
        stream = getOutputStream(outputStream);
        closed = false;
        hdfsFileSystem.appendHdfsDataFile(this);
        LOGGER.info("create hdfs file" + remoteFile + " success!");
        return true;
    }

    private FSDataOutputStream getOutputStream(FSDataOutputStream outputStream) {
        if (StringUtils.isBlank(this.codec)) {
            return outputStream;
        }
        try {
            if (this.codec.equals(HdfsCompression.TEXT.getComment())) {
                return outputStream;
            } else if (this.codec.equals(HdfsCompression.SNAPPY.getComment())) {
                return new FSDataOutputStream(new SnappyOutputStream(outputStream), null);
            } else if (this.codec.equals(HdfsCompression.LZ4.getComment())) {
                return new FSDataOutputStream(new LZ4BlockOutputStream(outputStream), null);
            } else if (this.codec.equals(HdfsCompression.GZIP.getComment())) {
                return new FSDataOutputStream(new GZIPOutputStream(outputStream), null);
            } else {
                return outputStream;
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("HDFSDataFile error",
                "getOutputStream error! hdfsPath is " + hdfsPath + ", fileName is " + fileName, e);
        }
        return outputStream;
    }

    /**
     * 异常重建
     * @return
     * @throws Exception
     */
    public synchronized boolean reBuild() throws Exception {
        HDFS_LOGGER.info("begin to reBuild hdfs file. uniquekey is " + uniqueKey);
        sync();
        return close();
    }

    /**
     * 重建连接
     * @return
     * @throws Exception
     */
    public synchronized boolean reBuildStream() {
        return reBuildStreamWithOutLock();
    }

    public boolean reBuildStreamWithOutLock() {
        HDFS_LOGGER.info("begin to reBuild hdfs file's stream. uniquekey is " + uniqueKey);
        try {
            this.stream.close();
        } catch (Throwable e) {
            HDFS_LOGGER.error(
                "HDFSDataFile error! close stream error when rebuild stream! uniquekey is "
                        + uniqueKey, e);
        }

        try {
            String remoteFile = hdfsPath + fileName;
            FileSystem fileSystem = this.hdfsSink.getHdfsFileSystem().getFileSystem();
            // 覆盖租约
            ((DistributedFileSystem) fileSystem).recoverLease(new Path(remoteFile));
            FSDataOutputStream outputStream = fileSystem.append(new Path(remoteFile), 4096);
            stream = getOutputStream(outputStream);
            HDFS_LOGGER.info("reBuild hdfs file's stream success. uniquekey is " + uniqueKey);
            return true;
        } catch (Throwable e) {
            HDFS_LOGGER.error("HDFSDataFile error! reBuild hdfs file's stream error! uniquekey is "
                              + uniqueKey, e);
        }
        return false;
    }

    public synchronized void write(byte[] b, int off, int len) throws Exception {
        stream.write(b, off, len);
    }

    /**
     * 将本地数据刷新到各个DataNode的内存中
     */
    public synchronized boolean flush() {
        if (closed) {
            return true;
        }

        try {
            stream.hflush();
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HDFSDataFile error",
                "flush file error! hdfsPath is " + hdfsPath + ", fileName is " + fileName, e);
            boolean isStreamClosed = HdfsStreamClosedChecker.invokeCheckClosed(
                (DFSOutputStream) stream.getWrappedStream(), hdfsPath + fileName);
            if (isStreamClosed) {
                reBuildStreamWithOutLock();
            }
            return false;
        }
    }

    /**
     * 将本地数据刷新到各个DataNode的磁盘中
     */
    public synchronized boolean sync() {
        if (closed) {
            return true;
        }

        try {
            if (stream instanceof HdfsDataOutputStream) {
                ((HdfsDataOutputStream) stream).hsync(EnumSet
                    .of(HdfsDataOutputStream.SyncFlag.UPDATE_LENGTH));
            } else {
                stream.hsync();
            }
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HDFSDataFile error",
                "sync file error! hdfsPath is " + hdfsPath + ", fileName is " + fileName, e);
            boolean isStreamClosed = HdfsStreamClosedChecker.invokeCheckClosed(
                (DFSOutputStream) stream.getWrappedStream(), hdfsPath + fileName);
            if (isStreamClosed) {
                reBuildStreamWithOutLock();
            }
            return false;
        }
    }

    public synchronized boolean close() {
        LOGGER.info("begin to close hdfs DateFile. hdfsPath is " + hdfsPath + ", fileName is "
                    + fileName);
        if (closed) {
            LOGGER.info("hdfsDataFile has closed already. unique key is " + uniqueKey);
        }
        try {
            try {
                if (stream instanceof HdfsDataOutputStream) {
                    ((HdfsDataOutputStream) stream).hsync(EnumSet
                        .of(HdfsDataOutputStream.SyncFlag.UPDATE_LENGTH));
                } else {
                    stream.hsync();
                }
            } catch (IOException e) {
                LOGGER.warn("stream is already closed. ingore", e);
            }

            stream.close();
            LOGGER.info("close hdfs datafile success. hdfsPath is " + hdfsPath);
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HDFSDataFile error",
                "close file error! hdfsPath is " + hdfsPath + ", fileName is " + fileName, e);
            return false;
        } finally {
            hdfsFileSystem.removeHdfsDataFile(this);
            closed = true;
        }
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getRemoteFilePath() {
        return this.hdfsPath + this.fileName;
    }
}
