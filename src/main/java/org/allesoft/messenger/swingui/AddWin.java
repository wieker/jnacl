package org.allesoft.messenger.swingui;

import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.RosterItemImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class AddWin extends JFrame {
    public AddWin(RosterTableModel model, Client client, JFrame mainWin) {
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

        JLabel validationLabel = new JLabel();
        validationLabel.setName("validationLabel");
        content.add(validationLabel);

        JButton addContactDoneButton = new JButton("Done");
        addContactDoneButton.setName("addContactDoneButton");
        addContactDoneButton.addActionListener((e) -> {
            if (!model.add(new RosterItemImpl(userIdField.getText()))) {
                validationLabel.setText("Duplicated user name");
                validationLabel.setForeground(Color.RED);
                pack();
                return;
            }
            client.writeRoster(model.getRoster());
            mainWin.pack();
            AddWin.this.dispatchEvent(new WindowEvent(AddWin.this, WindowEvent.WINDOW_CLOSING));
        });
        content.add(addContactDoneButton);

        add(content);

        pack();
        setVisible(true);
    }
}
