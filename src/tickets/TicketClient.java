package tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TicketClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 9090;

        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Requesting a ticket...");
            
            // Send specific request
            writer.println("Give me a ticket");
            
            // Read response
            String response = reader.readLine();
            System.out.println("Server response: " + response);

        } catch (IOException e) {
            System.out.println("Could not connect to server. Is it blocked by another client?");
        }
    }
}