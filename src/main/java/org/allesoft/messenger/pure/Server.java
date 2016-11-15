package org.allesoft.messenger.pure;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 15.11.2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private List<Socket> sockets = new ArrayList<>();

    private Server() {

    }

    public static Server initServer(int port) {
        Server server = new Server();
        try {
            server.serverSocket = new ServerSocket(port);
            new Thread(() -> {
                try {
                    while (true) {
                        Socket clientSocket = server.serverSocket.accept();
                        server.sockets.add(clientSocket);
                        new Thread(() -> {
                            try {
                                byte[] buf = new byte[1024];
                                while (true) {
                                    int c = clientSocket.getInputStream().read(buf, 0, buf.length);
                                    if (c < 0) {
                                        server.sockets.remove(clientSocket);
                                        return;
                                    }
                                    for (Socket connected : server.sockets) {
                                        if (connected != clientSocket) {
                                            connected.getOutputStream().write(buf, 0, c);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                server.sockets.remove(clientSocket);
                                System.out.println("Exception: " + e);
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return server;
    }

    public static byte[] waitPacketWithDBSizeHeader(InputStream inputStream) throws IOException {
        int size = inputStream.read() << 8 + inputStream.read();
        byte[] packet = new byte[size];
        int received = 0;
        while (received < size) {
            int c = inputStream.read(packet, received, size - received);
            if (c < 0) {
                throw new RuntimeException("Network errors");
            }
        }
        return packet;
    }
}
