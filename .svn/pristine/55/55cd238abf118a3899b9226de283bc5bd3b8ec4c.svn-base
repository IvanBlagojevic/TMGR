package org.gs4tr.termmanager.glossaryV2;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.security.model.Role;
import org.gs4tr.termmanager.model.FolderPolicy;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.GlossaryUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import junit.framework.Assert;

@TestSuite("glossary")
public class UpdateAppanedTest extends AbstractV2GlossaryServiceTest {

    private ITmgrGlossaryOperations _tmgrOperations;

    @Before
    public void setUp() throws OperationsException {
	_tmgrOperations = getGlossaryFactory().getOperations(createKey());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("connect")
    public void testAppendOnHoldTerms() throws OperationsException {
	Locale sourceLocale = Locale.US;
	Locale targetLocale = Locale.GERMANY;

	/* Mock Generic user with onHold role */
	UserProfileContext.clearContext();

	List<Role> roles = getModelObject("rolesGeneric", List.class);

	TmUserProfile userProfile = getModelObject("userProfile", TmUserProfile.class);

	Map<Long, List<Role>> rolesMap = new HashMap<Long, List<Role>>();
	rolesMap.put(1L, roles);

	List<FolderPolicy> foldersPolicies = getModelObject("foldersPolicies", List.class);
	List<FolderPolicy> adminFoldersPolicies = getModelObject("adminFolderPolocies", List.class);
	List<ProjectUserLanguage> projectUserLanguages = getModelObject("projectUserLanguages", List.class);

	userProfile.initializeOrganizationUser(rolesMap, foldersPolicies, adminFoldersPolicies, projectUserLanguages,
		new ArrayList<Submission>());
	userProfile.setSystemRoles(new HashSet<Role>(roles));

	Mockito.when(getSessionService().login(Mockito.anyString(), Mockito.anyString())).thenReturn(userProfile);

	UserProfileContext.setCurrentUserProfile(userProfile);

	Term term1 = new Term("dog", sourceLocale, "Hund", targetLocale);
	Term term2 = new Term("cat", sourceLocale, "Katze", targetLocale);

	GlossaryUpdateRequest request = new GlossaryUpdateRequest();
	request.add(term1);
	request.add(term2);
	request.setOption(UpdateOption.APPEND);

	BatchProcessResult result = getTmgrOperations().update(request);

	ArgumentCaptor<Action> argumentCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService()).updateTermEntries(Mockito.anyList(), Mockito.anyString(), Mockito.anyLong(),
		argumentCaptor.capture());

	assertEquals(Action.ADDED_REMOTELY, argumentCaptor.getValue());

	Assert.assertEquals(2, result.getProcessedItems());
	Assert.assertEquals(2, result.getCommittedItems());
	Assert.assertNull(result.getError());
	Assert.assertNull(result.getErrorMessages());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateAppend() throws OperationsException {
	Locale sourceLocale = Locale.US;
	Locale targetLocale = Locale.GERMANY;

	Term term1 = new Term("dog", sourceLocale, "Hund", targetLocale);
	Term term2 = new Term("cat", sourceLocale, "Katze", targetLocale);

	GlossaryUpdateRequest request = new GlossaryUpdateRequest();
	request.add(term1);
	request.add(term2);
	request.setOption(UpdateOption.APPEND);

	BatchProcessResult result = getTmgrOperations().update(request);

	ArgumentCaptor<Action> argumentCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService()).updateTermEntries(Mockito.anyList(), Mockito.anyString(), Mockito.anyLong(),
		argumentCaptor.capture());

	assertEquals(Action.ADDED_REMOTELY, argumentCaptor.getValue());

	Assert.assertEquals(2, result.getProcessedItems());
	Assert.assertEquals(2, result.getCommittedItems());
	Assert.assertNull(result.getError());
	Assert.assertNull(result.getErrorMessages());
    }

    /*
     * TERII-4192: TSO/TSR | Generic user is able to add terms
     */
    @Test(expected = OperationsException.class)
    public void testUpdateAppendWithGenericUser() throws OperationsException {

	// Need to create generic user and overwrite current user profile
	TmUserProfile generic = createGenericTmUserProfile();
	UserProfileContext.setCurrentUserProfile(generic);

	Term term1 = new Term("dog", Locale.US, "Hund", Locale.GERMANY);
	Term term2 = new Term("cat", Locale.US, "Katze", Locale.GERMANY);

	GlossaryUpdateRequest req = new GlossaryUpdateRequest();
	req.setOption(UpdateOption.APPEND);
	req.add(term1);
	req.add(term2);

	getTmgrOperations().update(req);

	Assert.fail("Failed to validate user write access.");
    }

    @Test
    public void testUpdateNoOption() throws OperationsException {
	Locale sourceLocale = Locale.US;
	Locale targetLocale = Locale.GERMANY;

	Term term1 = new Term("dog", sourceLocale, "Hund", targetLocale);
	Term term2 = new Term("cat", sourceLocale, "Katze", targetLocale);

	GlossaryUpdateRequest request = new GlossaryUpdateRequest();
	request.add(term1);
	request.add(term2);

	BatchProcessResult result = getTmgrOperations().update(request);

	Assert.assertEquals(0, result.getProcessedItems());
	Assert.assertEquals(0, result.getCommittedItems());
	Assert.assertNull(result.getError());
	Assert.assertNotNull(result.getErrorMessages());
    }

    private ITmgrGlossaryOperations getTmgrOperations() {
	return _tmgrOperations;
    }
}
