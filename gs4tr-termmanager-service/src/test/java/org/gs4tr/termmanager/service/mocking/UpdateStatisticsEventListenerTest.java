package org.gs4tr.termmanager.service.mocking;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.dao.ProjectUserLanguageDAO;
import org.gs4tr.termmanager.dao.StatisticsDAO;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.UpdateStatisticsEventListener;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

@TestSuite("service")
public class UpdateStatisticsEventListenerTest extends AbstractServiceTest {

    @Autowired
    private ProjectUserLanguageDAO _projectUserLanguageDao;

    @Autowired
    private StatisticsDAO _statisticsDao;

    @Autowired
    private UpdateStatisticsEventListener _statisticsEventListener;

    @Captor
    protected ArgumentCaptor<StatisticsInfo> _captor;

    @Before
    public void setUp() {
	reset(getProjectUserLanguageDao());
	reset(getStatisticsDao());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("updateStatisticsAddedPendingTerms")
    public void updateStatisticsAddedPendingTermsTest() {
	/*
	 * TERII-3752: Added pending terms is not showing in correct column of
	 * notification.
	 */
	EventMessage message = getModelObject("message", EventMessage.class);

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();
	statisticsInfos.add(new StatisticsInfo(1L, "en-US"));

	message.addContextVariable(EventMessage.VARIABLE_STATISTICS, statisticsInfos);

	List<ProjectUserLanguage> userLanguages = getModelObject("userLanguages", List.class);

	when(getProjectUserLanguageDao().getProjectUserLanguagesByProject(anyLong())).thenReturn(userLanguages);

	getStatisticsEventListener().notify(message);

	Assert.assertEquals(1, statisticsInfos.size());

	for (StatisticsInfo stats : statisticsInfos) {
	    assertEquals(1, stats.getAddedPendingApprovalCount().longValue());
	    assertEquals(0, stats.getAddedApprovedCount().longValue());
	    assertEquals(0, stats.getApprovedCount().longValue());
	    assertEquals(0, stats.getDeletedCount().longValue());
	    assertEquals(0, stats.getPendingApprovalCount().longValue());
	    assertEquals(0, stats.getUpdatedCount().longValue());
	}
    }

    @Test
    @TestCase("updateStatisticsMultipleAddedApprovedTerms")
    public void updateStatisticsMultipleAddedApprovedTermsTest() {

	EventMessage message = getModelObject("message", EventMessage.class);

	getStatisticsEventListener().notify(message);
	getStatisticsEventListener().notify(message);

	verify(getStatisticsDao(), never()).save(any(Statistics.class));

	Set<StatisticsInfo> statistics = message.getContextVariable(EventMessage.VARIABLE_STATISTICS);

	Assert.assertEquals(2, statistics.size());

	/* Only statistics with en-US locale should be incremented */
	for (StatisticsInfo statisticsInfo : statistics) {
	    if (statisticsInfo.getLanguageId().equals("en-US")) {
		assertEquals(2, statisticsInfo.getAddedApprovedCount().longValue());
		assertEquals(0, statisticsInfo.getAddedPendingApprovalCount().longValue());
		assertEquals(0, statisticsInfo.getApprovedCount().longValue());
		assertEquals(0, statisticsInfo.getDeletedCount().longValue());
		assertEquals(0, statisticsInfo.getPendingApprovalCount().longValue());
		assertEquals(0, statisticsInfo.getUpdatedCount().longValue());
	    }
	}

    }

    /* TERII-4931 | Update email notifications */

    @Test
    @TestCase("updateStatisticsMultipleAddedApprovedTerms")
    public void updateStatisticsWithAllStatusesTermsTest() {

	String termId_01 = "474e93ae-7264-4088-9d54-term00000001";

	EventMessage message = getModelObject("message", EventMessage.class);

	message.addContextVariable(EventMessage.VARIABLE_STATUS_TYPE, ItemStatusTypeHolder.PROCESSED.getName());

	TermEntry termEntry = new TermEntry();
	Term term = new Term();
	term.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	term.setStatusOld(ItemStatusTypeHolder.WAITING.getName());
	term.setUuId(termId_01);
	termEntry.addTerm(term);

	message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);

	UpdateCommand cmd = message.getContextVariable(EventMessage.VARIABLE_COMMAND);

	cmd.setMarkerId(termId_01);

	performNotify(1, message);

	cmd.setStatus(ItemStatusTypeHolder.WAITING.getName());

	performNotify(2, message);

	cmd.setStatus(ItemStatusTypeHolder.ON_HOLD.getName());

	performNotify(3, message);

	cmd.setStatus(ItemStatusTypeHolder.BLACKLISTED.getName());

	performNotify(4, message);

	cmd.setCommand(UpdateCommand.CommandEnum.UPDATE.getName());

	cmd.setStatus(ItemStatusTypeHolder.PROCESSED.getName());

	performNotify(5, message);

	cmd.setStatus(ItemStatusTypeHolder.WAITING.getName());

	performNotify(6, message);

	cmd.setStatus(ItemStatusTypeHolder.ON_HOLD.getName());

	performNotify(7, message);

	cmd.setStatus(ItemStatusTypeHolder.BLACKLISTED.getName());

	performNotify(8, message);

	cmd.setCommand(UpdateCommand.CommandEnum.REMOVE.getName());

	performNotify(9, message);

	Set<StatisticsInfo> statistics = message.getContextVariable(EventMessage.VARIABLE_STATISTICS);

	Assert.assertEquals(2, statistics.size());

	for (StatisticsInfo statisticsInfo : statistics) {
	    if (statisticsInfo.getLanguageId().equals("en-US")) {
		assertEquals(1, statisticsInfo.getAddedApprovedCount().longValue());
		assertEquals(2, statisticsInfo.getAddedPendingApprovalCount().longValue());
		assertEquals(3, statisticsInfo.getAddedOnHoldCount().longValue());
		assertEquals(4, statisticsInfo.getAddedBlacklistedCount().longValue());
		assertEquals(5, statisticsInfo.getApprovedCount().longValue());
		assertEquals(6, statisticsInfo.getPendingApprovalCount().longValue());
		assertEquals(7, statisticsInfo.getOnHoldCount().longValue());
		assertEquals(8, statisticsInfo.getBlackListedCount().longValue());

		assertEquals(9, statisticsInfo.getDeletedCount().longValue());
		assertEquals(26, statisticsInfo.getUpdatedCount().longValue());
	    }
	}

    }

    private void assertStatisticsInfos(Set<StatisticsInfo> statistics, int addedApprovedCount,
	    int addedPendingApprovalCount, int approvedCount, int deletedCount, int pendingApprovalCount,
	    int updatedCount, int addedOnHoldCount, int addedBlacklistedCount, int onHoldCount, int blackListedCount) {

	for (StatisticsInfo statisticsInfo : statistics) {
	    assertEquals(addedApprovedCount, statisticsInfo.getAddedApprovedCount().longValue());
	    assertEquals(addedPendingApprovalCount, statisticsInfo.getAddedPendingApprovalCount().longValue());
	    assertEquals(approvedCount, statisticsInfo.getApprovedCount().longValue());
	    assertEquals(deletedCount, statisticsInfo.getDeletedCount().longValue());
	    assertEquals(pendingApprovalCount, statisticsInfo.getPendingApprovalCount().longValue());
	    assertEquals(updatedCount, statisticsInfo.getUpdatedCount().longValue());
	    assertEquals(addedOnHoldCount, statisticsInfo.getAddedOnHoldCount().longValue());
	    assertEquals(addedBlacklistedCount, statisticsInfo.getAddedBlacklistedCount().longValue());
	    assertEquals(onHoldCount, statisticsInfo.getOnHoldCount().longValue());
	    assertEquals(blackListedCount, statisticsInfo.getBlackListedCount().longValue());
	}
    }

    private ProjectUserLanguageDAO getProjectUserLanguageDao() {
	return _projectUserLanguageDao;
    }

    private StatisticsDAO getStatisticsDao() {
	return _statisticsDao;
    }

    private UpdateStatisticsEventListener getStatisticsEventListener() {
	return _statisticsEventListener;
    }

    private void performNotify(int numOfTimes, EventMessage message) {
	for (int i = 0; i < numOfTimes; i++) {
	    getStatisticsEventListener().notify(message);
	}
    }
}
