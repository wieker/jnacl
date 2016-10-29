package org.allesoft.messenger.jclient;

/**
 * Created by wieker on 29.10.16.
 */
public interface Conversation {
    String getUserId();

    void send(String text);

    void send(byte[] text);

    void send(int band, String text);

    void send(int band, byte[] text);
}
