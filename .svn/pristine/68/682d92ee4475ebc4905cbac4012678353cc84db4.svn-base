package org.gs4tr.termmanager.model.dto.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.MultilingualTerm;
import org.gs4tr.termmanager.model.TermHolder;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.gs4tr.termmanager.model.TermTooltip;
import org.gs4tr.termmanager.model.dto.TargetDtoTermTranslation;
import org.gs4tr.termmanager.model.dto.TargetTerm;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.springframework.util.StringUtils;

public class MultilingualTermConverter {

    public static org.gs4tr.termmanager.model.dto.MultilingualTerm fromInternalToDto(MultilingualTerm internalEntity) {
	if (internalEntity == null) {
	    return null;
	}

	org.gs4tr.termmanager.model.dto.MultilingualTerm dtoEntity = new org.gs4tr.termmanager.model.dto.MultilingualTerm();

	boolean inTranslation = internalEntity.isInTranslation();
	boolean sourceInTranslation = internalEntity.isSourceInTranslation();

	dtoEntity.setInTranslation(inTranslation);
	dtoEntity.setSourceInTranslation(sourceInTranslation);
	dtoEntity.setProjectTicket(TicketConverter.fromInternalToDto(internalEntity.getProjectId()));
	dtoEntity.setAvailableTasks(internalEntity.getAvailableTasks());
	dtoEntity.setProjectName(internalEntity.getProjectName());
	dtoEntity.setTicket(internalEntity.getTermEntryId());
	dtoEntity.setTermEntryTicket(internalEntity.getTermEntryId());
	dtoEntity.setTermEntryCreationDate(internalEntity.getTermEntryCreationDate());
	dtoEntity.setUserCreated(internalEntity.getUserCreated());
	dtoEntity.setSubmissionTicket(TicketConverter.fromInternalToDto(internalEntity.getSubmissionId()));

	List<TermHolder> termHolders = internalEntity.getTerms();

	boolean isShowAutoSaved = internalEntity.isShowAutoSaved();

	TermHolder sourceTermHolder = findTermHolderLanguage(termHolders, internalEntity.getSourceLanguage());

	if (sourceTermHolder != null) {
	    termHolders.remove(sourceTermHolder);

	    List<Term> sourceTerms = new ArrayList<Term>(sourceTermHolder.getTerms());

	    Term sourceTerm = findFirstCreatedTerm(sourceTerms);
	    if (sourceTerm != null) {
		dtoEntity.setSourceTerm(new TargetDtoTermTranslation(sourceTerm, isShowAutoSaved));
		sourceTerms.remove(sourceTerm);
	    }
	    dtoEntity.setSourceSynonyms(TargetTermTranslationConverter.fromInternalToDto(sourceTerms, isShowAutoSaved));
	    dtoEntity.setSourceAlignment(sourceTermHolder.getAlignment().getValue());
	    Set<TermTooltip> sourceTermTooltip = sourceTermHolder.getTermTooltip();
	    if (CollectionUtils.isNotEmpty(sourceTermTooltip)) {
		dtoEntity.setSourceTermTooltip(sourceTermTooltip.toArray(new TermTooltip[sourceTermTooltip.size()]));
	    }
	}

	Map<String, TargetTerm> targetTermsMap = new HashMap<String, TargetTerm>();
	for (TermHolder termHolder : termHolders) {

	    List<Term> targetTerms = new ArrayList<Term>(termHolder.getTerms());

	    TargetTerm dtoTargetTerm = new TargetTerm();

	    Term targetTerm = findFirstCreatedTerm(targetTerms);
	    if (targetTerm != null) {
		dtoTargetTerm.setTargetTerm(new TargetDtoTermTranslation(targetTerm, isShowAutoSaved));
		targetTerms.remove(targetTerm);
	    }
	    dtoTargetTerm
		    .setTargetSynonyms(TargetTermTranslationConverter.fromInternalToDto(targetTerms, isShowAutoSaved));
	    dtoTargetTerm.setLanguageId(termHolder.getLanguageId());
	    dtoTargetTerm.setSubmissionId(termHolder.getSubmissionId());
	    dtoTargetTerm.setSubmissionName(termHolder.getSubmissionName());
	    dtoTargetTerm.setTargetAlignment(termHolder.getAlignment().getValue());

	    Set<TermTooltip> targetTermTooltip = termHolder.getTermTooltip();
	    if (CollectionUtils.isNotEmpty(targetTermTooltip)) {
		dtoTargetTerm
			.setTargetTermTooltip(targetTermTooltip.toArray(new TermTooltip[targetTermTooltip.size()]));
	    }

	    targetTermsMap.put(replaceLocaleCodeDash(termHolder.getLanguageId()), dtoTargetTerm);
	}

	dtoEntity.setTargetTerms(targetTermsMap);

	return dtoEntity;
    }

    public static List<MultilingualTerm> fromTermEntriesToMultilinguals(List<TermEntry> termentries,
	    TermSearchRequest command) {
	List<MultilingualTerm> results = new ArrayList<MultilingualTerm>();
	if (CollectionUtils.isEmpty(termentries)) {
	    return results;
	}

	// languages
	String sourceLanguageId = command.getSource();
	List<String> targetLanguageIds = command.getTargetLanguagesList();

	List<String> languages = new ArrayList<String>();
	languages.add(sourceLanguageId);
	if (CollectionUtils.isNotEmpty(targetLanguageIds)) {
	    languages.addAll(targetLanguageIds);
	}

	for (TermEntry termEntry : termentries) {
	    MultilingualTerm multilingualTerm = new MultilingualTerm();

	    List<TermHolder> termHolderList = new ArrayList<TermHolder>();

	    boolean inTranslation = true;
	    boolean inTranslationAsSource = false;

	    boolean isInFinalReview = false;
	    boolean isCanceled = false;

	    for (String language : languages) {
		TermHolder termHolder = new TermHolder();
		termHolder.setLanguageId(language);
		termHolder.setAlignment(Language.valueOf(language).getLanguageAlignment());

		Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
		// check null
		if (languageTerms == null) {
		    termHolderList.add(termHolder);
		    termHolder.setTerms(new HashSet<Term>());
		    continue;
		}

		Set<Term> terms = languageTerms.get(language);
		if (CollectionUtils.isEmpty(terms)) {
		    termHolderList.add(termHolder);
		    termHolder.setTerms(new HashSet<Term>());
		    continue;
		}

		termHolder.setTerms(terms);

		for (Term term : terms) {
		    String languageId = term.getLanguageId();

		    boolean statusInTranslation = ItemStatusTypeHolder.isTermInTranslation(term);
		    inTranslation = inTranslation && (statusInTranslation || term.getInTranslationAsSource());
		    inTranslationAsSource = !inTranslationAsSource ? term.getInTranslationAsSource() : true;
		    isInFinalReview = isInFinalReview || ItemStatusTypeHolder.isTermInFinalReview(term);
		    isCanceled = isCanceled || (term.getCanceled() != null ? term.getCanceled() : false);

		    if (StringUtils.isEmpty(multilingualTerm.getAssignee())) {
			multilingualTerm.setAssignee(term.getAssignee());
		    }

		    if (statusInTranslation || term.getInTranslationAsSource()) {
			termHolder.setSubmissionId(termEntry.getSubmissionId());
			termHolder.setSubmissionName(termEntry.getSubmissionName());
			termHolder.setSubmitter(termEntry.getSubmitter());

			Set<TermTooltip> termTooltip = new HashSet<TermTooltip>();
			TermTooltip tooltip = new TermTooltip(termEntry.getSubmissionName(), termEntry.getSubmitter());
			termTooltip.add(tooltip);
			termHolder.setTermTooltip(termTooltip);
		    }

		    if (languageId.equals(sourceLanguageId)) {
			termHolder.setSource(true);
		    }
		}
		termHolderList.add(termHolder);
	    }

	    multilingualTerm.setTermEntryId(termEntry.getUuId());
	    multilingualTerm.setProjectId(termEntry.getProjectId());
	    multilingualTerm.setProjectName(termEntry.getProjectName());
	    multilingualTerm.setSourceLanguage(sourceLanguageId);
	    multilingualTerm.setShowAutoSaved(command.isShowAutoSaved());
	    multilingualTerm.setTerms(termHolderList);
	    multilingualTerm.setTermEntryCreationDate(termEntry.getDateCreated());
	    multilingualTerm.setInTranslation(inTranslation || inTranslationAsSource);
	    multilingualTerm.setSourceInTranslation(inTranslationAsSource);
	    multilingualTerm.setSubmitter(termEntry.getSubmitter());
	    multilingualTerm.setUserCreated(termEntry.getUserCreated());
	    multilingualTerm.setSubmissionId(termEntry.getSubmissionId());
	    multilingualTerm.setCanceled(isCanceled);
	    multilingualTerm.setInFinalReview(isInFinalReview);

	    results.add(multilingualTerm);
	}

	return results;
    }

    public static String replaceLocaleCodeDash(String languageId) {
	return languageId.replaceAll(StringConstants.DASH, StringConstants.UNDERSCORE);
    }

    private static Term findFirstCreatedTerm(List<Term> targetTerms) {
	if (CollectionUtils.isEmpty(targetTerms)) {
	    return null;
	}

	for (Term term : targetTerms) {
	    if (term.isFirst()) {
		return term;
	    }
	}
	return null;
    }

    private static TermHolder findTermHolderLanguage(List<TermHolder> termHolders, String languageId) {
	for (TermHolder termHolder : termHolders) {
	    if (termHolder.getLanguageId().equals(languageId)) {
		return termHolder;
	    }
	}
	return null;
    }
}
