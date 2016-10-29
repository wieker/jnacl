package org.allesoft.messenger.jclient;

import org.allesoft.messenger.NaCl;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.keys.KeyPair;
import org.allesoft.messenger.jserver.Daemon;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 19.10.2016.
 */
public class ClientImpl extends Client {
    private Socket connection;
    private byte[] publicKey = new byte[32];
    private byte[] privateKey = new byte[32];
    private List<LowLevelMessageReceiver> conversations = new ArrayList<>();
    private String baseDirPath;
    private RosterImpl roster;

    public ClientImpl(String baseDirPath) {
        this.baseDirPath = baseDirPath;
        if (!new File(baseDirPath).exists()) {
            new File(baseDirPath).mkdir();
        }
    }

    public ClientImpl initKeys() {
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
            KeyPair pair = new KeyPair();
            publicKey = pair.getPublicKey().toBytes();
            privateKey = pair.getPrivateKey().toBytes();
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

    public ClientImpl loadRoster() {
        this.roster = new RosterImpl();
        File roster = new File(baseDirPath, "roster");
        if (!roster.exists()) {
            return this;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(roster)));
            String line;
            while ((line = reader.readLine()) != null) {
                this.roster.add(new RosterItemImpl(line));
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading from Disk");
            throw new RuntimeException();
        }
        return this;
    }

    @Override
    public void writeRoster(Roster roster) {
        List<String> result = new ArrayList<>();
        File rosterFile = new File(baseDirPath, "roster");
        try {
            FileWriter writer = new FileWriter(rosterFile);
            for (int i = 0; i < roster.size(); i ++) {
                RosterItem item = roster.getByIndex(i);
                writer.write(item.getValue() + LineSeparator.Unix);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error reading from Disk");
            throw new RuntimeException();
        }
    }

    @Override
    public void connect(String address, Integer port) {
        try {
            connection = new Socket(address, port);
            new Thread(() -> {
                try {
                    while (true) {
                        byte[] packet = Daemon.loopPacket(connection.getInputStream());
                        for (LowLevelMessageReceiver l : conversations) {
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

    @Override
    public Roster getRoster() {
        return roster;
    }

    @Override
    public MessageSender addConversation(String userId, MessageReceiver receiver) {
        Box naCl = createNaCl(userId);
        conversations.add(packet -> {
            int length = packet[NaCl.crypto_secretbox_NONCEBYTES] & 0xFF;
            byte[] chars = new byte[length];
            byte[] nonce = new byte[NaCl.crypto_secretbox_NONCEBYTES];
            System.arraycopy(packet, NaCl.crypto_secretbox_NONCEBYTES + 1, chars, 0, length);
            System.arraycopy(packet, 0, nonce, 0, NaCl.crypto_secretbox_NONCEBYTES);
            byte[] decoded = naCl.decrypt(nonce, chars);
            byte[] dstKey = new byte[NaCl.crypto_secretbox_KEYBYTES];
            System.arraycopy(decoded, 0, dstKey, 0, NaCl.crypto_secretbox_KEYBYTES);
            for (int i = 0; i < dstKey.length; i ++) {
                if (publicKey[i] != dstKey[i]) {
                    return;
                }
            }
            receiver.receive(LineSeparator.Unix + new String(decoded,
                    NaCl.crypto_secretbox_KEYBYTES, decoded.length - NaCl.crypto_secretbox_KEYBYTES));
        });
        return text -> {
            byte[] nonce = new byte[NaCl.crypto_secretbox_NONCEBYTES];
            SecureRandom rng = new SecureRandom();
            rng.nextBytes(nonce);
            byte[] plain = text.getBytes();
            byte[] binaryId = NaCl.getBinary(userId);
            int pktCount = (plain.length + 1 + NaCl.crypto_secretbox_KEYBYTES + NaCl.crypto_secretbox_NONCEBYTES + NaCl.crypto_secretbox_ZEROBYTES) / 256 + 1;
            byte[] out = new byte[pktCount * 256];
            byte[] finalPlain = new byte[plain.length + binaryId.length];
            System.arraycopy(binaryId, 0, finalPlain, 0, NaCl.crypto_secretbox_KEYBYTES);
            System.arraycopy(plain, 0, finalPlain, NaCl.crypto_secretbox_KEYBYTES, plain.length);
            byte[] chars = naCl.encrypt(nonce, finalPlain);
            System.arraycopy(nonce, 0, out, 0, NaCl.crypto_secretbox_NONCEBYTES);
            out[NaCl.crypto_secretbox_NONCEBYTES] = (byte) chars.length;
            System.arraycopy(chars, 0, out, NaCl.crypto_secretbox_NONCEBYTES + 1, chars.length);
            try {
                connection.getOutputStream().write(out);
            } catch (IOException x) {
                System.out.println("send error");
            }
        };
    }

    private Box createNaCl(String userId) {
        Box naCl = null;
        try {
            naCl = new Box(NaCl.getBinary(userId), privateKey);
        } catch (Exception x) {
            System.out.println("NaCL exception");
        }
        return naCl;
    }

    @Override
    public byte[] getPublicKey() {
        return publicKey;
    }
}
