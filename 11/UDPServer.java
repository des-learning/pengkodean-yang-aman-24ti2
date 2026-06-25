import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
  private static final int PORT = 8080;
  private static final int BUFFER_SIZE = 1024;

  public static void main(String[] args) {
    // Membuka DatagramSocket pada port tertentu untuk mendengarkan lalu lintas
    try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
      System.out.println("UDP Server Java berhasil berjalan pada port " + PORT);
      System.out.println("Menunggu pesan masuk dari klien...");

      byte[] receiveBuffer = new byte[BUFFER_SIZE];

      while (true) {
        // 1. Menyiapkan paket kosong untuk menampung data masuk
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        // 2. Menerima paket (Metode ini memblokir eksekusi sampai paket tiba)
        serverSocket.receive(receivePacket);

        // Extract data teks dari byte array paket yang diterima
        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

        // Mendapatkan metadata informasi pengirim jaringan
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();

        System.out.printf("Diterima dari [%s:%d]: %s%n",
            clientAddress.getHostAddress(), clientPort, message);

        // 3. Mengirimkan balik data yang sama (Echo) ke pengirim asli
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
            sendBuffer,
            sendBuffer.length,
            clientAddress,
            clientPort);

        serverSocket.send(sendPacket);
      }

    } catch (SocketException e) {
      System.err.println("Gagal membuka socket: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Terjadi kesalahan I/O: " + e.getMessage());
    }
  }
}
