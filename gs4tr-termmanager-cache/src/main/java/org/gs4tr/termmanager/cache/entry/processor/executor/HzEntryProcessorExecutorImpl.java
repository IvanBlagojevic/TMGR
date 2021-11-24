package org.gs4tr.termmanager.cache.entry.processor.executor;

import java.util.Map;
import java.util.Objects;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryProcessor;

@Service("hzEntryProcessorExecutor")
class HzEntryProcessorExecutorImpl implements HzEntryProcessorExecutor {

    @Autowired
    private HazelcastInstance _hzInstance;

    @Override
    public <K, V> Map<K, Object> executeOnEntries(CacheName name, EntryProcessor<K, V> command) {
	if (Objects.nonNull(command)) {
	    IMap<K, V> cache = findCacheByName(name);
	    return cache.executeOnEntries(command);
	}
	return null;
    }

    @Override
    public <K, V> Object executeOnKey(CacheName name, K key, EntryProcessor<K, V> command) {
	if (Objects.nonNull(command)) {
	    IMap<K, V> cache = findCacheByName(name);
	    return cache.executeOnKey(key, command);
	}
	return null;
    }

    private <K, V> IMap<K, V> findCacheByName(CacheName name) {
	return getHzInstance().getMap(name.getValue());
    }

    private HazelcastInstance getHzInstance() {
	return _hzInstance;
    }
}
