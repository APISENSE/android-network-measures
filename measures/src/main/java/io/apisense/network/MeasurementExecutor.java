package io.apisense.network;

/**
 * Asynchronous task executing the given measurement tasks,
 * and calling the given {@link MeasurementCallback} for each returned success or error.
 */
public class MeasurementExecutor implements Runnable {
  private final Measurement task;
  private final MeasurementCallback listener;

  public MeasurementExecutor(Measurement task, MeasurementCallback listener) {
    this.task = task;
    this.listener = listener;
  }

  @Override
  public void run() {
    try {
      listener.onResult(task.execute());
    } catch (MeasurementError error) {
      listener.onError(error);
    }
  }
}
