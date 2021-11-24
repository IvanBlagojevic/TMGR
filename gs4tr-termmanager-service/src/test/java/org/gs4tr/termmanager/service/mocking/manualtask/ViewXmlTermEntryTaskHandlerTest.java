package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.AttributeLevelEnum;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.dto.LanguagePreviewModel;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.manualtask.ViewXmlTermEntryTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.TermEntryPreviewCommand;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class ViewXmlTermEntryTaskHandlerTest extends AbstractManualtaskTest {

    private static final long PROJECT_ID = 1L;

    private static final String TASK_NAME = "view term";

    private static final String VALIDATION_RULE_NAME = "viewXmlTermEntryValidation.json";

    @Autowired
    private ViewXmlTermEntryTaskHandler _handler;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private TermEntryService _termEntryService;

    public ViewXmlTermEntryTaskHandler getHandler() {
	return _handler;
    }

    public ProjectService getProjectService() {
	return _projectService;
    }

    // [issue TERII-2936]: Notes are not visible in term preview
    @Test
    @TestCase("viewXmlTermEntry")
    public void getTermEntryPreviewTest1() {
	/*
	 * Test case: previewCommand does not contain grid languages and
	 * showAllLanguages = true.
	 */
	TermEntryPreviewCommand previewCommand = getModelObject("previewCommand1", TermEntryPreviewCommand.class);
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	when(getTermEntryService().findTermEntryById(anyString(), anyLong())).thenReturn(termEntry);

	TmProject project = new TmProject();
	project.setProjectId(PROJECT_ID);
	when(getProjectService().findProjectById(PROJECT_ID, Attribute.class)).thenReturn(project);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, previewCommand);

	verify(getProjectService()).findProjectById(PROJECT_ID, Attribute.class);
	verify(getTermEntryService()).findTermEntryById(anyString(), anyLong());

	assertJSONResponse(taskInfos);

	Collection<LanguagePreviewModel> languagePreviews = getLanguagePreviewModels(taskInfos);
	assertEquals(3, languagePreviews.size());

	Iterator<LanguagePreviewModel> each = languagePreviews.iterator();

	assertEquals(Locale.US.getCode(), each.next().getLanguage().getLocale());
	assertEquals(Locale.FRANCE.getCode(), each.next().getLanguage().getLocale());
	assertEquals(Locale.GERMANY.getCode(), each.next().getLanguage().getLocale());
    }

    @Test
    @TestCase("viewXmlTermEntry")
    public void getTermEntryPreviewTest2() {
	/*
	 * Test case: previewCommand contain one grid language (GERMANY) and
	 * showAllLanguages = true.
	 */
	TermEntryPreviewCommand previewCommand = getModelObject("previewCommand2", TermEntryPreviewCommand.class);
	TermEntry termEntry = getModelObject("termEntry2", TermEntry.class);

	when(getTermEntryService().findTermEntryById(anyString(), anyLong())).thenReturn(termEntry);

	TmProject project = new TmProject();
	project.setProjectId(PROJECT_ID);
	when(getProjectService().findProjectById(PROJECT_ID, Attribute.class)).thenReturn(project);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, previewCommand);

	verify(getTermEntryService()).findTermEntryById(anyString(), anyLong());
	verify(getProjectService()).findProjectById(PROJECT_ID, Attribute.class);
	assertJSONResponse(taskInfos);

	Collection<LanguagePreviewModel> languagePreviews = getLanguagePreviewModels(taskInfos);
	assertEquals(3, languagePreviews.size());

	Iterator<LanguagePreviewModel> each = languagePreviews.iterator();

	assertEquals(Locale.GERMANY.getCode(), each.next().getLanguage().getLocale());
	assertEquals(Locale.US.getCode(), each.next().getLanguage().getLocale());
	assertEquals(Locale.FRANCE.getCode(), each.next().getLanguage().getLocale());
    }

    @Test
    @TestCase("viewXmlTermEntry")
    public void getTermEntryPreviewTest3() {
	/*
	 * Test case: previewCommand contain one grid language (FRANCE) and
	 * showAllLanguages = false.
	 */
	TermEntryPreviewCommand previewCommand = getModelObject("previewCommand3", TermEntryPreviewCommand.class);
	TermEntry termEntry = getModelObject("termEntry2", TermEntry.class);

	when(getTermEntryService().findTermEntryById(anyString(), anyLong())).thenReturn(termEntry);

	TmProject project = new TmProject();
	project.setProjectId(PROJECT_ID);
	when(getProjectService().findProjectById(PROJECT_ID, Attribute.class)).thenReturn(project);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, previewCommand);

	verify(getTermEntryService()).findTermEntryById(anyString(), anyLong());
	verify(getProjectService()).findProjectById(PROJECT_ID, Attribute.class);
	assertJSONResponse(taskInfos);

	Collection<LanguagePreviewModel> languagePreviews = getLanguagePreviewModels(taskInfos);
	assertEquals(1, languagePreviews.size());

	LanguagePreviewModel lpm = languagePreviews.iterator().next();

	// Response should contain only preview for grid (FRANCE) language
	assertEquals(Locale.FRANCE.getCode(), lpm.getLanguage().getLocale());
    }

    @Test
    @TestCase("viewXmlTermEntry")
    public void getTermEntryPreviewTest4() {
	/*
	 * Test case: previewCommand contain two grid language (FRANCE, GERMANY)
	 * and showAllLanguages = true.
	 */
	TermEntryPreviewCommand previewCommand = getModelObject("previewCommand4", TermEntryPreviewCommand.class);
	TermEntry termEntry = getModelObject("termEntry2", TermEntry.class);

	when(getTermEntryService().findTermEntryById(anyString(), anyLong())).thenReturn(termEntry);

	TmProject project = new TmProject();
	project.setProjectId(PROJECT_ID);
	when(getProjectService().findProjectById(PROJECT_ID, Attribute.class)).thenReturn(project);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, previewCommand);

	verify(getTermEntryService()).findTermEntryById(anyString(), anyLong());
	verify(getProjectService()).findProjectById(PROJECT_ID, Attribute.class);
	assertJSONResponse(taskInfos);

	Collection<LanguagePreviewModel> languagePreviews = getLanguagePreviewModels(taskInfos);
	assertEquals(3, languagePreviews.size());

	Iterator<LanguagePreviewModel> each = languagePreviews.iterator();

	assertEquals(Locale.FRANCE.getCode(), each.next().getLanguage().getLocale());
	assertEquals(Locale.GERMANY.getCode(), each.next().getLanguage().getLocale());
	assertEquals(Locale.US.getCode(), each.next().getLanguage().getLocale());
    }

    @Test
    @TestCase("viewXmlTermEntry")
    public void getTermEntryPreviewTest5() {
	/*
	 * [new feature: TERII-4243] In case notes are configured in a project,
	 * server should send that information to UI)
	 */
	TermEntryPreviewCommand previewCommand = getModelObject("previewCommand1", TermEntryPreviewCommand.class);
	TermEntry termEntry = getModelObject("termEntry1", TermEntry.class);

	TmProject project = createProjectWithNote();

	when(getTermEntryService().findTermEntryById(anyString(), anyLong())).thenReturn(termEntry);
	when(getProjectService().findProjectById(PROJECT_ID, Attribute.class)).thenReturn(project);

	TaskModel[] taskInfos = getHandler().getTaskInfos(new Long[] { PROJECT_ID }, TASK_NAME, previewCommand);

	verify(getProjectService()).findProjectById(PROJECT_ID, Attribute.class);
	verify(getTermEntryService()).findTermEntryById(anyString(), anyLong());

	assertJSONResponse(taskInfos);

	Map<String, Object> model = taskInfos[0].getModel();

	assertTrue((boolean) model.get("notesConfigured"));
    }

    @Before
    public void setUp() throws Exception {
	reset(getProjectService());
	reset(getTermEntryService());
    }

    private void assertJSONResponse(final TaskModel[] taskInfos) {
	String jsonResponse = JsonUtils.writeValueAsString(taskInfos);
	try {
	    assertJSONResponse(jsonResponse, VALIDATION_RULE_NAME);
	} catch (Exception e) {
	    fail(e.getMessage());
	}
    }

    private TmProject createProjectWithNote() {
	Attribute note = new Attribute();
	note.setAttributeLevel(AttributeLevelEnum.LANGUAGE);
	note.setBaseTypeEnum(BaseTypeEnum.NOTE);
	note.setName("part of speech");
	TmProject project = new TmProject();
	project.setProjectId(PROJECT_ID);
	project.setAttributes(Collections.singletonList(note));
	return project;
    }

    @SuppressWarnings("unchecked")
    private Collection<LanguagePreviewModel> getLanguagePreviewModels(TaskModel[] taskInfos) {
	return (Collection<LanguagePreviewModel>) taskInfos[0].getModel().get("languagePreviewModels");
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}
