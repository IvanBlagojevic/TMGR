package org.gs4tr.termmanager.glossaryV2.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;

public class BlacklistTermConverter extends BilingualTermConverter {

    public static List<BlacklistTerm> buildBlacklistTermFromTermEntry(TermEntry termEntry, Locale locale,
	    String username, List<String> exportableStatuses) {
	List<BlacklistTerm> bilingualTerms = new ArrayList<>();

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (Objects.isNull(languageTerms)) {
	    return bilingualTerms;
	}

	String languageId = locale.getCode();

	Set<Term> terms = languageTerms.get(languageId);

	Set<Term> blacklistTerms = new HashSet<>();
	List<String> suggestions = new ArrayList<>();

	if (CollectionUtils.isNotEmpty(terms)) {
	    for (Term term : terms) {
		if (isBlacklistTerm(term)) {
		    blacklistTerms.add(term);
		} else if (ServiceUtils.isTermExportable(term, username, exportableStatuses)) {
		    suggestions.add(term.getName());
		}
	    }
	}

	for (Term term : blacklistTerms) {
	    String termText = term.getName();

	    BlacklistTerm blaclistTerm = new BlacklistTerm(termEntry.getUuId(), termText,
		    suggestions.toArray(new String[suggestions.size()]), locale);
	    blaclistTerm.setCreationDate(new Date(termEntry.getDateCreated()));
	    blaclistTerm.setCreationUser(termEntry.getUserCreated());
	    blaclistTerm.setModificationDate(new Date(termEntry.getDateModified()));
	    blaclistTerm.setModificationUser(termEntry.getUserModified());

	    bilingualTerms.add(blaclistTerm);
	}

	return bilingualTerms;

    }

    private static boolean isBlacklistTerm(Term term) {
	return ItemStatusTypeHolder.BLACKLISTED.getName().equals(term.getStatus());
    }
}
