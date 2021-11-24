package org.gs4tr.termmanager.dao.backup;

import java.sql.SQLException;
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
import org.gs4tr.termmanager.model.glossary.backup.converter.DbSubmissionTermEntryConverter;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbComment;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryDescription;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntryHistory;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermHistory;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("dbSubmissionTermEntryDAO")
public class DbSubmissionTermEntryDAOImpl extends BackupDAOImpl<DbSubmissionTermEntry>
	implements DbSubmissionTermEntryDAO {

    private static final String QUERY_BY_UUID = "select entry from DbSubmissionTermEntry entry where entry.uuId = :entityId";

    private static final String QUERY_BY_UUIDS = "select entry from DbSubmissionTermEntry entry where entry.uuId in (:entityIds)";

    private static final String QUERY_COUNT = "select count(entry.uuId) from DbSubmissionTermEntry entry ";

    public DbSubmissionTermEntryDAOImpl() {
	super(DbSubmissionTermEntry.class);
    }

    @Override
    public DbSubmissionTermEntry findByUuid(String uuid, boolean fetchChilds) {
	HibernateCallback<DbSubmissionTermEntry> cb = session -> {
	    Query query = session.createQuery(QUERY_BY_UUID);
	    query.setString("entityId", uuid);
	    return (DbSubmissionTermEntry) query.uniqueResult();
	};

	DbSubmissionTermEntry entry = execute(cb);
	if (fetchChilds) {
	    initChildEntities(entry, true);
	}
	return entry;
    }

    @Override
    public DbSubmissionTermEntry findByUuid(String uuid) {
	DbSubmissionTermEntry[] dbSubmissionTermEntries = new DbSubmissionTermEntry[1];

	doInTransaction(session -> {
	    Query query = session.createQuery(QUERY_BY_UUID);
	    query.setString("entityId", uuid);

	    DbSubmissionTermEntry dbSubmissionTermEntry = (DbSubmissionTermEntry) query.uniqueResult();

	    initChildEntities(dbSubmissionTermEntry, true);

	    dbSubmissionTermEntries[0] = dbSubmissionTermEntry;
	});
	return dbSubmissionTermEntries[0];
    }

    @Override
    public List<DbSubmissionTermEntry> findByUuids(Collection<String> uuids, boolean fetchChilds) {
	HibernateCallback<List<DbSubmissionTermEntry>> cb = session -> {
	    Query query = session.createQuery(QUERY_BY_UUIDS);
	    query.setParameterList("entityIds", uuids);
	    return (List<DbSubmissionTermEntry>) query.list();
	};

	List<DbSubmissionTermEntry> list = execute(cb);
	if (fetchChilds) {
	    initChildEntities(list, true);
	}

	return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagedList<DbSubmissionTermEntry> getDbSubmissionTermEntries(PagedListInfo info,
	    BackupSearchCommand command) {
	List<Long> projectIds = command.getProjectIds();
	List<String> shortCodes = command.getShortCodes();
	boolean fetchHistory = command.isFetchHistory();

	PagedList<DbSubmissionTermEntry> pagedList = new PagedList<>();
	pagedList.setPagedListInfo(info);

	Session session = null;
	try {
	    session = getSessionFactory().openSession();
	    Criteria criteria = session.createCriteria(DbSubmissionTermEntry.class);

	    if (CollectionUtils.isNotEmpty(projectIds)) {
		criteria.add(Restrictions.in("projectId", projectIds));
	    } else if (CollectionUtils.isNotEmpty(shortCodes)) {
		criteria.add(Restrictions.in("shortCode", shortCodes));
	    }

	    setScope(criteria, info.getIndex(), info.getSize());

	    List<DbSubmissionTermEntry> list = criteria.list();
	    initChildEntities(list, fetchHistory);

	    pagedList.setTotalCount((long) list.size());
	    pagedList.setElements(list.toArray(new DbSubmissionTermEntry[list.size()]));

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
    public PagedList<DbSubmissionTermEntry> getDbSubmissionTermEntriesForRecode(PagedListInfo info,
	    BackupSearchCommand command) {
	List<Long> projectIds = command.getProjectIds();

	PagedList<DbSubmissionTermEntry> pagedList = new PagedList<>();
	pagedList.setPagedListInfo(info);

	Session session = null;
	try {
	    session = getSessionFactory().openSession();
	    Criteria criteria = session.createCriteria(DbSubmissionTermEntry.class);

	    if (CollectionUtils.isNotEmpty(projectIds)) {
		criteria.add(Restrictions.in("projectId", projectIds));
	    }

	    setScope(criteria, info.getIndex(), info.getSize());

	    List<DbSubmissionTermEntry> list = criteria.list();
	    list.forEach(this::initDbSubmissionTermEntryHistoryForRecode);

	    pagedList.setTotalCount((long) list.size());
	    pagedList.setElements(list.toArray(new DbSubmissionTermEntry[list.size()]));

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

    @Override
    public void saveOrUpdateLocked(Collection<DbSubmissionTermEntry> incoming) throws BackupException {
	if (Objects.isNull(incoming)) {
	    return;
	}

	Map<String, List<DbSubmissionTermEntry>> grouped = groupEntriesById(incoming);
	Map<String, DbSubmissionTermEntry> merged = mergeEntries(grouped);

	doInTransaction(session -> {
	    List<DbSubmissionTermEntry> incomingList = new ArrayList<>(merged.values());
	    Set<String> ids = merged.keySet();

	    Query query = session.createQuery(QUERY_BY_UUIDS);
	    query.setParameterList("entityIds", ids);

	    List<DbSubmissionTermEntry> existingList = query.list();
	    if (CollectionUtils.isNotEmpty(existingList)) {
		for (DbSubmissionTermEntry existing : existingList) {
		    DbSubmissionTermEntry incomingEntry = findTermEntryMatch(existing.getUuId(), incomingList);
		    existing = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(incomingEntry, existing);
		    session.update(existing);
		    incomingList.remove(incomingEntry);
		}
	    }

	    if (CollectionUtils.isNotEmpty(incomingList)) {
		for (DbSubmissionTermEntry incomingEntry : incomingList) {
		    session.save(incomingEntry);
		}
	    }
	});
    }

    @Override
    public int updateSubmissionTermLanguagesByProjectId(String languageFrom, String languageTo, Long projectId) {
	HibernateCallback<Integer> cb = new HibernateCallback<Integer>() {
	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("DbSubmissionTermEntry.updateSubmissionTermLanguages");
		query.setParameter("languageFrom", languageFrom);
		query.setParameter("languageTo", languageTo);
		query.setParameter("projectId", projectId);

		return query.executeUpdate();
	    }
	};

	return execute(cb);
    }

    private void initChildEntities(DbSubmissionTermEntry entry, boolean fetchHistory) {
	if (entry == null) {
	    return;
	}

	Set<DbSubmissionTermEntryDescription> descriptions = entry.getDescriptions();
	if (CollectionUtils.isNotEmpty(descriptions)) {
	    Hibernate.initialize(descriptions);
	}

	Set<DbSubmissionTerm> terms = entry.getSubmissionTerms();
	if (CollectionUtils.isNotEmpty(terms)) {
	    Hibernate.initialize(terms);

	    for (DbSubmissionTerm term : terms) {
		Set<DbSubmissionTermDescription> termDescs = term.getDescriptions();
		if (CollectionUtils.isNotEmpty(termDescs)) {
		    Hibernate.initialize(termDescs);
		}

		Set<DbComment> comments = term.getComments();
		if (CollectionUtils.isNotEmpty(comments)) {
		    for (DbComment comment : comments) {
			Hibernate.initialize(comment);
		    }
		}
	    }
	}

	if (fetchHistory) {
	    Set<DbSubmissionTermEntryHistory> history = entry.getHistory();
	    if (CollectionUtils.isNotEmpty(history)) {
		Hibernate.initialize(history);
		for (DbSubmissionTermEntryHistory dbSubmissionTermEntryHistory : history) {
		    Set<DbSubmissionTermHistory> termHistories = dbSubmissionTermEntryHistory.getHistory();
		    if (CollectionUtils.isNotEmpty(termHistories)) {
			Hibernate.initialize(termHistories);
		    }
		}
	    }
	}
    }

    private void initChildEntities(List<DbSubmissionTermEntry> entries, boolean fetchHistory) {
	if (CollectionUtils.isEmpty(entries)) {
	    return;
	}

	for (DbSubmissionTermEntry entry : entries) {
	    initChildEntities(entry, fetchHistory);
	}
    }

    private void initDbSubmissionTermEntryHistoryForRecode(DbSubmissionTermEntry entry) {
	if (entry == null) {
	    return;
	}
	Set<DbSubmissionTermEntryHistory> histories = entry.getHistory();

	if (CollectionUtils.isNotEmpty(histories)) {
	    Hibernate.initialize(histories);

	    histories.forEach(history -> {
		Set<DbSubmissionTermHistory> termHistories = history.getHistory();

		if (CollectionUtils.isNotEmpty(termHistories)) {
		    Hibernate.initialize(termHistories);
		}
	    });
	}
    }

    private Map<String, DbSubmissionTermEntry> mergeEntries(Map<String, List<DbSubmissionTermEntry>> grouped) {
	Map<String, DbSubmissionTermEntry> merged = new HashMap<>();

	for (Map.Entry<String, List<DbSubmissionTermEntry>> entry : grouped.entrySet()) {
	    Iterator<DbSubmissionTermEntry> iterator = entry.getValue().iterator();
	    DbSubmissionTermEntry first = iterator.next();
	    while (iterator.hasNext()) {
		first = DbSubmissionTermEntryConverter.mergeWithExistingDbTermEntry(iterator.next(), first);
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
	return "DbSubmissionTermEntry.findById";
    }

    @Override
    protected String getFindByIdsQueryName() {
	return "DbSubmissionTermEntry.findByIds";
    }
}