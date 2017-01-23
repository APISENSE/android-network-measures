package io.apisense.network;

/**
 * This class represents the information about a failed measurement.
 */
public class FailedMeasurementResult extends MeasurementResult {
  FailedMeasurementResult(String taskName, long startTime, long endTime) {
    super(taskName, startTime, endTime);
  }
}
