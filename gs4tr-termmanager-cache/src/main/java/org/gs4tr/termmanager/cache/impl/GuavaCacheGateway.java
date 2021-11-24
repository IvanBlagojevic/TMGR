package org.gs4tr.termmanager.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import com.hazelcast.core.IMap;

@Repository("guavaCacheGateway")
public class GuavaCacheGateway<K, V> implements CacheGateway<K, V> {

    @Autowired
    private CacheManager _cacheManager;

    @Override
    public IMap<K, V> findCacheByName(CacheName name) {
	throw new UnsupportedOperationException("This method is not supported for this cache type.");
    }

    @Override
    public V get(CacheName name, K key) {
	Cache.ValueWrapper wrapper = getCache(name).get(key);
	return Objects.nonNull(wrapper) ? (V) wrapper.get() : null;
    }

    @Override
    public Map<K, V> getAll(CacheName name, Set<K> keys) {
	Map<K, V> map = new HashMap<>();
	Cache cache = getCache(name);
	for (K k : keys) {
	    Cache.ValueWrapper wrapper = cache.get(k);
	    if (Objects.nonNull(wrapper)) {
		map.put(k, (V) wrapper.get());
	    }
	}
	return map;
    }

    @Override
    public V getAndPut(CacheName name, K key, V value) {
	throw new UnsupportedOperationException("This method is not supported for this cache type.");
    }

    @Override
    public V getAndRemove(CacheName name, K key) {
	throw new UnsupportedOperationException("This method is not supported for this cache type.");
    }

    @Override
    public void put(CacheName name, K key, V value) {
	getCache(name).put(key, value);
    }

    @Override
    public V putIfAbsent(CacheName name, K key, V value) {
        Cache.ValueWrapper wrapper =  getCache(name).putIfAbsent(key, value);
        return Objects.nonNull(wrapper) ? (V) wrapper.get() : null;
    }

    @Override
    public void remove(CacheName name, K key) {
	getCache(name).evict(key);
    }

    @Override
    public void removeAll(CacheName name) {
	getCache(name).clear();
    }

    private Cache getCache(CacheName name) {
	return getCacheManager().getCache(name.getValue());
    }

    private CacheManager getCacheManager() {
	return _cacheManager;
    }
}
