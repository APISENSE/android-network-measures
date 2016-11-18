package org.xbill.DNS;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Wrap javadns TCPClient since it is only package visible.
 */
public final class PublicTCPClient implements DNSClient {
  private final TCPClient client;

  public PublicTCPClient(long endTime) throws IOException {
    client = new TCPClient(endTime);
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

  /**
   * Receive DNS data.
   *
   * @param max Not used in tcp implementation
   * @return The received content.
   * @throws IOException {@inheritDoc}
   */
  @Override
  public byte[] recv(int max) throws IOException {
    return client.recv();
  }

  @Override
  public void cleanup() throws IOException {
    client.cleanup();
  }
}
