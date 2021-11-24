package groovy.fileAnalysis

import java.util.HashMap
import java.util.List

import org.gs4tr.foundation.locale.Locale;

import org.gs4tr.termmanager.service.file.analysis.model.Alert
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject
import org.gs4tr.termmanager.service.file.analysis.model.AlertType
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguageReport
import org.gs4tr.termmanager.service.file.analysis.model.Status
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute;
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttribute.Level
import org.gs4tr.termmanager.service.file.analysis.model.ImportAttributeReport

custom1 = new ImportAttribute("custom1", Level.TERM_ATTRIBUTE);
custom2 = new ImportAttribute("custom2", Level.TERM_ATTRIBUTE);
custom3 = new ImportAttribute("custom3", Level.TERM_ATTRIBUTE);

importAttributeReport = new ImportAttributeReport("Nikon");
importAttributeReport.getImportAttributes().add(custom1);
importAttributeReport.getImportAttributes().add(custom2);
importAttributeReport.getImportAttributes().add(custom3);

report = builder.fileAnalysisReport([importAttributeReport: importAttributeReport, termEntryIdExist: false])

projectAttributeNames = [
    custom1.getName(),
    custom2.getName(),
    custom3.getName()] as Set

attributesByLevel = new HashMap<Level, Set<String>>();
attributesByLevel.put(Level.TERM_ATTRIBUTE, projectAttributeNames);