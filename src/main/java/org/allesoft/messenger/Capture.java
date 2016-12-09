package org.allesoft.messenger;

import org.abstractj.kalium.encoders.Hex;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Reads data from the input channel and writes to the output stream
 */
public class Capture extends AudioCore implements Runnable {

    private byte[] audioBytes;

    Thread thread;

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public void start() {
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop() {
        thread = null;
    }

    private void shutDown(String message) {
        System.out.println(message);
        thread = null;
    }

    public void run() {
        TargetDataLine line;
        AudioFormat format = getAudioFormat();
        line = getTargetAudioLine(format);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[line.getBufferSize()];
        int numBytesRead;

        while (thread != null) {
            if ((numBytesRead = readNext(line, data)) == -1) {
                break;
            }
            out.write(data, 0, numBytesRead);
        }
        closeAudioLine(line);

        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        audioBytes = out.toByteArray();

        System.out.println("Recorded");

    }

} // End class Capture
