package org.gs4tr.termmanager.cache.entity.view.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.cache.model.EntityView;
import org.springframework.stereotype.Service;

import com.hazelcast.core.EntryView;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Service("hzEntityViewProvider")
class HzEntityViewProviderImpl implements HzEntityViewProvider {

    @Autowired
    private HazelcastInstance _hzInstance;

    @Override
    public <K, V> EntityView<K, V> getEntityView(CacheName name, K key) {
	IMap<K, V> cache = findCacheByName(name);
	EntryView<K, V> entryView = cache.getEntryView(key);
	return buildEntityView(entryView);
    }

    private <K, V> EntityView<K, V> buildEntityView(EntryView<K, V> entryView) {
	return EntityView.Builder.<K, V> start().key(entryView.getKey())
		.cost(entryView.getCost()).hits(entryView.getHits())
		.creationTime(entryView.getCreationTime())
		.expirationTime(entryView.getExpirationTime())
		.lastStoredTime(entryView.getLastStoredTime())
		.lastAccessTime(entryView.getLastAccessTime())
		.lastUpdateTime(entryView.getLastUpdateTime())
		.version(entryView.getVersion()).value(entryView.getValue())
		.build();
    }

    private <K, V> IMap<K, V> findCacheByName(CacheName name) {
	return getHzInstance().getMap(name.getValue());
    }

    private HazelcastInstance getHzInstance() {
	return _hzInstance;
    }
}
