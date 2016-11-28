package org.allesoft.messenger.pure;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class TCPLayer implements Layer {
    Layer bottom;
    BlockingQueue<byte[]> queue = new LinkedBlockingDeque<>();
    ServerSocket serverSocket;
    Socket socket;

    public TCPLayer() {
        InfiniThreadFactory.tryItNow(() -> {
            serverSocket = new ServerSocket(8443);
            queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
                socket.getOutputStream().write(packet);
            });
            InfiniThreadFactory.infiniThread(() -> {
                socket = serverSocket.accept();
                passTcpPacket();
            });
        });
    }

    private void passTcpPacket() throws Exception {
        byte[] buf = new byte[1024 * 16];
        while (true) {
            int c = socket.getInputStream().read(buf, 0, buf.length);
            if (c < 0) {
                break;
            }
            byte[] pkt = new byte[c];
            System.arraycopy(buf, 0, pkt, 0, c);
            bottom.sendPacket(pkt);
            System.out.println("passed tcp packet");
        }
    }

    public TCPLayer(String address, int port) {
        InfiniThreadFactory.tryItNow(() -> {
            socket = new Socket(address, port);
            queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
                socket.getOutputStream().write(packet);
            });
            InfiniThreadFactory.infiniThread(() -> {
                passTcpPacket();
            });
        });
    }

    @Override
    public void sendPacket(byte[] packet) throws Exception {
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

    public TCPLayer regWithMux(ChannelMux mux) {
        mux.addChannel(ChannelMux.TCP_CHANNEL, this);
        setBottom(mux);
        return this;
    }
}
