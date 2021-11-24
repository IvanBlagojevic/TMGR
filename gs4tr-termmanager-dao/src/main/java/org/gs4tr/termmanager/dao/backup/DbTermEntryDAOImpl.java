package org.gs4tr.termmanager.dao.backup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.model.glossary.backup.converter.DbTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermHistory;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("dbTermEntryDAO")
public class DbTermEntryDAOImpl extends BackupDAOImpl<DbTermEntry> implements DbTermEntryDAO {

    private static final String QUERY_BY_UUID = "select entry from DbTermEntry entry where entry.uuId = :entityId";

    private static final String QUERY_BY_UUIDS = "select entry from DbTermEntry entry where entry.uuId in (:entityIds)";

    private static final String QUERY_COUNT = "select count(entry.uuId) from DbTermEntry entry ";

    private static final String QUERY_UPDATE_TERM_LANGUAGE = "update DbTerm t set t.languageId = :languageTo where t.languageId"
	    + " = :languageFrom and t.termEntryUuid in(select te.uuId from DbTermEntry te where te.projectId = :projectId)";

    public DbTermEntryDAOImpl() {
	super(DbTermEntry.class);
    }

    @Override
    public DbTermEntry findByUUID(String uuid) {
	DbTermEntry[] dbTermEntries = new DbTermEntry[1];

	doInTransaction(session -> {
	    Query query = session.createQuery(QUERY_BY_UUID);
	    query.setString("entityId", uuid);

	    DbTermEntry dbTermEntry = (DbTermEntry) query.uniqueResult();

	    initChildEntities(dbTermEntry, true);

	    dbTermEntries[0] = dbTermEntry;
	});
	return dbTermEntries[0];
    }

    @Override
    public DbTermEntry findByUuid(final String uuid, boolean fetchChilds) {
	HibernateCallback<DbTermEntry> cb = session -> {
	    Query query = session.createQuery(QUERY_BY_UUID);
	    query.setString("entityId", uuid);
	    DbTermEntry result = (DbTermEntry) query.uniqueResult();
	    if (fetchChilds) {
		initChildEntities(result, true);
	    }
	    return result;
	};
	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DbTermEntry> findByUuids(Collection<String> uuids, boolean fetchChilds) {
	HibernateCallback<List<DbTermEntry>> cb = session -> {
	    Query query = session.createQuery(QUERY_BY_UUIDS);
	    query.setParameterList("entityIds", uuids);
	    return (List<DbTermEntry>) query.list();
	};
	List<DbTermEntry> result = execute(cb);
	if (fetchChilds) {
	    initChildEntities(result, true);
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagedList<DbTermEntry> getDbTermEntries(final PagedListInfo info, BackupSearchCommand command) {

	List<Long> projectIds = command.getProjectIds();
	List<String> shortCodes = command.getShortCodes();
	boolean fetchHistory = command.isFetchHistory();

	PagedList<DbTermEntry> pagedList = new PagedList<>();
	pagedList.setPagedListInfo(info);

	Session session = null;

	try {
	    session = getSessionFactory().openSession();
	    Criteria criteria = session.createCriteria(DbTermEntry.class);

	    if (CollectionUtils.isNotEmpty(projectIds)) {
		criteria.add(Restrictions.in("projectId", projectIds));
	    } else if (CollectionUtils.isNotEmpty(shortCodes)) {
		criteria.add(Restrictions.in("shortCode", shortCodes));
	    }

	    setScope(criteria, info.getIndex(), info.getSize());

	    List<DbTermEntry> list = criteria.list();
	    initChildEntities(list, fetchHistory);

	    pagedList.setTotalCount((long) list.size());
	    pagedList.setElements(list.toArray(new DbTermEntry[list.size()]));

	    session.clear();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    if (session != null) {
		session.close();
	    }
	}

	return pagedList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagedList<DbTermEntry> getDbTermEntriesForRecode(PagedListInfo info, BackupSearchCommand command) {
	List<Long> projectIds = command.getProjectIds();

	PagedList<DbTermEntry> pagedList = new PagedList<>();
	pagedList.setPagedListInfo(info);

	Session session = null;

	try {
	    session = getSessionFactory().openSession();
	    Criteria criteria = session.createCriteria(DbTermEntry.class);

	    if (CollectionUtils.isNotEmpty(projectIds)) {
		criteria.add(Restrictions.in("projectId", projectIds));
	    }

	    setScope(criteria, info.getIndex(), info.getSize());

	    List<DbTermEntry> list = criteria.list();
	    list.forEach(this::initTermEntryHistoryForRecode);

	    pagedList.setTotalCount((long) list.size());
	    pagedList.setElements(list.toArray(new DbTermEntry[list.size()]));

	    session.clear();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	} finally {
	    if (session != null) {
		session.close();
	    }
	}

	return pagedList;
    }

    @Override
    public long getTotalCount(List<Long> projectIds) {
	HibernateCallback<Long> cb = session -> {
	    StringBuilder builder = new StringBuilder(QUERY_COUNT);
	    if (CollectionUtils.isNotEmpty(projectIds)) {
		builder.append("where entry.projectId in (:projectIds)");
	    }
	    Query query = session.createQuery(builder.toString());
	    if (CollectionUtils.isNotEmpty(projectIds)) {
		query.setParameterList("projectIds", projectIds);
	    }

	    return (Long) query.uniqueResult();
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveOrUpdateLocked(Collection<DbTermEntry> incoming) throws BackupException {
	if (Objects.isNull(incoming)) {
	    return;
	}

	Map<String, List<DbTermEntry>> grouped = groupEntriesById(incoming);
	Map<String, DbTermEntry> merged = mergeEntries(grouped);

	doInTransaction(session -> {
	    List<DbTermEntry> incomingList = new ArrayList<>(merged.values());
	    Set<String> ids = merged.keySet();

	    Query query = session.createQuery(QUERY_BY_UUIDS);
	    query.setParameterList("entityIds", ids);

	    List<DbTermEntry> existingList = query.list();
	    if (CollectionUtils.isNotEmpty(existingList)) {
		for (DbTermEntry existing : existingList) {
		    DbTermEntry incomingEntry = findTermEntryMatch(existing.getUuId(), incomingList);
		    existing = DbTermEntryConverter.mergeWithExistingDbTermEntry(incomingEntry, existing);
		    session.update(existing);
		    incomingList.remove(incomingEntry);
		}
	    }

	    if (CollectionUtils.isNotEmpty(incomingList)) {
		for (DbTermEntry incomingEntry : incomingList) {
		    session.save(incomingEntry);
		}
	    }
	});
    }

    @Override
    public <T> void updateEntitiesForRecodeOrClone(Collection<T> entities) {
	doInTransaction(session -> {
	    for (T entity : entities) {
		session.update(entity);
	    }
	});
    }

    @Override
    public void updateTermLanguage(Long projectId, String languageFrom, String languageTo) {
	doInTransaction(session -> {
	    Query query = session.createQuery(QUERY_UPDATE_TERM_LANGUAGE);
	    query.setParameter("languageTo", languageTo);
	    query.setParameter("languageFrom", languageFrom);
	    query.setParameter("projectId", projectId);
	    query.executeUpdate();
	});
    }

    private void initChildEntities(DbTermEntry entry, boolean fetchHistory) {
	if (entry == null) {
	    return;
	}

	Set<DbTermEntryDescription> descriptions = entry.getDescriptions();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    Hibernate.initialize(descriptions);
	}

	Set<DbTerm> terms = entry.getTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    Hibernate.initialize(terms);

	    for (DbTerm term : terms) {
		Set<DbTermDescription> termDescs = term.getDescriptions();
		if (CollectionUtils.isNotEmpty(termDescs)) {
		    Hibernate.initialize(termDescs);
		}
	    }
	}

	if (fetchHistory) {
	    Set<DbTermEntryHistory> history = entry.getHistory();
	    if (CollectionUtils.isNotEmpty(history)) {
		Hibernate.initialize(history);
		for (DbTermEntryHistory dbTermEntryHistory : history) {
		    Set<DbTermHistory> termHistories = dbTermEntryHistory.getHistory();
		    if (CollectionUtils.isNotEmpty(termHistories)) {
			Hibernate.initialize(termHistories);
		    }
		}
	    }
	}
    }

    private void initChildEntities(List<DbTermEntry> entries, boolean fetchHistory) {
	if (CollectionUtils.isEmpty(entries)) {
	    return;
	}

	for (DbTermEntry entry : entries) {
	    initChildEntities(entry, fetchHistory);
	}
    }

    private void initTermEntryHistoryForRecode(DbTermEntry entry) {
	if (entry == null) {
	    return;
	}

	Set<DbTermEntryHistory> termEntryHistories = entry.getHistory();

	if (CollectionUtils.isNotEmpty(termEntryHistories)) {
	    Hibernate.initialize(termEntryHistories);

	    termEntryHistories.forEach(termEntryHistory -> {
		Set<DbTermHistory> termHistories = termEntryHistory.getHistory();

		if (CollectionUtils.isNotEmpty(termHistories)) {
		    Hibernate.initialize(termHistories);
		}
	    });
	}
    }

    private Map<String, DbTermEntry> mergeEntries(Map<String, List<DbTermEntry>> grouped) {
	Map<String, DbTermEntry> merged = new HashMap<>();

	for (Map.Entry<String, List<DbTermEntry>> entry : grouped.entrySet()) {
	    Iterator<DbTermEntry> iterator = entry.getValue().iterator();
	    DbTermEntry first = iterator.next();
	    while (iterator.hasNext()) {
		first = DbTermEntryConverter.mergeWithExistingDbTermEntry(iterator.next(), first);
	    }
	    merged.put(first.getUuId(), first);
	}
	return merged;
    }

    private void setScope(Criteria criteria, int index, int size) {
	criteria.setFirstResult(index * size);
	criteria.setMaxResults(size);
    }

    @Override
    protected String getFindByIdQueryName() {
	return "DbTermEntry.findById";
    }

    @Override
    protected String getFindByIdsQueryName() {
	return "DbTermEntry.findByIds";
    }
}
