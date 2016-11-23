package org.allesoft.messenger.swingui;

import org.abstractj.kalium.encoders.Hex;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.ClientImpl;
import org.allesoft.messenger.jclient.RosterItemImpl;
import org.allesoft.messenger.pure.Server;

/**
 * Created by tatyana on 11/23/2016.
 */
public class DualSwing {
    public static void main(String[] args) {
        Server.initServer(10505);
        Client c1 = mainWinBuilder("c1");
        Client c2 = mainWinBuilder("c2");
        c1.getRoster().add(new RosterItemImpl(Hex.HEX.encode(c2.getPublicKey())));
        c2.getRoster().add(new RosterItemImpl(Hex.HEX.encode(c1.getPublicKey())));
    }

    private static Client mainWinBuilder(String arg) {
        Client c = new ClientImpl(arg).initKeys().loadRoster().connect("localhost", 10505);
        new MainWin(c);
        return c;
    }
}
