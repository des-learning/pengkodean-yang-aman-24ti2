#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>

#define PORT 8080
#define BUFFER_SIZE 1024

int main() {
    int sockfd;
    char buffer[BUFFER_SIZE];
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len;
    ssize_t n;

    // 1. Create a UDP socket (SOCK_DGRAM)
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }
    printf("UDP Server socket created successfully.\n");

    // 2. Clear structures and populate server network settings
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;         // IPv4 Address family
    server_addr.sin_port = htons(PORT);       // Host-to-network short byte order
    server_addr.sin_addr.s_addr = INADDR_ANY; // Bind to all available interfaces

    // 3. Bind the socket to the designated port
    if (bind(sockfd, (const struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        close(sockfd);
        exit(EXIT_FAILURE);
    }
    printf("Server successfully bound to port %d.\n", PORT);

    // 4. Enter infinite loop to receive and echo datagrams
    printf("Waiting for incoming client messages...\n");
    while (1) {
        addr_len = sizeof(client_addr);

        // Block until data arrives, populating client_addr with the sender's info
        n = recvfrom(sockfd, buffer, BUFFER_SIZE - 1, 0, 
                     (struct sockaddr *)&client_addr, &addr_len);
        
        if (n < 0) {
            perror("Receive failed");
            continue;
        }

        // Null-terminate the string received to print it safely
        buffer[n] = '\0';
        
        // Convert client IP from binary to string format for logging
        char client_ip[INET_ADDRSTRLEN];
        inet_ntop(AF_INET, &(client_addr.sin_addr), client_ip, INET_ADDRSTRLEN);
        printf("Received %ld bytes from [%s:%d]: %s\n", 
               (long)n, client_ip, ntohs(client_addr.sin_port), buffer);

        // 5. Echo back the exact payload to the client's return address
        ssize_t sent = sendto(sockfd, buffer, n, 0, 
                              (const struct sockaddr *)&client_addr, addr_len);
        if (sent < 0) {
            perror("Send back failed");
        }
    }

    // 6. Housekeeping (unreachable due to infinite loop, but good standard practice)
    close(sockfd);
    return 0;
}

