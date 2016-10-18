package org.allesoft.messenger;

import com.neilalexander.jnacl.NaCl;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import javax.swing.*;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextWin extends JFrame {
    public TextWin(String userId) {
        super("Conversation with " + userId);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        content.add(conversationArea);
        SwingUI.currentArea = conversationArea;
        InternalState.peerPublicKey = NaCl.getBinary(userId);
        try {
            InternalState.naCl = new NaCl(InternalState.privateKey, InternalState.peerPublicKey);
        } catch (Exception x) {
            System.out.println("NaCL exception");
        }

        JTextField newMessageField = new JTextField();
        newMessageField.setText("");
        content.add(newMessageField);

        JButton addContactButton = new JButton("Done");
        addContactButton.addActionListener((e) -> {
            String text = newMessageField.getText();
            conversationArea.append(LineSeparator.Unix + "My: " + text);
            byte[] nonce = new byte[NaCl.crypto_secretbox_NONCEBYTES];
            SecureRandom rng = new SecureRandom();
            rng.nextBytes(nonce);
            byte[] plain = text.getBytes();
            int pktCount = (plain.length + 1 + NaCl.crypto_secretbox_NONCEBYTES + NaCl.crypto_secretbox_ZEROBYTES) / 256 + 1;
            byte[] out = new byte[pktCount * 256];
            byte[] chars = InternalState.naCl.encrypt(text.getBytes(), nonce);
            System.arraycopy(nonce, 0, out, 0, NaCl.crypto_secretbox_NONCEBYTES);
            out[NaCl.crypto_secretbox_NONCEBYTES] = (byte) chars.length;
            System.arraycopy(chars, 0, out, NaCl.crypto_secretbox_NONCEBYTES + 1, chars.length);
            newMessageField.setText("");
            try {
                InternalState.connection.getOutputStream().write(out);
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
