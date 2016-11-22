package org.allesoft.messenger.pure;

import org.abstractj.kalium.encoders.Hex;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kabramovich on 22.11.2016.
 */
public class InfiniThreadFactory {
    static void infiniThread(InfiniThreadBody body) {
        new Thread(() -> {
            while (true) {
                try {
                    body.body();
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }
        }).start();
    }

    static BlockingQueue<byte[]> infiniThreadWithQueue(InfiniThreadBodyWithQueue body) {
        BlockingQueue<byte[]> packetsQueue = new LinkedBlockingQueue<>();
        new Thread(() -> {
            while (true) {
                try {
                    byte[] packet = packetsQueue.take();
                    body.body(packet);
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }
        }).start();
        return packetsQueue;
    }
}
