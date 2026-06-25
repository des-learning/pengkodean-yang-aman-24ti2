import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class VirtualThreadTcpServer {

  private static final int PORT = 8080;

  public static void main(String[] args) {
    System.out.println("Server listening on port " + PORT);

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {

      while (true) {
        Socket clientSocket = serverSocket.accept();

        Thread.startVirtualThread(() -> handleClient(clientSocket));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleClient(Socket socket) {
    System.out.println("Connected: " + socket.getRemoteSocketAddress());

    try (
        socket;
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(
            socket.getOutputStream(), true)) {

      writer.println("Welcome!");

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.printf("[%s] %s%n",
            socket.getRemoteSocketAddress(),
            line);

        writer.println("Echo: " + line);

        if ("exit".equalsIgnoreCase(line)) {
          break;
        }
      }

    } catch (IOException e) {
      System.out.println("Client disconnected: "
          + socket.getRemoteSocketAddress());
    }
  }
}
