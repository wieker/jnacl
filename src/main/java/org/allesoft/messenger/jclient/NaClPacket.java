package org.allesoft.messenger.jclient;

/**
 * Created by wieker on 29.10.16.
 */
public interface NaClPacket {
    void sendPacket(byte[] packet);

    byte[] waitPacket();
}
