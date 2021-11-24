package org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.termmanager.model.ImportErrorAction;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.tm3.api.TmException;

public class TermHelper {

    public static final Set<String> ALLOWED_STATUSES = initializeStatuses();

    public static void decrementStatusCount(String termStatus, CountWrapper countWrapper) {
	if (ItemStatusTypeHolder.PROCESSED.getName().equals(termStatus)) {
	    countWrapper.getAddedApprovedCount().decrement();
	} else if (ItemStatusTypeHolder.WAITING.getName().equals(termStatus)) {
	    countWrapper.getAddedPendingCount().decrement();
	} else if (ItemStatusTypeHolder.BLACKLISTED.getName().equals(termStatus)) {
	    countWrapper.getAddedBlacklistedCount().decrement();
	} else if (ItemStatusTypeHolder.ON_HOLD.getName().equals(termStatus)) {
	    countWrapper.getAddedOnHoldCount().decrement();
	}
    }

    public static void handleNewTerm(ImportSummary importSummary, DescriptionImportOption descriptionImportOption,
	    Map<String, Set<String>> allowedTermDescriptions, Term term, boolean ignoreCase) {

	String languageId = term.getLanguageId();
	String termStatus = term.getStatus();

	importSummary.addImportedTargetLanguage(languageId);
	importSummary.getNoImportedTerms().increment();

	Map<String, CountWrapper> addedPerLanguageCount = importSummary.getNoImportedTermsPerLanguage();

	CountWrapper countWrapper = addedPerLanguageCount.computeIfAbsent(languageId, k -> new CountWrapper());
	countWrapper.getTermCount().increment();
	countWrapper.getAddedTermCount().increment();

	incrementStatusCount(termStatus, countWrapper);
	incrementStatisticsStatusCount(termStatus, countWrapper);

	Set<Description> nonAllowedDescriptions = new HashSet<>();
	Set<Description> termDescriptions = term.getDescriptions();
	if (CollectionUtils.isNotEmpty(termDescriptions)) {
	    for (Description description : termDescriptions) {
		if (isTermDescriptionAllowed(descriptionImportOption, description, allowedTermDescriptions,
			ignoreCase)) {
		    // Issue TERII-4703
		    String descType = resolveDescriptionType(description.getType(),
			    allowedTermDescriptions.get(description.getBaseType()), ignoreCase);
		    description.setType(descType);
		    importSummary.addImportedTermDescription(descType);
		    importSummary.getNoImportedTermAttributes().increment();
		} else {
		    nonAllowedDescriptions.add(description);
		}
	    }
	}

	// TODO: added desc
	if (!nonAllowedDescriptions.isEmpty()) {
	    termDescriptions.removeAll(nonAllowedDescriptions);
	    nonAllowedDescriptions.clear();
	    term.setDescriptions(termDescriptions);
	}
    }

    // 28-April-2017, [Bug#TERII-4449]:
    public static void incrementStatisticsStatusCount(String status, CountWrapper counts) {
	if (ItemStatusTypeHolder.PROCESSED.getName().equals(status)) {
	    counts.getAddedApprovedStatisticsCount().increment();
	} else if (ItemStatusTypeHolder.WAITING.getName().equals(status)) {
	    counts.getAddedPendingStatisticsCount().increment();
	} else if (ItemStatusTypeHolder.ON_HOLD.getName().equals(status)) {
	    counts.getAddedOnHoldStatisticsCount().increment();
	} else if (ItemStatusTypeHolder.BLACKLISTED.getName().equals(status)) {
	    counts.getAddedBlacklistedStatisticsCount().increment();
	}
    }

    public static void incrementStatusCount(String termStatus, CountWrapper countWrapper) {
	if (ItemStatusTypeHolder.PROCESSED.getName().equals(termStatus)) {
	    countWrapper.getAddedApprovedCount().increment();
	} else if (ItemStatusTypeHolder.WAITING.getName().equals(termStatus)) {
	    countWrapper.getAddedPendingCount().increment();
	} else if (ItemStatusTypeHolder.BLACKLISTED.getName().equals(termStatus)) {
	    countWrapper.getAddedBlacklistedCount().increment();
	} else if (ItemStatusTypeHolder.ON_HOLD.getName().equals(termStatus)) {
	    countWrapper.getAddedOnHoldCount().increment();
	}
    }

    public static boolean isTermDescriptionAllowed(final DescriptionImportOption option, final Description tDescription,
	    final Map<String, Set<String>> allowedTermDescriptionTypes, boolean ignoreCase) {
	boolean allowed = false;
	if (DescriptionImportOption.IMPORT_ONLY_SELECTED == option) {
	    if (MapUtils.isNotEmpty(allowedTermDescriptionTypes)) {
		Set<String> termDescriptions = allowedTermDescriptionTypes.getOrDefault(tDescription.getBaseType(),
			Collections.emptySet());
		if (contains(tDescription.getType(), termDescriptions, ignoreCase)) {
		    allowed = true;
		}
	    }
	} else if (DescriptionImportOption.ADD_ALL == option) {
	    allowed = true;
	}
	return allowed;
    }

    public static boolean isTermEntryAttributeAllowed(final DescriptionImportOption option,
	    final String termEntryAttribute, final Set<String> allowedTermEntryAttributes) {
	boolean allowed = false;
	if (DescriptionImportOption.IMPORT_ONLY_SELECTED == option) {
	    if (CollectionUtils.isNotEmpty(allowedTermEntryAttributes)
		    && allowedTermEntryAttributes.contains(termEntryAttribute)) {
		allowed = true;
	    }
	} else if (DescriptionImportOption.ADD_ALL == option) {
	    allowed = true;
	}
	return allowed;
    }

    public static String resolveDescriptionType(String incomingType, Collection<String> allowedTypes,
	    boolean ignoreCase) {
	if (CollectionUtils.isEmpty(allowedTypes)) {
	    return incomingType;
	}

	return ignoreCase
		? allowedTypes.stream().filter(t -> t.equalsIgnoreCase(incomingType)).findFirst().orElse(incomingType)
		: incomingType;
    }

    public static void setTermEntryDateModified(TermEntry entry, ImportSummary summary) {
	long termEntryDate = entry.getDateModified();
	Map<String, Long> languageDateModified = summary.getLanguageDateModified();

	List<Term> terms = entry.ggetAllTerms();
	if (Objects.nonNull(terms)) {
	    for (Term term : terms) {
		String languageId = term.getLanguageId();

		long termDate = term.getDateModified();
		termEntryDate = Long.max(termEntryDate, termDate);

		languageDateModified.putIfAbsent(languageId, termDate);

		Long languageDate = languageDateModified.get(languageId);
		languageDateModified.put(languageId, Long.max(languageDate, termDate));
	    }
	}
	summary.setDateModified(termEntryDate);
	entry.setDateModified(termEntryDate);
    }

    public static TermEntry skipOrImportWithoutSync(TermEntry termEntry, ImportOptionsModel importModel,
	    ImportSummary importSummary) throws TmException {

	String syncLanguageId = importModel.getSyncLanguageId();
	if (StringUtils.isBlank(syncLanguageId)) {
	    return null;
	}
	ImportErrorAction action = importModel.getImportErrorAction();
	if (action == ImportErrorAction.SKIP) {
	    incrementSkipped(importSummary);
	    return resetTermEntry(termEntry);
	} else if (action == ImportErrorAction.IMPORT_TRANSLATIONS) {
	    if (MapUtils.isEmpty(termEntry.getLanguageTerms())) {
		incrementSkipped(importSummary);
		return resetTermEntry(termEntry);
	    }
	    return null;
	}
	throw new TmException(Messages.getString("TermHelper.0")); //$NON-NLS-1$
    }

    private static boolean contains(String incomingType, Set<String> allowedTypes, boolean ignoreCase) {
	for (String allowedType : allowedTypes) {
	    if (ignoreCase ? allowedType.equalsIgnoreCase(incomingType) : allowedType.equals(incomingType)) {
		return true;
	    }
	}

	return false;
    }

    private static void incrementSkipped(ImportSummary importSummary) {
	MutableInt skipped = importSummary.getNoSkippedTermEntries();
	skipped.increment();
    }

    private static Set<String> initializeStatuses() {
	Set<String> allowedStatuses = new HashSet<>();
	allowedStatuses.add(ItemStatusTypeHolder.PROCESSED.getName());
	allowedStatuses.add(ItemStatusTypeHolder.WAITING.getName());
	allowedStatuses.add(ItemStatusTypeHolder.BLACKLISTED.getName());
	allowedStatuses.add(ItemStatusTypeHolder.ON_HOLD.getName());
	return allowedStatuses;
    }

    private static TermEntry resetTermEntry(TermEntry termEntry) {
	termEntry.setUuId(null);
	termEntry.setLanguageTerms(null);
	termEntry.setDescriptions(null);
	return termEntry;
    }
}
