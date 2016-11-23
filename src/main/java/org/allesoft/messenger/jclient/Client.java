package org.allesoft.messenger.jclient;

import org.allesoft.messenger.pure.Layer;

/**
 * Created by kabramovich on 26.10.2016.
 */
public abstract class Client {
    public abstract void writeRoster(Roster roster);

    public abstract void connect(String address, Integer port);

    public abstract Roster getRoster();

    public abstract MessageSender addConversation(String userId, MessageReceiver receiver);

    public abstract byte[] getPublicKey();

    public abstract void registerChannel(String userId, int channel, Layer layer);
}
