package org.allesoft.messenger.jclient;

import org.abstractj.kalium.crypto.Box;

/**
 * Created by wieker on 29.10.16.
 */
public class NaClPacketImpl implements NaClPacket {
    Box box;

    public NaClPacketImpl(Box box) {
        this.box = box;
    }

    @Override
    public void sendPacket(byte[] packet) {
    }

    @Override
    public byte[] waitPacket() {
        return new byte[0];
    }
}
