package io.apisense.network;

/**
 * Exception thrown when a measurement fails to execute
 */
public class MeasurementError extends Exception {
  public MeasurementError(String reason) {
    super(reason);
  }

  public MeasurementError(String reason, Throwable e) {
    super(reason, e);
  }

  public MeasurementError(Exception e) {
    super(e);
  }
}
