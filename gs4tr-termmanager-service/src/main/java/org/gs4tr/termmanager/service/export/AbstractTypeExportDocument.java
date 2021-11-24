package org.gs4tr.termmanager.service.export;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ExportInfo;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.search.TermEntrySearchRequest;

public abstract class AbstractTypeExportDocument<T extends AbstractDelimiterLineBuilder>
	extends AbstractDelimiterExportDocument<T> {

    private static final String SEPARATOR = "\r\n";

    @Override
    protected String buildTermEntryXml(TermEntry termEntry, ExportInfo exportInfo, boolean isWS,
	    ExportTermsCallback exportTermsCallback) {
	if (isWS) {
	    setWsStatuses();
	}
	TermEntrySearchRequest searchRequest = getSearchRequest();
	String source = searchRequest.getSourceLocale();

	String target = source;

	List<String> targetLocales = searchRequest.getTargetLocales();
	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    target = targetLocales.get(0);
	}
	List<Term> terms = exportTermsCallback.getAllTermEntryTerms(termEntry);
	List<Term> sourceTerms = findTermsByLanguage(terms, source);
	List<Term> targetTerms = findTermsByLanguage(terms, target);

	if (CollectionUtils.isEmpty(sourceTerms) && CollectionUtils.isEmpty(targetTerms)) {
	    return StringUtils.EMPTY;
	}
	StringBuilder stringBuilder = new StringBuilder();
	List<String> attributeTypes = searchRequest.getTermAttributes();
	List<String> noteTypes = searchRequest.getTermNotes();

	boolean termEntryExported = false;

	for (Term sourceTerm : sourceTerms) {
	    boolean sourceTermExportable = isTermExportable(sourceTerm) && getSearchRequest().isIncludeSource()
		    && (!isWS || isExportableByStatus(sourceTerm));
	    for (Term targetTerm : targetTerms) {
		if (isTermExportable(targetTerm) && isExportableByStatus(targetTerm)
			&& (isWS ? sourceTermExportable : Boolean.TRUE)) {
		    termEntryExported = true;
		    T builder = getBuilderInstance();
		    exportInfo.incrementTotalTermsExported();

		    if (sourceTermExportable) {
			exportInfo.incrementTotalTermsExported();
			builder.addField(sourceTerm.getName());
		    }

		    builder.addField(targetTerm.getName());
		    String descriptions;

		    Set<Description> termEntryDescriptions = termEntry.getDescriptions();

		    Set<Description> sourceDescriptions = sourceTermExportable ? sourceTerm.getDescriptions() : null;

		    Set<Description> targetDescriptions = targetTerm.getDescriptions();
		    descriptions = appendDescriptions(termEntryDescriptions, sourceDescriptions, targetDescriptions,
			    attributeTypes, noteTypes);
		    builder.addField(descriptions);
		    String line = builder.toString();
		    stringBuilder.append(line);
		    stringBuilder.append(SEPARATOR);
		}
	    }
	}

	if (termEntryExported) {
	    int totalTermEntriesExported = exportInfo.getTotalTermEntriesExported();
	    exportInfo.setTotalTermEntriesExported(totalTermEntriesExported + 1);
	}

	return stringBuilder.toString();
    }

    @Override
    abstract T getBuilderInstance();

    @Override
    protected boolean isExportableByInTranslation(Term term, Boolean exportable) {
	boolean inFinalReview = getSearchRequest().getStatuses()
		.contains(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
	boolean inTranslationReview = getSearchRequest().getStatuses()
		.contains(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());

	return ItemStatusTypeHolder.isTermInTranslation(term) && (inFinalReview || inTranslationReview);
    }

    @Override
    protected boolean isExportableByStatus(Term term) {
	List<String> allowedStatuses = getSearchRequest().getStatuses();

	// If empty allow all statuses
	if (CollectionUtils.isEmpty(allowedStatuses)) {
	    return true;
	}

	return allowedStatuses.contains(term.getStatus());
    }

    @Override
    protected Boolean isTermExportable(Term term) {

	TermEntrySearchRequest searchRequest = getSearchRequest();
	Date dateModifiedFrom = searchRequest.getDateModifiedFrom();

	String termLocale = term.getLanguageId();
	String sourceLocale = searchRequest.getSourceLocale();

	String targetLocale = sourceLocale;

	List<String> targetLocales = searchRequest.getTargetLocales();
	if (CollectionUtils.isNotEmpty(targetLocales)) {
	    targetLocale = targetLocales.get(0);
	}

	boolean exportableByLanguage = (termLocale.equals(sourceLocale) || termLocale.equals(targetLocale));

	boolean exportableByName = StringUtils.isNotEmpty(term.getName());

	boolean exportableByDate = dateModifiedFrom == null || term.getDateModified() > dateModifiedFrom.getTime();

	return exportableByName && exportableByLanguage && exportableByDate;
    }

}
