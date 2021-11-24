package org.gs4tr.termmanager.io.tlog.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.exception.TransactionError;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;

import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;

@Component
public class PersistentStoreHandler implements InitializingBean, DisposableBean {

    private static final Log LOGGER = LogFactory.getLog(PersistentStoreHandler.class);

    private static final String TMP = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$

    private GuavaCache storeCache;

    @Override
    public void afterPropertiesSet() throws Exception {
	storeCache = new GuavaCache("stores", CacheBuilder.newBuilder().build());
    }

    public void closeAndClear(long projectId) {
	PersistentEntityStore store = storeCache.get(projectId, PersistentEntityStore.class);
	if (Objects.nonNull(store)) {
	    try {
		store.close();
	    } catch (Exception e) {
		LOGGER.warn(e.getMessage());
	    } finally {
		cleanTempStore(projectId);
	    }
	}
    }

    @Override
    public void destroy() throws Exception {
	storeCache.clear();
    }

    public PersistentEntityStore getOrOpen(long projectId) throws TransactionError {
	PersistentEntityStore store = storeCache.get(projectId, PersistentEntityStore.class);
	if (reOpen(store)) {
	    String dir = TMP.concat(File.separator).concat(String.valueOf(projectId));
	    store = PersistentEntityStores.newInstance(dir);
	    storeCache.put(projectId, store);
	}

	return store;
    }

    public boolean isLocked(long projectId) {
	PersistentEntityStore store = storeCache.get(projectId, PersistentEntityStore.class);
	return Objects.nonNull(store) && store.getEnvironment().isOpen();
    }

    public PersistentEntityStore open(long projectId) throws TransactionError {
	String dir = TMP.concat(File.separator).concat(String.valueOf(projectId));
	PersistentEntityStore store = PersistentEntityStores.newInstance(dir);
	storeCache.put(projectId, store);

	return store;
    }

    private void cleanTempStore(Long projectId) {
	try {
	    Path path = Paths.get(TMP, String.valueOf(projectId));
	    File file = path.toFile();
	    if (file.exists()) {
		FileUtils.forceDelete(file);
	    }
	} catch (IOException e) {
	    LOGGER.error(e.getMessage(), e);
	}
    }

    private boolean isOpen(PersistentEntityStore store) {
	return store.getEnvironment().isOpen();
    }

    private boolean reOpen(PersistentEntityStore store) {
	return Objects.isNull(store) || !isOpen(store);
    }
}
