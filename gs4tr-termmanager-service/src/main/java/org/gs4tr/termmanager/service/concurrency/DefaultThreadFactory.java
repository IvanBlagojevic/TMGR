package org.gs4tr.termmanager.service.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory for scheduled service (customizes thread name)
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL = new AtomicInteger(1);

    private final ThreadGroup _group;

    private final String _prefix;

    private final AtomicInteger _threadNumber = new AtomicInteger(1);

    public DefaultThreadFactory(String namePrefix) {
	SecurityManager s = System.getSecurityManager();
	_group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	_prefix = namePrefix + "-" + POOL.getAndIncrement() //$NON-NLS-1$
		+ "-thread-"; //$NON-NLS-1$
    }

    @Override
    public Thread newThread(Runnable r) {
	Thread t = new Thread(_group, r, _prefix + _threadNumber.getAndIncrement(), 0);
	t.setDaemon(false);

	if (t.getPriority() != Thread.NORM_PRIORITY) {
	    t.setPriority(Thread.NORM_PRIORITY);
	}
	return t;
    }
}
