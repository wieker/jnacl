package org.allesoft.messenger;

import com.neilalexander.jnacl.NaCl;
import org.allesoft.messenger.client.ClientState;
import org.allesoft.messenger.client.RosterItem;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class MainWin extends JFrame {
    public MainWin(ClientState clientState) {
        super("Swing messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setSize(300, 500);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextField ipLabel = new JTextField("127.0.0.1");
        content.add(ipLabel);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener( (e) -> {
            clientState.connect(ipLabel.getText());
        });
        content.add(connectButton);

        JTextField publicKeyLabel = new JTextField(NaCl.asHex(clientState.publicKey));
        content.add(publicKeyLabel);

        RosterTableModel rosterTableModel = new RosterTableModel();
        JTable rosterTable = new JTable(rosterTableModel);
        rosterTable.setDefaultRenderer(RosterItem.class,
                new RosterTableRenderer(rosterTableModel));
        rosterTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();
                    new TextWin(rosterTableModel.get(row), clientState);
                }
            }
        });
        content.add(rosterTable);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener( (e) -> {
                new AddWin(rosterTableModel, clientState);
        });
        content.add(addContactButton);

        add(content);

        for (RosterItem item : clientState.loadRoster()) {
            rosterTableModel.add(item);
        }

        pack();
        setVisible(true);
    }
}
