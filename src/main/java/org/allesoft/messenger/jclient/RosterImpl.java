package org.allesoft.messenger.jclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 19.10.2016.
 */
public class RosterImpl extends Roster {
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
    public void add(RosterItem item) {
        roster.add(item);
        for (RosterEventListener listener : listeners) {
            listener.fire();
        }
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
