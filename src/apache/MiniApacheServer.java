package apache;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MiniApacheServer {
    private static int port = 8080; 
    private static String documentRoot = "src/apache/www"; 
    private static String logFilePath = "src/apache/logs/access.log";

    public static void main(String[] args) {
        loadConfiguration();

        // Ensure the logs directory exists
        File logDir = new File("src/apache/logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Advanced Mini Apache Server running on port: " + port);
            System.out.println("Document Root: " + documentRoot);
            System.out.println("Logging to: " + logFilePath);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new HttpHandler(clientSocket, documentRoot, logFilePath)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void loadConfiguration() {
        File configFile = new File("src/apache/config/httpd.conf");
        if (configFile.exists()) {
            try (FileInputStream input = new FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(input);
                port = Integer.parseInt(props.getProperty("PORT", "8080"));
                documentRoot = props.getProperty("DOCUMENT_ROOT", "src/apache/www");
                logFilePath = props.getProperty("LOG_FILE", "src/apache/logs/access.log");
            } catch (IOException e) {
                System.out.println("Config error. Using defaults.");
            }
        }
    }

    // Synchronized method to safely write logs from multiple threads
    public static synchronized void writeLog(String logFilePath, String clientIP, String requestLine, int statusCode) {
        try (FileWriter fw = new FileWriter(logFilePath, true);
             PrintWriter logWriter = new PrintWriter(fw)) {
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logWriter.printf("[%s] IP: %s | Request: \"%s\" | Status: %d%n", timestamp, clientIP, requestLine, statusCode);
            
        } catch (IOException e) {
            System.err.println("Failed to write to log file.");
        }
    }
}

class HttpHandler implements Runnable {
    private Socket socket;
    private String documentRoot;
    private String logFilePath;

    public HttpHandler(Socket socket, String documentRoot, String logFilePath) {
        this.socket = socket;
        this.documentRoot = documentRoot;
        this.logFilePath = logFilePath;
    }

    @Override
    public void run() {
        String clientIP = socket.getInetAddress().getHostAddress();
        String requestLine = "UNKNOWN";
        int statusCode = 500;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(out, true)) {

            requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                statusCode = 400; // Bad Request (or dropped connection)
                requestLine = "Empty Browser Pre-fetch";
                return;
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length >= 2 && requestParts[0].equals("GET")) {
                String requestedPath = requestParts[1];
                
                if (requestedPath.equals("/")) {
                    requestedPath = "/index.html";
                }

                File fileToServe = new File(documentRoot + requestedPath);

                if (fileToServe.exists() && !fileToServe.isDirectory()) {
                    statusCode = 200;
                    writer.print("HTTP/1.1 200 OK\r\n");
                    writer.print("Content-Type: text/html\r\n\r\n");
                    writer.flush();
                    Files.copy(fileToServe.toPath(), out);
                    out.flush();
                } else {
                    statusCode = 404;
                    writer.print("HTTP/1.1 404 Not Found\r\n");
                    writer.print("Content-Type: text/html\r\n\r\n");
                    writer.print("<html><body><h1>404 Not Found</h1><p>File not found.</p></body></html>");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            // Write the outcome to the log file before closing
            MiniApacheServer.writeLog(logFilePath, clientIP, requestLine, statusCode);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}