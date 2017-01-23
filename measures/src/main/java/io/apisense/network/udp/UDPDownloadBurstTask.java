package io.apisense.network.udp;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import io.apisense.network.MeasurementError;
import io.apisense.network.MeasurementResult;

/**
 * Measurement class used to realise an UDP download burst
 * Measures the jitter, the jitter, the loss, and the number of out of order packets in download
 */
public class UDPDownloadBurstTask extends UDPBurstTask {
  public static final String TAG = "UDPDownloadBurst";

  public UDPDownloadBurstTask(UDPBurstConfig udpBurstConfig) {
    super(TAG, udpBurstConfig);
  }

  /**
   * {@inheritDoc}
   *
   * @return A {@link UDPBurstResult} object containing information on the UDP download burst.
   * @throws MeasurementError {@inheritDoc}
   */
  public MeasurementResult execute() throws MeasurementError {
    MetricCalculator metricCalculator = new MetricCalculator();
    DatagramSocket sock = openSocket();

    startTimeTask = System.currentTimeMillis();
    UDPPacket dataPacket;
    int pktRecv = 0;

    sendDownloadRequest(sock);

    for (int i = 0; i < config.getUdpBurstCount(); i++) {
      try {
        dataPacket = retrieveResponseDatagram(sock);
      } catch (MeasurementError e) {
        Log.w(TAG, e);
        break;
      }

      if (dataPacket.type == UDPPacket.PKT_DATA) {
        Log.v(TAG, "Received packed n°" + dataPacket.packetNum);
        pktRecv++;
        metricCalculator.addPacket(dataPacket.packetNum, dataPacket.timestamp);
      } else {
        throw new MeasurementError(taskName, "Error closing input stream from " + config.getTargetIp());
      }
    }
    endTimeTask = System.currentTimeMillis();
    sock.close();

    double outOfOrderRatio = metricCalculator.calculateOutOfOrderRatio();
    long jitter = metricCalculator.calculateJitter();
    int lostCount = config.getUdpBurstCount() - pktRecv;
    return new UDPBurstResult(TAG, this.startTimeTask, this.endTimeTask, config,
        pktRecv, lostCount, outOfOrderRatio, jitter);
  }

  /**
   * Send a request packet to download with UDP.
   *
   * @param sock The socket to send packets through.
   * @throws MeasurementError If any error occurred during measurement.
   */
  private void sendDownloadRequest(DatagramSocket sock) throws MeasurementError {
    UDPPacket requestPacket = new UDPPacket(taskName, UDPPacket.PKT_REQUEST, this.config);
    try {
      sock.send(requestPacket.createDatagram(config.getTargetIp(), DEFAULT_PORT));
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Error while sending download burst request on " + config.getTargetIp(), e);
    }
  }

}
