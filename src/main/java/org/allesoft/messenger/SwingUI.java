package org.allesoft.messenger;

import org.allesoft.messenger.client.ClientState;
import org.allesoft.messenger.client.InternalState;

import javax.swing.*;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class SwingUI {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Not enough parameters");
        } else {
            JFrame mainWindow = new MainWin(new ClientState(args[0]).initKeys());
        }
    }

}
