package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.dao.hibernate.AutowireSessionFactory;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper.ChunkedListExecutionCallback;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListHelper;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.dao.SubmissionLanguageDetailViewDAO;
import org.gs4tr.termmanager.model.EntityStatusPriority;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository("submissionLanguageDetailViewDAO")
public class SubmissionLanguageDetailViewDAOImpl extends AutowireSessionFactory
	implements SubmissionLanguageDetailViewDAO {

    private static final String SELECT_QUERY_PART = "select distinct details from SubmissionLanguage details ";

    @Override
    public PagedList<SubmissionLanguageDetailView> getEntityPagedList(SubmissionLanguageDetailRequest command,
	    PagedListInfo pagedListInfo) {
	if (pagedListInfo == null) {
	    pagedListInfo = new PagedListInfo();
	}

	List<SubmissionLanguage> submissionLanguages = findSubmissionLanguages(command, pagedListInfo);

	PagedList<SubmissionLanguageDetailView> pagedList = new PagedList<SubmissionLanguageDetailView>();
	pagedList.setPagedListInfo(pagedListInfo);
	pagedList.setTotalCount((long) submissionLanguages.size());

	submissionLanguages = PagedListHelper.getPage(submissionLanguages, pagedListInfo);

	List<SubmissionLanguageDetailView> viewItems = new ArrayList<SubmissionLanguageDetailView>();

	boolean submitterView = command.isSubmitterView();

	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    SubmissionLanguageDetailView view = new SubmissionLanguageDetailView();
	    view.setAssignee(submissionLanguage.getAssignee());
	    view.setDateSubmitted(submissionLanguage.getDateSubmitted().getTime());
	    view.setDateModified(submissionLanguage.getDateModified().getTime());
	    view.setLanguageId(submissionLanguage.getLanguageId());
	    view.setSubmissionLanguageId(submissionLanguage.getSubmissionLanguageId());
	    view.setMarkerId(submissionLanguage.getMarkerId());
	    view.setSubmissionLanguageComments(submissionLanguage.getSubmissionLanguageComments());
	    view.setTermCanceledCount(submissionLanguage.getTermCanceledCount());
	    view.setTermCompletedCount(submissionLanguage.getTermCompletedCount());
	    view.setTermCount(submissionLanguage.getTermCount());
	    view.setTermInFinalReviewCount(submissionLanguage.getTermInFinalReviewCount());
	    view.setTermInTranslationCount(submissionLanguage.getTermInTranslationCount());

	    EntityStatusPriority statusPriority = submissionLanguage.getEntityStatusPriority();
	    ItemStatusType status = submitterView ? statusPriority.getStatus() : submissionLanguage.getStatusAssignee();
	    view.setStatus(status);
	    Date dateCompleted = statusPriority.getDateCompleted();
	    if (dateCompleted != null) {
		view.setDateCompleted(dateCompleted.getTime());
	    }

	    boolean isCanceled = status.equals(ItemStatusTypeHolder.CANCELLED);
	    view.setCanceled(isCanceled);
	    boolean inTranslation = status.equals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW);
	    view.setInTranslation(inTranslation);
	    boolean inFinalReview = status.equals(ItemStatusTypeHolder.IN_FINAL_REVIEW);
	    view.setInFinalReview(inFinalReview);

	    viewItems.add(view);
	}

	pagedList.setElements(viewItems.toArray(new SubmissionLanguageDetailView[viewItems.size()]));

	return pagedList;
    }

    private Query createElementQuery(Session session, SubmissionLanguageDetailRequest command,
	    PagedListInfo pagedListInfo) {
	String selectQuery = SELECT_QUERY_PART;
	StringBuffer buffer = new StringBuffer(selectQuery);
	createJoinClause(buffer);
	createWhereClause(buffer, command);
	createOrderByClause(buffer, command, pagedListInfo);

	return session.createQuery(buffer.toString());
    }

    private void createJoinClause(StringBuffer buffer) {
	buffer.append("inner join details.submission submission ");
	buffer.append("inner join details.entityStatusPriority entityStatusPriority ");
	buffer.append("left join fetch details.submissionLanguageComments comments ");
    }

    private void createOrderByClause(StringBuffer buffer, SubmissionLanguageDetailRequest command,
	    PagedListInfo pagedListInfo) {
	if (command.isSubmitterView()) {
	    buffer.append("order by entityStatusPriority.priority desc, details.dateModified desc");
	} else {
	    buffer.append("order by entityStatusPriority.priorityAssignee desc, details.dateModified desc");
	}
    }

    private void createWhereClause(StringBuffer buffer, SubmissionLanguageDetailRequest command) {
	buffer.append("where details.languageId in (:languageIds) ");

	if (command.getSubmissionId() != null) {
	    buffer.append("and submission = :submissionId) ");
	}
    }

    @SuppressWarnings("unchecked")
    private List<SubmissionLanguage> findSubmissionLanguages(final SubmissionLanguageDetailRequest command,
	    final PagedListInfo pagedListInfo) {
	HibernateCallback<List<SubmissionLanguage>> cb = new HibernateCallback<List<SubmissionLanguage>>() {
	    @Override
	    public List<SubmissionLanguage> doInHibernate(Session session) throws HibernateException, SQLException {
		final Query query = createElementQuery(session, command, pagedListInfo);

		final List<SubmissionLanguage> submissionLanguages = new ArrayList<SubmissionLanguage>();

		Set<String> languageIds = command.getLanguageIds();

		ChunkedExecutionHelper.executeChuncked(languageIds, new ChunkedListExecutionCallback<String>() {
		    @Override
		    public void execute(List<String> chunkedLanguageIds) {
			initLanguageQueryParameters(query, chunkedLanguageIds);
			initQueryParameters(query, command);

			List<SubmissionLanguage> list = query.list();
			if (CollectionUtils.isNotEmpty(list)) {
			    submissionLanguages.addAll(list);
			}
		    }
		}, ChunkedExecutionHelper.DEFAULT_CHUNK_SIZE);

		return submissionLanguages;
	    }
	};

	return execute(cb);
    }

    private void initLanguageQueryParameters(Query query, List<String> languageIds) {
	query.setParameterList("languageIds", languageIds);
    }

    private void initQueryParameters(Query query, SubmissionLanguageDetailRequest command) {
	Long submissionId = command.getSubmissionId();

	if (submissionId != null) {
	    query.setLong("submissionId", submissionId);
	}
    }
}
