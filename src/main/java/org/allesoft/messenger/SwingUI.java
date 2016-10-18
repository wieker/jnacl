package org.allesoft.messenger;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.jserver.Daemon;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class SwingUI {
    public static Socket connection;
    public static JTextArea currentArea;

    public static void main(String[] args) {
        try {
            connection = new Socket("127.0.0.1", 50505);
            new Thread(() -> {
                try {
                    while (true) {
                        byte[] packet = Daemon.loopPacket(connection.getInputStream());
                        if (currentArea != null) {
                            SwingUtilities.invokeLater(() -> {
                                currentArea.append(LineSeparator.Unix + new String(packet));
                            });
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Exception");
                }
                }).start();
            JFrame mainWindow = new MainWin();
        } catch (IOException e) {
            System.out.println("server error");
        }
    }

}
