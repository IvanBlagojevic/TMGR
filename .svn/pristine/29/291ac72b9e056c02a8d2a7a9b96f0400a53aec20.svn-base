package org.gs4tr.termmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ExportTermModel;
import org.gs4tr.termmanager.model.glossary.Term;
import org.junit.Test;

public class AddTermWSTest extends AbstractSolrGlossaryTest {

    @Test
    public void addTermWSTest() throws Exception {
	Long projectId = 1l;

	String sourceName = "source";
	String targetName = "target";
	String sourceLocale = "en-US";
	String targetLocale = "de-DE";

	ExportTermModel model = getTermEntryService().addTermWS(projectId, sourceLocale, targetLocale, sourceName, null,
		targetName, null);

	assertEquals("U", model.getOperation());
	assertEquals(TmUserProfile.getCurrentUserName(), model.getCreationUser());
	assertEquals(sourceName, model.getSource());
	assertEquals(targetName, model.getTarget());

	String ticket = model.getTicket();
	int length = ticket.length();
	String sourceTicket = ticket.substring(0, length / 2);
	String targetTicket = ticket.substring(length / 2, length);
	Term sourceTerm = getTermService().findTermById(sourceTicket, projectId);
	Term targetTerm = getTermService().findTermById(targetTicket, projectId);

	assertNotNull(sourceTerm);
	assertNotNull(targetTerm);

	assertEquals(sourceName, sourceTerm.getName());
	assertEquals(targetName, targetTerm.getName());
    }
}
