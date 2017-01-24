package io.apisense.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Common measurement behavior.
 */
public abstract class Measurement {
  public final String taskName;

  protected Measurement(String taskName) {
    this.taskName = taskName;
  }

  /**
   * Ensure that the measurement is asynchronously called in an {@link ExecutorService}.
   *
   * @param callback The {@link MeasurementCallback} used
   *                 for reporting success or failure of this measurement.
   */
  public final void call(MeasurementCallback callback) {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(new MeasurementExecutor(this, callback));
  }

  /**
   * Actual, synchronous, process of a measurement.
   * This method has to be called from another thread than the UI one.
   *
   * @return The results of this measurement.
   * @throws MeasurementError If anything goes wrong during measurement.
   */
  public abstract MeasurementResult execute() throws MeasurementError;
}
