import java.io.*;
import java.net.*;

public class ParentServer {

  private static final int PORT = 8080;

  public static void main(String[] args) throws Exception {

    ServerSocket server = new ServerSocket(PORT);

    System.out.println("Listening on " + PORT);

    while (true) {
      Socket client = server.accept();

      new Thread(() -> {
        try {
          handleClient(client);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();
    }
  }

  private static void handleClient(Socket client) throws Exception {

    Process process = new ProcessBuilder(
        "java",
        "Worker").start();

    Thread clientToWorker = new Thread(() -> {
      try (
          BufferedReader clientIn = new BufferedReader(
              new InputStreamReader(client.getInputStream()));

          BufferedWriter workerIn = new BufferedWriter(
              new OutputStreamWriter(process.getOutputStream()))) {
        String line;

        while ((line = clientIn.readLine()) != null) {
          workerIn.write(line);
          workerIn.newLine();
          workerIn.flush();
        }

        process.getOutputStream().close();

      } catch (IOException ignored) {
      }
    });

    Thread workerToClient = new Thread(() -> {
      try (
          BufferedReader workerOut = new BufferedReader(
              new InputStreamReader(process.getInputStream()));

          BufferedWriter clientOut = new BufferedWriter(
              new OutputStreamWriter(client.getOutputStream()))) {
        String line;

        while ((line = workerOut.readLine()) != null) {
          clientOut.write(line);
          clientOut.newLine();
          clientOut.flush();
        }

      } catch (IOException ignored) {
      }
    });

    clientToWorker.start();
    workerToClient.start();

    clientToWorker.join();
    workerToClient.join();

    process.waitFor();

    client.close();
  }
}
