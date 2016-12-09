package org.allesoft.messenger;

import javax.sound.sampled.*;

/**
 * Created by wieker on 12/10/16.
 */
public class AudioCore {
    protected void playNext(SourceDataLine line, byte[] audioBytes) {
        line.write(audioBytes, 0, audioBytes.length);
        //line.drain();
        System.out.println("played");
    }

    protected void closeAudioLine(DataLine line) {
        line.stop();
        line.close();
    }

    protected SourceDataLine getSourceAudioLine(AudioFormat format) {
        SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
        } catch (LineUnavailableException ex) {
            System.out.println("Unable to open the line: " + ex);
            return null;
        }

        line.start();
        System.out.println("started playback");
        return line;
    }

    protected AudioFormat getAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int channels = 2;
        int frameSize = 4;
        int sampleSize = 16;
        boolean bigEndian = true;

        return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
                * channels, rate, bigEndian);
    }

    protected TargetDataLine getTargetAudioLine(AudioFormat format) {
        TargetDataLine line;
        try {
            line = AudioSystem.getTargetDataLine(format);
            line.open(format, line.getBufferSize());

            line.start();
        } catch (LineUnavailableException ex) {
            System.out.println("Unable to open the line: " + ex);
            return null;
        } catch (SecurityException ex) {
            System.out.println(ex.toString());
            //JavaSound.showInfoDialog();
            return null;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
        return line;
    }

    protected int readNext(TargetDataLine line, byte[] data) {
        return line.read(data, 0, data.length);
    }
}
