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

}
