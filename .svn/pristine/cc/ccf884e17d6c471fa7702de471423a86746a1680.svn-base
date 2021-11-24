package org.gs4tr.termmanager.model;

public enum BatchJobName {
    CANCEL_TRANSLATION("Canceling translation."), COMPLETE_TRANSLATION("Completing translation."), DELETE_PROJECT_NOTE(
	    "Note maintenance in progress..."), DELETE_RENAME_PROJECT_ATTRIBUTE(
		    "Attribute maintenance in progress..."), EXPORT_DOCUMENT("Export in progress..."), IMPORT_REPORT(
			    "Import summary report export is in progress..."), REPORT_EXPORT(
				    "Report export is in progress..."), SEND_EMAIL_MESSAGE("Sending email message.");

    private String _processDisplayName;

    private BatchJobName(String processDisplayName) {
	_processDisplayName = processDisplayName;
    }

    public String getProcessDisplayName() {
	return _processDisplayName;
    }
}
