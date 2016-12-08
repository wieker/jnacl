package org.allesoft.messenger;

import org.abstractj.kalium.encoders.Hex;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Reads data from the input channel and writes to the output stream
 */
public class Capture implements Runnable {

    private byte[] audioBytes;
    TargetDataLine line;

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
        thread = null;
    }

    public void run() {

        // define the required attributes for our line,
        // and make sure a compatible line is supported.

        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int channels = 2;
        int frameSize = 4;
        int sampleSize = 16;
        boolean bigEndian = true;

        AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                * channels, rate, bigEndian);

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            shutDown("Line matching " + info + " not supported.");
            return;
        }

        // get and open the target data line for capture.

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (LineUnavailableException ex) {
            shutDown("Unable to open the line: " + ex);
            return;
        } catch (SecurityException ex) {
            shutDown(ex.toString());
            //JavaSound.showInfoDialog();
            return;
        } catch (Exception ex) {
            shutDown(ex.toString());
            return;
        }

        // play back the captured audio data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;

        line.start();

        while (thread != null) {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }
            out.write(data, 0, numBytesRead);
            System.out.println("Recorded " + numBytesRead);
            System.out.println(Hex.HEX.encode(data));
        }

        // we reached the end of the stream.
        // stop and close the line.
        line.stop();
        line.close();
        line = null;

        // stop and close the output stream
        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // load bytes into the audio input stream for playback

        audioBytes = out.toByteArray();

        System.out.println("Recorded");

    }
} // End class Capture
