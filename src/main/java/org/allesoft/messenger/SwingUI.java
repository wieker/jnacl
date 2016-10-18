package org.allesoft.messenger;

import com.neilalexander.jnacl.NaCl;
import com.neilalexander.jnacl.crypto.curve25519xsalsa20poly1305;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.jserver.Daemon;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class SwingUI {
    public static Socket connection;
    public static JTextArea currentArea;
    public static byte[] publicKey = new byte[32];
    public static byte[] privateKey = new byte[32];
    public static byte[] peerPublicKey = new byte[32];
    public static NaCl naCl;

    public static void main(String[] args) {
        curve25519xsalsa20poly1305.crypto_box_keypair(publicKey, privateKey);
        try {
            connection = new Socket("127.0.0.1", 50505);
            new Thread(() -> {
                try {
                    while (true) {
                        byte[] packet = Daemon.loopPacket(connection.getInputStream());
                        int length = packet[NaCl.crypto_secretbox_NONCEBYTES];
                        byte[] chars = new byte[length];
                        System.arraycopy(packet, NaCl.crypto_secretbox_NONCEBYTES + 1, chars, 0, length);
                        byte[] decoded = naCl.decrypt(chars, packet);
                        if (currentArea != null) {
                            SwingUtilities.invokeLater(() -> {
                                currentArea.append(LineSeparator.Unix + new String(decoded));
                            });
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Exception");
                }
                }).start();
            JFrame mainWindow = new MainWin();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

}
