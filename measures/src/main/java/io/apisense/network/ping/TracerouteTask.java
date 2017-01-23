package io.apisense.network.ping;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.apisense.network.Measurement;
import io.apisense.network.MeasurementError;
import io.apisense.network.MeasurementResult;

/**
 * Measurement class used to realise a Traceroute
 */
public class TracerouteTask extends Measurement {
  private static final String TAG = "ICMPTraceroute";
  private final TracerouteConfig config;
  private String destIp;
  private ICMPConfig icmpConfig;

  public TracerouteTask(TracerouteConfig tracerouteConfig) {
    this.config = tracerouteConfig;
    this.icmpConfig = new ICMPConfig(config.getUrl());
  }

  @Override
  public MeasurementResult execute() throws MeasurementError {
    destIp = new ICMPTask(icmpConfig).execute().getIp();

    long taskStartTime = System.currentTimeMillis();
    ArrayList<ICMPResult> traces = new ArrayList<>();
    traceroute(1, traces);
    long taskEndTime = System.currentTimeMillis();
    return new TracerouteResult(TAG, taskStartTime, taskEndTime, config, traces);
  }

  private void traceroute(int currentTtl, List<ICMPResult> traces) {
    ICMPResult hop = null;
    try {
      icmpConfig.setTtl(currentTtl);
      hop = new ICMPTask(icmpConfig).execute();
      Log.v(TAG, "A new ICMPResult : " + hop);
      if (hop != null) {
        traces.add(hop);
      }
    } catch (MeasurementError e) {
      Log.e(TAG, "Error on ICMPResult (dst: " + config.getUrl() + ", ttl: " + currentTtl + "): "
          + e.getMessage(), e);
    } finally {
      if (notThereYet(hop, currentTtl)) {
        traceroute(currentTtl + 1, traces);
      }
    }
  }

  /**
   * Defines if the traceroute should continue, by checking that:
   * - The current TTL is under the TTL limit.
   * - The current node is NOT the target node.
   *
   * @param hop        The current hop.
   * @param currentTtl The TTL used for this ping.
   * @return True if the traceroute should iterate at least one more time, false otherwise.
   */
  private boolean notThereYet(ICMPResult hop, int currentTtl) {
    if (hop == null) { // If a node doesn't answer, we try the next one.
      return currentTtl < config.getTtlMax();
    }
    return currentTtl < config.getTtlMax() && !destIp.equals(hop.getIp());
  }
}

