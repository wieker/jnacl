package org.allesoft.messenger.jclient;

import java.io.IOException;

/**
 * Created by kabramovich on 14.11.2016.
 */
public abstract class Receiver {
    public abstract void sendPacket(byte[] payload) throws IOException;
}
