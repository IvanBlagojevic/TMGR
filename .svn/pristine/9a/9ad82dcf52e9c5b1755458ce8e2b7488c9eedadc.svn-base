package org.gs4tr.termmanager.service.lock.manager;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.LogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

@Service("exclusiveWriteLockManagerImpl")
public class ExclusiveWriteLockManagerImpl implements ExclusiveWriteLockManager {

    private static final int LEASE_TIME_MINUTES = 1; // $NON-NLS-1$

    private static final String LOCKS_MAP_NAME = "locks"; //$NON-NLS-1$

    private static final Log LOG = LogFactory.getLog(ExclusiveWriteLockManagerImpl.class);

    @Autowired
    private HazelcastInstance _hzInstance;

    @Override
    public void acquireLock(Object lockKey, String owner) {
	getHzInstance().getMap(LOCKS_MAP_NAME).lock(lockKey, LEASE_TIME_MINUTES, TimeUnit.MINUTES);
	LogHelper.debug(LOG, String.format(Messages.getString("ExclusiveWriteLockManagerImpl.1"), owner)); //$NON-NLS-1$
    }

    @Override
    public void releaseLock(Object lockKey, String owner) {
	getHzInstance().getMap(LOCKS_MAP_NAME).unlock(lockKey);
	LogHelper.debug(LOG, String.format(Messages.getString("ExclusiveWriteLockManagerImpl.2"), owner)); //$NON-NLS-1$
    }

    private HazelcastInstance getHzInstance() {
	return _hzInstance;
    }
}
