package org.gs4tr.termmanager.service.termentry.synchronization;

import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;

public enum TermEntrySynchronizerFactory {
    INSTANCE;

    public ITermEntrySynchronizer createTermEntrySynchronizer(SyncOption sync) throws IllegalArgumentException {
	try {
	    return (ITermEntrySynchronizer) sync.getClassName().newInstance();
	} catch (Exception ex) {
	    throw new IllegalArgumentException(String.format(Messages.getString("TermEntrySynchronizerFactory.0"), //$NON-NLS-1$
		    sync.getClassName()), ex);
	}
    }
}
