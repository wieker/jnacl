package org.allesoft.messenger;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.messenger.client.Client;
import org.allesoft.messenger.client.MessageSender;

import javax.swing.*;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextWin extends JFrame {

    public TextWin(String userId, Client client) {
        super("Conversation with " + userId);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        content.add(conversationArea);
        MessageSender sender = client.addConversation(userId, text1 -> conversationArea.append(text1));

        JTextField newMessageField = new JTextField();
        newMessageField.setText("");
        content.add(newMessageField);

        JButton addContactButton = new JButton("Done");
        addContactButton.addActionListener((e) -> {
            String text = newMessageField.getText();
            conversationArea.append(LineSeparator.Unix + "My: " + text);
            newMessageField.setText("");

            sender.send(text);
        });
        content.add(addContactButton);

        add(content);

        pack();
        setVisible(true);
    }
}
