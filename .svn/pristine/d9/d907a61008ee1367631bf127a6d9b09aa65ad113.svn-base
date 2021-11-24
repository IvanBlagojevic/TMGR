package org.gs4tr.termmanager.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StatisticsServiceConcurrencyTest extends AbstractSolrGlossaryTest {

    private static final int LIMIT = 100;

    private static final int THREAD_NUMBERS = 10;

    private ExecutorService _executor;

    @Autowired
    private StatisticsService _statisticsService;

    @After
    public void after() throws Exception {
	super.after();
	if (Objects.nonNull(getExecutor()) && !getExecutor().isShutdown()) {
	    shutdownAndAwaitTermination();
	}
    }

    @Before
    public void before() throws Exception {
	super.before();
	setExecutor(Executors.newFixedThreadPool(THREAD_NUMBERS));
    }

    public ExecutorService getExecutor() {
	return _executor;
    }

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    public void setExecutor(ExecutorService executor) {
	_executor = executor;
    }

    @Test
    public void updateStatisticsTest() {

	List<Statistics> statisticsBefore = getStatisticsService().findStatisticsByProjectId(1L);

	/* Test default Statistics counts before update */
	statisticsBefore.forEach(statistics -> assertStatisticsCounts(1, statistics));

	IntStream.rangeClosed(1, LIMIT).forEach(k -> getExecutor().execute(updateStatistics()));
	shutdownAndAwaitTermination();

	List<Statistics> statisticsAfter = getStatisticsService().findStatisticsByProjectId(1L);

	Assert.assertNotNull(statisticsAfter);

	/* Statistics that should be updated */
	for (int i = 0; i < 4; i++) {
	    Statistics statistics = statisticsAfter.get(i);
	    assertStatisticsCounts(101, statistics);
	}

	/* Rest statistics records should not be changed */
	for (int i = 4; i < statisticsAfter.size(); i++) {
	    Statistics statistics = statisticsAfter.get(i);
	    assertStatisticsCounts(1, statistics);
	}
    }

    private void assertStatisticsCounts(int counts, Statistics statistics) {
	Assert.assertEquals(counts, statistics.getAddedApproved());
	Assert.assertEquals(counts, statistics.getApproved());

	Assert.assertEquals(counts, statistics.getAddedPending());
	Assert.assertEquals(counts, statistics.getDemoted());

	Assert.assertEquals(counts, statistics.getAddedOnHold());
	Assert.assertEquals(counts, statistics.getOnHold());

	Assert.assertEquals(counts, statistics.getAddedBlacklisted());
	Assert.assertEquals(counts, statistics.getBlacklisted());

	Assert.assertEquals(counts, statistics.getDeleted());
    }

    private void awaitTermination() throws InterruptedException {
	if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
	    getExecutor().shutdownNow();
	    if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
		System.err.println("Executor did not terminate");
	    }
	}
    }

    private void shutdownAndAwaitTermination() {
	getExecutor().shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    getExecutor().shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }

    private Runnable updateStatistics() {
	return () -> {
	    StatisticsInfo statisticsInfo = new StatisticsInfo(1L, "en-US");

	    statisticsInfo.incrementAddedApprovedCount();
	    statisticsInfo.incrementApprovedCount();

	    statisticsInfo.incrementAddedPendingCount();
	    statisticsInfo.incrementPendingCount();

	    statisticsInfo.incrementAddedOnHoldCount();
	    statisticsInfo.incrementOnHoldCount();

	    statisticsInfo.incrementAddedBlacklistedCount();
	    statisticsInfo.incrementBlacklistedCount();

	    statisticsInfo.incrementDeletedCount();

	    getStatisticsService().updateStatistics(statisticsInfo);
	};
    }
}
