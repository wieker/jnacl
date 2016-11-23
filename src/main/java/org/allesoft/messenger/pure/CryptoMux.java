package org.allesoft.messenger.pure;

import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.encoders.Hex;
import org.abstractj.kalium.keys.PublicKey;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 23.11.2016.
 */
public class CryptoMux implements Layer {
    Layer top;
    Layer bottom;
    BlockingQueue<byte[]> queue;
    Map<String, Layer> mux = new TreeMap<>();

    public CryptoMux(Layer bottom) {
        this.bottom = bottom;
        queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
            byte[] theirKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
            System.arraycopy(packet, NaCl.Sodium.NONCE_BYTES, theirKey, 0, theirKey.length);
            String key = Hex.HEX.encode(theirKey);
            mux.get(key).getWaitingQueue().add(packet);
        });
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

    }

    public void addPeer(PublicKey key, Layer layer) {
        mux.put(Hex.HEX.encode(key.toBytes()), layer);
    }
}
