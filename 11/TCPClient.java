import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
  private static final String SERVER_IP = "127.0.0.1";
  private static final int PORT = 8080;

  public static void main(String[] args) {
    System.out.println("Mencoba terhubung ke TCP Server...");

    // 1. Membuat Socket dan otomatis melakukan jabat tangan (handshake) ke Server
    try (Socket socket = new Socket(SERVER_IP, PORT);
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      System.out.println("Berhasil terhubung ke server!");
      System.out.println("Ketik pesan dan tekan Enter (Ketik 'exit' untuk keluar):\n");

      String message;
      while (true) {
        System.out.print("Klien > ");
        message = userInput.readLine();

        if (message == null || message.equalsIgnoreCase("exit")) {
          break;
        }

        if (message.trim().isEmpty()) {
          continue;
        }

        // 2. Mengirimkan data teks ke server lewat OutStream
        out.println(message);

        // 3. Membaca balasan dari server (Bloking sampai server mengirimkan jawaban)
        String response = in.readLine();
        System.out.println("Server > " + response);
      }

      System.out.println("Memutuskan hubungan dari server.");

    } catch (IOException e) {
      System.err.println("Gagal terhubung atau terjadi kesalahan I/O: " + e.getMessage());
    }
  }
}
