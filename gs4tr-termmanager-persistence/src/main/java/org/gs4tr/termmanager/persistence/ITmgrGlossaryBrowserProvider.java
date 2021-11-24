package org.gs4tr.termmanager.persistence;

import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossaryBrowserProvider {
    ITmgrGlossaryBrowser getTmgrBrowser() throws TmException;
}
