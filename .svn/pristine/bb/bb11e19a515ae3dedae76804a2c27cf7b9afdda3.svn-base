package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.dao.hibernate.AutowireSessionFactory;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper.ChunkedListExecutionCallback;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListHelper;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.dao.SubmissionDetailViewDAO;
import org.gs4tr.termmanager.model.EntityStatusPriority;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("submissionDetailViewDAO")
public class SubmissionDetailViewDAOImpl extends AutowireSessionFactory implements SubmissionDetailViewDAO {

    private static final String SELECT_QUERY_PART = "select distinct sub from SubmissionUser subUser ";

    @Override
    public PagedList<SubmissionDetailView> getEntityPagedList(SubmissionSearchRequest command,
	    PagedListInfo pagedListInfo) {
	if (pagedListInfo == null) {
	    pagedListInfo = new PagedListInfo();
	}

	boolean submitterView = command.isSubmitterView();
	String userName = command.getUserName();

	List<Submission> submissions = findSubmissions(command, pagedListInfo);

	PagedList<SubmissionDetailView> pagedList = new PagedList<SubmissionDetailView>();
	pagedList.setPagedListInfo(pagedListInfo);
	pagedList.setTotalCount((long) submissions.size());

	submissions = PagedListHelper.getPage(submissions, pagedListInfo);

	List<SubmissionDetailView> viewItems = new ArrayList<SubmissionDetailView>();

	for (Submission submission : submissions) {
	    Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		String assignee = submissionLanguage.getAssignee();
		submission.appendAssingees(assignee);
		if (!submitterView && !assignee.equals(userName)) {
		    continue;
		}
		submission.appendTargetLanguageIds(submissionLanguage.getLanguageId());
	    }
	    TmProject project = submission.getProject();

	    SubmissionDetailView view = new SubmissionDetailView();
	    view.setAssignee(submission.getAssignees());
	    view.setDateSubmitted(submission.getDateSubmitted().getTime());
	    view.setDateModified(submission.getDateModified().getTime());
	    view.setProjectId(project.getProjectId());
	    view.setProjectName(project.getProjectInfo().getName());
	    view.setSourceLanguageId(submission.getSourceLanguageId());
	    view.setSubmissionId(submission.getSubmissionId());
	    view.setSubmissionName(submission.getName());
	    view.setSubmitter(submission.getSubmitter());
	    view.setTargetLanguageIds(submission.getTargetLanguageIds());
	    view.setTermEntryCount(submission.getTermEntryCount());
	    view.setMarkerId(submission.getMarkerId());

	    SubmissionUser submissionUser = submission.getSubmissionUser(userName);
	    EntityStatusPriority statusPriority = (!submitterView && submissionUser != null)
		    ? submissionUser.getEntityStatusPriority()
		    : submission.getEntityStatusPriority();
	    Date dateCompleted = statusPriority.getDateCompleted();
	    if (dateCompleted != null) {
		view.setDateCompleted(dateCompleted.getTime());
	    }

	    ItemStatusType status = statusPriority.getStatus();

	    view.setStatus(status);

	    boolean isCanceled = status.equals(ItemStatusTypeHolder.CANCELLED);
	    view.setCanceled(isCanceled);
	    boolean inTranslation = status.equals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW);
	    view.setInTranslation(inTranslation);
	    boolean inFinalReview = status.equals(ItemStatusTypeHolder.IN_FINAL_REVIEW);
	    view.setInFinalReview(inFinalReview);

	    viewItems.add(view);
	}

	pagedList.setElements(viewItems.toArray(new SubmissionDetailView[viewItems.size()]));

	return pagedList;
    }

    private void addDatesToWhereClause(StringBuffer buffer, SubmissionSearchRequest command) {
	Date fromSubmittedDate = command.getDateSubmittedFrom();
	Date toSubmittedDate = command.getDateSubmittedTo();

	Date fromModifiedDate = command.getDateModifiedFrom();
	Date toModifiedDate = command.getDateModifiedTo();

	Date fromCompletedDate = command.getDateCompletedFrom();
	Date toCompletedDate = command.getDateCompletedTo();

	if (fromSubmittedDate != null) {
	    buffer.append("and sub.dateSubmitted >= :fromSubmittedDate ");
	}
	if (toSubmittedDate != null) {
	    buffer.append("and sub.dateSubmitted <= :toSubmittedDate ");
	}

	if (fromModifiedDate != null) {
	    buffer.append("and sub.dateModified >= :fromModifiedDate ");
	}
	if (toModifiedDate != null) {
	    buffer.append("and sub.dateModified <= :toModifiedDate ");
	}

	if (fromCompletedDate != null) {
	    buffer.append("and userStatusPriority.dateCompleted >= :fromCompletedDate ");
	}
	if (toCompletedDate != null) {
	    buffer.append("and userStatusPriority.dateCompleted <= :toCompletedDate ");
	}
    }

    private Query createElementQuery(Session session, SubmissionSearchRequest command, PagedListInfo pagedListInfo) {
	String selectQuery = SELECT_QUERY_PART;
	StringBuffer buffer = new StringBuffer(selectQuery);
	createJoinClause(buffer);
	createWhereClause(buffer, command);
	createOrderByClause(buffer, command, pagedListInfo);

	return session.createQuery(buffer.toString());
    }

    private void createJoinClause(StringBuffer buffer) {
	buffer.append("inner join subUser.entityStatusPriority userStatusPriority ");
	buffer.append("inner join subUser.submission sub ");
	buffer.append("inner join sub.entityStatusPriority entityStatusPriority ");
	buffer.append("left join sub.project project ");
	buffer.append("left join sub.submissionLanguages submissionLanguages ");
    }

    private void createOrderByClause(StringBuffer buffer, SubmissionSearchRequest command,
	    PagedListInfo pagedListInfo) {
	String defaultSortProperty = command.isSubmitterView() ? "entityStatusPriority.priority desc"
		: "userStatusPriority.priority desc";

	String dateModifiedSortProperty = "sub.dateModified";

	buffer.append("order by").append(StringConstants.SPACE).append(defaultSortProperty)
		.append(StringConstants.SPACE);

	String sortProperty = StringUtils.isNotEmpty(pagedListInfo.getSortProperty()) ? pagedListInfo.getSortProperty()
		: dateModifiedSortProperty;
	buffer.append(StringConstants.COMMA).append(StringConstants.SPACE).append(sortProperty)
		.append(StringConstants.SPACE)
		.append(SortDirection.ASCENDING == pagedListInfo.getSortDirection() ? "asc" : "desc");
    }

    private void createWhereClause(StringBuffer buffer, SubmissionSearchRequest command) {

	if (command.getUserId() != null) {
	    buffer.append("where subUser.user.userProfileId = :userId ");
	} else {
	    buffer.append("where 1=1 ");
	}

	if (CollectionUtils.isNotEmpty(command.getSubmissionIds())) {
	    buffer.append("and sub.submissionId in (:submissionIds) ");
	}

	if (CollectionUtils.isNotEmpty(command.getSubmitters())) {
	    buffer.append("and sub.submitter in (:submitters) ");
	}

	if (CollectionUtils.isNotEmpty(command.getProjectIds())) {
	    buffer.append("and project.projectId in (:projectIds) ");
	}

	if (CollectionUtils.isNotEmpty(command.getAssingees())) {
	    buffer.append("and submissionLanguages.assignee in (:assignees) ");
	}

	if (CollectionUtils.isNotEmpty(command.getSourceLanguageIds())) {
	    buffer.append("and sub.sourceLanguageId in (:sourceLanguageIds) ");
	}

	if (CollectionUtils.isNotEmpty(command.getTargetLanguageIds())) {
	    buffer.append("and submissionLanguages.languageId in (:targetLanguageIds) ");
	}

	if (CollectionUtils.isNotEmpty(command.getStatuses())) {
	    if (command.isSubmitterView()) {
		buffer.append("and entityStatusPriority.status in (:statuses) ");
	    } else {
		buffer.append("and userStatusPriority.status in (:statuses) ");
	    }
	}

	if (StringUtils.isNotEmpty(command.getName())) {
	    buffer.append("and sub.name like :name ");
	}

	addDatesToWhereClause(buffer, command);
    }

    @SuppressWarnings("unchecked")
    private List<Submission> findSubmissions(final SubmissionSearchRequest command, final PagedListInfo pagedListInfo) {
	HibernateCallback<List<Submission>> cb = new HibernateCallback<List<Submission>>() {
	    @Override
	    public List<Submission> doInHibernate(Session session) throws HibernateException, SQLException {
		final Query query = createElementQuery(session, command, pagedListInfo);

		final List<Submission> submissions = new ArrayList<Submission>();

		Set<Long> submissionIds = command.getSubmissionIds();

		ChunkedExecutionHelper.executeChuncked(submissionIds, new ChunkedListExecutionCallback<Long>() {
		    @Override
		    public void execute(List<Long> chunkedSubmisionIds) {
			initQueryParameters(query, command, chunkedSubmisionIds);

			List<Submission> list = query.list();
			if (CollectionUtils.isNotEmpty(list)) {
			    submissions.addAll(list);
			}
		    }
		}, ChunkedExecutionHelper.DEFAULT_CHUNK_SIZE);

		return submissions;
	    }
	};

	return execute(cb);
    }

    private void initDatesParameters(Query query, SubmissionSearchRequest command) {
	Date fromSubmittedDate = command.getDateSubmittedFrom();
	Date toSubmittedDate = command.getDateSubmittedTo();

	Date fromModifiedDate = command.getDateModifiedFrom();
	Date toModifiedDate = command.getDateModifiedTo();

	Date fromCompletedDate = command.getDateCompletedFrom();
	Date toCompletedDate = command.getDateCompletedTo();

	if (fromSubmittedDate != null) {
	    query.setTimestamp("fromSubmittedDate", fromSubmittedDate);
	}
	if (toSubmittedDate != null) {
	    query.setTimestamp("toSubmittedDate", toSubmittedDate);
	}

	if (fromModifiedDate != null) {
	    query.setTimestamp("fromModifiedDate", fromModifiedDate);
	}
	if (toModifiedDate != null) {
	    query.setTimestamp("toModifiedDate", toModifiedDate);
	}

	if (fromCompletedDate != null) {
	    query.setTimestamp("fromCompletedDate", fromCompletedDate);
	}
	if (toCompletedDate != null) {
	    query.setTimestamp("toCompletedDate", toCompletedDate);
	}
    }

    private void initQueryParameters(Query query, SubmissionSearchRequest command, List<Long> submisionIds) {
	List<String> targetLanguageIds = command.getTargetLanguageIds();
	String submissionName = command.getName();
	List<String> submitters = command.getSubmitters();
	List<String> assingees = command.getAssingees();
	List<String> sourceLanguageIds = command.getSourceLanguageIds();
	List<String> statuses = command.getStatuses();
	List<Long> projectIds = command.getProjectIds();
	Long userId = command.getUserId();

	if (userId != null) {
	    query.setLong("userId", userId);
	}

	if (CollectionUtils.isNotEmpty(submisionIds)) {
	    query.setParameterList("submissionIds", submisionIds);
	}

	if (CollectionUtils.isNotEmpty(projectIds)) {
	    query.setParameterList("projectIds", projectIds);
	}

	if (CollectionUtils.isNotEmpty(submitters)) {
	    query.setParameterList("submitters", submitters);
	}

	if (CollectionUtils.isNotEmpty(assingees)) {
	    query.setParameterList("assignees", assingees);
	}

	if (CollectionUtils.isNotEmpty(sourceLanguageIds)) {
	    query.setParameterList("sourceLanguageIds", sourceLanguageIds);
	}

	if (CollectionUtils.isNotEmpty(command.getTargetLanguageIds())) {
	    query.setParameterList("targetLanguageIds", targetLanguageIds);
	}

	if (CollectionUtils.isNotEmpty(statuses)) {
	    query.setParameterList("statuses", statuses);
	    if (!command.isSubmitterView()) {
		query.setLong("userId", userId);
	    }
	}

	if (StringUtils.isNotEmpty(submissionName)) {
	    query.setString("name", '%' + submissionName + '%');
	}

	initDatesParameters(query, command);
    }
}
