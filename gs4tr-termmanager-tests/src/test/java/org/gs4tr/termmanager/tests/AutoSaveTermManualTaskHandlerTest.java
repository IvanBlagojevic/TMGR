package org.gs4tr.termmanager.tests;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

@TestSuite("auto_save_term")
public class AutoSaveTermManualTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("process_tasks")
    public void autoSaveNewDescriptionPostTest() throws Exception {
	String subTermId = SUB_TERM_ID_01;

	Term term = getSubmissionTermService().findById(subTermId, PROJECT_ID);
	Assert.assertEquals("Big Maus", term.getTempText());

	Set<Description> descriptions = term.getDescriptions();
	Assert.assertTrue(CollectionUtils.isEmpty(descriptions));

	String termTicket = subTermId;
	String taskName = "auto save translation";
	String text = "This is dummy text";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "autoSaveNewDescriptionCommand.json",
		new String[] { "$termTicket", termTicket }, new String[] { "$text", text });

	Long[] projectIds = new Long[] { PROJECT_ID };

	TaskResponse tasksResponse = taskHandler.processTasks(projectIds, null, command, null);

	Assert.assertNotNull(tasksResponse);

	term = getSubmissionTermService().findById(subTermId, PROJECT_ID);

	descriptions = term.getDescriptions();
	Assert.assertTrue(CollectionUtils.isNotEmpty(descriptions));
	Assert.assertEquals(1, descriptions.size());
	Description description = descriptions.iterator().next();
	Assert.assertEquals(text, description.getValue());
    }

    @Test
    @TestCase("process_tasks")
    public void autoSaveTermPostTest() throws Exception {
	String subTermId = SUB_TERM_ID_01;

	Term subTerm = getSubmissionTermService().findById(subTermId, PROJECT_ID);
	Assert.assertEquals("Big Maus", subTerm.getTempText());

	String taskName = "auto save translation";

	String termText = "This is dummy text";

	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "autoSaveTermCommand.json",
		new String[] { "$termTicket", subTermId }, new String[] { "$termText", termText });

	Long[] projectIds = new Long[] { PROJECT_ID };

	TaskResponse tasksResponse = taskHandler.processTasks(projectIds, null, command, null);

	Assert.assertNotNull(tasksResponse);

	subTerm = getSubmissionTermService().findById(subTermId, PROJECT_ID);

	Assert.assertEquals(termText, subTerm.getTempText());
    }
}
