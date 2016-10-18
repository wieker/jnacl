package org.allesoft.jserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class Daemon {
    List<Socket> clients = new ArrayList<>();

    public void openSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(50505);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                workWith(clientSocket);
            }
        } catch (IOException x) {
            System.out.println("IOException in core");
        }
    }

    public void workWith(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        Runnable logic = () -> {
            while (true) {
                byte[] packet = loopPacket(inputStream);
                try {
                    for (Socket s : clients) {
                        if (s != socket) {
                            s.getOutputStream().write(packet);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("write error");
                }
            }
        };
        new Thread(logic).start();
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
        new Daemon().openSocket();
    }
}
