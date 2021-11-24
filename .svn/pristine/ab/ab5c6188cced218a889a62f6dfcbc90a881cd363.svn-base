package org.gs4tr.termmanager.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.cache.model.CacheView;
import org.gs4tr.termmanager.cache.view.provider.HzCacheViewProvider;
import org.gs4tr.termmanager.model.ImportSummary;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HzCacheViewProviderTest extends AbstractSpringCacheGatewayTest<String, ImportSummary> {

    @Autowired
    private HzCacheViewProvider _hzCacheViewProvider;

    @Test
    public void getCacheViewTest() {
	getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, "key1", new ImportSummary());
	getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, "key2", new ImportSummary());
	getCacheGateway().put(CacheName.IMPORT_PROGRESS_STATUS, "key3", new ImportSummary());

	getCacheGateway().get(CacheName.IMPORT_PROGRESS_STATUS, "key1");
	getCacheGateway().get(CacheName.IMPORT_PROGRESS_STATUS, "key2");
	getCacheGateway().remove(CacheName.IMPORT_PROGRESS_STATUS, "key3");

	CacheView cacheView = getHzCacheViewProvider().getCacheView(CacheName.IMPORT_PROGRESS_STATUS);

	// Correct because we have cluster with one node.
	assertEquals(0l, cacheView.getBackupEntryCount());

	assertNotNull(cacheView.getPutOperationCount());
	assertNotNull(cacheView.getGetOperationCount());
	assertNotNull(cacheView.getHits());
	assertNotNull(cacheView.getTotal());
	assertNotNull(cacheView.getOwnedEntryCount());

	assertNotNull(cacheView.getOwnedEntryMemoryCost());
    }

    private HzCacheViewProvider getHzCacheViewProvider() {
	return _hzCacheViewProvider;
    }
}
