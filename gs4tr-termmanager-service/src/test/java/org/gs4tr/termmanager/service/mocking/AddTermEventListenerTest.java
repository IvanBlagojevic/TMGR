package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.AddTermEventListener;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class AddTermEventListenerTest extends AbstractServiceTest {

    private static final String EN_US = "en-US";

    private static final String MESSAGE_ID = "MESSAGEID";
    private Boolean UPDATE_USER_LATEST_CHANGE = Boolean.TRUE;
    @Autowired
    private AddTermEventListener _listener;
    private ProjectDetail _projectDetail;
    private TermEntry _termEntry;
    private TmProject _tmProject;
    private TmUserProfile _tmUser;

    /*
     * TERII-2999: Term entry count not working properly after importing some
     * glossary.
     */
    @Test
    @TestCase("addTermEventListener")
    public void addApproveTermTest() {
	UpdateCommand command = getModelObject("command1", UpdateCommand.class);

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	ProjectDetailInfo info = new ProjectDetailInfo(getTmProject().getProjectId());

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), processed,
		UPDATE_USER_LATEST_CHANGE, info);

	getListener().notify(message);

	Term enTerm = getTermEntry().getLanguageTerms().get(EN_US).iterator().next();

	validateTermFields(enTerm);

	assertEquals(processed, enTerm.getStatus());

	/* verify that term entry count is increased */
	assertEquals(1l, info.getTermEntryCount().get());
	assertEquals(1l, info.getUserTermEntryCount().values().stream().mapToLong(AtomicLong::get).sum());

	/* verify that term count is increased */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageForbiddenTermCount()));

	assertEquals(0l, info.getTotalCount(info.getLanguageForbiddenTermCount()));
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	assertNotNull(info.getDateModified());
    }

    /*
     * TERII-3030: Blacklist count is not increased when we add term with blacklist
     * status
     */
    @Test
    @TestCase("addTermEventListener")
    public void addBlackListTermTest() {
	UpdateCommand command = getModelObject("command2", UpdateCommand.class);

	final String blackListed = ItemStatusTypeHolder.BLACKLISTED.getName();

	ProjectDetailInfo info = new ProjectDetailInfo(getTmProject().getProjectId());

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), blackListed,
		UPDATE_USER_LATEST_CHANGE, info);

	getListener().notify(message);

	Term enTerm = getTermEntry().getLanguageTerms().get(EN_US).iterator().next();

	validateTermFields(enTerm);

	assertEquals(blackListed, enTerm.getStatus());

	/* verify that term entry count is increased */
	assertEquals(1l, info.getTermEntryCount().get());
	assertEquals(1l, info.getUserTermEntryCount().values().stream().mapToLong(AtomicLong::get).sum());

	/* verify that forbidden term count is increased */
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
	assertEquals(1l, info.getTotalCount(info.getLanguageForbiddenTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	assertNotNull(info.getDateModified());
    }

    public AddTermEventListener getListener() {
	return _listener;
    }

    @Override
    public ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    public TermEntry getTermEntry() {
	return _termEntry;
    }

    public TmProject getTmProject() {
	return _tmProject;
    }

    public TmUserProfile getTmUser() {
	return _tmUser;
    }

    /*
     * TERII-5706 After merging approved and demoted terms from floating-multi
     * editor, merged term has pending status
     */
    @Test
    @TestCase("addTermEventListener")
    public void sameTermsShouldBeMergedAsHigherLevelStatusTest() {
	UpdateCommand command = getModelObject("command1", UpdateCommand.class);
	command.setCommand("add");
	command.setItemType("term");

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	ProjectDetailInfo info = new ProjectDetailInfo(getTmProject().getProjectId());
	info.incrementPendingTermCount("en-US");

	/* existing term status(pending) is lower level than added term(approved) */
	_termEntry = getModelObject("termEntry01", TermEntry.class);

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), processed,
		UPDATE_USER_LATEST_CHANGE, info);

	getListener().notify(message);

	Term enTerm = getTermEntry().getLanguageTerms().get(EN_US).iterator().next();

	validateTermFields(enTerm);

	assertEquals(processed, enTerm.getStatus());

	/* approved term count is increased */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* pending term count is decreased */
	assertEquals(0l, info.getTotalCount(info.getLanguagePendingTermCount()));

	assertNotNull(info.getDateModified());
    }

    @Before
    public void setUp() {
	_tmProject = getModelObject("tmProject", TmProject.class);
	_tmUser = (TmUserProfile) UserProfileContext.getCurrentUserProfile();
	_projectDetail = getModelObject("projectDetail", ProjectDetail.class);
	_termEntry = getModelObject("emptyTermEntry", TermEntry.class);

	ProjectUserDetail projectUserDetail = new ProjectUserDetail(getTmUser(), getProjectDetail());

	Set<ProjectUserDetail> userDetails = new HashSet<>();

	userDetails.add(projectUserDetail);

	_projectDetail.setUserDetails(userDetails);
    }

    private EventMessage createEventMessage(TmProject tmProject, UpdateCommand command, TermEntry termEntry,
	    String status, Boolean updateUserLatestChange, ProjectDetailInfo info) {
	EventMessage message = new EventMessage(MESSAGE_ID);
	message.addContextVariable(EventMessage.VARIABLE_PROJECT, tmProject);
	message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
	message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);
	message.addContextVariable(EventMessage.VARIABLE_STATUS_TYPE, status);
	message.addContextVariable(EventMessage.VARIABLE_USER_LATEST_CHANGE, updateUserLatestChange);
	message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, info);

	return message;
    }

    private void validateTermFields(Term enTerm) {
	Map<String, Set<Term>> languageTerms = getTermEntry().getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	assertNotNull(languageTerms);
	assertNotNull(enLanguageTerms);
	assertNotNull(enTerm);
	assertNotNull(enTerm.getUuId());
	assertNotNull(enTerm.getDateCreated());
	assertNotNull(enTerm.getDateModified());

	assertTrue(CollectionUtils.isNotEmpty(enLanguageTerms));
	assertTrue(enLanguageTerms.size() == 1);
	assertTrue(enTerm.getUserLatestChange().equals(1L));

	assertEquals("Konto", enTerm.getName());
	assertEquals("marko", enTerm.getUserCreated());
	assertEquals("marko", enTerm.getUserModified());
	assertEquals(EN_US, enTerm.getLanguageId());
    }
}
