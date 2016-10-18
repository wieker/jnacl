package org.allesoft.messenger;

import com.neilalexander.jnacl.crypto.curve25519xsalsa20poly1305;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.jserver.Daemon;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class InternalState {
    public static Socket connection;
    public static byte[] publicKey = new byte[32];
    public static byte[] privateKey = new byte[32];
    public static List<MessageListener> conversations = new ArrayList<>();

    public static void init() {
        File privateKeyFile = new File("private_key");
        File publicKeyFile = new File("public_key");
        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            try {
                InputStream inputStream1 = new FileInputStream(privateKeyFile);
                InputStream inputStream2 = new FileInputStream(publicKeyFile);
                inputStream1.read(privateKey);
                inputStream2.read(publicKey);
                inputStream1.close();
                inputStream2.close();
            } catch (IOException e) {
                System.out.println("Error reading from Disk");
                throw new RuntimeException();
            }
        } else {
            curve25519xsalsa20poly1305.crypto_box_keypair(InternalState.publicKey, InternalState.privateKey);
            try {
                OutputStream outputStream1 = new FileOutputStream(privateKeyFile);
                OutputStream outputStream2 = new FileOutputStream(publicKeyFile);
                outputStream1.write(privateKey);
                outputStream2.write(publicKey);
                outputStream1.close();
                outputStream2.close();
            } catch (IOException e) {
                System.out.println("Error writing to Disk");
                throw new RuntimeException();
            }
        }
    }

    public static List<RosterItem> loadRoster() {
        List<RosterItem> result = new ArrayList<>();
        File roster = new File("roster");
        if (!roster.exists()) {
            return result;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(roster)));
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(new RosterItem(line));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading from Disk");
            throw new RuntimeException();
        }
        return result;
    }

    public static void writeRoster(List<RosterItem> roster) {
        List<String> result = new ArrayList<>();
        File rosterFile = new File("roster");
        try {
            FileWriter writer = new FileWriter(rosterFile);
            for (RosterItem item : roster) {
                writer.write(item.value + LineSeparator.Unix);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error reading from Disk");
            throw new RuntimeException();
        }
    }

    public static void connect(String address) {
        try {
            connection = new Socket(address, 50505);
            new Thread(() -> {
                try {
                    while (true) {
                        byte[] packet = Daemon.loopPacket(connection.getInputStream());
                        for (MessageListener l : conversations) {
                            l.receive(packet);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Exception");
                }
                }).start();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

}
