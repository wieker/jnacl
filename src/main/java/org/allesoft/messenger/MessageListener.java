package org.allesoft.messenger;

import com.neilalexander.jnacl.NaCl;

import javax.swing.*;

/**
 * Created by kabramovich on 18.10.2016.
 */
public interface MessageListener {
    void receive(byte[] packet);
}
