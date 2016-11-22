package org.allesoft.messenger.jclient;

/**
 * Created by tatyana on 11/22/2016.
 */
public enum RosterError {
    OK("OK"),
    DUPLICATE_ENTRY("Duplicated user name"),
    EMPTY_NAME("Empty name"),
    WRONG_SYMBOL("Wrong symbol"),
    ;

    String message;

    RosterError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
