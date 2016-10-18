package org.allesoft.messenger;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class RosterTableModel implements TableModel {
    List<RosterItem> roster = new ArrayList<>();
    List<TableModelListener> changeListeners = new ArrayList<>();

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
        return roster.get(rowIndex);
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
        return roster.get(index).value;
    }
}
