package org.gs4tr.termmanager.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ImportSummary;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/cache/spring/applicationContext-hazelcast.xml",
	"classpath:org/gs4tr/termmanager/cache/spring/applicationContext-init.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public class GuavaCacheGatewayTest {

    private static final CacheName IMPORT_PROGRESS_STATUS = CacheName.IMPORT_PROGRESS_STATUS;

    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";
    private static final String KEY_3 = "key3";

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportSummary> _guavaCacheGateway;

    @After
    public void after() {
        getGuavaCacheGateway().removeAll(CacheName.IMPORT_PROGRESS_STATUS);
    }

    @Test
    public void putGetTest() {
	assertNull(getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));

	getGuavaCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary());

	ImportSummary importSummary = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNotNull(importSummary);
    }

    @Test
    public void putIfAbsentTest() {
	getGuavaCacheGateway().putIfAbsent(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(1));

	ImportSummary importSummary = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertEquals(1l, importSummary.getNoTermEntryForImport().longValue());

	getGuavaCacheGateway().putIfAbsent(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(2));

	importSummary = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);
	// This is correct, key1 was already in cache
	assertEquals(1l, importSummary.getNoTermEntryForImport().longValue());
    }

    @Test
    public void putRemoveTest() {
	assertNull(getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));

	getGuavaCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(15));

	ImportSummary importSummary = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNotNull(importSummary);
	assertEquals(15l, importSummary.getNoTermEntryForImport().longValue());

	getGuavaCacheGateway().remove(IMPORT_PROGRESS_STATUS, KEY_1);

	assertNull(getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));
    }

    @Test
    public void removeAllTest() {
	getGuavaCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_1, new ImportSummary(1));
	getGuavaCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_2, new ImportSummary(2));
	getGuavaCacheGateway().put(IMPORT_PROGRESS_STATUS, KEY_3, new ImportSummary(3));

	ImportSummary importSummary1 = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1);
	assertEquals(1l, importSummary1.getNoTermEntryForImport().longValue());
	ImportSummary importSummary2 = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_2);
	assertEquals(2l, importSummary2.getNoTermEntryForImport().longValue());
	ImportSummary importSummary3 = getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_3);
	assertEquals(3l, importSummary3.getNoTermEntryForImport().longValue());

	getGuavaCacheGateway().removeAll(IMPORT_PROGRESS_STATUS);

	assertNull(getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_1));
	assertNull(getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_2));
	assertNull(getGuavaCacheGateway().get(IMPORT_PROGRESS_STATUS, KEY_3));
    }

    private CacheGateway<String, ImportSummary> getGuavaCacheGateway() {
	return _guavaCacheGateway;
    }
}
