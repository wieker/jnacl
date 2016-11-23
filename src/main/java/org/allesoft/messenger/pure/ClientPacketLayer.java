package org.allesoft.messenger.pure;

import org.abstractj.kalium.encoders.Hex;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by kabramovich on 22.11.2016.
 */
public class ClientPacketLayer implements Layer {
    private Socket socket;
    private Layer top;

    public static ClientPacketLayer connectClient(String address, int port) {
        try {
            ClientPacketLayer layer = new ClientPacketLayer();
            layer.socket = new Socket(address, port);
            //System.out.println("Client started " + layer.socket.toString());
            InfiniThreadFactory.infiniThread(() -> {
                byte[] buf;
                buf = Server.waitPacketWithDBSizeHeader(layer.getSocket().getInputStream());
                //System.out.println("Received by " + layer.getSocket().toString());
                //System.out.println(Hex.HEX.encode(buf));

                if (layer.top != null) {
                    layer.top.getWaitingQueue().add(buf);
                }
            });
            return layer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPacket(byte[] packet) throws Exception {
        Server.sendPacket(socket.getOutputStream(), packet);
    }

    @Override
    public BlockingQueue<byte[]> getWaitingQueue() {
        return null;
    }

    private Socket getSocket() {
        return socket;
    }

    @Override
    public void setTop(Layer top) {
        this.top = top;
    }

    @Override
    public void setBottom(Layer layer) {

    }
}
