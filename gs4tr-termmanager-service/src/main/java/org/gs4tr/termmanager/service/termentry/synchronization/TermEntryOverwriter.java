package org.gs4tr.termmanager.service.termentry.synchronization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.TermHelper;
import org.gs4tr.termmanager.service.termentry.memorizer.ArrayDequeMemorizer;
import org.gs4tr.termmanager.service.termentry.memorizer.HashMapMemorizer;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.gs4tr.termmanager.service.xls.report.builder.ImportSummaryReportBuilder;
import org.gs4tr.tm3.api.TmException;

/**
 * <i>Overwrite on import</i>:
 * <p>
 * When this option is selected TMGR should be overwritten with content form the
 * file based on TermEntryID. Languages that are not in the file should not be
 * affected. Languages in the file should completely replace previous content in
 * the TMGR for appropriate term entry: all terms, synonyms, attributes and
 * notes.
 * <p>
 * 
 * @see <a href=
 *      "https://collaborate.translations.com/display/techprodmng/Term+Import">Import
 *      Terminology</a>
 */
public class TermEntryOverwriter extends AbstractTermEntrySynchronizer {

    private static final Log LOGGER = LogFactory.getLog(TermEntryOverwriter.class);

    /* Languages that were in the header */
    private List<String> _importedLocales;

    public TermEntryOverwriter() {
	super();
    }

    @Override
    public TermEntry synchronizeTermEntries(TermEntry incoming, TermEntry existing, ImportOptionsModel importModel,
	    ImportSummary summary) throws TmException {

	setImportedLocales(importModel.getImportLocales());

	existing = getCache().fetch(incoming, null);

	if (existing != null) {
	    getCache().store(incoming);
	    return incoming;
	}

	existing = findTermEntry(incoming, importModel, summary);
	if (existing == null) {
	    getCache().store(incoming);
	    return null;
	}

	Long projectId = existing.getProjectId();
	if (!importModel.getProjectId().equals(projectId)) {
	    return null;
	}

	EqualsBuilderHelper equalsBuilder = getEqualsBuilder();
	equalsBuilder.setIgnoreCase(isIgnoreCase());

	if (equalsBuilder.isEquals(incoming, existing)) {
	    getCache().store(existing);
	    countUnchanged(existing, summary);
	    return existing;
	}

	TermEntry existingClone = new TermEntry(existing);

	ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(existingClone, existing,
		importModel.getAllowedTermEntryAttributes(), importModel.getSyncLanguageId(), getImportedLocales(),
		summary.getReport(), isIgnoreCase());

	/* Handle termEntry descriptions */
	DescriptionImportWrapper descWrapper = new DescriptionImportWrapper();
	boolean termEntryDescriptionModified = overwriteDescriptions(incoming.getDescriptions(),
		existing.getDescriptions(), descWrapper, importModel.getAllowedTermEntryAttributesMap());

	existing.setDescriptions(descWrapper.getOverwrittenDescriptions());

	summary.getNoImportedTermEntryAttributes().add(descWrapper.getCount().intValue());
	summary.getImportedTermEntryAttributes().addAll(descWrapper.getTypes());

	List<Term> beforeOverwriteTerms = new ArrayList<>();
	List<Term> terms = existing.ggetTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    beforeOverwriteTerms.addAll(terms);
	}

	/* Handle terms and terms descriptions */
	TermImportWrapper termWrapper = overwriteTerms(incoming, existing, summary.getNoImportedTermsPerLanguage(),
		importModel);

	summary.getNoDeletedTerms().add(termWrapper.getDeletedCount());
	summary.getNoUpdatedTerms().add(termWrapper.getUpdatedCount());
	summary.getNoImportedTerms().add(termWrapper.getAddedCount());
	summary.getNoSkippedTerms().add(termWrapper.getSkipedCount());
	summary.getImportedTargetLanguages().addAll(termWrapper.getAddedLanguages());

	existing.setLanguageTerms(getLanguageTerms(termWrapper.getOverwrittenTerms()));

	existing.setUserModified(incoming.getUserModified());

	List<Term> afterOverwriteTerms = existing.ggetTerms();

	if (termEntryDescriptionModified) {
	    existing.setDateModified(new Date().getTime());
	}

	/* Set termEntry dateModified based on newest term dateModified */
	TermHelper.setTermEntryDateModified(existing, summary);

	/* Handle imported, updated and deleted termEntries count */
	handleTermEntryCounts(summary, existing, beforeOverwriteTerms, afterOverwriteTerms,
		termEntryDescriptionModified);

	DescriptionImportWrapper termDescWrapper = termWrapper.getDescriptionWrapper();

	summary.getImportedTermDescriptions().addAll(termDescWrapper.getTypes());
	summary.getNoImportedTermAttributes().add(termDescWrapper.getCount());

	appendReport(builder);

	getCache().store(existing);

	return existing;
    }

    /*
     * Languages that are in TMGR but not in the file shouldn't be changed.
     */
    private void addExistingLanguages(TermImportWrapper wrapper, Set<Term> existingTerms) {
	Iterator<Term> it = existingTerms.iterator();
	while (it.hasNext()) {
	    Term term = it.next();
	    if (term.getFirst()) {
		wrapper.addTerm(term);
		it.remove();
		break;
	    }
	}

	for (Term term : existingTerms) {
	    wrapper.addTerm(term);
	}
    }

    private int addExistingTerms(TermImportWrapper wrapper, CountWrapper perLanguageCount, Set<Term> existingTerms,
	    Set<Description> existingDescriptions) {
	int existingEnabledTermCount = 0;

	for (Term existing : existingTerms) {
	    wrapper.addTerm(existing);
	    if (!existing.isDisabled()) {
		String status = existing.getStatus();

		Set<Description> descriptions = existing.getDescriptions();
		if (CollectionUtils.isNotEmpty(descriptions)) {
		    existingDescriptions.addAll(descriptions);
		}
		TermHelper.decrementStatusCount(status, perLanguageCount);
		existingEnabledTermCount++;
	    }
	}

	return existingEnabledTermCount;
    }

    private void addIncomingTerms(TermImportWrapper wrapper, Set<Term> incomingTerms, Set<Term> existingTerms,
	    CountWrapper perLanguageCount, Set<Description> incomingDescriptions) {
	for (Term incoming : incomingTerms) {
	    /*
	     * Ensure modification date and modification user is not changed if a term is
	     * not updated with overwrite on import
	     */
	    if (!getEqualsBuilder().containsTerm(existingTerms, incoming).isPresent()) {
		wrapper.addTerm(incoming);
	    }
	    String status = incoming.getStatus();
	    Set<Description> descriptions = incoming.getDescriptions();
	    if (CollectionUtils.isNotEmpty(descriptions)) {
		incomingDescriptions.addAll(descriptions);
	    }

	    TermHelper.incrementStatusCount(status, perLanguageCount);
	}
    }

    private boolean allTermsEqual(List<Term> beforeOverwriteTerms, List<Term> afterOverwriteTerms) {
	return getEqualsBuilder().equalsAll(afterOverwriteTerms, beforeOverwriteTerms);
    }

    /* Count added and updated terms. */
    private void countAddedOrUpdated(Set<Term> incomingTerms, Set<Term> existingTerms, int existingTermCount,
	    int incomingTermCount, TermImportWrapper wrapper, CountWrapper perLanguageCount) {

	List<Term> existingUnchanged = new ArrayList<>();

	for (Term incoming : incomingTerms) {
	    Optional<Term> match = getEqualsBuilder().containsTerm(existingTerms, incoming);
	    if (!match.isPresent()) {
		if (incomingTermCount != existingTermCount) {
		    wrapper.getAddedCount().increment();
		    wrapper.getAddedLanguages().add(incoming.getLanguageId());
		    perLanguageCount.getTermCount().increment();
		    perLanguageCount.getAddedTermCount().increment();
		    TermHelper.incrementStatisticsStatusCount(incoming.getStatus(), perLanguageCount);
		} else {
		    // TODO: remove wrapper counts from code
		    wrapper.getUpdatedCount().increment();
		}
	    } else {
		Term term = match.get();
		perLanguageCount.getUnchangedTermIds().add(term.getUuId());
		existingUnchanged.add(term);
	    }
	}

	/*
	 * TERII-4961 - Import Summary: Updated term counts are incorrect after updating
	 * term that has a synonym
	 */
	if (incomingTermCount == existingTermCount) {
	    List<Term> existingClone = new ArrayList<>(existingTerms);
	    existingClone.removeAll(existingUnchanged);
	    existingClone.forEach(t -> perLanguageCount.getUpdatedTermIds().add(t.getUuId()));
	}
    }

    /*
     * Count deleted terms and disable all existing (overwritten) language terms.
     */
    private void countDeletedTerms(TermImportWrapper wrapper, Set<Term> incomingTerms, CountWrapper perLanguageCount,
	    int existingEnabledTermCount, Set<Term> existingTerms, final int incomingCount) {

	MutableInt deletedCount = wrapper.getDeletedCount();

	EqualsBuilderHelper equalsBuilder = getEqualsBuilder();

	long date = new Date().getTime();

	for (Term existing : existingTerms) {
	    if (!equalsBuilder.containsTerm(incomingTerms, existing).isPresent()) {
		existing.setDisabled(Boolean.TRUE);
		resolveFirstTerm(existingTerms, existing);
		existing.setDateModified(date);
		if (incomingCount != existingEnabledTermCount) {
		    deletedCount.increment();
		    perLanguageCount.getTermCount().decrement();

		    /*
		     * Use this later to update daily/weekly email notifications
		     */
		    perLanguageCount.getDeletedTerms().increment();
		}
	    }
	}
    }

    /*
     * Languages that were in the header but had no value in the file should be
     * deleted
     */
    private void deleteExistingLanguage(TermImportWrapper wrapper, Map<String, CountWrapper> addedPerLanguageCount,
	    Set<Term> existingTerms, String languageId) {

	CountWrapper perLanguageCount = initCountWrapper(addedPerLanguageCount, languageId);

	Iterator<Term> newTermIterator = existingTerms.iterator();

	long date = new Date().getTime();

	while (newTermIterator.hasNext()) {
	    Term existing = newTermIterator.next();
	    wrapper.addTerm(existing);
	    if (!existing.isDisabled()) {
		existing.setDisabled(Boolean.TRUE);
		existing.setDateModified(date);
		wrapper.getDeletedCount().increment();
		perLanguageCount.getDeletedTerms().increment();
		perLanguageCount.getTermCount().decrement();

		TermEntryUtils.updateDuplicateTerms(existingTerms, existing);

		TermHelper.decrementStatusCount(existing.getStatus(), perLanguageCount);
	    }
	}
    }

    private TermEntry findTermEntry(TermEntry entry, ImportOptionsModel importModel, ImportSummary summary)
	    throws TmException {
	String[] flParameters = getResultFields(importModel);

	return importModel.isOverwriteByTermEntryId() ? findTermEntryById(entry.getUuId(), importModel.getProjectId())
		: searchMatch(entry, importModel, summary, flParameters);
    }

    private TermEntry findTermEntryById(String termEntryId, Long projectId, String... flParameters) {
	TermEntry termEntry = null;
	try {
	    termEntry = getBrowser().findById(termEntryId, projectId, flParameters);
	} catch (TmException e) {
	    LOGGER.error(e.getMessage(), e);
	}
	return termEntry;
    }

    private List<String> getImportedLocales() {
	return _importedLocales;
    }

    private Map<String, Set<Term>> getLanguageTerms(Map<String, List<Term>> overwrittenTerms) {
	Map<String, Set<Term>> languageTerms = new HashMap<>(overwrittenTerms.size());
	for (Entry<String, List<Term>> entry : overwrittenTerms.entrySet()) {
	    languageTerms.put(entry.getKey(), new HashSet<>(entry.getValue()));
	}
	return languageTerms;
    }

    private String[] getResultFields(ImportOptionsModel importModel) {
	TmgrSearchFilter filter = new TmgrSearchFilter();

	List<String> importLocales = importModel.getImportLocales();
	filter.addLanguageResultField(true, importModel.getSynonymNumber(),
		importLocales.toArray(new String[importLocales.size()]));

	Set<String> resultFields = filter.getResultFields();
	return resultFields.toArray(new String[resultFields.size()]);
    }

    private void handleTermEntryCounts(ImportSummary importSummary, TermEntry existing, List<Term> beforeOverwriteTerms,
	    List<Term> afterOverwriteTerms, boolean termEntryDescriptionModified) {
	if (CollectionUtils.isEmpty(afterOverwriteTerms) && CollectionUtils.isNotEmpty(beforeOverwriteTerms)) {
	    importSummary.getNoDeletedTermEntries().increment();
	} else if (CollectionUtils.isNotEmpty(afterOverwriteTerms) && CollectionUtils.isEmpty(beforeOverwriteTerms)) {
	    importSummary.getNoImportedTermEntries().increment();
	} else if (termEntryDescriptionModified || !allTermsEqual(beforeOverwriteTerms, afterOverwriteTerms)) {
	    importSummary.getUpdatedTermEntrieIds().add(existing.getUuId());
	} else {
	    importSummary.getUnchangedTermEntrieIds().add(existing.getUuId());
	}
    }

    private CountWrapper initCountWrapper(Map<String, CountWrapper> addedPerLanguageCount, String languageId) {
	CountWrapper perLanguageCount = addedPerLanguageCount.get(languageId);
	if (perLanguageCount == null) {
	    perLanguageCount = new CountWrapper();
	    addedPerLanguageCount.put(languageId, perLanguageCount);
	}
	return perLanguageCount;
    }

    private boolean isAtLeastOneTermInTranslation(Set<Term> terms) {
	return terms.stream()
		.anyMatch(term -> term.getInTranslationAsSource() || ItemStatusTypeHolder.isTermInTranslation(term));
    }

    /*
     * Returns true if the user is added, updated or deleted description.
     */
    private boolean overwriteDescriptions(Set<Description> incomingDescriptions, Set<Description> existingDescriptions,
	    DescriptionImportWrapper wrapper, Map<String, Set<String>> allowedDescriptions) {

	Set<String> types = wrapper.getTypes();
	MutableInt count = wrapper.getCount();
	boolean modified = false;

	if (CollectionUtils.isEmpty(incomingDescriptions)) {
	    return modified;
	}

	// Issue TERII-4703
	for (Description incomingDesc : incomingDescriptions) {
	    String type = TermHelper.resolveDescriptionType(incomingDesc.getType(),
		    allowedDescriptions.get(incomingDesc.getBaseType()), isIgnoreCase());
	    incomingDesc.setType(type);
	    wrapper.getOverwrittenDescriptions().add(incomingDesc);
	}

	if (CollectionUtils.isEmpty(existingDescriptions)) {
	    modified = true;
	    for (Description incoming : incomingDescriptions) {
		count.increment();
		types.add(incoming.getType());
	    }
	    return modified;
	}

	for (Description incoming : incomingDescriptions) {
	    if (!getEqualsBuilder().containsDescription(incoming, existingDescriptions)) {
		count.increment();
		modified = true;
		types.add(incoming.getType());
	    }
	}
	return modified;
    }

    /*
     * All terms in the project language(s) for a given term entry in TMGR will be
     * overwritten with content from the file.
     */
    private TermImportWrapper overwriteTerms(TermEntry incomingTermEntry, TermEntry existingTermEntry,
	    Map<String, CountWrapper> addedPerLanguageCount, ImportOptionsModel importOptions) {
	TermImportWrapper wrapper = new TermImportWrapper();
	DescriptionImportWrapper descriptionWrapper = wrapper.getDescriptionWrapper();

	Map<String, Set<Term>> incomingMap = incomingTermEntry.getLanguageTerms();
	Map<String, Set<Term>> existingMap = existingTermEntry.getLanguageTerms();
	if (existingMap == null) {
	    existingMap = new HashMap<>();
	}
	Map<String, Set<Term>> copyExistingMap = new HashMap<>(existingMap);

	List<String> importLocales = new ArrayList<>(getImportedLocales());
	if (MapUtils.isNotEmpty(incomingMap)) {
	    for (Entry<String, Set<Term>> incomingEntry : incomingMap.entrySet()) {
		String languageId = incomingEntry.getKey();
		Set<Term> incomingTerms = incomingEntry.getValue();
		final int incomingCount = incomingTerms.size();
		CountWrapper perLanguageCount = initCountWrapper(addedPerLanguageCount, languageId);
		Set<Term> existingTerms = existingMap.get(languageId);
		if (existingTerms == null) {
		    existingTerms = new HashSet<>();
		}
		importLocales.remove(languageId);

		// Languages that have terms in submission should be skipped.
		if (isAtLeastOneTermInTranslation(existingTerms) || isAtLeastOneTermInTranslation(incomingTerms)) {
		    wrapper.getSkipedCount().add(incomingCount);
		    continue;
		}
		copyExistingMap.remove(languageId);
		Set<Description> incomingDescriptions = new HashSet<>();
		Set<Description> existingDescriptions = new HashSet<>();
		addIncomingTerms(wrapper, incomingTerms, existingTerms, perLanguageCount, incomingDescriptions);
		final int existingEnabledTermCount = addExistingTerms(wrapper, perLanguageCount, existingTerms,
			existingDescriptions);
		overwriteDescriptions(incomingDescriptions, existingDescriptions, descriptionWrapper,
			importOptions.getAllowedTermDescriptions());
		countAddedOrUpdated(incomingTerms, existingTerms, existingEnabledTermCount, incomingCount, wrapper,
			perLanguageCount);
		countDeletedTerms(wrapper, incomingTerms, perLanguageCount, existingEnabledTermCount, existingTerms,
			incomingCount);
	    }
	}
	reconcileTermEntry(wrapper, copyExistingMap, importLocales, addedPerLanguageCount);

	return wrapper;
    }

    private void reconcileTermEntry(TermImportWrapper wrapper, Map<String, Set<Term>> copyExistingMap,
	    List<String> importLocales, Map<String, CountWrapper> addedPerLanguageCount) {

	Set<Entry<String, Set<Term>>> entries = copyExistingMap.entrySet();

	for (Entry<String, Set<Term>> existingEntry : entries) {
	    String languageId = existingEntry.getKey();
	    Set<Term> existingTerms = existingEntry.getValue();
	    if (isAtLeastOneTermInTranslation(existingTerms)) {
		addExistingLanguages(wrapper, existingTerms);
		continue;
	    }
	    if (importLocales.contains(languageId)) {
		deleteExistingLanguage(wrapper, addedPerLanguageCount, existingTerms, languageId);
	    } else {
		addExistingLanguages(wrapper, existingTerms);
	    }
	}
    }

    private void resolveFirstTerm(Set<Term> existingTerms, Term existing) {
	if (existing.isFirst()) {
	    existing.setFirst(Boolean.FALSE);
	    Iterator<Term> iterator = existingTerms.iterator();
	    while (iterator.hasNext()) {
		Term synonym = iterator.next();
		if (!synonym.equals(existing) && !synonym.isDisabled()) {
		    synonym.setFirst(Boolean.TRUE);
		    break;
		}
	    }
	}
    }

    private void setImportedLocales(List<String> importedLocales) {
	_importedLocales = importedLocales;
    }

    @Override
    protected void initCache(int numElements, ImportOptionsModel importOptions) {
	_cache = importOptions.isOverwriteByTermEntryId() ? new HashMapMemorizer(numElements)
		: new ArrayDequeMemorizer(numElements, isIgnoreCase());
    }
}