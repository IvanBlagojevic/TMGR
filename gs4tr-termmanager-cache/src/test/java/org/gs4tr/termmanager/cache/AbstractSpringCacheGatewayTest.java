package org.gs4tr.termmanager.cache;

import org.gs4tr.foundation.modules.spring.TestEnvironmentAwareContextLoader;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.junit.runner.RunWith;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/gs4tr/termmanager/cache/spring/applicationContext-hazelcast.xml",
	"classpath:org/gs4tr/termmanager/cache/spring/applicationContext-init.xml" }, loader = TestEnvironmentAwareContextLoader.class)
public abstract class AbstractSpringCacheGatewayTest<K, V> {

    @Autowired
    @Qualifier("hzCacheGateway")
    private CacheGateway<K, V> _cacheGateway;

    @After
    public void after() {
	getCacheGateway().removeAll(CacheName.IMPORT_PROGRESS_STATUS);
	getCacheGateway().removeAll(CacheName.BATCH_PROCESSING_STATUS);
    }

    protected CacheGateway<K, V> getCacheGateway() {
	return _cacheGateway;
    }
}
