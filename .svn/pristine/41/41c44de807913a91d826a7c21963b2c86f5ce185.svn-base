package org.gs4tr.termmanager.io.tests;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.gs4tr.termmanager.dao.backup.BackupException;
import org.gs4tr.termmanager.io.tlog.TransactionProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.cache.guava.GuavaCache;

import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStoreImpl;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import jetbrains.exodus.entitystore.StoreTransaction;

@Ignore
public class PocXodusEntityMultipleAccess extends AbstractPocXodus {

    private static final String DIR = "target/entityStoreMA";

    private static final int N_THREADS = Runtime.getRuntime().availableProcessors() + 1;

    private ListeningExecutorService _executor;

    private GuavaCache _storeCache;

    @Before
    public void setUP() {
	_storeCache = new GuavaCache("testStores", CacheBuilder.newBuilder().build());
	_executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));

	_storeCache.putIfAbsent(1L, PersistentEntityStores.newInstance(DIR));
    }

    @After
    public void tearDown() {
	_storeCache.clear();
	shutdownAndAwaitTermination();
    }

    @Test(expected = Exception.class)
    public void testStoreMultipleAccess_case1() throws Exception {
	CountDownLatch countDownLatch = new CountDownLatch(2);

	PersistentEntityStoreImpl store = PersistentEntityStores.newInstance(DIR.concat("_Test"));

	Callable<EntityId> task1 = createCallable(store, countDownLatch, 1000);
	Callable<EntityId> task2 = createCallable(store, countDownLatch, 10);

	_executor.invokeAll(Arrays.asList(task1, task2));

	countDownLatch.await();
    }

    @Test
    public void testStoreMultipleAccess_case2() throws Exception {
	CountDownLatch countDownLatch = new CountDownLatch(2);

	createCallable1(countDownLatch, 1000);
	_executor.invokeAll(Arrays.asList(createCallable1(countDownLatch, 1000), createCallable1(countDownLatch, 10)));

	countDownLatch.await();
    }

    private void awaitTermination() throws InterruptedException {
	if (!_executor.awaitTermination(30, TimeUnit.SECONDS)) {
	    _executor.shutdownNow();
	    if (!_executor.awaitTermination(30, TimeUnit.SECONDS)) {
		LOG.error("Executor did not terminate");
	    }
	}
    }

    private Callable<EntityId> createCallable(PersistentEntityStore store, CountDownLatch countDownLatch, int sleep) {
	return () -> store.computeInExclusiveTransaction(t -> {
	    Entity entityBatch = t.newEntity("entry");
	    try {
		entityBatch.setProperty("import", "Testing this");
		Thread.sleep(sleep);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    } finally {
		countDownLatch.countDown();
	    }

	    return entityBatch.getId();
	});
    }

    private Callable<EntityId> createCallable1(CountDownLatch countDownLatch, int sleep) {
	return () -> doInTransaction(1L, txn -> {

	    Entity entityBatch = txn.newEntity("entry");
	    try {
		entityBatch.setProperty("import", "TEST");
		Thread.sleep(sleep);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    } finally {
		countDownLatch.countDown();
	    }

	    return entityBatch.getId();
	});
    }

    private PersistentEntityStore getStore(Long key) {
	return _storeCache.get(key, PersistentEntityStore.class);
    }

    private void shutdownAndAwaitTermination() {
	_executor.shutdown();
	try {
	    awaitTermination();
	} catch (Exception e) {
	    _executor.shutdownNow();
	    Thread.currentThread().interrupt();
	}
    }

    protected EntityId doInTransaction(Long key, TransactionProcessor<EntityId, StoreTransaction> processor)
	    throws BackupException {
	processor.beforeTransactionCompletion();

	EntityId id;

	PersistentEntityStore store = getStore(key);
	StoreTransaction txn = store.beginExclusiveTransaction();
	try {
	    id = processor.process(txn);
	    txn.commit();
	    txn.flush();
	} catch (Exception e) {
	    if (txn != null) {
		txn.revert();
	    }
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    processor.afterTransactionCompletion();
	    if (txn != null && !txn.isFinished()) {
		txn.abort();
	    }
	    store.close();
	}

	return id;
    }
}
