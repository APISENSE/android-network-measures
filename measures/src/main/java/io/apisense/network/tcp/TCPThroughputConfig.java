package io.apisense.network.tcp;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Configuration class for both upload an download TCPThroughputTask
 */
public class TCPThroughputConfig {
  /**
   * IP address of the remote server used for the test
   */
  private InetAddress target;

  /**
   * URL of the remote server used for the test
   */
  private String url;

  /**
   * Data limit for upload in Mb
   */
  private int dataLimitMbUp = 7;

  /**
   * Data limit for download in Mb
   */
  private int dataLimitMbDown = 10;

  /**
   * Size of a single packet in bytes
   */
  private int pktSizeUpBytes = 700;

  /**
   * Duration of a sample period in second
   */
  private float samplePeriodSec = 0.5f;

  /**
   * Duration of the slow start period in float
   */
  private float slowStartPeriodSec = 0.5f;

  /**
   * Timeout of the TCP connection established for the test
   */
  private int tcpTimeoutSec = 15;

  public TCPThroughputConfig(String url) throws UnknownHostException {
    setUrl(url);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) throws UnknownHostException {
    this.url = url;
    target = InetAddress.getByName(url);
  }

  public InetAddress getTarget() {
    return target;
  }

  public int getDataLimitMbUp() {
    return dataLimitMbUp;
  }

  public void setDataLimitMbUp(int dataLimitMbUp) {
    this.dataLimitMbUp = dataLimitMbUp;
  }

  public int getDataLimitMbDown() {
    return dataLimitMbDown;
  }

  public void setDataLimitMbDown(int dataLimitMbDown) {
    this.dataLimitMbDown = dataLimitMbDown;
  }

  public int getPktSizeUpBytes() {
    return pktSizeUpBytes;
  }

  public void setPktSizeUpBytes(int pktSizeUpBytes) {
    this.pktSizeUpBytes = pktSizeUpBytes;
  }

  public float getSamplePeriodSec() {
    return samplePeriodSec;
  }

  public void setSamplePeriodSec(float samplePeriodSec) {
    this.samplePeriodSec = samplePeriodSec;
  }

  public float getSlowStartPeriodSec() {
    return slowStartPeriodSec;
  }

  public void setSlowStartPeriodSec(float slowStartPeriodSec) {
    this.slowStartPeriodSec = slowStartPeriodSec;
  }

  public int getTcpTimeoutSec() {
    return tcpTimeoutSec;
  }

  public void setTcpTimeoutSec(int tcpTimeoutSec) {
    this.tcpTimeoutSec = tcpTimeoutSec;
  }
}
