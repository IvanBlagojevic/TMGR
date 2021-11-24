package org.gs4tr.termmanager.webservice.converters;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.LanguageModel;
import org.gs4tr.termmanager.model.dto.TermV2Model;
import org.gs4tr.termmanager.model.dto.converter.DescriptionConverter;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.webservice.model.response.SegmentTermHit;
import org.gs4tr.termmanager.webservice.model.response.TermEntryHit;

public class TermEntryConverter {

    public static List<SegmentTermHit> convertToSegmentTermHits(List<TermEntry> termEntries, String source,
	    List<String> targets, String currentUser, TmProject project) {

	if (CollectionUtils.isEmpty(termEntries)) {
	    return new ArrayList<>();
	}

	Function<? super TermEntry, ? extends List<SegmentTermHit>> toTermHits = termEntry -> createTermHits(targets,
		termEntry, termEntry.getLanguageTerms().get(source), currentUser, project);

	return termEntries.stream().map(toTermHits).flatMap(Collection::stream).collect(toList());
    }

    public static List<TermEntryHit> convertToTermEntryHits(List<TermEntry> termEntries, String source,
	    List<String> targetLanguages) {

	List<TermEntryHit> hits = new ArrayList<>();

	if (CollectionUtils.isEmpty(termEntries)) {
	    return hits;
	}

	for (TermEntry termEntry : termEntries) {
	    Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	    LanguageModel sources = toLangSet(source, languageTerms.get(source));

	    List<LanguageModel> targets = targetLanguages.stream()
		    .map(language -> toLangSet(language, languageTerms.get(language))).collect(toList());

	    TermEntryHit hit = new TermEntryHit();
	    hit.setTermEntryId(termEntry.getUuId());
	    hit.setEntryDescriptions(DescriptionConverter.convertDescriptionsToDto(termEntry.getDescriptions()));
	    hit.setSources(sources);
	    hit.setTargets(targets);

	    hits.add(hit);
	}

	return hits;
    }

    // Suppresses default constructor, ensuring non-instant-ability.
    private TermEntryConverter() {
    }

    private static List<TermV2Model> convertTerms(Set<Term> terms) {

	if (CollectionUtils.isEmpty(terms)) {
	    return new ArrayList<>();
	}

	return terms.stream().map(TermEntryConverter::toTermV2Model).collect(toList());
    }

    private static List<SegmentTermHit> createTermHits(List<String> targetLanguages, TermEntry termEntry,
	    Set<Term> sourceTerms, String currentUser, TmProject project) {

	if (CollectionUtils.isEmpty(sourceTerms)) {
	    return new ArrayList<>();
	}

	removeNonExportableTerms(termEntry, currentUser, project, targetLanguages);

	return sourceTerms.stream().map(term -> termHit(termEntry, term, targetLanguages, currentUser, project))
		.filter(Objects::nonNull).collect(toList());
    }

    private static Set<Description> joinEntryAndTermDescriptions(TermEntry termEntry, Term term) {
	Set<Description> joined = new HashSet<>();

	Set<Description> entryDescriptions = termEntry.getDescriptions();
	if (CollectionUtils.isNotEmpty(entryDescriptions)) {
	    joined.addAll(entryDescriptions);
	}

	Set<Description> termDescriptions = term.getDescriptions();
	if (CollectionUtils.isNotEmpty(termDescriptions)) {
	    joined.addAll(termDescriptions);
	}

	return joined;
    }

    private static void removeNonExportableTerms(TermEntry termEntry, String user, TmProject project,
	    List<String> targetLanguages) {

	if (CollectionUtils.isEmpty(targetLanguages)) {
	    return;
	}

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Map.Entry<String, Set<Term>>> terms = languageTerms.entrySet();
	Iterator<Map.Entry<String, Set<Term>>> iteratorTerms = terms.iterator();
	while (iteratorTerms.hasNext()) {
	    Set<Term> langTerms = iteratorTerms.next().getValue();
	    langTerms.removeIf(
		    term -> !ServiceUtils.isTermExportable(term, user, ServiceUtils.getExportableStatusList(project)));
	}
    }

    private static SegmentTermHit termHit(TermEntry termEntry, Term sourceTerm, List<String> targetLanguages,
	    String user, TmProject project) {
	if (CollectionUtils.isEmpty(targetLanguages)) {
	    return null;
	}

	if (!ServiceUtils.isTermExportable(sourceTerm, user, ServiceUtils.getExportableStatusList(project))) {
	    return null;
	}

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	SegmentTermHit termHit = new SegmentTermHit();

	termHit.setLocale(sourceTerm.getLanguageId());
	/*
	 * TERII-2922 - Term Entry attribute should be sent as source attribute
	 */
	Set<Description> descriptions = joinEntryAndTermDescriptions(termEntry, sourceTerm);

	termHit.setDescriptions(DescriptionConverter.convertDescriptionsToDto(descriptions));

	termHit.setTermHit(sourceTerm.getName());

	List<LanguageModel> targets = targetLanguages.stream()
		.map(language -> toLangSet(language, languageTerms.get(language))).collect(toList());

	/* TERII-4886 - don't allow empty targets */
	targets.removeIf(model -> model.getTerms().isEmpty());

	if (targets.isEmpty()) {
	    return null;
	}

	termHit.setTargets(targets);

	return termHit;
    }

    private static LanguageModel toLangSet(String languageId, Set<Term> terms) {
	LanguageModel langSet = new LanguageModel();
	langSet.setLocale(languageId);
	langSet.setTerms(convertTerms(terms));

	return langSet;
    }

    private static TermV2Model toTermV2Model(Term term) {
	TermV2Model termModel = new TermV2Model();
	termModel.setTermText(term.getName());
	termModel.setForbidden(term.getForbidden());
	termModel.setFuzzy(term.isFuzzy());
	termModel.setTermId(term.getUuId());
	Set<Description> desc = term.getDescriptions();
	termModel.setDescriptions(DescriptionConverter.convertDescriptionsToDto(desc));
	termModel.setStatus(ItemStatusTypeHolder.getStatusDisplayName(term.getStatus()));
	return termModel;
    }
}
