package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.gs4tr.termmanager.service.lock.manager.ExclusiveWriteLockManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO: After we add cleanUp() method and improve shutdown, we should consider removing @Ignore from this test.  
@Ignore
public class ExclusiveWriteLockManagerTest extends AbstractSpringHazelcastTest {

    static final class Value {
	private int _amount;
	private int _valueId;
    }

    private static final int LIMIT = 100;

    private static final String OWNER = "donnie";

    private static final int THREAD_NUMBERS = 10;

    @Autowired
    private ExclusiveWriteLockManager _exclusiveWriteLockManager;

    private ExecutorService _executor;

    @After
    public void cleanUp() {
	if (Objects.nonNull(getExecutor()) && !getExecutor().isShutdown()) {
	    shutdownAndAwaitTermination();
	}
    }

    @Test
    public void exclusiveWritePessimisticLockTest() throws InterruptedException {
	Value value = new Value();

	IntStream.rangeClosed(1, LIMIT).forEach(k -> getExecutor().execute(increment(value)));

	shutdownAndAwaitTermination();

	assertEquals(LIMIT, value._amount);
    }

    @Before
    public void setUp() {
	_executor = Executors.newFixedThreadPool(THREAD_NUMBERS);
    }

    private void acquireLock(int valueId) {
	getExclusiveWriteLockManager().acquireLock(valueId, OWNER);
    }

    private void awaitTermination() throws InterruptedException {
	if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
	    getExecutor().shutdownNow();
	    if (!getExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
		System.err.println("Executor did not terminate");
	    }
	}
    }

    private ExclusiveWriteLockManager getExclusiveWriteLockManager() {
	return _exclusiveWriteLockManager;
    }

    private ExecutorService getExecutor() {
	return _executor;
    }

    private Runnable increment(Value value) {
	return () -> {
	    acquireLock(value._valueId);
	    try {
		value._amount++;
	    } finally {
		releaseLock(value._valueId);
	    }
	};
    }

    private void releaseLock(int valueId) {
	getExclusiveWriteLockManager().releaseLock(valueId, OWNER);
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
}
