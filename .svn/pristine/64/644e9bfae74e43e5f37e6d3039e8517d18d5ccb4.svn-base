package org.gs4tr.termmanager.service.mocking;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import java.util.Date;
import java.util.Iterator;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.AddNoteEventListener;
import org.gs4tr.termmanager.service.listeners.Messages;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by emisia on 6/5/17.
 */

@TestSuite("service")
public class AddNoteEventListenerTest extends AbstractServiceTest {

    private static final String EN_US = "en-US";

    private static final String MESSAGE_ID = "MESSAGEID";
    @Autowired
    private AddNoteEventListener _listener;
    private ProjectDetailInfo _projectDetailInfo;
    private TermEntry _termEntry;
    private TmProject _tmProject;

    @Test
    @TestCase("addNoteEventListener")
    public void addNoteTerm() {
	UpdateCommand command = getModelObject("command", UpdateCommand.class);

	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), getProjectDetailInfo());

	Iterator<Term> terms = getTermEntry().getLanguageTerms().get(EN_US).iterator();
	Term term = terms.next();
	int descNumBefore = term.getDescriptions().size();

	Date dateModifiedProject = getProjectDetailInfo().getDateModified();
	Long dateModifiedTermEntry = getTermEntry().getDateModified();
	String userModified = getTermEntry().getUserModified();

	Long dateModifiedTerm = term.getDateModified();

	Iterator<Description> descIterator = term.getDescriptions().iterator();
	Description description = descIterator.next();

	String descVal = description.getValue();
	String descBaseType = description.getBaseType();

	boolean isExceptionThrown = false;

	try {
	    getListener().notify(message);
	} catch (UserException e) {
	    testExpectedExceptionMessage(e);
	    isExceptionThrown = true;
	}

	assertTrue(isExceptionThrown);

	assertEquals(dateModifiedTermEntry, getTermEntry().getDateModified());
	assertEquals(userModified, getTermEntry().getUserModified());

	assertEquals(dateModifiedTerm, term.getDateModified());
	assertEquals(userModified, term.getUserModified());

	assertEquals(descNumBefore, term.getDescriptions().size());

	assertEquals(descVal, description.getValue());
	assertEquals(descBaseType, description.getBaseType());

	assertEquals(dateModifiedProject, getProjectDetailInfo().getDateModified());
    }

    @Test
    @TestCase("addNoteEventListener")
    public void createDuplicateTermByAddingNoteTest() {

	String newDescValue = "New Konto";

	String userModified = TmUserProfile.getCurrentUserProfile().getUserName();

	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue(newDescValue);
	EventMessage message = createEventMessage(getTmProject(), command, getTermEntry(), getProjectDetailInfo());

	Iterator<Term> terms = getTermEntry().getLanguageTerms().get(EN_US).iterator();
	Term term1 = terms.next();
	int descNumBeforeTerm = term1.getDescriptions().size();

	Date dateModifiedProject = getProjectDetailInfo().getDateModified();

	Long dateModifiedTermEntry = getTermEntry().getDateModified();

	Long dateModifiedTerm = term1.getDateModified();

	Iterator<Description> descIterator = term1.getDescriptions().iterator();
	Description description = descIterator.next();

	String descVal = description.getValue();

	String descBaseType = description.getBaseType();

	getListener().notify(message);

	// Num of descriptions should be increased by one;
	descNumBeforeTerm++;

	assertNotSame(dateModifiedTermEntry, getTermEntry().getDateModified());
	assertEquals(userModified, getTermEntry().getUserModified());

	assertNotSame(dateModifiedTerm, term1.getDateModified());
	assertEquals(userModified, term1.getUserModified());

	// Test description
	assertEquals(descNumBeforeTerm, term1.getDescriptions().size());

	descIterator = term1.getDescriptions().iterator();

	description = descIterator.next();

	assertEquals(descVal, description.getValue());
	assertEquals(descBaseType, description.getBaseType());

	description = descIterator.next();

	assertEquals(newDescValue, description.getValue());
	assertEquals(descBaseType, description.getBaseType());

	assertNotSame(dateModifiedProject, getProjectDetailInfo().getDateModified());
    }

    public AddNoteEventListener getListener() {
	return _listener;
    }

    public ProjectDetailInfo getProjectDetailInfo() {
	return _projectDetailInfo;
    }

    public TermEntry getTermEntry() {
	return _termEntry;
    }

    public TmProject getTmProject() {
	return _tmProject;
    }

    @Before
    public void setUp() {
	_tmProject = getModelObject("tmProject", TmProject.class);
	_termEntry = getModelObject("termEntry01", TermEntry.class);
	_projectDetailInfo = getModelObject("projectDetailInfo", ProjectDetailInfo.class);
    }

    private EventMessage createEventMessage(TmProject tmProject, UpdateCommand command, TermEntry termEntry,
	    ProjectDetailInfo projectDetailInfo) {
	EventMessage message = new EventMessage(MESSAGE_ID);
	message.addContextVariable(EventMessage.VARIABLE_PROJECT, tmProject);
	message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
	message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);
	message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, projectDetailInfo);
	return message;
    }

    private void testExpectedExceptionMessage(UserException e) {
	assertEquals(e.getMessage(), Messages.getString("AddNoteEventListener.1"));
	assertEquals(e.getDescription(), Messages.getString("AddNoteEventListener.2"));
    }

}
