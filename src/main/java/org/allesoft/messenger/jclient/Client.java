package org.allesoft.messenger.jclient;

/**
 * Created by kabramovich on 26.10.2016.
 */
public abstract class Client {
    public abstract void writeRoster(RosterImpl roster);

    public abstract void connect(String address, Integer port);

    public abstract RosterImpl getRoster();

    public abstract MessageSender addConversation(String userId, MessageReceiver receiver);

    public abstract byte[] getPublicKey();
}
