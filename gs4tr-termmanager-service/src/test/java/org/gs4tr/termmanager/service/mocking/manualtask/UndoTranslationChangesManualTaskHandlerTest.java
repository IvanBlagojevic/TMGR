package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.manualtask.UndoTranslationChangesManualTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.UndoCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("manualtask")
public class UndoTranslationChangesManualTaskHandlerTest extends AbstractManualtaskTest {

    @Autowired
    private UndoTranslationChangesManualTaskHandler _handler;

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Before
    public void setUp() throws Exception {
	reset(getSubmissionTermService());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("undoTranslation")
    public void undoTranslationTest_01() {
	List<String> termIds = getModelObject("termIds", List.class);
	Map<String, String> undoMap = getModelObject("undoMap", Map.class);

	UndoCommand command = new UndoCommand();
	command.setTermIds(termIds);

	when(getSubmissionTermService().undoTermTranslation(anyList(), anyLong())).thenReturn(undoMap);

	Long projectId = 1L;

	TaskResponse response = getHandler().processTasks(new Long[] { projectId }, null, command, null);

	validateMockitoUsage();
	verify(getSubmissionTermService(), times(1)).undoTermTranslation(anyList(), anyLong());

	Assert.assertNotNull(response);

	String result = JsonUtils.writeValueAsString(response);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	Assert.assertNotNull(resultNode);

	JsonNode modelNode = resultNode.get("model");
	Assert.assertNotNull(modelNode);

	JsonNode undoNode = modelNode.get("undoResults");
	Assert.assertNotNull(undoNode);
    }

    private UndoTranslationChangesManualTaskHandler getHandler() {
	return _handler;
    }

    private SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

}
