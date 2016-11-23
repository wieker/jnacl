package org.allesoft.messenger.swingui;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.MessageSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextWin extends JFrame {

    public TextWin(String userId, Client client) {
        super("Conversation with " + userId);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        conversationArea.setEditable(false);
        content.add(conversationArea, BorderLayout.CENTER);
        MessageSender sender = client.addConversation(userId, text1 -> conversationArea.append(LineSeparator.Unix + text1));

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());
        JTextField newMessageField = new JTextField();
        newMessageField.setText("");
        sendPanel.add(newMessageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Done");
        sendButton.addActionListener((e) -> {
            String text = newMessageField.getText();
            conversationArea.append(LineSeparator.Unix + "My: " + text);
            newMessageField.setText("");

            sender.send(text);
            newMessageField.requestFocus();
        });
        sendPanel.add(sendButton, BorderLayout.EAST);
        content.add(sendPanel, BorderLayout.SOUTH);

        newMessageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    sendButton.doClick();
                }
            }
        });
        conversationArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                newMessageField.requestFocus();
            }
        });

        add(content);

        pack();
        setVisible(true);
        newMessageField.requestFocus();
    }
}
