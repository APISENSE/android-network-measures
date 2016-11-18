package org.xbill.DNS;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Common interface to use UDP and TCP clients.
 */
public interface DNSClient {
  void bind(SocketAddress addr) throws IOException;

  void connect(SocketAddress addr) throws IOException;

  void send(byte[] data) throws IOException;

  byte[] recv(int max) throws IOException;

  void cleanup() throws IOException;
}
