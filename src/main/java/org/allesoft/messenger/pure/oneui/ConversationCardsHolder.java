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
    private JComponent visible;
    private Repainter repainter;

    public ConversationCardsHolder(Client client, JPanel rightPanel, Repainter repainter) {
        this.client = client;
        this.rightPanel = rightPanel;
        this.repainter = repainter;
    }

    public ConversationPanel getConversation(String userId) {
        ConversationPanel panel = conversationPanelMap.get(userId);
        if (visible != null) {
            rightPanel.remove(visible);
        }
        if (panel == null) {
            panel = new ConversationPanel(userId, client, repainter);
        }
        rightPanel.add(panel);
        ((CardLayout)rightPanel.getLayout()).show(rightPanel, userId);
        repainter.repaint();
        return panel;
    }

    public void add(JComponent component) {
        if (visible != null) {
            rightPanel.remove(visible);
        }
        visible = component;
        rightPanel.add(component);
        ((CardLayout)rightPanel.getLayout()).show(rightPanel, component.getName());
        repainter.repaint();
    }
}
