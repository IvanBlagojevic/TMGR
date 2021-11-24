package org.gs4tr.termmanager.glossaryV2.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.tm3.api.glossary.GlossaryField;
import org.gs4tr.tm3.api.glossary.TermAttribute;

public class BilingualTermConverter {

    public static List<org.gs4tr.tm3.api.glossary.Term> buildBilingualTermFromTermEntry(TermEntry termEntry,
	    Locale sourceLocale, Locale targetLocale, String username, List<String> exportableStatuses) {
	List<org.gs4tr.tm3.api.glossary.Term> bilingualTerms = new ArrayList<>();

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	if (Objects.isNull(languageTerms)) {
	    return bilingualTerms;
	}

	String sourceLanguage = sourceLocale.getCode();
	String targetLanguage = targetLocale.getCode();

	Set<Term> sourceTerms = languageTerms.get(sourceLanguage);
	Set<Term> targetTerms = languageTerms.get(targetLanguage);

	if (CollectionUtils.isEmpty(sourceTerms) || CollectionUtils.isEmpty(targetTerms)) {
	    return bilingualTerms;
	}

	for (Term sourceTerm : sourceTerms) {

	    String source = sourceTerm.getName();
	    boolean sourceTermExportable = ServiceUtils.isTermExportable(sourceTerm, username, exportableStatuses);

	    for (Term targetTerm : targetTerms) {

		if (sourceTermExportable && ServiceUtils.isTermExportable(targetTerm, username, exportableStatuses)) {
		    String target = targetTerm.getName();

		    Map<GlossaryField, String> metadata = new HashMap<>();

		    List<TermAttribute> sourceAttributes = new ArrayList<>();
		    handleDescriptions(sourceAttributes, metadata, sourceTerm.getDescriptions(), sourceLanguage);

		    /*
		     * TERII-2922 - WF4/TS | Term Entry attribute should be sent as source attribute
		     */
		    handleDescriptions(sourceAttributes, metadata, termEntry.getDescriptions(), sourceLanguage);

		    List<TermAttribute> targetAttributes = new ArrayList<>();
		    handleDescriptions(targetAttributes, metadata, targetTerm.getDescriptions(), targetLanguage);

		    org.gs4tr.tm3.api.glossary.Term term = new org.gs4tr.tm3.api.glossary.Term(source, sourceLocale,
			    target, targetLocale);
		    term.setId(termEntry.getUuId());
		    term.setCreationDate(new Date(termEntry.getDateCreated()));
		    term.setCreationUser(termEntry.getUserCreated());
		    term.setModificationDate(new Date(termEntry.getDateModified()));
		    term.setModificationUser(termEntry.getUserModified());
		    term.setSourceAttributes(sourceAttributes);
		    term.setTargetAttributes(targetAttributes);
		    term.setMetadata(metadata);
		    term.setSourceStatus(ItemStatusTypeHolder.getStatusDisplayName(sourceTerm.getStatus()));
		    term.setTargetStatus(ItemStatusTypeHolder.getStatusDisplayName(targetTerm.getStatus()));

		    bilingualTerms.add(term);
		}
	    }
	}

	return bilingualTerms;
    }

    private static void buildMetadata(Map<GlossaryField, String> metadata, String languageId, String type,
	    String value) {
	StringBuilder builder = new StringBuilder();
	String metaValue = metadata.get(GlossaryField.DESCRIPTION);
	if (StringUtils.isNotEmpty(metaValue)) {
	    builder.append(metaValue);
	}

	builder.append(languageId).append(SolrGlossaryAdapter.RS).append(type).append(SolrGlossaryAdapter.RS)
		.append(value).append(SolrGlossaryAdapter.RS);
	metaValue = builder.toString();
	metadata.put(GlossaryField.DESCRIPTION, metaValue);
    }

    protected static void handleDescriptions(List<TermAttribute> attributes, Map<GlossaryField, String> metadata,
	    Set<Description> descriptions, String languageId) {
	if (CollectionUtils.isEmpty(descriptions)) {
	    return;
	}

	for (Description desc : descriptions) {
	    if (Description.ATTRIBUTE.equals(desc.getBaseType())) {
		String type = desc.getType();
		String value = desc.getValue();
		TermAttribute attribute = new TermAttribute(type, value);
		attributes.add(attribute);
		buildMetadata(metadata, languageId, type, value);
	    }
	}
    }
}
