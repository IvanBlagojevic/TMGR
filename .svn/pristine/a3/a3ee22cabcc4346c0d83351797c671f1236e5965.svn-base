package org.gs4tr.termmanager.service.impl;

import java.io.Serializable;

import org.gs4tr.termmanager.model.ImportSummary;

public class ImportProgressInfo implements Serializable {

    private static final long serialVersionUID = -4475055409838041167L;

    private boolean _canceled = false;

    private ImportSummary _importSummary;

    private int _percentage;

    public ImportProgressInfo(int totalTermEntries) {
	_importSummary = new ImportSummary(totalTermEntries);
    }

    public ImportSummary getImportSummary() {
	return _importSummary;
    }

    public int getPercentage() {
	return _percentage;
    }

    public boolean isCanceled() {
	return _canceled;
    }

    public void setCanceled(boolean canceled) {
	_canceled = canceled;
    }

    public void setImportSummary(ImportSummary importSummary) {
	_importSummary = importSummary;
    }

    public void setPercentage(int percentage) {
	_percentage = percentage;
    }
}
