package io.apisense.network.tcp;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.apisense.network.Measurement;
import io.apisense.network.MeasurementError;

/**
 * Abstract class containing common code used for UDP upload and download tests
 */
abstract class TCPThroughputTask extends Measurement {
  protected static final int BUFFER_SIZE = 5000;
  private static final String TAG = "TCPThroughputTask";
  private static final int SEC_TO_MS = 1000;
  protected final TCPThroughputConfig config;

  // helper variables
  protected int accumulativeSize = 0;
  //start time of each sampling period in milliseconds
  protected long startSampleTime = 0;
  protected long taskStartTime = 0;
  protected long taskEndTime = 0;
  /**
   * Data consummed (sent/received) after slow star period (in bits)
   */
  protected long dataConsumedAfterSlowStart = 0;
  List<Double> samplingResults = new ArrayList<>();

  TCPThroughputTask(String taskName, TCPThroughputConfig tcpThroughputConfig) {
    super(taskName);
    config = tcpThroughputConfig;
  }

  protected Socket buildUpSocket(InetAddress hostname, int portNum) throws MeasurementError {
    try {
      Socket tcpSocket = new Socket();
      SocketAddress remoteAddr = new InetSocketAddress(hostname, portNum);
      tcpSocket.connect(remoteAddr, config.getTcpTimeoutSec() * SEC_TO_MS);
      tcpSocket.setSoTimeout(config.getTcpTimeoutSec() * SEC_TO_MS);
      tcpSocket.setTcpNoDelay(true);
      return tcpSocket;
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Error opening socket at " + hostname + ":" + portNum, e);
    }
  }

  /**
   * update the total received packet size
   *
   * @param delta time period increment
   */
  protected void updateSize(int delta) {
    double gtime = System.currentTimeMillis() - this.taskStartTime;
    //ignore slow start
    if (gtime < config.getSlowStartPeriodSec() * SEC_TO_MS) {
      return;
    }
    if (this.startSampleTime == 0) {
      this.startSampleTime = System.currentTimeMillis();
      this.accumulativeSize = 0;
    }
    this.dataConsumedAfterSlowStart += delta;
    this.accumulativeSize += delta;
    double time = System.currentTimeMillis() - this.startSampleTime;
    if (time >= (config.getSamplePeriodSec() * SEC_TO_MS)) {
      double throughput = (double) this.accumulativeSize * 1000.0 / time; //in bits/second
      this.addSamplingResult(throughput);
      this.accumulativeSize = 0;
      this.startSampleTime = System.currentTimeMillis();
    }
  }

  protected void addSamplingResult(double item) {
    samplingResults.add(item);
    Collections.sort(samplingResults);
  }

  /**
   * Fills up an array with random bytes
   *
   * @param byteArray Array to fill with random bytes
   */
  protected void genRandomByteArray(byte[] byteArray) {
    Random randStr = new Random();
    for (int i = 0; i < byteArray.length; i++) {
      byteArray[i] = (byte) ('a' + randStr.nextInt(26));
    }
  }


  /**
   * Actually close the given stream, logging a warning
   * if anything wrong occured.
   *
   * Cannot use {@link TCPThroughputTask#closeStream(Closeable)}
   * since it requires a cast available since API 19.
   *
   * @param socket The socket to close.
   */
  protected void closeSocket(Socket socket) {
    try {
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      Log.w(TAG, "Fail while closing socket", e);
    }
  }

  /**
   * Actually close the given stream, logging a warning
   * if anything wrong occured.
   *
   * @param stream The closeable to close.
   */
  protected void closeStream(Closeable stream) {
    try {
      if (stream != null) {
        stream.close();
      }
    } catch (IOException e) {
      Log.w(TAG, "Fail while closing stream", e);
    }
  }
}
