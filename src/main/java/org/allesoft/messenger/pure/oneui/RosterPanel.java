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
public class RosterPanel extends JPanel {
    public RosterPanel(Client client, ConversationCardsHolder holder) {
        setLayout(new BorderLayout());

        JTextField publicKeyLabel = new JTextField(NaCl.asHex(client.getPublicKey()));
        add(publicKeyLabel, BorderLayout.NORTH);

        RosterTableModel rosterTableModel = new RosterTableModel(client.getRoster(),
                () -> repaint());
        JTable rosterTable = new JTable(rosterTableModel);
        rosterTable.setDefaultRenderer(RosterItemImpl.class,
                new RosterTableRenderer(rosterTableModel));
        rosterTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();
                    ConversationPanel panel = holder.getConversation(rosterTableModel.get(row));
                }
            }
        });
        add(rosterTable, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        JButton addContactButton = new JButton("Add Contact");
        addContactButton.setName("addContactButton");
        addContactButton.addActionListener( (e) -> {

        });
        bottomPanel.add(addContactButton, BorderLayout.NORTH);

        JButton settingsButton = new JButton("settings");
        settingsButton.setName("settingsButton");
        settingsButton.addActionListener( (e) -> {

        });
        bottomPanel.add(settingsButton, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
