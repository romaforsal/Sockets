package tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TicketServerV1 {
    public static void main(String[] args) {
        int port = 9090;
        int currentTicket = 1;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Ticket Server (Iterative Version) running on port " + port);

            while (true) {
                // Accepts a client and goes back to accept() after finishing
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected...");

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Reads a request. If the client (like Telnet) sends nothing, 
                // the server stays blocked here waiting for read()
                String request = reader.readLine(); 

                if (request != null && request.equalsIgnoreCase("Give me a ticket")) {
                    // Responds with incremental number
                    writer.println("Your ticket number is: " + currentTicket++);
                } else {
                    writer.println("Invalid request.");
                }

                // Closes connection
                clientSocket.close();
                System.out.println("Client disconnected. Waiting for next client...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}