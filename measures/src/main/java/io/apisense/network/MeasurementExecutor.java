package io.apisense.network;

import android.os.AsyncTask;

/**
 * Asynchronous task executing the given measurement tasks,
 * and calling the given {@link MeasurementCallback} for each returned success or error.
 */
public class MeasurementExecutor extends AsyncTask<Measurement, Void, MeasurementResult> {
  private final MeasurementCallback listener;
  private MeasurementError error;

  public MeasurementExecutor(MeasurementCallback listener) {
    this.listener = listener;
  }

  @Override
  protected MeasurementResult doInBackground(Measurement... measurementTasks) {
    if (measurementTasks.length == 0) {
      this.error = new MeasurementError("None", "No task to run!");
      return null;
    }

    Measurement task = measurementTasks[0];
    try {
      return task.execute();
    } catch (MeasurementError measurementError) {
      this.error = measurementError;
      long time = System.currentTimeMillis();
      return new FailedMeasurementResult(task.taskName, time, time);
    }
  }

  @Override
  protected void onPostExecute(MeasurementResult result) {
    super.onPostExecute(result);
    if (hasError() || result == null) {
      listener.onError(error);
    } else {
      listener.onResult(result);
    }
  }

  private boolean hasError() {
    return error != null;
  }
}
