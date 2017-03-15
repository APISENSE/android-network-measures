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

  /**
   * Round-trip time of the ping
   */
  private final Rtt rtt;

  ICMPResult(long startTime, long endTime, ICMPConfig config, String hostname, String ip, long ping, int ttl, Rtt rtt) {
    super(ICMPTask.TAG, startTime, endTime);
    this.config = config;
    this.hostname = hostname;
    this.ip = ip;
    this.ping = ping;
    this.ttl = ttl;
    this.rtt = rtt;
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

  public Rtt getRtt() {
    return rtt;
  }

  @Override
  public String toString() {
    return "ICMPResult{" +
        "config=" + config +
        ", hostname='" + hostname + '\'' +
        ", ip='" + ip + '\'' +
        ", ping=" + ping +
        ", ttl=" + ttl +
        ", rtt=" + rtt +
        "} " + super.toString();
  }
}
