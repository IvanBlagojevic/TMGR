package groovy.fileAnalysis

import org.gs4tr.termmanager.service.impl.ImportValidationCallback;

import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;

importValidationCallback = new ImportValidationCallback(1);
importValidationCallback.addAlert(AlertSubject.MULTIPLE_WORKSHEETS, AlertType.WARNING, "Your import file contains multiple work sheets: \"[Sheet1, Sheet2]\". Please note that only the first active sheet will be imported.");

importValidationCallback.setFileName("Skype_SKY000001.tbx");
importValidationCallback.setIdColumnExist(true);

languageAttributeMap = ["de-DE": 2, "fr-FR": 2] as Map

importValidationCallback.setLanguageAttributeMap(languageAttributeMap);
importValidationCallback.incrementTotalTerms();
importValidationCallback.incrementTotalTerms();

languageMap = importValidationCallback.getLanguageMap();
languageMap.put("en-US", 2);
languageMap.put("de-DE", 4);
languageMap.put("fr-FR", 2);

termEntryDescriptions = importValidationCallback.getTermEntryDescriptions();
termEntryDescriptions.add("definition");

termDescriptions = importValidationCallback.getTermDescriptions();
termDescriptions.add("context");

termNotes = importValidationCallback.getTermNotes();
termNotes.add("Usage");

termEntryAttributesCount = importValidationCallback.getTermEntryAttributesCount();
termEntryAttributesCount.put("definition", 2);
