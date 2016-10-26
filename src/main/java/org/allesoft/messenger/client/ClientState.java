package org.allesoft.messenger.client;

import com.neilalexander.jnacl.crypto.curve25519xsalsa20poly1305;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.jserver.Daemon;
import org.allesoft.messenger.MessageListener;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 19.10.2016.
 */
public class ClientState {
    public Socket connection;
    public byte[] publicKey = new byte[32];
    public byte[] privateKey = new byte[32];
    public List<MessageListener> conversations = new ArrayList<>();
    public String baseDirPath;
    public Roster roster;

    public ClientState(String baseDirPath) {
        this.baseDirPath = baseDirPath;
        if (!new File(baseDirPath).exists()) {
            new File(baseDirPath).mkdir();
        }
    }

    public ClientState initKeys() {
        File privateKeyFile = new File(baseDirPath, "private_key");
        File publicKeyFile = new File(baseDirPath, "public_key");
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
            curve25519xsalsa20poly1305.crypto_box_keypair(publicKey, privateKey);
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
        return this;
    }

    public ClientState loadRoster() {
        this.roster = new Roster();
        File roster = new File(baseDirPath, "roster");
        if (!roster.exists()) {
            return this;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(roster)));
            String line;
            while ((line = reader.readLine()) != null) {
                this.roster.add(new RosterItem(line));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading from Disk");
            throw new RuntimeException();
        }
        return this;
    }

    public void writeRoster(Roster roster) {
        List<String> result = new ArrayList<>();
        File rosterFile = new File(baseDirPath, "roster");
        try {
            FileWriter writer = new FileWriter(rosterFile);
            for (RosterItem item : roster.getRoster()) {
                writer.write(item.value + LineSeparator.Unix);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error reading from Disk");
            throw new RuntimeException();
        }
    }

    public void connect(String address) {
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

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }
}
