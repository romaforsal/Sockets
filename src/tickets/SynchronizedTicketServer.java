package tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SynchronizedTicketServer {
    // Standard primitive integer, vulnerable to race conditions if not synchronized
    private static int currentTicket = 1;

    public static void main(String[] args) {
        int port = 9090;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Synchronized Ticket Server running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = reader.readLine();
            if (request != null && request.equalsIgnoreCase("Give me a ticket")) {
                // Call the synchronized method
                int ticketNumber = getNextTicket();
                writer.println("Your ticket number is: " + ticketNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // The 'synchronized' keyword ensures only one thread can execute this at a time
    private static synchronized int getNextTicket() {
        return currentTicket++;
    }
}