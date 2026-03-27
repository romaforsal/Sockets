package simulation;

import java.io.IOException;
import java.net.Socket;

public class SimulationClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        // Change to 8081 for Nginx test
        int port = 8080; 
        int totalClients = 500;

        System.out.println("Starting " + totalClients + " simulated clients...");

        for (int i = 0; i < totalClients; i++) {
            final int clientId = i;
            new Thread(() -> {
                try (Socket socket = new Socket(serverAddress, port)) {
                    // Just connect, hold for a moment, and disconnect
                    Thread.sleep(100); 
                    // System.out.println("Client " + clientId + " connected successfully.");
                } catch (IOException | InterruptedException e) {
                    System.out.println("Client " + clientId + " failed to connect.");
                }
            }).start();
        }
    }
}