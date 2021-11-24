package org.gs4tr.termmanager.io.tests;

import java.util.stream.IntStream;

import org.gs4tr.termmanager.model.serializer.JsonIO;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.StringBinding;
import jetbrains.exodus.env.Cursor;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.env.Store;
import jetbrains.exodus.env.StoreConfig;

@Ignore
public class PocXodusEnvTest extends AbstractPocXodus {

    private static final String ENV = "target/xodusEnv";

    private static final String ENV_WITH_DUPES = "target/xodusEnvWithDupes";

    private Environment env;

    private Environment envWithDupes;

    private Store store;

    private Store storeWithDupes;

    @Before
    public void setUP() {
	env = Environments.newInstance(ENV);
	store = env.computeInTransaction(txn -> env.openStore(STORE_ENTRY, StoreConfig.WITHOUT_DUPLICATES, txn));

	envWithDupes = Environments.newInstance(ENV_WITH_DUPES);
	storeWithDupes = envWithDupes
		.computeInTransaction(txn -> envWithDupes.openStore(STORE_ENTRY, StoreConfig.WITH_DUPLICATES, txn));

	LOG.info("Initialized environment and term entries store!");
    }

    @After
    public void tearDOWN() {
	env.clear();
	env.close();

	envWithDupes.clear();
	envWithDupes.close();

	LOG.info("environment cleared and closed!");
    }

    @Test
    public void test_1_storeOneEntry() {
	TermEntry entry = createTermEntry();

	ByteIterable key = StringBinding.stringToEntry("myKey");

	ByteIterable value = StringBinding.stringToEntry(JsonIO.writeValueAsString(entry));

	env.executeInTransaction(txn -> store.put(txn, key, value));

	env.executeInTransaction(txn -> Assert.assertTrue(store.exists(txn, key, value)));

	LOG.info("Put test success!");
    }

    @Test
    public void test_2_storeTwoEntries() {
	TermEntry entry1 = createTermEntry();
	TermEntry entry2 = createTermEntry();

	ByteIterable key1 = StringBinding.stringToEntry("myKey1");
	ByteIterable value1 = StringBinding.stringToEntry(JsonIO.writeValueAsString(entry1));

	ByteIterable key2 = StringBinding.stringToEntry("myKey2");
	ByteIterable value2 = StringBinding.stringToEntry(JsonIO.writeValueAsString(entry2));

	env.executeInTransaction(txn -> store.put(txn, key1, value1));
	env.executeInTransaction(txn -> store.put(txn, key2, value2));

	env.executeInTransaction(txn -> Assert.assertTrue(store.exists(txn, key2, value2)));
	env.executeInTransaction(txn -> Assert.assertTrue(store.exists(txn, key1, value1)));

	env.executeInReadonlyTransaction(txn -> {
	    int count = 0;
	    Cursor cursor = store.openCursor(txn);
	    while (cursor.getNext()) {
		count++;
		String key = StringBinding.entryToString(cursor.getKey());
		String value = StringBinding.entryToString(cursor.getValue());
		LOG.info(String.format("Key: [%s], value: [%s]", key, value));
		Assert.assertEquals("myKey" + count, key);
	    }

	    Assert.assertEquals(2, count);
	});

	LOG.info("Put test success!");
    }

    @Ignore
    @Test
    public void test_3_storeDupKeys() {
	TermEntry entry = createTermEntry();

	ByteIterable key1 = StringBinding.stringToEntry("myKey");
	ByteIterable value1 = StringBinding.stringToEntry(JsonIO.writeValueAsString(entry));

	ByteIterable key2 = StringBinding.stringToEntry("myKey");
	ByteIterable value2 = StringBinding.stringToEntry(JsonIO.writeValueAsString(entry));

	envWithDupes.executeInTransaction(txn -> storeWithDupes.put(txn, key1, value1));
	envWithDupes.executeInTransaction(txn -> storeWithDupes.put(txn, key2, value2));

	envWithDupes.executeInReadonlyTransaction(txn -> {
	    int count = 0;
	    Cursor cursor = storeWithDupes.openCursor(txn);
	    while (cursor.getNext()) {
		count++;
		String key = StringBinding.entryToString(cursor.getKey());
		String value = StringBinding.entryToString(cursor.getValue());
		LOG.info(String.format("Key: [%s], value: [%s]", key, value));
		Assert.assertEquals("myKey", key);
	    }

	    Assert.assertEquals(2, count);
	});

	LOG.info("Put test success!");
    }

    @Test
    public void test_4_storeEntriesInOneBatch() {

	int numberOfEntries = 500;

	IntStream.rangeClosed(1, numberOfEntries).forEach(i -> {
	    TermEntry entry = createTermEntry();
	    ByteIterable key = StringBinding.stringToEntry("myKey" + i);
	    ByteIterable value = StringBinding.stringToEntry(JsonIO.writeValueAsString(entry));

	    env.executeInTransaction(txn -> store.put(txn, key, value));
	});

	env.executeInReadonlyTransaction(txn -> {
	    int count = 0;
	    Cursor cursor = store.openCursor(txn);
	    while (cursor.getNext()) {
		count++;
		String key = StringBinding.entryToString(cursor.getKey());
		String value = StringBinding.entryToString(cursor.getValue());
		Assert.assertNotNull(value);
		LOG.info(String.format("Key: [%s], value: [%s]", key, value));
		// Assert.assertEquals("myKey" + count, key);
	    }

	    Assert.assertEquals(numberOfEntries, count);
	});

	LOG.info("Put test success!");
    }
}
