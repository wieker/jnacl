package org.allesoft.messenger.pure;

import org.allesoft.messenger.Capture;
import org.allesoft.messenger.Playback;

import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 09.12.2016.
 */
public class AudioLayer implements Layer {
    Layer bottom;

    @Override
    public void sendPacket(byte[] packet) throws Exception {
        bottom.sendPacket(packet);
    }

    @Override
    public BlockingQueue<byte[]> getWaitingQueue() {
        return InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
            Playback player = new Playback(packet);
            player.start();
            Thread.sleep(100l);
        });
    }

    @Override
    public void setTop(Layer layer) {

    }

    @Override
    public void setBottom(Layer layer) {
        bottom = layer;
    }

    public void stream() {
        InfiniThreadFactory.infiniThread(() -> {
            Capture capture = new Capture();
            capture.start();
            InfiniThreadFactory.tryItNow(() -> Thread.sleep(100l));
            capture.stop();
            while (capture.getAudioBytes() == null) {
                InfiniThreadFactory.tryItNow(() -> Thread.sleep(100l));
            }
            InfiniThreadFactory.tryItNow(() -> sendPacket(capture.getAudioBytes()));
        });
    }
}
