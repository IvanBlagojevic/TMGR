package org.gs4tr.termmanager.service.persistence.importer;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.persistence.update.GetTermEntryCallback;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.persistence.update.ImportSummaryReportCallback;
import org.gs4tr.termmanager.service.loghandler.ImportTransactionLogHandler;
import org.gs4tr.tm3.api.TmException;

public interface ITmgrGlossaryImporter {

    void handleImport(TermEntryReader reader, ImportOptionsModel importModel, ImportSummary importSummary,
	    GetTermEntryCallback getTermEntryCallback, ImportSummaryReportCallback reportCallback,
	    Consumer<Long> timeConsumer, BiConsumer<String, Integer> percentageConsumer,
	    BiConsumer<String, ImportSummary> importSummaryConsumer, ITermEntrySynchronizer synchronizer,
	    BooleanSupplier cancelImportNotifier, ImportTransactionLogHandler logHandler, String Collection)
	    throws TmException;
}