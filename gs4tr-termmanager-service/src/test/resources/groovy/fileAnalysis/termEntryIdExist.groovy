package groovy.fileAnalysis

import java.util.List

import org.gs4tr.termmanager.service.file.analysis.model.Alert
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject
import org.gs4tr.termmanager.service.file.analysis.model.AlertType
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisAlerts

alert = new Alert(AlertSubject.MULTIPLE_WORKSHEETS, AlertType.WARNING, "Your import file contains multiple work sheets");

alerts = new FileAnalysisAlerts("Nikon");
alerts.getAlerts().add(alert);

report = builder.fileAnalysisReport([fileAnalysisAlerts: alerts, termEntryIdExist: true])
