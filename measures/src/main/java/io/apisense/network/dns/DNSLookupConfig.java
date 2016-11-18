package io.apisense.network.dns;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The description of DNS lookup measurement
 */
public class DNSLookupConfig {
  private static final String TAG = "DNSLookupConfig";
  /**
   * Domain name or IP to query.
   */
  private final String target;

  /**
   * DNS server to use for the query,
   * Will retrieve the configured ones from the phone by default.
   */
  private String server = retrieveDeviceServers()[0];

  /**
   * Query class,
   * by default to 'IN'.
   */
  private String qclass = "IN";

  /**
   * Query type, is determined dependending on the target.
   * By default to 'A'.
   */
  private String qtype = "A";

  /**
   * Explicitly ask to the DNS request to be over TCP.
   */
  private boolean forceTCP = false;

  /**
   * Timeout of the DNS request, in milliseconds.
   * default value: 5000 ms.
   */
  private int timeout = 5000;

  public DNSLookupConfig(String target) {
    if (target.endsWith(".")) {
      this.target = target;
    } else {
      Log.w(TAG, "User missed the point by giving a relative domain. Using absolute domain...");
      this.target = target + ".";
    }
  }

  /**
   * Retrieve the DNS servers specified in the Android device configuration.
   *
   * @return A list of DNS servers.
   */
  private static String[] retrieveDeviceServers() {
    List<String> servers = new ArrayList<>();
    try {
      Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
      Method method = SystemProperties.getMethod("get", String.class);
      for (String name : new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4",}) {
        String value = (String) method.invoke(null, name);
        if (value != null && !"".equals(value) && !servers.contains(value))
          servers.add(value);
      }
    } catch (Exception ex) {
      Log.d(TAG, "Unable to get local DNS resolver");
    }
    return servers.toArray(new String[0]);
  }

  @Override
  public String toString() {
    return "DNSLookupConfig{" +
        "target='" + target + '\'' +
        ", server='" + server + '\'' +
        ", qclass='" + qclass + '\'' +
        ", qtype='" + qtype + '\'' +
        '}';
  }

  public String getTarget() {
    return target;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getQclass() {
    return qclass;
  }

  public void setQclass(String qclass) {
    this.qclass = qclass;
  }

  public String getQtype() {
    return qtype;
  }

  public void setQtype(String qtype) {
    this.qtype = qtype;
  }

  public boolean isForceTCP() {
    return forceTCP;
  }

  public void setForceTCP(boolean forceTCP) {
    this.forceTCP = forceTCP;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
}
