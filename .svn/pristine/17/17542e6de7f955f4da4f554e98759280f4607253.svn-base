package org.gs4tr.termmanager.glossaryV2;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.tm3.api.BatchProcessResult;
import org.gs4tr.tm3.api.UpdateOption;
import org.gs4tr.tm3.api.glossary.Term;
import org.gs4tr.tm3.httpconnector.resolver.model.GlossaryUpdateRequest;
import org.gs4tr.tm3.httpconnector.resolver.model.OperationsException;
import org.gs4tr.tm3.httpconnector.resolver.model.TmgrKey;
import org.gs4tr.tm3.httpconnector.servlet.ITmgrGlossaryOperations;
import org.junit.Test;

import junit.framework.Assert;

@TestSuite("glossary")
public class UpdateSkipTest extends AbstractV2GlossaryServiceTest {

    @Test
    public void testUpdateSkip() throws OperationsException {
	TmgrKey key = createKey();

	ITmgrGlossaryOperations operations = getGlossaryFactory().getOperations(key);

	Locale sourceLocale = Locale.US;
	Locale targetLocale = Locale.GERMANY;

	Term term1 = new Term("dog", sourceLocale, "Hund", targetLocale);
	Term term2 = new Term("cat", sourceLocale, "Katze", targetLocale);

	GlossaryUpdateRequest request = new GlossaryUpdateRequest();
	request.add(term1);
	request.add(term2);
	request.setOption(UpdateOption.SKIP);

	BatchProcessResult result = operations.update(request);

	Assert.assertEquals(2, result.getProcessedItems());
	Assert.assertEquals(0, result.getCommittedItems());
	Assert.assertNull(result.getError());
	Assert.assertNull(result.getErrorMessages());
    }
}
