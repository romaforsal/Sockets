package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiChatClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 8888;

        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the Multi-Room Chat. Type your messages (or 'EXIT' to quit).");

            // Thread to listen for incoming messages simultaneously
            Thread listenerThread = new Thread(new IncomingListener(socket));
            listenerThread.start();

            // Main thread to send messages
            String userInput;
            while (true) {
                userInput = scanner.nextLine();
                out.println(userInput);

                if (userInput.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }
            
            System.exit(0);

        } catch (IOException e) {
            System.out.println("Could not connect to the server.");
        }
    }

    private static class IncomingListener implements Runnable {
        private Socket socket;

        public IncomingListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }
}