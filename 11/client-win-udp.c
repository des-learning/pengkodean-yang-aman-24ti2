#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>
#include <ws2tcpip.h>

// Menautkan library Winsock secara otomatis jika menggunakan MSVC / Visual Studio
#pragma comment(lib, "ws2_32.lib")

#define PORT 8080
#define BUFFER_SIZE 1024

int main() {
    WSADATA wsaData;
    SOCKET sockfd;
    char buffer[BUFFER_SIZE];
    struct sockaddr_in server_addr, client_addr;
    int addr_len;
    int n;

    // 1. Inisialisasi Winsock
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        printf("Gagal inisialisasi Winsock. Error Code: %d\n", WSAGetLastError());
        return 1;
    }

    // 2. Membuat UDP socket
    sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sockfd == INVALID_SOCKET) {
        printf("Gagal membuat socket. Error Code: %d\n", WSAGetLastError());
        WSACleanup();
        return 1;
    }
    printf("Winsock Server socket berhasil dibuat.\n");

    // 3. Mengisi informasi alamat server
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    // 4. Bind socket ke port
    if (bind(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == SOCKET_ERROR) {
        printf("Bind gagal. Error Code: %d\n", WSAGetLastError());
        closesocket(sockfd);
        WSACleanup();
        return 1;
    }
    printf("Server sukses terikat pada port %d.\n", PORT);
    printf("Menunggu pesan masuk...\n");

    // 5. Loop untuk menerima dan mengirim balik data (Echo)
    while (1) {
        addr_len = sizeof(client_addr);

        n = recvfrom(sockfd, buffer, BUFFER_SIZE - 1, 0, 
                     (struct sockaddr *)&client_addr, &addr_len);
        
        if (n == SOCKET_ERROR) {
            printf("Gagal menerima data. Error Code: %d\n", WSAGetLastError());
            continue;
        }

        buffer[n] = '\0';
        
        // Mengonversi alamat IP klien ke teks format Windows kompatibel
        char client_ip[INET_ADDRSTRLEN];
        inet_ntop(AF_INET, &(client_addr.sin_addr), client_ip, INET_ADDRSTRLEN);
        printf("Diterima %d byte dari [%s:%d]: %s\n", 
               n, client_ip, ntohs(client_addr.sin_port), buffer);

        // Kirim balik data ke klien
        int sent = sendto(sockfd, buffer, n, 0, 
                          (struct sockaddr *)&client_addr, addr_len);
        if (sent == SOCKET_ERROR) {
            printf("Gagal mengirim data balik. Error Code: %d\n", WSAGetLastError());
        }
    }

    // 6. Pembersihan (Kode ini tidak terjangkau karena loop infinite, tetapi ideal secara struktur)
    closesocket(sockfd);
    WSACleanup();
    return 0;
}

