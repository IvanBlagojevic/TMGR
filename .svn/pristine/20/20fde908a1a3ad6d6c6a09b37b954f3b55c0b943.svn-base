package org.gs4tr.termmanager.io.tests.edd.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.io.edd.event.UpdateCountEvent;
import org.gs4tr.termmanager.io.edd.handler.StatisticsUpdateHandler;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StatisticsUpdateHandlerTest extends AbstractIOTest {

    @Autowired
    private StatisticsUpdateHandler _statisticsUpdateHandler;

    @Test(expected = IllegalArgumentException.class)
    public void statisticsDetailsUpdateFail() {
	getStatisticsUpdateHandler().onEvent(new UpdateCountEvent(null, null));
    }

    @Test
    public void statisticsDetailsUpdatedSuccessfullyTest() {

	List<Statistics> beforeUpdate = getStatisticsDAO().getStatisticsByProjectId(TestHelper.PROJECT_ID_1);
	assertNotNull(beforeUpdate);

	Statistics englishStatisticBU = getEnglishWeeklyStatistics(beforeUpdate);
	assertNotNull(englishStatisticBU);

	assertEquals(0, englishStatisticBU.getAddedApproved());
	assertEquals(0, englishStatisticBU.getApproved());
	assertEquals(0, englishStatisticBU.getDemoted());
	assertEquals(0, englishStatisticBU.getAddedPending());
	assertEquals(0, englishStatisticBU.getDeleted());
	assertEquals(0, englishStatisticBU.getAddedBlacklisted());
	assertEquals(0, englishStatisticBU.getAddedOnHold());
	assertEquals(0, englishStatisticBU.getBlacklisted());
	assertEquals(0, englishStatisticBU.getOnHold());
	assertEquals(0, englishStatisticBU.getUpdated());

	Set<StatisticInfoIO> infoDetails = TestHelper.createStatisticsInfoDetails();

	getStatisticsUpdateHandler().onEvent(new UpdateCountEvent(null, infoDetails));

	getStatisticsDAO().flush();
	getStatisticsDAO().clear();

	List<Statistics> afterUpdate = getStatisticsDAO().getStatisticsByProjectId(TestHelper.PROJECT_ID_1);
	assertNotNull(afterUpdate);

	Statistics englishStatisticAU = getEnglishWeeklyStatistics(afterUpdate);
	assertNotNull(englishStatisticAU);

	assertEquals(1, englishStatisticAU.getAddedApproved());
	assertEquals(1, englishStatisticAU.getApproved());
	assertEquals(1, englishStatisticAU.getDemoted());
	assertEquals(1, englishStatisticAU.getAddedPending());
	assertEquals(1, englishStatisticAU.getDeleted());
	assertEquals(1, englishStatisticAU.getAddedBlacklisted());
	assertEquals(1, englishStatisticAU.getAddedOnHold());
	assertEquals(1, englishStatisticAU.getBlacklisted());
	assertEquals(1, englishStatisticAU.getOnHold());
	assertEquals(1, englishStatisticAU.getUpdated());

    }

    private Statistics getEnglishWeeklyStatistics(List<Statistics> statistics) {

	return statistics.stream().filter(
		s -> s.getProjectUserLanguage().getLanguage().equals(LANGUAGE_ID) && s.getReportType().equals("weekly"))
		.findFirst().orElse(null);
    }

    private StatisticsUpdateHandler getStatisticsUpdateHandler() {
	return _statisticsUpdateHandler;
    }
}
