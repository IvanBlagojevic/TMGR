package org.gs4tr.termmanager.service.persistence.importer.impl;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.gs4tr.foundation.modules.entities.model.UserProfile;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation3.callback.ImportCallback;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.persistence.solr.importer.ImportErrorHelper;
import org.gs4tr.termmanager.persistence.solr.util.SolrGlossaryAdapter;
import org.gs4tr.termmanager.persistence.update.GetTermEntryCallback;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.persistence.update.ImportSummaryReportCallback;
import org.gs4tr.termmanager.service.loghandler.ImportTransactionLogHandler;
import org.gs4tr.termmanager.service.persistence.importer.ITmgrGlossaryImporter;
import org.gs4tr.termmanager.service.persistence.importer.ImportHandler;
import org.gs4tr.termmanager.service.persistence.importer.termentry.GenericBuffer;
import org.gs4tr.termmanager.service.persistence.importer.termentry.TermEntryBatchUpdater;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.tm3.api.TmException;

import jetbrains.exodus.entitystore.EntityId;
import net.padlocksoftware.license.ImportException;

public class TmgrGlossaryImporter implements ITmgrGlossaryImporter {

    private static final String ACTION_TYPE = "import";

    private SolrGlossaryAdapter _adapter;

    public TmgrGlossaryImporter(TmgrSolrConnector connector) {
	_adapter = connector.getAdapter();
    }

    @Override
    public void handleImport(TermEntryReader reader, ImportOptionsModel model, ImportSummary summary,
	    GetTermEntryCallback termEntryCallback, ImportSummaryReportCallback reportCallback,
	    Consumer<Long> timeConsumer, BiConsumer<String, Integer> percentageConsumer,
	    BiConsumer<String, ImportSummary> summaryConsumer, ITermEntrySynchronizer synchronizer,
	    BooleanSupplier cancelImportNotifier, ImportTransactionLogHandler logHandler, String collection)
	    throws TmException {

	UserProfile userProfile = UserProfileContext.getCurrentUserProfile();

	String userName = userProfile.getUserInfo().getUserName();

	long projectId = model.getProjectId();

	Optional<EntityId> optionalEntity = logHandler.startAppending(projectId, userName, ACTION_TYPE, collection);

	EntityId entityId;

	if (optionalEntity.isPresent()) {
	    entityId = optionalEntity.get();
	} else {
	    throw new ImportException(String.format("Invalid EntityId for projectId: %d", projectId));
	}

	GenericBuffer<TermEntry> batchUpdater = new TermEntryBatchUpdater(getAdapter().getImportBatchSize(),
		getExceptionHandler(summary), logHandler, projectId, entityId);

	ImportCallback handler = new ImportHandler(batchUpdater, model, percentageConsumer, summary, summaryConsumer,
		synchronizer, termEntryCallback, reportCallback, timeConsumer, cancelImportNotifier);

	reader.readTermEntries(handler);

	summary.getImportedTargetLanguages().remove(model.getSyncLanguageId());

	try {
	    logHandler.finishAppendingWithCallback(projectId, entityId, handler);
	} catch (Exception e) {
	    getExceptionHandler(summary).accept(e);
	}
    }

    private SolrGlossaryAdapter getAdapter() {
	return _adapter;
    }

    private Consumer<Throwable> getExceptionHandler(ImportSummary importSummary) {
	return error -> ImportErrorHelper.handleErrorMessage(importSummary, error.getMessage());
    }
}
