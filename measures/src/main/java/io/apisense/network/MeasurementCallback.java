package io.apisense.network;

/**
 * Defines the behavior to process on Measurement finished,
 * either by a success or an error.
 */
public interface MeasurementCallback {

  /**
   * Method to execute when a new {@link MeasurementResult} is received.
   *
   * @param result The new result.
   */
  void onResult(MeasurementResult result);

  /**
   * Method to execute when a new {@link MeasurementError} is thrown.
   *
   * @param error The thrown error.
   */
  void onError(MeasurementError error);
}
