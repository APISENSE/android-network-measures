package io.apisense.network.tcp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import io.apisense.network.MeasurementError;
import io.apisense.network.MeasurementResult;


/**
 * Measurement class used to realise an TCP download test
 */
public class TCPDownloadTask extends TCPThroughputTask {
  private static final String TAG = "TCPDownloadTask";
  private static final int PORT_DOWNLINK = 6001;

  public TCPDownloadTask(TCPThroughputConfig tcpThroughputConfig) {
    super(tcpThroughputConfig);
  }

  /**
   * {@inheritDoc}
   *
   * @return A {@link TCPThroughputResult} object containing information on the TCP download test.
   * @throws MeasurementError {@inheritDoc}
   */
  public MeasurementResult execute() throws MeasurementError {
    Log.d(TAG, "Start");
    Socket tcpSocket = buildUpSocket(config.getTarget(), PORT_DOWNLINK);

    try {
      this.taskStartTime = System.currentTimeMillis();
      retrieveData(tcpSocket);
      this.taskEndTime = System.currentTimeMillis();
    } catch (OutOfMemoryError e) { // TODO: See if this catch clause is really necessary
      throw new MeasurementError("Detect out of memory at Downlink task.", e);
    } finally {
      closeSocket(tcpSocket);
    }
    Log.d(TAG, "Finished");

    return new TCPThroughputResult(TAG, this.taskStartTime, this.taskEndTime,
        config, this.samplingResults, this.dataConsumedAfterSlowStart);
  }

  /**
   * Read the data sent by the server to the socket,
   * and update the performance statistics accordingly.
   *
   * @param tcpSocket The socket to read through
   * @throws MeasurementError If the socket interaction fails.
   */
  private void retrieveData(Socket tcpSocket) throws MeasurementError {
    int read_bytes;
    byte[] buffer = new byte[BUFFER_SIZE];
    InputStream iStream = null;
    try {
      iStream = tcpSocket.getInputStream();
      do {
        read_bytes = iStream.read(buffer, 0, buffer.length);
        updateSize(read_bytes);
      } while (read_bytes >= 0);
    } catch (IOException e) {
      throw new MeasurementError("Error to receive data from " + config.getTarget(), e);
    } finally {
      closeStream(iStream);
    }
  }
}
