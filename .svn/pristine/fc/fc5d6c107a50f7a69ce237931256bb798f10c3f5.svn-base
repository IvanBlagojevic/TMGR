package org.gs4tr.termmanager.service.file.analysis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.model.ImportLanguage;
import org.gs4tr.termmanager.service.file.analysis.model.Status;
import org.gs4tr.termmanager.service.file.analysis.request.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("importLanguagePostProcessorChain")
class ImportLanguagePostProcessorChain implements PostProcessorChain {

    private static final Log LOG = LogFactory.getLog(ImportLanguagePostProcessorChain.class);

    private final PostProcessorChain _nextChain;

    @Autowired
    public ImportLanguagePostProcessorChain(
	    @Qualifier("importAttributePostProcessorChain") PostProcessorChain nextChain) {
	_nextChain = nextChain;
    }

    @Override
    public void postProcess(FileAnalysisReport target, Context context) {
	Set<String> userProjectLanguageCodes = context.getUserProjectLanguageCodes();

	List<ImportLanguage> importLanguages = target.getImportLanguageReport().getImportLanguages();

	for (final ImportLanguage importLanguage : importLanguages) {
	    String importLocaleCode = importLanguage.getLocale().getCode();
	    if (userProjectLanguageCodes.contains(importLocaleCode)) {
		importLanguage.setStatus(Status.EXISTING);
		String format = Messages.getString("ImportLanguagePostProcessor.0"); // $NON-NLS-1$
		LogHelper.debug(LOG, String.format(format, importLocaleCode));
		continue;
	    }

	    Map<String, List<Locale>> userProjectLocalesByLanguage = context.getUserProjectLocalesByLanguage();

	    String language = importLanguage.getLocale().getLanguage();
	    List<Locale> userProjectLocales = userProjectLocalesByLanguage.get(language);
	    if (CollectionUtils.isNotEmpty(userProjectLocales)) {
		for (Locale projectLocale : userProjectLocales) {
		    String projectLocaleCode = projectLocale.getCode();
		    importLanguage.addSimilarProjectLanguage(projectLocaleCode);
		    String format = Messages.getString("ImportLanguagePostProcessor.1"); // $NON-NLS-1$
		    LogHelper.warn(LOG, String.format(format, importLocaleCode, projectLocaleCode));
		}
	    }
	    importLanguage.setStatus(Status.NEW);
	}

	getNextChain().postProcess(target, context);
    }

    private PostProcessorChain getNextChain() {
	return _nextChain;
    }
}
