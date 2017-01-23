package io.apisense.network.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import io.apisense.network.MeasurementError;

/**
 * @author Hongyi Yao (hyyao@umich.edu) A helper structure for packing and unpacking network
 *         message
 */
final class UDPPacket {
  static final int PKT_ERROR = 1;
  static final int PKT_RESPONSE = 2;
  static final int PKT_DATA = 3;
  static final int PKT_REQUEST = 4;

  /**
   * Name of the task to referecence in case of a {@link MeasurementError}.
   */
  private final String taskName;

  /**
   * Type of the {@link UDPPacket},
   * may be {@link UDPPacket#PKT_ERROR}, {@link UDPPacket#PKT_RESPONSE},
   * {@link UDPPacket#PKT_DATA}, or {@link UDPPacket#PKT_REQUEST}
   */
  public final int type;

  /**
   * Number of burst to send.
   */
  public final int burstCount;
  /**
   * Number of packet received in a wrong order
   */
  public final int outOfOrderNum;
  /**
   * Data packet: local timestamp
   * Response packet: jitter
   */
  public final long timestamp;
  /**
   * Size of each UDP packet to send.
   */
  public final int packetSize;
  /**
   * Request sequence number.
   */
  public final int seq;
  /**
   * Time to wait between each packet.
   */
  public final int udpInterval;
  /**
   * Identification of the packet,
   * determine its order in the sequence.
   */
  public int packetNum;

  /**
   * Build from scratch an {@link UDPPacket} from the given configuration.
   *
   * @param taskName Name of the task to referecence in case of a {@link MeasurementError}
   * @param type     Type of packet to build.
   * @param config   Burst configuration to set in the packet.
   */
  public UDPPacket(String taskName, int type, UDPBurstConfig config) {
    this.taskName = taskName;
    this.type = type;
    this.burstCount = config.getUdpBurstCount();
    this.packetSize = config.getPacketSizeByte();
    this.udpInterval = (int) Math.ceil(config.getUdpInterval() / 1000); // convert µs to ms
    this.seq = 0;

    // Unrelevant properties
    outOfOrderNum = 0;
    timestamp = System.currentTimeMillis();

  }

  /**
   * Unpack received message and fill the structure
   *
   * @param taskName Name of the task to referecence in case of a {@link MeasurementError}
   * @param rawdata  Network message
   * @throws MeasurementError stream reader failed
   */
  public UDPPacket(String taskName, byte[] rawdata) throws MeasurementError {
    this.taskName = taskName;
    ByteArrayInputStream byteIn = new ByteArrayInputStream(rawdata);
    DataInputStream dataIn = new DataInputStream(byteIn);

    try {
      type = dataIn.readInt();
      burstCount = dataIn.readInt();
      packetNum = dataIn.readInt();
      outOfOrderNum = dataIn.readInt();
      timestamp = dataIn.readLong();
      packetSize = dataIn.readInt();
      seq = dataIn.readInt();
      udpInterval = dataIn.readInt();
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Fetch payload failed! " + e.getMessage());
    }

    try {
      byteIn.close();
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Error closing inputstream!");
    }
  }

  public DatagramPacket createDatagram(InetAddress target, int port) throws MeasurementError {
    byte[] data = getByteArray();
    return new DatagramPacket(data, data.length, target, port);
  }

  /**
   * Pack the structure to the network message
   *
   * @return the network message in byte[]
   * @throws MeasurementError stream writer failed
   */
  public byte[] getByteArray() throws MeasurementError {

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(byteOut);

    try {
      dataOut.writeInt(type);
      dataOut.writeInt(burstCount);
      dataOut.writeInt(packetNum);
      dataOut.writeInt(outOfOrderNum);
      dataOut.writeLong(timestamp);
      dataOut.writeInt(packetSize);
      dataOut.writeInt(seq);
      dataOut.writeInt(udpInterval);
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Create rawpacket failed! " + e.getMessage());
    }

    byte[] rawPacket = byteOut.toByteArray();

    try {
      byteOut.close();
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Error closing outputstream!");
    }
    return rawPacket;
  }
}
