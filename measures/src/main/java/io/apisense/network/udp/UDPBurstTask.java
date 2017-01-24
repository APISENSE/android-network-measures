package io.apisense.network.udp;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import io.apisense.network.Measurement;
import io.apisense.network.MeasurementError;

/**
 * Abstract class containing common code used for UDP upload and download tests
 */
public abstract class UDPBurstTask extends Measurement {
  protected static final int DEFAULT_PORT = 31341;

  /**
   * round-trip delay, in msec.
   */
  protected static final int RCV_TIMEOUT = 2000;

  protected long startTimeTask; //time in milliseconds
  protected long endTimeTask; //time in milliseconds

  protected UDPBurstConfig config;


  public UDPBurstTask(String taskName, UDPBurstConfig udpBurstConfig) {
    super(taskName);
    this.config = udpBurstConfig;
  }

  /**
   * Wait for the socket to retrieve a response to the previous burst.
   *
   * @param sock The socket to listen through.
   * @return An {@link UDPPacket} containing the response.
   * @throws MeasurementError If any error occurred during measurement.
   */
  @NonNull
  protected UDPPacket retrieveResponseDatagram(DatagramSocket sock) throws MeasurementError {
    byte[] buffer = new byte[config.getPacketSizeByte()];
    DatagramPacket recvpacket = new DatagramPacket(buffer, buffer.length);

    try {
      sock.receive(recvpacket);
    } catch (SocketException e1) {
      throw new MeasurementError(taskName, "Timed out reading from " + config.getTargetIp(), e1);
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Error reading from " + config.getTargetIp(), e);
    }

    return new UDPPacket(taskName, recvpacket.getData());
  }

  /**
   * Opens a datagram (UDP) socket
   *
   * @return a datagram socket used for sending/receiving
   * @throws MeasurementError if an error occurs
   */
  protected DatagramSocket openSocket() throws MeasurementError {
    DatagramSocket sock;

    // Open datagram socket
    try {
      sock = new DatagramSocket();
      sock.setSoTimeout(RCV_TIMEOUT);
    } catch (SocketException e) {
      throw new MeasurementError(taskName, "Socket creation failed", e);
    }

    return sock;
  }
}
