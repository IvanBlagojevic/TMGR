package org.gs4tr.termmanager.service.termentry.synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.persistence.solr.query.TextFilter;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.TermHelper;
import org.gs4tr.termmanager.service.termentry.memorizer.Memorizable;
import org.gs4tr.termmanager.service.xls.report.builder.ImportSummaryReportBuilder;
import org.gs4tr.tm3.api.TmException;

public abstract class AbstractTermEntrySynchronizer implements ITermEntrySynchronizer {

    private ITmgrGlossaryBrowser _browser;

    /*
     * This class provides implementation for custom equals and contains methods
     */
    private EqualsBuilderHelper _equalsBuilder;

    private boolean _ignoreCase = true;

    private ITmgrGlossarySearcher _searcher;

    protected Memorizable<TermEntry, String> _cache;

    protected AbstractTermEntrySynchronizer() {
	_equalsBuilder = new EqualsBuilderHelper();
    }

    @Override
    public void clearCache() {
	getCache().clear();
    }

    @Override
    public void initialize(ITmgrGlossaryConnector connector, ImportOptionsModel importOptions) throws TmException {
	_searcher = connector.getTmgrSearcher();
	_browser = connector.getTmgrBrowser();
	_ignoreCase = importOptions.isIgnoreCase();
	initCache(numElements(connector), importOptions);
	_equalsBuilder.setIgnoreCase(importOptions.isIgnoreCase());
    }

    private int numElements(ITmgrGlossaryConnector connector) {
	TmgrSolrConnector tmgrConnector = (TmgrSolrConnector) connector;
	return tmgrConnector.getAdapter().getImportBatchSize();
    }

    protected void appendReport(ImportSummaryReportBuilder builder) throws TmException {
	try {
	    builder.appendReport();
	} catch (Exception e) {
	    throw new TmException(e.getMessage(), e);
	}
    }

    protected void countUnchanged(TermEntry existing, ImportSummary summary) {
	summary.getUnchangedTermEntrieIds().add(existing.getUuId());
	Map<String, Set<Term>> languageTerms = existing.getLanguageTerms();
	if (MapUtils.isNotEmpty(languageTerms)) {
	    Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	    for (Entry<String, Set<Term>> entry : languageTerms.entrySet()) {
		CountWrapper countWrapper = termCountsPerLanguage.computeIfAbsent(entry.getKey(),
			key -> new CountWrapper());

		Set<Term> terms = entry.getValue();
		terms.forEach(t -> countWrapper.getUnchangedTermIds().add(t.getUuId()));
	    }
	}
    }

    protected ITmgrGlossaryBrowser getBrowser() {
	return _browser;
    }

    protected Memorizable<TermEntry, String> getCache() {
	return _cache;
    }

    protected EqualsBuilderHelper getEqualsBuilder() {
	return _equalsBuilder;
    }

    protected ITmgrGlossarySearcher getSearcher() {
	return _searcher;
    }

    protected abstract void initCache(int numElements, ImportOptionsModel importOptions);

    protected boolean isIgnoreCase() {
	return _ignoreCase;
    }

    protected TermEntry searchMatch(TermEntry entry, ImportOptionsModel importModel, ImportSummary importSummary,
	    String... flParameters) throws TmException {

	String syncLanguageId = importModel.getSyncLanguageId();

	TermEntry cachedTermEntry = getCache().fetch(entry, syncLanguageId);

	if (cachedTermEntry != null) {
	    return cachedTermEntry;
	}

	boolean caseSensitive = !importModel.isIgnoreCase();

	Long projectId = importModel.getProjectId();
	entry.setProjectId(projectId);

	Map<String, Set<Term>> languageTerms = entry.getLanguageTerms();

	if (languageTerms == null || CollectionUtils.isEmpty(languageTerms.get(syncLanguageId))) {
	    return TermHelper.skipOrImportWithoutSync(entry, importModel, importSummary);
	}

	Set<Term> terms = languageTerms.get(syncLanguageId);

	EqualsBuilderHelper equalsBuilder = getEqualsBuilder();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setImportSearch(caseSensitive);
	filter.addProjectId(projectId);
	filter.setSourceLanguage(syncLanguageId);
	filter.setPageable(new TmgrPageRequest(0, 100, null));
	filter.setExcludeDisabled(false);
	filter.addResultField(flParameters);

	List<TermEntry> matches = new ArrayList<>();

	for (Term term : terms) {
	    try {
		String termName = term.getName();
		if (StringUtils.isEmpty(termName)) {
		    continue;
		}
		TextFilter textFilter = new TextFilter(termName);
		textFilter.setCaseSensitive(caseSensitive);
		textFilter.setExactMatch(true);

		filter.setTextFilter(textFilter);

		List<TermEntry> results = getSearcher().concordanceSearch(filter).getResults();

		if (CollectionUtils.isNotEmpty(results)) {
		    // first iteration to check if there is no match in
		    // termEntry from file
		    for (TermEntry termEntry : results) {
			if (importSummary.getImportedTermEntryIds().contains(termEntry.getUuId())) {
			    return null;
			}
		    }

		    // second iteration is to find best match if any or just
		    // first match from result list
		    for (TermEntry termEntry : results) {
			if (equalsBuilder.isEquals(entry, termEntry)) {
			    return termEntry;
			}

			Set<Term> matchTerms = termEntry.getLanguageTerms().get(syncLanguageId);
			for (Term matchTerm : matchTerms) {
			    if (equalsBuilder.isEqual(termName, matchTerm.getName())) {
				matches.add(termEntry);
			    }
			}
		    }

		    if (!matches.isEmpty()) {
			return matches.get(0);
		    }
		}
	    } catch (TmException e) {
		matches.clear();
		throw new TmException(e.getMessage(), e);
	    }

	    matches.clear();
	}

	return null;
    }
}
