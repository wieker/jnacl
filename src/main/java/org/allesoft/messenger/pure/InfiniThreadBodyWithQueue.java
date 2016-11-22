package org.allesoft.messenger.pure;

/**
 * Created by kabramovich on 22.11.2016.
 */
public interface InfiniThreadBodyWithQueue {
    void body(byte[] packet) throws Exception;
}
