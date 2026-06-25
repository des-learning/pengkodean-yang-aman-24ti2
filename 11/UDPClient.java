import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
  private static final String SERVER_IP = "127.0.0.1";
  private static final int PORT = 8080;
  private static final int BUFFER_SIZE = 1024;

  public static void main(String[] args) {
    // Membuka DatagramSocket tanpa parameter port (port acak dipilih otomatis oleh
    // OS)
    try (DatagramSocket clientSocket = new DatagramSocket();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

      InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
      byte[] receiveBuffer = new byte[BUFFER_SIZE];

      System.out.println("UDP Client Java berhasil dibuat.");
      System.out.println("Ketik sesuatu dan tekan Enter (Ketik 'exit' untuk keluar):\n");

      while (true) {
        System.out.print("Klien > ");
        String message = reader.readLine();

        if (message == null || message.equalsIgnoreCase("exit")) {
          break;
        }

        if (message.trim().isEmpty()) {
          continue;
        }

        // 1. Mengubah string menjadi byte dan membungkusnya ke dalam paket tujuan
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
            sendBuffer,
            sendBuffer.length,
            serverAddress,
            PORT);

        // 2. Mengirimkan paket lewat socket
        clientSocket.send(sendPacket);

        // 3. Menyiapkan kontainer kosong untuk menerima respons dari server
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        clientSocket.receive(receivePacket);

        // 4. Membaca dan menampilkan hasil respons ke layar console
        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Server > " + response);
      }

      System.out.println("Menutup koneksi klien.");

    } catch (SocketException e) {
      System.err.println("Kesalahan Socket: " + e.getMessage());
    } catch (UnknownHostException e) {
      System.err.println("Host server tidak dikenal: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Kesalahan I/O: " + e.getMessage());
    }
  }
}
