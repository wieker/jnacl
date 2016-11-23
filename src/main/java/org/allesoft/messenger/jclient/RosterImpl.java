package org.allesoft.messenger.jclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 19.10.2016.
 */
public class RosterImpl implements Roster {
    private List<RosterItem> roster = new ArrayList<>();
    private List<RosterEventListener> listeners = new ArrayList<>();

    public List<RosterItem> getRoster() {
        return roster;
    }

    @Override
    public int size() {
        return roster.size();
    }

    @Override
    public RosterError add(RosterItem item) {
        if (item.getValue().trim().isEmpty()) {
            return RosterError.EMPTY_NAME;
        }
        if (!item.getValue().trim().matches("([0-9a-f]){64}")) {
            return RosterError.WRONG_SYMBOL;
        }
        for (RosterItem existing : roster) {
            if (existing.getValue().equalsIgnoreCase(item.getValue().trim())) {
                return RosterError.DUPLICATE_ENTRY;
            }
        }
        roster.add(item);
        for (RosterEventListener listener : listeners) {
            listener.fire();
        }
        return RosterError.OK;
    }

    @Override
    public RosterItem getByIndex(int index) {
        return roster.get(index);
    }

    @Override
    public void addListener(RosterEventListener listener) {
        listeners.add(listener);
    }
}
