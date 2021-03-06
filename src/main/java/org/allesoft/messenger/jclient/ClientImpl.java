package org.allesoft.messenger.jclient;

import org.abstractj.kalium.keys.PublicKey;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.abstractj.kalium.keys.KeyPair;
import org.allesoft.messenger.pure.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kabramovich on 19.10.2016.
 */
public class ClientImpl extends Client {
    private byte[] publicKey = new byte[32];
    private byte[] privateKey = new byte[32];
    private String baseDirPath;
    private RosterImpl roster;
    CryptoMux muxLayer;
    Map<String, ChannelMux> muxers = new TreeMap<>();

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
    public ClientImpl connect(String address, Integer port) {
        Layer linkLayer = ClientPacketLayer.connectClient(address, port);
        muxLayer = new CryptoMux(linkLayer);
        linkLayer.setTop(muxLayer);
        return this;
    }

    @Override
    public Roster getRoster() {
        return roster;
    }

    @Override
    public MessageSender addConversation(String userId, MessageReceiver receiver) {
        ChannelMux channelMux = getChannelMux(userId);
        int textChannel = ChannelMux.TEXT_CHANNEL;
        channelMux.addChannel(textChannel, InfiniThreadFactory.stabLayerWithReceive(
                (packet) -> receiver.receive(new String(packet))));
        return text -> {
            InfiniThreadFactory.tryItNow(() -> {
                channelMux.sendPacket(textChannel, text.getBytes());
            });
        };
    }

    public void registerChannel(String userId, int channel, Layer layer) {
        ChannelMux channelMux = getChannelMux(userId);
        channelMux.addChannel(channel, layer);
        layer.setBottom(InfiniThreadFactory.stabLayerWithSend((packet) -> channelMux.sendPacket(channel, packet)));
    }

    private ChannelMux getChannelMux(String userId) {
        ChannelMux mux = muxers.get(userId);
        if (mux != null) {
            return mux;
        }
        Layer cryptoLayer = new CryptoLayer(new KeyPair(privateKey), new PublicKey(userId));
        muxLayer.addPeer(new PublicKey(userId), cryptoLayer);
        cryptoLayer.setBottom(muxLayer);
        ChannelMux channelMux = new ChannelMux(cryptoLayer);
        cryptoLayer.setTop(channelMux);
        muxers.put(userId, channelMux);
        return channelMux;
    }

    @Override
    public byte[] getPublicKey() {
        return publicKey;
    }
}
