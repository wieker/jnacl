package org.allesoft.messenger.jclient;

/**
 * Created by kabramovich on 18.10.2016.
 */
public class RosterItemImpl extends RosterItem {
    private String value;

    public RosterItemImpl() {
    }

    public RosterItemImpl(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
