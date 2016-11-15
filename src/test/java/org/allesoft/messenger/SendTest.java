package org.allesoft.messenger;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Encoder;
import org.abstractj.kalium.encoders.Hex;
import org.abstractj.kalium.keys.KeyPair;
import org.allesoft.messenger.jclient.*;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jserver.Daemon;
import org.allesoft.messenger.pure.*;
import org.testng.annotations.Test;

/**
 * Created by kabramovich on 26.10.2016.
 */
public class SendTest {
    @Test
    public void testMsg() {
        Daemon daemon = new Daemon();
        daemon.openSocket(6666);

        Client client1 = new ClientImpl("testC1").initKeys().loadRoster();
        client1.connect("localhost", 6666);

        Client client2 = new ClientImpl("testC2").initKeys().loadRoster();
        client2.connect("localhost", 6666);

        MessageSender sender1 = client1.addConversation(NaCl.asHex(client2.getPublicKey()), text -> System.out.println(text));
        MessageSender sender2 = client2.addConversation(NaCl.asHex(client1.getPublicKey()), text -> System.out.println(text));

        new Box(NaCl.asHex(client1.getPublicKey()), NaCl.asHex(client2.getPublicKey()), Encoder.HEX);

        sender1.send("Message from the first client to the second");
        sender2.send("Message from the second client to the first");

        sender1.send("Message from the first client to the second#######################################################################################################################");
        sender2.send("Message from the second client to the first#######################################################################################################################");
    }

    @Test
    public void sendReceiveTest() throws Exception {
        Daemon daemon = new Daemon();
        daemon.openSocket(6667);

        KeyPair pair1 = new KeyPair();
        KeyPair pair2 = new KeyPair();
        Box box1 = new Box(pair2.getPublicKey(), pair1.getPrivateKey());
        Box box2 = new Box(pair1.getPublicKey(), pair2.getPrivateKey());

        Receiver receiver = new ReceiverImpl("localhost", 6667, box1);
        Receiver sender = new ReceiverImpl("localhost", 6667, box2);
        Thread.sleep(1000);
        sender.sendPacket(Hex.HEX.decode("0FAB22"));
        Thread.sleep(1000);
        sender.sendPacket(Hex.HEX.decode("0FAB22"));
        Thread.sleep(1000);
        sender.sendPacket(Hex.HEX.decode("0FAB22"));
        Thread.sleep(1000);
        sender.sendPacket(Hex.HEX.decode("0FAB22"));

        Thread.sleep(5000l);
    }

    @Test
    public void checkPlain() throws Exception {
        Daemon daemon = new Daemon();
        daemon.openSocket(6667);

        PlainReceiver receiver1 = new PlainReceiver("localhost", 6667);
        PlainReceiver receiver2 = new PlainReceiver("localhost", 6667);

        receiver1.sendPacket(Hex.RAW.decode("FF66"));
        receiver2.sendPacket(Hex.RAW.decode("FF55"));

        Thread.sleep(1000l);

        receiver1.sendPacket(Hex.RAW.decode("FF66"));
        receiver2.sendPacket(Hex.RAW.decode("FF55"));

        Thread.sleep(1000l);

        receiver1.sendPacket(Hex.RAW.decode("FF66"));
        receiver2.sendPacket(Hex.RAW.decode("FF55"));

        Thread.sleep(1000l);

        receiver1.sendPacket(Hex.RAW.decode("FF66"));
        receiver2.sendPacket(Hex.RAW.decode("FF55"));

        Thread.sleep(1000l);

        receiver1.sendPacket(Hex.RAW.decode("FF77"));
        receiver2.sendPacket(Hex.RAW.decode("FF88"));
        System.out.println(daemon.toString());
    }

    @Test
    public void testX() {
        Server server = Server.initServer(1055);
        org.allesoft.messenger.pure.Client c1 = org.allesoft.messenger.pure.Client.connectClient("localhost", 1055);
        org.allesoft.messenger.pure.Client c2 = org.allesoft.messenger.pure.Client.connectClient("localhost", 1055);
        c1.sendPacket(Hex.HEX.decode("FF77"));

        for (;;) {

        }
    }
}
