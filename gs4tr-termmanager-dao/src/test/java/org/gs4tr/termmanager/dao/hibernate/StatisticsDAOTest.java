package org.gs4tr.termmanager.dao.hibernate;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

public class StatisticsDAOTest extends AbstractSpringDAOIntegrationTest {

    @Autowired
    private StatisticsDAO _statisticsDAO;

    @Test
    public void testClearUserStatistics() {
	Set<Long> stIds = new HashSet<Long>();
	stIds.add(1L);
	stIds.add(2L);

	StatisticsDAO statisticsDAO = getStatisticsDAO();

	List<Statistics> sts1 = statisticsDAO.findByIds(stIds);
	for (Statistics st : sts1) {
	    assertStatisticsCounts(1, st);
	}

	statisticsDAO.clearUserStatistics(stIds);
	statisticsDAO.flush();
	statisticsDAO.clear();

	List<Statistics> sts2 = statisticsDAO.findByIds(stIds);
	for (Statistics st : sts2) {
	    assertStatisticsCounts(0, st);
	}
    }

    @Test
    public void testGetStatisticsByProjectId() {
	List<Statistics> sts = getStatisticsDAO().getStatisticsByProjectId(2L);
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));
    }

    @Test
    public void testGetStatisticsByUserId() {
	Long userId = 2L;

	List<Statistics> sts = getStatisticsDAO().getStatisticsByUserId(userId, "daily");
	Assert.assertTrue(CollectionUtils.isNotEmpty(sts));
    }

    @Test
    public void testUpdateStatistics() {
	StatisticsInfo statisticsInfo = new StatisticsInfo(2L, "en-US");

	statisticsInfo.getAddedApprovedCount().addAndGet(1);
	statisticsInfo.getApprovedCount().addAndGet(2);

	statisticsInfo.getAddedPendingApprovalCount().addAndGet(3);
	statisticsInfo.getPendingApprovalCount().addAndGet(4);

	statisticsInfo.getAddedOnHoldCount().addAndGet(5);
	statisticsInfo.getOnHoldCount().addAndGet(6);

	statisticsInfo.getAddedBlacklistedCount().addAndGet(7);
	statisticsInfo.getBlackListedCount().addAndGet(8);

	statisticsInfo.getDeletedCount().addAndGet(9);

	List<Statistics> statisticsBefore = getStatisticsDAO().getStatisticsByProjectId(2L);
	getStatisticsDAO().flush();
	getStatisticsDAO().clear();

	/* Test default Statistics counts before update */
	statisticsBefore.forEach(statistics -> assertStatisticsCounts(1, statistics));

	getStatisticsDAO().updateStatistics(statisticsInfo);

	List<Statistics> statisticsAfter = getStatisticsDAO().getStatisticsByProjectId(2L);

	Statistics statisticsWeekly = statisticsAfter.get(0);

	Assert.assertEquals("weekly", statisticsWeekly.getReportType());

	Assert.assertEquals(2, statisticsWeekly.getAddedApproved());
	Assert.assertEquals(3, statisticsWeekly.getApproved());

	Assert.assertEquals(4, statisticsWeekly.getAddedPending());
	Assert.assertEquals(5, statisticsWeekly.getDemoted());

	Assert.assertEquals(6, statisticsWeekly.getAddedOnHold());
	Assert.assertEquals(7, statisticsWeekly.getOnHold());

	Assert.assertEquals(8, statisticsWeekly.getAddedBlacklisted());
	Assert.assertEquals(9, statisticsWeekly.getBlacklisted());

	Assert.assertEquals(10, statisticsWeekly.getDeleted());

	Statistics statisticsDaily = statisticsAfter.get(1);

	Assert.assertEquals("daily", statisticsDaily.getReportType());

	Assert.assertEquals(2, statisticsDaily.getAddedApproved());
	Assert.assertEquals(3, statisticsDaily.getApproved());

	Assert.assertEquals(4, statisticsDaily.getAddedPending());
	Assert.assertEquals(5, statisticsDaily.getDemoted());

	Assert.assertEquals(6, statisticsDaily.getAddedOnHold());
	Assert.assertEquals(7, statisticsDaily.getOnHold());

	Assert.assertEquals(8, statisticsDaily.getAddedBlacklisted());
	Assert.assertEquals(9, statisticsDaily.getBlacklisted());

	Assert.assertEquals(10, statisticsDaily.getDeleted());

	/* Rest statistics records should not be changed */
	for (int i = 2; i < statisticsAfter.size(); i++) {
	    Statistics statistics = statisticsAfter.get(i);
	    assertStatisticsCounts(1, statistics);
	}

    }

    @Test
    public void updateStatisticsWithStatisticsInfoIOTest() {

	StatisticsDAO statisticsDAO = getStatisticsDAO();

	long projectId = 2L;
	String languageId = "en-US";

	List<Statistics> beforeUpdate = statisticsDAO.getStatisticsByProjectAndLanguage(projectId, languageId);
	assertTrue(CollectionUtils.isNotEmpty(beforeUpdate));
	assertEquals(2, beforeUpdate.size());

	long addedApprovedCountBeforeUpdate = beforeUpdate.get(0).getAddedApproved();
	long addedBlacklistedCountBeforeUpdate = beforeUpdate.get(0).getAddedApproved();
	long addedOnHoldCountBeforeUpdate = beforeUpdate.get(0).getOnHold();
	long addedPendingApprovalCountBeforeUpdate = beforeUpdate.get(0).getAddedPending();
	long approvedCountBeforeUpdate = beforeUpdate.get(0).getApproved();
	long blacklistedCountBeforeUpdate = beforeUpdate.get(0).getBlacklisted();
	long deletedCountBeforeUpdate = beforeUpdate.get(0).getDeleted();
	long onHoldCountBeforeUpdate = beforeUpdate.get(0).getOnHold();
	long pendingApprovalCountBeforeUpdate = beforeUpdate.get(0).getDemoted();
	long updatedCountBeforeUpdate = beforeUpdate.get(0).getUpdated();

	StatisticInfoIO info = createStatisticsInfo(projectId, languageId);

	long addedApprovedCount = info.getAddedApprovedCount();
	long addedBlacklistedCount = info.getAddedBlacklistedCount();
	long addedOnHoldCount = info.getAddedOnHoldCount();
	long addedPendingApprovalCount = info.getAddedPendingApprovalCount();
	long approvedCount = info.getApprovedCount();
	long blacklistedCount = info.getBlackListedCount();
	long deletedCount = info.getDeletedCount();
	long onHoldCount = info.getOnHoldCount();
	long pendingApprovalCount = info.getPendingApprovalCount();
	long updatedCount = info.getUpdatedCount();

	statisticsDAO.updateStatistics(info);

	statisticsDAO.flush();
	statisticsDAO.clear();

	List<Statistics> afterUpdate = statisticsDAO.getStatisticsByProjectAndLanguage(projectId, languageId);

	for (Statistics statistics : afterUpdate) {
	    assertEquals(addedApprovedCountBeforeUpdate + addedApprovedCount, statistics.getAddedApproved());
	    assertEquals(addedBlacklistedCountBeforeUpdate + addedBlacklistedCount, statistics.getAddedApproved());
	    assertEquals(addedOnHoldCountBeforeUpdate + addedOnHoldCount, statistics.getOnHold());
	    assertEquals(addedPendingApprovalCountBeforeUpdate + addedPendingApprovalCount,
		    statistics.getAddedPending());
	    assertEquals(approvedCountBeforeUpdate + approvedCount, statistics.getApproved());
	    assertEquals(blacklistedCountBeforeUpdate + blacklistedCount, statistics.getBlacklisted());
	    assertEquals(deletedCountBeforeUpdate + deletedCount, statistics.getDeleted());
	    assertEquals(onHoldCountBeforeUpdate + onHoldCount, statistics.getOnHold());
	    assertEquals(pendingApprovalCountBeforeUpdate + pendingApprovalCount, statistics.getDemoted());
	    assertEquals(updatedCountBeforeUpdate + updatedCount, statistics.getUpdated());
	}

    }

    private void assertStatisticsCounts(int counts, Statistics statistics) {
	org.junit.Assert.assertEquals(counts, statistics.getAddedApproved());
	org.junit.Assert.assertEquals(counts, statistics.getApproved());

	org.junit.Assert.assertEquals(counts, statistics.getAddedPending());
	org.junit.Assert.assertEquals(counts, statistics.getDemoted());

	org.junit.Assert.assertEquals(counts, statistics.getAddedOnHold());
	org.junit.Assert.assertEquals(counts, statistics.getOnHold());

	org.junit.Assert.assertEquals(counts, statistics.getAddedBlacklisted());
	org.junit.Assert.assertEquals(counts, statistics.getBlacklisted());

	org.junit.Assert.assertEquals(counts, statistics.getDeleted());
    }

    private StatisticInfoIO createStatisticsInfo(long projectId, String languageId) {
	StatisticInfoIO infoIO = new StatisticInfoIO();
	infoIO.setProjectId(projectId);
	infoIO.setLanguageId(languageId);
	infoIO.setAddedApprovedCount(5L);
	infoIO.setAddedBlacklistedCount(5L);
	infoIO.setAddedOnHoldCount(5L);
	infoIO.setAddedPendingApprovalCount(5L);
	infoIO.setApprovedCount(5L);
	infoIO.setBlackListedCount(5L);
	infoIO.setDeletedCount(5L);
	infoIO.setOnHoldCount(5L);
	infoIO.setPendingApprovalCount(5L);
	infoIO.setUpdatedCount(5L);
	return infoIO;
    }

    private StatisticsDAO getStatisticsDAO() {
	return _statisticsDAO;
    }
}
