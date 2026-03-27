package tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketServerV3 {
    // Atomic integer to prevent race conditions
    private static AtomicInteger currentTicket = new AtomicInteger(1);
    
    // Set the maximum number of simultaneous clients (backpressure) 
    private static final int MAX_SIMULTANEOUS_CLIENTS = 2;

    public static void main(String[] args) {
        int port = 9090;

        // Create a thread pool with a fixed number of threads 
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Thread Pool Ticket Server (Version 3) running on port " + port);
            System.out.println("Maximum simultaneous clients allowed: " + MAX_SIMULTANEOUS_CLIENTS);

            // accept() in an infinite loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connection detected...");

                // Instead of creating a new Thread, we pass the task to the ExecutorService 
                threadPool.execute(new ClientTask(clientSocket));
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            // Shut down the pool gracefully if the server stops
            threadPool.shutdown();
        }
    }

    // Runnable task that handles the client logic
    private static class ClientTask implements Runnable {
        private Socket clientSocket;

        public ClientTask(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = reader.readLine();

                if (request != null && request.equalsIgnoreCase("Give me a ticket")) {
                    int ticketNumber = currentTicket.getAndIncrement();
                    writer.println("Your ticket number is: " + ticketNumber);
                    System.out.println("Ticket " + ticketNumber + " assigned to a client.");
                } else {
                    writer.println("Invalid request.");
                }

            } catch (IOException e) {
                System.out.println("Client disconnected before finishing.");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}