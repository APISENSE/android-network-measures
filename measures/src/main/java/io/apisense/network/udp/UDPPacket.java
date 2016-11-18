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
     * Identification of the packet,
     * determine its order in the sequence.
     */
    public int packetNum;

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

    public UDPPacket(int type, UDPBurstConfig config) {
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
     * @param rawdata network message
     * @throws MeasurementError stream reader failed
     */
    public UDPPacket(byte[] rawdata) throws MeasurementError {
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
            throw new MeasurementError("Fetch payload failed! " + e.getMessage());
        }

        try {
            byteIn.close();
        } catch (IOException e) {
            throw new MeasurementError("Error closing inputstream!");
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
            throw new MeasurementError("Create rawpacket failed! " + e.getMessage());
        }

        byte[] rawPacket = byteOut.toByteArray();

        try {
            byteOut.close();
        } catch (IOException e) {
            throw new MeasurementError("Error closing outputstream!");
        }
        return rawPacket;
    }
}
