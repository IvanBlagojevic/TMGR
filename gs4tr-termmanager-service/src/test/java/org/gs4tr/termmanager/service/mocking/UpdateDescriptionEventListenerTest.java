package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.Messages;
import org.gs4tr.termmanager.service.listeners.UpdateDescriptionEventListener;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.Assert;

@TestSuite("service")
public class UpdateDescriptionEventListenerTest extends AbstractServiceTest {

    private static final String MESSAGE_ID = "MESSAGEID";
    @Autowired
    private UpdateDescriptionEventListener _listener;
    private ProjectDetail _projectDetail;
    private ProjectDetailInfo _projectDetailInfo;
    private TermEntry _termEntry;
    private TermEntry _termEntryForUpdate;
    private TmProject _tmProject;
    private TmUserProfile _tmUser;

    public UpdateDescriptionEventListener getListener() {
	return _listener;
    }

    @Override
    public ProjectDetail getProjectDetail() {
	return _projectDetail;
    }

    public ProjectDetailInfo getProjectDetailInfo() {
	return _projectDetailInfo;
    }

    public TermEntry getTermEntry() {
	return _termEntry;
    }

    public TermEntry getTermEntryForUpdate() {
	return _termEntryForUpdate;
    }

    public TmProject getTmProject() {
	return _tmProject;
    }

    public TmUserProfile getTmUser() {
	return _tmUser;
    }

    @Before
    public void setUp() {
	_tmProject = getModelObject("tmProject", TmProject.class);
	_tmUser = (TmUserProfile) UserProfileContext.getCurrentUserProfile();
	_projectDetail = getModelObject("projectDetail", ProjectDetail.class);
	_termEntry = getModelObject("termEntry01", TermEntry.class);
	_termEntryForUpdate = getModelObject("termEntryForUpdate", TermEntry.class);
	_projectDetailInfo = getModelObject("projectDetailInfo", ProjectDetailInfo.class);

	ProjectUserDetail projectUserDetail = new ProjectUserDetail(getTmUser(), getProjectDetail());

	Set<ProjectUserDetail> userDetails = new HashSet<>();

	userDetails.add(projectUserDetail);

	_projectDetail.setUserDetails(userDetails);
    }

    @Test
    @TestCase("updateDescriptionEventListener")
    public void updateDuplicateTermTest() {

	UpdateCommand command = getModelObject("command", UpdateCommand.class);

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), processed,
		getProjectDetailInfo());

	Date dateModifiedProject = getProjectDetailInfo().getDateModified();

	boolean isExceptionThrown = false;

	try {
	    getListener().notify(message);
	} catch (UserException e) {
	    testExpectedExceptionMessage(e);
	    isExceptionThrown = true;
	}

	assertTrue(isExceptionThrown);

	Set<Map.Entry<String, Set<Term>>> entrySetTerms = getTermEntry().getLanguageTerms().entrySet();

	assertEquals(1, entrySetTerms.size());

	Iterator<Map.Entry<String, Set<Term>>> entryTermsIterator = entrySetTerms.iterator();

	Map.Entry<String, Set<Term>> set = entryTermsIterator.next();

	Set<Term> terms = set.getValue();
	assertEquals(2, terms.size());
	Iterator<Term> termsIterator = terms.iterator();

	Term term = termsIterator.next();
	Set<Description> descriptions = term.getDescriptions();
	assertEquals(1, descriptions.size());
	Iterator<Description> descIterator = descriptions.iterator();
	assertEquals("Konto 1", descIterator.next().getValue());

	term = termsIterator.next();
	descriptions = term.getDescriptions();
	assertEquals(1, descriptions.size());
	descIterator = descriptions.iterator();
	assertEquals("Konto 2", descIterator.next().getValue());

	assertEquals(dateModifiedProject, getProjectDetailInfo().getDateModified());
    }

    @Test
    @TestCase("updateDescriptionEventListener")
    public void updateNonEmptyTermEntryTest() {

	String commandVal = "TermEntry desc 2";
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue(commandVal);

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntryForUpdate(), processed,
		getProjectDetailInfo());

	int descNumBefore = getTermEntryForUpdate().getDescriptions().size();

	Long dateModified = getTermEntryForUpdate().getDateModified();

	String userModified = getTermEntryForUpdate().getUserModified();

	getListener().notify(message);

	assertEquals(descNumBefore, getTermEntryForUpdate().getDescriptions().size());

	Iterator<Description> descIterator = getTermEntryForUpdate().getDescriptions().iterator();

	Description description = descIterator.next();

	assertEquals(commandVal, description.getValue());

	assertNotSame(dateModified, getTermEntryForUpdate().getDateModified());

	assertNotSame(userModified, getTermEntryForUpdate().getUserModified());
    }

    @Test
    @TestCase("updateDescriptionEventListener")
    public void updateTermTest() {

	String synonymVal = "New Konto";

	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue(synonymVal);

	final String processed = ItemStatusTypeHolder.PROCESSED.getName();

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), processed,
		getProjectDetailInfo());

	Date dateModifiedProject = getProjectDetailInfo().getDateModified();

	getListener().notify(message);

	Set<Map.Entry<String, Set<Term>>> entrySetTerms = getTermEntry().getLanguageTerms().entrySet();

	assertEquals(1, entrySetTerms.size());

	Iterator<Map.Entry<String, Set<Term>>> entryTermsIterator = entrySetTerms.iterator();

	Map.Entry<String, Set<Term>> set = entryTermsIterator.next();

	Set<Term> terms = set.getValue();
	assertEquals(2, terms.size());
	Iterator<Term> termsIterator = terms.iterator();

	Term term = termsIterator.next();
	Set<Description> descriptions = term.getDescriptions();
	assertEquals(1, descriptions.size());
	Iterator<Description> descIterator = descriptions.iterator();
	assertEquals(synonymVal, descIterator.next().getValue());

	term = termsIterator.next();
	descriptions = term.getDescriptions();
	assertEquals(1, descriptions.size());
	descIterator = descriptions.iterator();
	assertEquals("Konto 2", descIterator.next().getValue());

	assertNotSame(dateModifiedProject, getProjectDetailInfo().getDateModified());

    }

    private EventMessage createEventMessage(TmProject tmProject, UpdateCommand command, TermEntry termEntry,
	    String status, ProjectDetailInfo projectDetailInfo) {
	EventMessage message = new EventMessage(MESSAGE_ID);
	message.addContextVariable(EventMessage.VARIABLE_PROJECT, tmProject);
	message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
	message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);
	message.addContextVariable(EventMessage.VARIABLE_STATUS_TYPE, status);
	message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, projectDetailInfo);
	return message;
    }

    private void testExpectedExceptionMessage(UserException e) {
	Assert.assertEquals(e.getMessage(), Messages.getString("UpdateDescriptionEventListener.2"));
	Assert.assertEquals(e.getDescription(), Messages.getString("UpdateDescriptionEventListener.3"));
    }

}
