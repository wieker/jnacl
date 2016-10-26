package org.allesoft.messenger;

import org.allesoft.messenger.client.ClientState;
import org.allesoft.messenger.client.InternalState;
import org.allesoft.messenger.client.RosterItem;

import javax.swing.*;
import java.awt.event.WindowEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class AddWin extends JFrame {
    public AddWin(RosterTableModel model, ClientState clientState) {
        super("Add contact");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextField userIdField = new JTextField();
        userIdField.setText("User ID");
        content.add(userIdField);

        JButton addContactButton = new JButton("Done");
        addContactButton.addActionListener((e) -> {
            model.add(new RosterItem(userIdField.getText()));
            clientState.writeRoster(model.getRoster());
            AddWin.this.dispatchEvent(new WindowEvent(AddWin.this, WindowEvent.WINDOW_CLOSING));
        });
        content.add(addContactButton);

        add(content);

        pack();
        setVisible(true);
    }
}
