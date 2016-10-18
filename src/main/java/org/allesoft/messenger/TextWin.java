package org.allesoft.messenger;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextWin extends JFrame {
    public TextWin(String userId) {
        super("Add contact");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        content.add(conversationArea);
        SwingUI.currentArea = conversationArea;

        JTextField newMessageField = new JTextField();
        newMessageField.setText("");
        content.add(newMessageField);

        JButton addContactButton = new JButton("Done");
        addContactButton.addActionListener((e) -> {
            String text = newMessageField.getText();
            conversationArea.append(LineSeparator.Unix + "My: " + text);
            byte[] chars = text.getBytes();
            byte[] empty = new byte[256];
            for (int j = 0; j < empty.length; j ++) {
                empty[j] = 'x';
            }
            newMessageField.setText("");
            try {
                int i = 0;
                for (; i <= chars.length - empty.length; i += empty.length) {
                    SwingUI.connection.getOutputStream().write(chars, i, empty.length);
                }
                SwingUI.connection.getOutputStream().write(chars, i, chars.length - i);
                SwingUI.connection.getOutputStream().write(empty, 0, empty.length - chars.length + i);
            } catch (IOException x) {
                System.out.println("send error");
            }
        });
        content.add(addContactButton);

        add(content);

        pack();
        setVisible(true);
    }
}
