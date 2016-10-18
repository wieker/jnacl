package org.allesoft.messenger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class MainWin extends JFrame {
    public MainWin() {
        super("Swing messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(300, 500);

        JPanel content = new JPanel();

        RosterTableModel rosterTableModel = new RosterTableModel();
        JTable rosterTable = new JTable(rosterTableModel);
        rosterTable.setDefaultRenderer(RosterItem.class,
                new RosterTableRenderer(rosterTableModel));
        content.add(rosterTable);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddWin(rosterTableModel);
            }
        });
        content.add(addContactButton);

        add(content);

        setVisible(true);

        rosterTableModel.add(new RosterItem("Item1"));
        rosterTableModel.add(new RosterItem("Item2"));
    }
}
