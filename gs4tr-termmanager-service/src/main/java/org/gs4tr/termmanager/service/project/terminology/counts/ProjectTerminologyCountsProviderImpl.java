package org.gs4tr.termmanager.service.project.terminology.counts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.ProjectTerminologyCounts;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts.LanguageTermCount;
import org.gs4tr.termmanager.persistence.solr.query.AbstractPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrPageRequest;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.TermEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * <p>
 * Concrete <tt>ProjectTerminologyCountsProvider</tt> implementation that
 * provides a way to get <code>ProjectTerminologyCounts</code>.
 * </p>
 * 
 * @since 5.0
 */
@Component
class ProjectTerminologyCountsProviderImpl implements ProjectTerminologyCountsProvider {

    private static final Log LOG = LogFactory.getLog(ProjectTerminologyCountsProviderImpl.class);

    private static final String SEARCH_FACET_TERM_COUNTS = "TermEntryService#searchFacetTermCounts(..)"; //$NON-NLS-1$

    @Value("${synonym.number:5}")
    private int _synonymNumber;

    private final TermEntryService _termEntryService;

    @Autowired
    public ProjectTerminologyCountsProviderImpl(TermEntryService termEntryService) {
	_termEntryService = termEntryService;
    }

    @Override
    public ProjectTerminologyCounts getProjectTerminologyCounts(Long projectId, List<String> languages) {
	Validate.notNull(projectId, Messages.getString("ProjectTerminologyCountsProviderImpl.0")); //$NON-NLS-1$

	Validate.notEmpty(languages, Messages.getString("ProjectTerminologyCountsProviderImpl.2")); //$NON-NLS-1$

	TmgrSearchFilter f = getTmgrFilter(projectId, languages);

	ProjectTerminologyCounts terminologyCounts = new ProjectTerminologyCounts();
	terminologyCounts.setProjectId(projectId);
	terminologyCounts.setLanguages(new ArrayList<>(languages));
	terminologyCounts.setNumberOfTermEntries(getNumberOfTermEntries(f));

	FacetTermCounts fTermCounts = getFacetTermCounts(f);
	Map<String, LanguageTermCount> termCountByLanguage = fTermCounts.getTermCountByLanguage();
	for (Entry<String, LanguageTermCount> entry : termCountByLanguage.entrySet()) {
	    Long termCount = entry.getValue().getTermCount();
	    terminologyCounts.put(entry.getKey(), termCount);
	}

	return terminologyCounts;
    }

    private FacetTermCounts getFacetTermCounts(final TmgrSearchFilter f) {
	if (LOG.isInfoEnabled()) {
	    StopWatch clock = new StopWatch(String.format(Messages.getString("ProjectTerminologyCountsProviderImpl.1"), //$NON-NLS-1$
		    f.getProjectIds().get(0), f.getTargetLanguages()));
	    try {
		clock.start(SEARCH_FACET_TERM_COUNTS);
		return getTermEntryService().searchFacetTermCounts(f);
	    } finally {
		clock.stop();
		LOG.info(clock.prettyPrint());
	    }
	}
	return getTermEntryService().searchFacetTermCounts(f);
    }

    private long getNumberOfTermEntries(final TmgrSearchFilter f) {
	return getTermEntryService().getNumberOfTermEntries(f).get(f.getProjectIds().get(0)).longValue();
    }

    private int getSynonymNumber() {
	return _synonymNumber;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private TmgrSearchFilter getTmgrFilter(Long projectId, List<String> languages) {
	final TmgrSearchFilter f = new TmgrSearchFilter();
	f.setTargetLanguages(languages);
	f.addProjectId(projectId);
	f.addLanguageResultField(true, getSynonymNumber(), languages.stream().toArray(String[]::new));
	/*
	 * 1 is maximum number of facets for a field that should be returned
	 */
	f.setPageable(new TmgrPageRequest(AbstractPageRequest.DEFAULT_PAGE, 1, null));
	return f;
    }
}
