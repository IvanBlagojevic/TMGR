package org.gs4tr.termmanager.io.tests.tlog;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import jetbrains.exodus.entitystore.EntityId;

@Ignore
public class TransactionLogMalestingTest extends AbstractIOTest {

    private static final int COUNT = 100;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(COUNT);

    private static final Log LOGGER = LogFactory.getLog(TransactionLogMalestingTest.class);

    @Test
    public void testMalest() throws Exception {
	// CountDownLatch countDownLatch = new CountDownLatch(COUNT);
	// IntStream.range(0, COUNT).forEach(i ->
	// EXECUTOR_SERVICE.execute(createTask(countDownLatch)));

	IntStream.range(0, COUNT).forEach(i -> testImport());
    }

    private Runnable createTask(CountDownLatch countDownLatch) {
	return () -> {
	    testImport();
	    countDownLatch.countDown();
	};
    }

    private void testImport() {

	String threadName = Thread.currentThread().getName();

	LOGGER.info(String.format("Thread [%s] started.", threadName));

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = TestHelper.PROJECT_ID_2; // 2L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	Optional<EntityId> optChildId = logHandler.appendAndLink(projectId, optParentId.get(),
		TestHelper.createTransactionalUnit());
	Assert.assertTrue(optChildId.isPresent());

	logHandler.finishAppending(projectId, optParentId.get());

	LOGGER.info(String.format("Thread [%s] stopped.", threadName));
    }
}
