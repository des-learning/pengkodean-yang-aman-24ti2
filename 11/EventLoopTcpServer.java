import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class EventLoopTcpServer {

  private static final int PORT = 8080;
  private static final int BUFFER_SIZE = 1024;

  public static void main(String[] args) throws IOException {

    Selector selector = Selector.open();

    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.bind(new InetSocketAddress(PORT));
    serverChannel.configureBlocking(false);

    serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    System.out.println("Server listening on port " + PORT);

    while (true) {

      selector.select(); // blocks until an event occurs

      Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

      while (keys.hasNext()) {

        SelectionKey key = keys.next();
        keys.remove();

        try {

          if (key.isAcceptable()) {
            acceptConnection(selector, key);
          }

          if (key.isReadable()) {
            readData(key);
          }

        } catch (IOException e) {
          closeConnection(key);
        }
      }
    }
  }

  private static void acceptConnection(
      Selector selector,
      SelectionKey key) throws IOException {

    ServerSocketChannel server = (ServerSocketChannel) key.channel();

    SocketChannel client = server.accept();

    if (client == null) {
      return;
    }

    client.configureBlocking(false);

    ClientContext context = new ClientContext();

    client.register(
        selector,
        SelectionKey.OP_READ,
        context);

    System.out.println("Connected: "
        + client.getRemoteAddress());
  }

  private static void readData(
      SelectionKey key) throws IOException {

    SocketChannel client = (SocketChannel) key.channel();

    ClientContext context = (ClientContext) key.attachment();

    int bytesRead = client.read(context.buffer);

    if (bytesRead == -1) {
      closeConnection(key);
      return;
    }

    if (bytesRead == 0) {
      return;
    }

    context.buffer.flip();

    String message = StandardCharsets.UTF_8.decode(
        context.buffer).toString();

    context.buffer.clear();

    System.out.printf(
        "[%s] %s%n",
        client.getRemoteAddress(),
        message.trim());

    // Echo response
    ByteBuffer response = ByteBuffer.wrap(
        ("Echo: " + message).getBytes(StandardCharsets.UTF_8));

    while (response.hasRemaining()) {
      client.write(response);
    }
  }

  private static void closeConnection(
      SelectionKey key) {

    try {
      Channel channel = key.channel();

      if (channel instanceof SocketChannel client) {
        System.out.println(
            "Disconnected: "
                + client.getRemoteAddress());
      }

      channel.close();
      key.cancel();

    } catch (IOException ignored) {
    }
  }

  static class ClientContext {
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
  }
}
