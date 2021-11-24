package org.gs4tr.termmanager.tests;

import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Test;

@TestSuite("view_xml_termentry")
public class ViewXmlTermEntryTaskHandlerTest extends AbstractSolrGlossaryTest {

    @Test
    @TestCase("get_task_infos")
    public void testGetTaskInfos() throws Exception {

	String taskName = "view term";
	ManualTaskHandler taskHandler = getHandler(taskName);

	Object command = getTaskHandlerCommand(taskHandler, "viewTermEntryPreview.json",
		new String[] { "$termEntryId", TERM_ENTRY_ID_01 });

	Long[] projectIds = { PROJECT_ID };

	TaskModel[] taskInfos = taskHandler.getTaskInfos(projectIds, taskName, command);

	String result = JsonUtils.writeValueAsString(taskInfos);

	assertJSONResponse(result, "viewXmlTermentryValidation.json");
    }
}
