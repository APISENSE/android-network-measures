package io.apisense.network;

/**
 * Common measurement behavior.
 */
public abstract class Measurement {
  public final String taskName;

  protected Measurement(String taskName) {
    this.taskName = taskName;
  }

  /**
   * Ensure that the measurement is called in an {@link android.os.AsyncTask}.
   *
   * @param callback The {@link MeasurementCallback} used
   *                 for reporting success or failure of this measurement.
   */
  public final void call(MeasurementCallback callback) {
    new MeasurementExecutor(callback).execute(this);
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
