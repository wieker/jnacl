package org.allesoft.messenger.swingui;

import org.allesoft.messenger.NaCl;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.RosterItemImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class MainWin extends JFrame {

    private JTable rosterTable = null;

    public MainWin(Client client) {
        super("Swing messenger");
        setName("mainWin");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setSize(300, 500);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        JPanel connectServer = new JPanel();
        connectServer.setLayout(new BorderLayout());

        JTextField ipLabel = new JTextField("127.0.0.1");
        connectServer.add(ipLabel, BorderLayout.NORTH);

        JButton connectButton = new JButton("Connect");
        connectButton.setName("connectButton");
        connectButton.addActionListener( (e) -> {
            client.connect(ipLabel.getText(), 50505);
        });
        connectServer.add(connectButton, BorderLayout.SOUTH);
        content.add(connectServer, BorderLayout.NORTH);

        JPanel rosterPanel = new JPanel();
        rosterPanel.setLayout(new BorderLayout());

        JTextField publicKeyLabel = new JTextField(NaCl.asHex(client.getPublicKey()));
        rosterPanel.add(publicKeyLabel, BorderLayout.NORTH);

        RosterTableModel rosterTableModel = new RosterTableModel(client.getRoster(),
                () -> rosterTable.repaint());
        rosterTable = new JTable(rosterTableModel);
        rosterTable.setDefaultRenderer(RosterItemImpl.class,
                new RosterTableRenderer(rosterTableModel));
        rosterTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();
                    new TextWin(rosterTableModel.get(row), client);
                }
            }
        });
        rosterPanel.add(rosterTable, BorderLayout.CENTER);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.setName("addContactButton");
        addContactButton.addActionListener( (e) -> {
                new AddWin(rosterTableModel, client, this);
        });
        rosterPanel.add(addContactButton, BorderLayout.SOUTH);
        content.add(rosterPanel, BorderLayout.CENTER);

        add(content);

        pack();
        setVisible(true);
    }
}
