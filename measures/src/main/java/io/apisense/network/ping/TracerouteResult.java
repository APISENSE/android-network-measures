package io.apisense.network.ping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.apisense.network.MeasurementResult;

/**
 * Class containing result of a {@link TracerouteTask}
 */

public final class TracerouteResult extends MeasurementResult {
  /**
   * Configuration used for the traceroute.
   */
  private final TracerouteConfig config;

  /**
   * List of the hops of the Traceroute command
   */
  private final List<ICMPResult> hops;

  TracerouteResult(String taskName, long startTime, long endTime, TracerouteConfig config, ArrayList<ICMPResult> hops) {
    super(taskName, startTime, endTime);
    this.config = config;
    this.hops = Collections.unmodifiableList(hops);

  }

  public List<ICMPResult> getHops() {
    return Collections.unmodifiableList(hops);
  }

  public TracerouteConfig getConfig() {
    return config;
  }

  @Override
  public String toString() {
    return "TracerouteResult{" +
        "config=" + config +
        ", hops=" + hops +
        "} " + super.toString();
  }
}
