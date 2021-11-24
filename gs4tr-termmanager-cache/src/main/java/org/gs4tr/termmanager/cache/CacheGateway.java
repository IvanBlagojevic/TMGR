package org.gs4tr.termmanager.cache;

import java.util.Map;
import java.util.Set;

import com.hazelcast.core.IMap;
import org.gs4tr.termmanager.cache.model.CacheName;

/**
 * The <tt>CacheGateway</tt> interface is a very simple wrapper pattern. Other
 * objects access the resource through this Gateway, which translates the simple
 * method calls into the appropriate specialized API.
 *
 * @author TMGR_Backend
 *
 * @param <K>
 *            the type of keys maintained by this <tt>CacheGateway</tt>
 * @param <V>
 *            the type of mapped values
 */
public interface CacheGateway<K, V> {

    /**
     * Returns the value for the specified key from cache with specified cache
     * name, or {@code null} if cache does not contain this key.
     * <p>
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            the specified key.
     * @return value if one existed or null if no mapping existed for this key.
     */
    V get(CacheName name, K key);

    /**
     * Gets a collection of entries from the Cache, with the specified cache
     * name, returning them as <code>Map</code> of the values associated with
     * the set of keys requested.
     *
     * @param name
     *            The specified cache name.
     * @param keys
     *            The keys whose associated values are to be returned.
     * @return A map of entries that were found for the given keys.
     */
    Map<K, V> getAll(CacheName name, Set<K> keys);

    /**
     * Associates the specified value with the specified key in cache with
     * specified cache name. If the cache previously contained a mapping for the
     * key, the old value is replaced by the specified value.
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            the specified key.
     * @param value
     *            value to be associated with the specified key.
     * @return previous value associated with {@code key} or {@code null} if
     *         there was no mapping for {@code key}.
     */
    V getAndPut(CacheName name, K key, V value);

    /**
     * Atomically removes the entry for a specified key in cache with the
     * specified cache name only if currently mapped to some value.
     * <p>
     * This is equivalent to:
     *
     * <pre>
     * <code>
     * if (cache.containsKey(key)) {
     *   V oldValue = cache.get(key);
     *   cache.remove(key);
     *   return oldValue;
     * } else {
     *   return null;
     * }
     * </code>
     * </pre>
     *
     * except that the action is performed atomically.
     * <p>
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            remove the mapping for this key.
     * @return value if one existed or null if no mapping existed for this key.
     */
    V getAndRemove(CacheName name, K key);

    /**
     * Associates the specified value with the specified key in cache with
     * specified cache name. If the cache previously contained a mapping for the
     * key, the old value is replaced by the specified value.
     * <p>
     * This method is preferred to {@link #getAndPut(CacheName, Object, Object)}
     * if the old value is not needed.
     * <p>
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            the specified key.
     * @param value
     *            the value to associate with the key.
     */
    void put(CacheName name, K key, V value);

    /**
     * If the specified key is not already associated with a value, associate it
     * with the given value.
     * <p>
     * This is equivalent to
     *
     * <pre>
     * {@code
     * if (!cache.containsKey(key))
     *   return cache.put(key, value);
     * else
     *   return cache.get(key);
     * }
     * </pre>
     *
     * except that the action is performed atomically.
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     */
    V putIfAbsent(CacheName name, K key, V value);

    /**
     * Removes the mapping for a key from cache with specified cache name if it
     * is present.
     * <p>
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            key whose mapping is to be removed from the cache.
     */
    void remove(CacheName name, K key);

    /**
     * Removes all of the mappings from cache with specified cache name. The
     * order that the individual entries are removed is undefined.
     * <p>
     *
     * @param name
     *            the specified cache name.
     */
    void removeAll(CacheName name);

    IMap<K, V> findCacheByName(CacheName name);
}
