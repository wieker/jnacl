package org.allesoft.messenger.pure.oneui;

import org.allesoft.messenger.jclient.Client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class MainWindow extends JFrame {

    private JTable rosterTable = null;

    public MainWindow(Client client) {
        super("Swing messenger");
        setName("mainWin");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new CardLayout());
        Repainter repainter = () -> {
            pack();
            revalidate();
            repaint();
        };
        JPanel leftPanel = new RosterPanel(client, new ConversationCardsHolder(client, rightPanel, repainter), repainter);

        content.add(leftPanel, BorderLayout.WEST);
        content.add(rightPanel, BorderLayout.EAST);
        add(content);

        setVisible(true);
        repainter.repaint();
    }
}
