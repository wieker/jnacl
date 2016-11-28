package org.allesoft.messenger.pure.oneui;

import org.allesoft.messenger.jclient.ClientImpl;

import javax.swing.*;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class OneUI {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Not enough parameters");
        } else {
            JFrame mainWindow = new MainWindow(new ClientImpl(args[0]).initKeys().loadRoster());
        }
    }
}
