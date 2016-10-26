package org.allesoft.messenger;

import org.allesoft.messenger.client.RosterImpl;
import org.allesoft.messenger.client.RosterItem;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class RosterTableModel implements TableModel {
    RosterImpl roster;
    List<TableModelListener> changeListeners = new ArrayList<>();

    public RosterTableModel(RosterImpl roster) {
        this.roster = roster;
        roster.addListener(() -> {
            for (TableModelListener l : changeListeners) {
                l.tableChanged(new TableModelEvent(this, roster.size(),
                        roster.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
            }
        });
    }

    @Override
    public int getRowCount() {
        return roster.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return "Account";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return RosterItem.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return roster.getByIndex(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        changeListeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        changeListeners.remove(l);
    }

    public void add(RosterItem rosterItem) {
        roster.add(rosterItem);
        for (TableModelListener l : changeListeners) {
            l.tableChanged(new TableModelEvent(this, roster.size(),
                    roster.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
        }
    }

    public String get(int index) {
        return roster.getByIndex(index).getValue();
    }

    public RosterImpl getRoster() {
        return roster;
    }
}
