package org.gs4tr.termmanager.dao.hibernate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListHelper;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.termmanager.dao.ProjectLanguageDetailDAO;
import org.gs4tr.termmanager.model.Language;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectLanguageUserDetail;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.ProjectLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.ProjectLanguageDetailView;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

@Repository("projectLanguageDetailDAO")
public class ProjectLanguageDetailDAOImpl extends AbstractHibernateGenericDao<ProjectLanguageDetail, Long>
	implements ProjectLanguageDetailDAO {

    private static final String SELECT_QUERY_PART = "select details from ProjectLanguageDetail details ";

    private static final int SIZE = 1000;

    private static final String UPDATE_ACTIVE_SUBMISSION_COUNTS = ", pld.activeSubmissionCount =  :activeSubmissionCount";

    private static final String UPDATE_COMPLETED_SUBMISSION_COUNTS = ", pld.completedSubmissionCount = :completedSubmissionCount";

    private static final String UPDATE_LANGUAGE_DETAIL = "update ProjectLanguageDetail as pld set pld.approvedTermCount =  :approvedTermCount, pld.forbiddenTermCount = :forbiddenTermCount, pld.pendingApprovalCount = :pendingApprovalCount, pld.onHoldTermCount = :onHoldTermCount, pld.termCount = :termCount, pld.termInSubmissionCount =  :termInSubmissionCount, pld.termEntryCount = :termEntryCount, pld.dateModified = case when  pld.dateModified < :dateModified then :dateModified else pld.dateModified end";

    private static final String UPDATE_LANGUAGE_DETAIL_WHERE = " where pld.languageId = :languageId and pld.projectDetail = (select pd from ProjectDetail as pd where pd.project.projectId = :projectId)";

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public ProjectLanguageDetailDAOImpl() {
	super(ProjectLanguageDetail.class, Long.class);
    }

    @Override
    public ProjectLanguageDetail findProjectLangDetailByLangId(Long projectId, String languageId) {
	HibernateCallback<ProjectLanguageDetail> cb = session -> {
	    Query query = session.getNamedQuery("ProjectLanguageDetail.findByProjectAndLanguageId");
	    query.setLong("projectId", projectId);
	    query.setString("languageId", languageId);

	    return (ProjectLanguageDetail) query.uniqueResult();

	};

	return execute(cb);
    }

    @Override
    public PagedList<ProjectLanguageDetailView> getEntityPagedList(ProjectLanguageDetailRequest command,
	    PagedListInfo pagedListInfo) {
	if (pagedListInfo == null) {
	    pagedListInfo = new PagedListInfo();
	}
	pagedListInfo.setSize(SIZE);

	List<ProjectLanguageDetail> details = findProjectLanguageDetails(command);

	PagedList<ProjectLanguageDetailView> pagedList = new PagedList<>();
	pagedList.setPagedListInfo(pagedListInfo);
	pagedList.setTotalCount((long) details.size());

	details = PagedListHelper.getPage(details, pagedListInfo);

	TmUserProfile user = command.getUser();
	Long userProfileId = user.getUserProfileId();
	boolean isPowerUser = user.isPowerUser();

	List<ProjectLanguageDetailView> views = new ArrayList<>();
	for (ProjectLanguageDetail detail : details) {
	    ProjectLanguageUserDetail userDetail = detail.getProjectUserDetail(userProfileId);

	    long activeSubmissionCount = 0;
	    long completedSubmissionCount = 0;

	    if (userDetail != null) {
		activeSubmissionCount = userDetail.getActiveSubmissionCount();
		completedSubmissionCount = userDetail.getCompletedSubmissionCount();
	    }

	    if (isPowerUser) {
		activeSubmissionCount = detail.getActiveSubmissionCount();
		completedSubmissionCount = detail.getCompletedSubmissionCount();
	    }

	    ProjectLanguageDetailView view = new ProjectLanguageDetailView();
	    view.setProjectLanguageDetailViewId(detail.getProjectLanguageDetailId());
	    view.setDateModified(detail.getDateModified().getTime());
	    view.setActiveSubmissionCount(activeSubmissionCount);
	    view.setApprovedTermCount(detail.getApprovedTermCount());
	    view.setPendingApprovalTermCount(detail.getPendingApprovalCount());
	    view.setOnHoldTermCount(detail.getOnHoldTermCount());
	    view.setCompletedSubmissionCount(completedSubmissionCount);
	    view.setForbiddenTermCount(detail.getForbiddenTermCount());
	    view.setLanguage(Language.valueOf(detail.getLanguageId()));
	    view.setTermCount(detail.getTermCount());
	    view.setTermInSubmissionCount(detail.getTermInSubmissionCount());

	    views.add(view);
	}

	views.sort(Comparator.comparing(view -> view.getLanguage().getDisplayName()));

	pagedList.setElements(views.toArray(new ProjectLanguageDetailView[views.size()]));

	return pagedList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectLanguageDetail> getProjectLanguageDetailsByProjectId(final Long projectId) {
	HibernateCallback<List<ProjectLanguageDetail>> cb = session -> {
	    Query query = session.getNamedQuery("ProjectLanguageDetail.findByProjectId");
	    query.setLong("projectId", projectId);

	    return query.list();
	};

	return execute(cb);
    }

    @Override
    public boolean incrementalUpdateProjectLanguageDetail(String languageId, ProjectDetailInfo info,
	    Date dateModified) {
	String queryName = "ProjectLanguageDetail.incrementUpdateProjectLanguageDetail";
	return updateProjectLanguageDetail(languageId, info, dateModified, queryName);
    }

    @Override
    public boolean incrementalUpdateProjectLanguageDetail(ProjectLanguageDetailInfoIO projectLanguageInfo) {
	String queryName = "ProjectLanguageDetail.incrementUpdateProjectLanguageDetail";
	return updateProjectLanguageDetail(projectLanguageInfo, queryName);
    }

    @Override
    public boolean recodeProjectLanguageDetail(Long projectId, String languageFrom, String languageTo) {
	HibernateCallback<Integer> cb = (session) -> {
	    Query query = session.getNamedQuery("ProjectLanguageDetail.recodeProjectLanguageDetail");
	    query.setString("languageFrom", languageFrom);
	    query.setString("languageTo", languageTo);
	    query.setLong("projectId", projectId);

	    return query.executeUpdate();
	};

	return execute(cb) == 1;
    }

    @Override
    public boolean updateProjectLanguageDetail(String languageId, ProjectDetailInfo info, Date dateModified) {
	String queryName = "ProjectLanguageDetail.updateProjectLanguageDetail";
	return updateProjectLanguageDetail(languageId, info, dateModified, queryName);

    }

    @Override
    public boolean updateProjectLanguageDetail(ProjectLanguageDetailInfoIO detailInfo) {
	String queryName = "ProjectLanguageDetail.updateProjectLanguageDetail";
	return updateProjectLanguageDetailInfo(detailInfo);
    }

    private void appendSubmissionCountParameters(ProjectLanguageDetailInfoIO info, Query query) {

	Long activeSubmissionCount = info.getActiveSubmissionCount();
	Long completedSubmissionCount = info.getActiveSubmissionCount();

	if (activeSubmissionCount != null) {
	    query.setLong("activeSubmissionCount", activeSubmissionCount);
	}

	if (completedSubmissionCount != null) {
	    query.setLong("completedSubmissionCount", completedSubmissionCount);
	}
    }

    private void appendSubmissionCountQuery(ProjectLanguageDetailInfoIO info, StringBuilder builder) {

	if (info.getActiveSubmissionCount() != null) {
	    builder.append(UPDATE_ACTIVE_SUBMISSION_COUNTS);
	}

	if (info.getCompletedSubmissionCount() != null) {
	    builder.append(UPDATE_COMPLETED_SUBMISSION_COUNTS);
	}

    }

    private Criteria createElementQuery(Session session) {

	Criteria criteria = session.createCriteria(ProjectLanguageDetail.class);
	criteria.createAlias("projectDetail", "projectDetail", JoinType.INNER_JOIN);

	criteria.add(Restrictions.eq("disabled", Boolean.FALSE));
	criteria.addOrder(Order.desc("languageId"));

	return criteria;
    }

    @SuppressWarnings("unchecked")
    private List<ProjectLanguageDetail> findProjectLanguageDetails(final ProjectLanguageDetailRequest command) {
	HibernateCallback<List<ProjectLanguageDetail>> cb = session -> {
	    final Criteria query = createElementQuery(session);

	    final List<ProjectLanguageDetail> projectLanguageDetail = new ArrayList<>();

	    Set<String> languageIds = command.getLanguageIds();

	    ChunkedExecutionHelper.executeChuncked(languageIds, chunkedLanguageIds -> {
		initLanguageQueryParameters(query, chunkedLanguageIds);
		initQueryParameters(query, command);

		List<ProjectLanguageDetail> list = query.list();
		if (CollectionUtils.isNotEmpty(list)) {
		    projectLanguageDetail.addAll(list);
		}
	    }, ChunkedExecutionHelper.DEFAULT_CHUNK_SIZE);

	    return projectLanguageDetail;
	};

	return execute(cb);
    }

    private void initLanguageQueryParameters(Criteria criteria, List<String> languageIds) {
	criteria.add(Restrictions.in("languageId", languageIds));
    }

    private void initQueryParameters(Criteria criteria, ProjectLanguageDetailRequest command) {
	criteria.add(Restrictions.eq("projectDetail.projectDetailId", command.getProjectDetailId()));
    }

    private boolean updateProjectLanguageDetail(ProjectLanguageDetailInfoIO info, String queryName) {

	Session session = null;
	Transaction transaction = null;
	int response = 0;

	try {
	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    Query query = session.getNamedQuery(queryName);
	    query.setLong("approvedTermCount", info.getApprovedTermCount());
	    query.setLong("forbiddenTermCount", info.getForbiddenTermCount());
	    query.setLong("pendingApprovalCount", info.getPendingTermCount());
	    query.setLong("onHoldTermCount", info.getOnHoldTermCount());
	    query.setLong("termCount", info.getTermCount());
	    query.setLong("termEntryCount", info.getTermEntryCount());
	    query.setLong("activeSubmissionCount", info.getActiveSubmissionCount());
	    query.setLong("completedSubmissionCount", info.getCompletedSubmissionCount());
	    query.setLong("termInSubmissionCount", info.getTermInSubmissionCount());
	    query.setLong("projectId", info.getProjectId());
	    query.setTimestamp("dateModified", info.getDateModified());
	    query.setString("languageId", info.getLanguageId());

	    response = query.executeUpdate();

	    transaction.commit();
	    session.flush();
	    session.clear();

	} catch (Exception e) {
	    LOGGER.error(e, e);
	    if (transaction != null) {
		transaction.rollback();
	    }
	} finally {
	    if (session != null) {
		session.close();
	    }
	}

	return response == 1;

    }

    private boolean updateProjectLanguageDetail(String languageId, ProjectDetailInfo info, Date dateModified,
	    String queryName) {
	long approvedTermCount = info.getLanguageCount(languageId, info.getLanguageApprovedTermCount());
	long forbiddenTermCount = info.getLanguageCount(languageId, info.getLanguageForbiddenTermCount());
	long pendingTermCount = info.getLanguageCount(languageId, info.getLanguagePendingTermCount());
	long onHoldTermCount = info.getLanguageCount(languageId, info.getLanguageOnHoldTermCount());
	long activeSubmissionCount = info.getLanguageCount(languageId, info.getLanguageActiveSubmissionCount());
	long completedSubmissionCount = info.getLanguageCount(languageId, info.getLanguageCompletedSubmissionCount());
	long termInSubmissionCount = info.getLanguageCount(languageId, info.getLanguageTermInSubmissionCount());
	long termCount = info.getLanguageCount(languageId, info.getLanguageTermCount());

	HibernateCallback<Integer> cb = (session) -> {
	    Query query = session.getNamedQuery(queryName);
	    query.setLong("approvedTermCount", approvedTermCount);
	    query.setLong("forbiddenTermCount", forbiddenTermCount);
	    query.setLong("pendingApprovalCount", pendingTermCount);
	    query.setLong("onHoldTermCount", onHoldTermCount);
	    query.setLong("termCount", termCount);
	    query.setLong("termEntryCount", info.getTermEntryCount().get());
	    query.setLong("activeSubmissionCount", activeSubmissionCount);
	    query.setLong("completedSubmissionCount", completedSubmissionCount);
	    query.setLong("termInSubmissionCount", termInSubmissionCount);
	    query.setLong("projectId", info.getProjectId());
	    query.setTimestamp("dateModified", dateModified);
	    query.setString("languageId", languageId);

	    return query.executeUpdate();
	};

	return execute(cb) == 1;

    }

    private boolean updateProjectLanguageDetailInfo(ProjectLanguageDetailInfoIO info) {
	Session session = null;
	Transaction transaction = null;
	int response = 0;

	try {
	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    StringBuilder queryBuilder = new StringBuilder(UPDATE_LANGUAGE_DETAIL);

	    appendSubmissionCountQuery(info, queryBuilder);

	    queryBuilder.append(UPDATE_LANGUAGE_DETAIL_WHERE);

	    final Query query = session.createQuery(queryBuilder.toString());

	    query.setLong("approvedTermCount", info.getApprovedTermCount());
	    query.setLong("forbiddenTermCount", info.getForbiddenTermCount());
	    query.setLong("pendingApprovalCount", info.getPendingTermCount());

	    appendSubmissionCountParameters(info, query);

	    query.setLong("onHoldTermCount", info.getOnHoldTermCount());
	    query.setLong("termCount", info.getTermCount());
	    query.setLong("termEntryCount", info.getTermEntryCount());
	    query.setLong("termInSubmissionCount", info.getTermInSubmissionCount());
	    query.setLong("projectId", info.getProjectId());
	    query.setTimestamp("dateModified", info.getDateModified());
	    query.setString("languageId", info.getLanguageId());

	    response = query.executeUpdate();

	    transaction.commit();
	    session.flush();
	    session.clear();

	} catch (Exception e) {
	    LOGGER.error(e, e);
	    if (transaction != null) {
		transaction.rollback();
	    }
	} finally {
	    if (session != null) {
		session.close();
	    }
	}

	return response == 1;

    }
}
