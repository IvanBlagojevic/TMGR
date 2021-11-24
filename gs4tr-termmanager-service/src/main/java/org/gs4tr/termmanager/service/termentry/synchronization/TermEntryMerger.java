package org.gs4tr.termmanager.service.termentry.synchronization;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.TermHelper;
import org.gs4tr.termmanager.service.termentry.memorizer.ArrayDequeMemorizer;
import org.gs4tr.termmanager.service.termentry.memorizer.Memorizable;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.gs4tr.termmanager.service.xls.report.builder.ImportSummaryReportBuilder;
import org.gs4tr.tm3.api.TmException;

public class TermEntryMerger extends AbstractTermEntrySynchronizer {

    public TermEntryMerger() {
	super();
    }

    @Override
    public TermEntry synchronizeTermEntries(TermEntry incoming, TermEntry existing, ImportOptionsModel importModel,
	    ImportSummary summary) throws TmException {
	if (existing == null) {
	    existing = searchMatch(incoming, importModel, summary);
	}

	EqualsBuilderHelper equalsBuilder = getEqualsBuilder();
	equalsBuilder.setIgnoreCase(isIgnoreCase());
	equalsBuilder.setIncludeStatus(false);

	if (equalsBuilder.isEquals(incoming, existing)) {
	    countUnchanged(existing, summary);
	    TermHelper.setTermEntryDateModified(existing, summary);
	    return existing;
	}

	if (Objects.nonNull(existing)) {
	    TermEntry existingClone = new TermEntry(existing);

	    String syncLanguageId = importModel.getSyncLanguageId();

	    ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(existingClone, existing,
		    importModel.getAllowedTermEntryAttributes(), syncLanguageId, importModel.getImportLocales(),
		    summary.getReport(), isIgnoreCase());

	    DescriptionImportOption descriptionImportOption = importModel.getDescriptionImportOption();

	    Set<Description> newEntryDescriptions = TermEntryUtils.getNullSafeDescriptions(incoming);
	    Set<Description> existingEntryDescriptions = TermEntryUtils.getNullSafeDescriptions(existing);

	    boolean termEntryDescriptionAdded = false;

	    long date = new Date().getTime();

	    for (Description newDescription : newEntryDescriptions) {
		if (!equalsBuilder.containsDescription(newDescription, existingEntryDescriptions)) {
		    String newType = newDescription.getType();
		    if (TermHelper.isTermEntryAttributeAllowed(descriptionImportOption, newType,
			    importModel.getAllowedTermEntryAttributes())) {
			// Issue TERII-4703
			newType = TermHelper.resolveDescriptionType(newType,
				importModel.getAllowedTermEntryAttributes(), isIgnoreCase());
			newDescription.setType(newType);

			existing.addDescription(newDescription);
			existing.setDateModified(date);
			termEntryDescriptionAdded = true;

			summary.getNoImportedTermEntryAttributes().increment();
			summary.addImportedTermEntryAttribute(newType);
		    }
		}
	    }

	    mergeTerms(existing, incoming, summary, descriptionImportOption, importModel.getAllowedTermDescriptions());

	    long oldDateModified = existing.getDateModified().longValue();

	    /* Set termEntry dateModified based on newest term dateModified */
	    TermHelper.setTermEntryDateModified(existing, summary);

	    long newDateModified = existing.getDateModified().longValue();
	    if (termEntryDescriptionAdded || (oldDateModified != newDateModified)) {
		// [21-April-2017, Since 5.0]
		summary.getUpdatedTermEntrieIds().add(existing.getUuId());
	    }

	    if (StringUtils.isNotEmpty(syncLanguageId)) {
		appendReport(builder);
	    }

	    Memorizable<TermEntry, String> cache = getCache();
	    if (cache != null) {
		cache.store(existing);
	    }
	}

	return existing;
    }

    private boolean areTermsEqual(Term existingTerm, Term incomingTerm) {
	return getEqualsBuilder().isEqualsIgnoreCase(existingTerm, incomingTerm);
    }

    private boolean isBlacklisted(Term existing, Term incoming) {

	boolean isExistingBlacklisted = existing.getStatus().equals(ItemStatusTypeHolder.BLACKLISTED.getName());
	boolean isIncomingBlacklisted = incoming.getStatus().equals(ItemStatusTypeHolder.BLACKLISTED.getName());

	return isExistingBlacklisted || isIncomingBlacklisted;
    }

    private void mergeDescriptions(ImportSummary summary, DescriptionImportOption descriptionImportOption,
	    Map<String, Set<String>> allowedTermDescriptions, Term newTerm, Term existingTerm) {

	Set<Description> newDescriptions = TermEntryUtils.getNullSafeDescriptions(newTerm);
	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	long date = new Date().getTime();
	for (Description newDescription : newDescriptions) {
	    Set<Description> existingDescriptions = TermEntryUtils.getNullSafeDescriptions(existingTerm);
	    if (!getEqualsBuilder().containsDescription(newDescription, existingDescriptions)) {
		if (TermHelper.isTermDescriptionAllowed(descriptionImportOption, newDescription,
			allowedTermDescriptions, isIgnoreCase())) {
		    // Issue TERII-4703
		    String newType = TermHelper.resolveDescriptionType(newDescription.getType(),
			    allowedTermDescriptions.get(newDescription.getBaseType()), isIgnoreCase());
		    newDescription.setType(newType);

		    existingTerm.addDescription(newDescription);
		    existingTerm.setDateModified(date);
		    existingTerm.setUserModified(TmUserProfile.getCurrentUserName());

		    CountWrapper termCounts = termCountsPerLanguage.computeIfAbsent(existingTerm.getLanguageId(),
			    key -> new CountWrapper());

		    termCounts.getUpdatedTermIds().add(existingTerm.getUuId());

		    summary.addImportedTermDescription(newDescription.getType());
		    summary.getNoImportedTermAttributes().increment();
		}
	    }
	}
    }

    private void mergeTerms(TermEntry existingEntry, TermEntry newEntry, ImportSummary importSummary,
	    DescriptionImportOption descriptionImportOption, Map<String, Set<String>> allowedTermDescriptions) {

	List<Term> newTerms = TermEntryUtils.getNullSafeTerns(newEntry);
	List<Term> existingTerms = TermEntryUtils.getNullSafeTerns(existingEntry);

	Iterator<Term> newTermsIterator = newTerms.iterator();

	while (newTermsIterator.hasNext()) {
	    Term newTerm = newTermsIterator.next();

	    Optional<Term> optionalExistingTerm = existingTerms.stream().filter(
		    existingTerm -> (!isBlacklisted(existingTerm, newTerm)) && areTermsEqual(existingTerm, newTerm))
		    .findFirst();

	    if (optionalExistingTerm.isPresent()) {
		importSummary.getNoDuplicatedTerms().increment();
		mergeDescriptions(importSummary, descriptionImportOption, allowedTermDescriptions, newTerm,
			optionalExistingTerm.get());
		newTermsIterator.remove();
	    }
	}

	if (CollectionUtils.isNotEmpty(newTerms)) {
	    long dateTime = new Date().getTime();
	    String user = TmUserProfile.getCurrentUserName();
	    for (Term newTerm : newTerms) {
		// setting new uuid in case it is UI merge is performed
		newTerm.setUuId(UUID.randomUUID().toString());
		newTerm.setDateCreated(dateTime);
		newTerm.setDateModified(dateTime);
		newTerm.setUserCreated(user);
		newTerm.setUserModified(user);

		TermHelper.handleNewTerm(importSummary, descriptionImportOption, allowedTermDescriptions, newTerm,
			isIgnoreCase());
		existingEntry.addTerm(newTerm);
	    }
	}
    }

    @Override
    protected void initCache(int numElements, ImportOptionsModel importOptions) {
	_cache = new ArrayDequeMemorizer(numElements, isIgnoreCase());
    }
}
