package org.gs4tr.termmanager.cache.entry.processor.executor;

import java.util.Map;

import org.gs4tr.termmanager.cache.model.CacheName;

import com.hazelcast.map.EntryProcessor;

public interface HzEntryProcessorExecutor {

    /**
     * Applies the user defined <tt>EntryProcessor</tt> to the all entries in
     * the cache with specified cache name.
     * <p/>
     * 
     * @param name
     *            the specified cache name.
     * @param command
     *            an user defined <tt>EntryProcessor</tt>.
     * @return the results mapped by each key in the cache.
     */
    <K, V> Map<K, Object> executeOnEntries(CacheName name, EntryProcessor<K, V> command);

    /**
     * Applies the user defined <tt>EntryProcessor</tt> to the entry mapped by
     * the key in the cache with specified cache name. Returns the the object
     * which is result of the process() method of EntryProcessor.
     * <p/>
     *
     * @param name
     *            the specified cache name.
     * @param key
     *            the specified key.
     * @param command
     *            an user defined <tt>EntryProcessor</tt>.
     * @return the result of the processing, if any.
     * 
     * @throws NullPointerException
     *             if the specified key is null.
     */
    <K, V> Object executeOnKey(CacheName name, K key, EntryProcessor<K, V> command);
}
