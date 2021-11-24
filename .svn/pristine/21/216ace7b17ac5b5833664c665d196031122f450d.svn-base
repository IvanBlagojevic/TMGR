package org.gs4tr.termmanager.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ImportSummary;
import org.junit.Test;

import org.apache.commons.collections.MapUtils;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class HzCacheGatewayTest extends AbstractSpringCacheGatewayTest<String, ImportSummary> {

    private static final CacheName IMPORT_PROGRESS_STATUS = CacheName.IMPORT_PROGRESS_STATUS;

    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";
    private static final String KEY_3 = "key3";

    @Test
    public void getAndPutTest() {
	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));

	ImportSummary importSummary = getCacheGateway().getAndPut(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary());

	// This is correct, there was no mapping for the key
	assertNull(importSummary);

	ImportSummary oldImportSummary = getCacheGateway().getAndPut(IMPORT_PROGRESS_STATUS, KEY_1,
		new ImportSummary(15));

	assertNotNull(oldImportSummary);
	assertEquals(0l, oldImportSummary.getNoTermEntryForImport().longValue());

	ImportSummary newImportSummary = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNotNull(newImportSummary);
	assertEquals(15l, newImportSummary.getNoTermEntryForImport().longValue());
    }

    @Test
    public void getAndRemoveTest() {
	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));

	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(1));

	ImportSummary importSummary = getCacheGateway().getAndRemove(IMPORT_PROGRESS_STATUS, KEY_1);

	assertEquals(1l, importSummary.getNoTermEntryForImport().longValue());

	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));
    }

    @Test
    public void putAndGetAllTest() {
	final HashSet<String> keys = Sets.newHashSet(KEY_1, KEY_2, KEY_3);
	
	Map<String, ImportSummary> summaries = getCacheGateway().getAll(IMPORT_PROGRESS_STATUS, keys);
	assertTrue(MapUtils.isEmpty(summaries));

	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(1));
	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_2, new ImportSummary(2));
	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_3, new ImportSummary(3));

	summaries = getCacheGateway().getAll(IMPORT_PROGRESS_STATUS, keys);
	assertEquals(3, summaries.size());

	assertEquals(1, summaries.get(KEY_1).getNoTermEntryForImport().intValue());
	assertEquals(2, summaries.get(KEY_2).getNoTermEntryForImport().intValue());
	assertEquals(3, summaries.get(KEY_3).getNoTermEntryForImport().intValue());
    }

    @Test
    public void putGetTest() {
	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));

	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary());

	ImportSummary importSummary = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNotNull(importSummary);
    }

    @Test
    public void putIfAbsentTest() {
	getCacheGateway().putIfAbsent(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(1));

	ImportSummary importSummary = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertEquals(1l, importSummary.getNoTermEntryForImport().longValue());

	getCacheGateway().putIfAbsent(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(2));

	importSummary = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);
	// This is correct, key1 was already in cache
	assertEquals(1l, importSummary.getNoTermEntryForImport().longValue());
    }

    @Test
    public void putRemoveTest() {
	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));

	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(15));

	ImportSummary importSummary = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNotNull(importSummary);
	assertEquals(15l, importSummary.getNoTermEntryForImport().longValue());

	getCacheGateway().remove(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));
    }

    @Test
    public void removeAllTest() {
	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(1));
	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_2, new ImportSummary(2));
	getCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_3, new ImportSummary(3));

	ImportSummary importSummary1 = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);
	assertEquals(1l, importSummary1.getNoTermEntryForImport().longValue());
	ImportSummary importSummary2 = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_2);
	assertEquals(2l, importSummary2.getNoTermEntryForImport().longValue());
	ImportSummary importSummary3 = getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_3);
	assertEquals(3l, importSummary3.getNoTermEntryForImport().longValue());

	getCacheGateway().removeAll(IMPORT_PROGRESS_STATUS);

	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));
	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_2));
	assertNull(getCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_3));
    }
}
