package org.gs4tr.termmanager.glossaryV2.blacklist;

import java.util.List;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.glossaryV2.AbstractV2GlossaryServiceTest;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.api.blacklist.BlacklistTerm;
import org.gs4tr.tm3.httpconnector.resolver.model.BlacklistUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrBlacklistOperations;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

@TestSuite("glossary")
public class UpdateDoNotOverwriteBlacklistTest extends AbstractV2GlossaryServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("blacklistSegmentSearch")
    public void testUpdateDoNotOverwrite() throws OperationsException {

	List<TermEntry> termEntries = getModelObject("termEntries1", List.class);

	TmgrKey key = createKey();

	ITmgrBlacklistOperations operations = getBlacklistFactory().getOperations(key);

	Locale locale = Locale.US;

	BlacklistTerm term1 = new BlacklistTerm("dog", locale);
	term1.setId(termEntries.get(0).getUuId());

	BlacklistTerm term2 = new BlacklistTerm("cat", locale);

	BlacklistUpdateRequest request = new BlacklistUpdateRequest();
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
