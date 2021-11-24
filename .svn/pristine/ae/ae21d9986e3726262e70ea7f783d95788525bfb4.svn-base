package org.gs4tr.termmanager.service.impl;

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.persistence.update.AnalyzeImportInfo;
import org.gs4tr.termmanager.persistence.update.GetTermEntryCallback;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.persistence.update.ImportSummaryReportCallback;
import org.gs4tr.termmanager.service.ImportTermService;
import org.gs4tr.termmanager.service.loghandler.ImportTransactionLogHandler;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig.Builder;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderFactory;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.termentry.synchronization.TermEntrySynchronizerFactory;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.utils.StreamUtils;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.gs4tr.termmanager.service.xls.report.builder.ImportSummaryReportBuilder;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("importTermService")
public class ImportTermServiceImpl extends AbstractNotifyingService implements ImportTermService {

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Autowired
    private ImportTransactionLogHandler _logHandler;

    @Override
    public void cancelImportRequest(Set<String> threadNames) {
	if (CollectionUtils.isEmpty(threadNames)) {
	    return;
	}

	Consumer<String> consumer = getCancelImportProcessor();
	for (String thread : threadNames) {
	    consumer.accept(thread);
	}
    }

    @Override
    public ImportSummary importDocument(InputStream stream, ImportProgressInfo importProgressInfo,
	    ImportOptionsModel importOptions, ImportTypeEnum importType, String fileEncoding,
	    final SyncOption syncOption) throws Exception {

	LogHelper.debug(LOGGER, String.format(Messages.getString("ImportTermServiceImpl.3"), syncOption)); //$NON-NLS-1$

	TermEntryReaderConfig.Builder readerConfig = new TermEntryReaderConfig.Builder();
	readerConfig.userProjectLanguages(importOptions.getImportLocales());
	readerConfig.encoding(fileEncoding);
	readerConfig.importType(importType);
	readerConfig.stream(stream);

	final Long projectId = importOptions.getProjectId();

	Map<String, Set<String>> comboValuesPerAttribute = TermEntryUtils
		.getComboValuesPerAttribute(getProjectService(), projectId);

	readerConfig.comboValuesPerAttribute(comboValuesPerAttribute);

	TermEntryReader importReader = TermEntryReaderFactory.INSTANCE.createReader(readerConfig.build());

	ImportSummary summary = importProgressInfo.getImportSummary();

	GetTermEntryCallback getTermEntryCallback = getTermEntryCallback(importOptions, syncOption);

	ImportSummaryReportCallback reportCallback = getReportCallback(importOptions, summary);

	ITermEntrySynchronizer synchronizer = null;
	try {
	    boolean notEmptyGlossary = isGlossaryNotEmpty(importOptions.getSyncLanguageId(), projectId);
	    importOptions.setNotEmptyGlossary(notEmptyGlossary);

	    synchronizer = TermEntrySynchronizerFactory.INSTANCE.createTermEntrySynchronizer(syncOption);
	    synchronizer.initialize(getConnector(), importOptions);

	    getImporter().handleImport(importReader, importOptions, summary, getTermEntryCallback, reportCallback,
		    getTimeConsumer(summary), getPercentageConsumer(), getImportSummaryConsumer(), synchronizer,
		    getCancelRequestNotifier(summary.getImportId()), getLogHandler(), getRegularCollection());
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    if (nonNull(synchronizer)) {
		synchronizer.clearCache();
	    }
	}

	return summary;
    }

    @Override
    public ImportSummary importDocumentWS(ImportOptionsModel importOptions, InputStream stream, Long startTime,
	    ImportTypeEnum importType, SyncOption syncOption) throws Exception {

	File fileToImport = StreamUtils.createTempFile(stream);

	String filename = fileToImport.getName();

	String fileEncoding = ServiceUtils.getFileEncoding(fileToImport);

	InputStream tempInputStream = StreamUtils.openTempInputStream(fileToImport);

	InputStream forImportStream = StreamUtils.openTempInputStream(fileToImport);

	Set<String> languages = TmUserProfile.getCurrentUserProfile().getProjectUserLanguages()
		.get(importOptions.getProjectId());

	if (importType == null) {
	    importType = ImportTypeEnum.TBX;
	}

	validateImportingDocument(tempInputStream, new ArrayList<>(languages), importType, fileEncoding);

	ImportProgressInfo importProgressInfo = new ImportProgressInfo(0);
	ImportSummary importSummary = importProgressInfo.getImportSummary();
	importSummary.setImportId(filename);
	importSummary.setStartTime(System.currentTimeMillis());

	getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, filename, importProgressInfo);

	importDocument(forImportStream, importProgressInfo, importOptions, ImportTypeEnum.TBX, fileEncoding,
		syncOption);

	IOUtils.closeQuietly(forImportStream);
	fileToImport.delete();

	return importSummary;
    }

    @Override
    public boolean isLocked(long projectId) {
	return getLogHandler().isLocked(projectId);
    }

    @Override
    public void updateProjectDetailOnImport(ImportOptionsModel importOptions, ImportSummary importSummary) {
	getProjectDetailService().updateProjectDetailOnImport(importOptions.getProjectId(),
		importSummary.getLanguageDateModified(), importSummary.getDateModified());
    }

    @Override
    public AnalyzeImportInfo validateImportingDocument(InputStream inputStream, List<String> projectUserLanguages,
	    ImportTypeEnum importType, String fileEncoding) throws Exception {
	AnalyzeImportInfo analyzeImportInfo = new AnalyzeImportInfo();

	Builder builder = new TermEntryReaderConfig.Builder();
	builder.userProjectLanguages(projectUserLanguages);
	builder.encoding(fileEncoding);
	builder.importType(importType);
	builder.stream(inputStream);

	TermEntryReader reader = TermEntryReaderFactory.INSTANCE.createReader(builder.build());

	ImportValidationCallback validationCallback = new ImportValidationCallback(1);

	// TODO: Since 5.0, we change validation logic. Check if there are any
	// error alert and trow exception
	reader.validate(validationCallback);

	int totalTermEntryForImport = validationCallback.getTotalTerms();
	boolean notAllowedStatus = validationCallback.isNotAllowedStatusPresent();

	Set<String> termEntryDescriptions = validationCallback.getTermEntryDescriptions();
	Map<String, Integer> termEntryAttributesCount = validationCallback.getTermEntryAttributesCount();

	Set<String> termDescriptions = validationCallback.getTermDescriptions();
	Set<String> termNotes = validationCallback.getTermNotes();

	analyzeImportInfo.setIdColumnExist(validationCallback.isIdColumnExist());
	analyzeImportInfo.setNotAllowedStatusPresent(notAllowedStatus);
	analyzeImportInfo.setTermEntryAttributesCount(termEntryAttributesCount);
	analyzeImportInfo.setTermEntryAttributes(termEntryDescriptions);
	analyzeImportInfo.setTermDescription(termDescriptions);
	analyzeImportInfo.setTermNotes(termNotes);
	analyzeImportInfo.getNoTermEntryForImport().setValue(totalTermEntryForImport);

	analyzeImportInfo.setUnknownStatuses(validationCallback.getUnknownStatuses());

	analyzeImportInfo.setLanguages(TermEntryUtils.createLanguageMap(validationCallback.getLanguageMap()));

	analyzeImportInfo
		.setLanguagesAttributes(TermEntryUtils.createLanguageMap(validationCallback.getLanguageAttributeMap()));

	return analyzeImportInfo;
    }

    private TmgrSearchFilter createFilter(Long projectId, String syncLanguageId) {
	TmgrSearchFilter searchFilter = new TmgrSearchFilter();
	searchFilter.addProjectId(projectId);
	searchFilter.setSourceLanguage(syncLanguageId);
	return searchFilter;
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private Consumer<String> getCancelImportProcessor() {
	return (threadName) -> {
	    CacheGateway<String, ImportProgressInfo> cacheGateway = getCacheGateway();
	    ImportProgressInfo info = cacheGateway.get(CacheName.IMPORT_PROGRESS_STATUS, threadName);
	    info.setCanceled(true);
	};
    }

    private BooleanSupplier getCancelRequestNotifier(String threadName) {
	return () -> {
	    CacheGateway<String, ImportProgressInfo> cacheGateway = getCacheGateway();
	    ImportProgressInfo info = cacheGateway.get(CacheName.IMPORT_PROGRESS_STATUS, threadName);
	    return info.isCanceled();
	};
    }

    private BiConsumer<String, ImportSummary> getImportSummaryConsumer() {
	return (importUuid, importSummary) -> {
	    CacheGateway<String, ImportProgressInfo> cacheGateway = getCacheGateway();
	    ImportProgressInfo info = cacheGateway.get(CacheName.IMPORT_PROGRESS_STATUS, importUuid);
	    info.setImportSummary(importSummary);
	};
    }

    private ImportTransactionLogHandler getLogHandler() {
	return _logHandler;
    }

    private BiConsumer<String, Integer> getPercentageConsumer() {
	return (importUuid, percentage) -> {
	    CacheGateway<String, ImportProgressInfo> cacheGateway = getCacheGateway();
	    ImportProgressInfo info = cacheGateway.get(CacheName.IMPORT_PROGRESS_STATUS, importUuid);
	    info.setPercentage(percentage);
	};
    }

    private ImportSummaryReportCallback getReportCallback(ImportOptionsModel importOptions, ImportSummary summary) {
	return newEntry -> {
	    TermEntry fakeOldEntry = new TermEntry();
	    fakeOldEntry.setUuId(newEntry.getUuId());
	    fakeOldEntry.setLanguageTerms(new HashMap<>());

	    ImportSummaryReportBuilder builder = new ImportSummaryReportBuilder(fakeOldEntry, newEntry,
		    importOptions.getAllowedTermEntryAttributes(), importOptions.getSyncLanguageId(),
		    importOptions.getImportLocales(), summary.getReport(), importOptions.isIgnoreCase());
	    builder.appendReport();
	};
    }

    private GetTermEntryCallback getTermEntryCallback(ImportOptionsModel importOptions, final SyncOption syncOption) {
	return item -> TermEntryUtils.createTermEntry(item, importOptions, syncOption);

    }

    private Consumer<Long> getTimeConsumer(ImportSummary importSummary) {
	return endTime -> importSummary
		.setTotalTimeForImport(ServiceUtils.getTotalTimeFormatted(endTime, importSummary.getStartTime()));
    }

    private boolean isGlossaryNotEmpty(String syncLanguageId, Long projectId) throws TmException {
	TmgrSearchFilter filter = createFilter(projectId, syncLanguageId);
	ITmgrGlossaryBrowser browser = getConnector().getTmgrBrowser();
	return browser.getNumberOfTermEntriesOnProject(filter).longValue() > 0;
    }
}