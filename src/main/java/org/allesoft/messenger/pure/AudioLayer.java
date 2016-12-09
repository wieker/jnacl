package org.allesoft.messenger.pure;

import org.allesoft.messenger.AudioCore;
import org.allesoft.messenger.Capture;
import org.allesoft.messenger.Playback;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 09.12.2016.
 */
public class AudioLayer extends AudioCore implements Layer {
    Layer bottom;
    private BlockingQueue<byte[]> queue;
    private SourceDataLine sourceDataLine;

    public void start() {
        sourceDataLine = getSourceAudioLine(getAudioFormat());
    }

    public void stop() {
        closeAudioLine(sourceDataLine);
        sourceDataLine = null;
    }

    public AudioLayer() {
        queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
            while (! isVoiceChatActive()) {
                Thread.sleep(100l);
            }
            if (isVoiceChatActive()) {
                playNext(sourceDataLine, packet);
            }
        });
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
        bottom = layer;
    }

    public void stream() {
        BlockingQueue<byte[]> bq = InfiniThreadFactory.infiniThreadWithQueue(this::sendPacket);

        InfiniThreadFactory.infiniThread(() -> {
            AudioFormat format = getAudioFormat();
            TargetDataLine line = getTargetAudioLine(format);

            byte[] data = new byte[line.getBufferSize()];
            int numBytesRead;

            while (isVoiceChatActive()) {
                numBytesRead = readNext(line, data);

                InfiniThreadFactory.tryItNow(() -> bq.put(data));
            }
            closeAudioLine(line);
        });
    }

    public boolean isVoiceChatActive() {
        return sourceDataLine != null;
    }
}
