package org.allesoft.messenger;

import com.neilalexander.jnacl.NaCl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class MainWin extends JFrame {
    public MainWin() {
        super("Swing messenger");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setSize(300, 500);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JTextField publicKeyLabel = new JTextField(NaCl.asHex(SwingUI.publicKey));
        content.add(publicKeyLabel);

        RosterTableModel rosterTableModel = new RosterTableModel();
        JTable rosterTable = new JTable(rosterTableModel);
        rosterTable.setDefaultRenderer(RosterItem.class,
                new RosterTableRenderer(rosterTableModel));
        rosterTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = target.getSelectedColumn();
                    new TextWin(rosterTableModel.get(row));
                }
            }
        });
        content.add(rosterTable);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener( (e) -> {
                new AddWin(rosterTableModel);
        });
        content.add(addContactButton);

        add(content);

        rosterTableModel.add(new RosterItem("Item1"));
        rosterTableModel.add(new RosterItem("Item2"));

        pack();
        setVisible(true);
    }
}
