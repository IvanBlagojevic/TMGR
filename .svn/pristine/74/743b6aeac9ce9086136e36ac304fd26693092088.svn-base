package groovy.fileAnalysis

import org.gs4tr.termmanager.service.impl.ImportValidationCallback;

import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;

importValidationCallback = new ImportValidationCallback(1);
importValidationCallback.setFileName("Skype_SKY000001.tbx");
importValidationCallback.addAlert(AlertSubject.HEADER_CHECK, AlertType.ERROR, "There are multiple \"TermEntryID\" columns.")
importValidationCallback.addAlert(AlertSubject.MULTIPLE_WORKSHEETS, AlertType.WARNING, "Your import file contains multiple work sheets: \"[Sheet1, Sheet2]\". Please note that only the first active sheet will be imported.");


