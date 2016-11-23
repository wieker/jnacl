package org.allesoft.messenger.pure;

import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 23.11.2016.
 */
public class EchoLayer implements Layer {
    BlockingQueue<byte[]> queue;
    Layer bottom;

    public EchoLayer(Layer bottom) {
        this.bottom = bottom;
        queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> sendPacket(packet));
    }

    @Override
    public void sendPacket(byte[] packet) throws Exception {
        bottom.sendPacket(packet);
    }

    @Override
    public BlockingQueue<byte[]> getWaitingQueue() {
        return queue;
    }

    @Override
    public void setTop(Layer layer) {

    }

    @Override
    public void setBottom(Layer layer) {

    }
}
