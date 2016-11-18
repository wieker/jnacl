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
    private Box cryptoBox;

    private Client(Box cryptoBox) {
        this.cryptoBox = cryptoBox;
    }

    public static Client connectClient(Box box, String address, int port) {
        try {
            Client client = new Client(box);
            client.socket = new Socket(address, port);
            System.out.println("Client started " + client.socket.toString());
            new Thread(() -> {
                while (true) {
                    try {
                        byte[] buf;
                        buf = Server.waitPacketWithDBSizeHeader(client.socket.getInputStream());
                        System.out.println("Received by " + client.socket.toString());
                        System.out.println(Hex.HEX.encode(buf));

                        byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
                        byte[] theirKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
                        byte[] ourKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
                        byte[] cryptoBody = new byte[buf.length - nonce.length - ourKey.length - theirKey.length];
                        System.arraycopy(buf, 0, nonce, 0, nonce.length);
                        System.arraycopy(buf, nonce.length, theirKey, 0, theirKey.length);
                        System.arraycopy(buf, nonce.length + ourKey.length, ourKey, 0, ourKey.length);
                        System.arraycopy(buf, nonce.length + ourKey.length + theirKey.length, cryptoBody, 0, cryptoBody.length);
                        byte[] plain = client.cryptoBox.decrypt(nonce, cryptoBody);
                        System.out.println(Hex.HEX.encode(plain));

                        client.encryptExpensiveAndSendWithKeys(new PublicKey(ourKey), new PublicKey(theirKey), plain);
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

    public void encryptExpensiveAndSendWithKeys(PublicKey our, PublicKey their,
                                                byte[] payload) throws IOException {
        byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(nonce);
        byte[] ourKey = our.toBytes();
        byte[] theirKey = their.toBytes();
        byte[] cryptoBody = cryptoBox.encrypt(nonce, payload);
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
