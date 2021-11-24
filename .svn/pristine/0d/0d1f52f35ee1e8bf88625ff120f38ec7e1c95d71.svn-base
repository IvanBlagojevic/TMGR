package org.gs4tr.termmanager.cache.impl;

import java.util.Map;
import java.util.Set;

import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Repository("hzCacheGateway")
class HzCacheGateway<K, V> implements CacheGateway<K, V> {

    @Autowired
    private HazelcastInstance _hzInstance;

    @Override
    public IMap<K, V> findCacheByName(CacheName name) {
	return getHzInstance().getMap(name.getValue());
    }

    @Override
    public V get(CacheName name, K key) {
	IMap<K, V> cache = findCacheByName(name);
	return cache.get(key);
    }

    @Override
    public Map<K, V> getAll(CacheName name, Set<K> keys) {
	IMap<K, V> cache = findCacheByName(name);
	return cache.getAll(keys);
    }

    @Override
    public V getAndPut(CacheName name, K key, V value) {
	IMap<K, V> cache = findCacheByName(name);
	return cache.put(key, value);
    }

    @Override
    public V getAndRemove(CacheName name, K key) {
	IMap<K, V> cache = findCacheByName(name);
	return cache.remove(key);
    }

    @Override
    public void put(CacheName name, K key, V value) {
	IMap<K, V> cache = findCacheByName(name);
	cache.set(key, value);
    }

    @Override
    public V putIfAbsent(CacheName name, K key, V value) {
	IMap<K, V> cache = findCacheByName(name);
	return cache.putIfAbsent(key, value);
    }

    @Override
    public void remove(CacheName name, K key) {
	IMap<K, V> cache = findCacheByName(name);
	cache.delete(key);
    }

    @Override
    public void removeAll(CacheName name) {
	findCacheByName(name).evictAll();
    }

    private HazelcastInstance getHzInstance() {
	return _hzInstance;
    }
}
