import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
  private static final int PORT = 8080;

  public static void main(String[] args) {
    // 1. Membuka ServerSocket pada port 8080
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("TCP Server Java berjalan pada port " + PORT);
      System.out.println("Menunggu klien terhubung...");

      // 2. Menerima koneksi masuk (Bloking call, menunggu sampai ada klien yang
      // connect)
      try (Socket clientSocket = serverSocket.accept()) {
        System.out.println("Klien terhubung dari: " + clientSocket.getRemoteSocketAddress());

        // 3. Menyiapkan stream untuk membaca data masuk dan mengirim data keluar
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // true = auto-flush

        String inputLine;
        // 4. Membaca pesan baris demi baris dari klien hingga klien memutus koneksi
        while ((inputLine = in.readLine()) != null) {
          System.out.println("Diterima dari klien: " + inputLine);

          // 5. Echo (mengirim balik) pesan yang sama ke klien
          out.println("Echo: " + inputLine);
        }

        System.out.println("Klien memutuskan koneksi.");
      }

    } catch (IOException e) {
      System.err.println("Terjadi kesalahan pada Server: " + e.getMessage());
    }
  }
}
