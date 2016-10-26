package org.allesoft.messenger.client;

import com.neilalexander.jnacl.NaCl;

import javax.swing.*;

/**
 * Created by kabramovich on 18.10.2016.
 */
public interface LowLevelMessageReceiver {
    void receive(byte[] packet);
}
