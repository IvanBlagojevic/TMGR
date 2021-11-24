package org.gs4tr.termmanager.glossaryV2.blacklist;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.glossaryV2.AbstractV2GlossaryServiceTest;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.junit.Test;
import org.mockito.Mockito;

@TestSuite("glossary")
public class DeleteBlacklistTermTest extends AbstractV2GlossaryServiceTest {

    @Test
    @TestCase("segmentAndConcordanceSearch")
    public void testDeleteTerm() throws Exception {
	TmgrKey key = createKey();

	ITmgrBlacklistOperations operations = getBlacklistFactory().getOperations(key);

	String text = "ordinary";
	Locale locale = Locale.US;
	BlacklistTerm term = new BlacklistTerm(text, locale);

	TermEntry termEntry = getModelObject("termEntry_01", TermEntry.class);

	Mockito.when(getTermEntryService().findTermEntryById(Mockito.anyString(), Mockito.anyLong()))
		.thenReturn(termEntry);

	operations.delete(term);
    }
}
