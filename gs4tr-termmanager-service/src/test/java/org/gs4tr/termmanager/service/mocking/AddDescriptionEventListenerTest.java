package org.gs4tr.termmanager.service.mocking;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.AddDescriptionEventListener;
import org.gs4tr.termmanager.service.listeners.Messages;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class AddDescriptionEventListenerTest extends AbstractServiceTest {
    private static final String EN_US = "en-US";

    private static final String MESSAGE_ID = "MESSAGEID";
    @Autowired
    private AddDescriptionEventListener _listener;
    private ProjectDetailInfo _projectDetailInfo;
    private TermEntry _termEntry;
    private TermEntry _termEntryForUpdate;
    private TmProject _tmProject;

    @Test
    @TestCase("addDescriptionEventListener")
    public void addDescriptionToEmptyTermEntryTest() {
	String commandDescValue = "TermEntry desc 1";

	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue(commandDescValue);
	getTermEntryForUpdate().setDescriptions(null);
	EventMessage message = createEventMessage(getTmProject(), command, getTermEntryForUpdate(),
		getProjectDetailInfo());

	getListener().notify(message);

	assertEquals(1, getTermEntryForUpdate().getDescriptions().size());

	Iterator<Description> descIterator = getTermEntryForUpdate().getDescriptions().iterator();
	Description description = descIterator.next();
	assertEquals(commandDescValue, description.getValue());
    }

    @Test
    @TestCase("addDescriptionEventListener")
    public void addDescriptionToNonEmptyTermEntryTest() {
	String commandDescValue = "TermEntry desc 2";

	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue(commandDescValue);
	EventMessage message = createEventMessage(getTmProject(), command, getTermEntryForUpdate(),
		getProjectDetailInfo());

	int descNumBefore = getTermEntryForUpdate().getDescriptions().size();

	// Num of descriptions should be increased by one;
	descNumBefore++;

	Iterator<Description> descIterator = getTermEntryForUpdate().getDescriptions().iterator();
	Description description = descIterator.next();

	String descValue = description.getValue();

	getListener().notify(message);

	assertEquals(descNumBefore, getTermEntryForUpdate().getDescriptions().size());

	descIterator = getTermEntryForUpdate().getDescriptions().iterator();
	description = descIterator.next();
	assertEquals(commandDescValue, description.getValue());

	description = descIterator.next();
	assertEquals(descValue, description.getValue());
    }

    /*
     * TERII-5909 WF5 | Wrong modification user is recorded when add term entry from
     * WF
     */
    @Test
    @TestCase("addDescriptionEventListener")
    public void addTermWrongModificationUserTest() {
	/* source term */
	UpdateCommand command1 = getModelObject("command1", UpdateCommand.class);
	command1.setCommand("add");
	command1.setItemType("term");

	/* target term */
	UpdateCommand command2 = getModelObject("command2", UpdateCommand.class);
	command2.setCommand("add");
	command2.setItemType("term");

	/* target term description */
	UpdateCommand command3 = getModelObject("command3", UpdateCommand.class);
	command3.setCommand("add");
	command3.setItemType("description");

	List<UpdateCommand> updateCommands = Arrays.asList(command1, command2, command3);

	TermEntry termEntry01 = getModelObject("emptyTermEntry", TermEntry.class);
	termEntry01.setAction(Action.ADDED_REMOTELY);

	String remoteUser = "remoteUser";

	getTermEntryService().notifyListeners(getTmProject(), getProjectDetailInfo(), new HashSet<>(), termEntry01,
		ItemStatusTypeHolder.PROCESSED.toString(), updateCommands, remoteUser);

	assertTrue(termEntry01.getAction() == Action.ADDED_REMOTELY);
	assertEquals(termEntry01.getUserCreated(), remoteUser);
	assertEquals(termEntry01.getUserModified(), remoteUser);

	Set<Term> terms = termEntry01.getLanguageTerms().get("de-DE");
	assertEquals(terms.size(), 1);

	Term targetTerm = terms.iterator().next();
	assertEquals(targetTerm.getUserCreated(), remoteUser);
	assertEquals(targetTerm.getUserModified(), remoteUser);
    }

    @Test
    @TestCase("addDescriptionEventListener")
    public void addingDescriptionTest() {

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

    @Test
    @TestCase("addDescriptionEventListener")
    public void createDuplicateTermByAddingDescriptionTest() {
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

    public ProjectDetailInfo getProjectDetailInfo() {
	return _projectDetailInfo;
    }

    public TermEntry getTermEntry() {
	return _termEntry;
    }

    public TermEntry getTermEntryForUpdate() {
	return _termEntryForUpdate;
    }

    @Before
    public void setUp() {
	_tmProject = getModelObject("tmProject", TmProject.class);
	_termEntry = getModelObject("termEntry01", TermEntry.class);
	_termEntryForUpdate = getModelObject("termEntryForUpdate", TermEntry.class);
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

    private AddDescriptionEventListener getListener() {
	return _listener;
    }

    private TmProject getTmProject() {
	return _tmProject;
    }

    private void testExpectedExceptionMessage(UserException e) {
	assertEquals(e.getMessage(), Messages.getString("AddDescriptionEventListener.3"));
	assertEquals(e.getDescription(), Messages.getString("AddDescriptionEventListener.4"));
    }

}
