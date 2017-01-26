package io.apisense.network.tcp;

import java.util.Collections;
import java.util.List;

import io.apisense.network.MeasurementResult;

/**
 * Class containing result of {@link TCPThroughputTask} (upload and download)
 */
public final class TCPThroughputResult extends MeasurementResult {
  /**
   * Configuration used for the throughput test.
   */
  private final TCPThroughputConfig config;

  /**
   * Contains the number of bytes received or sent depending of the kind of test,
   * for each sample period, ordered in ascending order
   */
  private final List<Double> tcpSpeedResults;

  /**
   * Median throughput of all tests in bytes.
   */
  private final double medianThroughput;

  /**
   * Data used (sent or received) after slow start period in bits
   */
  private final long usedData;

  public TCPThroughputResult(String taskName, long startTime, long endTime, TCPThroughputConfig config, List<Double> tcpSpeedResults, long dataConsumedAfterSlowStart) {
    super(taskName, startTime, endTime);
    this.config = config;
    this.usedData = dataConsumedAfterSlowStart;
    this.tcpSpeedResults = Collections.unmodifiableList(tcpSpeedResults);
    this.medianThroughput = tcpSpeedResults.isEmpty() ? 0 : computeMedianSpeedPerSecond();
  }

  public TCPThroughputConfig getConfig() {
    return config;
  }

  public List<Double> getTcpSpeedResults() {
    return Collections.unmodifiableList(tcpSpeedResults);
  }

  public double getMedianThroughput() {
    return medianThroughput;
  }

  public long getUsedData() {
    return usedData;
  }

  private double computeMedianSpeedPerSecond() {
    double result;
    if (tcpSpeedResults.size() % 2 == 0) {
      result = tcpSpeedResults.get(tcpSpeedResults.size() / 2) + tcpSpeedResults.get(tcpSpeedResults.size() / 2 - 1) / 2;
    } else {
      result = tcpSpeedResults.get((tcpSpeedResults.size() - 1) / 2);
    }
    return result;
  }

  public String toString() {
    String s = super.toString() + "\n";
    s += "TCP Speed Results : [";
    for (Double d : tcpSpeedResults) {
      s += String.valueOf(d) + " ";
    }
    s += "]";
    return s;
  }
}
