package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.manualtask.ExportItemCountTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.ExportCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.tm3.api.Page;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class ExportItemCountTaskHandlerTest extends AbstractManualtaskTest {

    private static final String TASK_NAME = "export item count"; //$NON-NLS-1$

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private ExportItemCountTaskHandler _taskHandler;

    @Autowired
    private TermEntryService _termEntryService;

    public ProjectService getProjectService() {
	return _projectService;
    }

    public ExportItemCountTaskHandler getTaskHandler() {
	return _taskHandler;
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("exportItemCount")
    public void getTaskInfosTest() {
	ExportCommand command = getModelObject("exportCommand", ExportCommand.class);

	Map<Long, Long> counts = getModelObject("counts", Map.class);

	ArrayList<TermEntry> results = new ArrayList<TermEntry>();

	Page<TermEntry> page = new Page<TermEntry>(50, 0, 50, results);

	when(getTermEntryService().searchTermEntries(any(TmgrSearchFilter.class))).thenReturn(page);
	when(getTermEntryService().getNumberOfTermEntries(any(TmgrSearchFilter.class))).thenReturn(counts);

	TaskModel[] taskInfos = getTaskHandler().getTaskInfos(new Long[] {}, TASK_NAME, command);

	verify(getTermEntryService()).getNumberOfTermEntries(any(TmgrSearchFilter.class));
	verify(getTermEntryService()).searchTermEntries(any(TmgrSearchFilter.class));

	String result = JsonUtils.writeValueAsString(taskInfos);
	try {
	    assertJSONResponse(result, "exportItemCountValidation.json");
	} catch (Exception e) {
	    fail(e.getMessage());
	}

	Map<String, Object> model = taskInfos[0].getModel();

	assertEquals(50, (int) model.get("termEntryCountExport"));

	assertEquals(500, (long) model.get("totalTermEntryCount"));
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Before
    public void setUp() {
	resetMocks();
    }

    private ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    private void resetMocks() {
	reset(getProjectService());
	reset(getProjectDAO());
	reset(getTermEntryService());
    }
}
