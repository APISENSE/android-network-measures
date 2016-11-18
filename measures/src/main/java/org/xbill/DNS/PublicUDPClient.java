package org.xbill.DNS;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Wrap javadns UDPClient since it is only package visible.
 */
public final class PublicUDPClient implements DNSClient {
  private final UDPClient client;

  public PublicUDPClient(long endTime) throws IOException {
    client = new UDPClient(endTime);
  }

  @Override
  public void bind(SocketAddress addr) throws IOException {
    client.bind(addr);
  }

  @Override
  public void connect(SocketAddress addr) throws IOException {
    client.connect(addr);
  }

  @Override
  public void send(byte[] data) throws IOException {
    client.send(data);
  }

  @Override
  public byte[] recv(int max) throws IOException {
    return client.recv(max);
  }

  @Override
  public void cleanup() throws IOException {
    client.cleanup();
  }
}
