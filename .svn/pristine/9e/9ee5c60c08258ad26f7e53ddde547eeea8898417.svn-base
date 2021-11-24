package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertNotNull;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("view_all_attributes")
public class ViewAllAttributesTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("get_task_infos")
    public void testGetTaskInfos() throws Exception {

	ManualTaskHandler editTermTaskHandler = getHandler("view properties");

	Object editTermCommand = getTaskHandlerCommand(editTermTaskHandler, "allAttributes.json",
		new String[] { "$termEntryId", TERM_ENTRY_ID_01 }, new String[] { "$sourceTermId", TERM_ID_01 },
		new String[] { "$targetTermId", TERM_ID_02 });

	TaskModel[] taskInfos = editTermTaskHandler.getTaskInfos(new Long[] { 1l }, null, editTermCommand);

	assertNotNull(taskInfos);

    }
}
