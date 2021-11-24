package org.gs4tr.termmanager.dao.backup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.termmanager.model.glossary.backup.DbBaseTermEntry;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BackupDAOImpl<T extends DbBaseTermEntry> extends AbstractHibernateGenericDao<T, String> {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    protected BackupDAOImpl(Class<T> clazz) {
	super(clazz, String.class);
    }

    protected void doInTransaction(TransactionConsumer callable) throws BackupException {
	Session session = null;
	Transaction txn = null;
	try {
	    session = getSessionFactory().openSession();
	    callable.beforeTransactionCompletion();
	    txn = session.beginTransaction();

	    callable.accept(session);
	    txn.commit();
	    session.flush();
	    session.clear();
	} catch (Exception e) {
	    if (txn != null && txn.isActive()) {
		txn.rollback();
	    }
	    throw new BackupException(e.getMessage(), e);
	} finally {
	    callable.afterTransactionCompletion();
	    if (session != null) {
		session.close();
	    }
	}
    }

    protected <T extends DbBaseTermEntry> T findTermEntryMatch(String id, List<T> entries) {
	return entries.stream().filter(e -> id.equals(e.getUuId())).findFirst().get();
    }

    protected <T extends DbBaseTermEntry> Map<String, List<T>> groupEntriesById(Collection<T> incomingEntries) {
	Map<String, List<T>> grouped = new HashMap<>();
	for (T incoming : incomingEntries) {
	    grouped.computeIfAbsent(incoming.getUuId(), k -> new ArrayList<>()).add(incoming);
	}
	return grouped;
    }
}
