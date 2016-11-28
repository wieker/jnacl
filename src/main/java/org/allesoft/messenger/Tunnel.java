package org.allesoft.messenger;

import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.ClientImpl;
import org.allesoft.messenger.jclient.MessageSender;
import org.allesoft.messenger.pure.ChannelMux;
import org.allesoft.messenger.pure.Server;
import org.allesoft.messenger.pure.TCPLayer;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class Tunnel {
    public static void main(String[] args) {
        Server server = Server.initServer(6666);

        Client client1 = new ClientImpl("testC1").initKeys().loadRoster();
        client1.connect("localhost", 6666);

        Client client2 = new ClientImpl("testC2").initKeys().loadRoster();
        client2.connect("localhost", 6666);

        MessageSender sender1 = client1.addConversation(NaCl.asHex(client2.getPublicKey()), text -> System.out.println(text));
        MessageSender sender2 = client2.addConversation(NaCl.asHex(client1.getPublicKey()), text -> System.out.println(text));

        client1.registerChannel(NaCl.asHex(client2.getPublicKey()), ChannelMux.TCP_CHANNEL, new TCPLayer());
        client2.registerChannel(NaCl.asHex(client1.getPublicKey()), ChannelMux.TCP_CHANNEL, new TCPLayer("kernel.org", 443));

        sender1.send("Message from the first client to the second");
        sender2.send("Message from the second client to the first");

        sender1.send("Message from the first client to the second#######################################################################################################################");
        sender2.send("Message from the second client to the first#######################################################################################################################");
    }
}
