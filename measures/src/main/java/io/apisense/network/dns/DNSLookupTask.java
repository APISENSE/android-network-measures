package io.apisense.network.dns;

import android.support.annotation.NonNull;
import android.util.Log;

import org.xbill.DNS.DClass;
import org.xbill.DNS.DNSClient;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.PublicTCPClient;
import org.xbill.DNS.PublicUDPClient;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.apisense.network.Measurement;
import io.apisense.network.MeasurementError;

/**
 * Measures the DNS lookup time
 */
public class DNSLookupTask extends Measurement {
  public static final String TAG = "DNSLookup";
  private final DNSLookupConfig config;

  public DNSLookupTask(DNSLookupConfig config) {
    super(TAG);
    this.config = config;
  }

  /**
   * Parse the raw response bytes to a wrapped dns answer.
   *
   * @param useTCP    If the client is currently using TCP.
   * @param respBytes The raw response bytes.
   * @return The parsed {@link Message}.
   * @throws MeasurementError   If the response could not be parsed.
   * @throws TruncatedException If the response is truncated while client is using UDP.
   */
  @NonNull
  private Message parseMessage(boolean useTCP, byte[] respBytes)
      throws TruncatedException, MeasurementError {
    Message response;
    try {
      response = new Message(respBytes);
      Log.d(TAG, "Successfully parsed response");
      // if the response was truncated, then re-query over TCP
      if (!useTCP && response.getHeader().getFlag(Flags.TC)) {
        throw new TruncatedException();
      }
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Problem trying to parse dns packet", e);
    }
    return response;
  }

  /**
   * Initialize and connect the dns TCP or UDP client.
   *
   * @param server  The server to connect the client to.
   * @param useTCP  Tells if the client should be TCP or UDP.
   * @param endTime The request timeout.
   * @return The initialized client.
   * @throws MeasurementError If any error occurred during client creation or connection.
   */
  @NonNull
  private DNSClient connectClient(String server, boolean useTCP, long endTime)
      throws MeasurementError {
    DNSClient client;
    try {
      if (useTCP) {
        client = new PublicTCPClient(endTime);
      } else {
        client = new PublicUDPClient(endTime);
        client.bind(null);
      }
      SocketAddress addr = new InetSocketAddress(server, 53);
      client.connect(addr);
    } catch (IOException e) {
      throw new MeasurementError(taskName, "Error while creating client", e);
    }
    Log.d(TAG, "Initialized client");
    return client;
  }

  /**
   * Send a request to the DNS client and return
   * the time when request was sent.
   *
   * @param client The client to send query on.
   * @param output The raw query to send.
   * @return The timestamp when request was successfully sent, -1 if unsuccessful.
   */
  private static long sendRequest(DNSClient client, byte[] output) {
    try {
      client.send(output);
    } catch (IOException e) {
      Log.e(TAG, "Error while sending DNS request", e);
      return -1;
    }
    return System.currentTimeMillis();
  }

  /**
   * Receive and return a DNS response from the server.
   *
   * @param client  The client to receive response from.
   * @param udpSize The maximum size of the response if UDP.
   * @return The raw response in a byte array, the array will be empty if nothing has been received.
   */
  @NonNull
  private static byte[] receiveResponse(DNSClient client, int udpSize) {
    byte[] in = {};
    try {
      in = client.recv(udpSize);
    } catch (IOException e) {
      Log.d(TAG, "Problem while receiving packet ", e);
    }
    return in;
  }

  @Override
  public DNSLookupResult execute() throws MeasurementError {
    Log.d(TAG, "Running DNS lookup with configuration: " + config);
    Record question;
    try {
      question = Record.newRecord(Name.fromString(config.getTarget()),
          Type.value(config.getQtype()), DClass.value(config.getQclass()));
    } catch (TextParseException e) {
      throw new MeasurementError("Error constructing packet", e);
    }
    Message query = Message.newQuery(question);
    Log.v(TAG, "Constructed question: " + question);
    Log.v(TAG, "Constructed query: " + query);
    return sendMeasurement(query, config.isForceTCP());
  }

  /**
   * Put the query on the wire and wait for responses.
   *
   * @param query    The DNS query to send to the server.
   * @param forceTCP Tells whether we should force request to use TCP or not.
   * @return The lookup result.
   * @throws MeasurementError If anything goes wrong during DNS lookup.
   */
  @NonNull
  private DNSLookupResult sendMeasurement(Message query, boolean forceTCP) throws MeasurementError {
    byte[] output = query.toWire();
    OPTRecord opt = query.getOPT();

    int udpSize = opt != null ? opt.getPayloadSize() : 512;
    boolean useTCP = forceTCP || (output.length > udpSize);
    long timeout = System.currentTimeMillis() + config.getTimeout();

    DNSClient client = connectClient(config.getServer(), useTCP, timeout);

    // Sending request
    long startTime;
    do {
      startTime = sendRequest(client, output);
    } while (startTime == -1 && System.currentTimeMillis() < timeout);

    // Retrieving result
    byte[] respBytes;
    do {
      respBytes = receiveResponse(client, udpSize);
    } while (respBytes.length == 0 && System.currentTimeMillis() < timeout);
    long endTime = System.currentTimeMillis();

    // Parsing response
    DNSLookupResult result;
    try {
      result = DNSLookupResult.fromMessage(config, parseMessage(useTCP, respBytes), startTime, endTime);
    } catch (TruncatedException e) {
      Log.d(TAG, "UDP response truncated, re-querying over TCP");
      try {
        client.cleanup();
      } catch (IOException err) {
        Log.w(TAG, "Unable to clean client while retrying over TCP", e);
      }
      return sendMeasurement(query, true);
    }

    return result;
  }
}
