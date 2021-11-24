package org.gs4tr.termmanager.io.edd.api;

import org.gs4tr.termmanager.io.exception.EventException;

@FunctionalInterface
public interface Handler<E extends Event> {

    void onEvent(E event) throws EventException;
}
