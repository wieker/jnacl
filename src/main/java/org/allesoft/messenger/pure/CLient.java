package org.allesoft.messenger.pure;

import org.abstractj.kalium.encoders.Hex;

import java.net.Socket;

/**
 * Created by kabramovich on 15.11.2016.
 */
public class Client {
    private Socket socket;

    private Client() {

    }

    public static Client connectClient(String address, int port) {
        try {
            Client client = new Client();
            client.socket = new Socket(address, port);
            System.out.println("Client started " + client.socket.toString());
            new Thread(() -> {
                try {
                    byte[] buf = new byte[1024];
                    while (true) {
                        int c = client.socket.getInputStream().read(buf, 0, buf.length);
                        if (c < 0) {
                            return;
                        }
                        System.out.println("Received by " + client.socket.toString());
                        System.out.println(Hex.HEX.encode(buf));
                        client.socket.getOutputStream().write(buf, 0, c);
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }).start();
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(byte[] packet) {
        try {
            socket.getOutputStream().write(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
