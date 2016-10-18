package org.allesoft.messenger;

import com.neilalexander.jnacl.crypto.curve25519xsalsa20poly1305;

import javax.swing.*;
import java.io.File;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class SwingUI {
    public static JTextArea currentArea;

    public static void main(String[] args) {
        InternalState.init();
        JFrame mainWindow = new MainWin();
    }

}
