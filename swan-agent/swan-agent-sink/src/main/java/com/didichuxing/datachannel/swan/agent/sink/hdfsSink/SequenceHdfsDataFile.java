package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.util.ReflectionUtils;

import com.didichuxing.datachannel.swan.agent.common.api.HdfsCompression;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-31 11:29
 */
public class SequenceHdfsDataFile extends HdfsDataFile {

    private static final ILog   LOGGER      = LogFactory.getLog(HdfsDataFile.class.getName());

    private static final ILog   HDFS_LOGGER = LogFactory.getLog("hdfs-auth");

    private SequenceFile.Writer writer      = null;
    private Writable            key         = NullWritable.get();
    private BytesWritable       value       = new BytesWritable();

    public SequenceHdfsDataFile(HdfsSink hdfsSink, String hdfsPath, String fileName,
                                String sourceKey, HdfsFileSystem hdfsFileSystem, String codec) {
        super(hdfsSink, hdfsPath, fileName, sourceKey, hdfsFileSystem, codec);
    }

    @Override
    public boolean init() throws Exception {
        String remoteFile = hdfsPath + fileName;
        LOGGER.info("begin to create sequence hdfs file" + remoteFile + ",codec is " + this.codec);
        stream = this.hdfsSink.getHdfsFileSystem().getFileSystem()
            .create(new Path(remoteFile), false, 4096);
        Configuration conf = new Configuration();
        String codecClassName = getSfCompressionType(this.codec);
        CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(
            Class.forName(codecClassName), conf);
        writer = SequenceFile.createWriter(conf, SequenceFile.Writer.stream(stream),
            SequenceFile.Writer.keyClass(NullWritable.class),
            SequenceFile.Writer.valueClass(BytesWritable.class),
            SequenceFile.Writer.compression(SequenceFile.CompressionType.BLOCK, codec));
        closed = false;
        hdfsFileSystem.appendHdfsDataFile(this);
        LOGGER.info("create sequence hdfs file" + remoteFile + " success!");
        return true;
    }

    private static String getSfCompressionType(String type) {
        if (StringUtils.isBlank(type)) {
            return "org.apache.hadoop.io.compress.DefaultCodec";
        }

        if (type.equals(HdfsCompression.SEQ_LZ4.getComment())) {
            return "org.apache.hadoop.io.compress.Lz4Codec";
        } else if (type.equals(HdfsCompression.SEQ_SNAPPY.getComment())) {
            return "org.apache.hadoop.io.compress.SnappyCodec";
        } else {
            return "org.apache.hadoop.io.compress.DefaultCodec";
        }
    }

    @Override
    public boolean reBuildStreamWithOutLock() {
        HDFS_LOGGER.info("begin to reBuild hdfs file's stream. uniquekey is " + uniqueKey);
        try {
            this.writer.close();
            this.stream.close();
        } catch (Throwable e) {
            HDFS_LOGGER.error(
                "SequenceHdfsDataFile error close stream error when rebuild stream! uniquekey is "
                        + uniqueKey, e);
        }

        try {
            String remoteFile = hdfsPath + fileName;
            FileSystem fileSystem = this.hdfsSink.getHdfsFileSystem().getFileSystem();
            // 覆盖租约
            ((DistributedFileSystem) fileSystem).recoverLease(new Path(remoteFile));
            stream = fileSystem.append(new Path(remoteFile), 4096);
            Configuration conf = new Configuration();
            String codecClassName = getSfCompressionType(this.codec);
            CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(
                Class.forName(codecClassName), conf);
            writer = SequenceFile.createWriter(conf, SequenceFile.Writer.stream(stream),
                SequenceFile.Writer.keyClass(NullWritable.class),
                SequenceFile.Writer.valueClass(BytesWritable.class),
                SequenceFile.Writer.compression(SequenceFile.CompressionType.BLOCK, codec));
            closed = false;
            HDFS_LOGGER.info("reBuild hdfs file's stream success. uniquekey is " + uniqueKey);
            return true;
        } catch (Throwable e) {
            HDFS_LOGGER.error(
                "SequenceHdfsDataFile error. reBuild hdfs file's stream error! uniquekey is "
                        + uniqueKey, e);
        }
        return false;
    }

    /**
     * 异常重建
     * @return
     * @throws Exception
     */
    @Override
    public synchronized boolean reBuild() throws Exception {
        LOGGER.info("begin to reBuild sequence hdfs file. uniquekey is " + uniqueKey);
        sync();
        return close();
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws Exception {
        value.set(b, 0, b.length);
        writer.append(key, value);
    }

    /**
     * 将本地数据刷新到各个DataNode的内存中
     */
    @Override
    public synchronized boolean flush() {
        if (closed) {
            return true;
        }

        try {
            writer.hflush();
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("SequenceHdfsDataFile error", "flush file error! hdfsPath is "
                                                                   + hdfsPath + ", fileName is "
                                                                   + fileName, e);
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
    @Override
    public synchronized boolean sync() {
        if (closed) {
            return true;
        }

        try {
            writer.sync();// 这一步把block缓存数据写入流缓存中
            writer.hsync();// 这一步把流中数据落盘到Datanode中
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("SequenceHdfsDataFile error", "sync file error! hdfsPath is "
                                                                   + hdfsPath + ", fileName is "
                                                                   + fileName, e);
            boolean isStreamClosed = HdfsStreamClosedChecker.invokeCheckClosed(
                (DFSOutputStream) stream.getWrappedStream(), hdfsPath + fileName);
            if (isStreamClosed) {
                reBuildStreamWithOutLock();
            }
            return false;
        }
    }

    @Override
    public synchronized boolean close() {
        LOGGER.info("begin to close sequence hdfs DateFile. hdfsPath is " + hdfsPath
                    + ", fileName is " + fileName);
        if (closed) {
            LOGGER.info("hdfsDataFile has closed already. unique key is " + uniqueKey);
        }
        try {
            writer.close();
            stream.close();
            LOGGER.info("close sequence hdfs datafile success. hdfsPath is " + hdfsPath);
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("SequenceHdfsDataFile error", "close file error! hdfsPath is "
                                                                   + hdfsPath + ", fileName is "
                                                                   + fileName, e);
            return false;
        } finally {
            hdfsFileSystem.removeHdfsDataFile(this);
            closed = true;
        }
    }

}
