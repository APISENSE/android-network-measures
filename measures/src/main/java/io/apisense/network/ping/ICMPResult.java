package io.apisense.network.ping;

import io.apisense.network.MeasurementResult;

/**
 * Contains the output of a ping command
 */
public final class ICMPResult extends MeasurementResult {
  /**
   * Configuration used for the burst.
   */
  private final ICMPConfig config;

  /**
   * Name of the host to ping
   */
  private final String hostname;

  /**
   * IP address of the host to ping
   */
  private final String ip;

  /**
   * Latency of the ping
   */
  private final long ping;

  /**
   * Time to live of the ICMP packet
   */
  private final int ttl;

  ICMPResult(long startTime, long endTime, ICMPConfig config, String hostname, String ip, long ping, int ttl) {
    super(ICMPTask.TAG, startTime, endTime);
    this.config = config;
    this.hostname = hostname;
    this.ip = ip;
    this.ping = ping;
    this.ttl = ttl;
  }

  public ICMPConfig getConfig() {
    return config;
  }

  public String getHostname() {
    return hostname;
  }

  public String getIp() {
    return ip;
  }

  public long getPing() {
    return ping;
  }

  public int getTtl() {
    return ttl;
  }

  @Override
  public String toString() {
    return "ICMPResult{" +
        "config=" + config +
        ", hostname='" + hostname + '\'' +
        ", ip='" + ip + '\'' +
        ", ping=" + ping +
        ", ttl=" + ttl +
        "} " + super.toString();
  }
}
