package org.gs4tr.termmanager.service.solr.restore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.RegularBackupCleaner;
import org.gs4tr.termmanager.service.SubmissionBackupCleaner;
import org.gs4tr.termmanager.service.utils.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component("cleanUpProcessorV2")
public class CleanUpProcessorV2Impl implements ICleanUpProcessorV2 {

    private static final Log LOGGER = LogFactory.getLog(CleanUpProcessorV2Impl.class);

    private static final String TASK = "CLEANUP";

    @Value("${index.batchSize:500}")
    private int _batchSize;

    private ExecutorService _executorService;

    private volatile boolean _finished = false;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private RegularBackupCleaner _regularBackupCleaner;

    @Autowired
    private SubmissionBackupCleaner _submissionBackupCleaner;

    @Override
    public void cleanup() throws Exception {
	StopWatch watch = new StopWatch(TASK);
	watch.start(TASK);
	deleteDisabledTerminology();

	LogHelper.debug(LOGGER, "Start executing cleanup of hidden terminology.");
	getRegularBackupCleaner().deleteHiddenTerms(getBatchSize());
	LogHelper.debug(LOGGER, "Finished executing cleanup of hidden terminology.");

	shutdownAndAwaitTermination();

	watch.stop();
	LogHelper.info(LOGGER, watch.prettyPrint());

	_finished = true;
    }

    public RegularBackupCleaner getRegularBackupCleaner() {
	return _regularBackupCleaner;
    }

    public SubmissionBackupCleaner getSubmissionBackupCleaner() {
	return _submissionBackupCleaner;
    }

    @Override
    public boolean isFinished() {
	return _finished;
    }

    private void awaitTermination() throws InterruptedException {
	if (!_executorService.awaitTermination(30, TimeUnit.SECONDS)) {
	    _executorService.shutdownNow();
	    if (!_executorService.awaitTermination(30, TimeUnit.SECONDS)) {
		LOGGER.error("Executor did not terminate");
	    }
	}
    }

    private Runnable clearDisabledCounts(Collection<Long> projectIds, CountDownLatch cdl) {
	return () -> {
	    getRegularBackupCleaner().clearCountsAndStatistics(projectIds, getBatchSize());
	    LogHelper.debug(LOGGER,
		    String.format("Finished clearCountsAndStatistics.", Thread.currentThread().getName()));
	    cdl.countDown();
	};
    }

    private Runnable clearSubmissions(Collection<Long> projectIds, CountDownLatch cdl) {
	return () -> {
	    getSubmissionBackupCleaner().deleteSubmissionsByProjectIds(projectIds, getBatchSize());
	    LogHelper.debug(LOGGER,
		    String.format("Finished deleteSubmissionsByProjectIds.", Thread.currentThread().getName()));
	    cdl.countDown();
	};
    }

    private Runnable createDeleteDisabledRunnable(Collection<Long> projectIds, CountDownLatch cdl) {
	return () -> {
	    getRegularBackupCleaner().deleteByProjectIds(projectIds, getBatchSize());
	    LogHelper.debug(LOGGER, String.format("%s Finished deleteByProjectIds.", Thread.currentThread().getName()));
	    cdl.countDown();
	};
    }

    private void deleteDisabledTerminology() throws InterruptedException {
	Set<Long> disabledProjectIds = getDisabledProjectIds();
	Map<Integer, List<Long>> split = StreamUtils.splitList(new ArrayList<>(disabledProjectIds), 5);
	if (!split.isEmpty()) {
	    int num = split.keySet().size() * 3;
	    _executorService = Executors.newFixedThreadPool(num);
	    CountDownLatch cdl = new CountDownLatch(num);
	    LogHelper.debug(LOGGER, "Start executing cleanup of disabled terminology.");
	    for (Map.Entry<Integer, List<Long>> entry : split.entrySet()) {
		_executorService.execute(createDeleteDisabledRunnable(entry.getValue(), cdl));
		_executorService.execute(clearDisabledCounts(entry.getValue(), cdl));
		_executorService.execute(clearSubmissions(entry.getValue(), cdl));
	    }
	    cdl.await();
	    LogHelper.debug(LOGGER, "Finished executing cleanup of disabled terminology.");
	}
    }

    private int getBatchSize() {
	return _batchSize;
    }

    private Set<Long> getDisabledProjectIds() {
	Set<Long> projectIds = new HashSet<>();

	List<Long> disabledProjectIds = getProjectService().findAllDisabledProjectIds();
	List<Long> xxxProjects = getProjectService().findProjectsByNameLike("xxx");

	projectIds.addAll(disabledProjectIds);
	projectIds.addAll(xxxProjects);

	return projectIds;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private void shutdownAndAwaitTermination() {
	if (Objects.isNull(_executorService)) {
	    return;
	}
	_executorService.shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    _executorService.shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }
}
