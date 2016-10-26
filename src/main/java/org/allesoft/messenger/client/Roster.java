package org.allesoft.messenger.client;

/**
 * Created by kabramovich on 26.10.2016.
 */
public abstract class Roster {
    public abstract int size();

    public abstract void add(RosterItem item);

    public abstract RosterItem getByIndex(int index);

    public abstract void addListener(RosterEventListener listener);
}
