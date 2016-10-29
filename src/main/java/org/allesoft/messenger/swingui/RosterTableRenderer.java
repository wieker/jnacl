package org.allesoft.messenger.swingui;

import org.allesoft.messenger.jclient.RosterItem;
import org.allesoft.messenger.jclient.RosterItemImpl;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class RosterTableRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        RosterItem item = (RosterItem)value;
        setForeground(Color.BLACK);
        setBackground(Color.RED);
        this.setText(item.getValue());
        return this;
    }

    public RosterTableRenderer(RosterTableModel model) {
        super();
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.add(new RosterItemImpl("New item"));
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
