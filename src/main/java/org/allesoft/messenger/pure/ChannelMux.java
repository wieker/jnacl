package org.allesoft.messenger.pure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 23.11.2016.
 */
public class ChannelMux implements Layer {
    Layer top;
    Layer bottom;
    BlockingQueue<byte[]> queue;
    List<Layer> channels = new ArrayList<>();

    public ChannelMux(Layer bottom) {
        this.bottom = bottom;
        queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
            int channel = packet[0];
            byte[] ost = new byte[packet.length - 1];
            System.arraycopy(packet, 1, ost, 0, ost.length);
            channels.get(channel).getWaitingQueue().add(packet);
        });
    }

    @Override
    public void sendPacket(byte[] packet) throws Exception {

    }

    public void sendPacket(int channel, byte[] packet) throws Exception {
        byte[] full = new byte[packet.length + 1];
        full[0] = (byte) channel;
        System.arraycopy(packet, 0, full, 1, packet.length);
        bottom.sendPacket(full);
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

    public int addChannel(Layer layer) {
        channels.add(layer);
        return channels.size() - 1;
    }
}
