package org.gs4tr.termmanager.dao.backup;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

@Repository("regularBackupCleanerDAO")
public class RegularBackupCleanerDAOImpl extends BackupDAOImpl<DbTermEntry> implements RegularBackupCleanerDAO {

    /* delete terms */
    private static final String DELETE_TERMS = "DELETE FROM TM_TERM WHERE TERM_ID IN (:termIds)";
    private static final String DELETE_TERM_DESCRIPTIONS = "DELETE FROM TM_TERM_DESCRIPTION WHERE TERM_ID IN (:termIds)";
    /* delete termEntries */
    private static final String DELETE_TERM_ENTRIES = "DELETE FROM TM_TERMENTRY WHERE TERMENTRY_ID IN (:entryIds)";
    private static final String DELETE_TERM_ENTRY_DESCRIPTIONS = "DELETE FROM TM_TERMENTRY_DESCRIPTION WHERE TERMENTRY_ID IN (:entryIds)";
    private static final String DELETE_TERM_ENTRY_HISTORY = "DELETE FROM TM_TERMENTRY_HISTORY WHERE TERMENTRY_HISTORY_ID IN (:termEntryHistoryIds)";
    private static final String DELETE_TERM_HISTORY = "DELETE FROM TM_TERM_HISTORY WHERE TERMENTRY_HISTORY_ID IN (:termEntryHistoryIds)";
    /* hidden terms */
    private static final String GET_HIDDEN_TERM_IDS_GET = "SELECT TERM_ID FROM TM_TERM "
	    + "INNER JOIN TM_TERMENTRY ON TM_TERM.TERMENTRY_ID = TM_TERMENTRY.TERMENTRY_ID "
	    + "WHERE TM_TERM.LANGUAGE_ID NOT IN (SELECT TM_PROJECT_LANGUAGE.LANGUAGE_ID FROM TM_PROJECT_LANGUAGE "
	    + "WHERE TM_TERMENTRY.PROJECT_ID = TM_PROJECT_LANGUAGE.PROJECT_ID "
	    + "AND TM_TERM.LANGUAGE_ID = TM_PROJECT_LANGUAGE.LANGUAGE_ID)";
    /*
     * select project detail, project language detail and project user language ids
     */
    private static final String GET_PROJECT_DETAIL_IDS = "SELECT PROJECT_DETAIL_ID FROM TM_PROJECT_DETAIL WHERE PROJECT_ID IN (:projectIds)";
    private static final String GET_PROJECT_LANGUAGE_DETAIL_IDS = "SELECT PROJECT_LANGUAGE_DETAIL_ID FROM TM_PROJECT_LANGUAGE_DETAIL WHERE PROJECT_DETAIL_ID IN (:projectDetailIds)";
    private static final String GET_PROJECT_USER_LANGUAGE_IDS = "SELECT PROJECT_USER_LANGUAGE_ID FROM TM_PROJECT_USER_LANGUAGE WHERE PROJECT_ID IN (:projectIds)";
    /* select termEntry and term ids */
    private static final String GET_TERM_ENTRY_HISTORY_IDS = "SELECT TERMENTRY_HISTORY_ID FROM TM_TERMENTRY_HISTORY WHERE TERMENTRY_ID IN (:entryIds)";
    private static final String GET_TERM_ENTRY_IDS_BY_PROJECT_IDS = "SELECT TERMENTRY_ID FROM TM_TERMENTRY WHERE PROJECT_ID IN (:projectIds)";
    private static final String GET_TERM_ENTRY_WITHOUT_TERMS = "SELECT TM_TERMENTRY.TERMENTRY_ID FROM TM_TERMENTRY WHERE ((SELECT COUNT(TM_TERM.TERM_ID) FROM TM_TERM WHERE TM_TERM.TERMENTRY_ID = TM_TERMENTRY.TERMENTRY_ID) = 0)";
    private static final String GET_TERM_IDS_BY_TERM_ENTRY_IDS = "SELECT TERM_ID FROM TM_TERM WHERE TERMENTRY_ID IN (:entryIds)";
    /* clear counts and statistics */
    private static final String UPDATE_PROJECT_DETAIL = "UPDATE TM_PROJECT_DETAIL SET ACTIVE_SUBMISSION_COUNT = 0, APPROVE_TERM_COUNT = 0, COMPLETED_SUBMISSION_COUNT = 0, DATE_MODIFIED =:dateModified, FORBIDDEN_TERM_COUNT = 0, ON_HOLD_TERM_COUNT = 0, PENDING_APPROVAL_TERM_COUNT = 0, TERM_COUNT = 0, TERMENTRY_COUNT = 0, TERM_IN_SUBMISSION_COUNT = 0, LANGUAGE_COUNT = 0 WHERE PROJECT_DETAIL_ID IN (:projectDetailIds)";
    private static final String UPDATE_PROJECT_LANGUAGE_DETAIL = "UPDATE TM_PROJECT_LANGUAGE_DETAIL SET ACTIVE_SUBMISSION_COUNT = 0, APPROVE_TERM_COUNT = 0, COMPLETED_SUBMISSION_COUNT = 0, DATE_MODIFIED =:dateModified, FORBIDDEN_TERM_COUNT = 0, ON_HOLD_TERM_COUNT = 0, PENDING_APPROVAL_TERM_COUNT = 0, TERM_COUNT = 0, TERMENTRY_COUNT = 0, TERM_IN_SUBMISSION_COUNT = 0 WHERE PROJECT_LANGUAGE_DETAIL_ID IN (:projectLanguageDetailIds)";
    private static final String UPDATE_PROJECT_LANGUAGE_USER_DETAIL = "UPDATE TM_PROJECT_LANGUAGE_USER_DETAIL SET ACTIVE_SUBMISSION_COUNT = 0, COMPLETED_SUBMISSION_COUNT = 0 WHERE PROJECT_LANGUAGE_DETAIL_ID IN (:projectLanguageDetailIds)";
    private static final String UPDATE_PROJECT_USER_DETAIL = "UPDATE TM_PROJECT_USER_DETAIL SET ACTIVE_SUBMISSION_COUNT = 0, COMPLETED_SUBMISSION_COUNT = 0, DATE_MODIFIED =:dateModified, TERMENTRY_COUNT = 0 WHERE PROJECT_DETAIL_ID IN (:projectDetailIds)";
    private static final String UPDATE_STATISTICS = "UPDATE TM_STATISTICS SET ADDED_APPROVED_COUNT = 0, ADDED_BLACKLISTED_COUNT = 0, ADDED_ON_HOLD_COUNT = 0, ADDED_PENDING_COUNT = 0, APPROVED_COUNT = 0, BLACKLISTED_COUNT = 0, DELETED_COUNT = 0, DEMOTED_COUNT = 0, ON_HOLD_COUNT = 0, UPDATED_COUNT = 0 WHERE PROJECT_USER_LANGUAGE_ID IN (:projectUserLanguageIds)";

    public RegularBackupCleanerDAOImpl() {
	super(DbTermEntry.class);
    }

    @Override
    public void clearCountsAndStatistics(Collection<Long> projectIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(projectIds, chunk -> {

	    Date dateModified = new Date(System.currentTimeMillis());
	    List<Long> projectDetailIds = getProjectDetailIds(chunk);
	    List<Long> projectLanguageDetailIds = getProjectLanguageDetailIds(projectDetailIds);
	    List<Long> projectUserLanguageIds = getProjectUserLanguageIds(chunk);

	    updateProjectDetail(projectDetailIds, dateModified);
	    updateProjectLanguageDetail(projectLanguageDetailIds, dateModified);
	    updateProjectLanguageUserDetail(projectLanguageDetailIds);
	    updateProjectUserDetail(projectDetailIds, dateModified);
	    updateStatistics(projectUserLanguageIds);

	}, chunkSize);

    }

    @Override
    public void deleteByProjectIds(Collection<Long> projectIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(projectIds, chunk -> {
	    List<Long> entryIDS = getTermEntryIds(chunk);
	    if (isNotEmpty(entryIDS)) {
		deleteTermEntryChilds(entryIDS, chunkSize);
		deleteTerms(entryIDS, chunkSize);
		deleteTermEntries(entryIDS, chunkSize);
	    }
	}, chunkSize);
    }

    @Override
    public void deleteHiddenTerms(int chunkSize) {
	List<Long> termIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery queryGetHiddenTerms = session.createSQLQuery(GET_HIDDEN_TERM_IDS_GET);
	    List<Long> hiddenTermIds = queryGetHiddenTerms.list();
	    if (isNotEmpty(hiddenTermIds)) {
		termIds.addAll(hiddenTermIds);
	    }
	});

	deleteTermChilds(termIds, chunkSize);
	deleteTermEntriesWithOutTerms(chunkSize);
    }

    private void deleteTermChilds(List<Long> termIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(termIds, chunk -> {
	    deleteTermDescriptions(chunk);
	    deleteTerms(chunk);
	}, chunkSize);
    }

    private void deleteTermDescriptions(List<Long> termIds) {
	doInTransaction(session -> {
	    SQLQuery queryDeleteTermDescriptions = session.createSQLQuery(DELETE_TERM_DESCRIPTIONS);
	    queryDeleteTermDescriptions.setParameterList("termIds", termIds);
	    queryDeleteTermDescriptions.executeUpdate();
	});
    }

    private void deleteTermEntries(List<Long> entryIDs, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(entryIDs, this::deleteTermEntries, chunkSize);
    }

    private void deleteTermEntries(List<Long> entryIds) {
	doInTransaction(session -> {
	    SQLQuery queryDeleteTermEntries = session.createSQLQuery(DELETE_TERM_ENTRIES);
	    queryDeleteTermEntries.setParameterList("entryIds", entryIds);
	    queryDeleteTermEntries.executeUpdate();
	});
    }

    private void deleteTermEntriesWithOutTerms(int chunkSize) {
	List<Long> termEntryIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery queryGetTermEntriesWithoutTerms = session.createSQLQuery(GET_TERM_ENTRY_WITHOUT_TERMS);
	    queryGetTermEntriesWithoutTerms.setReadOnly(true);
	    List<Long> hiddenTermEntryIds = queryGetTermEntriesWithoutTerms.list();
	    if (isNotEmpty(hiddenTermEntryIds)) {
		termEntryIds.addAll(hiddenTermEntryIds);
	    }
	});

	deleteTermEntryChilds(termEntryIds, chunkSize);
	deleteTermEntries(termEntryIds, chunkSize);
    }

    private void deleteTermEntryChilds(List<Long> entryIDS, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(entryIDS, chunk -> {
	    deleteTermEntryDescriptions(chunk);
	    List<Long> entryHistoryIds = getTermEntryHistoryIds(chunk);
	    if (isNotEmpty(entryHistoryIds)) {
		ChunkedExecutionHelper.executeChuncked(entryHistoryIds, chunkOfEntryHistoryIds -> {
		    deleteTermHistory(chunkOfEntryHistoryIds);
		    deleteTermEntryHistory(chunkOfEntryHistoryIds);
		}, chunkSize);
	    }
	}, chunkSize);
    }

    private void deleteTermEntryDescriptions(List<Long> entryIds) {
	doInTransaction(session -> {
	    SQLQuery queryDeleteTermEntryDescriptions = session.createSQLQuery(DELETE_TERM_ENTRY_DESCRIPTIONS);
	    queryDeleteTermEntryDescriptions.setParameterList("entryIds", entryIds);
	    queryDeleteTermEntryDescriptions.executeUpdate();
	});
    }

    private void deleteTermEntryHistory(List<Long> entryHistoryIds) {
	doInTransaction(session -> {
	    SQLQuery queryDeleteTermEntryHistory = session.createSQLQuery(DELETE_TERM_ENTRY_HISTORY);
	    queryDeleteTermEntryHistory.setParameterList("termEntryHistoryIds", entryHistoryIds);
	    queryDeleteTermEntryHistory.executeUpdate();
	});
    }

    private void deleteTermHistory(List<Long> entryHistoryIds) {
	doInTransaction(session -> {
	    SQLQuery queryDeleteTermHistory = session.createSQLQuery(DELETE_TERM_HISTORY);
	    queryDeleteTermHistory.setParameterList("termEntryHistoryIds", entryHistoryIds);
	    queryDeleteTermHistory.executeUpdate();
	});
    }

    private void deleteTerms(List<Long> termIds) {
	doInTransaction(session -> {
	    SQLQuery queryDeleteTerms = session.createSQLQuery(DELETE_TERMS);
	    queryDeleteTerms.setParameterList("termIds", termIds);
	    queryDeleteTerms.executeUpdate();
	});
    }

    private void deleteTerms(List<Long> entryIDs, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(entryIDs, chunk -> {
	    List<Long> termIds = getTermIds(chunk);
	    if (isNotEmpty(termIds)) {
		deleteTermChilds(termIds, chunkSize);
	    }
	}, chunkSize);
    }

    private List<Long> getProjectDetailIds(Collection<Long> projectIds) {
	List<Long> projectDetailIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery getProjectIdsQuery = session.createSQLQuery(GET_PROJECT_DETAIL_IDS);
	    getProjectIdsQuery.setParameterList("projectIds", projectIds);
	    getProjectIdsQuery.setReadOnly(true);
	    projectDetailIds.addAll(getProjectIdsQuery.list());

	});

	return projectDetailIds;
    }

    private List<Long> getProjectLanguageDetailIds(List<Long> projectDetailIds) {
	List<Long> projectLanguageDetailIds = new ArrayList<>();
	doInTransaction(session -> {

	    SQLQuery query = session.createSQLQuery(GET_PROJECT_LANGUAGE_DETAIL_IDS);
	    query.setParameterList("projectDetailIds", projectDetailIds);
	    query.setReadOnly(true);
	    projectLanguageDetailIds.addAll(query.list());

	});

	return projectLanguageDetailIds;
    }

    private List<Long> getProjectUserLanguageIds(Collection<Long> projectIds) {
	List<Long> projectUserLanguageIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(GET_PROJECT_USER_LANGUAGE_IDS);
	    query.setParameterList("projectIds", projectIds);
	    query.setReadOnly(true);
	    projectUserLanguageIds.addAll(query.list());
	});

	return projectUserLanguageIds;
    }

    private List<Long> getTermEntryHistoryIds(List<Long> entryIds) {
	final List<Long> termEntryHistoryIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery queryGetTermEntryHistoryIds = session.createSQLQuery(GET_TERM_ENTRY_HISTORY_IDS);
	    queryGetTermEntryHistoryIds.setParameterList("entryIds", entryIds);
	    queryGetTermEntryHistoryIds.setReadOnly(true);
	    termEntryHistoryIds.addAll(queryGetTermEntryHistoryIds.list());
	});
	return termEntryHistoryIds;
    }

    private List<Long> getTermEntryIds(Collection<Long> projectIds) {
	final List<Long> termEntryIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery queryGetTermEntryIds = session.createSQLQuery(GET_TERM_ENTRY_IDS_BY_PROJECT_IDS);
	    queryGetTermEntryIds.setParameterList("projectIds", projectIds);
	    queryGetTermEntryIds.setReadOnly(true);
	    termEntryIds.addAll(queryGetTermEntryIds.list());
	});
	return termEntryIds;
    }

    private List<Long> getTermIds(List<Long> entryIds) {
	final List<Long> termIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery queryGetTermIDS = session.createSQLQuery(GET_TERM_IDS_BY_TERM_ENTRY_IDS);
	    queryGetTermIDS.setParameterList("entryIds", entryIds);
	    queryGetTermIDS.setReadOnly(true);
	    termIds.addAll(queryGetTermIDS.list());
	});
	return termIds;
    }

    private void updateProjectDetail(Collection<Long> projectDetailIds, Date dateModified) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(UPDATE_PROJECT_DETAIL);
	    query.setParameter("dateModified", dateModified);
	    query.setParameterList("projectDetailIds", projectDetailIds);
	    query.executeUpdate();
	});
    }

    private void updateProjectLanguageDetail(Collection<Long> projectLanguageDetailIds, Date dateModified) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(UPDATE_PROJECT_LANGUAGE_DETAIL);
	    query.setParameter("dateModified", dateModified);
	    query.setParameterList("projectLanguageDetailIds", projectLanguageDetailIds);
	    query.executeUpdate();
	});

    }

    private void updateProjectLanguageUserDetail(List<Long> projectLanguageDetailIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(UPDATE_PROJECT_LANGUAGE_USER_DETAIL);
	    query.setParameterList("projectLanguageDetailIds", projectLanguageDetailIds);
	    query.executeUpdate();
	});
    }

    private void updateProjectUserDetail(List<Long> projectDetailIds, Date dateModified) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(UPDATE_PROJECT_USER_DETAIL);
	    query.setParameter("dateModified", dateModified);
	    query.setParameterList("projectDetailIds", projectDetailIds);
	    query.executeUpdate();
	});

    }

    private void updateStatistics(List<Long> projectUserLanguageIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(UPDATE_STATISTICS);
	    query.setParameterList("projectUserLanguageIds", projectUserLanguageIds);
	    query.executeUpdate();
	});
    }

}
