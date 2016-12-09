package org.allesoft.messenger;

import javax.sound.sampled.*;

/**
 * Write data to the OutputChannel.
 */
public class Playback extends AudioCore implements Runnable {

    private byte[] audioBytes;

    Thread thread;

    public Playback(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }

    public void start() {
        thread = new Thread(this);
        thread.setName("Playback");
        thread.start();
    }

    public void run() {
        AudioFormat format = getAudioFormat();

        SourceDataLine line = getSourceAudioLine(format);
        if (line == null) return;

        byte[] audioBytes = this.audioBytes;
        playNext(line, audioBytes);

        closeAudioLine(line);
    }

}
