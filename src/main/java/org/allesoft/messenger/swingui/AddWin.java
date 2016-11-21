package org.allesoft.messenger.swingui;

import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.RosterItemImpl;

import javax.swing.*;
import java.awt.event.WindowEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class AddWin extends JFrame {
    public AddWin(RosterTableModel model, Client client) {
        super("Add contact");
        setName("addWin");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextField userIdField = new JTextField();
        userIdField.setName("userIdField");
        userIdField.setText("User ID");
        content.add(userIdField);

        JButton addContactDoneButton = new JButton("Done");
        addContactDoneButton.setName("addContactDoneButton");
        addContactDoneButton.addActionListener((e) -> {
            model.add(new RosterItemImpl(userIdField.getText()));
            client.writeRoster(model.getRoster());
            AddWin.this.dispatchEvent(new WindowEvent(AddWin.this, WindowEvent.WINDOW_CLOSING));
        });
        content.add(addContactDoneButton);

        add(content);

        pack();
        setVisible(true);
    }
}
