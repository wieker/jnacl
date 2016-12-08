package org.allesoft.messenger.pure.oneui;

import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.RosterError;
import org.allesoft.messenger.jclient.RosterItemImpl;
import org.allesoft.messenger.swingui.AddWin;
import org.allesoft.messenger.swingui.RosterTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class AddFriendPanel extends JPanel {
    public AddFriendPanel(RosterTableModel model, Client client) {
        setName("addWin");

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
            RosterError error = model.add(new RosterItemImpl(userIdField.getText()));
            if (!error.equals(RosterError.OK)) {
                validationLabel.setText(error.getMessage());
                validationLabel.setForeground(Color.RED);
                return;
            }
            client.writeRoster(model.getRoster());
        });
        content.add(addContactDoneButton);

        add(content);

        setVisible(true);
    }
}
