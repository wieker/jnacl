package org.allesoft.messenger.pure.oneui;

import org.allesoft.messenger.NaCl;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.RosterItemImpl;
import org.allesoft.messenger.swingui.AddWin;
import org.allesoft.messenger.swingui.RosterTableModel;
import org.allesoft.messenger.swingui.RosterTableRenderer;
import org.allesoft.messenger.swingui.TextWin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        JPanel leftPanel = new RosterPanel(client, new ConversationCardsHolder(client, rightPanel));

        content.add(leftPanel, BorderLayout.WEST);
        content.add(rightPanel, BorderLayout.EAST);

        add(content);

        pack();
        setVisible(true);
    }
}
