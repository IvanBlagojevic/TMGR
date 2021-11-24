package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StatisticsServiceTest extends AbstractSolrGlossaryTest {

    @Autowired
    private StatisticsService _statisticsService;

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    @Test
    public void statisticsUpdateTest() {
	StatisticsInfo statisticsInfo = new StatisticsInfo(1L, "en-US");

	statisticsInfo.getAddedApprovedCount().addAndGet(1);
	statisticsInfo.getApprovedCount().addAndGet(2);

	statisticsInfo.getAddedPendingApprovalCount().addAndGet(3);
	statisticsInfo.getPendingApprovalCount().addAndGet(4);

	statisticsInfo.getAddedOnHoldCount().addAndGet(5);
	statisticsInfo.getOnHoldCount().addAndGet(6);

	statisticsInfo.getAddedBlacklistedCount().addAndGet(7);
	statisticsInfo.getBlackListedCount().addAndGet(8);

	statisticsInfo.getDeletedCount().addAndGet(9);

	List<Statistics> statisticsBefore = getStatisticsService().findStatisticsByProjectId(1L);

	/* Test default Statistics counts before update */
	statisticsBefore.forEach(statistics -> assertStatisticsCounts(1, statistics));

	getStatisticsService().updateStatistics(statisticsInfo);

	List<Statistics> statisticsAfter = getStatisticsService().findStatisticsByProjectId(1L);

	for (int i = 0; i < 3; i++) {
	    Statistics statistics = statisticsAfter.get(i);
	    assertStatisticsCounts(statistics);
	}

	/* Rest statistics records should not be changed */
	for (int i = 4; i < statisticsAfter.size(); i++) {
	    Statistics statistics = statisticsAfter.get(i);
	    assertStatisticsCounts(1, statistics);
	}

    }

    @Test
    public void updateStatisticsOnImportTest() {
	ImportSummary importSummary = new ImportSummary();

	Map<String, ImportSummary.CountWrapper> countMap = new HashMap<>();

	ImportSummary.CountWrapper en_Wrapper = new ImportSummary.CountWrapper();
	ImportSummary.CountWrapper de_Wrapper = new ImportSummary.CountWrapper();
	ImportSummary.CountWrapper fr_Wrapper = new ImportSummary.CountWrapper();

	en_Wrapper.getDeletedTerms().increment();
	de_Wrapper.getUpdatedTermIds().add(UUID.randomUUID().toString());

	en_Wrapper.getAddedApprovedStatisticsCount().increment();
	de_Wrapper.getAddedApprovedStatisticsCount().increment();
	fr_Wrapper.getAddedPendingStatisticsCount().increment();

	countMap.put("en-US", en_Wrapper); //$NON-NLS-1$
	countMap.put("de-DE", de_Wrapper); //$NON-NLS-1$
	countMap.put("fr-FR", fr_Wrapper); //$NON-NLS-1$

	importSummary.setNoImportedTermsPerLanguage(countMap);

	getStatisticsService().updateStatisticsOnImport(1L, importSummary);

	List<Statistics> sts = getStatisticsService().findStatisticsByProjectId(1L, ProjectUserLanguage.class);

	for (Statistics s : sts) {
	    switch (s.getProjectUserLanguage().getLanguage()) {
	    case "en-US": //$NON-NLS-1$
		assertEquals(2, s.getAddedApproved());
		assertEquals(2, s.getDeleted());
		break;
	    case "de-DE": //$NON-NLS-1$
		assertEquals(1, s.getAddedApproved());
		assertEquals(1, s.getUpdated());
		break;
	    case "fr-FR": //$NON-NLS-1$
		assertEquals(2, s.getAddedPending());
		break;
	    default:
		throw new IllegalArgumentException("Invalid language ID."); //$NON-NLS-1$
	    }
	}
    }

    private void assertStatisticsCounts(Statistics statistics) {
	Assert.assertEquals(2, statistics.getAddedApproved());
	Assert.assertEquals(3, statistics.getApproved());

	Assert.assertEquals(4, statistics.getAddedPending());
	Assert.assertEquals(5, statistics.getDemoted());

	Assert.assertEquals(6, statistics.getAddedOnHold());
	Assert.assertEquals(7, statistics.getOnHold());

	Assert.assertEquals(8, statistics.getAddedBlacklisted());
	Assert.assertEquals(9, statistics.getBlacklisted());

	Assert.assertEquals(10, statistics.getDeleted());
    }

    private void assertStatisticsCounts(int counts, Statistics statistics) {
	Assert.assertEquals(counts, statistics.getAddedApproved());
	Assert.assertEquals(counts, statistics.getApproved());

	Assert.assertEquals(counts, statistics.getAddedPending());
	Assert.assertEquals(counts, statistics.getDemoted());

	Assert.assertEquals(counts, statistics.getAddedOnHold());
	Assert.assertEquals(counts, statistics.getOnHold());

	Assert.assertEquals(counts, statistics.getAddedBlacklisted());
	Assert.assertEquals(counts, statistics.getBlacklisted());

	Assert.assertEquals(counts, statistics.getDeleted());
    }

}
