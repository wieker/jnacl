package org.allesoft.messenger.pure;

import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Hex;
import org.abstractj.kalium.keys.PublicKey;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;

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
                while (true) {
                    try {
                        byte[] buf;
                        buf = Server.waitPacketWithDBSizeHeader(client.socket.getInputStream());
                        System.out.println("Received by " + client.socket.toString());
                        System.out.println(Hex.HEX.encode(buf));
                        Server.sendPacket(client.socket.getOutputStream(), buf);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e);
                    }
                }
            }).start();
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void encryptExpensiveAndSendWithKeys(Box box, PublicKey our, PublicKey their,
                                                byte[] payload) throws IOException {
        byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(nonce);
        byte[] ourKey = our.toBytes();
        byte[] theirKey = their.toBytes();
        byte[] cryptoBody = box.encrypt(nonce, payload);
        byte[] packet = new byte[nonce.length + ourKey.length + theirKey.length + cryptoBody.length];
        int pos = 0;
        System.arraycopy(nonce, 0, packet, 0, nonce.length);
        System.arraycopy(ourKey, 0, packet, pos += nonce.length, ourKey.length);
        System.arraycopy(theirKey, 0, packet, pos += ourKey.length, theirKey.length);
        System.arraycopy(cryptoBody, 0, packet, pos + theirKey.length, cryptoBody.length);
        Server.sendPacket(socket.getOutputStream(), packet);
    }

    public void sendPacket(byte[] packet) throws Exception {
        Server.sendPacket(socket.getOutputStream(), packet);
    }

}
