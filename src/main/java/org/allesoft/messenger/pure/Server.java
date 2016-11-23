package org.allesoft.messenger.pure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        InfiniThreadFactory.tryItNow(() -> {
            server.serverSocket = new ServerSocket(port);
            InfiniThreadFactory.infiniThread(() -> {
                Socket clientSocket = server.serverSocket.accept();
                server.sockets.add(clientSocket);
                new Thread(() -> {
                    try {
                        while (true) {
                            byte[] buf = waitPacketWithDBSizeHeader(clientSocket.getInputStream());
                            for (Socket connected : server.sockets) {
                                if (connected != clientSocket) {
                                    sendPacket(connected.getOutputStream(), buf);
                                }
                            }
                        }
                    } catch (Exception e) {
                        server.sockets.remove(clientSocket);
                        System.out.println("Exception: " + e);
                    }
                }).start();
            });
        });
        return server;
    }

    public static byte[] waitPacketWithDBSizeHeader(InputStream inputStream) throws IOException {
        int big = inputStream.read();
        if (big < 0) {
            return null;
        }
        int little = inputStream.read();
        if (little < 0) {
            return null;
        }
        int size = (big << 8) + little;
        byte[] packet = new byte[size];
        int received = 0;
        while (received < size) {
            int c = inputStream.read(packet, received, size - received);
            if (c < 0) {
                throw new RuntimeException("Network errors");
            }
            received += c;
        }
        return packet;
    }

    public static void sendPacket(OutputStream outputStream, byte[] packet) {
        try {
            byte[] sizeField = new byte[2];
            sizeField[0] = (byte) (packet.length >> 8);
            sizeField[1] = (byte) (packet.length & 0xff);
            outputStream.write(sizeField);
            outputStream.write(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
