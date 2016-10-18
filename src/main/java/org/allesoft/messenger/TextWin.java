package org.allesoft.messenger;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextWin extends JFrame {
    public TextWin(String userId) {
        super("Add contact");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        content.add(conversationArea);

        JTextField newMessageField = new JTextField();
        newMessageField.setText("");
        content.add(newMessageField);

        JButton addContactButton = new JButton("Done");
        addContactButton.addActionListener((e) -> {
            conversationArea.append(LineSeparator.Unix + "My: " + newMessageField.getText());
            newMessageField.setText("");
        });
        content.add(addContactButton);

        add(content);

        setVisible(true);
    }
}
