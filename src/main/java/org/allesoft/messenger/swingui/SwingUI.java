package org.allesoft.messenger.swingui;

import org.allesoft.messenger.jclient.ClientImpl;

import javax.swing.*;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class SwingUI {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Not enough parameters");
        } else {
            JFrame mainWindow = new MainWin(new ClientImpl(args[0]).initKeys().loadRoster());
        }
    }

}
