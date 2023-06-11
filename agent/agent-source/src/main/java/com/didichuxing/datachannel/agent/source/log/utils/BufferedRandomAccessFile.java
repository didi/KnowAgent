package com.didichuxing.datachannel.agent.source.log.utils;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/5 17:39
 */
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BufferedRandomAccessFile extends RandomAccessFile {

    static final int              LogBuffSz_      = 20;                       // 64K buffer
    public static final int       BuffSz_         = (1 << LogBuffSz_);
    static final long             BuffMask_       = ~(((long) BuffSz_) - 1L);

    private String                path_;

    private int                   seekTimes       = 0;
    private int                   seekTimesThread = 5;

    private final int             maxLineSize     = BuffSz_ * seekTimesThread;

    /*
     * This implementation is based on the buffer implementation in Modula-3's "Rd", "Wr", "RdClass", and "WrClass"
     * interfaces.
     */
    private boolean               dirty_;                                     // true iff unflushed bytes exist
    private boolean               syncNeeded_;                                // dirty_ can be cleared by e.g. seek, so track
    // sync separately
    private long                  curr_;                                      // current position in file
    private long                  lo_, hi_;                                   // bounds on characters in "buff"
    private byte[]                buff_;                                      // local buffer
    private long                  maxHi_;                                     // this.lo + this.buff.length
    private boolean               hitEOF_;                                    // buffer contains last file block?
    private long                  diskPos_;                                   // disk position

    private byte[]                indexBuffer;
    private ByteArrayOutputStream out;

    /*
     * To describe the above fields, we introduce the following abstractions for the file "f": len(f) the length of the
     * file curr(f) the current position in the file c(f) the abstract contents of the file disk(f) the contents of f's
     * backing disk file closed(f) true iff the file is closed "curr(f)" is an index in the closed interval [0, len(f)].
     * "c(f)" is a character sequence of length "len(f)". "c(f)" and "disk(f)" may differ if "c(f)" contains unflushed
     * writes not reflected in "disk(f)". The flush operation has the effect of making "disk(f)" identical to "c(f)". A
     * file is said to be *valid* if the following conditions hold: V1. The "closed" and "curr" fields are correct:
     * f.closed == closed(f) f.curr == curr(f) V2. The current position is either contained in the buffer, or just past
     * the buffer: f.lo <= f.curr <= f.hi V3. Any (possibly) unflushed characters are stored in "f.buff": (forall i in
     * [f.lo, f.curr): c(f)[i] == f.buff[i - f.lo]) V4. For all characters not covered by V3, c(f) and disk(f) agree:
     * (forall i in [f.lo, len(f)): i not in [f.lo, f.curr) => c(f)[i] == disk(f)[i]) V5. "f.dirty" is true iff the
     * buffer contains bytes that should be flushed to the file; by V3 and V4, only part of the buffer can be dirty.
     * f.dirty == (exists i in [f.lo, f.curr): c(f)[i] != f.buff[i - f.lo]) V6. this.maxHi == this.lo + this.buff.length
     * Note that "f.buff" can be "null" in a valid file, since the range of characters in V3 is empty when
     * "f.lo == f.curr". A file is said to be *ready* if the buffer contains the current position, i.e., when: R1.
     * !f.closed && f.buff != null && f.lo <= f.curr && f.curr < f.hi When a file is ready, reading or writing a single
     * byte can be performed by reading or writing the in-memory buffer without performing a disk operation.
     */

    /**
     * Open a new <code>BufferedRandomAccessFile</code> on <code>file</code> in mode <code>mode</code>, which should be
     * "r" for reading only, or "rw" for reading and writing.
     */
    public BufferedRandomAccessFile(File file, String mode) throws IOException {
        this(file, mode, 0);
    }

    public BufferedRandomAccessFile(File file, String mode, int size) throws IOException {
        super(file, mode);
        path_ = file.getAbsolutePath();
        this.init(size);
    }

    /**
     * Open a new <code>BufferedRandomAccessFile</code> on the file named <code>name</code> in mode <code>mode</code>,
     * which should be "r" for reading only, or "rw" for reading and writing.
     */
    public BufferedRandomAccessFile(String name, String mode) throws IOException {
        this(name, mode, 0);
    }

    public BufferedRandomAccessFile(String name, String mode, int size)
                                                                       throws FileNotFoundException {
        super(name, mode);
        path_ = name;
        this.init(size);
    }

    private void init(int size) {
        this.dirty_ = false;
        this.lo_ = this.curr_ = this.hi_ = 0;
        this.buff_ = (size > BuffSz_) ? new byte[size] : new byte[BuffSz_];
        this.maxHi_ = (long) BuffSz_;
        this.hitEOF_ = false;
        this.diskPos_ = 0L;
        this.indexBuffer = new byte[128];
        this.out = new ByteArrayOutputStream(4096);
    }

    public String getPath() {
        return path_;
    }

    public void sync() throws IOException {
        if (syncNeeded_) {
            flush();
            getChannel().force(true);
            syncNeeded_ = false;
        }
    }

    // public boolean isEOF() throws IOException
    // {
    //// assert getFilePointer() <= length();
    // return getFilePointer() == length();
    // }

    @Override
    public void close() throws IOException {
        this.flush();
        this.buff_ = new byte[0];
        super.close();
    }

    /**
     * Flush any bytes in the file's buffer that have not yet been written to disk. If the file was created read-only,
     * this method is a no-op.
     */
    public void flush() throws IOException {
        this.flushBuffer();
    }

    /* Flush any dirty bytes in the buffer to disk. */
    private void flushBuffer() throws IOException {
        if (this.dirty_) {
            if (this.diskPos_ != this.lo_) {
                super.seek(this.lo_);
            }
            int len = (int) (this.curr_ - this.lo_);
            super.write(this.buff_, 0, len);
            this.diskPos_ = this.curr_;
            this.dirty_ = false;
        }
    }

    /*
     * Read at most "this.buff.length" bytes into "this.buff", returning the number of bytes read. If the return result
     * is less than "this.buff.length", then EOF was read.
     */
    private int fillBuffer() throws IOException {
        int cnt = 0;
        int rem = this.buff_.length;
        while (rem > 0) {
            int n = super.read(this.buff_, cnt, rem);
            if (n < 0) {
                break;
            }
            cnt += n;
            rem -= n;
        }
        if ((cnt < 0) && (this.hitEOF_ = (cnt < this.buff_.length))) {
            // make sure buffer that wasn't read is initialized with -1
            Arrays.fill(this.buff_, cnt, this.buff_.length, (byte) 0xff);
        }
        this.diskPos_ += cnt;
        return cnt;
    }

    /*
     * This method positions <code>this.curr</code> at position <code>pos</code>. If <code>pos</code> does not fall in
     * the current buffer, it flushes the current buffer and loads the correct one.<p> On exit from this routine
     * <code>this.curr == this.hi</code> iff <code>pos</code> is at or past the end-of-file, which can only happen if
     * the file was opened in read-only mode.
     */
    @Override
    public void seek(long pos) throws IOException {
        if (pos >= this.hi_ || pos < this.lo_) {
            // seeking outside of current buffer -- flush and read
            this.flushBuffer();
            this.lo_ = pos & BuffMask_; // init at BuffSz boundary
            this.maxHi_ = this.lo_ + (long) this.buff_.length;
            if (this.diskPos_ != this.lo_) {
                super.seek(this.lo_);
                this.diskPos_ = this.lo_;
            }
            int n = this.fillBuffer();
            this.hi_ = this.lo_ + (long) n;
        } else {
            // seeking inside current buffer -- no read required
            if (pos < this.curr_) {
                // if seeking backwards, we must flush to maintain V4
                this.flushBuffer();
            }
        }
        this.curr_ = pos;
    }

    @Override
    public long getFilePointer() {
        return this.curr_;
    }

    @Override
    public long length() throws IOException {
        // max accounts for the case where we have written past the old file length, but not yet flushed our buffer

        return super.length();
        /* return Math.max(this.curr_, super.length()); */
    }

    private long preOffset = 0L;

    public long preOffset() {
        return preOffset;
    }

    /**
     * 读到文件末尾还没有读到换行符则先等待timeout s再读。如果还是文件末尾则认为文件结束
     *
     * @return the next line of text from this file, or null if end of file is encountered before even one byte is read.
     * @exception IOException if an I/O error occurs.
     */

    //    public String readNewLine(Long timeout) throws IOException, InterruptedException {
    //        preOffset = this.curr_;
    //        StringBuffer input = new StringBuffer(4096);
    //        seekTimes = 0;
    //        int c = -1;
    //        long size = 0L;
    //        boolean eol = false;
    //        boolean isEndFlag = false;
    //        String bigLine = null;
    //        // 长度最长为1M(不考虑中文字符的影响)
    //        while (!eol) {
    //            switch (c = read()) {
    //                case -1:
    //                    if (!isEndFlag) {
    //                        isEndFlag = true;
    //                        TimeUnit.MILLISECONDS.sleep(timeout);
    //                        continue;
    //                    } else {
    //                        eol = true;
    //                    }
    //                    break;
    //                case -2:
    //                    // 此时表示seek了seekTimesThread次，其中seek1次是1M，此时表示超出大小
    //                    if (bigLine == null) {
    //                        // 只需要第一行
    //                        bigLine = input.toString();
    //                    }
    //                    // 清空input
    //                    input.setLength(0);
    //                    break;
    //                case '\n':
    //                    eol = true;
    //                    break;
    //                case '\r':
    //                    eol = true;
    //                    long cur = getFilePointer();
    //                    if ((read()) != '\n') {
    //                        seek(cur);
    //                    }
    //                    break;
    //                default:
    //                    input.append((char) c);
    //                    size++;
    //                    break;
    //            }
    //        }
    //
    //        if ((c == -1) && (input.length() == 0)) {
    //            return null;
    //        }
    //
    //        if (bigLine != null) {
    //            return bigLine;
    //        } else {
    //            return input.toString();
    //        }
    //    }

    public byte[] readNewLine(Long timeout) throws IOException, InterruptedException {
        preOffset = this.curr_;
        out.reset();
        //StringBuffer input = new StringBuffer(4096);
        seekTimes = 0;
        //int c = -1;
        long size = 0L;
        boolean eol = false;
        boolean isEndFlag = false;
        //boolean bigLine = false;
        // 长度最长为1M(不考虑中文字符的影响)
        int length = -1;
        while (!eol) {
            length = read(indexBuffer);
            if (length >= 0) {
                int crIndex = FileUtils.getLineDelimiterIndex(indexBuffer,
                    FileUtils.CR_LINE_DELIMITER, length);
                int lfIndex;
                if (crIndex >= 0) {
                    secureWriteBuffer(indexBuffer, 0, crIndex);
                    eol = true;
                    long curr = getFilePointer();
                    seek(curr - length + crIndex + 1);
                    if ((read()) != '\n') {
                        seek(curr_ - 1);
                    }
                } else if ((lfIndex = FileUtils.getLineDelimiterIndex(indexBuffer,
                    FileUtils.LF_LINE_DELIMITER, length)) >= 0) {
                    secureWriteBuffer(indexBuffer, 0, lfIndex);
                    eol = true;
                    long curr = getFilePointer();
                    seek(curr - length + lfIndex + 1);
                } else {
                    secureWriteBuffer(indexBuffer, 0, length);
                }
            } else if (length == -1) {
                if (!isEndFlag) {
                    isEndFlag = true;
                    TimeUnit.MILLISECONDS.sleep(timeout);
                } else {
                    eol = true;
                }
            }
        }

        if (length < 0 && out.size() == 0) {
            return null;
        } else {
            return out.toByteArray();
        }
    }

    private void secureWriteBuffer(byte[] value, int off, int len) {
        if (out.size() < maxLineSize) {
            out.write(value, off, len);
        }
    }

    @Override
    public int read() throws IOException {
        if (this.curr_ >= this.hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF_) {
                return -1;
            }

            // slow path -- read another buffer
            this.seek(this.curr_);

            if (this.curr_ == this.hi_) {
                return -1;
            }

            seekTimes++;
            if (seekTimes >= seekTimesThread) {
                seekTimes = 0;
                return -2;
            }
        }
        byte res = this.buff_[(int) (this.curr_ - this.lo_)];
        this.curr_++;
        return ((int) res) & 0xFF; // convert byte -> int
    }

    public boolean ifEnd() throws IOException {
        if (this.curr_ >= this.hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF_) {
                return true;
            }

            // slow path -- read another buffer
            this.seek(this.curr_);

            if (this.curr_ == this.hi_) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.curr_ >= this.hi_) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF_) {
                return -1;
            }

            // slow path -- read another buffer
            this.seek(this.curr_);
            if (this.curr_ == this.hi_) {
                return -1;
            }
        }
        len = Math.min(len, (int) (this.hi_ - this.curr_));
        int buffOff = (int) (this.curr_ - this.lo_);
        System.arraycopy(this.buff_, buffOff, b, off, len);
        this.curr_ += len;
        return len;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.curr_ >= this.hi_) {
            if (this.hitEOF_ && this.hi_ < this.maxHi_) {
                // at EOF -- bump "hi"
                this.hi_++;
            } else {
                // slow path -- write current buffer; read next one
                this.seek(this.curr_);
                if (this.curr_ == this.hi_) {
                    // appending to EOF -- bump "hi"
                    this.hi_++;
                }
            }
        }
        this.buff_[(int) (this.curr_ - this.lo_)] = (byte) b;
        this.curr_++;
        this.dirty_ = true;
        syncNeeded_ = true;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int n = this.writeAtMost(b, off, len);
            off += n;
            len -= n;
            this.dirty_ = true;
            syncNeeded_ = true;
        }
    }

    /*
     * Write at most "len" bytes to "b" starting at position "off", and return the number of bytes written.
     */
    private int writeAtMost(byte[] b, int off, int len) throws IOException {
        if (this.curr_ >= this.hi_) {
            if (this.hitEOF_ && this.hi_ < this.maxHi_) {
                // at EOF -- bump "hi"
                this.hi_ = this.maxHi_;
            } else {
                // slow path -- write current buffer; read next one
                this.seek(this.curr_);
                if (this.curr_ == this.hi_) {
                    // appending to EOF -- bump "hi"
                    this.hi_ = this.maxHi_;
                }
            }
        }
        len = Math.min(len, (int) (this.hi_ - this.curr_));
        int buffOff = (int) (this.curr_ - this.lo_);
        System.arraycopy(b, off, this.buff_, buffOff, len);
        this.curr_ += len;
        return len;
    }
}
