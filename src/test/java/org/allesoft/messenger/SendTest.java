package org.allesoft.messenger;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Encoder;
import org.abstractj.kalium.encoders.Hex;
import org.allesoft.messenger.jclient.*;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.pure.*;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by kabramovich on 26.10.2016.
 */
public class SendTest {
    @Test
    public void testMsg() {
        Server server = Server.initServer(6666);

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

        InfiniThreadFactory.tryItNow(() -> { Thread.sleep(1000l); });
    }

    @Test
    public void testFtp() {
        Server server = Server.initServer(6666);

        Client client1 = new ClientImpl("testC1").initKeys().loadRoster();
        client1.connect("localhost", 6666);

        Client client2 = new ClientImpl("testC2").initKeys().loadRoster();
        client2.connect("localhost", 6666);

        MessageSender sender1 = client1.addConversation(NaCl.asHex(client2.getPublicKey()), text -> System.out.println(text));
        MessageSender sender2 = client2.addConversation(NaCl.asHex(client1.getPublicKey()), text -> System.out.println(text));

        FileAcceptRequest fileAcceptRequest = (FTPLayer layer, String fileName, long size) -> {
                System.out.println("File: " + fileName);
                System.out.println("Size: " + size);
                InfiniThreadFactory.tryItNow(() -> layer.receive(new File("receive")));
                return true;
            };
        FTPLayer source;
        client1.registerChannel(NaCl.asHex(client2.getPublicKey()), 2, source = new FTPLayer(fileAcceptRequest));
        client2.registerChannel(NaCl.asHex(client1.getPublicKey()), 2, new FTPLayer(fileAcceptRequest));

        sender1.send("Message from the first client to the second");
        sender2.send("Message from the second client to the first");

        sender1.send("Message from the first client to the second#######################################################################################################################");
        sender2.send("Message from the second client to the first#######################################################################################################################");

        InfiniThreadFactory.tryItNow(() -> source.sendFile(Hex.HEX.decode("FF0867"), "Text hex"));

        InfiniThreadFactory.tryItNow(() -> { Thread.sleep(1000l); });
    }

}
