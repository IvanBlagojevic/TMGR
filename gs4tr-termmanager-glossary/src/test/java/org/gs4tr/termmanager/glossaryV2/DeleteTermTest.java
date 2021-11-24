package org.gs4tr.termmanager.glossaryV2;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@TestSuite("glossary")
public class DeleteTermTest extends AbstractV2GlossaryServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("segmentAndConcordanceSearch")
    public void testDeleteTerm() throws Exception {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	String source = "ordinary";
	Locale sourceLocale = Locale.US;
	String target = "dummy text";
	Locale targetLocale = Locale.GERMANY;
	Term term = new Term(source, sourceLocale, target, targetLocale);

	TermEntry termEntry = getModelObject("termEntry_01", TermEntry.class);

	Mockito.when(getTermEntryService().findTermEntryById(Mockito.anyString(), Mockito.anyLong()))
		.thenReturn(termEntry);

	operations.delete(term);

	ArgumentCaptor<Action> argumentCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService()).updateTermEntries(Mockito.anyList(), Mockito.anyString(), Mockito.anyLong(),
		argumentCaptor.capture());

	assertEquals(Action.EDITED_REMOTELY, argumentCaptor.getValue());
    }
}
