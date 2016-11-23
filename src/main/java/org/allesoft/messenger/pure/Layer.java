package org.allesoft.messenger.pure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by kabramovich on 22.11.2016.
 */
public interface Layer {
    void sendPacket(byte[] packet) throws Exception;

    BlockingQueue<byte[]> getWaitingQueue();

    void setTop(Layer layer);

    void setBottom(Layer layer);
}
