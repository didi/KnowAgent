package com.didiglobal.logi.auvjob.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.StringUtils;

public class Sequence {
  private static final Log logger = LogFactory.getLog(Sequence.class);
  private final long twepoch = 1288834974657L;
  private final long workerIdBits = 5L;
  private final long datacenterIdBits = 5L;
  private final long maxWorkerId = 31L;
  private final long maxDatacenterId = 31L;
  private final long sequenceBits = 12L;
  private final long workerIdShift = 12L;
  private final long datacenterIdShift = 17L;
  private final long timestampLeftShift = 22L;
  private final long sequenceMask = 4095L;
  private final long workerId;
  private final long datacenterId;
  private long sequence = 0L;
  private long lastTimestamp = -1L;

  public Sequence() {
    this.datacenterId = getDatacenterId(31L);
    this.workerId = getMaxWorkerId(this.datacenterId, 31L);
  }

  /**
   * constructor.
   *
   * @param workerId workerId
   * @param datacenterId datacenterId
   */
  public Sequence(long workerId, long datacenterId) {
    Assert.isFalse(workerId > 31L || workerId < 0L,
            String.format("worker Id can't be greater than %d or less than 0", 31L));
    Assert.isFalse(datacenterId > 31L || datacenterId < 0L,
            String.format("datacenter Id can't be greater than %d or less than 0", 31L));
    this.workerId = workerId;
    this.datacenterId = datacenterId;
  }

  protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
    StringBuilder mpid = new StringBuilder();
    mpid.append(datacenterId);
    String name = ManagementFactory.getRuntimeMXBean().getName();
    if (StringUtils.isEmpty(name)) {
      mpid.append(name.split("@")[0]);
    }

    return (long) (mpid.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
  }

  protected static long getDatacenterId(long maxDatacenterId) {
    long id = 0L;

    try {
      InetAddress ip = InetAddress.getLocalHost();
      NetworkInterface network = NetworkInterface.getByInetAddress(ip);
      if (network == null) {
        id = 1L;
      } else {
        byte[] mac = network.getHardwareAddress();
        if (null != mac) {
          id = (255L & (long) mac[mac.length - 1] | 65280L & (long) mac[mac.length - 2] << 8) >> 6;
          id %= maxDatacenterId + 1L;
        }
      }
    } catch (Exception var7) {
      logger.warn("getDatacenterId: " + var7.getMessage());
    }

    return id;
  }

  /**
   * nextId.
   *
   * @return id
   */
  public synchronized long nextId() {
    long timestamp = this.timeGen();
    if (timestamp < this.lastTimestamp) {
      long offset = this.lastTimestamp - timestamp;
      if (offset > 5L) {
        throw new RuntimeException(String.format("Clock moved backwards.  "
                + "Refusing to generate id for %d milliseconds", offset));
      }

      try {
        this.wait(offset << 1);
        timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
          throw new RuntimeException(String.format("Clock moved backwards.  "
                  + "Refusing to generate id for %d milliseconds", offset));
        }
      } catch (Exception var6) {
        throw new RuntimeException(var6);
      }
    }

    if (this.lastTimestamp == timestamp) {
      this.sequence = this.sequence + 1L & 4095L;
      if (this.sequence == 0L) {
        timestamp = this.tilNextMillis(this.lastTimestamp);
      }
    } else {
      this.sequence = ThreadLocalRandom.current().nextLong(1L, 3L);
    }

    this.lastTimestamp = timestamp;
    return timestamp - 1288834974657L << 22 | this.datacenterId << 17 | this.workerId << 12
            | this.sequence;
  }

  protected long tilNextMillis(long lastTimestamp) {
    long timestamp;
    for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
    }

    return timestamp;
  }

  protected long timeGen() {
    return System.currentTimeMillis();
  }
}
