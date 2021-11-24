package org.gs4tr.termmanager.cache.view.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.cache.model.CacheView;
import org.springframework.stereotype.Service;

import com.hazelcast.core.IMap;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.monitor.LocalMapStats;

@Service("hzCacheViewProvider")
class HzCacheViewProviderImpl implements HzCacheViewProvider {

    @Autowired
    private HazelcastInstance _hzInstance;

    @Override
    public CacheView getCacheView(CacheName name) {
	return buildCacheView(findCacheByName(name).getLocalMapStats());
    }

    private CacheView buildCacheView(final LocalMapStats snapshot) {
	return CacheView.Builder.start().total(snapshot.total())
		.creationTime(snapshot.getCreationTime())
		.ownedEntryCount(snapshot.getOwnedEntryCount())
		.backupEntryCount(snapshot.getBackupEntryCount())
		.ownedEntryMemoryCost(snapshot.getOwnedEntryMemoryCost())
		.getOperationCount(snapshot.getGetOperationCount())
		.putOperationCount(snapshot.getPutOperationCount())
		.maxPutLatency(snapshot.getMaxPutLatency())
		.maxGetLatency(snapshot.getMaxGetLatency())
		.heapCost(snapshot.getHeapCost()).hits(snapshot.getHits())
		.build();
    }

    private <K, V> IMap<K, V> findCacheByName(CacheName name) {
	return getHzInstance().getMap(name.getValue());
    }

    private HazelcastInstance getHzInstance() {
	return _hzInstance;
    }
}
