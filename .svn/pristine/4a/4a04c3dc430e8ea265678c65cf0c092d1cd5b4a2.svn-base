package org.gs4tr.termmanager.service.persistence.importer;

import static java.util.Objects.nonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.Messages;
import org.gs4tr.termmanager.persistence.solr.importer.ImportErrorHelper;
import org.gs4tr.termmanager.persistence.update.DescriptionImportOption;
import org.gs4tr.termmanager.persistence.update.GetTermEntryCallback;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.persistence.update.ImportSummaryReportCallback;
import org.gs4tr.termmanager.service.persistence.importer.termentry.GenericBuffer;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.TermHelper;
import org.gs4tr.tm3.api.TmException;

public class ImportHandler implements ImportCallback {

    private static final Log LOGGER = LogFactory.getLog(ImportHandler.class);

    private GenericBuffer<TermEntry> _batchUpdater;

    private BooleanSupplier _cancelImportNotifier;

    private ImportOptionsModel _model;

    private BiConsumer<String, Integer> _percentageConsumer;

    private ImportSummaryReportCallback _reportCallback;

    private ImportSummary _summary;

    private BiConsumer<String, ImportSummary> _summaryConsumer;

    private ITermEntrySynchronizer _synchronizer;

    private GetTermEntryCallback _termEntryCallback;

    private Consumer<Long> _timeConsumer;

    public ImportHandler(GenericBuffer<TermEntry> batchUpdater, ImportOptionsModel model,
	    BiConsumer<String, Integer> percentageConsumer, ImportSummary summary,
	    BiConsumer<String, ImportSummary> summaryConsumer, ITermEntrySynchronizer synchronizer,
	    GetTermEntryCallback termEntryCallback, ImportSummaryReportCallback reportCallback,
	    Consumer<Long> timeConsumer, BooleanSupplier cancelImportNotifier) {
	_batchUpdater = batchUpdater;
	_model = model;
	_percentageConsumer = percentageConsumer;
	_summary = summary;
	_summaryConsumer = summaryConsumer;
	_synchronizer = synchronizer;
	_termEntryCallback = termEntryCallback;
	_reportCallback = reportCallback;
	_timeConsumer = timeConsumer;
	_cancelImportNotifier = cancelImportNotifier;
    }

    @Override
    public int getTotalTerms() {
	return getSummary().getNoTermEntryForImport().intValue();
    }

    @Override
    public void handlePercentage(int percentage) {
	ImportSummary summary = getSummary();
	String importId = summary.getImportId();
	if (percentage == 50) {
	    getBatchUpdater().clear();
	}
	if (percentage == 100) {
	    getBatchUpdater().clear();
	    long endTime = System.currentTimeMillis();
	    summary.setEndTime(endTime);
	    getTimeConsumer().accept(endTime);
	    if (nonNull(importId)) {
		getSummaryConsumer().accept(importId, summary);
	    }
	}

	BiConsumer<String, Integer> percentageConsumer = getPercentageConsumer();
	if (nonNull(importId) && nonNull(percentageConsumer)) {
	    percentageConsumer.accept(importId, percentage);
	}
    }

    @Override
    public void handleTermEntry(Object item) {
	ImportSummary summary = getSummary();

	TermEntry incomingTermEntry = getTermEntryCallback().getTermEntryInstace(item);

	ITermEntrySynchronizer synchronizer = getSynchronizer();

	GenericBuffer<TermEntry> batchUpdater = getBatchUpdater();

	ImportOptionsModel model = getModel();

	try {
	    TermEntry existingTermEntry = null;
	    if (synchronizer != null) {
		existingTermEntry = synchronizer.synchronizeTermEntries(incomingTermEntry, null, model, summary);
	    }

	    // This happens in case where termEntry does not contain
	    // sync language and SKIP option is selected.
	    if (incomingTermEntry.getUuId() == null && incomingTermEntry.getLanguageTerms() == null
		    && incomingTermEntry.getDescriptions() == null) {
		return;
	    }

	    if (existingTermEntry != null) {
		existingTermEntry.setAction(Action.IMPORTED);
		batchUpdater.add(existingTermEntry);
	    } else {
		incomingTermEntry.setAction(Action.IMPORTED);
		importNewTermEntry(incomingTermEntry, summary, model);
		batchUpdater.add(incomingTermEntry);
		summary.getNoImportedTermEntries().increment();
		if (Objects.nonNull(getReportCallback())) {
		    getReportCallback().handleNewTermEntry(incomingTermEntry);
		}
	    }
	} catch (TmException e) {
	    summary.getNoSkippedTermEntries().increment();
	    ImportErrorHelper.handleErrorMessage(summary, e.getMessage());
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(Messages.getString("ImportHandler.0")); //$NON-NLS-1$
	    }
	    LOGGER.error(e.getMessage(), e);
	}
    }

    @Override
    public boolean isImportCanceled() {
	BooleanSupplier supplier = getCancelImportNotifier();
	return supplier != null && supplier.getAsBoolean();
    }

    private GenericBuffer<TermEntry> getBatchUpdater() {
	return _batchUpdater;
    }

    private BooleanSupplier getCancelImportNotifier() {
	return _cancelImportNotifier;
    }

    private ImportOptionsModel getModel() {
	return _model;
    }

    private BiConsumer<String, Integer> getPercentageConsumer() {
	return _percentageConsumer;
    }

    private ImportSummaryReportCallback getReportCallback() {
	return _reportCallback;
    }

    private ImportSummary getSummary() {
	return _summary;
    }

    private BiConsumer<String, ImportSummary> getSummaryConsumer() {
	return _summaryConsumer;
    }

    private ITermEntrySynchronizer getSynchronizer() {
	return _synchronizer;
    }

    private GetTermEntryCallback getTermEntryCallback() {
	return _termEntryCallback;
    }

    private Consumer<Long> getTimeConsumer() {
	return _timeConsumer;
    }

    private void handleNewTerms(TermEntry termEntry, ImportSummary info,
	    DescriptionImportOption descriptionImportOption, Map<String, Set<String>> allowedTermDescriptions) {
	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	List<Term> terms = termEntry.ggetTerms();

	if (CollectionUtils.isNotEmpty(terms)) {
	    for (Term term : terms) {
		if (!isAllowedStatus(term.getStatus())) {
		    removeTermsInSubmission(languageTerms, term);
		    // and increment the number of skips term
		    info.getNoSkippedTerms().increment();
		    continue;
		}

		TermHelper.handleNewTerm(info, descriptionImportOption, allowedTermDescriptions, term,
			getModel().isIgnoreCase());
	    }
	}
    }

    /*
     * Term entries with ID that doesn't exist in TMGR will be imported as new
     * entries and will be assigned new IDs.
     */
    private void importNewTermEntry(TermEntry termEntry, ImportSummary summary, ImportOptionsModel importOptions) {
	DescriptionImportOption descriptionImportOption = importOptions.getDescriptionImportOption();

	Set<Description> termEntryDescriptions = termEntry.getDescriptions();
	Set<Description> nonAllowedDescriptions = new HashSet<>();
	if (CollectionUtils.isNotEmpty(termEntryDescriptions)) {
	    for (Description description : termEntryDescriptions) {
		String type = description.getType();
		if (TermHelper.isTermEntryAttributeAllowed(descriptionImportOption, type,
			importOptions.getAllowedTermEntryAttributes())) {
		    summary.addImportedTermEntryAttribute(type);
		    summary.getNoImportedTermEntryAttributes().increment();
		} else {
		    nonAllowedDescriptions.add(description);
		}
	    }
	}

	if (!nonAllowedDescriptions.isEmpty()) {
	    termEntryDescriptions.removeAll(nonAllowedDescriptions);
	    nonAllowedDescriptions.clear();
	    termEntry.setDescriptions(termEntryDescriptions);
	}

	handleNewTerms(termEntry, summary, descriptionImportOption, importOptions.getAllowedTermDescriptions());
	String termEntryId = UUID.randomUUID().toString();
	summary.addImportedTermEntryId(termEntryId);
	termEntry.setUuId(termEntryId);

	TermHelper.setTermEntryDateModified(termEntry, summary);
    }

    private boolean isAllowedStatus(String status) {
	return TermHelper.ALLOWED_STATUSES.contains(status);
    }

    private void removeTermsInSubmission(Map<String, Set<Term>> languageTerms, Term term) {
	String languageId = term.getLanguageId();
	Set<Term> langSet = languageTerms.get(languageId);
	langSet.remove(term); // Remove terms in submission
	if (CollectionUtils.isEmpty(langSet)) {
	    languageTerms.remove(languageId);
	}
    }

}
