package org.gs4tr.termmanager.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.glossary.Term;

public class TerminologyUtils {

    public static Set<Term> getValidTerms(Set<Term> terms) {
	return CollectionUtils.isEmpty(terms) ? new HashSet<Term>()
		: terms.stream().filter(t -> validateTerm(t)).collect(Collectors.toSet());
    }

    public static boolean validateTerm(Term term) {
	return term.getUuId() != null && term.getStatus() != null && term.getLanguageId() != null
		&& term.getDateModified() != null && term.getUserCreated() != null;
    }
}
