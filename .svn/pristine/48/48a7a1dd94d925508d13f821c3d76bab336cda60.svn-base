package org.gs4tr.termmanager.dao.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.dao.hibernate.AbstractHibernateGenericDao;
import org.gs4tr.foundation.modules.dao.hibernate.HibernateCallback;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.dao.utils.DaoUtils;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository("statisticsDAO")
public class StatisticsDAOImpl extends AbstractHibernateGenericDao<Statistics, Long> implements StatisticsDAO {

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public StatisticsDAOImpl() {
	super(Statistics.class, Long.class);
    }

    @Override
    public void addOrUpdateProjectUserLangStatistics(ProjectUserLanguage pul, String weeklyReport, String dailyReport) {
	Statistics stWeekly = new Statistics();
	stWeekly.setReportType(weeklyReport);
	stWeekly.setProjectUserLanguage(pul);
	save(stWeekly);

	Statistics stDaily = new Statistics();
	stDaily.setReportType(dailyReport);
	stDaily.setProjectUserLanguage(pul);
	save(stDaily);
    }

    @Override
    public void clearUserStatistics(final Collection<Long> statisticsIds) {
	HibernateCallback<Integer> cb = new HibernateCallback<Integer>() {
	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.clearUserStatistics");
		query.setParameterList("statisticsIds", statisticsIds);

		return query.executeUpdate();
	    }
	};

	execute(cb);
    }

    @Override
    public void decrementStatistics(Statistics statistics) {
	HibernateCallback<Integer> cb = new HibernateCallback<Integer>() {
	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.decrementStatistics");
		query.setLong("newAddedApproved", statistics.getAddedApproved());
		query.setLong("newApproved", statistics.getApproved());
		query.setLong("newAddedPending", statistics.getAddedPending());
		query.setLong("newPending", statistics.getDemoted());
		query.setLong("newAddedOnHold", statistics.getAddedOnHold());
		query.setLong("newOnHold", statistics.getOnHold());
		query.setLong("newAddedBlacklisted", statistics.getAddedBlacklisted());
		query.setLong("newBlacklisted", statistics.getBlacklisted());
		query.setLong("newDeleted", statistics.getDeleted());
		query.setLong("newUpdated", statistics.getUpdated());
		query.setLong("statisticsId", statistics.getStatisticsId());
		return query.executeUpdate();
	    }
	};

	execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Statistics> getStatisticsByProjectAndLanguage(final Long projectId, final String language) {
	HibernateCallback<List<Statistics>> cb = new HibernateCallback<List<Statistics>>() {
	    @Override
	    public List<Statistics> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.getStatisticsByProjectAndLanguage");
		query.setLong("projectId", projectId);
		query.setString("language", language);

		return query.list();
	    }

	};
	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Statistics> getStatisticsByProjectAndLanguages(final Long projectId, final Set<String> languages) {
	HibernateCallback<List<Statistics>> cb = new HibernateCallback<List<Statistics>>() {
	    @Override
	    public List<Statistics> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.getStatisticsByProjectAndLanguages");
		query.setLong("projectId", projectId);
		query.setParameterList("languages", languages);

		return query.list();
	    }

	};
	return execute(cb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Statistics> getStatisticsByProjectId(final Long projectId, final Class<?>... classesToFetch) {
	HibernateCallback<List<Statistics>> cb = new HibernateCallback<List<Statistics>>() {
	    @Override
	    public List<Statistics> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.getStatisticsByProjectId");
		query.setLong("projectId", projectId);

		List<Statistics> sts = query.list();
		if (Objects.nonNull(classesToFetch) && CollectionUtils.isNotEmpty(sts)) {
		    for (Statistics s : sts) {
			DaoUtils.initializeEntities(s, classesToFetch);
		    }
		}

		return sts;
	    }

	};
	return execute(cb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Statistics> getStatisticsByUserId(final Long userId, final String reportType) {
	HibernateCallback<List<Statistics>> cb = new HibernateCallback<List<Statistics>>() {
	    @Override
	    public List<Statistics> doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.getStatisticsByUserId");
		query.setLong("userId", userId);
		query.setString("reportType", reportType);

		return query.list();
	    }
	};
	return execute(cb);
    }

    public void updateStatistics(StatisticsInfo statisticsInfo) {
	HibernateCallback<Integer> cb = new HibernateCallback<Integer>() {
	    @Override
	    public Integer doInHibernate(Session session) throws HibernateException, SQLException {
		Query query = session.getNamedQuery("Statistics.updateStatistics");
		query.setLong("newAddedApproved", statisticsInfo.getAddedApprovedCount().longValue());
		query.setLong("newApproved", statisticsInfo.getApprovedCount().longValue());
		query.setLong("newAddedPending", statisticsInfo.getAddedPendingApprovalCount().longValue());
		query.setLong("newPending", statisticsInfo.getPendingApprovalCount().longValue());
		query.setLong("newAddedOnHold", statisticsInfo.getAddedOnHoldCount().longValue());
		query.setLong("newOnHold", statisticsInfo.getOnHoldCount().longValue());
		query.setLong("newAddedBlacklisted", statisticsInfo.getAddedBlacklistedCount().longValue());
		query.setLong("newBlacklisted", statisticsInfo.getBlackListedCount().longValue());
		query.setLong("newDeleted", statisticsInfo.getDeletedCount().longValue());
		query.setLong("projectId", statisticsInfo.getProjectId());
		query.setString("language", statisticsInfo.getLanguageId());
		query.setLong("newUpdated", statisticsInfo.getUpdatedCount().longValue());
		return query.executeUpdate();
	    }
	};

	execute(cb);
    }

    @Override
    public boolean updateStatistics(StatisticInfoIO statisticInfo) {

	Session session = null;
	Transaction transaction = null;
	int result = 0;

	try {
	    session = getSessionFactory().openSession();
	    transaction = session.beginTransaction();

	    Query query = session.getNamedQuery("Statistics.updateStatistics");
	    query.setLong("newAddedApproved", statisticInfo.getAddedApprovedCount());
	    query.setLong("newApproved", statisticInfo.getApprovedCount());
	    query.setLong("newAddedPending", statisticInfo.getAddedPendingApprovalCount());
	    query.setLong("newPending", statisticInfo.getPendingApprovalCount());
	    query.setLong("newAddedOnHold", statisticInfo.getAddedOnHoldCount());
	    query.setLong("newOnHold", statisticInfo.getOnHoldCount());
	    query.setLong("newAddedBlacklisted", statisticInfo.getAddedBlacklistedCount());
	    query.setLong("newBlacklisted", statisticInfo.getBlackListedCount());
	    query.setLong("newDeleted", statisticInfo.getDeletedCount());
	    query.setLong("projectId", statisticInfo.getProjectId());
	    query.setString("language", statisticInfo.getLanguageId());
	    query.setLong("newUpdated", statisticInfo.getUpdatedCount());

	    result = query.executeUpdate();

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

	return result == 1;

    }
}
