package org.allesoft.messenger.jclient;

import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.SecureRandom;

/**
 * Created by wieker on 29.10.16.
 */
public class Receiver {
    public static int PKT_SIZE_FIELD = 2;
    private Socket connection;
    private Box box;

    public Receiver(String address, Integer port, Box box) {
        try {
            connection = new Socket(address, port);
            this.box = box;
            new Thread(() -> {
                try {
                    while (true) {
                        byte[] header = receiveXBytes(connection.getInputStream(),
                                NaCl.Sodium.NONCE_BYTES + PKT_SIZE_FIELD +
                                2 * NaCl.Sodium.PUBLICKEY_BYTES);
                        byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
                        System.arraycopy(header, 0, nonce, 0, NaCl.Sodium.NONCE_BYTES);
                        int size = header[4] & 0xff << 8 + header[5] & 0xff;
                        byte[] senderKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
                        System.arraycopy(header, NaCl.Sodium.NONCE_BYTES + PKT_SIZE_FIELD,
                                senderKey, 0, NaCl.Sodium.PUBLICKEY_BYTES);
                        byte[] receiverKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
                        System.arraycopy(header, NaCl.Sodium.NONCE_BYTES + PKT_SIZE_FIELD + NaCl.Sodium.PUBLICKEY_BYTES,
                                receiverKey, 0, NaCl.Sodium.PUBLICKEY_BYTES);
                        byte[] cryptoBody = receiveXBytes(connection.getInputStream(), size);
                        byte[] text = box.decrypt(nonce, cryptoBody);
                        System.out.println("Received:" + Hex.HEX.encode(text));
                        System.out.println("From:" + Hex.HEX.encode(senderKey));
                        System.out.println("To:" + Hex.HEX.encode(receiverKey));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

    public byte[] receiveXBytes(InputStream inputStream, int x) throws IOException {
        byte[] result = new byte[x];
        int total = 0;
        while (total < result.length) {
            int read = inputStream.read(result, total, result.length - total);
            if (read < 0) {
                throw new RuntimeException("Something bad");
            }
            total += read;
        }
        return result;
    }

    public void sendPacket(byte[] payload) throws IOException {
        byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(nonce);
        byte[] cryptoBody = box.encrypt(nonce, payload);
        byte[] sizeField = new byte[PKT_SIZE_FIELD];
        sizeField[0] = (byte) (cryptoBody.length >> 8);
        sizeField[1] = (byte) (cryptoBody.length & 0xff);
        byte[] key = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
        connection.getOutputStream().write(nonce);
        connection.getOutputStream().write(sizeField);
        connection.getOutputStream().write(key);
        connection.getOutputStream().write(key);
        connection.getOutputStream().write(cryptoBody);
        connection.getOutputStream().flush();
    }

}
