#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>

#define SERVER_IP "127.0.0.1" // Alamat localhost (ubah jika server berada di komputer lain)
#define PORT 8080
#define BUFFER_SIZE 1024

int main() {
    int sockfd;
    char buffer[BUFFER_SIZE];
    char message[BUFFER_SIZE];
    struct sockaddr_in server_addr;
    socklen_t addr_len;
    ssize_t n;

    // 1. Membuat UDP socket (SOCK_DGRAM)
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("Gagal membuat socket");
        exit(EXIT_FAILURE);
    }
    printf("UDP Client socket berhasil dibuat.\n");

    // 2. Mengisi informasi alamat network server
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    
    // Mengonversi alamat IP teks ke format biner yang dipahami sistem
    if (inet_pton(AF_INET, SERVER_IP, &server_addr.sin_addr) <= 0) {
        perror("Alamat IP tidak valid");
        close(sockfd);
        exit(EXIT_FAILURE);
    }

    addr_len = sizeof(server_addr);

    // 3. Loop interaktif untuk mengirim dan menerima pesan
    printf("Ketik sesuatu dan tekan Enter untuk mengirim (Ketik 'exit' untuk keluar):\n\n");
    while (1) {
        printf("Klien > ");
        if (fgets(message, sizeof(message), stdin) == NULL) {
            break;
        }

        // Menghapus karakter newline (\n) di akhir input
        message[strcspn(message, "\n")] = '\0';

        // Keluar dari loop jika pengguna mengetik 'exit'
        if (strcmp(message, "exit") == 0) {
            break;
        }

        // Jangan kirim jika input kosong
        if (strlen(message) == 0) {
            continue;
        }

        // 4. Mengirim data ke server menggunakan sendto()
        // Di sini kita wajib menyertakan target alamat server_addr
        ssize_t sent = sendto(sockfd, message, strlen(message), 0,
                              (const struct sockaddr *)&server_addr, addr_len);
        if (sent < 0) {
            perror("Gagal mengirim data");
            continue;
        }

        // 5. Menerima kembali data (Echo) dari server menggunakan recvfrom()
        n = recvfrom(sockfd, buffer, BUFFER_SIZE - 1, 0,
                     (struct sockaddr *)&server_addr, &addr_len);
        if (n < 0) {
            perror("Gagal menerima data");
            continue;
        }

        // Menutup string dengan null-terminator agar aman dicetak
        buffer[n] = '\0';
        printf("Server > %s\n", buffer);
    }

    // 6. Menutup socket saat selesai
    printf("Menutup koneksi klien.\n");
    close(sockfd);
    return 0;
}

