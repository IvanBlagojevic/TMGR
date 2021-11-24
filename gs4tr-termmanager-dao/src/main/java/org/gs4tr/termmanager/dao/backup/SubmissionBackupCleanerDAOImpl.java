package org.gs4tr.termmanager.dao.backup;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

@Repository("submissionBackupCleanerDAO")
public class SubmissionBackupCleanerDAOImpl extends BackupDAOImpl<DbSubmissionTermEntry>
	implements SubmissionBackupCleanerDAO {

    /* delete submission */
    private static final String DELETE_SUBMISSIONS = "DELETE FROM TM_SUBMISSION WHERE PROJECT_ID IN (:projectIds)";
    private static final String DELETE_SUBMISSION_LANGUAGE = "DELETE FROM TM_SUBMISSION_LANGUAGE WHERE SUBMISSION_ID IN (:submissionIds)";
    private static final String DELETE_SUBMISSION_LANGUAGE_COMMENTS = "DELETE FROM TM_SUBMISSION_LANGUAGE_COMMENT WHERE SUBMISSION_LANGUAGE_ID IN (:submissionLanguageIds)";
    private static final String DELETE_SUBMISSION_TERMENTRY = "DELETE FROM TM_SUBMISSION_TERMENTRY WHERE PROJECT_ID IN (:projectIds)";
    private static final String DELETE_SUBMISSION_TERMENTRY_DESCRIPTIONS = "DELETE FROM TM_SUBMISSION_TERMENTRY_DESCRIPTION WHERE TERMENTRY_ID IN (:termEntryIds)";
    private static final String DELETE_SUBMISSION_TERMENTRY_HISTORY = "DELETE FROM TM_SUBMISSION_TERMENTRY_HISTORY WHERE TERMENTRY_UUID IN (:uuids)";
    private static final String DELETE_SUBMISSION_TERMS = "DELETE FROM TM_SUBMISSION_TERM WHERE TERMENTRY_ID IN (:termEntryIds)";
    private static final String DELETE_SUBMISSION_TERM_COMMENT = "DELETE FROM TM_COMMENT WHERE TERM_ID IN (:termIds)";
    private static final String DELETE_SUBMISSION_TERM_DESCRIPTIONS = "DELETE FROM TM_SUBMISSION_TERM_DESCRIPTION WHERE TERM_ID IN (:termIds)";
    private static final String DELETE_SUBMISSION_TERM_HISTORY = "DELETE FROM TM_SUBMISSION_TERM_HISTORY WHERE TERMENTRY_UUID IN (:uuids)";
    private static final String DELETE_SUBMISSION_USER = "DELETE FROM TM_SUBMISSION_USER WHERE SUBMISSION_ID IN (:submissionIds)";
    /*
     * select submission, submission term entry and submission term language id
     */
    private static final String GET_SUBMISSION_IDS = "SELECT SUBMISSION_ID FROM TM_SUBMISSION WHERE PROJECT_ID IN (:projectIds)";
    private static final String GET_SUBMISSION_LANGUAGE_IDS = "SELECT SUBMISSION_LANGUAGE_ID FROM TM_SUBMISSION_LANGUAGE WHERE SUBMISSION_ID IN (:submissionIds)";
    private static final String GET_SUBMISSION_TERMENTRY_IDS = "SELECT TERMENTRY_ID FROM TM_SUBMISSION_TERMENTRY WHERE PROJECT_ID IN (:projectIds)";
    private static final String GET_SUBMISSION_TERMENTRY_UUIDS = "SELECT UUID FROM TM_SUBMISSION_TERMENTRY WHERE TERMENTRY_ID IN (:termEntryIds)";
    private static final String GET_SUBMISSION_TERM_IDS = "SELECT TERM_ID FROM TM_SUBMISSION_TERM WHERE TERMENTRY_ID IN (:subTermEntryIds)";

    protected SubmissionBackupCleanerDAOImpl() {
	super(DbSubmissionTermEntry.class);
    }

    @Override
    public void deleteSubmissionsByProjectIds(Collection<Long> projectIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(projectIds, chunk -> {
	    List<Long> submissionIds = getSubmissionIds(projectIds);
	    if (isNotEmpty(submissionIds)) {
		deleteSubmissionChilds(submissionIds, chunkSize);
		deleteSubmissionTermEntryChilds(chunk, chunkSize);
		deleteSubmissionTermEntries(chunk);
		deleteSubmissions(chunk);
	    }
	}, chunkSize);
    }

    private void deleteSubmissionChilds(List<Long> submissionIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(submissionIds, chunk -> {
	    List<Long> submissionLanguageIds = getSubmissionLanguageIds(chunk);
	    deleteSubmissionLanguageComments(submissionLanguageIds, chunkSize);
	    deleteSubmissionLanguage(chunk);
	    deleteSubmissionUser(chunk);
	}, chunkSize);

    }

    private void deleteSubmissionHistory(Collection<Long> submissionTermEntryIds) {
	List<String> uuids = getSubmissionTermEntryUUIDs(submissionTermEntryIds);
	deleteSubmissionTermHistory(uuids);
	deleteSubmissionTermEntryHistory(uuids);
    }

    private void deleteSubmissionLanguage(Collection<Long> submissionIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_LANGUAGE);
	    query.setParameterList("submissionIds", submissionIds);
	    query.executeUpdate();

	});
    }

    private void deleteSubmissionLanguageComments(Collection<Long> submissionLanguageIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(submissionLanguageIds, this::deleteSubmissionLanguageComments,
		chunkSize);
    }

    private void deleteSubmissionLanguageComments(Collection<Long> submissionLanguageIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_LANGUAGE_COMMENTS);
	    query.setParameterList("submissionLanguageIds", submissionLanguageIds);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionTermComments(Collection<Long> submissionTermIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(submissionTermIds, this::deleteSubmissionTermComments, chunkSize);
    }

    private void deleteSubmissionTermComments(Collection<Long> termIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERM_COMMENT);
	    query.setParameterList("termIds", termIds);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionTermDescriptions(Collection<Long> submissionTermIds, int chunkSize) {
	ChunkedExecutionHelper.executeChuncked(submissionTermIds, this::deleteSubmissionTermDescriptions, chunkSize);
    }

    private void deleteSubmissionTermDescriptions(Collection<Long> termIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERM_DESCRIPTIONS);
	    query.setParameterList("termIds", termIds);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionTermEntries(Collection<Long> projectIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERMENTRY);
	    query.setParameterList("projectIds", projectIds);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionTermEntryChilds(Collection<Long> projectIds, int chunkSize) {
	List<Long> submissionTermEntryIds = getSubmissionTermEntryIds(projectIds);
	if (isNotEmpty(submissionTermEntryIds)) {
	    List<Long> submissionTermIds = getSubmissionTermIds(submissionTermEntryIds);
	    deleteSubmissionTermComments(submissionTermIds, chunkSize);
	    deleteSubmissionTermDescriptions(submissionTermIds, chunkSize);
	    deleteSubmissionTerms(submissionTermEntryIds);
	    deleteSubmissionHistory(submissionTermEntryIds);
	    deleteSubmissionTermEntryDescription(submissionTermEntryIds);
	}
    }

    private void deleteSubmissionTermEntryDescription(Collection<Long> submissionTermEntryIds) {

	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERMENTRY_DESCRIPTIONS);
	    query.setParameterList("termEntryIds", submissionTermEntryIds);
	    query.executeUpdate();

	});
    }

    private void deleteSubmissionTermEntryHistory(List<String> ids) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERMENTRY_HISTORY);
	    query.setParameterList("uuids", ids);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionTermHistory(List<String> ids) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERM_HISTORY);
	    query.setParameterList("uuids", ids);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionTerms(Collection<Long> submissionTermEntryIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_TERMS);
	    query.setParameterList("termEntryIds", submissionTermEntryIds);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissionUser(Collection<Long> submissionIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSION_USER);
	    query.setParameterList("submissionIds", submissionIds);
	    query.executeUpdate();
	});
    }

    private void deleteSubmissions(Collection<Long> projectIds) {
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(DELETE_SUBMISSIONS);
	    query.setParameterList("projectIds", projectIds);
	    query.executeUpdate();
	});
    }

    private List<Long> getSubmissionIds(Collection<Long> projectIds) {
	List<Long> submissionIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(GET_SUBMISSION_IDS);
	    query.setParameterList("projectIds", projectIds);
	    query.setReadOnly(true);
	    submissionIds.addAll(query.list());
	});
	return submissionIds;
    }

    private List<Long> getSubmissionLanguageIds(Collection<Long> submissionIds) {
	List<Long> submissionLanguageIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(GET_SUBMISSION_LANGUAGE_IDS);
	    query.setParameterList("submissionIds", submissionIds);
	    query.setReadOnly(true);
	    submissionLanguageIds.addAll(query.list());

	});
	return submissionLanguageIds;
    }

    private List<Long> getSubmissionTermEntryIds(Collection<Long> projectIds) {
	List<Long> submissionTermEntryIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(GET_SUBMISSION_TERMENTRY_IDS);
	    query.setParameterList("projectIds", projectIds);
	    query.setReadOnly(true);

	    submissionTermEntryIds.addAll(query.list());
	});

	return submissionTermEntryIds;

    }

    private List<String> getSubmissionTermEntryUUIDs(Collection<Long> submissionTermEntryIds) {
	List<String> uuids = new ArrayList<>();

	doInTransaction(session -> {

	    SQLQuery query = session.createSQLQuery(GET_SUBMISSION_TERMENTRY_UUIDS);
	    query.setParameterList("termEntryIds", submissionTermEntryIds);
	    query.setReadOnly(true);
	    uuids.addAll(query.list());
	});

	return uuids;
    }

    private List<Long> getSubmissionTermIds(Collection<Long> submissionTermEntryIds) {
	List<Long> submissionTermIds = new ArrayList<>();
	doInTransaction(session -> {
	    SQLQuery query = session.createSQLQuery(GET_SUBMISSION_TERM_IDS);
	    query.setParameterList("subTermEntryIds", submissionTermEntryIds);
	    query.setReadOnly(true);
	    submissionTermIds.addAll(query.list());

	});
	return submissionTermIds;
    }

}
