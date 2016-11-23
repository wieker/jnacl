package org.allesoft.messenger;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Hex;
import org.abstractj.kalium.keys.KeyPair;
import org.allesoft.messenger.pure.*;
import org.testng.annotations.Test;

/**
 * Created by kabramovich on 18.11.2016.
 */
public class BottomUpTest {

    @Test
    public void testY() throws Exception {
        KeyPair pair1 = new KeyPair();
        KeyPair pair2 = new KeyPair();

        Server server = Server.initServer(1055);
        Layer l1 = new CryptoLayer(pair1, pair2.getPublicKey());
        Layer l2 = new CryptoLayer(pair2, pair1.getPublicKey());
        Layer l1n = ClientPacketLayer.connectClient("localhost", 1055);
        Layer l2n = ClientPacketLayer.connectClient("localhost", 1055);
        CryptoMux l1m = new CryptoMux(l1n);
        CryptoMux l2m = new CryptoMux(l2n);
        l1.setBottom(l1m);
        l1n.setTop(l1m);
        l2.setBottom(l2m);
        l2n.setTop(l2m);
        l1m.addPeer(pair2.getPublicKey(), l1);
        l2m.addPeer(pair1.getPublicKey(), l2);
        l1.setTop(new EchoLayer(l1));
        l2.setTop(new EchoLayer(l2));
        l1.sendPacket(Hex.HEX.decode("FF77"));

        for (;;) {

        }
    }

}
