package org.gs4tr.termmanager.persistence;

import java.util.Map;

import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.faceting.FacetTermCounts;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossarySearcher extends IGlossarySearcher<TermEntry, TmgrSearchFilter, TmException> {

    Map<Long, Long> getNumberOfTermEntries(TmgrSearchFilter filter);

    long getNumberOfTerms(TmgrSearchFilter filter) throws TmException;

    FacetTermCounts searchFacetTermCounts(TmgrSearchFilter filter);
}
