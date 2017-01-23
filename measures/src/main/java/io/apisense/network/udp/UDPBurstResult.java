package io.apisense.network.udp;

import io.apisense.network.MeasurementResult;

/**
 * Class containing result of {@link UDPBurstTask} (upload and download)
 */

public final class UDPBurstResult extends MeasurementResult {
  /**
   * Configuration used for the burst.
   */
  private final UDPBurstConfig config;

  /**
   * Number of packets sent during the test
   */
  private final int packetCount;

  /**
   * ratio (between 0 and 1) of packets out of order.
   *
   * Out-of-order packets are defined as arriving packets
   * with sequence numbers smaller than their predecessors
   */
  private final double outOfOrderRatio;

  /**
   * Jitter as specified in RFC3393
   */
  private final long jitter;

  /**
   * Number of packet lost during burst.
   */
  private final int lostCount;

  public UDPBurstResult(String taskName, long startTime, long endTime, UDPBurstConfig config, int packetCount, int lostCount, double outOfOrderRatio, long jitter) {
    super(taskName, startTime, endTime);
    this.config = config;
    this.packetCount = packetCount;
    this.lostCount = lostCount;
    this.outOfOrderRatio = outOfOrderRatio;
    this.jitter = jitter;
  }

  public UDPBurstConfig getConfig() {
    return config;
  }

  public int getPacketCount() {
    return packetCount;
  }

  public int getLostCount() {
    return lostCount;
  }

  public double getOutOfOrderRatio() {
    return outOfOrderRatio;
  }

  public long getJitter() {
    return jitter;
  }

  @Override
  public String toString() {
    return "UDPBurstResult{" +
        "config=" + config +
        ", packetCount=" + packetCount +
        ", outOfOrderRatio=" + outOfOrderRatio +
        ", jitter=" + jitter +
        ", lostCount=" + lostCount +
        "} " + super.toString();
  }
}
