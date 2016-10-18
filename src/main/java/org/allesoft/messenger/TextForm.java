package org.allesoft.messenger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextForm {
    private JTextArea conversationText;
    private JTextField newMessageField;
    private JButton sendMessageButton;

    public TextForm() {
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conversationText.append("\nMy: " + newMessageField.getText());
                newMessageField.setText("");
            }
        });
    }
}
