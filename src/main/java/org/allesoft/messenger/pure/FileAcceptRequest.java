package org.allesoft.messenger.pure;

/**
 * Created by kabramovich on 23.11.2016.
 */
public interface FileAcceptRequest {
    boolean accept(FTPLayer layer, String fileName, long size);
}
