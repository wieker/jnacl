package org.allesoft.messenger.jclient;

import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.encoders.Hex;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by kabramovich on 14.11.2016.
 */
public class PlainReceiver extends Receiver {
    Socket socket;

    public PlainReceiver(String address, Integer port) throws Exception {
        System.out.println("connecting");
        socket = new Socket(address, port);
        System.out.println("connected" + socket);
        new Thread(() -> {
            try {
                byte[] header = new byte[100];
                while (true) {
                    socket.getInputStream().read(header, 0, header.length);
                    System.out.println("Prescan peer: " + Hex.HEX.encode(header));
                    Thread.sleep(1000l);;
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void sendPacket(byte[] payload) throws IOException {
        socket.getOutputStream().write(payload);
    }
}
