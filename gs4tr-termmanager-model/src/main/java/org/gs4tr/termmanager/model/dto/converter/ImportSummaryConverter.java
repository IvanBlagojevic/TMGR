package org.gs4tr.termmanager.model.dto.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.mutable.MutableLong;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper;
import org.gs4tr.termmanager.model.LanguageComparator;
import org.gs4tr.termmanager.model.ProjectTerminologyCounts;
import org.gs4tr.termmanager.model.dto.BatchImportSummary;
import org.gs4tr.termmanager.model.dto.BatchImportSummary.Counts;
import org.gs4tr.termmanager.model.dto.BatchImportSummary.TermCounts;

public class ImportSummaryConverter {

    private static final String TIME_FORMAT = "%d min, %d sec"; // $NON-NLS-1$

    public static String formatTime(final long time) {
	long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
	long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
	return String.format(TIME_FORMAT, minutes, seconds - TimeUnit.MINUTES.toSeconds(minutes));
    }

    public static org.gs4tr.termmanager.model.dto.BatchImportSummary fromImportSummariesToBatch(
	    final Collection<ImportSummary> summaries) {
	final Counts termEntriesCounts = new Counts();
	/*
	 * Pre-import counts are the same for each summary, so take firt one
	 */
	ProjectTerminologyCounts terminologyPreImportCounts = summaries.iterator().next().getPreImportCounts();
	termEntriesCounts.addPreTotal(terminologyPreImportCounts.getNumberOfTermEntries());

	/* TERII-4949 Batch import - summary term count is incorrect */
	Map<String, Set<String>> updatedTermCount = new HashMap<>();
	Map<String, Set<String>> unchangedTermCount = new HashMap<>();

	Map<String, Long> numberOfTermsByLanguage = terminologyPreImportCounts.getNumberOfTermsByLanguage();

	final List<String> languageCodes = terminologyPreImportCounts.getLanguages();
	Map<String, TermCounts> termsCounts = new LinkedHashMap<>(languageCodes.size());
	for (final String languageCode : languageCodes) {
	    TermCounts termCounts = new TermCounts(languageCode);
	    termsCounts.put(languageCode, termCounts);

	    termCounts.setPreTotal(numberOfTermsByLanguage.get(languageCode));

	    updatedTermCount.computeIfAbsent(languageCode, k -> new HashSet<>());
	    unchangedTermCount.computeIfAbsent(languageCode, k -> new HashSet<>());
	}

	/* TERII-4946 - Import summary updated term entries count is incorrect */
	Set<String> updatedTermEntries = new HashSet<>();
	Set<String> unchangedTermEntries = new HashSet<>();

	final MutableLong totalTime = new MutableLong();
	for (final ImportSummary summary : summaries) {
	    totalTime.add(summary.getEndTime() - summary.getStartTime());

	    termEntriesCounts.addAdded(summary.getNoImportedTermEntries().longValue());
	    termEntriesCounts.addRemoved(summary.getNoDeletedTermEntries().longValue());

	    updatedTermEntries.addAll(summary.getUpdatedTermEntrieIds());
	    unchangedTermEntries.addAll(summary.getUnchangedTermEntrieIds());

	    Map<String, CountWrapper> addedPerLanguageCount = summary.getNoImportedTermsPerLanguage();

	    for (Entry<String, CountWrapper> entry : addedPerLanguageCount.entrySet()) {
		String language = entry.getKey();

		TermCounts termCounts = termsCounts.get(language);
		final CountWrapper wrapper = entry.getValue();
		termCounts.addRemoved(wrapper.getDeletedTerms().longValue());
		termCounts.addAdded(wrapper.getAddedTermCount().longValue());

		Set<String> updated = updatedTermCount.computeIfAbsent(language, k -> new HashSet<>());
		updated.addAll(wrapper.getUpdatedTermIds());

		Set<String> unchanged = unchangedTermCount.computeIfAbsent(language, k -> new HashSet<>());
		unchanged.addAll(wrapper.getUnchangedTermIds());
	    }
	}

	/* TERII-4949 Batch import - summary term count is incorrect */
	for (Entry<String, TermCounts> entry : termsCounts.entrySet()) {
	    String language = entry.getKey();

	    Set<String> updated = updatedTermCount.get(language);
	    Set<String> unchanged = unchangedTermCount.get(language);
	    unchanged.removeAll(updated);

	    TermCounts termCounts = termsCounts.get(language);

	    termCounts.addUnchanged(unchanged.size());
	    termCounts.addUpdated(updated.size());
	}

	/* TERII-4946 - Import summary updated term entries count is incorrect */
	unchangedTermEntries.removeAll(updatedTermEntries);

	termEntriesCounts.addUpdated(updatedTermEntries.size());
	termEntriesCounts.addUnchanged(unchangedTermEntries.size());

	BatchImportSummary result = new BatchImportSummary();
	result.setImportTime(formatTime(totalTime.longValue()));
	result.setTermEntries(termEntriesCounts);
	result.setTerms(termsCounts.values());
	return result;
    }

    public static org.gs4tr.termmanager.model.dto.ImportSummary fromInternalToDto(ImportSummary internalEntity) {

	org.gs4tr.termmanager.model.dto.ImportSummary dtoEntity = new org.gs4tr.termmanager.model.dto.ImportSummary();

	dtoEntity.setTotalTermEntryUpdated(internalEntity.getUpdatedTermEntryIds().size());
	dtoEntity.setTotalTermEntryDeleted(internalEntity.getNoDeletedTermEntries().intValue());
	dtoEntity.setTotalTermEntryDuplicated(internalEntity.getUnchangedTermEntrieIds().size());

	dtoEntity.setTotalTermsSkipped(internalEntity.getNoSkippedTerms().intValue());
	dtoEntity.setTotalTermsUpdated(internalEntity.getNoUpdatedTerms().intValue());
	dtoEntity.setTotalTermsDeleted(internalEntity.getNoDeletedTerms().intValue());
	dtoEntity.setTotalTermsDuplicated(internalEntity.getNoDuplicatedTerms().intValue());

	dtoEntity.setTotalImported(internalEntity.getNoImportedTermEntries().intValue());
	dtoEntity.setTotalSkipped(internalEntity.getNoSkippedTermEntries().intValue()
		+ internalEntity.getNoTermEntriesWithoutSynchLanguage().intValue());
	int errorsSize = internalEntity.getErrorMessages() != null ? internalEntity.getErrorMessages().size() : 0;

	dtoEntity.setTotalTermEntryErrors(errorsSize);
	dtoEntity.setTotalTermEntryForImport(internalEntity.getNoTermEntryForImport().intValue());
	dtoEntity.setTotalTermsImported(internalEntity.getNoImportedTerms().intValue());

	dtoEntity.setErrorMessages(ImportErrorMessageConverter.fromInternalToDto(internalEntity.getErrorMessages()));

	dtoEntity.setTotalTimeForImport(internalEntity.getTotalTimeForImport());

	dtoEntity.setTotalTermsAttributesImported(internalEntity.getNoImportedTermAttributes().intValue());

	dtoEntity.setTotalTermEntryAttributesImported(internalEntity.getNoImportedTermEntryAttributes().intValue());

	Set<String> imortedDescriptions = internalEntity.getImportedTermDescriptions();

	if (imortedDescriptions != null) {
	    String[] termDescriptions = imortedDescriptions.toArray(new String[imortedDescriptions.size()]);
	    dtoEntity.setImportedDescriptions(termDescriptions);
	}

	Set<String> importedTermEntryAttributes = internalEntity.getImportedTermEntryAttributes();

	if (importedTermEntryAttributes != null) {
	    int length = importedTermEntryAttributes.size();
	    String[] importedDescriptions = dtoEntity.getImportedDescriptions();
	    int descriptionsCount = ArrayUtils.isEmpty(importedDescriptions) ? 0 : imortedDescriptions.size();
	    dtoEntity
		    .setImportedAttributes(importedTermEntryAttributes.toArray(new String[length + descriptionsCount]));

	    if (ArrayUtils.isNotEmpty(importedDescriptions)) {
		String[] importedAttributes = dtoEntity.getImportedAttributes();
		for (int i = 0; i < importedDescriptions.length; i++) {
		    importedAttributes[length++] = importedDescriptions[i];
		}
	    }
	}

	dtoEntity.setTotalTermsDuplicated(internalEntity.getNoDuplicatedTerms().intValue());

	org.gs4tr.termmanager.model.dto.Language[] targetLanguages = LanguageConverter
		.fromInternalToDto(internalEntity.getImportedTargetLanguages());

	Arrays.sort(targetLanguages, new LanguageComparator());

	dtoEntity.setImportedTargetLanguages(targetLanguages);

	return dtoEntity;
    }

    private ImportSummaryConverter() {
    }
}