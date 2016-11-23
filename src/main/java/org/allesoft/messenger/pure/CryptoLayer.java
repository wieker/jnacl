package org.allesoft.messenger.pure;

import org.abstractj.kalium.NaCl;
import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.encoders.Hex;
import org.abstractj.kalium.keys.KeyPair;
import org.abstractj.kalium.keys.PublicKey;

import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kabramovich on 22.11.2016.
 */
public class CryptoLayer implements Layer {
    BlockingQueue queue;
    PublicKey our;
    PublicKey their;
    Box box;
    Layer bottom;

    public CryptoLayer(KeyPair our, PublicKey their) {
        this.our = our.getPublicKey();
        this.their = their;
        this.box = new Box(their, our.getPrivateKey());
        queue = InfiniThreadFactory.infiniThreadWithQueue((packet) -> {
            byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
            byte[] theirKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
            byte[] ourKey = new byte[NaCl.Sodium.PUBLICKEY_BYTES];
            byte[] cryptoBody = new byte[packet.length - nonce.length - ourKey.length - theirKey.length];
            System.arraycopy(packet, 0, nonce, 0, nonce.length);
            System.arraycopy(packet, nonce.length, theirKey, 0, theirKey.length);
            System.arraycopy(packet, nonce.length + ourKey.length, ourKey, 0, ourKey.length);
            System.arraycopy(packet, nonce.length + ourKey.length + theirKey.length, cryptoBody, 0, cryptoBody.length);
            byte[] plain = box.decrypt(nonce, cryptoBody);
            System.out.println(Hex.HEX.encode(plain));

            sendPacket(plain);
        });
    }

    @Override
    public void sendPacket(byte[] payload) throws Exception {
        byte[] nonce = new byte[NaCl.Sodium.NONCE_BYTES];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(nonce);
        byte[] ourKey = our.toBytes();
        byte[] theirKey = their.toBytes();
        byte[] cryptoBody = box.encrypt(nonce, payload);
        byte[] packet = new byte[nonce.length + ourKey.length + theirKey.length + cryptoBody.length];
        int pos = 0;
        System.arraycopy(nonce, 0, packet, 0, nonce.length);
        System.arraycopy(ourKey, 0, packet, pos += nonce.length, ourKey.length);
        System.arraycopy(theirKey, 0, packet, pos += ourKey.length, theirKey.length);
        System.arraycopy(cryptoBody, 0, packet, pos + theirKey.length, cryptoBody.length);
        if (bottom != null) {
            bottom.sendPacket(packet);
        }
    }

    @Override
    public BlockingQueue<byte[]> getWaitingQueue() {
        return queue;
    }

    @Override
    public void setTop(Layer layer) {

    }

    @Override
    public void setBottom(Layer bottom) {
        this.bottom = bottom;
    }
}
