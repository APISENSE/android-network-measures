package io.apisense.network.udp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.apisense.network.MeasurementConfigException;

/**
 * Configuration class for both upload an download UDPBurstTask
 */

public class UDPBurstConfig {
  /**
   * Min packet size = (int type) + (int burstCount) + (int packetNum) + (int intervalNum) + (long
   * timestamp) + (int packetSize) + (int seq) + (int udpInterval) = 36
   */
  private static final int MIN_PACKETSIZE = 36;

  /**
   * Leave enough margin for min MTU in the link and IP options.
   */
  private static final int MAX_PACKETSIZE = 500;

  /**
   * Size of the packets in bytes
   */
  private int packetSizeByte = 100;

  /**
   * Number of packet to send by burst.
   */
  private int udpBurstCount = 16;

  /**
   * Interval between burst (µs).
   *
   * This value will be rounded in {@link UDPPacket#UDPPacket(int, UDPBurstConfig)}.
   */
  private int udpInterval = 500;

  /**
   * IP address of the remote server used for the test
   */
  private InetAddress targetIp;

  /**
   * URL of the remote server used for the test
   */
  private String url;

  public UDPBurstConfig(String url, int packetSizeByte) throws MeasurementConfigException {
    setUrl(url);
    if (packetSizeByte >= UDPBurstConfig.MIN_PACKETSIZE
        && packetSizeByte <= UDPBurstConfig.MAX_PACKETSIZE) {
      this.packetSizeByte = packetSizeByte;
    } else {
      throw new MeasurementConfigException("PacketSizeByte must be between "
          + String.valueOf(UDPBurstConfig.MIN_PACKETSIZE) + " and "
          + String.valueOf(UDPBurstConfig.MAX_PACKETSIZE));
    }
  }

  public int getPacketSizeByte() {
    return packetSizeByte;
  }

  public void setPacketSizeByte(int packetSizeByte) {
    this.packetSizeByte = packetSizeByte;
  }

  public int getUdpBurstCount() {
    return udpBurstCount;
  }

  public void setUdpBurstCount(int udpBurstCount) {
    this.udpBurstCount = udpBurstCount;
  }

  public int getUdpInterval() {
    return udpInterval;
  }

  public void setUdpInterval(int udpInterval) {
    this.udpInterval = udpInterval;
  }

  public InetAddress getTargetIp() {
    return targetIp;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) throws MeasurementConfigException {
    this.url = url;
    try {
      targetIp = InetAddress.getByName(url);
    } catch (UnknownHostException e) {
      throw new MeasurementConfigException(e);
    }
  }

  @Override
  public String toString() {
    return "UDPBurstConfig{" +
        "packetSizeByte=" + packetSizeByte +
        ", udpBurstCount=" + udpBurstCount +
        ", udpInterval=" + udpInterval +
        ", targetIp=" + targetIp +
        ", url='" + url + '\'' +
        '}';
  }
}
