package io.apisense.network.tcp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import io.apisense.network.MeasurementError;
import io.apisense.network.MeasurementResult;

/**
 * Measurement class used to realise an TCP upload test
 */
public class TCPUploadTask extends TCPThroughputTask {
    private static final String TAG = "TCPUploadTask";
    private static final int PORT_UPLINK = 6002;
    private static final String UPLINK_FINISH_MSG = "*";
    private static final long MB_TO_B = 1048576;

    public TCPUploadTask(TCPThroughputConfig tcpThroughputConfig) {
        super(tcpThroughputConfig);
    }

    /**
     * {@inheritDoc}
     *
     * @return A {@link TCPThroughputResult} object containing information on the TCP upload test.
     * @throws MeasurementError {@inheritDoc}
     */
    public MeasurementResult execute() throws MeasurementError {
        Log.d(TAG, "Start");
        Socket tcpSocket = buildUpSocket(config.getTarget(), PORT_UPLINK);

        OutputStream oStream;
        InputStream iStream;

        try {
            oStream = tcpSocket.getOutputStream();
            iStream = tcpSocket.getInputStream();
        } catch (IOException e) {
            throw new MeasurementError("Unable to open stream", e);
        }


        this.taskStartTime = System.currentTimeMillis();
        sendData(oStream);
        this.taskEndTime = System.currentTimeMillis();
        Log.d(TAG, "Finished");

        try {
            retrieveResult(iStream);
        } catch (OutOfMemoryError e) { // TODO: See if this catch clause is really necessary
            throw new MeasurementError("Detect out of memory during Uplink task.", e);
        } finally {
            closeStream(oStream);
            closeStream(iStream);
            closeSocket(tcpSocket);
        }
        return new TCPThroughputResult(TAG, this.taskStartTime, this.taskEndTime,
                config, this.samplingResults, this.dataConsumedAfterSlowStart);
    }

    /**
     * Update samplingResults with the received packet.
     *
     * @param iStream The input stream to receive the result packet from.
     * @throws MeasurementError If the interaction with stream fails.
     */
    private void retrieveResult(InputStream iStream) throws MeasurementError {
        String message;
        int resultMsgLen;
        try {
            // read from server side results
            byte[] resultMsg = new byte[BUFFER_SIZE];
            resultMsgLen = iStream.read(resultMsg, 0, resultMsg.length);
            message = new String(resultMsg);
        } catch (IOException e) {
            throw new MeasurementError("Unable to retrieve upload result", e);
        }

        if (!message.isEmpty() && resultMsgLen > 0) {
            String resultMsgStr = message.substring(0, resultMsgLen);
            // Sample result string is "1111.11#2222.22#3333.33";
            String[] results = resultMsgStr.split("#");
            for (String result : results) {
                this.addSamplingResult(Double.valueOf(result));
            }
        }
    }

    /**
     * Send the required quantity of data on the given {@link Socket}.
     *
     * @param oStream The output stream to write onto.
     * @return The quantity of sent bytes.
     * @throws MeasurementError If the interaction with stream fails.
     */
    private long sendData(OutputStream oStream) throws MeasurementError {
        byte[] uplinkBuffer = new byte[config.getPktSizeUpBytes()];
        this.genRandomByteArray(uplinkBuffer);
        long pktSizeSent = 0;

        try {
            do {
                sendMessageOnStream(uplinkBuffer, oStream);
                pktSizeSent += config.getPktSizeUpBytes();
            } while (pktSizeSent < config.getDataLimitMbUp() * MB_TO_B);

            // send last message with special content
            sendMessageOnStream(TCPUploadTask.UPLINK_FINISH_MSG.getBytes(), oStream);
        } catch (IOException e) {
            throw new MeasurementError("Unable to upload data", e);
        }
        return pktSizeSent;
    }

    /**
     * Write a message on the given {@link OutputStream}
     *
     * @param message The byte array to write.
     * @param stream  The stream to write onto.
     * @throws IOException If the interaction with stream fails.
     */
    private void sendMessageOnStream(byte[] message, OutputStream stream) throws IOException {
        stream.write(message, 0, message.length);
        stream.flush();
    }
}
