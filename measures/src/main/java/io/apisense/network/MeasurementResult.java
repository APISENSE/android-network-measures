package io.apisense.network;

/**
 * Abstract class containing the attributes shared by all the results returned the Measurement methods call()
 */
public abstract class MeasurementResult {
  /**
   * Name of the task (ie TCP download, Traceroute, etc)
   */
  private final String taskName;

  /**
   * Time of the beginning of the task in milliseconds
   */
  private final long startTime;

  /**
   *  Time of the end of the task in milliseconds
   */
  private final long endTime;

  /**
   * Number of milliseconds representing the duration of the task
   */
  private final long duration;

  protected MeasurementResult(String taskName, long startTime, long endTime){
    this.taskName = taskName;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = endTime - startTime;
  }

  public String getTaskName() {
    return taskName;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public long getDuration() {
    return duration;
  }

  @Override
  public String toString() {
    return "MeasurementResult{" +
            "taskName='" + taskName + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", duration=" + duration +
            '}';
  }
}
