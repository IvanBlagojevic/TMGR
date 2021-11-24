package org.gs4tr.termmanager.persistence.update;

import org.gs4tr.termmanager.model.glossary.TermEntry;

@FunctionalInterface
public interface GetTermEntryCallback {

    TermEntry getTermEntryInstace(Object item);
}
