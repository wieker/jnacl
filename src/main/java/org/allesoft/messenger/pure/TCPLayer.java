package org.allesoft.messenger.pure;

import org.abstractj.kalium.encoders.Hex;

import java.net.Inet4Address;
import java.net.InetAddress;
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
    TCPLayerState state = TCPLayerState.TCP_FREE;

    public TCPLayer() {
        InfiniThreadFactory.tryItNow(() -> {
            serverSocket = new ServerSocket(8443);
            queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
                socket.getOutputStream().write(packet);
            });
            InfiniThreadFactory.infiniThread(() -> {
                socket = serverSocket.accept();
                socket.setSoTimeout(10000);
                passTcpPacket();
            });
        });
    }

    private void passTcpPacket() throws Exception {
        byte[] buf = new byte[1024 * 16];
        while (true) {
            System.out.println("wait " + socket.toString());
            int c = socket.getInputStream().read(buf, 0, buf.length);
            if (c < 0) {
                state = TCPLayerState.TCP_FREE;
                break;
            }
            byte[] pkt = new byte[c];
            System.arraycopy(buf, 0, pkt, 0, c);
            bottom.sendPacket(pkt);
            System.out.println("passed tcp packet from " + socket.toString());
        }
    }

    public TCPLayer(String address, int port) {
        InfiniThreadFactory.tryItNow(() -> {
            queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
                if (TCPLayerState.TCP_CONNECTED.equals(state)) {
                    socket.getOutputStream().write(packet);
                    socket.getOutputStream().flush();
                    System.out.println("sent tcp packet to " + socket.toString());
                    System.out.println(Hex.HEX.encode(packet));
                    System.out.println(new String(packet));
                } else {
                    System.out.println(Hex.HEX.encode(packet));
                    int dstport = ((packet[2] & 0xFF) << 8) + (packet[3] & 0xFF);
                    byte[] ip = new byte[4];
                    System.arraycopy(packet, 4, ip, 0, 4);
                    InetAddress inet4Address = Inet4Address.getByAddress(ip);
                    System.out.println("connecting to " + dstport + " " + inet4Address.toString());
                    socket = new Socket(inet4Address, dstport);
                    socket.setSoTimeout(10000);
                    state = TCPLayerState.TCP_CONNECTED;
                    byte[] result = new byte[8];
                    result[0] = 0;
                    result[1] = 90;
                    System.arraycopy(packet, 2, result, 2, 6);
                    bottom.sendPacket(result);
                }
            });
            InfiniThreadFactory.infiniThread(() -> {
                if (TCPLayerState.TCP_CONNECTED.equals(state)) {
                    passTcpPacket();
                } else {
                    System.out.println("server disconnected");
                    Thread.sleep(1000l);
                }
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
