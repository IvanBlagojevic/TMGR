package org.gs4tr.termmanager.persistence;

import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossaryUpdaterProvider {
    ITmgrGlossaryUpdater getTmgrUpdater() throws TmException;
}
