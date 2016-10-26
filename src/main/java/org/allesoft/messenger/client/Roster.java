package org.allesoft.messenger.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kabramovich on 19.10.2016.
 */
public class Roster {
    List<RosterItem> roster = new ArrayList<>();
    List<RosterEventListener> listeners = new ArrayList<>();

    public List<RosterItem> getRoster() {
        return roster;
    }

    public void setRoster(List<RosterItem> roster) {
        this.roster = roster;
    }

    public int size() {
        return roster.size();
    }

    public void add(RosterItem item) {
        roster.add(item);
        for (RosterEventListener listener : listeners) {
            listener.fire();
        }
    }

    public RosterItem getByIndex(int index) {
        return roster.get(index);
    }

    public void addListener(RosterEventListener listener) {
        listeners.add(listener);
    }
}
