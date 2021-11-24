package org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer;

import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.tm3.api.TmException;

public interface ITermEntrySynchronizer {

    void clearCache();

    void initialize(ITmgrGlossaryConnector connector, ImportOptionsModel importOptions) throws TmException;

    TermEntry synchronizeTermEntries(TermEntry incoming, TermEntry existing, ImportOptionsModel importModel,
	    ImportSummary importSummary) throws TmException;
}
