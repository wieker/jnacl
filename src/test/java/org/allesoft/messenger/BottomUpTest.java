package org.allesoft.messenger;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Hex;
import org.abstractj.kalium.keys.KeyPair;
import org.allesoft.messenger.pure.CryptoLayer;
import org.allesoft.messenger.pure.Server;
import org.testng.annotations.Test;

/**
 * Created by kabramovich on 18.11.2016.
 */
public class BottomUpTest {

    @Test
    public void testX() throws Exception {
        KeyPair pair1 = new KeyPair();
        KeyPair pair2 = new KeyPair();
        Box box = new Box(pair2.getPublicKey(), pair1.getPrivateKey());

        Server server = Server.initServer(1055);
        org.allesoft.messenger.pure.Client c1 = org.allesoft.messenger.pure.Client.connectClient(box, "localhost", 1055);
        org.allesoft.messenger.pure.Client c2 = org.allesoft.messenger.pure.Client.connectClient(box, "localhost", 1055);
        c1.encryptExpensiveAndSendWithKeys(pair1.getPublicKey(), pair2.getPublicKey(), Hex.HEX.decode("FF77"));

        for (;;) {

        }
    }

    @Test
    public void testY() throws Exception {
        KeyPair pair1 = new KeyPair();
        KeyPair pair2 = new KeyPair();
        Box box = new Box(pair2.getPublicKey(), pair1.getPrivateKey());

        Server server = Server.initServer(1055);
        CryptoLayer l1 = new CryptoLayer("localhost", 1055, box, pair1.getPublicKey(), pair2.getPublicKey());
        CryptoLayer l2 = new CryptoLayer("localhost", 1055, box, pair2.getPublicKey(), pair1.getPublicKey());
        l1.sendPacket(Hex.HEX.decode("FF77"));

        for (;;) {

        }
    }

}
