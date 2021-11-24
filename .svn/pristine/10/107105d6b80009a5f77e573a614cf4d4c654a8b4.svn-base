package org.gs4tr.termmanager.glossaryV2.blacklist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.glossaryV2.AbstractV2GlossaryServiceTest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.gs4tr.tm3.httpconnector.resolver.model.BlacklistUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import junit.framework.Assert;

@TestSuite("glossary")
public class UpdateAppanedBlacklistTest extends AbstractV2GlossaryServiceTest {

    private ITmgrBlacklistOperations _operations;

    @Before
    public void setUp() throws OperationsException {
	_operations = getBlacklistFactory().getOperations(createKey());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateAppend() throws OperationsException {
	Locale locale = Locale.US;

	BlacklistTerm term1 = new BlacklistTerm("dog", locale);
	BlacklistTerm term2 = new BlacklistTerm("cat", locale);

	BlacklistUpdateRequest request = new BlacklistUpdateRequest();
	request.add(term1);
	request.add(term2);
	request.setOption(UpdateOption.APPEND);

	BatchProcessResult result = getOperations().update(request);

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

	BlacklistTerm term1 = new BlacklistTerm("dog", Locale.US);
	BlacklistTerm term2 = new BlacklistTerm("cat", Locale.US);

	BlacklistUpdateRequest req = new BlacklistUpdateRequest();
	req.setOption(UpdateOption.APPEND);
	req.add(term1);
	req.add(term2);

	getOperations().update(req);

	Assert.fail("Failed to validate user write access.");
    }

    @Test
    public void testUpdateNoOption() throws OperationsException {
	Locale locale = Locale.US;

	BlacklistTerm term1 = new BlacklistTerm("dog", locale);
	BlacklistTerm term2 = new BlacklistTerm("cat", locale);

	BlacklistUpdateRequest request = new BlacklistUpdateRequest();
	request.add(term1);
	request.add(term2);

	BatchProcessResult result = getOperations().update(request);

	Assert.assertEquals(0, result.getProcessedItems());
	Assert.assertEquals(0, result.getCommittedItems());
	Assert.assertNull(result.getError());
	Assert.assertNotNull(result.getErrorMessages());
    }

    private ITmgrBlacklistOperations getOperations() {
	return _operations;
    }
}
