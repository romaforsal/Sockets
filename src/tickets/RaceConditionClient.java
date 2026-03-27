package tickets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RaceConditionClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 9090;
        int numberOfRequests = 50;

        System.out.println("Starting " + numberOfRequests + " simultaneous ticket requests...");

        for (int i = 0; i < numberOfRequests; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket(serverAddress, port);
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    writer.println("Give me a ticket");
                    String response = reader.readLine();
                    System.out.println(response);

                } catch (IOException e) {
                    System.out.println("Connection failed.");
                }
            }).start();
        }
    }
}