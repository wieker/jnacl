package org.allesoft.messenger.jclient;

/**
 * Created by kabramovich on 18.10.2016.
 */
public interface LowLevelMessageReceiver {
    void receive(byte[] packet);
}
