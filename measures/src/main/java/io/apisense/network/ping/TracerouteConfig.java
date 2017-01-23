package io.apisense.network.ping;

/**
 * Configuration class for ICMPTraceroute
 */

public final class TracerouteConfig {
  /**
   * URL of the remote host
   */
  private final String url;

  /**
   * Maximum time to live of ICMP packets used in traceroute
   */
  private int ttlMax;

  public TracerouteConfig(String url) {
    this.url = url;
    this.ttlMax = 42;
  }

  public String getUrl() {
    return url;
  }

  public int getTtlMax() {
    return ttlMax;
  }

  public void setTtlMax(int ttlMax) {
    this.ttlMax = ttlMax;
  }

  @Override
  public String toString() {
    return "TracerouteConfig{" +
        "url='" + url + '\'' +
        ", ttlMax=" + ttlMax +
        '}';
  }
}
