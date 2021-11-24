package org.gs4tr.termmanager.io.tests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.gs4tr.termmanager.model.serializer.JsonIO;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStoreImpl;
import jetbrains.exodus.entitystore.PersistentEntityStores;

@Ignore
public class PocXodusEntityStoreTest extends AbstractPocXodus {

    private static final String LINK = "batchesLink";

    private static final String SHORTCODE = "SHO000001";

    private static final int SIZE = 500;

    private static final String TERM_ENTRY_BATCH = "batch";

    private PersistentEntityStore store;

    @Before
    public void setUP() {
	store = PersistentEntityStores.newInstance("target/entityStore");
    }

    @After
    public void tearDOWN() {
	store.clear();
	store.close();
    }

    @Test
    public void testImportPOC() {
	// prepare term entries for import
	List<TermEntry> batch1 = new ArrayList<>();
	List<TermEntry> batch2 = new ArrayList<>();

	IntStream.range(0, SIZE).forEach(i -> {
	    batch1.add(createTermEntry());
	    batch2.add(createTermEntry());
	});

	// simulate import, starting import and batch writing
	EntityId importEntityId = startImport(SHORTCODE);

	EntityId entityIdBatch1 = appendBatch(importEntityId, batch1);
	Assert.assertNotNull(entityIdBatch1);

	EntityId entityIdBatch2 = appendBatch(importEntityId, batch2);
	Assert.assertNotNull(entityIdBatch2);

	// validate that term entries are written
	readTermEntries(importEntityId);
    }

    @Ignore
    @Test
    public void testStoreOpeningTime() {
	List<PersistentEntityStore> stores = new ArrayList<>();

	long start = System.currentTimeMillis();
	IntStream.range(0, 1000).forEach(i -> {
	    PersistentEntityStoreImpl store = PersistentEntityStores.newInstance("target/entityStore" + i);
	    stores.add(store);
	});
	long end = System.currentTimeMillis();
	LOG.info(String.format("@@@@@ testStoreOpeningTime TIME: %d", end - start));

	stores.forEach(s -> s.close());
    }

    private EntityId appendBatch(EntityId importEntityId, List<TermEntry> batch) {
	return store.computeInExclusiveTransaction(txn -> {

	    Entity importEntity = txn.getEntity(importEntityId);

	    Entity entityBatch = txn.newEntity(TERM_ENTRY_BATCH);

	    try {
		entityBatch.setProperty("import", "import of entityBatch term entries");
		entityBatch.setBlob("batch", new ByteArrayInputStream(JsonIO.writeValueAsBytes(batch)));

		importEntity.addLink(LINK, entityBatch);
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }

	    return entityBatch.getId();
	});
    }

    private void readTermEntries(EntityId importEntityId) {
	store.executeInReadonlyTransaction(txn -> {

	    Entity importEntity = txn.getEntity(importEntityId);
	    EntityIterable iterable = importEntity.getLinks(LINK);

	    Assert.assertEquals(2, iterable.size());

	    for (Entity bacthEntity : iterable) {
		LOG.info(String.format("Entity ID is: [%s]", bacthEntity.getId()));
		InputStream batch = bacthEntity.getBlob("batch");
		List<TermEntry> termEntries = JsonIO.readValue(batch, List.class);
		LOG.info(String.format("TermEntries: [%s]", termEntries));
		Assert.assertEquals(SIZE, termEntries.size());
	    }
	});
    }

    private EntityId startImport(String shortcode) {
	return store.computeInExclusiveTransaction(txn -> {

	    Entity entityImport = txn.newEntity(shortcode);

	    try {
		entityImport.setProperty("import", "import in specified project");
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }

	    return entityImport.getId();
	});
    }
}
