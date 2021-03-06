package org.allesoft.messenger.pure;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kabramovich on 22.11.2016.
 */
public class InfiniThreadFactory {
    public static void infiniThread(InfiniThreadBody body) {
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

    public static BlockingQueue<byte[]> infiniThreadWithQueue(InfiniThreadBodyWithQueue body) {
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

    public static Layer stabLayerWithReceive(InfiniThreadBodyWithQueue body) {
        return new Layer() {
            BlockingQueue<byte[]> queue;
            Layer bottom;

            {
                queue = InfiniThreadFactory.infiniThreadWithQueue(body);
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
        };
    }

    public static Layer stabLayerWithSend(InfiniThreadBodyWithQueue body) {
        return new Layer() {

            @Override
            public void sendPacket(byte[] packet) throws Exception {
                body.body(packet);
            }

            @Override
            public BlockingQueue<byte[]> getWaitingQueue() {
                return null;
            }

            @Override
            public void setTop(Layer layer) {

            }

            @Override
            public void setBottom(Layer layer) {

            }
        };
    }

    public static void tryItNow(InfiniThreadBody body) {
        try {
            body.body();
        } catch (Exception e) {
            System.out.println("Try failed");
            throw new RuntimeException(e);
        }
    }

    public static void thread(InfiniThreadBody body) {
        new Thread(() -> {
            try {
                body.body();
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }).start();
    }
}
