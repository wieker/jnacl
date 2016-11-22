package org.allesoft.messenger.jclient;

/**
 * Created by kabramovich on 26.10.2016.
 */
public interface Roster {
    int size();

    RosterError add(RosterItem item);

    RosterItem getByIndex(int index);

    void addListener(RosterEventListener listener);
}
