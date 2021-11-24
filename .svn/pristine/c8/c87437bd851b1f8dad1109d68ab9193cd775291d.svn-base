package org.gs4tr.termmanager.glossaryV2.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class TermConverter {

    public static Term buildTermFromSolrDocument(String termId, TermEntry termEntry) {
	return termEntry.ggetTermById(termId);
    }

    public static List<Term> buildTermsFromSolrDocuments(Collection<String> termIds, List<TermEntry> termEntries) {
	if (termEntries == null) {
	    return null;
	}

	List<Term> terms = new ArrayList<Term>();

	for (TermEntry termEntry : termEntries) {
	    if (CollectionUtils.isNotEmpty(termIds)) {
		for (String termId : termIds) {
		    Term term = buildTermFromSolrDocument(termId, termEntry);
		    if (term != null) {
			terms.add(term);
		    }
		}
	    } else {
		terms.addAll(termEntry.ggetTerms());
	    }
	}

	return terms;
    }
}
