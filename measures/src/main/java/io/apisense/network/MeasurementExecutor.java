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
    for (Measurement task : measurementTasks) {
      try {
        return task.execute();
      } catch (MeasurementError measurementError) {
        this.error = measurementError;
      }
    }
    return null;
  }

  @Override
  protected void onPostExecute(MeasurementResult result) {
    super.onPostExecute(result);
    if (hasError()) {
      listener.onError(error);
    } else {
      listener.onResult(result);
    }
  }

  private boolean hasError() {
    return error != null;
  }
}
