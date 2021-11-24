package org.gs4tr.termmanager.service.mocking.sso;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.termmanager.model.ProjectUserDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.tm3.api.TmException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import jetbrains.exodus.entitystore.EntityId;

@TestSuite("service")
public class TermEntryServiceSsoTest extends AbstractSsoServiceTest {

    private static final String EN_US = "en-US";

    private static final String FR_FR = "fr-FR";

    private static final long PROJECT_ID = 1L;

    @Test
    @TestCase("termEntry")
    @SuppressWarnings("unchecked")
    public void addTermEntryAsSSOUserTest() throws TmException {

	String currentUserEmail = TmUserProfile.getCurrentUserEmail();

	List<TranslationUnit> translationUnits = getModelObject("addTermTranslationUnit", List.class);

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(null);

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.ADDED_REMOTELY);

	ArgumentCaptor<TransactionalUnit> argumentTU = ArgumentCaptor.forClass(TransactionalUnit.class);
	ArgumentCaptor<Long> argumentProjectId = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<EntityId> argumentEntityId = ArgumentCaptor.forClass(EntityId.class);

	verify(getTransactionLogHandler(), times(1)).appendAndLink(anyLong(), any(EntityId.class),
		argumentTU.capture());

	verify(getTransactionLogHandler(), times(1)).finishAppending(argumentProjectId.capture(),
		argumentEntityId.capture());

	List<TermEntry> termEntries = argumentTU.getValue().getTermEntries();

	assertTrue(isNotEmpty(termEntries));

	for (TermEntry entry : termEntries) {
	    Set<Term> terms = entry.getLanguageTerms().get(EN_US);
	    assertTrue(isNotEmpty(terms));

	    for (Term term : terms) {
		assertEquals(currentUserEmail, term.getUserCreated());
		assertEquals(currentUserEmail, term.getUserModified());
	    }
	}
    }

    @Test
    @TestCase("termEntry")
    @SuppressWarnings("unchecked")
    public void addTermEntryAsSSOUserWSTest() {

	String currentUserEmail = TmUserProfile.getCurrentUserEmail();

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	getTermEntryService().addTermWS(PROJECT_ID, EN_US, FR_FR, "sourceTerm", null, "targetTerm", null);

	ArgumentCaptor<TransactionalUnit> argumentTU = ArgumentCaptor.forClass(TransactionalUnit.class);
	ArgumentCaptor<Long> argumentProjectId = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<EntityId> argumentEntityId = ArgumentCaptor.forClass(EntityId.class);

	verify(getTransactionLogHandler(), times(1)).appendAndLink(anyLong(), any(EntityId.class),
		argumentTU.capture());

	verify(getTransactionLogHandler(), times(1)).finishAppending(argumentProjectId.capture(),
		argumentEntityId.capture());

	List<TermEntry> termEntries = argumentTU.getValue().getTermEntries();

	assertTrue(isNotEmpty(termEntries));

	for (TermEntry entry : termEntries) {
	    List<Term> terms = entry.ggetTerms();
	    assertTrue(isNotEmpty(terms));

	    for (Term term : terms) {
		assertEquals(currentUserEmail, term.getUserCreated());
		assertEquals(currentUserEmail, term.getUserModified());
	    }
	}

    }

    @Before
    public void setUp() throws Exception {
	reset(getProjectDAO());
	reset(getGlossaryBrowser());
	reset(getTransactionLogHandler());

	reset(getTermEntryService());

	TmProject tmProject;

	tmProject = getModelObject("tmProject", TmProject.class);
	tmProject.getProjectDetail().setProject(tmProject);

	Set<ProjectUserDetail> set = new HashSet<>();
	set.add(new ProjectUserDetail((TmUserProfile) UserProfileContext.getCurrentUserProfile(),
		tmProject.getProjectDetail()));
	tmProject.getProjectDetail().setUserDetails(set);

	when(getProjectDAO().load(any(Long.class))).thenReturn(tmProject);
	when(getProjectDAO().findById(any(Long.class))).thenReturn(tmProject);
    }

    @Test
    @TestCase("termEntry")
    @SuppressWarnings("unchecked")
    public void updateTermEntryAsSSOUserTest() throws TmException {

	String currentUserEmail = TmUserProfile.getCurrentUserEmail();

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);
	List<TranslationUnit> translationUnits = getModelObject("updateTermTranslationUnit", List.class);

	String termName = translationUnits.get(0).getSourceTermUpdateCommands().get(0).getValue();

	when(getTransactionLogHandler().startAppending(anyLong(), anyString(), anyString(), anyString()))
		.thenReturn(Optional.of(getEntityId()));

	when(getGlossaryBrowser().findById(anyString(), any(Long.class))).thenReturn(termEntry);

	getTermEntryService().updateTermEntries(translationUnits, EN_US, PROJECT_ID, Action.EDITED_REMOTELY);

	ArgumentCaptor<TransactionalUnit> argumentTU = ArgumentCaptor.forClass(TransactionalUnit.class);
	ArgumentCaptor<Long> argumentProjectId = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<EntityId> argumentEntityId = ArgumentCaptor.forClass(EntityId.class);

	verify(getTransactionLogHandler(), times(1)).appendAndLink(anyLong(), any(EntityId.class),
		argumentTU.capture());

	verify(getTransactionLogHandler(), times(1)).finishAppending(argumentProjectId.capture(),
		argumentEntityId.capture());

	List<TermEntry> termEntries = argumentTU.getValue().getTermEntries();
	assertTrue(isNotEmpty(termEntries));

	for (TermEntry entry : termEntries) {
	    Set<Term> terms = entry.getLanguageTerms().get(EN_US);
	    assertTrue(isNotEmpty(terms));

	    for (Term term : terms) {
		if (termName.equals(term.getName())) {
		    assertEquals(currentUserEmail, term.getUserCreated());
		    assertEquals(currentUserEmail, term.getUserModified());
		}
	    }
	}

    }

}
