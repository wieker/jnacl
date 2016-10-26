package org.allesoft.messenger.swingui;

import org.allesoft.messenger.NaCl;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.RosterItem;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class MainWin extends JFrame {
    public MainWin(Client client) {
        super("Swing messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setSize(300, 500);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextField ipLabel = new JTextField("127.0.0.1");
        content.add(ipLabel);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener( (e) -> {
            client.connect(ipLabel.getText(), 50505);
        });
        content.add(connectButton);

        JTextField publicKeyLabel = new JTextField(NaCl.asHex(client.getPublicKey()));
        content.add(publicKeyLabel);

        RosterTableModel rosterTableModel = new RosterTableModel(client.getRoster());
        JTable rosterTable = new JTable(rosterTableModel);
        rosterTable.setDefaultRenderer(RosterItem.class,
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
        content.add(rosterTable);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener( (e) -> {
                new AddWin(rosterTableModel, client);
        });
        content.add(addContactButton);

        add(content);

        pack();
        setVisible(true);
    }
}
