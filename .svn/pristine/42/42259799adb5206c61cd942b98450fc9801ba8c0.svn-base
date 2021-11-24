package org.gs4tr.termmanager.service.file.analysis;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.gs4tr.termmanager.service.utils.ServiceUtils.getFileEncoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.locale.LocaleException;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ImportFileExtension;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.service.file.analysis.model.Alert;
import org.gs4tr.termmanager.service.file.analysis.model.AlertSubject;
import org.gs4tr.termmanager.service.file.analysis.model.AlertType;
import org.gs4tr.termmanager.service.file.analysis.model.FileAnalysisReport;
import org.gs4tr.termmanager.service.file.analysis.request.Context;
import org.gs4tr.termmanager.service.file.analysis.request.FilesAnalysisRequest;
import org.gs4tr.termmanager.service.file.analysis.response.FilesAnalysisResponse;
import org.gs4tr.termmanager.service.file.manager.FileManager;
import org.gs4tr.termmanager.service.impl.ImportValidationCallback;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderConfig.Builder;
import org.gs4tr.termmanager.service.termentry.reader.factory.TermEntryReaderFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * <p>
 * Concrete <tt>FilesAnalysisRequestHandler</tt> implementation that provides a
 * breakdown of the analyzeAsync process of provided files.
 * </p>
 *
 * @since 5.0
 */
@Component
class FilesAnalysisRequestHandlerImpl implements FilesAnalysisRequestHandler, InitializingBean, DisposableBean {

    private static final CacheName CACHE_NAME = CacheName.ANALYSIS_PROGRESS_STATUS;

    private static final Log LOG = LogFactory.getLog(FilesAnalysisRequestHandlerImpl.class);

    /**
     * The maximum number of processors available to the virtual machine; never
     * smaller than one.
     */
    private static final int NCPUS = Runtime.getRuntime().availableProcessors();

    /**
     * The naming format to use when naming worker threads (
     * {@link Thread#setName}).
     */
    private static final String THREAD_NAME_FORMAT = "FilesAnalysisThreadPool-worker-%d"; //$NON-NLS-1$

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, FilesAnalysisResponse> _cacheGateway;

    private ExecutorService _executorService;

    @Autowired
    private FileManager _manager;

    @Autowired
    @Qualifier("sourceLanguagePostProcessorChain")
    private PostProcessorChain _postProcessorChain;

    @Autowired
    private ImportValidationCallbackTransformer _validationCallbackTransformer;

    @Override
    public void afterPropertiesSet() throws Exception {
	ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
	threadFactoryBuilder.setNameFormat(THREAD_NAME_FORMAT);
	threadFactoryBuilder.setPriority(Thread.NORM_PRIORITY);
	threadFactoryBuilder.setDaemon(true);

	setExecutorService(Executors.newFixedThreadPool(NCPUS, threadFactoryBuilder.build()));

	LogHelper.info(LOG, Messages.getString("FilesAnalysisRequestHandlerImpl.7"), getExecutorService().toString());
    }

    @Override
    public void destroy() throws Exception {
	LogHelper.info(LOG, String.format(Messages.getString("FilesAnalysisRequestHandlerImpl.2"), getClass())); //$NON-NLS-1$
	ExecutorService executorService = getExecutorService();
	if (Objects.nonNull(executorService)) {
	    LogHelper.info(LOG,
		    String.format(Messages.getString("FilesAnalysisRequestHandlerImpl.3"), executorService.toString())); //$NON-NLS-1$
	    executorService.shutdownNow();
	}
    }

    @Override
    public Future<FilesAnalysisResponse> handleAsync(FilesAnalysisRequest request) {
	Objects.requireNonNull(request, Messages.getString("FilesAnalysisRequestHandlerImpl.0")); //$NON-NLS-1$
	getCacheGateway().put(CACHE_NAME, request.getProcessingId(), new FilesAnalysisResponse());
	return getExecutorService().submit(() -> analyzeAsync(request));
    }

    private FileAnalysisReport analyze(File file, Context context) {

	final String name = file.getName(), extension = getExtension(name);

	LogHelper.debug(LOG, String.format(Messages.getString("FilesAnalysisRequestHandlerImpl.6"), name)); //$NON-NLS-1$

	FileAnalysisReport result;
	if (!ImportFileExtension.supported(extension)) {
	    String message = String.format(MessageResolver.getMessage("FilesAnalysisRequestHandlerImpl.0"), extension); //$NON-NLS-1$
	    result = new FileAnalysisReport(name);
	    result.getFileAnalysisAlerts().getAlerts()
		    .add(new Alert(AlertSubject.INVALID_FILE_FORMAT, AlertType.ERROR, message));
	    return result;
	}

	try (InputStream stream = new FileInputStream(file)) {
	    Builder builder = new TermEntryReaderConfig.Builder().stream(stream);
	    builder.comboValuesPerAttribute(context.getComboValuesPerAttribute());
	    builder.importType(ImportTypeEnum.getImportType(extension));
	    builder.encoding(getFileEncoding(file));

	    TermEntryReader reader = TermEntryReaderFactory.INSTANCE.createReader(builder.build());

	    ImportValidationCallback validationCallback = new ImportValidationCallback(1);
	    validationCallback.setFileName(name);
	    reader.validate(validationCallback);

	    result = getValidationCallbackTransformer().transformToFileAnalysisReport(validationCallback);
	} catch (IOException e) {
	    LogHelper.error(LOG, e.getMessage(), e);
	    String message = String.format(MessageResolver.getMessage("FilesAnalysisRequestHandlerImpl.1"), name); //$NON-NLS-1$
	    result = new FileAnalysisReport(name);
	    result.getFileAnalysisAlerts().getAlerts()
		    .add(new Alert(AlertSubject.FILE_NOT_FOUND, AlertType.ERROR, message));
	} catch (LocaleException e) {
	    LogHelper.error(LOG, e.getMessage(), e);
	    result = new FileAnalysisReport(name);
	    result.getFileAnalysisAlerts().getAlerts()
		    .add(new Alert(AlertSubject.HEADER_CHECK, AlertType.ERROR, e.getMessage()));
	} catch (Exception unexpected) {
	    // #analyzeAsync(FilesAnalysisRequest)} will take care of this
	    throw unexpected;
	}

	return result;
    }

    private FilesAnalysisResponse analyzeAsync(FilesAnalysisRequest request) {
	FilesAnalysisResponse response = new FilesAnalysisResponse();

	StopWatch clock = new StopWatch(Messages.getString("profiling.asynchronous.analysis"));
	try {
	    String directory = request.getDirectory();

	    List<File> batch = new ArrayList<>();
	    try {
		clock.start(String.format(Messages.getString("reading.files.from.the.directory"), directory));
		batch.addAll(getFileManager().read(directory));
	    } finally {
		clock.stop();
	    }
	    List<FileAnalysisReport> reports = new ArrayList<>(batch.size());
	    for (final File file : batch) {
		try {
		    clock.start(String.format(Messages.getString("analyzing.file"), file.getName()));
		    reports.add(analyze(file, request.getContext()));
		} finally {
		    clock.stop();
		}
	    }
	    try {
		clock.start(Messages.getString("applying.post.processors"));
		postProcessReports(reports, request.getContext());
	    } finally {
		clock.stop();
	    }
	    populateResponse(response, reports, request);
	} catch (Exception e) {
	    LogHelper.error(LOG, Messages.getString("asynchronous.analysis.completed.exceptionally"));
	    LogHelper.error(LOG, e.getMessage(), e);
	    response.setCompletedExceptionally(true);
	} finally {
	    response.setCompleted(true);
	    try {
		clock.start(String.format(Messages.getString("puts.an.results.into.cache"), CACHE_NAME.getValue()));
		getCacheGateway().put(CACHE_NAME, request.getProcessingId(), response);
	    } finally {
		clock.stop();
	    }
	    LogHelper.info(LOG, clock.prettyPrint());

	}
	return response;
    }

    private CacheGateway<String, FilesAnalysisResponse> getCacheGateway() {
	return _cacheGateway;
    }

    private ExecutorService getExecutorService() {
	return _executorService;
    }

    private FileManager getFileManager() {
	return _manager;
    }

    private PostProcessorChain getPostProcessorChain() {
	return _postProcessorChain;
    }

    private ImportValidationCallbackTransformer getValidationCallbackTransformer() {
	return _validationCallbackTransformer;
    }

    private void populateResponse(FilesAnalysisResponse response, List<FileAnalysisReport> reports,
	    FilesAnalysisRequest request) {
	for (final FileAnalysisReport report : reports) {
	    response.addIfNonNull(report.getImportLanguageReport());
	    response.addIfNonNull(report.getFileAnalysisAlerts());
	    response.addIfNonNull(report.getImportAttributeReport());
	    response.addIfNonNull(report.getImportTypeReport());
	    response.setOverwriteByTermEntryId(report.isTermEntryIdExist());
	}
	response.addIfNotEmpty(request.getContext().getAttributesByLevel());
    }

    private void postProcessReports(List<FileAnalysisReport> results, Context context) {
	for (final FileAnalysisReport report : results) {
	    List<Alert> alerts = report.getFileAnalysisAlerts().getAlerts();
	    if (alerts.stream().map(Alert::getType).noneMatch(Predicate.isEqual(AlertType.ERROR))) {
		getPostProcessorChain().postProcess(report, context);
		if (Objects.nonNull(report.getImportLanguageReport())) {
		    report.getImportLanguageReport().sortImportLanguages();
		}
		if (Objects.nonNull(report.getImportAttributeReport())) {
		    report.getImportAttributeReport().sortImportAttributes();
		}
	    }
	}
    }

    private void setExecutorService(ExecutorService executorService) {
	_executorService = executorService;
    }
}
