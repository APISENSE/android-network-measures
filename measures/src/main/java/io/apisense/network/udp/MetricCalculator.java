package io.apisense.network.udp;

import java.util.ArrayList;

/**
 * @author Hongyi Yao (hyyao@umich.edu) This class calculates the out-of-order ratio and delay
 *         jitter in the array of received UDP packets
 */
final class MetricCalculator {
  private int maxPacketNum;
  private ArrayList<Long> offsetedDelayList;
  private int packetCount;
  private int outOfOrderCount;

  public MetricCalculator() {
    maxPacketNum = -1;
    offsetedDelayList = new ArrayList<>();
    packetCount = 0;
    outOfOrderCount = 0;
  }

  /**
   * Out-of-order packets is defined as arriving packets with sequence numbers smaller than their
   * predecessors.
   *
   * @param packetNum: packet number in burst sequence
   * @param timestamp: estimated one-way delay(contains clock offset)
   */
  public void addPacket(int packetNum, long timestamp) {
    if (packetNum > maxPacketNum) {
      maxPacketNum = packetNum;
    } else {
      outOfOrderCount++;
    }
    offsetedDelayList.add(System.currentTimeMillis() - timestamp);
    packetCount++;
  }

  /**
   * Out-of-order ratio is defined as the ratio between the number of out-of-order packets and the
   * total number of packets.
   *
   * @return the inversion number of the current UDP burst
   */
  public double calculateOutOfOrderRatio() {
    if (packetCount != 0) {
      return (double) outOfOrderCount / packetCount;
    } else {
      return 0.0;
    }
  }

  /**
   * Calculate jitter as the standard deviation of one-way delays[RFC3393] We can assume the clock
   * offset between server and client is constant in a short period(several milliseconds) since
   * typical oscillators have no more than 100ppm of frequency error , then it will be cancelled
   * out during the calculation process
   *
   * @return the jitter of UDP burst
   */
  public long calculateJitter() {
    if (packetCount > 1) {
      double offsetedDelay_mean = 0;
      for (long offsetedDelay : offsetedDelayList) {
        offsetedDelay_mean += (double) offsetedDelay / packetCount;
      }

      double jitter = 0;
      for (long offsetedDelay : offsetedDelayList) {
        jitter += ((double) offsetedDelay - offsetedDelay_mean)
            * ((double) offsetedDelay - offsetedDelay_mean) / (packetCount - 1);
      }
      jitter = Math.sqrt(jitter);

      return (long) jitter;
    } else {
      return 0;
    }
  }
}
