package org.gs4tr.termmanager.persistence.solr.importer;

import org.gs4tr.termmanager.model.ImportErrorMessage;
import org.gs4tr.termmanager.model.ImportSummary;

public class ImportErrorHelper {

    public static void handleErrorMessage(final ImportSummary summary, String message) {
	if (summary.getErrorMessages() == null || summary.getErrorMessages().size() < 100) {
	    ImportErrorMessage error = new ImportErrorMessage();
	    error.setErrorMessage(message);
	    error.setTermEntryCounter(
		    summary.getNoSkippedTermEntries().intValue() + summary.getNoImportedTermEntries().intValue());
	    summary.addErrorMessages(error);
	}
    }

    private ImportErrorHelper() {
	super();
    }
}
