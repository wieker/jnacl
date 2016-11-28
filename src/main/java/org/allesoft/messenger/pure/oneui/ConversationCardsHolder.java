package org.allesoft.messenger.pure.oneui;

import org.allesoft.messenger.jclient.Client;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kabramovich on 28.11.2016.
 */
public class ConversationCardsHolder {
    private Map<String, ConversationPanel> conversationPanelMap = new TreeMap<>();
    private Client client;
    private JPanel rightPanel;

    public ConversationCardsHolder(Client client, JPanel rightPanel) {
        this.client = client;
        this.rightPanel = rightPanel;
    }

    public ConversationPanel getConversation(String userId) {
        ConversationPanel panel = conversationPanelMap.get(userId);
        if (panel == null) {
            panel = new ConversationPanel(userId, client);
            rightPanel.add(panel);
            ((CardLayout)rightPanel.getLayout()).show(rightPanel, userId);
        }
        return panel;
    }
}
