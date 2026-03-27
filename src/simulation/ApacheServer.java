package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ApacheServer {
    public static void main(String[] args) {
        int port = 8080;
        int activeThreads = 0;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Apache Simulation Server (Blocking I/O) running on port " + port);

            while (true) {
                // Blocks waiting for a connection [cite: 230]
                Socket clientSocket = serverSocket.accept();
                activeThreads++;
                System.out.println("New connection. Total Active Threads: " + activeThreads);

                // Creates a new thread for EVERY connection 
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Simulating blocking I/O work 
                Thread.sleep(200); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}