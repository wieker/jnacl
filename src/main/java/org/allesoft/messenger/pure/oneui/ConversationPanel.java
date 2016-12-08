package org.allesoft.messenger.pure.oneui;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.allesoft.messenger.Capture;
import org.allesoft.messenger.NaCl;
import org.allesoft.messenger.Playback;
import org.allesoft.messenger.jclient.Client;
import org.allesoft.messenger.jclient.MessageSender;
import org.allesoft.messenger.pure.ChannelMux;
import org.allesoft.messenger.pure.FTPLayer;
import org.allesoft.messenger.pure.InfiniThreadFactory;
import org.allesoft.messenger.pure.TCPLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class ConversationPanel extends JPanel {

    public ConversationPanel(String userId, Client client, Repainter repainter) {
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        setName(userId);

        JTextArea conversationArea = new JTextArea();
        conversationArea.setText("Conversation with " + userId);
        conversationArea.setEditable(false);
        content.add(conversationArea, BorderLayout.CENTER);
        MessageSender sender = client.addConversation(userId, text1 -> {
            conversationArea.append(LineSeparator.Unix + text1);
            repainter.repaint();
        });
        FTPLayer audioMessageLayer = new FTPLayer((layer, name, size) -> {
            System.out.println("Audio request accepted");
            InfiniThreadFactory.tryItNow(() -> layer.receive(new byte[size]));
            return true;
        }, (layer) -> {
            System.out.println("Started playback");
            Playback playback = new Playback(layer.getMemoryToTransfer());
            playback.start();
        });
        client.registerChannel(userId, ChannelMux.FILE_CHANNEL, audioMessageLayer);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
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
            repainter.repaint();
        });
        sendPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(sendPanel, BorderLayout.NORTH);

        JPanel additionalPanel = new JPanel();
        JButton sendFileButton = new JButton("Send File");
        sendFileButton.addActionListener((e) -> {
        });
        additionalPanel.add(sendFileButton);
        JButton startSocksClientButton = new JButton("Start SOCKS4 entry point");
        startSocksClientButton.addActionListener((e) -> {
            client.registerChannel(userId, ChannelMux.TCP_CHANNEL, new TCPLayer());
        });
        additionalPanel.add(startSocksClientButton);
        JButton startSocksServerButton = new JButton("Start SOCKS4 exit point");
        startSocksServerButton.addActionListener((e) -> {
            client.registerChannel(userId, ChannelMux.TCP_CHANNEL, new TCPLayer("ololo", 555));
        });
        additionalPanel.add(startSocksServerButton);
        JButton audioMessageButton = new JButton("Audio message");
        audioMessageButton.addActionListener((e) -> {
            SwingUtilities.invokeLater(() -> {
                Capture capture = new Capture();
                capture.start();
                InfiniThreadFactory.tryItNow(() -> { Thread.sleep(5000); });
                capture.stop();
                while (capture.getAudioBytes() == null) {
                    InfiniThreadFactory.tryItNow(() -> { Thread.sleep(100); });
                }
                InfiniThreadFactory.tryItNow(() -> { audioMessageLayer.sendFile(capture.getAudioBytes(), "audioMessage"); });
            });
        });
        additionalPanel.add(audioMessageButton);
        JButton videoMessageButton = new JButton("Video message");
        videoMessageButton.addActionListener((e) -> {
        });
        additionalPanel.add(videoMessageButton);
        bottomPanel.add(additionalPanel, BorderLayout.SOUTH);
        content.add(bottomPanel, BorderLayout.SOUTH);

        newMessageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    sendButton.doClick();
                }
            }
        });
        conversationArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                newMessageField.requestFocus();
                String text = newMessageField.getText();
                newMessageField.setText(text + e.getKeyChar());
            }
        });

        add(content);

        newMessageField.requestFocus();
    }
}
