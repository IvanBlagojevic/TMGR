package org.gs4tr.termmanager.glossaryV2;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;

public class GlossaryV2Utils {

    public static String findTermMarkerId(TermEntry termEntry, String status, String languageId) {
	String defaultMarkerId = UUID.randomUUID().toString();

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (languageTerms == null) {
	    return defaultMarkerId;
	}

	Set<Term> terms = languageTerms.get(languageId);
	if (terms == null) {
	    return defaultMarkerId;
	}

	Optional<Term> optional = terms.stream().filter(t -> (t.getStatus().equals(status))).findFirst();
	return optional.isPresent() ? optional.get().getUuId() : defaultMarkerId;
    }
}
