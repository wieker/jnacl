package org.allesoft.messenger;

import org.abstractj.kalium.encoders.Hex;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

/**
 * Write data to the OutputChannel.
 */
public class Playback implements Runnable {

    private byte[] audioBytes;
    SourceDataLine line;

    Thread thread;

    public Playback(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }

    public void start() {
        thread = new Thread(this);
        thread.setName("Playback");
        thread.start();
    }

    public void stop() {
        thread = null;
    }

    private void shutDown(String message) {
        System.out.println(message);
        if (thread != null) {
            thread = null;
        }
    }

    public void run() {

        // make sure we have something to play
        if (audioBytes == null) {
            shutDown("No loaded audio to play back");
            return;
        }

        // get an AudioInputStream of the desired format for playback

        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int channels = 2;
        int frameSize = 4;
        int sampleSize = 16;
        boolean bigEndian = true;

        AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                * channels, rate, bigEndian);
        int frameSizeInBytes = format.getFrameSize();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
        // reset to the beginnning of the stream
        try {
            audioInputStream.reset();
        } catch (Exception e) {
            shutDown("Unable to reset the stream\n" + e);
            return;
        }

        try {
            audioInputStream.reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format,
                audioInputStream);

        if (playbackInputStream == null) {
            shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
            return;
        }

        // define the required attributes for our line,
        // and make sure a compatible line is supported.

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            shutDown("Line matching " + info + " not supported.");
            return;
        }

        // get and open the source data line for playback.

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, 16384);
        } catch (LineUnavailableException ex) {
            shutDown("Unable to open the line: " + ex);
            return;
        }

        // play back the captured audio data

        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead = 0;

        // start the source data line
        line.start();

        while (thread != null) {
            try {
                if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                    break;
                }
                int numBytesRemaining = numBytesRead;
                while (numBytesRemaining > 0) {
                    numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    System.out.println("Played " + data);
                    System.out.println(Hex.HEX.encode(data));
                }
            } catch (Exception e) {
                shutDown("Error during playback: " + e);
                break;
            }
        }
        // we reached the end of the stream.
        // let the data play out, then
        // stop and close the line.
        if (thread != null) {
            line.drain();
        }
        line.stop();
        line.close();
        line = null;
        shutDown(null);
    }
} // End class Playback
