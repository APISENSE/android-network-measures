package io.apisense.network;

/**
 * Exception thrown when a measurement fails to execute
 */
public class MeasurementError extends Exception {
  public final String taskName;

  public MeasurementError(String taskName, String reason) {
    super(reason);
    this.taskName = taskName;
  }

  public MeasurementError(String taskName, String reason, Throwable e) {
    super(reason, e);
    this.taskName = taskName;
  }

  public MeasurementError(String taskName, Exception e) {
    super(e);
    this.taskName = taskName;
  }
}
