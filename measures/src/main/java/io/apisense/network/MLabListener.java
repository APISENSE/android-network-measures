package io.apisense.network;

import java.util.List;

/**
 * Handle the callback from {@link MLabNS} task.
 */
public interface MLabListener {
  /**
   * Execute this method when available servers are found.
   *
   * @param ips The list of available servers
   */
  void onMLabFinished(List<String> ips);
}
