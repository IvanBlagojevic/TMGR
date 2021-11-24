package org.gs4tr.termmanager.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.gs4tr.termmanager.cache.entity.view.provider.HzEntityViewProvider;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.cache.model.EntityView;
import org.gs4tr.termmanager.model.BatchJobName;
import org.gs4tr.termmanager.model.BatchJobsInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HzEntityViewProviderTest extends AbstractSpringCacheGatewayTest<String, BatchJobsInfo> {

    private static final CacheName CACHE_NAME = CacheName.BATCH_PROCESSING_STATUS;

    private static final String KEY = "key";

    @Autowired
    private HzEntityViewProvider _hzEntityViewProvider;

    @Test
    public void getEntityViewTest() {
	BatchJobsInfo originalValue = new BatchJobsInfo();
	getCacheGateway().put(CACHE_NAME, KEY, originalValue);

	EntityView<String, BatchJobsInfo> entityView = getHzEntityViewProvider().getEntityView(CACHE_NAME, KEY);

	// Correct, we are storing entries in OBJECT format.
	assertEquals(0, entityView.getCost());

	assertTrue(entityView.getCreationTime() > 0);
	assertTrue(entityView.getExpirationTime() > 0);
	assertTrue(entityView.getLastUpdateTime() > 0);

	assertEquals(0, entityView.getVersion());
	assertEquals(0, entityView.getHits());
	assertEquals(0, entityView.getLastAccessTime());

	BatchJobsInfo entityViewValue = entityView.getValue();
	assertEquals(originalValue.getBatchJobsCompleted().size(), entityViewValue.getBatchJobsCompleted().size());
	assertEquals(originalValue.getBatchJobsInProcess().size(), entityViewValue.getBatchJobsInProcess().size());
	assertEquals(KEY, entityView.getKey());

	BatchJobsInfo cacheValue = getCacheGateway().get(CACHE_NAME, KEY);
	List<BatchJobName> batchJobsInProcess = cacheValue.getBatchJobsInProcess();
	batchJobsInProcess.add(BatchJobName.DELETE_RENAME_PROJECT_ATTRIBUTE);

	getCacheGateway().put(CACHE_NAME, KEY, cacheValue);

	EntityView<String, BatchJobsInfo> newEntityView = getHzEntityViewProvider().getEntityView(CACHE_NAME, KEY);

	assertTrue(newEntityView.getCreationTime() > 0);
	assertEquals(entityView.getCreationTime(), newEntityView.getCreationTime());
	assertTrue(newEntityView.getExpirationTime() > 0);
	assertTrue(newEntityView.getLastUpdateTime() > 0);
	assertTrue(newEntityView.getLastAccessTime() > 0);

	assertEquals(2, newEntityView.getHits());
	assertEquals(1, newEntityView.getVersion());

	BatchJobsInfo newEntityViewValue = newEntityView.getValue();
	assertEquals(cacheValue.getBatchJobsCompleted().size(), newEntityViewValue.getBatchJobsCompleted().size());
	assertEquals(cacheValue.getBatchJobsInProcess().size(), newEntityViewValue.getBatchJobsInProcess().size());
	assertEquals(KEY, newEntityView.getKey());
    }

    private HzEntityViewProvider getHzEntityViewProvider() {
	return _hzEntityViewProvider;
    }
}
