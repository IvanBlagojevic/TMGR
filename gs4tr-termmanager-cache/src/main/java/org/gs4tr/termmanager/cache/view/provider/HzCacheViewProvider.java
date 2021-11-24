package org.gs4tr.termmanager.cache.view.provider;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.cache.model.CacheView;

public interface HzCacheViewProvider {

    /**
     * Returns {@code CacheView} for cache with specified cache name.
     * {@code CacheView} is the statistics for the local portion of this
     * distributed cache (i.e <tt>IMap</tt>) and contains information such as
     * ownedEntryCount backupEntryCount, lastUpdateTime, lockedEntryCount.
     * 
     * @param name
     *            the specified cache name.
     * @return {@code CacheView} of the cache with specified cache name.
     */
    CacheView getCacheView(CacheName name);
}
