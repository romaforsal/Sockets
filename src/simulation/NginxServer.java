package simulation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NginxServer {
    public static void main(String[] args) {
        int port = 8081;

        try {
            // Create a non-blocking server socket channel [cite: 242]
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));

            // Create the Selector (The Event Loop equivalent to epoll/select) [cite: 243, 250-253]
            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Nginx Simulation Server (Event-Driven / Non-Blocking) running on port " + port);
            int activeConnections = 0;

            // The Event Loop [cite: 243]
            while (true) {
                // Wait for an event (accept, read, etc.) [cite: 260]
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) continue;

                    // If the event is a new connection [cite: 247]
                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        // Register the new socket into the event loop for reading [cite: 249]
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        
                        activeConnections++;
                        System.out.println("Accepted new connection without creating a thread. Total: " + activeConnections);
                    } 
                    // If the event is data ready to be read [cite: 258]
                    else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        // Here we would read. For simulation, we just close it to mimic the quick client.
                        clientChannel.close();
                        activeConnections--;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}