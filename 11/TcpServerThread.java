import java.io.*;
import java.net.*;

public class TcpServerThread {

  private static final int PORT = 8080;

  public static void main(String[] args) {
    System.out.println("Server starting on port " + PORT);

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: "
            + clientSocket.getInetAddress());

        // Create a new thread for each client
        ClientHandler handler = new ClientHandler(clientSocket);
        new Thread(handler).start();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class ClientHandler implements Runnable {

  private final Socket socket;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(
            socket.getOutputStream(), true)) {
      writer.println("Welcome to the server!");

      String message;
      while ((message = reader.readLine()) != null) {
        System.out.println(
            "Received from " + socket.getInetAddress() +
                ": " + message);

        // Echo response
        writer.println("Server received: " + message);

        if ("exit".equalsIgnoreCase(message)) {
          break;
        }
      }

    } catch (IOException e) {
      System.out.println("Client disconnected: " + e.getMessage());
    } finally {
      try {
        socket.close();
      } catch (IOException ignored) {
      }
    }
  }
}
