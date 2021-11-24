package groovy.fileAnalysis

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

english = new ImportLanguage(Locale.ENGLISH.getCode());
germany = new ImportLanguage(Locale.GERMANY.getCode());
french = new ImportLanguage(Locale.FRENCH.getCode());

importLanguageReport = new ImportLanguageReport("Nikon");
importLanguageReport.getImportLanguages().add(english);
importLanguageReport.getImportLanguages().add(germany);
importLanguageReport.getImportLanguages().add(french);

custom1 = new ImportAttribute("custom1", Level.TERM_ATTRIBUTE);
custom2 = new ImportAttribute("custom2", Level.TERM_ATTRIBUTE);
custom3 = new ImportAttribute("custom3", Level.TERM_ATTRIBUTE);

importAttributeReport = new ImportAttributeReport("Nikon");
importAttributeReport.getImportAttributes().add(custom1);
importAttributeReport.getImportAttributes().add(custom2);
importAttributeReport.getImportAttributes().add(custom3);

alerts = new FileAnalysisAlerts("Nikon");

report = builder.fileAnalysisReport([fileAnalysisAlerts: alerts, importLanguageReport: importLanguageReport, importAttributeReport: importAttributeReport, termEntryIdExist: false])

userProjectLanguageCodes = [
    Locale.ENGLISH.getCode(),
    Locale.GERMANY.getCode(),
    Locale.FRENCH.getCode()] as Set
