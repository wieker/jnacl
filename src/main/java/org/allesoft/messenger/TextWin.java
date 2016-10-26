package org.allesoft.messenger;

import com.neilalexander.jnacl.NaCl;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.messenger.client.ClientState;
import org.allesoft.messenger.client.InternalState;

import javax.swing.*;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class TextWin extends JFrame {

    public TextWin(String userId, ClientState clientState) {
        super("Conversation with " + userId);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(400, 200);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        content.add(conversationArea);
        NaCl naCl = createNaCl(userId, clientState);
        clientState.conversations.add((packet) -> {
            TextWin.this.receive(naCl, conversationArea, packet, clientState);
        });

        JTextField newMessageField = new JTextField();
        newMessageField.setText("");
        content.add(newMessageField);

        JButton addContactButton = new JButton("Done");
        addContactButton.addActionListener((e) -> {
            String text = newMessageField.getText();
            conversationArea.append(LineSeparator.Unix + "My: " + text);
            newMessageField.setText("");

            sendBytes(naCl, userId, text, clientState);
        });
        content.add(addContactButton);

        add(content);

        pack();
        setVisible(true);
    }

    void receive(NaCl naCl, JTextArea conversation, byte[] packet, ClientState clientState) {
        int length = packet[NaCl.crypto_secretbox_NONCEBYTES];
        byte[] chars = new byte[length];
        System.arraycopy(packet, NaCl.crypto_secretbox_NONCEBYTES + 1, chars, 0, length);
        byte[] decoded = naCl.decrypt(chars, packet);
        byte[] dstKey = new byte[NaCl.crypto_secretbox_KEYBYTES];
        System.arraycopy(decoded, 0, dstKey, 0, NaCl.crypto_secretbox_KEYBYTES);
        for (int i = 0; i < dstKey.length; i ++) {
            if (clientState.publicKey[i] != dstKey[i]) {
                return;
            }
        }
        if (conversation != null) {
            SwingUtilities.invokeLater(() -> {
                conversation.append(LineSeparator.Unix + new String(decoded,
                        NaCl.crypto_secretbox_KEYBYTES, decoded.length - NaCl.crypto_secretbox_KEYBYTES));
            });
        }
    }

    private void sendBytes(NaCl naCl, String userId, String text, ClientState clientState) {
        byte[] nonce = new byte[NaCl.crypto_secretbox_NONCEBYTES];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(nonce);
        byte[] plain = text.getBytes();
        byte[] binaryId = NaCl.getBinary(userId);
        int pktCount = (plain.length + 1 + NaCl.crypto_secretbox_KEYBYTES + NaCl.crypto_secretbox_NONCEBYTES + NaCl.crypto_secretbox_ZEROBYTES) / 256 + 1;
        byte[] out = new byte[pktCount * 256];
        byte[] finalPlain = new byte[plain.length + binaryId.length];
        System.arraycopy(binaryId, 0, finalPlain, 0, NaCl.crypto_secretbox_KEYBYTES);
        System.arraycopy(plain, 0, finalPlain, NaCl.crypto_secretbox_KEYBYTES, plain.length);
        byte[] chars = naCl.encrypt(finalPlain, nonce);
        System.arraycopy(nonce, 0, out, 0, NaCl.crypto_secretbox_NONCEBYTES);
        out[NaCl.crypto_secretbox_NONCEBYTES] = (byte) chars.length;
        System.arraycopy(chars, 0, out, NaCl.crypto_secretbox_NONCEBYTES + 1, chars.length);
        try {
            clientState.connection.getOutputStream().write(out);
        } catch (IOException x) {
            System.out.println("send error");
        }
    }

    private NaCl createNaCl(String userId, ClientState clientState) {
        NaCl naCl = null;
        try {
            naCl = new NaCl(clientState.privateKey, NaCl.getBinary(userId));
        } catch (Exception x) {
            System.out.println("NaCL exception");
        }
        return naCl;
    }
}
