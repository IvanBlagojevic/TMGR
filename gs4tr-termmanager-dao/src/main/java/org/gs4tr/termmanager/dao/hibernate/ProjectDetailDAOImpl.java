package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper;
import org.gs4tr.foundation.modules.dao.utils.ChunkedExecutionHelper.ChunkedListExecutionCallback;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.SortDirection;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.utils.DaoUtils;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.search.command.UserProjectSearchRequest;
import org.gs4tr.termmanager.model.view.ProjectReport;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

@Repository("projectDetailDAO")
public class ProjectDetailDAOImpl extends AbstractHibernateGenericDao<ProjectDetail, Long> implements ProjectDetailDAO {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String REPORT_QUERY_BY_LANGUAGE = "select pld from ProjectLanguageDetail pld ";

    private static final String REPORT_QUERY_BY_PROJECT = "select pd from ProjectDetail pd ";

    private static final int SIZE = 200;

    private static final String UPDATE_ACTIVE_SUBMISSION_COUNTS = ", pd.activeSubmissionCount =  :activeSubmissionCount";

    private static final String UPDATE_COMPLETED_SUBMISSION_COUNTS = ", pd.completedSubmissionCount = :completedSubmissionCount";

    private static final String UPDATE_DASHBOARD_COUNTS = "update ProjectDetail as pd set pd.approvedTermCount =  :approvedTermCount, pd.forbiddenTermCount = :forbiddenTermCount, pd.pendingApprovalCount =  :pendingApprovalCount, pd.onHoldTermCount =  :onHoldTermCount, pd.termCount =  :termCount, pd.termEntryCount =  :termEntryCount, pd.termInSubmissionCount =  :termInSubmissionCount, pd.dateModified = case when  pd.dateModified < :dateModified then :dateModified else pd.dateModified end";

    private static final String UPDATE_DASHBOARD_COUNTS_WHERE = " where pd.project.projectId = :projectId";

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public ProjectDetailDAOImpl() {
	super(ProjectDetail.class, Long.class);
    }

    @Override
    public ProjectDetail findByProjectId(final Long projectId, final Class<?>... classesToFetch) {
	HibernateCallback<ProjectDetail> cb = new HibernateCallback<ProjectDetail>() {
	    @Override
	    public ProjectDetail doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("ProjectDetail.findByProjectId");
		query.setLong("projectId", projectId);
		ProjectDetail projectDetail = (ProjectDetail) query.uniqueResult();
		if (classesToFetch != null && projectDetail != null) {
		    DaoUtils.initializeEntities(projectDetail, classesToFetch);
		}
		return projectDetail;
	    }
	};

	return execute(cb);
    }

    @Override
    public List<ProjectReport> getAllProjectsReport(boolean groupByLanguages, boolean isPowerUser, Set<Long> projectIds,
	    Set<String> languageIds) {
	List<ProjectReport> reports = new ArrayList<ProjectReport>();

	if (groupByLanguages) {
	    List<ProjectLanguageDetail> resultList = getAllProjectLanguageDetails(isPowerUser, projectIds);
	    if (CollectionUtils.isNotEmpty(resultList)) {
		for (ProjectLanguageDetail projectLanguageDetail : resultList) {
		    ProjectReport report = new ProjectReport();

		    ProjectDetail projectDetail = projectLanguageDetail.getProjectDetail();
		    String projectName = projectDetail.getProject().getProjectInfo().getName();

		    report.setLanguageId(projectLanguageDetail.getLanguageId());

		    Date maxModifiedDate = projectDetail.getDateModified();

		    String maxModifiedDateFormat = null;
		    if (maxModifiedDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			maxModifiedDateFormat = sdf.format(maxModifiedDate);
		    }

		    report.setProjectName(projectName);
		    report.setTermCount(projectLanguageDetail.getTermCount());
		    report.setMaxModifiedDate(maxModifiedDateFormat);

		    reports.add(report);
		}
	    }
	} else {
	    List<ProjectDetail> resultList = getAllProjectDetails(isPowerUser, projectIds);

	    if (CollectionUtils.isNotEmpty(resultList)) {
		for (ProjectDetail projectDetail : resultList) {
		    ProjectReport report = new ProjectReport();

		    String projectName = projectDetail.getProject().getProjectInfo().getName();

		    long languageCount = 0;
		    Set<ProjectLanguageDetail> languageDetails = projectDetail.getLanguageDetails();
		    if (CollectionUtils.isNotEmpty(languageDetails)) {
			for (ProjectLanguageDetail languageDetail : languageDetails) {
			    if (languageDetail.isDisabled()) {
				continue;
			    }
			    if (!languageIds.contains(languageDetail.getLanguageId())) {
				continue;
			    }
			    languageCount++;
			}
		    }

		    report.setLanguageCount(languageCount);

		    Date maxModifiedDate = projectDetail.getDateModified();
		    String maxModifiedDateFormat = null;
		    if (maxModifiedDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			maxModifiedDateFormat = sdf.format(maxModifiedDate);
		    }

		    report.setProjectName(projectName);
		    report.setTermCount(projectDetail.getTermCount());
		    report.setMaxModifiedDate(maxModifiedDateFormat);

		    reports.add(report);
		}
	    }
	}

	return reports;
    }

    public boolean incrementalUpdateProjectDetail(ProjectDetailInfo info) {
	String queryName = "ProjectDetail.incrementUpdateProjectDetail";
	return updateProjectDetail(info, queryName);
    }

    @Override
    public boolean incrementalUpdateProjectDetail(ProjectDetailCountsIO counts) {
	String queryName = "ProjectDetail.incrementUpdateProjectDetail";
	return updateProjectDetail(counts, queryName);
    }

    @Override
    public List<ProjectDetail> searchProjectDetails(UserProjectSearchRequest searchRequest,
	    PagedListInfo pagedListInfo) {
	return findProjectUserDetails(searchRequest, pagedListInfo);
    }

    @Override
    public void updateDateModifiedByProjectId(Long projectId, Date newDateModified) {
	execute(session -> {
	    Query query = session.getNamedQuery("ProjectDetail.updateDateModifiedByProjectId");
	    query.setParameter("projectId", projectId);
	    query.setParameter("dateModified", newDateModified);
	    query.executeUpdate();
	    return null;
	});
    }

    public void updateProjectAndLanguagesDateModifiedByProjectId(Long projectId, Set<String> languages,
	    Date newDateModified) {
	execute(session -> {
	    Query pdQuery = session.getNamedQuery("ProjectDetail.updateDateModifiedByProjectId");
	    pdQuery.setLong("projectId", projectId);
	    pdQuery.setTimestamp("dateModified", newDateModified);
	    pdQuery.executeUpdate();

	    Query pldQuery = session.getNamedQuery("ProjectLanguageDetail.updateDateModified");
	    pldQuery.setLong("projectId", projectId);
	    pldQuery.setTimestamp("dateModified", newDateModified);
	    pldQuery.setParameterList("languageIds", languages);
	    pldQuery.executeUpdate();

	    return null;
	});
    }

    public boolean updateProjectDetail(ProjectDetailInfo info) {
	String queryName = "ProjectDetail.updateProjectDetail";
	return updateProjectDetail(info, queryName);
    }

    @Override
    public boolean updateProjectDetail(ProjectDetailCountsIO counts) {
	return updateDashboardCounts(counts);
    }

    private void addDatesToWhereClause(Criteria criteria, UserProjectSearchRequest command) {
	Date fromModifiedDate = command.getDateModifiedFrom();
	Date toModifiedDate = command.getDateModifiedTo();

	if (fromModifiedDate != null) {
	    criteria.add(Restrictions.ge("dateModified", fromModifiedDate));
	}
	if (toModifiedDate != null) {
	    criteria.add(Restrictions.le("dateModified", toModifiedDate));
	}
    }

    private void appendSubmissionCountParameters(ProjectDetailCountsIO counts, Query query) {

	Long activeSubmissionCount = counts.getActiveSubmissionCount();
	Long completedSubmissionCount = counts.getActiveSubmissionCount();

	if (activeSubmissionCount != null) {
	    query.setLong("activeSubmissionCount", activeSubmissionCount);
	}

	if (completedSubmissionCount != null) {
	    query.setLong("completedSubmissionCount", completedSubmissionCount);
	}
    }

    private void appendSubmissionCountQuery(ProjectDetailCountsIO counts, StringBuilder builder) {

	if (counts.getActiveSubmissionCount() != null) {
	    builder.append(UPDATE_ACTIVE_SUBMISSION_COUNTS);
	}

	if (counts.getCompletedSubmissionCount() != null) {
	    builder.append(UPDATE_COMPLETED_SUBMISSION_COUNTS);
	}

    }

    private Criteria createElementQuery(Session session, UserProjectSearchRequest command,
	    PagedListInfo pagedListInfo) {

	Criteria criteria = session.createCriteria(ProjectDetail.class);
	criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

	addDatesToWhereClause(criteria, command);
	createJoinClause(criteria);

	criteria.add(Restrictions.eq("project.projectInfo.enabled", Boolean.TRUE));

	createOrderByClause(criteria, pagedListInfo);

	return criteria;

    }

    private void createJoinClause(Criteria criteria) {
	criteria.createAlias("project", "project", JoinType.INNER_JOIN);
	criteria.createAlias("languageDetails", "languageDetails", JoinType.LEFT_OUTER_JOIN);
	criteria.createAlias("userDetails", "userDetails", JoinType.LEFT_OUTER_JOIN);
    }

    private void createOrderByClause(Criteria criteria, PagedListInfo pagedListInfo) {

	String sortProperty = StringUtils.isEmpty(pagedListInfo.getSortProperty()) ? "projectDetailId"
		: pagedListInfo.getSortProperty();
	SortDirection sortDirection = pagedListInfo.getSortDirection();

	if (sortDirection == SortDirection.ASCENDING) {
	    criteria.addOrder(Order.asc(sortProperty));
	} else {
	    criteria.addOrder(Order.desc(sortProperty));
	}

    }

    @SuppressWarnings("unchecked")
    private List<ProjectDetail> findProjectUserDetails(final UserProjectSearchRequest command,
	    final PagedListInfo pagedListInfo) {
	HibernateCallback<List<ProjectDetail>> cb = new HibernateCallback<List<ProjectDetail>>() {
	    @Override
	    public List<ProjectDetail> doInHibernate(Session session) throws HibernateException, SQLException {
		final Criteria query = createElementQuery(session, command, pagedListInfo);

		final List<ProjectDetail> projectDetails = new ArrayList<ProjectDetail>();

		Set<Long> projectIds = command.getProjectIds();

		ChunkedExecutionHelper.executeChuncked(projectIds, new ChunkedListExecutionCallback<Long>() {
		    @Override
		    public void execute(List<Long> chunkedProjectIds) {

			initProjectIdsParameter(query, chunkedProjectIds);
			initQueryParameters(query, command);

			List<ProjectDetail> list = query.list();
			if (CollectionUtils.isNotEmpty(list)) {
			    projectDetails.addAll(list);
			}
		    }
		}, ChunkedExecutionHelper.DEFAULT_CHUNK_SIZE);

		return projectDetails;
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    private List<ProjectDetail> getAllProjectDetails(final boolean isPowerUser, final Set<Long> projectIds) {
	HibernateCallback<List<ProjectDetail>> cb = new HibernateCallback<List<ProjectDetail>>() {
	    @Override
	    public List<ProjectDetail> doInHibernate(Session session) throws HibernateException, SQLException {

		String queryString = REPORT_QUERY_BY_PROJECT;

		StringBuilder builder = new StringBuilder(queryString);

		if (!isPowerUser) {
		    builder.append("where pd.project.projectId in (:projectIds)");
		    builder.append(StringConstants.SPACE);
		}

		final Query query = session.createQuery(builder.toString());

		if (!isPowerUser) {
		    final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		    ChunkedExecutionHelper.executeChuncked(projectIds, new ChunkedListExecutionCallback<Long>() {
			@Override
			public void execute(List<Long> chunkedProjectIds) {
			    query.setParameterList("projectIds", chunkedProjectIds);

			    List<Map<String, Object>> list = query.list();
			    if (CollectionUtils.isNotEmpty(list)) {
				result.addAll(list);
			    }
			}
		    }, SIZE);
		}

		return query.list();
	    }
	};

	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    private List<ProjectLanguageDetail> getAllProjectLanguageDetails(final boolean isPowerUser,
	    final Set<Long> projectIds) {
	HibernateCallback<List<ProjectLanguageDetail>> cb = new HibernateCallback<List<ProjectLanguageDetail>>() {
	    @Override
	    public List<ProjectLanguageDetail> doInHibernate(Session session) throws HibernateException, SQLException {

		String queryString = REPORT_QUERY_BY_LANGUAGE;

		StringBuilder builder = new StringBuilder(queryString);

		if (!isPowerUser) {
		    builder.append("inner join pld.projectDetail pd ");
		    builder.append("where pd.project.projectId in (:projectIds)");
		}

		final Query query = session.createQuery(builder.toString());

		if (!isPowerUser) {
		    final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		    ChunkedExecutionHelper.executeChuncked(projectIds, new ChunkedListExecutionCallback<Long>() {
			@Override
			public void execute(List<Long> chunkedProjectIds) {
			    query.setParameterList("projectIds", chunkedProjectIds);

			    List<Map<String, Object>> list = query.list();
			    if (CollectionUtils.isNotEmpty(list)) {
				result.addAll(list);
			    }
			}
		    }, SIZE);
		}

		return query.list();
	    }
	};

	return execute(cb);
    }

    private void initDatesParameters(Query query, UserProjectSearchRequest command) {
	Date fromModifiedDate = command.getDateModifiedFrom();
	Date toModifiedDate = command.getDateModifiedTo();

	if (fromModifiedDate != null) {
	    query.setTimestamp("fromModifiedDate", fromModifiedDate);
	}

	if (toModifiedDate != null) {
	    query.setTimestamp("toModifiedDate", toModifiedDate);
	}
    }

    private void initProjectIdsParameter(Criteria criteria, List<Long> projectIds) {
	criteria.add(Restrictions.in("project.projectId", projectIds));
    }

    private void initQueryParameters(Criteria criteria, UserProjectSearchRequest command) {
	Set<String> languageIds = command.getLanguageIds();
	String projectName = command.getName();
	String projectShortcode = command.getShortCode();

	if (CollectionUtils.isNotEmpty(languageIds)) {
	    criteria.add(Restrictions.in("languageDetails.languageId", languageIds));
	}

	if (StringUtils.isNotEmpty(projectName)) {
	    criteria.add(Restrictions.ilike("project.projectInfo.name", projectName, MatchMode.ANYWHERE));
	}

	if (StringUtils.isNotEmpty(projectShortcode)) {
	    criteria.add(Restrictions.ilike("project.projectInfo.shortCode", projectShortcode, MatchMode.ANYWHERE));
	}
    }

    private boolean updateDashboardCounts(ProjectDetailCountsIO counts) {
	Transaction transaction = null;
	Session session = null;
	int success = 0;

	try {

	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    StringBuilder queryBuilder = new StringBuilder(UPDATE_DASHBOARD_COUNTS);

	    appendSubmissionCountQuery(counts, queryBuilder);

	    queryBuilder.append(UPDATE_DASHBOARD_COUNTS_WHERE);

	    final Query query = session.createQuery(queryBuilder.toString());
	    query.setLong("approvedTermCount", counts.getApprovedTermCount());
	    query.setLong("forbiddenTermCount", counts.getForbiddenTermCount());
	    query.setLong("pendingApprovalCount", counts.getPendingTermCount());
	    query.setLong("onHoldTermCount", counts.getOnHoldTermCount());

	    appendSubmissionCountParameters(counts, query);

	    query.setLong("termCount", counts.getTotalCount());
	    query.setLong("termEntryCount", counts.getTermEntryCount());
	    query.setLong("termInSubmissionCount", counts.getTermInSubmissionCount());
	    query.setLong("projectId", counts.getProjectId());
	    query.setTimestamp("dateModified", counts.getDateModified());

	    success = query.executeUpdate();

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

	return success == 1;

    }

    private boolean updateProjectDetail(ProjectDetailCountsIO counts, String queryName) {

	Transaction transaction = null;
	Session session = null;
	int success = 0;

	try {

	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    Query query = session.getNamedQuery(queryName);
	    query.setLong("approvedTermCount", counts.getApprovedTermCount());
	    query.setLong("forbiddenTermCount", counts.getForbiddenTermCount());
	    query.setLong("pendingApprovalCount", counts.getPendingTermCount());
	    query.setLong("onHoldTermCount", counts.getOnHoldTermCount());
	    query.setLong("termCount", counts.getTotalCount());
	    query.setLong("termEntryCount", counts.getTermEntryCount());
	    query.setLong("activeSubmissionCount", counts.getActiveSubmissionCount());
	    query.setLong("completedSubmissionCount", counts.getCompletedSubmissionCount());
	    query.setLong("termInSubmissionCount", counts.getTermInSubmissionCount());
	    query.setLong("projectId", counts.getProjectId());
	    query.setTimestamp("dateModified", counts.getDateModified());

	    success = query.executeUpdate();

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

	return success == 1;
    }

    private boolean updateProjectDetail(ProjectDetailInfo info, String queryName) {

	Date dateModified = info.getDateModified();

	long approvedTermCount = info.getTotalCount(info.getLanguageApprovedTermCount());
	long forbiddenTermCount = info.getTotalCount(info.getLanguageForbiddenTermCount());
	long pendingTermCount = info.getTotalCount(info.getLanguagePendingTermCount());
	long onHoldTermCount = info.getTotalCount(info.getLanguageOnHoldTermCount());

	long activeSubmissionCount = info.getActiveSubmissionCount().get();
	long completedSubmissionCount = info.getCompletedSubmissionCount().get();
	long termInSubmissionCount = info.getTotalCount(info.getLanguageTermInSubmissionCount());

	long termCount = approvedTermCount + forbiddenTermCount + pendingTermCount + onHoldTermCount
		+ termInSubmissionCount;

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

	    return query.executeUpdate();
	};

	return execute(cb) == 1;
    }
}
