package org.gs4tr.termmanager.service.file.analysis;

import static org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver.getMessage;

import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.request.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("sourceLanguagePostProcessorChain")
class SourceLanguagePostProcessorChain implements PostProcessorChain {

    private static final Log LOG = LogFactory.getLog(SourceLanguagePostProcessorChain.class);

    private final PostProcessorChain _nextChain;

    @Autowired
    public SourceLanguagePostProcessorChain(
	    @Qualifier("importLanguagePostProcessorChain") PostProcessorChain nextChain) {
	_nextChain = nextChain;
    }

    /**
     * <p>
     * If source language is missing in the file, considered that as an error
     * and abort analysis for the file. Remove other warning(s), if any exist.
     * </p>
     * 
     * {@inheritDoc}
     */
    @Override
    public void postProcess(FileAnalysisReport target, Context context) {
	List<ImportLanguage> importLanguages = target.getImportLanguageReport().getImportLanguages();

	ImportLanguage sourceLanguage = tryToFindSource(importLanguages, context);
	if (Objects.isNull(sourceLanguage)) {
	    String message = String.format(getMessage("SourceLanguagePostProcessor.0"), context.getSourceLanguage()); // $NON-NLS-1$
	    Alert alert = new Alert(AlertSubject.NO_COMMON_SOURCE_LANGUAGE, AlertType.ERROR, message);
	    target.getFileAnalysisAlerts().getAlerts().clear();
	    target.getFileAnalysisAlerts().getAlerts().add(alert);
	    target.setImportLanguageReport(null);
	    target.setImportAttributeReport(null);
	    LogHelper.error(LOG, alert.getMessage());
	    return;
	}

	sourceLanguage.setStatus(Status.SOURCE);
	getNextChain().postProcess(target, context);
    }

    private PostProcessorChain getNextChain() {
	return _nextChain;
    }

    private ImportLanguage tryToFindSource(List<ImportLanguage> importLanguages, Context context) {
	final String sourceLanguage = context.getSourceLanguage();
	return importLanguages.stream().filter(language -> sourceLanguage.equals(language.getLanguage().getLocale()))
		.findFirst().orElse(null);
    }
}
