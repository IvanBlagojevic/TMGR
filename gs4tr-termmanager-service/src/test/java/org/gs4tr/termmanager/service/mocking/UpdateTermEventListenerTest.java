package org.gs4tr.termmanager.service.mocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.listeners.UpdateTermEventListener;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("service")
public class UpdateTermEventListenerTest extends AbstractServiceTest {

    private static final String EN_US = "en-US";

    private static final String MESSAGE_ID = "MESSAGEID";

    private static final Long PROJECT_ID = 1L;

    @Autowired
    private UpdateTermEventListener _listener;

    @Test
    @TestCase("updateTermEventListener")
    public void demoteToPendingApprovalTermStatusTest() {
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");
	command.setStatus(ItemStatusTypeHolder.PROCESSED.toString());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToPendingPoliciesIds = getModelObject("demoteToPendingPoliciesIds", List.class);

	/* Add demoteToPending policies to user */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToPendingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID);
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.WAITING.getName(), enTerm.getStatus());

	assertEquals(1l, info.getTotalCount(info.getLanguagePendingTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void demoteToWaitingTermStatusTest() {
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");
	command.setStatus(ItemStatusTypeHolder.PROCESSED.toString());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToWaitingPoliciesIds = getModelObject("demoteToWaitingPoliciesIds", List.class);

	/* Add demoteToWaiting policies to user */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToWaitingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID);
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.ON_HOLD.getName(), enTerm.getStatus());

	assertEquals(1l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void failedToDemoteTermStatusToPendingApprovalTest() {
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");
	command.setStatus(ItemStatusTypeHolder.PROCESSED.toString());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Boolean updateUserLatestChange = Boolean.TRUE;

	/*
	 * User doesn't have demoteToPending policies, and term status remains the same.
	 */

	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID);
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enTerm.getStatus());

	assertEquals(0l, info.getTotalCount(info.getLanguagePendingTermCount()));
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
    }

    public UpdateTermEventListener getListener() {
	return _listener;
    }

    @Before
    public void setUp() {
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testBadStatusAfterAutoMerge_Case1() {
	UpdateCommand command = getModelObject("command1", UpdateCommand.class);
	/*
	 * Case where main term status is OnHold ,synonym status is Approved and we set
	 * synonym name to be same as main term name
	 */
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(2, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en-US");
	info.incrementOnHoldTermCount("en-US");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* synonym is deleted */
	Term disabledTerm = enLanguageTerms.stream().filter(term -> !term.isFirst()).findFirst().get();
	assertTrue(disabledTerm.isDisabled());

	Term enTerm = enLanguageTerms.stream().filter(Term::isFirst).findFirst().get();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("super", enTerm.getUserModified());

	/* main term name remains the same */
	assertEquals("mainTermName", enTerm.getName());

	/* main term change status to Approved */
	assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enTerm.getStatus());

	/* approved term count remains 1 */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* onHold term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testBadStatusAfterAutoMerge_Case2() {
	UpdateCommand command = getModelObject("command2", UpdateCommand.class);
	/*
	 * Case where main term status is OnHold ,synonym status is Approved and we set
	 * main term name to be same as synonym name
	 */
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(2, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en-US");
	info.incrementOnHoldTermCount("en-US");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* main term is deleted and synonym take his place */
	Term disabledTerm = enLanguageTerms.stream().filter(term -> !term.isFirst()).findFirst().get();
	assertTrue(disabledTerm.isDisabled());

	Term enTerm = enLanguageTerms.stream().filter(Term::isFirst).findFirst().get();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("super", enTerm.getUserModified());

	/* main term name become synonym name */
	assertEquals("synonymName", enTerm.getName());

	/* main term change status to Approved */
	assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enTerm.getStatus());

	/* approved term count remains 1 */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* onHold term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testBadStatusAfterAutoMerge_Case3() {
	UpdateCommand command = getModelObject("command3", UpdateCommand.class);
	/*
	 * Case where main term status is Approved ,synonym status is OnHold and we set
	 * synonym name to be same as main term name
	 */
	TermEntry termEntry = getModelObject("termEntry2", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(2, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en-US");
	info.incrementOnHoldTermCount("en-US");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* synonym is deleted */
	Term disabledTerm = enLanguageTerms.stream().filter(term -> !term.isFirst()).findFirst().get();
	assertTrue(disabledTerm.isDisabled());

	Term enTerm = enLanguageTerms.stream().filter(Term::isFirst).findFirst().get();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("super", enTerm.getUserModified());

	/* main term name remains the same */
	assertEquals("mainTermName", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enTerm.getStatus());

	/* approved term count remains 1 */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* onHold term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testBadStatusAfterAutoMerge_Case4() {
	UpdateCommand command = getModelObject("command4", UpdateCommand.class);
	/*
	 * Case where main term status is Approved ,synonym status is OnHold and we set
	 * main term name to be same as synonym name
	 */
	TermEntry termEntry = getModelObject("termEntry2", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(2, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en-US");
	info.incrementOnHoldTermCount("en-US");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* main term is deleted and synonym take his place */
	Term disabledTerm = enLanguageTerms.stream().filter(term -> !term.isFirst()).findFirst().get();
	assertTrue(disabledTerm.isDisabled());

	Term enTerm = enLanguageTerms.stream().filter(Term::isFirst).findFirst().get();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("super", enTerm.getUserModified());

	/* main term name become synonym name */
	assertEquals("synonymName", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enTerm.getStatus());

	/* approved term count remains 1 */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* onHold term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testBadStatusAfterAutoMerge_Case5() {
	UpdateCommand command = getModelObject("command1", UpdateCommand.class);
	/*
	 * Case where main term status is OnHold ,synonym status is Approved and we set
	 * synonym name to be same as main term name and user have demoteToPending
	 * policies
	 */
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(2, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToPendingPoliciesIds = getModelObject("demoteToPendingPoliciesIds", List.class);

	/* Add demoteToPending policies to user */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToPendingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en-US");
	info.incrementOnHoldTermCount("en-US");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* synonym is deleted */
	Term disabledTerm = enLanguageTerms.stream().filter(term -> !term.isFirst()).findFirst().get();
	assertTrue(disabledTerm.isDisabled());

	Term enTerm = enLanguageTerms.stream().filter(Term::isFirst).findFirst().get();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("super", enTerm.getUserModified());

	/* main term name remains the same */
	assertEquals("mainTermName", enTerm.getName());

	/* because of demoteToPending policies new term status is pendingApproval */
	assertEquals(ItemStatusTypeHolder.WAITING.getName(), enTerm.getStatus());

	/* approved term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* onHold term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));

	/* pendigApproval term count is incremented */
	assertEquals(1l, info.getTotalCount(info.getLanguagePendingTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testBadStatusAfterAutoMerge_Case6() {
	UpdateCommand command = getModelObject("command1", UpdateCommand.class);
	/*
	 * Case where main term status is OnHold ,synonym status is Approved and we set
	 * synonym name to be same as main term name and user have demoteToWaiting
	 * policies
	 */
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(2, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToPendingPoliciesIds = getModelObject("demoteToWaitingPoliciesIds", List.class);

	/* Add demoteToWaiting policies to user */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToPendingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en-US");
	info.incrementOnHoldTermCount("en-US");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* synonym is deleted */
	Term disabledTerm = enLanguageTerms.stream().filter(term -> !term.isFirst()).findFirst().get();
	assertTrue(disabledTerm.isDisabled());

	Term enTerm = enLanguageTerms.stream().filter(Term::isFirst).findFirst().get();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("super", enTerm.getUserModified());

	/* main term name remains the same */
	assertEquals("mainTermName", enTerm.getName());

	/* because of demoteToWaiting policies status remains the same */
	assertEquals(ItemStatusTypeHolder.ON_HOLD.getName(), enTerm.getStatus());

	/* approved term count is decremented */
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* onHold term count remains the same */
	assertEquals(1l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguagePendingTermCount()));
    }

    /*
     * TERII-5715 Bad count when we merge two same terms with status approved
     */
    @Test
    @TestCase("updateTermEventListener")
    public void testCountWhenMergingTwoSameTermsWithStatusApproved() {
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	/*
	 * Setting the same term value and term status(Approved) as existing term
	 */
	command.setValue("Business Control Panel");
	command.setStatus(ItemStatusTypeHolder.PROCESSED.getName());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(1, enLanguageTerms1.size());

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("Business Control Panel", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enTerm.getStatus());

	/* approved term count remains 1 */
	assertEquals(1l, info.getTotalCount(info.getLanguageApprovedTermCount()));

	/* pending approval term count is 0 */
	assertEquals(0l, info.getTotalCount(info.getLanguagePendingTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void testCurrentTermStatusIsNotProcessed() {
	/*
	 * User want to update term which current status is not Processed and set status
	 * to Blacklisted
	 */
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");
	command.setStatus(ItemStatusTypeHolder.BLACKLISTED.toString());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	/* setting current term status to ON HOLD */
	Term enTerm = enLanguageTerms.iterator().next();
	enTerm.setStatus(ItemStatusTypeHolder.ON_HOLD.getName());

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToPendingPoliciesIds = getModelObject("demoteToPendingPoliciesIds", List.class);

	/*
	 * Add demoteToPending policies to user which are unnecessary for this case
	 * because current status is not Processed
	 */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToPendingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID);
	info.incrementOnHoldTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());

	/* New status is updated by user */
	assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), enTerm.getStatus());

	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
	assertEquals(1l, info.getTotalCount(info.getLanguageForbiddenTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void updateTermSameStatusDifferentTermValueTest() {

	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Map<String, Set<Term>> languageTermsMap = termEntry.getLanguageTerms();
	Set<Term> enLanguageTerms1 = languageTermsMap.get(EN_US);

	assertEquals(1, enLanguageTerms1.size());
	Term term = enLanguageTerms1.iterator().next();
	term.setStatus(ItemStatusTypeHolder.BLACKLISTED.getName());

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	/*
	 * Here we check if new updatedLangue is added when term value is changed (term
	 * status should remain the same)
	 */
	Set<String> updatedLanguages = info.getUpdatedLanguages();
	assertEquals(updatedLanguages.size(), 1);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), enTerm.getStatus());

	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
    }

    /*
     * TERII-3001:
     * "Blacklist count is not increment when terms are blacklisted form floating multi-editor"
     */
    @Test
    @TestCase("updateTermEventListener")
    public void updateTermTest() {

	UpdateCommand command = getModelObject("command", UpdateCommand.class);

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Boolean updateUserLatestChange = Boolean.TRUE;

	ProjectDetailInfo info = new ProjectDetailInfo(termEntry.getProjectId());
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertNotNull(enTerm);
	assertNotNull(enTerm.getDateModified());

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("Business Control Panel", enTerm.getName());
	assertEquals(ItemStatusTypeHolder.BLACKLISTED.getName(), enTerm.getStatus());

	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
	assertEquals(1l, info.getTotalCount(info.getLanguageForbiddenTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void userChangeTermAndTermStatusTest() {

	/* User change term and set term status to Pending before click on save */
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");
	command.setStatus(ItemStatusTypeHolder.WAITING.toString());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToWaitingPoliciesIds = getModelObject("demoteToWaitingPoliciesIds", List.class);

	/* Add demote to Waiting policies to user */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToWaitingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID);
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());

	/* Term status is updated by user */
	assertEquals(ItemStatusTypeHolder.WAITING.getName(), enTerm.getStatus());

	assertEquals(1l, info.getTotalCount(info.getLanguagePendingTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
    }

    @Test
    @TestCase("updateTermEventListener")
    public void userWithPendingAndWaitingPoliciesTest() {
	UpdateCommand command = getModelObject("command", UpdateCommand.class);
	command.setValue("New Value");
	command.setStatus(ItemStatusTypeHolder.PROCESSED.toString());

	TermEntry termEntry = getModelObject("termEntry", TermEntry.class);

	Boolean updateUserLatestChange = Boolean.TRUE;

	List<String> demoteToPendingPoliciesIds = getModelObject("demoteToPendingPoliciesIds", List.class);

	List<String> demoteToWaitingPoliciesIds = getModelObject("demoteToWaitingPoliciesIds", List.class);

	/* Add demoteToPending and demoteToWaiting policies to user */
	Set<String> contextPolicies = TmUserProfile.getCurrentUserProfile().getContextPolicies(PROJECT_ID);
	contextPolicies.addAll(demoteToPendingPoliciesIds);
	contextPolicies.addAll(demoteToWaitingPoliciesIds);

	ProjectDetailInfo info = new ProjectDetailInfo(PROJECT_ID);
	info.incrementApprovedTermCount("en");

	EventMessage message = createEventMessage(command, termEntry, updateUserLatestChange, info);

	getListener().notify(message);

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> enLanguageTerms = languageTerms.get(EN_US);

	Term enTerm = enLanguageTerms.iterator().next();

	assertEquals("marko", enTerm.getUserModified());
	assertEquals("New Value", enTerm.getName());

	/* New term status is status with higher type level which is WAITING */
	String statusWithHigherTypeLevel = getItemStatusWithHigherTypeLevel(ItemStatusTypeHolder.ON_HOLD,
		ItemStatusTypeHolder.WAITING);

	assertEquals(statusWithHigherTypeLevel, enTerm.getStatus());

	assertEquals(1l, info.getTotalCount(info.getLanguagePendingTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageApprovedTermCount()));
	assertEquals(0l, info.getTotalCount(info.getLanguageOnHoldTermCount()));
    }

    private EventMessage createEventMessage(UpdateCommand command, TermEntry termEntry, Boolean updateUserLatestChange,
	    ProjectDetailInfo projectDetailInfo) {

	EventMessage message = new EventMessage(MESSAGE_ID);
	message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
	message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);
	message.addContextVariable(EventMessage.VARIABLE_USER_LATEST_CHANGE, updateUserLatestChange);
	message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, projectDetailInfo);

	return message;
    }

    private static String getItemStatusWithHigherTypeLevel(ItemStatusType itemStatus1, ItemStatusType itemStatus2) {

	String status1 = itemStatus1.toString();
	String status2 = itemStatus2.toString();

	return ItemStatusTypeHolder.getItemStatusTypeLevel(status1) < ItemStatusTypeHolder
		.getItemStatusTypeLevel(status2) ? status1 : status2;

    }
}
