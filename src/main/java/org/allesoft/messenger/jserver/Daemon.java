package org.allesoft.messenger.jserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class Daemon {
    List<Socket> clients = new ArrayList<>();

    public void openSocket(Integer port) {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.getOutputStream().write(2);
                    System.out.println("Remote client: " + clientSocket.getRemoteSocketAddress());
                    System.out.println("Prescan server: " + clientSocket.getInputStream().read());
                    System.out.println("Accepted socket");
                    clients.add(clientSocket);
                    workWith(clientSocket);
                }
            } catch (IOException x) {
                System.out.println("IOException in core");
            }}).start();
    }

    public void workWith(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        Runnable logic = () -> {
            while (true) {
                byte[] packet = loopPacket(inputStream);
                if (packet == null) {
                    cleanupClient(socket);
                    return;
                }
                try {
                    for (Socket s : clients) {
                        if (s != socket) {
                            s.getOutputStream().write(packet);
                        }
                    }
                } catch (IOException e) {
                    cleanupClient(socket);
                    return;
                }
            }
        };
        new Thread(logic).start();
    }

    private void cleanupClient(Socket socket) {
        System.out.println("write error");
        clients.remove(socket);
        try {
            socket.close();
        } catch (IOException e1) {
        }
    }

    public static byte[] loopPacket(InputStream inputStream) {
        byte[] packet = new byte[256];
        int completed = 0;
        try {
            while (true) {
                int done = inputStream.read(packet, completed, packet.length - completed);
                if (done < 1) {
                    return null;
                }
                completed += done;
                if (completed == packet.length) {
                    return packet;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception in thread");
            return null;
        }
    }

    public static void main(String[] args) {
        new Daemon().openSocket(50505);
    }
}
