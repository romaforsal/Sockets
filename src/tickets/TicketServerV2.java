package tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketServerV2 {
    // We use an AtomicInteger to avoid race conditions 
    // when multiple threads ask for a ticket at the exact same time
    private static AtomicInteger currentTicket = new AtomicInteger(1);

    public static void main(String[] args) {
        int port = 9090;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Concurrent Ticket Server (Version 2) running on port " + port);

            // accept() in a loop 
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // For each client -> create and start a new Thread 
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // This method isolates the logic for each client in its own thread
    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // If a client (like Telnet) hangs here, it only blocks THIS thread, 
            // not the main server loop
            String request = reader.readLine();

            if (request != null && request.equalsIgnoreCase("Give me a ticket")) {
                // getAndIncrement() safely gives the current value and adds 1
                int ticketNumber = currentTicket.getAndIncrement();
                writer.println("Your ticket number is: " + ticketNumber);
                System.out.println("Ticket " + ticketNumber + " dispatched.");
            } else {
                writer.println("Invalid request.");
            }

        } catch (IOException e) {
            System.out.println("Error handling client connection.");
        } finally {
            // Always close the client socket when done
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}