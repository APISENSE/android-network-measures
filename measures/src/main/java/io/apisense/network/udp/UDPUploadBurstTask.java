package io.apisense.network.udp;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import io.apisense.network.MeasurementError;
import io.apisense.network.MeasurementResult;

/**
 * Measurement class used to realise an UDP upload burst
 * Measures the jitter, the jitter, the loss, and the number of out of order packets in upload
 */
public class UDPUploadBurstTask extends UDPBurstTask {
  private static final String TAG = "UDPUploadBurst";

  public UDPUploadBurstTask(UDPBurstConfig udpBurstConfig) {
    super(TAG, udpBurstConfig);
  }

  /**
   * {@inheritDoc}
   *
   * @return A {@link UDPBurstResult} object containing information on the UDP upload burst.
   * @throws MeasurementError {@inheritDoc}
   */
  public MeasurementResult execute() throws MeasurementError {
    DatagramSocket sock = openSocket();

    UDPPacket dataPacket;
    startTimeTask = System.currentTimeMillis();

    // Send burst
    for (int i = 0; i < config.getUdpBurstCount(); i++) {
      dataPacket = new UDPPacket(taskName, UDPPacket.PKT_DATA, this.config);
      dataPacket.packetNum = i;

      // Flatten UDP packet
      try {
        sock.send(dataPacket.createDatagram(config.getTargetIp(), DEFAULT_PORT));
      } catch (IOException e) {
        sock.close();
        throw new MeasurementError(taskName, "Error while sending upload burst on " + config.getTargetIp(), e);
      }

      // Sleep udpInterval millisecond
      try {
        int timeMs = config.getUdpInterval() / 1000;
        int timeµs = config.getUdpInterval() % 1000;
        Thread.sleep(timeMs, timeµs * 1000);
      } catch (InterruptedException e) {
        Log.w(TAG, "Wait interrupted on UDP burst", e);
      }
    }
    endTimeTask = System.currentTimeMillis();

    // Receive response
    try {
      UDPPacket responsePacket = retrieveResponseDatagram(sock);
      return buildResult(responsePacket);
    } catch (MeasurementError error) {
      sock.close();
      throw error;
    }
  }

  /**
   * Analyse a response packet and create an {@link UDPBurstResult} from its content.
   *
   * @param responsePacket The packet to build results from.
   * @return The {@link UDPBurstResult} built from the response.
   * @throws MeasurementError If the given packet is not a response packet.
   */
  @NonNull
  private UDPBurstResult buildResult(UDPPacket responsePacket) throws MeasurementError {
    // Reconstruct UDP packet from flattened network data
    if (responsePacket.type == UDPPacket.PKT_RESPONSE) {
      int packetCount = responsePacket.packetNum;
      double outOfOrderRatio = (double) responsePacket.outOfOrderNum / responsePacket.packetNum;
      long jitter = responsePacket.timestamp;
      int lostCount = config.getUdpBurstCount() - packetCount;
      return new UDPBurstResult(TAG, this.startTimeTask, this.endTimeTask, config,
          packetCount, lostCount, outOfOrderRatio, jitter);
    } else {
      throw new MeasurementError(taskName, "Error: not a response packet! seq: " + responsePacket.seq);
    }
  }
}
