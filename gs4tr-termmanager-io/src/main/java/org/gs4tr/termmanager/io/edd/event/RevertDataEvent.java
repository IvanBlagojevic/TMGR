package org.gs4tr.termmanager.io.edd.event;

import java.util.List;

import org.gs4tr.termmanager.model.glossary.TermEntry;

public class RevertDataEvent extends AbstractDataEvent {

    public RevertDataEvent(String collection, List<TermEntry> data) {
	super(collection, data);
    }

    public RevertDataEvent(List<TermEntry> data) {
	super(null, data);
    }
}
