package org.gs4tr.termmanager.glossaryV2;

import java.util.List;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.GlossaryUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

@TestSuite("glossary")
public class UpdateDoNotOverwriteTest extends AbstractV2GlossaryServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("segmentAndConcordanceSearch")
    public void testUpdateDoNotOverwrite() throws OperationsException {

	List<TermEntry> termEntries = getModelObject("termEntries", List.class);

	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	Locale sourceLocale = Locale.US;
	Locale targetLocale = Locale.GERMANY;

	Term term1 = new Term("dog", sourceLocale, "Hund", targetLocale);
	term1.setId(termEntries.get(0).getUuId());

	Term term2 = new Term("cat", sourceLocale, "Katze", targetLocale);

	GlossaryUpdateRequest request = new GlossaryUpdateRequest();
	request.add(term1);
	request.add(term2);
	request.setOption(UpdateOption.DONTOVERWRITE);

	Mockito.when(getTermEntryService().findTermentriesByIds(Mockito.anyList(), Mockito.anyLong()))
		.thenReturn(termEntries);

	BatchProcessResult result = operations.update(request);

	Assert.assertEquals(2, result.getProcessedItems());
	Assert.assertEquals(1, result.getCommittedItems());
	Assert.assertNull(result.getError());
	Assert.assertNull(result.getErrorMessages());
    }
}
