package org.gs4tr.termmanager.cache.entity.view.provider;

import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.cache.model.EntityView;

public interface HzEntityViewProvider {

    /**
     * Returns the <tt>EntityView</tt> for the specified key.
     * 
     * @param name
     *            the specified cache name.
     * @param key
     *            the key of the entry.
     * @return <tt>EntityView</tt> of the specified key.
     * @throws NullPointerException
     *             if the specified key is null.
     * @see EntityView
     */
    <K, V> EntityView<K, V> getEntityView(CacheName name, K key);
}
